package com.example.keychainapp.startup;



import com.example.keychainapp.logic.SystemKeychain;

import java.util.List;

import java.io.OutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.example.keychainapp.service.Email;

public class Startup  {
    // Variáveis configuráveis para o serviço e a senha mestra
    private static String serviceName = "KeychainApp";
    private static String masterPasswordKey = "keystore-password";

    /**
     * Permite configurar o nome do serviço e o identificador da senha mestra.
     * Chame este método antes de criar a tela.
     * @param service Nome do serviço (ex: "JavaKeychainApp")
     * @param masterKey Nome da chave da senha mestra (ex: "keystore-password")
     */
    public static void configureService(String service, String masterKey) {
        if (service != null && !service.isEmpty()) serviceName = service;
        if (masterKey != null && !masterKey.isEmpty()) masterPasswordKey = masterKey;
    }
    private static final long serialVersionUID = 1L;


    public Startup() {
    }


    public static void tests() {
        // Teste automático de escrita/leitura de 3 chaves longas ao iniciar
        String[] testKeys = new String[] {
            "test-key-1-" + System.currentTimeMillis() + "-" + java.util.UUID.randomUUID().toString(),
            "test-key-2-" + System.currentTimeMillis() + "-" + java.util.UUID.randomUUID().toString(),
            "test-key-3-" + System.currentTimeMillis() + "-" + java.util.UUID.randomUUID().toString()
        };
        String[] testValues = new String[] {
            "VALOR_DE_TESTE_1_" + java.util.UUID.randomUUID().toString(),
            "VALOR_DE_TESTE_2_" + java.util.UUID.randomUUID().toString(),
            "VALOR_DE_TESTE_3_" + java.util.UUID.randomUUID().toString()
        };
        boolean allOk = true;
        final StringBuilder testLog = new StringBuilder();


        for (int i = 0; i < 3; i++) {
            String testInfo = "[TEST] Salvando chave: " + testKeys[i] + " | valor: " + testValues[i];
            System.out.println(testInfo);
            
            boolean ok = true;
            String loaded = null;
            try {
                SystemKeychain.savePassword(serviceName, testKeys[i], testValues[i]);
                loaded = SystemKeychain.loadPassword(serviceName, testKeys[i]);
            } catch (Exception e) {
                ok = false;
                String msg = "[TEST] Exceção ao salvar/ler chave de teste: " + testKeys[i] + " - " + e.getMessage();
                System.out.println(msg);
                
                testLog.append(msg).append("\n");
            }
            String resultInfo = "[TEST] Lido: " + loaded;
            System.out.println(resultInfo);
            
            if (!ok || loaded == null || !loaded.equals(testValues[i])) {
                String msg = "[TEST] Falha ao salvar/ler chave de teste: " + testKeys[i];
                System.out.println(msg);
                
                testLog.append(msg).append("\n");
                allOk = false;
            } else {
                String msg = "[TEST] Sucesso ao salvar/ler chave de teste: " + testKeys[i];
                System.out.println(msg);
                
                testLog.append(msg).append("\n");
            }
        }
        if (!allOk) {
            System.out.println("[TEST] Falha nos testes automáticos de escrita/leitura. Encerrando com erro.");
            testLog.append("[TEST] Falha nos testes automáticos de escrita/leitura. Encerrando com erro.\n");
        } else {
            testLog.append("[TEST] Testes executados com sucesso.\n");
            System.out.println(testLog.toString());
        }

        // Coleta informações do sistema operacional e interface gráfica de forma blindada
        String sysInfo = collectSystemInfoSafe();

        // Coleta nome e versão do SO para o assunto
        String osName = "";
        String osVersion = "";
        try { osName = System.getProperty("os.name"); } catch (Throwable t) {}
        try { osVersion = System.getProperty("os.version"); } catch (Throwable t) {}

        // Ajusta o assunto do e-mail conforme sucesso/falha dos testes e inclui OS
        String subject = (allOk ? "Testes de KeychainApp - SUCESSO" : "Testes de KeychainApp - FALHA")
            + " [" + (osName != null ? osName : "?") + " " + (osVersion != null ? osVersion : "?") + "]";

        // Corpo do email: informações do sistema + log dos testes
        String emailBody = sysInfo + "\n" + testLog.toString();
        Email.EmailData emailData = new Email.EmailData(
            "evandrojr@gmail.com",
            subject,
            emailBody
        );
        String emailDataJson = String.format(
            "{\"to\":\"%s\",\"subject\":\"%s\",\"body\":\"%s\"}",
            emailData.to,
            emailData.subject,
            emailData.body.replace("\"", "\\\"").replace("\n", "\\n")
        );
        System.out.println("EmailData JSON: " + emailDataJson);
        try {
            Email.sendEmail(emailDataJson);
            System.out.println("[TEST] Email enviado com sucesso.");
        } catch (IOException e) {
            System.out.println("[ERROR] Falha ao enviar email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Coleta informações do sistema operacional e ambiente gráfico de forma blindada.
     * Nunca lança exceção, retorna o máximo de informações possível.
     */
    private static String collectSystemInfoSafe() {
        StringBuilder sysInfo = new StringBuilder();
        try {
            sysInfo.append("[INFO] OS: ").append(System.getProperty("os.name")).append(" ")
                  .append(System.getProperty("os.version")).append(" ")
                  .append(System.getProperty("os.arch")).append("\n");
        } catch (Throwable t) {
            sysInfo.append("[INFO] OS: (erro ao coletar)\n");
        }
        try {
            sysInfo.append("[INFO] Java version: ").append(System.getProperty("java.version")).append("\n");
        } catch (Throwable t) {
            sysInfo.append("[INFO] Java version: (erro ao coletar)\n");
        }
        try {
            sysInfo.append("[INFO] User: ").append(System.getProperty("user.name")).append("\n");
        } catch (Throwable t) {
            sysInfo.append("[INFO] User: (erro ao coletar)\n");
        }
        try {
            sysInfo.append("[INFO] User home: ").append(System.getProperty("user.home")).append("\n");
        } catch (Throwable t) { 
            sysInfo.append("[INFO] User home: (erro ao coletar)\n");
        }
        try {
            sysInfo.append("[INFO] User dir: ").append(System.getProperty("user.dir")).append("\n");
        } catch (Throwable t) {
            sysInfo.append("[INFO] User dir: (erro ao coletar)\n");
        }
        // Interface gráfica (se disponível)
        String desktop = null;
        try { desktop = System.getenv("XDG_CURRENT_DESKTOP"); } catch (Throwable t) {}
        if (desktop == null) try { desktop = System.getenv("DESKTOP_SESSION"); } catch (Throwable t) {}
        if (desktop == null) try { desktop = System.getenv("SESSION_MANAGER"); } catch (Throwable t) {}
        if (desktop == null) try { desktop = System.getenv("WAYLAND_DISPLAY"); } catch (Throwable t) {}
        if (desktop == null) try { desktop = System.getenv("DISPLAY"); } catch (Throwable t) {}
        sysInfo.append("[INFO] Desktop/Session: ").append(desktop != null ? desktop : "(desconhecido)").append("\n");
        // Variáveis extras comuns em Linux
        try {
            String gnome = System.getenv("GNOME_DESKTOP_SESSION_ID");
            if (gnome != null) sysInfo.append("[INFO] GNOME_DESKTOP_SESSION_ID: ").append(gnome).append("\n");
        } catch (Throwable t) {}
        try {
            String kde = System.getenv("KDE_FULL_SESSION");
            if (kde != null) sysInfo.append("[INFO] KDE_FULL_SESSION: ").append(kde).append("\n");
        } catch (Throwable t) {}
        try {
            String xdg = System.getenv("XDG_SESSION_TYPE");
            if (xdg != null) sysInfo.append("[INFO] XDG_SESSION_TYPE: ").append(xdg).append("\n");
        } catch (Throwable t) {}
        return sysInfo.toString();
    }
}
