package server.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Responsável pela persistência histórica em formato JSON.
 */
public class LogService {
    private static final String LOG_PATH = "logs/history.json";

    public LogService() {
        File logsDir = new File("logs");
        if (!logsDir.exists()) logsDir.mkdir();
    }

    public synchronized void registrar(String evento, String usuario, Double valor) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String entry = String.format("{\"timestamp\": \"%s\", \"evento\": \"%s\", \"usuario\": \"%s\", \"valor\": %s}",
                timestamp, evento, usuario != null ? usuario : "SISTEMA", valor != null ? valor : 0.0);

        try (FileWriter fw = new FileWriter(LOG_PATH, true); PrintWriter pw = new PrintWriter(fw)) {
            pw.println(entry);
        } catch (IOException e) {
            System.err.println("Erro crítico ao gravar log: " + e.getMessage());
        }
    }
}
