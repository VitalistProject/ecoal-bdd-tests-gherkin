# language: pt

Funcionalidade: Autenticação e gerenciamento de acesso de usuários
  Como usuário do sistema
  Quero realizar meu cadastro, autenticar minha conta e consultar meus dados
  Para acessar e utilizar as funcionalidades do sistema ESG

  # CADASTRO DE USUÁRIO

  @smoke @funcional @regressivo @padrão
  Cenário: Cadastro bem-sucedido de usuário
    Dado que eu tenha os seguintes dados de cadastro:
      | campo        | valor                  |
      | name         | Guilhermino Silva      |
      | email        | guilhermino@gmail.com  |
      | password     | password               |
      | role         | user                   |
      | company_cnpj | 12345678000100         |
      | department   | Operations             |
    Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-up" de cadastro de usuários
    Então o status code da resposta deve ser 201

  @funcional @regressivo @padrão
  Cenário: Cadastro de usuário sem sucesso ao passar o campo password com menos de 8 caracteres
    Dado que eu tenha os seguintes dados de cadastro:
      | campo        | valor                  |
      | name         | Guilhermino Almeida    |
      | email        | guigui@gmail.com       |
      | password     | senha                  |
      | role         | user                   |
      | company_cnpj | 12345678000100         |
      | department   | Operations             |
    Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-up" de cadastro de usuários
    Então o status code da resposta deve ser 400
    E o corpo de resposta de erro da api deve retornar a mensagem "size must be between 8 and 2147483647"

  @funcional @regressivo @padrão
  Cenário: Cadastro de usuário sem sucesso ao utilizar um email já cadastrado
    Dado que eu tenha os seguintes dados de cadastro:
      | campo        | valor                  |
      | name         | Guilhermino Silva      |
      | email        | guilhermino@gmail.com  |
      | password     | password               |
      | role         | user                   |
      | company_cnpj | 12345678000100         |
      | department   | Operations             |
    Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-up" de cadastro de usuários
    Então o status code da resposta deve ser 409
    E o corpo de resposta de erro da api deve retornar a mensagem de erro do email "Email already registered"

  @funcional @regressivo @padrão
  Cenário: Cadastro de usuário sem sucesso ao informar um CNPJ de empresa inexistente
    Dado que eu tenha os seguintes dados de cadastro:
      | campo        | valor                         |
      | name         | Guilhermino Silva             |
      | email        | guiguiinexistente@gmail.com   |
      | password     | password                      |
      | role         | user                          |
      | company_cnpj | 99999999000199                |
      | department   | Operations                    |
    Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-up" de cadastro de usuários
    Então o status code da resposta deve ser 404
    E o corpo de resposta de erro da api deve retornar a mensagem de erro do cnpj "Company not found"

  @funcional @regressivo @padrão
  Cenário: Validar contrato do cadastro bem-sucedido de usuário
    Dado que eu tenha os seguintes dados de cadastro:
      | campo        | valor                  |
      | name         | Guilhermino Silva      |
      | email        | guilhermes@gmail.com   |
      | password     | senha1234              |
      | role         | user                   |
      | company_cnpj | 12345678000100         |
      | department   | Operations             |
    Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-up" de cadastro de usuários
    Então o status code da resposta deve ser 201
    E que o arquivo de contrato esperado é o "Cadastro bem-sucedido de usuario"
    Então a resposta da requisição deve estar em conformidade com o contrato selecionado

  # AUTENTICAÇÃO DE USUÁRIO

  @smoke @funcional @regressivo @padrão
  Cenário: Login bem-sucedido de usuário
    Dado que eu tenha os seguintes dados de login:
      | campo    | valor                 |
      | email    | guilhermino@gmail.com |
      | password | password              |
    Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-in" de autenticação de usuários
    Então o status code da resposta deve ser 200

  @funcional @regressivo @padrão
  Cenário: Login sem sucesso ao informar uma senha incorreta
    Dado que eu tenha os seguintes dados de login:
      | campo    | valor                 |
      | email    | guilhermino@gmail.com |
      | password | senhaerrada           |
    Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-in" de autenticação de usuários
    Então o status code da resposta deve ser 401

  @funcional @regressivo @padrão
  Cenário: Login sem sucesso ao informar um email não cadastrado
    Dado que eu tenha os seguintes dados de login:
      | campo    | valor                     |
      | email    | inexistente@gmail.com     |
      | password | password                  |
    Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-in" de autenticação de usuários
    Então o status code da resposta deve ser 401

  @funcional @regressivo @padrão
  Cenário: Login sem sucesso ao informar uma senha com menos de 8 caracteres
    Dado que eu tenha os seguintes dados de login:
      | campo    | valor                 |
      | email    | guilhermino@gmail.com |
      | password | senha                 |
    Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-in" de autenticação de usuários
    Então o status code da resposta deve ser 401

  @funcional @regressivo @padrão
  Cenário: Validar contrato do login bem-sucedido de usuário
    Dado que eu tenha os seguintes dados de login:
      | campo    | valor                 |
      | email    | guilhermino@gmail.com |
      | password | password              |
    Quando eu enviar a requisição POST para o endpoint "/api/auth/sign-in" de autenticação de usuários
    Então o status code da resposta deve ser 200
    E que o arquivo de contrato esperado é o "Autenticacao bem-sucedida de usuario"
    Então a resposta da requisição deve estar em conformidade com o contrato selecionado

  # CONSULTA DO USUÁRIO AUTENTICADO

  @smoke @funcional @regressivo @padrão
  Cenário: Consultar dados do usuário autenticado com sucesso
    Dado que eu esteja autenticado no sistema
    Quando eu enviar a requisição GET para o endpoint "/api/auth/me" de consulta dos dados do usuário
    Então o status code da resposta deve ser 200

  @funcional @regressivo @padrão
  Cenário: Consultar dados do usuário sem estar autenticado
    Dado que eu não esteja autenticado no sistema
    Quando eu enviar a requisição GET para o endpoint "/api/auth/me" de consulta dos dados do usuário
    Então o status code da resposta deve ser 403

  @funcional @regressivo @padrão
  Cenário: Consultar dados do usuário com token inválido
    Dado que eu esteja utilizando um token de autenticação inválido
    Quando eu enviar a requisição GET para o endpoint "/api/auth/me" de consulta dos dados do usuário
    Então o status code da resposta deve ser 403

  @funcional @regressivo @padrão
  Cenário: Validar contrato da consulta dos dados do usuário autenticado
    Dado que eu esteja autenticado no sistema
    Quando eu enviar a requisição GET para o endpoint "/api/auth/me" de consulta dos dados do usuário
    Então o status code da resposta deve ser 200
    E que o arquivo de contrato esperado é o "Consulta bem sucedida de usuario autenticado"
    Então a resposta da requisição deve estar em conformidade com o contrato selecionado