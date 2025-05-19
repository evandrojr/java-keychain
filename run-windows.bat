@echo off
REM Script para rodar o Java Keychain App no Windows
REM Certifique-se de ter o Java 64-bit instalado e o JAR na mesma pasta

set JAR=java-keychain-app.jar
if not exist %JAR% (
    echo JAR %JAR% nao encontrado. Execute o build ou copie o arquivo para esta pasta.
    pause
    exit /b 1
)

java -jar %JAR%

pause
