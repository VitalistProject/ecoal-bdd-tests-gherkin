
# language: pt

@funcional @regressivo @padrão
Funcionalidade: Gerenciar tarefas

  Como usuário do sistema
  Quero cadastrar, consultar, atualizar e excluir tarefas
  Para acompanhar o progresso das minhas metas

  @smoke
  Cenário: Cadastrar uma tarefa - POST /api/goals/{goalId}/tasks
    Dado que eu esteja autenticado para gerenciar tarefas
    E que exista uma meta temporária para os testes de tarefas
    Quando eu cadastrar uma tarefa no endpoint "/api/goals" com os seguintes dados:
      | title       | Reduzir consumo de energia |
      | description | Desligar equipamentos sem uso |
      | score       | 10.0 |
    Então o status da resposta da tarefa deve ser 201
    E a resposta da tarefa deve possuir os campos obrigatórios
    E a resposta da tarefa deve respeitar o contrato "Cadastro de tarefa"

  Cenário: Cadastrar uma tarefa sem título - POST /api/goals/{goalId}/tasks
    Dado que eu esteja autenticado para gerenciar tarefas
    E que exista uma meta temporária para os testes de tarefas
    Quando eu cadastrar uma tarefa sem título no endpoint "/api/goals"
    Então o status da resposta da tarefa deve ser 400

  @smoke
  Cenário: Listar tarefas - GET /api/goals/{goalId}/tasks
    Dado que eu esteja autenticado para gerenciar tarefas
    E que exista uma meta temporária para os testes de tarefas
    E que exista uma tarefa cadastrada nessa meta
    Quando eu consultar as tarefas no endpoint "/api/goals"
    Então o status da resposta da tarefa deve ser 200
    E a resposta da tarefa deve respeitar o contrato "Listagem de tarefas"

  Cenário: Listar tarefas concluídas - GET /api/goals/{goalId}/tasks?completed=true
    Dado que eu esteja autenticado para gerenciar tarefas
    E que exista uma meta temporária para os testes de tarefas
    E que exista uma tarefa cadastrada nessa meta
    Quando eu consultar as tarefas concluídas no endpoint "/api/goals"
    Então o status da resposta da tarefa deve ser 200

  Cenário: Listar tarefas pendentes - GET /api/goals/{goalId}/tasks?completed=false
    Dado que eu esteja autenticado para gerenciar tarefas
    E que exista uma meta temporária para os testes de tarefas
    E que exista uma tarefa cadastrada nessa meta
    Quando eu consultar as tarefas pendentes no endpoint "/api/goals"
    Então o status da resposta da tarefa deve ser 200

  Cenário: Listar tarefas por data - GET /api/goals/{goalId}/tasks?created_at=2026-01-01
    Dado que eu esteja autenticado para gerenciar tarefas
    E que exista uma meta temporária para os testes de tarefas
    E que exista uma tarefa cadastrada nessa meta
    Quando eu consultar as tarefas criadas a partir de "2026-01-01" no endpoint "/api/goals"
    Então o status da resposta da tarefa deve ser 200

  Cenário: Listar tarefas usando dois filtros - GET /api/goals/{goalId}/tasks?completed=true&created_at=2026-01-01
    Dado que eu esteja autenticado para gerenciar tarefas
    E que exista uma meta temporária para os testes de tarefas
    E que exista uma tarefa cadastrada nessa meta
    Quando eu consultar as tarefas concluídas criadas a partir de "2026-01-01" no endpoint "/api/goals"
    Então o status da resposta da tarefa deve ser 200

  @smoke
  Cenário: Consultar uma tarefa - GET /api/goals/{goalId}/tasks/{id}
    Dado que eu esteja autenticado para gerenciar tarefas
    E que exista uma meta temporária para os testes de tarefas
    E que exista uma tarefa cadastrada nessa meta
    Quando eu consultar essa tarefa no endpoint "/api/goals"
    Então o status da resposta da tarefa deve ser 200
    E a resposta da tarefa deve respeitar o contrato "Consulta de uma tarefa"

  Cenário: Consultar tarefa inexistente - GET /api/goals/{goalId}/tasks/{id}
    Dado que eu esteja autenticado para gerenciar tarefas
    E que exista uma meta temporária para os testes de tarefas
    Quando eu consultar uma tarefa inexistente no endpoint "/api/goals"
    Então o status da resposta da tarefa deve ser 404

  @smoke
  Cenário: Atualizar uma tarefa - PUT /api/goals/{goalId}/tasks/{id}
    Dado que eu esteja autenticado para gerenciar tarefas
    E que exista uma meta temporária para os testes de tarefas
    E que exista uma tarefa cadastrada nessa meta
    Quando eu atualizar essa tarefa no endpoint "/api/goals"
    Então o status da resposta da tarefa deve ser 200
    E a resposta da tarefa deve respeitar o contrato "Atualização de tarefa"

  @smoke
  Cenário: Excluir uma tarefa - DELETE /api/goals/{goalId}/tasks/{id}
    Dado que eu esteja autenticado para gerenciar tarefas
    E que exista uma meta temporária para os testes de tarefas
    E que exista uma tarefa cadastrada nessa meta
    Quando eu excluir essa tarefa no endpoint "/api/goals"
    Então o status da resposta da tarefa deve ser 204
    E a resposta da tarefa não deve possuir conteúdo
