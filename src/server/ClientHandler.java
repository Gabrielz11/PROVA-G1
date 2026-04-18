package server;

import security.SecurityHelper;
import shared.Protocolo;
import javax.net.ssl.SSLSocket;
import java.io.*;

/**
 * Handler reescrito para utilizar o Protocolo compartilhado (Boa Prática).
 */
public class ClientHandler implements Runnable {
    private SSLSocket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String usuario;
    private AuctionServer server;

    public ClientHandler(SSLSocket socket, AuctionServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            if (!tentarAutenticar()) {
                socket.close();
                return;
            }

            enviarMensagem(Protocolo.BEM_VINDO + server.getState().getItemLeilao());
            enviarMensagem(Protocolo.STATUS + server.getState().getMaiorLance() + ":" + server.getState().getVencedorAtual() + ":" + server.getState().getItemLeilao());

            String input;
            while ((input = in.readLine()) != null) {
                if (input.startsWith(Protocolo.LANCE)) {
                    processarLance(input.substring(Protocolo.LANCE.length()));
                } else if (input.equals(Protocolo.SAIR)) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Usuário " + usuario + " saiu.");
        } finally {
            server.removerCliente(this);
            try { socket.close(); } catch (IOException e) {}
        }
    }

    private boolean tentarAutenticar() throws IOException {
        out.println(Protocolo.AUTH_REQ);
        String resp = in.readLine();
        if (resp == null) return false;
        String[] creds = resp.split(":");
        if (creds.length == 2 && SecurityHelper.autenticar(creds[0], creds[1])) {
            this.usuario = creds[0];
            out.println(Protocolo.AUTH_SUCCESS);
            return true;
        }
        out.println(Protocolo.AUTH_FAIL);
        return false;
    }

    private void processarLance(String valorStr) {
        try {
            double v = Double.parseDouble(valorStr);
            if (server.getState().processarLance(v, usuario)) {
                System.out.println("[LANCE] " + usuario + " ofertou R$ " + v);
                server.broadcast(Protocolo.NOVO_LANCE + usuario + ":" + v);
                server.getLogService().registrar("LANCE", usuario, v);
            } else {
                out.println(Protocolo.ERRO + "Lance insuficiente. Atual: " + server.getState().getMaiorLance());
            }
        } catch (NumberFormatException e) {
            out.println(Protocolo.ERRO + "Valor inválido.");
        }
    }

    public void enviarMensagem(String m) { out.println(m); }
}
