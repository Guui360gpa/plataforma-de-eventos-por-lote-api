# Diagrama ER — EventFlow

```mermaid
erDiagram
    USER ||--o{ REFRESH_TOKEN : possui
    USER ||--o{ EVENT : organiza
    USER ||--o{ REGISTRATION : inscreve

    EVENT ||--o{ TICKET_BATCH : possui
    EVENT ||--o{ REGISTRATION : recebe

    TICKET_BATCH ||--o{ REGISTRATION : consumida_por

    USER {
        uuid id PK
        string nome
        string email UK
        string senhaHash
        enum role "ADMIN, ORGANIZADOR, PARTICIPANTE"
    }

    REFRESH_TOKEN {
        uuid id PK
        uuid user_id FK
        string token UK
        datetime expiraEm
        boolean revogado
    }

    EVENT {
        uuid id PK
        uuid organizador_id FK
        string titulo
        string descricao
        string local
        datetime dataInicio
        datetime dataFim
        enum status "RASCUNHO, PUBLICADO, CANCELADO"
    }

    TICKET_BATCH {
        uuid id PK
        uuid event_id FK
        string nome
        decimal preco
        int quantidadeTotal
        int quantidadeDisponivel
        int ordem
        datetime dataInicioVenda
        datetime dataFimVenda
        int version "controle de concorrência"
    }

    REGISTRATION {
        uuid id PK
        uuid participante_id FK
        uuid batch_id FK
        uuid event_id FK
        enum status "CONFIRMADA, CANCELADA"
        datetime criadaEm
    }
```

## Notas de modelagem

- `REGISTRATION` referencia `event_id` além de `batch_id` para evitar join extra ao listar inscrições por evento (desnormalização proposital).
- `TICKET_BATCH.version` é o campo `@Version` do JPA usado no lock otimista.
- `REFRESH_TOKEN.token` é único e indexado para busca rápida na validação/rotação.
- `EVENT.organizador_id` só aceita usuários com `role = ORGANIZADOR` (validado em nível de aplicação, não de banco).
- Cardinalidade 1:N entre `TICKET_BATCH` e `REGISTRATION`: um lote pode ter várias inscrições, mas cada inscrição pertence a exatamente um lote.
