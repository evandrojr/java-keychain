package com.example.keychainapp.logic;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import com.example.keychainapp.logic.CryptoUtils;
import com.example.keychainapp.logic.SystemKeychain.KeychainException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Base64;
import java.security.SecureRandom;
import java.util.logging.Logger;


/**
 * KeychainService
 *
 * - Gera uma senha aleatória forte para o KeyStore Java na primeira execução.
 * - Salva essa senha no keychain do sistema operacional (Credential Manager, Keychain, GNOME Keyring).
 * - Recupera a senha do keychain nas execuções seguintes.
 * - Usa a senha para proteger o KeyStore, que armazena a chave AES usada para criptografar os valores.
 * - Todos os passos são logados para facilitar auditoria e troubleshooting.
 */
public class KeychainService {
    private static final String KEYSTORE_TYPE = "JCEKS";
    private static final String KEYSTORE_PATH = System.getProperty("user.home") + "/keychain.jks";
    private static final String KEY_ALIAS = "keychain_key";
    private static final String KEYCHAIN_SERVICE = "JavaKeychainApp";
    private static final String KEYCHAIN_KEY = "keystore-password";
    private String keystorePassword;
    private static final Logger LOGGER = Logger.getLogger(KeychainService.class.getName());


    public KeychainService() {
        try {
            LOGGER.info("[KeychainService] Iniciando serviço de keychain seguro...");
            keystorePassword = loadOrGenerateKeystorePassword();
            initializeKeyStore();
            LOGGER.info("[KeychainService] KeyStore inicializado e pronto para uso.");
        } catch (Exception e) {
            LOGGER.severe("[KeychainService] Erro ao inicializar o serviço: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Recupera a senha do KeyStore do keychain do SO, ou gera uma nova se não existir.
     * Loga cada passo do processo.
     */
    private String loadOrGenerateKeystorePassword() throws KeychainException
{
        LOGGER.info("[KeychainService] Buscando senha do KeyStore no keychain do SO...");
        
        String encryptedPwd = SystemKeychain.loadPassword(KEYCHAIN_SERVICE, KEYCHAIN_KEY);
        if (encryptedPwd != null && !encryptedPwd.isEmpty()) {
            try {
                String pwd = CryptoUtils.decrypt(encryptedPwd);
                LOGGER.info("[KeychainService] Senha do KeyStore encontrada no keychain do SO (criptografada).");
                return pwd;
            } catch (Exception e) {
                LOGGER.severe("[KeychainService] Falha ao descriptografar a senha do KeyStore: " + e.getMessage());
            }
        }
        LOGGER.info("[KeychainService] Senha do KeyStore não encontrada. Gerando nova senha aleatória...");
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String generated = Base64.getEncoder().encodeToString(bytes);
        try {
            String encrypted = CryptoUtils.encrypt(generated);
            SystemKeychain.savePassword(KEYCHAIN_SERVICE, KEYCHAIN_KEY, encrypted);
            LOGGER.info("[KeychainService] Nova senha salva no keychain do SO com sucesso (criptografada).");
        } catch (Exception e) {
            LOGGER.severe("[KeychainService] Falha ao criptografar a senha do KeyStore: " + e.getMessage());
        }
        return generated;
    }

    /**
     * Inicializa o KeyStore Java, criando um novo se não existir.
     * Loga o processo para auditoria.
     */
    private void initializeKeyStore() throws Exception {
        if (!Files.exists(Paths.get(KEYSTORE_PATH))) {
            LOGGER.info("[KeychainService] KeyStore não encontrado. Criando novo KeyStore protegido por senha do keychain...");
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            keyStore.load(null, null);
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            keyStore.setKeyEntry(KEY_ALIAS, secretKey, keystorePassword.toCharArray(), null);
            try (FileOutputStream fos = new FileOutputStream(KEYSTORE_PATH)) {
                keyStore.store(fos, keystorePassword.toCharArray());
            }
            LOGGER.info("[KeychainService] Novo KeyStore criado e salvo em " + KEYSTORE_PATH);
        } else {
            LOGGER.info("[KeychainService] KeyStore já existe em " + KEYSTORE_PATH);
        }
    }

    public void save(String key, String value) throws Exception {
        SecretKey secretKey = getSecretKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedValue = cipher.doFinal(value.getBytes());
        String encodedValue = Base64.getEncoder().encodeToString(encryptedValue);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(key + ".txt"))) {
            writer.write(encodedValue);
        }
    }

    public String retrieve(String key) throws Exception {
        SecretKey secretKey = getSecretKey();
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        String encodedValue;
        try (BufferedReader reader = new BufferedReader(new FileReader(key + ".txt"))) {
            encodedValue = reader.readLine();
        }
        byte[] decryptedValue = cipher.doFinal(Base64.getDecoder().decode(encodedValue));
        return new String(decryptedValue);
    }

    private SecretKey getSecretKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
        try (FileInputStream fis = new FileInputStream(KEYSTORE_PATH)) {
            keyStore.load(fis, keystorePassword.toCharArray());
        }
        return (SecretKey) keyStore.getKey(KEY_ALIAS, keystorePassword.toCharArray());
    }
}