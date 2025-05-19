
package com.example.keychainapp.service;

import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


public class Email {
    private static final String API_URL = "https://psimobi.fly.dev/api/email";

    // Modelo de dados para o e-mail
    public static class EmailData {
        public String to;
        public String subject;
        public String body;

        public EmailData(String to, String subject, String body) {
            this.to = to;
            this.subject = subject;
            this.body = body;
        }
    }

    /**
     * Envia um e-mail via POST para a API.
     * @param emailData JSON do e-mail (ex: {"to":"...","subject":"...","body":"..."})
     * @return resposta da API como String
     * @throws IOException se houver erro de rede
     */
    public static String sendEmail(String emailDataJson) throws IOException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
            writer.write(emailDataJson);
            writer.flush();
            writer.close();

            int responseCode = conn.getResponseCode();
            InputStream is = (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}

