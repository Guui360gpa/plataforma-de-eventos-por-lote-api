# Fluxos de Sequência — EventFlow

## 1. Login e emissão de tokens

```mermaid
sequenceDiagram
    actor U as Usuário
    participant API as API (/auth/login)
    participant DB as Banco de Dados

    U->>API: POST /auth/login (email, senha)
    API->>DB: busca usuário por email
    DB-->>API: usuário + senhaHash
    API->>API: valida senha (hash)
    alt credenciais inválidas
        API-->>U: 401 Unauthorized
    else credenciais válidas
        API->>API: gera access token (curto)
        API->>DB: cria e salva refresh token
        API-->>U: 200 OK (access token, refresh token)
    end
```

## 2. Renovação de token (refresh rotation)

```mermaid
sequenceDiagram
    actor U as Usuário
    participant API as API (/auth/refresh)
    participant DB as Banco de Dados

    U->>API: POST /auth/refresh (refresh token)
    API->>DB: busca refresh token
    alt token não existe ou expirado
        API-->>U: 401 Unauthorized
    else token já revogado (reuso)
        API->>DB: revoga TODOS os tokens do usuário
        API-->>U: 401 Unauthorized (possível roubo de token)
    else token válido
        API->>DB: revoga token atual
        API->>DB: cria novo refresh token
        API->>API: gera novo access token
        API-->>U: 200 OK (novo access + refresh token)
    end
```

## 3. Inscrição em evento — disputa pela última vaga

```mermaid
sequenceDiagram
    actor P1 as Participante 1
    actor P2 as Participante 2
    participant API as API (/events/{id}/registrations)
    participant DB as Banco de Dados (TicketBatch)

    par Requisições concorrentes
        P1->>API: POST inscrição
    and
        P2->>API: POST inscrição
    end

    API->>DB: busca lote elegível (menor ordem, disponível)
    DB-->>API: lote com 1 vaga (@Version = N)

    par Tentativas de decremento simultâneas
        API->>DB: decrementa vaga (P1) [@Version = N]
    and
        API->>DB: decrementa vaga (P2) [@Version = N]
    end

    DB-->>API: sucesso para P1 (nova @Version = N+1)
    DB-->>API: OptimisticLockException para P2

    API->>DB: (para P2) busca próximo lote elegível
    alt existe outro lote com vaga
        API->>DB: decrementa vaga do próximo lote
        API-->>P2: 200 OK (inscrito no próximo lote)
    else nenhum lote disponível
        API-->>P2: 409 "vagas esgotadas"
    end

    API-->>P1: 200 OK (inscrição confirmada)
```

## 4. Cancelamento de inscrição

```mermaid
sequenceDiagram
    actor U as Participante
    participant API as API (/registrations/{id})
    participant DB as Banco de Dados

    U->>API: DELETE /registrations/{id}
    API->>DB: busca inscrição
    alt inscrição não existe ou não pertence ao usuário
        API-->>U: 403/404
    else status já CANCELADA
        API-->>U: 400 (idempotência: já cancelada)
    else status CONFIRMADA
        API->>DB: status = CANCELADA
        API->>DB: incrementa quantidadeDisponivel do lote original
        API-->>U: 200 OK
    end
```

## 5. Cancelamento de evento (cascata)

```mermaid
sequenceDiagram
    actor O as Organizador
    participant API as API (/events/{id})
    participant DB as Banco de Dados

    O->>API: PUT /events/{id} (status=CANCELADO)
    API->>API: valida se é o dono do evento
    API->>DB: status do evento = CANCELADO
    API->>DB: busca todas inscrições CONFIRMADA do evento
    loop para cada inscrição
        API->>DB: status = CANCELADA
        API->>DB: devolve vaga ao lote correspondente
    end
    API-->>O: 200 OK
```
