package security;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsável por gerenciar as configurações de SSL e a base de usuários.
 * Agora utiliza caminhos relativos à pasta de resources.
 */
public class SecurityHelper {
    private static final String USUARIOS_FILE = "resources/usuarios.json";
    private static final Map<String, String> USUARIOS = new HashMap<>();

    static {
        carregarUsuarios();
    }

    private static void carregarUsuarios() {
        File file = new File(USUARIOS_FILE);
        if (!file.exists()) {
            USUARIOS.put("gabriel", "123");
            USUARIOS.put("admin", "admin");
            USUARIOS.put("comprador1", "senha123");
            USUARIOS.put("riquelmmy", "123");
            salvarTodosUsuarios();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) content.append(line);
            
            String json = content.toString().trim();
            if (json.startsWith("{") && json.endsWith("}")) {
                json = json.substring(1, json.length() - 1); // Remove { }
                String[] pairs = json.split(",");
                for (String pair : pairs) {
                    String[] kv = pair.split(":");
                    if (kv.length == 2) {
                        String k = kv[0].trim().replace("\"", "");
                        String v = kv[1].trim().replace("\"", "");
                        USUARIOS.put(k, v);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar usuários JSON: " + e.getMessage());
        }
    }

    private static void salvarTodosUsuarios() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USUARIOS_FILE))) {
            writer.println("{");
            Object[] keys = USUARIOS.keySet().toArray();
            for (int i = 0; i < keys.length; i++) {
                String k = (String) keys[i];
                String v = USUARIOS.get(k);
                String line = "  \"" + k + "\": \"" + v + "\"";
                if (i < keys.length - 1) line += ",";
                writer.println(line);
            }
            writer.println("}");
        } catch (IOException e) {
            System.err.println("Erro ao salvar usuários JSON: " + e.getMessage());
        }
    }

    public static void configurarSSLServidor() {
        System.setProperty("javax.net.ssl.keyStore", "resources/keystore.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
    }

    public static void configurarSSLCliente() {
        System.setProperty("javax.net.ssl.trustStore", "resources/truststore.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
    }

    public static boolean autenticar(String usuario, String senha) {
        return USUARIOS.containsKey(usuario) && USUARIOS.get(usuario).equals(senha);
    }

    public static synchronized boolean cadastrar(String usuario, String senha) {
        if (USUARIOS.containsKey(usuario)) {
            return false;
        }
        USUARIOS.put(usuario, senha);
        salvarTodosUsuarios(); // Sobrescreve o JSON com o novo usuário
        return true;
    }
}
