# language: pt

@funcional @regressivo @padrão
Funcionalidade: Consultar dados analíticos

  Como usuário do sistema
  Quero consultar os dados analíticos
  Para acompanhar o desempenho das metas e indicadores ESG

  @smoke
  Cenário: Consultar progresso das dimensões com período padrão - GET /api/analytics/dimensions
    Dado que eu esteja autenticado para consultar os dados analíticos
    Quando eu consultar o endpoint "/api/analytics/dimensions" de progresso das dimensões
    Então o status da resposta deve ser 200

  @smoke
  Cenário: Consultar progresso das dimensões com período mensal - GET /api/analytics/dimensions?period=monthly
    Dado que eu esteja autenticado para consultar os dados analíticos
    Quando eu consultar o endpoint "/api/analytics/dimensions" de progresso das dimensões com período "monthly"
    Então o status da resposta deve ser 200

  @smoke
  Cenário: Validar contrato da consulta de progresso das dimensões - GET /api/analytics/dimensions
    Dado que eu esteja autenticado para consultar os dados analíticos
    Quando eu consultar o endpoint "/api/analytics/dimensions" de progresso das dimensões
    Então o status da resposta deve ser 200
    E a resposta deve atender ao contrato "Consulta de progresso das dimensões"

  @smoke
  Cenário: Consultar pontuação geral - GET /api/analytics/score
    Dado que eu esteja autenticado para consultar os dados analíticos
    Quando eu consultar o endpoint "/api/analytics/score" de pontuação geral
    Então o status da resposta deve ser 200

  @smoke
  Cenário: Validar contrato da consulta de pontuação geral - GET /api/analytics/score
    Dado que eu esteja autenticado para consultar os dados analíticos
    Quando eu consultar o endpoint "/api/analytics/score" de pontuação geral
    Então o status da resposta deve ser 200
    E a resposta deve atender ao contrato "Consulta de pontuação geral"

  @smoke
  Cenário: Consultar dados do dashboard - GET /api/analytics/dashboard
    Dado que eu esteja autenticado para consultar os dados analíticos
    Quando eu consultar o endpoint "/api/analytics/dashboard" de dados do dashboard
    Então o status da resposta deve ser 200

  @smoke
  Cenário: Validar contrato da consulta do dashboard - GET /api/analytics/dashboard
    Dado que eu esteja autenticado para consultar os dados analíticos
    Quando eu consultar o endpoint "/api/analytics/dashboard" de dados do dashboard
    Então o status da resposta deve ser 200
    E a resposta deve atender ao contrato "Consulta dos dados do dashboard"