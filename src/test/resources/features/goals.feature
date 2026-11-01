
# language: pt

@funcional @regressivo @padrão
Funcionalidade: Gerenciar metas

  Como usuário do sistema
  Quero cadastrar, consultar, atualizar e excluir metas
  Para acompanhar os objetivos da minha empresa

  @smoke
  Cenário: Cadastrar uma meta - POST /api/goals
    Dado que eu esteja autenticado para gerenciar metas
    Quando eu cadastrar uma meta com os seguintes dados no endpoint "/api/goals":
      | title       | Reduzir consumo de energia |
      | description | Diminuir o consumo energético da empresa |
      | dimension   | ENVIRONMENTAL |
    Então o status da resposta da meta deve ser 201
    E a resposta da meta deve possuir os campos obrigatórios
    E a resposta da meta deve respeitar o contrato "Cadastro de meta"

  Cenário: Tentar cadastrar uma meta sem título - POST /api/goals
    Dado que eu esteja autenticado para gerenciar metas
    Quando eu cadastrar uma meta sem informar o título no endpoint "/api/goals"
    Então o status da resposta da meta deve ser 400

  @smoke
  Cenário: Listar todas as metas - GET /api/goals
    Dado que eu esteja autenticado para gerenciar metas
    Quando eu consultar a lista de metas no endpoint "/api/goals"
    Então o status da resposta da meta deve ser 200
    E a resposta da meta deve respeitar o contrato "Listagem de metas"

  Cenário: Listar metas por dimensão - GET /api/goals?dimension=ENVIRONMENTAL
    Dado que eu esteja autenticado para gerenciar metas
    Quando eu consultar as metas da dimensão "ENVIRONMENTAL" no endpoint "/api/goals"
    Então o status da resposta da meta deve ser 200

  Cenário: Listar metas por data de criação - GET /api/goals?created_at=2026-01-01
    Dado que eu esteja autenticado para gerenciar metas
    Quando eu consultar as metas criadas a partir de "2026-01-01" no endpoint "/api/goals"
    Então o status da resposta da meta deve ser 200

  @smoke
  Cenário: Consultar uma meta cadastrada - GET /api/goals/{id}
    Dado que eu esteja autenticado para gerenciar metas
    E que exista uma meta cadastrada
    Quando eu consultar essa meta no endpoint "/api/goals"
    Então o status da resposta da meta deve ser 200
    E a resposta da meta deve respeitar o contrato "Consulta de uma meta"

  Cenário: Consultar uma meta inexistente - GET /api/goals/{id}
    Dado que eu esteja autenticado para gerenciar metas
    Quando eu consultar uma meta inexistente no endpoint "/api/goals"
    Então o status da resposta da meta deve ser 404

  @smoke
  Cenário: Atualizar uma meta cadastrada - PUT /api/goals/{id}
    Dado que eu esteja autenticado para gerenciar metas
    E que exista uma meta cadastrada
    Quando eu atualizar essa meta no endpoint "/api/goals"
    Então o status da resposta da meta deve ser 200
    E a resposta da meta deve respeitar o contrato "Atualização de meta"

  @smoke
  Cenário: Excluir uma meta cadastrada - DELETE /api/goals/{id}
    Dado que eu esteja autenticado para gerenciar metas
    E que exista uma meta cadastrada
    Quando eu excluir essa meta no endpoint "/api/goals"
    Então o status da resposta da meta deve ser 204
    E a resposta da meta não deve possuir conteúdo
