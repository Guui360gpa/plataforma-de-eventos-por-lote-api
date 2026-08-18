# Guia de Contribuição — EventFlow

## Branches

- `main` — branch estável e protegida. Só recebe merge via Pull Request.
- `feature/<nome-da-funcionalidade>` — nova funcionalidade. Ex: `feature/refresh-token`.
- `fix/<nome-do-bug>` — correção de bug. Ex: `fix/lote-quantidade-negativa`.
- `chore/<descricao>` — tarefas de manutenção (config, deps, CI). Ex: `chore/setup-testcontainers`.
- `docs/<descricao>` — apenas documentação. Ex: `docs/regras-de-negocio`.

Toda branch nasce a partir da `main` atualizada.

## Commits

Padrão [Conventional Commits](https://www.conventionalcommits.org/):

```
<tipo>: <descrição curta no imperativo>

[corpo opcional explicando o porquê]
```

Tipos:
- `feat` — nova funcionalidade
- `fix` — correção de bug
- `refactor` — mudança de código sem alterar comportamento
- `test` — adição/ajuste de testes
- `docs` — documentação
- `chore` — configuração, dependências, build

Exemplos:
```
feat: adiciona rotação de refresh token
fix: corrige overselling na última vaga do lote
test: adiciona teste de concorrência com Testcontainers
docs: documenta regras de negócio de cancelamento
```

Regras:
- Commits pequenos e atômicos (uma mudança lógica por commit).
- Descrição no imperativo ("adiciona", não "adicionado" ou "adicionando").
- Sem commits diretos na `main`.

## Pull Requests

1. Abrir a PR assim que a branch estiver pronta para revisão (ou como *draft* se ainda em andamento).
2. Título da PR segue o mesmo padrão dos commits: `feat: adiciona rotação de refresh token`.
3. Descrição da PR deve conter:
   - O que foi feito e por quê.
   - Como testar (passos ou comando).
   - Referência à issue/tarefa relacionada, se houver.
4. PR obrigatoriamente revisada pelo outro integrante antes do merge — sem autoaprovação.
5. Todos os testes (`./mvnw test`) precisam passar antes do merge.
6. Resolver todos os comentários da revisão antes de mergear.
7. Merge via **squash merge** (histórico limpo na `main`).
8. Deletar a branch imediatamente após o merge.

## Checklist antes de abrir a PR

- [ ] Código compila e testes passam localmente.
- [ ] Novas regras de negócio têm teste correspondente.
- [ ] Sem `TODO`/código comentado esquecido.
- [ ] Documentação (README/regras de negócio) atualizada se o comportamento mudou.
