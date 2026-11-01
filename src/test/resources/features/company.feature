
# language: pt

@funcional @regressivo @padrão
Funcionalidade: Gerenciar empresas

  Como usuário do sistema
  Quero cadastrar, consultar, atualizar e excluir empresas
  Para gerenciar as informações das organizações

  @smoke
  Cenário: Cadastrar uma empresa
    Dado que eu esteja autenticado para gerenciar empresas
    Quando eu cadastrar uma empresa com os seguintes dados:
      | name              | Empresa BDD Teste |
      | cnpj              | 98765432000199 |
      | global_score_goal | 80.0 |
    Então o status da resposta da empresa deve ser 201
    E a resposta da empresa deve possuir os campos obrigatórios

  Cenário: Tentar cadastrar uma empresa sem nome
    Dado que eu esteja autenticado para gerenciar empresas
    Quando eu cadastrar uma empresa sem informar o nome
    Então o status da resposta da empresa deve ser 400

  @smoke
  Cenário: Consultar uma empresa cadastrada
    Dado que eu esteja autenticado para gerenciar empresas
    E que exista uma empresa cadastrada
    Quando eu consultar essa empresa
    Então o status da resposta da empresa deve ser 200
    E a resposta da empresa deve possuir os campos obrigatórios

  Cenário: Consultar uma empresa inexistente
    Dado que eu esteja autenticado para gerenciar empresas
    Quando eu consultar a empresa com ID inexistente
    Então o status da resposta da empresa deve ser 404

  @smoke
  Cenário: Atualizar uma empresa cadastrada
    Dado que eu esteja autenticado para gerenciar empresas
    E que exista uma empresa cadastrada
    Quando eu atualizar os dados dessa empresa
    Então o status da resposta da empresa deve ser 200
    E a resposta da empresa deve possuir os campos obrigatórios

  Cenário: Tentar atualizar uma empresa com CNPJ já cadastrado
    Dado que eu esteja autenticado para gerenciar empresas
    E que exista uma empresa cadastrada
    Quando eu atualizar essa empresa utilizando um CNPJ já cadastrado
    Então o status da resposta da empresa deve ser 409

  @smoke
  Cenário: Excluir uma empresa cadastrada
    Dado que eu esteja autenticado para gerenciar empresas
    E que exista uma empresa cadastrada
    Quando eu excluir essa empresa
    Então o status da resposta da empresa deve ser 204
    E a resposta da empresa não deve possuir conteúdo
