# Regras de Negócio — EventFlow

### 1. Autenticação e Tokens
- Access token expira curto (ex: 15min); refresh token expira longo (ex: 7 dias).
- Refresh token é single-use: a cada `/auth/refresh`, o token antigo é revogado e um novo é emitido (rotação). Reuso de token revogado revoga todos os tokens do usuário (indício de roubo de token).
- Logout revoga apenas o refresh token da sessão atual.
- Banimento de usuário (ADMIN) revoga **todos** os refresh tokens ativos do usuário, imediatamente.
- Senha nunca é retornada em nenhum endpoint, nem em logs.
- Email é único no sistema; tentativa de cadastro duplicado retorna erro claro (não vaza se o email existe, opcionalmente).

### 2. Papéis e Autorização
- `PARTICIPANTE` é o papel padrão no cadastro público. `ORGANIZADOR` e `ADMIN` não são autoatribuíveis via `/auth/register`.
- Dono do evento = organizador que criou o evento. Edição/exclusão de evento e criação de lote só pelo dono, mesmo que o usuário seja `ORGANIZADOR`.
- `ADMIN` tem acesso administrativo mas não deve automaticamente virar dono de eventos de terceiros.
- Usuário banido não consegue autenticar (login bloqueado) nem usar tokens existentes.

### 3. Ciclo de Vida do Evento
- Estados: `RASCUNHO` → `PUBLICADO` → `CANCELADO` (transição unidirecional; não é possível voltar de `CANCELADO`).
- Apenas eventos `PUBLICADO` aparecem em `GET /events` (listagem pública).
- Evento em `RASCUNHO` não pode receber inscrições.
- Cancelar um evento (`CANCELADO`) deve cancelar em cascata todas as inscrições `CONFIRMADA` associadas e devolver vagas (mesmo sem uso prático, por consistência de dados).
- `dataFim` deve ser posterior a `dataInicio` (validação na criação/edição).
- Edição de evento já `PUBLICADO` com inscrições ativas: permitir editar descrição/local, mas restringir mudança de datas que invalide lotes já vendidos (definir regra: bloquear ou exigir confirmação).

### 4. Lotes de Ingresso
- `ordem` define a sequência de liberação; deve ser única por evento.
- Um lote só é "vendável" se: `quantidadeDisponivel > 0` E data atual dentro de `[dataInicioVenda, dataFimVenda]` E evento está `PUBLICADO`.
- Ao criar um lote, `quantidadeDisponivel` inicial = `quantidadeTotal`.
- Não é permitido criar lote com `quantidadeTotal <= 0` ou `preço < 0`.
- Lotes de um mesmo evento não devem ter janelas de venda sobrepostas de forma ambígua (definir se é permitido ou não).

### 5. Regra Central: Seleção de Lote e Concorrência
- Ao inscrever-se, o sistema busca o lote de **menor `ordem`** entre os elegíveis (vendável, conforme item 4).
- Se o lote elegível não tiver vaga no momento exato da tentativa (condição de corrida), a operação falha com `OptimisticLockException` via `@Version`.
- Nesse caso, o serviço **não propaga o erro ao usuário**: internamente repete a busca a partir do próximo lote elegível (nova consulta, não apenas "próximo em memória", pois o estado pode ter mudado).
- Se nenhum lote tiver vaga disponível, retornar erro de negócio "vagas esgotadas" (não erro técnico).
- Decremento de `quantidadeDisponivel` e criação da `Registration` devem ocorrer na mesma transação atômica.
- Um participante não pode ter mais de uma inscrição `CONFIRMADA` ativa no mesmo evento (evitar duplicidade) — validar antes de consumir vaga.

### 6. Cancelamento de Inscrição
- Apenas o dono da inscrição (ou ADMIN) pode cancelá-la.
- Cancelamento só é permitido se `status = CONFIRMADA` (idempotência: cancelar já cancelada retorna erro/no-op).
- Ao cancelar, `status` muda para `CANCELADA` e `quantidadeDisponivel` do lote original é incrementada em 1 — **mesmo que aquele lote já tenha "fechado"** por data (a vaga deve voltar para o lote de origem, não para o lote atualmente ativo).
- Definir prazo limite para cancelamento (ex: até X horas antes do evento) — se não houver, deixar explícito que cancelamento é livre até a data do evento.
- Cancelamento de inscrição não deve reabrir automaticamente um lote já com `dataFimVenda` expirada para novas compras — a vaga liberada só é reutilizável por outra inscrição se as condições de elegibilidade (item 4) ainda valerem.

### 7. Consistência e Integridade
- Toda operação de escrita que envolve `quantidadeDisponivel` usa `@Version` (otimista) — nunca lock pessimista, para não travar sob concorrência alta.
- `quantidadeDisponivel` nunca pode ficar negativa nem maior que `quantidadeTotal` (invariante validada em toda alteração).
- Testes de concorrência (Testcontainers) devem simular N threads disputando 1 vaga e garantir: exatamente 1 sucesso, N-1 falhas tratadas (fallback ou "esgotado"), sem overselling.
