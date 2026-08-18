# EventFlow — Plataforma de Eventos com Controle de Vagas por Lote

Plataforma para criação e gestão de eventos com venda de ingressos em lotes progressivos (ex: 1º lote R$50, 2º lote R$80), controle de concorrência em tempo real e autenticação segura com JWT + refresh token.

## Sobre o projeto

Quando um lote de ingressos esgota, o próximo é liberado automaticamente. O sistema trata o cenário de dois usuários disputando a última vaga do mesmo lote, usando lock otimista no banco de dados.

Projeto desenvolvido em dupla como estudo de Spring Boot, Spring Security, JPA e testes automatizados (JUnit 5 + Mockito), com fluxo de trabalho baseado em feature branches e Pull Requests no GitHub.

## Contributors

<!-- COMMIT-COUNT:START -->
| Developer | Branch | Commits |
|---|---|---:|
| Bernardo Novaes | Novaes | 4 |
| Guilherme Paiva | Paiva | 0 |
<!-- COMMIT-COUNT:END -->

## Funcionalidades

- Cadastro e autenticação de usuários com JWT (access token curto) e refresh token (armazenado no banco, revogável)
- Três papéis de acesso: `ADMIN`, `ORGANIZADOR` e `PARTICIPANTE`
- Criação e gestão de eventos por organizadores
- Lotes de ingresso com preço, quantidade e ordem de liberação
- Inscrição em eventos com consumo automático de vaga do lote ativo
- Tratamento de concorrência na última vaga disponível (lock otimista via `@Version`)
- Cancelamento de inscrição com devolução da vaga
- Moderação administrativa (banir usuário revoga todos os refresh tokens ativos)

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3 |
| Persistência | Spring Data JPA |
| Banco de dados | PostgreSQL (produção) / H2 (testes) |
| Segurança | Spring Security + JWT (access + refresh token) |
| Testes | JUnit 5, Mockito, Testcontainers |
| Build | Maven |
| Versionamento | Git + GitHub (feature branches, Pull Requests) |

## Modelagem de dados

**User** — id, nome, email, senhaHash, role (`ADMIN`, `ORGANIZADOR`, `PARTICIPANTE`)

**RefreshToken** — id, token, user, expiraEm, revogado

**Event** — id, organizador, título, descrição, local, dataInicio, dataFim, status (`RASCUNHO`, `PUBLICADO`, `CANCELADO`)

**TicketBatch** — id, event, nome, preço, quantidadeTotal, quantidadeDisponivel, ordem, dataInicioVenda, dataFimVenda, `@Version` (controle de concorrência)

**Registration** — id, participante, batch, event, status (`CONFIRMADA`, `CANCELADA`), criadaEm

## Endpoints principais

### Autenticação
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/auth/register` | Cadastro de usuário | Público |
| POST | `/auth/login` | Login, retorna access + refresh token | Público |
| POST | `/auth/refresh` | Gera novo access token | Refresh token válido |
| POST | `/auth/logout` | Revoga o refresh token | Autenticado |

### Eventos
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| GET | `/events` | Lista eventos publicados | Público |
| GET | `/events/{id}` | Detalhe do evento | Público |
| POST | `/events` | Cria evento | ORGANIZADOR |
| PUT | `/events/{id}` | Edita evento | ORGANIZADOR (dono) |
| DELETE | `/events/{id}` | Remove evento | ORGANIZADOR (dono) |

### Lotes
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| GET | `/events/{id}/batches` | Lista lotes e disponibilidade | Público |
| POST | `/events/{id}/batches` | Cria lote | ORGANIZADOR (dono) |

### Inscrições
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| POST | `/events/{id}/registrations` | Inscreve no evento (consome vaga) | PARTICIPANTE |
| GET | `/users/me/registrations` | Lista minhas inscrições | Autenticado |
| DELETE | `/registrations/{id}` | Cancela inscrição (devolve vaga) | Dono da inscrição |

### Administração
| Método | Rota | Descrição | Acesso |
|---|---|---|---|
| GET | `/admin/users` | Lista usuários | ADMIN |
| PUT | `/admin/users/{id}/ban` | Bane usuário e revoga tokens | ADMIN |

## Regra de negócio: concorrência na última vaga

A inscrição busca o lote de menor `ordem` que ainda tenha `quantidadeDisponivel > 0` e esteja dentro do período de venda. O campo `@Version` do JPA garante lock otimista: se dois participantes tentarem consumir a última vaga simultaneamente, um deles recebe `OptimisticLockException` e o serviço tenta automaticamente o próximo lote disponível (ou retorna "vagas esgotadas" se não houver mais nenhum).

## Estratégia de testes

- **Unitários (JUnit 5 + Mockito):** regras de negócio isoladas — seleção de lote, cálculo de disponibilidade, autorização por papel — mockando repositórios e serviços.
- **Integração (Testcontainers):** testes contra um PostgreSQL real, incluindo simulação de concorrência com múltiplas threads disputando a última vaga.
- **Segurança:** testes de endpoints com `MockMvc` e `@WithMockUser`, validando que cada rota respeita os papéis definidos.

## Como rodar o projeto

```bash
# clonar o repositório
git clone https://github.com/<seu-usuario>/eventflow.git
cd eventflow

# subir o banco de dados via docker (a definir em docker-compose.yml)
docker compose up -d

# rodar a aplicação
./mvnw spring-boot:run

# rodar os testes
./mvnw test
```

## Fluxo de trabalho (Git/GitHub)

- `main` é a branch estável e protegida (merge apenas via Pull Request)
- Cada funcionalidade nasce em uma branch própria: `feature/nome-da-funcionalidade`
- Todo PR passa por code review do outro integrante antes do merge
- Squash merge ao integrar, com deleção da branch em seguida

## Roadmap

- [ ] Setup inicial do projeto (Spring Boot, banco, estrutura de pacotes)
- [ ] Autenticação: cadastro, login, JWT e refresh token
- [ ] CRUD de eventos e lotes
- [ ] Fluxo de inscrição com controle de concorrência
- [ ] Cancelamento de inscrição
- [ ] Painel administrativo (moderação de usuários)
- [ ] Cobertura de testes unitários e de integração
- [ ] Documentação da API (Swagger/OpenAPI)

## Integrantes

- [Guilherme P. Alves](https://github.com/Guui360gpa)
- [Bernardo N. Novaes](https://github.com/novaes7be)
