#!/bin/bash

# Encerra em caso de erro em comandos essenciais
set -o pipefail

# Entra na raiz do projeto (um nível acima da pasta scripts)
cd "$(dirname "$0")/.." || exit 1

echo "================================"
echo "VERIFICANDO JAVA..."
echo "================================"

# Verifica se JAVA_HOME está configurado
if [ -z "$JAVA_HOME" ]; then
    echo "ERRO: JAVA_HOME nao esta configurado."
    echo "Instale o JDK 21 e configure JAVA_HOME."
    exit 1
fi

# Verifica se o Java existe no caminho informado
if [ ! -x "$JAVA_HOME/bin/java" ]; then
    echo "ERRO: Java nao encontrado em JAVA_HOME."
    exit 1
fi

# Adiciona o Java ao PATH
export PATH="$JAVA_HOME/bin:$PATH"

java -version

echo ""
echo "================================"
echo "EXECUTANDO TESTES BDD..."
echo "================================"

mvn clean test
RESULTADO=$?

echo ""
echo "================================"

if [ "$RESULTADO" -eq 0 ]; then
    echo "TESTES EXECUTADOS COM SUCESSO!"
else
    echo "FALHA NA EXECUCAO DOS TESTES."
fi

echo "================================"

exit "$RESULTADO"