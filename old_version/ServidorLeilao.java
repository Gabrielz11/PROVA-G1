import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.*;
import java.util.concurrent.*;
import java.text.SimpleDateFormat;

public class ServidorLeilao {
    private static final int PORT = 12345;
    private static final String LOG_FILE = "historico_leilao.json";
    private static final Map<String, String> USUARIOS = new HashMap<>();
    private static final List<ClientHandler> clientes = new CopyOnWriteArrayList<>();
    
    // Estado do Leilão
    private static String itemLeilao = "Notebook Gamer Pro";
    private static double maiorLance = 1000.0;
    private static String vencedorAtual = "Nenhum";
    private static boolean leilaoAtivo = true;

    static {
        // Simulação de banco de dados de usuários (BÔNUS: Autenticação)
        USUARIOS.put("gabriel", "123");
        USUARIOS.put("admin", "admin");
        USUARIOS.put("comprador1", "senha123");
    }

    public static void main(String[] args) {
        configurarSSL();
        
        try (SSLServerSocket serverSocket = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(PORT)) {
            System.out.println("--- SERVIDOR DE LEILÃO INICIADO (SSL ATIVO) ---");
            System.out.println("Item: " + itemLeilao + " | Lance Inicial: R$ " + maiorLance);
            registrarNoHistorico("INICIO_LEILAO", null, maiorLance);

            while (leilaoAtivo) {
                try {
                    SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientSocket);
                    clientes.add(handler);
                    new Thread(handler).start();
                } catch (IOException e) {
                    if (leilaoAtivo) System.err.println("Erro ao aceitar conexão: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erro fatal no servidor: " + e.getMessage());
            System.err.println("Certifique-se de que o arquivo 'keystore.p12' existe na mesma pasta.");
        }
    }

    private static class ClientHandler implements Runnable {
        private SSLSocket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String usuarioAutenticado;

        public ClientHandler(SSLSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Autenticação
                if (!autenticar()) {
                    socket.close();
                    return;
                }

                enviarMensagem("BEM-VINDO ao leilão do item: " + itemLeilao);
                enviarMensagem("STATUS:" + maiorLance + ":" + vencedorAtual);

                String input;
                while ((input = in.readLine()) != null) {
                    if (input.startsWith("LANCE:")) {
                        processarLance(input.split(":")[1]);
                    } else if (input.equals("SAIR")) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Cliente " + (usuarioAutenticado != null ? usuarioAutenticado : "desconhecido") + " desconectado.");
            } finally {
                clientes.remove(this);
                try { socket.close(); } catch (IOException e) {}
            }
        }

        private boolean autenticar() throws IOException {
            out.println("AUTH_REQ");
            String credenciais = in.readLine();
            if (credenciais == null) return false;
            
            String[] partes = credenciais.split(":");
            if (partes.length == 2 && USUARIOS.containsKey(partes[0]) && USUARIOS.get(partes[0]).equals(partes[1])) {
                this.usuarioAutenticado = partes[0];
                out.println("AUTH_SUCCESS");
                System.out.println("Usuário conectado: " + usuarioAutenticado);
                return true;
            } else {
                out.println("AUTH_FAIL");
                return false;
            }
        }

        private synchronized void processarLance(String valorStr) {
            try {
                double valor = Double.parseDouble(valorStr);
                if (valor > maiorLance) {
                    maiorLance = valor;
                    vencedorAtual = usuarioAutenticado;
                    String msg = "NOVO_LANCE:" + vencedorAtual + ":" + maiorLance;
                    broadcast(msg);
                    registrarNoHistorico("NOVO_LANCE", vencedorAtual, maiorLance);
                    System.out.println("Lance: R$ " + maiorLance + " por " + vencedorAtual);
                } else {
                    out.println("ERRO: O lance deve ser maior que R$ " + maiorLance);
                }
            } catch (NumberFormatException e) {
                out.println("ERRO: Valor inválido.");
            }
        }

        public void enviarMensagem(String msg) {
            out.println(msg);
        }
    }

    private static void broadcast(String msg) {
        for (ClientHandler cliente : clientes) {
            cliente.enviarMensagem(msg);
        }
    }

    private static void registrarNoHistorico(String evento, String usuario, Double valor) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        
        // Formantando os dados como JSON
        String jsonEntry = String.format("{\"timestamp\": \"%s\", \"evento\": \"%s\", \"usuario\": \"%s\", \"valor\": %s}",
                timestamp,
                evento,
                usuario != null ? usuario : "SISTEMA",
                valor != null ? valor.toString() : "null");

        try (FileWriter fw = new FileWriter(LOG_FILE, true); PrintWriter pw = new PrintWriter(fw)) {
            pw.println(jsonEntry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void configurarSSL() {
        System.setProperty("javax.net.ssl.keyStore", "keystore.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
    }
}
