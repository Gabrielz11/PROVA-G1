package server;

import security.SecurityHelper;
import server.persistence.LogService;
import shared.Protocolo;
import javax.net.ssl.*;
import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Servidor Central: Coordena as conexões e o estado global.
 */
public class AuctionServer {
    private static final int PORT = 12345;
    
    private final List<ClientHandler> clientes = new CopyOnWriteArrayList<>();
    private final LogService logService = new LogService();
    private final AuctionState state = new AuctionState();

    public static void main(String[] args) {
        new AuctionServer().iniciar();
    }

    public void iniciar() {
        SecurityHelper.configurarSSLServidor();

        try (SSLServerSocket serverSocket = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(PORT)) {
            System.out.println("--- SERVIDOR DE LEILÃO PROFISSIONAL INICIADO ---");
            logService.registrar(Protocolo.BEM_VINDO, "SISTEMA", state.getMaiorLance());

            while (state.isLeilaoAtivo()) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clientes.add(handler);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            System.err.println("Erro Fatal: " + e.getMessage());
        }
    }

    public AuctionState getState() { return state; }
    public LogService getLogService() { return logService; }

    public void broadcast(String msg) {
        for (ClientHandler c : clientes) c.enviarMensagem(msg);
    }

    public void removerCliente(ClientHandler c) { clientes.remove(c); }
}
