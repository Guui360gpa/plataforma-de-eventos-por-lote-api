# Instrução de Colaboração Git/GitHub

## 1. Objetivo

Este documento define o padrão de utilização do Git e do GitHub no projeto, com o objetivo de manter o repositório organizado, facilitar o trabalho em equipe e reduzir conflitos durante o desenvolvimento.

A colaboração será baseada em **branches por funcionalidade**, **Pull Requests** e **revisão de código**, mantendo a branch `main` sempre estável.

## 2. Estrutura das Branches

A estrutura principal do projeto será:

```text
main
│
├── feature/nome-da-funcionalidade
├── feature/nome-da-funcionalidade
├── fix/nome-do-problema
└── docs/nome-da-documentacao
```

### `main`

A branch `main` representa a versão **estável do projeto**.

**Regras:**

- Não desenvolver diretamente na `main`.
- Não fazer `push` diretamente na `main`.
- Alterações devem entrar por meio de Pull Request.
- A `main` deve permanecer em condição funcional.

### `feature/*`

Utilizada para o desenvolvimento de novas funcionalidades.

Exemplos:

```text
feature/cadastro-usuario
feature/login
feature/criacao-evento
feature/listagem-eventos
```

### `fix/*`

Utilizada para correções de problemas ou bugs.

Exemplos:

```text
fix/erro-login
fix/validacao-email
fix/erro-cadastro
```

### `docs/*`

Utilizada para alterações exclusivamente relacionadas à documentação.

Exemplos:

```text
docs/readme
docs/api-documentation
```

## 3. Antes de Começar uma Tarefa

Sempre atualizar a `main` local antes de criar uma nova branch:

```bash
git switch main
git pull origin main
```

Depois, criar a branch correspondente à tarefa:

```bash
git switch -c feature/nome-da-tarefa
```

Exemplo:

```bash
git switch -c feature/cadastro-usuario
```

## 4. Durante o Desenvolvimento

Cada alteração deve ser realizada dentro da própria branch.

Não realizar alterações diretamente na `main`.

Os commits devem representar alterações específicas e possuir mensagens claras.

Exemplo:

```bash
git add .
git commit -m "feat: adiciona cadastro de usuario"
```

Exemplos de mensagens:

```text
feat: adiciona autenticação
fix: corrige validação de email
refactor: reorganiza UserService
docs: atualiza documentação da API
test: adiciona testes de usuario
```

## 5. Enviando a Branch para o GitHub

Na primeira vez que a branch for enviada:

```bash
git push -u origin feature/nome-da-tarefa
```

Exemplo:

```bash
git push -u origin feature/cadastro-usuario
```

Depois dos próximos commits:

```bash
git push
```

## 6. Pull Request

Quando a funcionalidade estiver pronta, deverá ser aberto um Pull Request:

```text
feature/cadastro-usuario
          ↓
    Pull Request
          ↓
        main
```

O Pull Request deve:

- Possuir um título claro.
- Explicar o que foi desenvolvido.
- Informar alterações importantes.
- Ser revisado pelo outro integrante.
- Ter os testes realizados antes do merge.
- Somente depois da revisão ser integrado à `main`.

## 7. Atualizando a Branch e Evitando Conflitos

Antes de finalizar uma funcionalidade, verificar se a `main` recebeu novas alterações.

Na sua branch:

```bash
git fetch origin
git merge origin/main
```

Se não houver conflitos, a branch estará atualizada.

Caso existam conflitos, eles devem ser resolvidos antes da integração.

## 8. Como Resolver Conflitos

Quando ocorrer um conflito, o Git poderá apresentar algo semelhante a:

```text
<<<<<<< HEAD
seu código
=======
código da main
>>>>>>> main
```

O conflito deve ser analisado cuidadosamente.

### Procedimento

1. Identificar o que cada alteração faz.
2. Decidir qual código deve permanecer ou combinar as alterações.
3. Remover os marcadores de conflito.
4. Testar o projeto.
5. Adicionar os arquivos corrigidos.
6. Criar um commit.
7. Enviar a resolução para o GitHub.

Comandos:

```bash
git add .
git commit -m "merge: resolve conflitos com main"
git push
```

## 9. Uso de `rebase`

Para manter o fluxo simples e previsível, o projeto utilizará **`merge` como estratégia padrão** para atualizar branches.

Evitar utilizar:

```bash
git rebase
```

principalmente em branches que já foram enviadas ao GitHub e estão sendo utilizadas pelo outro integrante.

Caso seja necessário utilizar `rebase`, os integrantes deverão combinar previamente.

## 10. Regra para `push --force`

É proibido utilizar:

```bash
git push --force
```

na branch `main`.

Também não utilizar `push --force` em uma branch que esteja sendo utilizada pelo outro integrante sem comunicação prévia.

## 11. Divisão das Tarefas

As branches devem representar **tarefas**, e não pessoas.

### Evitar

```text
main
guilherme
joao
```

### Preferir

```text
main
feature/login
feature/cadastro
feature/eventos
feature/pagamentos
```

Dessa forma, qualquer integrante pode trabalhar em qualquer funcionalidade.

## 12. Evitar Alterações Simultâneas

Sempre que possível, dividir as responsabilidades para evitar que os dois integrantes alterem simultaneamente os mesmos arquivos ou trechos de código.

Por exemplo:

```text
Guilherme
└── feature/cadastro-usuario

Outro integrante
└── feature/autenticacao
```

Arquivos centrais como:

```text
SecurityConfig.java
pom.xml
application.properties
User.java
```

devem receber atenção especial, pois alterações simultâneas podem aumentar a possibilidade de conflitos.

Quando for necessário que ambos trabalhem no mesmo arquivo, comunicar previamente.

## 13. Fluxo Padrão do Projeto

O fluxo oficial será:

```text
1. Atualizar main
       ↓
2. Criar feature branch
       ↓
3. Desenvolver
       ↓
4. Criar commits
       ↓
5. Fazer push
       ↓
6. Abrir Pull Request
       ↓
7. Realizar revisão
       ↓
8. Resolver conflitos, se houver
       ↓
9. Testar
       ↓
10. Fazer merge na main
       ↓
11. Deletar a branch
```

### Exemplo Completo

```bash
git switch main
git pull origin main

git switch -c feature/cadastro-usuario

# desenvolvimento...

git add .
git commit -m "feat: adiciona cadastro de usuario"

git push -u origin feature/cadastro-usuario
```

Depois que o Pull Request for aprovado:

```bash
git switch main
git pull origin main
```

A branch da funcionalidade poderá ser removida localmente:

```bash
git branch -d feature/cadastro-usuario
```

## 14. Regra Principal

> **A `main` é estável. Cada tarefa possui sua própria branch. Todo código passa por Pull Request antes de entrar na `main`.**

Esse fluxo deve ser seguido pelos integrantes para manter o projeto organizado, facilitar a colaboração e reduzir problemas relacionados a conflitos e alterações simultâneas.
