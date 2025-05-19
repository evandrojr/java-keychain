# Java Keychain App

This project is a Java application that allows users to securely store and retrieve sensitive data using the system's encrypted keychain (Linux GNOME/KDE, macOS).


## Requirements and Compatibility

- Java 7 or higher
- Gradle

## Requirements and Compatibility

- Java 7 or higher
- Gradle

### Supported Operating Systems and Desktop Environments

- **Linux**
  - **Tested distributions:** Ubuntu 20.04+, Fedora 34+, Debian 11+, Arch Linux (2022+)
  - **Supported desktop environments:**
    - GNOME 3.36+
    - KDE Plasma 5.18+
  - **Keychain integration:**
    - GNOME: `secret-tool` (libsecret)
    - KDE: `kwalletcli`

- **macOS**
  - **Tested versions:** macOS 10.15 (Catalina)+, 11 (Big Sur), 12 (Monterey), 13 (Ventura)
  - **Keychain integration:** Built-in `security` command

- **Windows**
  - **Tested versions:** Windows 10, Windows 11
  - **Keychain integration:** Windows Credential Manager (via PowerShell)
  - **Requirements:** PowerShell and the [CredentialManager](https://www.powershellgallery.com/packages/CredentialManager) module

#### Windows Support

On Windows, the application uses the Windows Credential Manager for secure password storage via native integration (JNA). No PowerShell or external module is required for the main app features.

**Requirements:**
- Java 7 or higher (64-bit recommended)
- Windows 10 or 11


**How it works:**
- The app uses the system keychain (Credential Manager, Keychain, GNOME Keyring) for secure password storage and retrieval.
- All credential data is stored encrypted by the OS.
- For extra security, the Java KeyStore (JCEKS) used to encrypt local values is protected by a strong random password, which is itself stored in the system keychain.
- The password for the KeyStore is never hardcoded or shown to the user.
- All steps are logged for audit and troubleshooting.

**KeyStore password management flow:**
1. On first run, the app tries to load the KeyStore password from the system keychain.
2. If not found, it generates a strong random password, saves it in the keychain, and uses it to create the KeyStore.
3. On subsequent runs, the password is loaded from the keychain and used to open the KeyStore.
4. The KeyStore stores the AES key used to encrypt/decrypt values saved locally.
5. All steps are logged to the console.

**Extra protection: encryption of the KeyStore password before saving to the keychain**

To further increase security, the KeyStore password is encrypted with a fixed AES key (hardcoded in the application) before being stored in the system keychain. This means:

- Even if an attacker gains access to the keychain, they will only see the encrypted password, not the real KeyStore password.
- Only the application (with the fixed key) can decrypt and use the real KeyStore password.
- The fixed key is embedded in the application code. For maximum security, do not share or expose this key.

**Flow summary:**

1. On first run, the app generates a strong random password for the KeyStore.
2. This password is encrypted with AES using a fixed key.
3. The encrypted password is saved in the system keychain.
4. On subsequent runs, the encrypted password is loaded from the keychain and decrypted in memory before use.
5. The decrypted password is used to open the KeyStore.

> **Note:** This adds an extra layer of protection, but if an attacker has access to both the keychain and the application binary (and can reverse engineer it), they may still recover the fixed key. This approach is a defense-in-depth measure, not a substitute for OS-level security.

**Log example:**
```
[KeychainService] Iniciando serviço de keychain seguro...
[KeychainService] Buscando senha do KeyStore no keychain do SO...
[KeychainService] Senha do KeyStore não encontrada. Gerando nova senha aleatória...
[KeychainService] Nova senha salva no keychain do SO com sucesso.
[KeychainService] KeyStore não encontrado. Criando novo KeyStore protegido por senha do keychain...
[KeychainService] Novo KeyStore criado e salvo em /home/usuario/keychain.jks
[KeychainService] KeyStore inicializado e pronto para uso.
```

**Troubleshooting:**
- If you see errors like `UnsatisfiedLinkError` or issues saving/loading passwords:
  - Make sure you are using a 64-bit Java (run `java -version` and check for "64-Bit").
  - Ensure the JNA and jna-platform dependencies are present in your JAR (the build includes them by default).
  - Run the app as a regular user (not as Administrator) for normal credential storage.
  - If you upgraded from an old version, delete any old credentials and try again.
- All errors and operations are logged to the console for easier troubleshooting.

**No PowerShell or CredentialManager module is needed for the main app.**
## Como gerar o instalador para Windows

1. Gere o JAR do aplicativo:
   ```
   ./gradlew jar
   ```
   O arquivo será criado em `build/libs/` (ex: `java-keychain-app-1.0.0.jar`).

2. Instale o Inno Setup no Windows:
   - Baixe em: https://jrsoftware.org/isinfo.php

3. Copie para a mesma pasta:
   - O JAR gerado (`build/libs/java-keychain-app-1.0.0.jar`)
   - O arquivo `JavaKeychainApp.iss`
   - (Opcional) O script `install-deps.ps1` (apenas se desejar instalar dependências extras, como Java, automaticamente)

4. (Opcional) Edite o arquivo `JavaKeychainApp.iss` para garantir que o nome do JAR está correto.

5. Compile o instalador:
   - Abra o Inno Setup Compiler
   - Abra o arquivo `JavaKeychainApp.iss`
   - Clique em “Compile”
   - O instalador `.exe` será gerado na pasta de saída

6. Execute o instalador `.exe` no Windows para instalar e configurar todas as dependências automaticamente.
## How to Run with Auto Reload (Linux)


If you want the application to automatically recompile and restart every time you save a `.java` file, you can use the `entr` utility (Linux only):

1. Install entr:
   ```
   sudo apt-get install entr
   ```

2. Run the provided script:
   ```
   ./auto-reload.sh
   ```

Every time you save a Java file, the application will be rebuilt and restarted automatically.

## How to Run the Program

To run the program normally, use:
```
./gradlew run
```



### Supported Operating Systems and Desktop Environments

- **Linux**
  - **Tested distributions:** Ubuntu 20.04+, Fedora 34+, Debian 11+, Arch Linux (2022+)
  - **Supported desktop environments:**
    - GNOME 3.36+
    - KDE Plasma 5.18+
  - **Keychain integration:**
    - GNOME: `secret-tool` (libsecret)
    - KDE: `kwalletcli`

- **macOS**
  - **Tested versions:** macOS 10.15 (Catalina)+, 11 (Big Sur), 12 (Monterey), 13 (Ventura)
  - **Keychain integration:** Built-in `security` command

- **Windows**
  - **Tested versions:** Windows 10, Windows 11
  - **Keychain integration:** Windows Credential Manager 

#### Windows Support

On Windows, the application uses the Windows Credential Manager for secure password storage. This requires PowerShell e o módulo CredentialManager.


**Se ocorrer erro ao salvar ou recuperar senhas no Windows:**
- Certifique-se de que o PowerShell está instalado e atualizado.
  - Se não estiver instalado, instale pela Microsoft Store (recomendado) ou pelo site oficial:
    - [PowerShell na Microsoft Store](ms-windows-store://pdp/?productid=9MZ1SNWT0N5D)
    - [PowerShell no site oficial](https://github.com/PowerShell/PowerShell)
- Instale o módulo CredentialManager:
  1. Abra o PowerShell como Administrador
  2. Execute:
     ```
     Install-Module -Name CredentialManager -Scope CurrentUser
     ```

Após instalar, tente novamente. Não são necessárias outras dependências além de Java e Gradle.

## Installing Dependencies

All Java dependencies are managed automatically by Gradle. The first time you run any Gradle command, dependencies will be downloaded automatically.

If you want to download all dependencies manually, run:
```
./gradlew dependencies
```

No manual installation of Java libraries is required.

### System dependencies for Linux keychain integration

To use the system keychain integration on Linux, you need one of the following utilities installed:

- For GNOME (most Ubuntu, Fedora, etc):
  - `secret-tool` (package: `libsecret-tools`)
  - Install with:
    ```
    sudo apt-get install libsecret-tools
    ```
- For KDE:
  - `kwalletcli` (package: `kwalletcli`)
  - Install with:
    ```
    sudo apt-get install kwalletcli
    ```

On macOS, no extra dependencies are needed (uses the built-in `security` command).

## How to Run the Application

1. Clone the repository:
   ```
   git clone https://github.com/evandrojr/java-keychain-app
   cd java-keychain-app
   ```

2. Build the project using Gradle:
   ```
   ./gradlew build
   ```


3. Run the application:
   ```
   ./gradlew run
   ```

## How to Generate the JAR

To generate a runnable JAR file, use:
```
./gradlew jar
```
The JAR will be created in `build/libs/`. To run it (replace VERSION with the actual version):
```
java -cp build/libs/java-keychain-app-VERSION.jar com.example.keychainapp.Main
```

## How to Run the Tests

To run the automated tests, use the following command:
```
./gradlew test
```

## Contributions

Contributions are welcome! Feel free to open issues or pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

