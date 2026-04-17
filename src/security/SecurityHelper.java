package security;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsável por gerenciar as configurações de SSL e a base de usuários.
 * Agora utiliza caminhos relativos à pasta de resources.
 */
public class SecurityHelper {
    private static final Map<String, String> USUARIOS = new HashMap<>();

    static {
        USUARIOS.put("gabriel", "123");
        USUARIOS.put("admin", "admin");
        USUARIOS.put("comprador1", "senha123");
        USUARIOS.put("riquelmmy", "123");
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
}
