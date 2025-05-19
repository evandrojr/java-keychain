#!/bin/bash
# Script para compilar e criar o JAR java-keychain-app na raiz do projeto
set -e

./gradlew clean jar

JAR_ORIG=$(ls build/libs/java-keychain-app-*.jar | head -n1)
JAR_DEST=java-keychain-app.jar

if [ -f "$JAR_ORIG" ]; then
    cp "$JAR_ORIG" ./java-keychain-app.jar
    echo "JAR copiado para ./java-keychain-app.jar"
else
    echo "JAR não encontrado em build/libs. Verifique se a compilação foi bem-sucedida."
    exit 1
fi
