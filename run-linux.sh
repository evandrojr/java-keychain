#!/bin/bash
# Script para rodar o Java Keychain App no Linux
# Certifique-se de ter o Java instalado e o JAR na mesma pasta

JAR="java-keychain-app.jar"
if [ ! -f "$JAR" ]; then
    echo "JAR $JAR não encontrado. Execute o build ou copie o arquivo para esta pasta."
    exit 1
fi

java -jar "$JAR"
