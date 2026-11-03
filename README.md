# eCOAL — Testes Automatizados BDD

**eCOAL BDD Tests**: suíte de testes automatizados com BDD para a API REST do sistema de gestão ESG eCOAL. Os testes validam autenticação, empresas, metas, tarefas e métricas analíticas ESG, cobrindo status code, corpo de resposta e contratos JSON Schema.

---

## 1. Pré-requisitos

Para executar os testes automatizados, é necessário ter instalado:

- **Java JDK 21:** necessário para compilação e execução dos testes.
- **Apache Maven 3.10.0 ou superior:** utilizado para gerenciamento de dependências e execução dos testes.
- **Docker e Docker Compose:** necessários caso sejam utilizados para inicializar a API e seus serviços.

A variável de ambiente `JAVA_HOME` deve estar configurada apontando para a instalação do JDK 21.

Para verificar as instalações, execute:

```bash
java -version
mvn -version
```

---

## 2. API testada

**Importante:** a API que será testada precisa estar em execução localmente antes de iniciar os testes automatizados.

O projeto está configurado para realizar requisições ao seguinte endereço:

```text
http://localhost:8080
```

Certifique-se de que a API esteja inicializada e acessível nessa porta.

O código-fonte da API está disponível no seguinte repositório:

**Repositório da API:** https://github.com/VitalistProject/esg-ecoai-java

Consulte o README do repositório para obter as instruções de instalação, configuração e inicialização da aplicação.

---

## 3. Como executar os testes

Existem duas maneiras de executar os testes automatizados:

### Opção 1: Pelo terminal

1. Certifique-se de que a API esteja em execução localmente.
2. Abra um terminal na pasta raiz do projeto, onde está localizado o arquivo `pom.xml`.
3. Execute o seguinte comando:

```bash
mvn clean test
```

---

O Maven realizará a limpeza dos arquivos de compilação anteriores, compilará o projeto e executará os testes automatizados.

### Opção 2: Pelo arquivo BAT (Windows)

1. Certifique-se de que a API esteja em execução localmente.
2. Acesse a pasta `scripts` do projeto.
3. Execute o arquivo `run-tests.bat`.
4. Aguarde a conclusão da compilação e execução dos testes.

O script executará automaticamente o comando:

```bash
mvn clean test
```

---

### Opção 3: Pelo arquivo SH (Linux)

1. Certifique-se de que a API esteja em execução localmente.
2. Acesse a pasta `scripts` do projeto.
3. Dê permissão de execução ao arquivo `run-tests.sh` com o comando:

   ```bash
   chmod +x run-tests.sh
   ```

4. Execute o script:

   ```bash
   ./run-tests.sh
   ```

5. Aguarde a conclusão da compilação e execução dos testes.

O script executará automaticamente o comando:

```bash
mvn clean test
```

---

**Credenciais dos testes:**

| E-mail | Senha | Usado em |
|---|---|---|
| alice@example.com | password | company, goals, tasks |
| guilhermino@gmail.com | password | auth, analytics |

O CNPJ `12345678000100` precisa estar cadastrado no banco como empresa de referência para os testes de cadastro de usuário.

---

## 4. Arquitetura dos testes

O projeto segue o padrão **Service Layer**: os Step Definitions nunca fazem chamadas HTTP diretamente — delegam para classes de serviço dedicadas. Isso mantém os steps legíveis e a lógica HTTP centralizada.


**Padrões adotados:**

- **Isolamento por cenário** — Cucumber cria uma nova instância dos Steps a cada cenário, garantindo estado limpo.
- **Autenticação prévia** — cada Steps tem seu próprio passo `@Dado que eu esteja autenticado para X`, que obtém o JWT e o injeta no service.
- **Fixture inteligente** — `TaskService.createGoalFixture()` percorre as metas existentes e seleciona a primeira com menos de 10 tarefas, sem criar dados desnecessários.
- **Validação em dois níveis** — campos obrigatórios via `assertNotNull` + estrutura completa via JSON Schema.

---

## Tags

| Tag | Significado |
|---|---|
| `@padrão` | Aplicada em todas as Funcionalidades — executada por padrão pelo Runner |
| `@smoke` | Caminhos felizes e validações de contrato |
| `@regressivo` | Cenários negativos (erros esperados) |
| `@funcional` | Classificação funcional do teste |

---

## 5. Testes

### auth.feature — Autenticação e Acesso

**Cadastro de usuário — `POST /api/auth/sign-up`**

| Cenário | Tags | Status Esperado |
|---|---|---|
| Cadastro bem-sucedido de usuário | @smoke | 201 |
| Validar contrato do cadastro bem-sucedido | @smoke | 201 + JSON Schema ✓ |
| Cadastro com senha menor que 8 caracteres | @regressivo | 400 |
| Cadastro com e-mail já cadastrado | @regressivo | 409 |
| Cadastro com CNPJ de empresa inexistente | @regressivo | 404 |

**Gherkin:**

```gherkin
Cenário: Cadastro bem-sucedido de usuário - POST /api/auth/sign-up
  Dado que eu tenha os seguintes dados de cadastro:
    | campo        | valor                 |
    | name         | Guilhermino Silva     |
    | email        | guilhermino@gmail.com |
    | password     | password              |
    | role         | user                  |
    | company_cnpj | 12345678000100        |
    | department   | Operations            |
  Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-up" de cadastro de usuários
  Então o status code da resposta deve ser 201
```

---

**Login — `POST /api/auth/sign-in`**

| Cenário | Tags | Status Esperado |
|---|---|---|
| Login bem-sucedido | @smoke | 200 |
| Validar contrato do login bem-sucedido | @smoke | 200 + JSON Schema ✓ |
| Login com senha incorreta | @regressivo | 401 |
| Login com e-mail não cadastrado | @regressivo | 401 |
| Login com senha menor que 8 caracteres | @regressivo | 401 |

**Gherkin:**

```gherkin
Cenário: Login bem-sucedido de usuário - POST /api/auth/sign-in
  Dado que eu tenha os seguintes dados de login:
    | campo    | valor                 |
    | email    | guilhermino@gmail.com |
    | password | password              |
  Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-in" de autenticação de usuários
  Então o status code da resposta deve ser 200
```

---

**Consulta do usuário autenticado — `GET /api/auth/me`**

| Cenário | Tags | Status Esperado |
|---|---|---|
| Consultar dados com token válido | @smoke | 200 |
| Validar contrato da consulta | @smoke | 200 + JSON Schema ✓ |
| Consultar sem estar autenticado | @regressivo | 403 |
| Consultar com token inválido | @regressivo | 403 |

**Gherkin:**

```gherkin
Cenário: Consultar dados do usuário autenticado com sucesso - GET /api/auth/me
  Dado que eu esteja autenticado no sistema
  Quando eu enviar a requisição GET para o endpoint "/api/auth/me" de consulta dos dados do usuário
  Então o status code da resposta deve ser 200
```

---

### company.feature — Empresas

**CRUD — `/api/companies`**

| Cenário | Tags | Status Esperado |
|---|---|---|
| Cadastrar uma empresa | @smoke | 201 |
| Cadastrar empresa sem nome | @regressivo | 400 |
| Consultar empresa por ID | @smoke | 200 |
| Consultar empresa com ID inexistente | @regressivo | 404 |
| Atualizar empresa com dados válidos | @smoke | 200 |
| Atualizar empresa com CNPJ já cadastrado | @regressivo | 409 |
| Excluir empresa | @smoke | 204 |

**Gherkin:**

```gherkin
Cenário: Cadastrar uma empresa - POST /api/companies
  Dado que eu esteja autenticado para gerenciar empresas
  Quando eu cadastrar uma empresa com os seguintes dados:
    | name              | Empresa BDD Teste |
    | cnpj              | 98765432000199    |
    | global_score_goal | 80.0              |
  Então o status da resposta da empresa deve ser 201
  E a resposta da empresa deve possuir os campos obrigatórios

Cenário: Consultar uma empresa inexistente - GET /api/companies/{id}
  Dado que eu esteja autenticado para gerenciar empresas
  Quando eu consultar a empresa com ID inexistente
  Então o status da resposta da empresa deve ser 404
```

---

### goals.feature — Metas ESG

As metas são organizadas por dimensão: `ENVIRONMENTAL`, `SOCIAL` ou `GOVERNANCE`.

**CRUD + Filtros — `/api/goals`**

| Cenário | Tags | Status Esperado |
|---|---|---|
| Cadastrar meta com dados válidos | @smoke | 201 + JSON Schema ✓ |
| Cadastrar meta sem título | @regressivo | 400 |
| Listar todas as metas | @smoke | 200 + JSON Schema ✓ |
| Listar metas por dimensão `?dimension=ENVIRONMENTAL` | @smoke | 200 |
| Listar metas por data `?created_at=2026-01-01` | @smoke | 200 |
| Consultar meta por ID | @smoke | 200 + JSON Schema ✓ |
| Consultar meta inexistente | @regressivo | 404 |
| Atualizar meta | @smoke | 200 + JSON Schema ✓ |
| Excluir meta | @smoke | 204 |

**Gherkin:**

```gherkin
Cenário: Cadastrar uma meta - POST /api/goals
  Dado que eu esteja autenticado para gerenciar metas
  Quando eu cadastrar uma meta com os seguintes dados no endpoint "/api/goals":
    | title       | Reduzir consumo de energia               |
    | description | Diminuir o consumo energético da empresa |
    | dimension   | ENVIRONMENTAL                            |
  Então o status da resposta da meta deve ser 201
  E a resposta da meta deve possuir os campos obrigatórios
  E a resposta da meta deve respeitar o contrato "Cadastro de meta"

Cenário: Listar metas por dimensão - GET /api/goals?dimension=ENVIRONMENTAL
  Dado que eu esteja autenticado para gerenciar metas
  Quando eu consultar as metas da dimensão "ENVIRONMENTAL" no endpoint "/api/goals"
  Então o status da resposta da meta deve ser 200

Cenário: Excluir uma meta cadastrada - DELETE /api/goals/{id}
  Dado que eu esteja autenticado para gerenciar metas
  E que exista uma meta cadastrada
  Quando eu excluir essa meta no endpoint "/api/goals"
  Então o status da resposta da meta deve ser 204
  E a resposta da meta não deve possuir conteúdo
```

---

### tasks.feature — Tarefas

As tarefas são filhas de metas e acessadas via `/api/goals/{goalId}/tasks`. O passo `que exista uma meta temporária para os testes de tarefas` usa um fixture inteligente que seleciona automaticamente uma meta com capacidade disponível (menos de 10 tarefas).

| Cenário | Tags | Status Esperado |
|---|---|---|
| Cadastrar tarefa com dados válidos | @smoke | 201 + JSON Schema ✓ |
| Cadastrar tarefa sem título | @regressivo | 400 |
| Listar tarefas da meta | @smoke | 200 + JSON Schema ✓ |
| Listar tarefas concluídas `?completed=true` | @smoke | 200 |
| Listar tarefas pendentes `?completed=false` | @smoke | 200 |
| Listar tarefas por data `?created_at=2026-01-01` | @smoke | 200 |
| Listar com dois filtros combinados | @smoke | 200 |
| Consultar tarefa por ID | @smoke | 200 + JSON Schema ✓ |
| Consultar tarefa inexistente | @regressivo | 404 |
| Atualizar tarefa | @smoke | 200 + JSON Schema ✓ |
| Excluir tarefa | @smoke | 204 |

**Gherkin:**

```gherkin
Cenário: Cadastrar uma tarefa - POST /api/goals/{goalId}/tasks
  Dado que eu esteja autenticado para gerenciar tarefas
  E que exista uma meta temporária para os testes de tarefas
  Quando eu cadastrar uma tarefa no endpoint "/api/goals" com os seguintes dados:
    | title       | Reduzir consumo de energia    |
    | description | Desligar equipamentos sem uso |
    | score       | 10.0                          |
  Então o status da resposta da tarefa deve ser 201
  E a resposta da tarefa deve possuir os campos obrigatórios
  E a resposta da tarefa deve respeitar o contrato "Cadastro de tarefa"

Cenário: Listar tarefas usando dois filtros - GET /api/goals/{goalId}/tasks?completed=true&created_at=2026-01-01
  Dado que eu esteja autenticado para gerenciar tarefas
  E que exista uma meta temporária para os testes de tarefas
  E que exista uma tarefa cadastrada nessa meta
  Quando eu consultar as tarefas concluídas criadas a partir de "2026-01-01" no endpoint "/api/goals"
  Então o status da resposta da tarefa deve ser 200
```

---

### analytics.feature — Analytics ESG

| Cenário | Endpoint | Tags | Status Esperado |
|---|---|---|---|
| Consultar progresso das dimensões | `GET /api/analytics/dimensions` | @smoke | 200 |
| Consultar progresso com `?period=monthly` | `GET /api/analytics/dimensions?period=monthly` | @smoke | 200 |
| Validar contrato do progresso das dimensões | `GET /api/analytics/dimensions` | @smoke | 200 + JSON Schema ✓ |
| Consultar pontuação geral | `GET /api/analytics/score` | @smoke | 200 |
| Validar contrato da pontuação geral | `GET /api/analytics/score` | @smoke | 200 + JSON Schema ✓ |
| Consultar dados do dashboard | `GET /api/analytics/dashboard` | @smoke | 200 |
| Validar contrato do dashboard | `GET /api/analytics/dashboard` | @smoke | 200 + JSON Schema ✓ |

**Gherkin:**

```gherkin
Cenário: Validar contrato da consulta de progresso das dimensões - GET /api/analytics/dimensions
  Dado que eu esteja autenticado para consultar os dados analíticos
  Quando eu consultar o endpoint "/api/analytics/dimensions" de progresso das dimensões
  Então o status da resposta deve ser 200
  E a resposta deve atender ao contrato "Consulta de progresso das dimensões"

Cenário: Validar contrato da consulta do dashboard - GET /api/analytics/dashboard
  Dado que eu esteja autenticado para consultar os dados analíticos
  Quando eu consultar o endpoint "/api/analytics/dashboard" de dados do dashboard
  Então o status da resposta deve ser 200
  E a resposta deve atender ao contrato "Consulta dos dados do dashboard"
```

---

## 6. Contratos JSON Schema

Cada contrato define a estrutura obrigatória da resposta da API. A validação usa `networknt json-schema-validator` — se qualquer campo obrigatório estiver ausente ou com tipo incorreto, o teste falha com a mensagem de violação.

| Schema | Endpoint | Campos obrigatórios |
|---|---|---|
| `cadastro-bem-sucedido-de-usuario.json` | POST /api/auth/sign-up | id, name, email, role, company_id |
| `autenticacao-bem-sucedida-de-usuario.json` | POST /api/auth/sign-in | token, user.{id, name, email, role} |
| `consulta_bem_sucedida_de_usuario_autenticado.json` | GET /api/auth/me | id, name, email, role, department, company_id |
| `meta.json` | POST/GET/PUT /api/goals | id, title, description, dimension, tasks, company_id, created_at, updated_at |
| `lista-metas.json` | GET /api/goals | Array de objetos meta |
| `tarefa.json` | POST/GET/PUT /api/goals/{id}/tasks | id, title, description, score, completed, completed_at, goal_id, created_at, updated_at |
| `lista-tarefas.json` | GET /api/goals/{id}/tasks | Array de objetos tarefa |
| `consulta-progresso-dimensoes.json` | GET /api/analytics/dimensions | dimensions[].{dimension, completed, total, percentage} |
| `consulta-pontuacao-geral.json` | GET /api/analytics/score | score, max_score, percentage |
| `consulta-dashboard.json` | GET /api/analytics/dashboard | Dados consolidados do dashboard ESG |

---

## 7. Tecnologias utilizadas

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem principal |
| Maven | 3.9+ | Build e gerenciamento de dependências |
| Cucumber Java | 7.18.1 | Motor BDD — interpreta Gherkin em PT-BR |
| Cucumber JUnit | 7.18.1 | Integração com JUnit 4 |
| REST Assured | 5.5.0 | Cliente HTTP para chamadas à API |
| JUnit | 4 (via cucumber-junit) | Execução e asserções |
| networknt json-schema-validator | 1.5.1 | Validação de contratos JSON Schema |
| Gson | 2.11.0 | Serialização de payloads de request |
| Lombok | 1.18.34 | Geração de getters/setters nos modelos |
| org.json | 20240303 | Montagem de JSONObjects nos serviços |
