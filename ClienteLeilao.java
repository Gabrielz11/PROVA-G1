import javax.net.ssl.*;
import java.io.*;
import java.util.Scanner;

public class ClienteLeilao {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore", "keystore.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket(SERVER_IP, SERVER_PORT)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("--- CONECTADO AO LEILÃO SEGURO ---");

            // Autenticação
            String serverMsg = in.readLine();
            if ("AUTH_REQ".equals(serverMsg)) {
                System.out.print("Usuário: ");
                String user = scanner.nextLine();
                System.out.print("Senha: ");
                String pass = scanner.nextLine();
                out.println(user + ":" + pass);
                
                String status = in.readLine();
                if (!"AUTH_SUCCESS".equals(status)) {
                    System.out.println("Login falhou.");
                    return;
                }
                System.out.println("Autenticado como: " + user + "\n");
            }

            // Thread para receber atualizações do servidor
            new Thread(() -> {
                try {
                    String update;
                    while ((update = in.readLine()) != null) {
                        if (update.startsWith("NOVO_LANCE:")) {
                            String[] partes = update.split(":");
                            System.out.println("\n[MURAL] " + partes[1] + " deu um lance de R$ " + partes[2]);
                        } else if (update.startsWith("STATUS:")) {
                            String[] partes = update.split(":");
                            System.out.println("Informação: Item por R$ " + partes[1] + " (" + partes[2] + ")");
                        } else if (update.startsWith("ERRO:")) {
                            System.out.println("\n[AVISO] " + update);
                        } else {
                            System.out.println("\n[SERVIDOR] " + update);
                        }
                        System.out.print("Digite seu lance (ou 'sair'): ");
                    }
                } catch (IOException e) {
                    System.out.println("\nDesconectado do servidor.");
                }
            }).start();

            // Loop de lances
            System.out.print("Digite seu lance (ou 'sair'): ");
            while (scanner.hasNextLine()) {
                String lance = scanner.nextLine();
                if (lance.equalsIgnoreCase("sair")) break;
                if (!lance.isEmpty()) {
                    out.println("LANCE:" + lance);
                }
            }

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            System.err.println("Dica: O servidor está rodando? O arquivo 'keystore.p12' está na pasta?");
        }
    }
}
