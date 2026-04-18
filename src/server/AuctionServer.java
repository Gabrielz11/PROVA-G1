package server;

import security.SecurityHelper;
import server.persistence.LogService;
import shared.Protocolo;
import javax.net.ssl.*;
import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Servidor Central: Coordena as conexões e o estado global.
 */
public class AuctionServer {
    private static final int PORT = 12345;
    
    private final List<ClientHandler> clientes = new CopyOnWriteArrayList<>();
    private final LogService logService = new LogService();
    private AuctionState state;

    public static void main(String[] args) {
        new AuctionServer().iniciar();
    }

    public void iniciar() {
        Scanner sc = new Scanner(System.in);
        System.out.println("--- CONFIGURAÇÃO DO LEILÃO ---");
        System.out.print("Digite o nome do item para leilão: ");
        String item = sc.nextLine();
        System.out.print("Digite o preço inicial: ");
        double preco = Double.parseDouble(sc.nextLine());

        state = new AuctionState(item, preco);
        SecurityHelper.configurarSSLServidor();

        // Thread para comandos do admin no servidor
        new Thread(() -> {
            try {
                while (state.isLeilaoAtivo()) {
                    if (sc.hasNextLine()) {
                        String cmd = sc.nextLine();
                        if (cmd.equalsIgnoreCase("fim")) {
                            finalizar();
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                // Silencioso se o input for fechado
            }
        }).start();

        try (SSLServerSocket serverSocket = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(PORT)) {
            System.out.println("\n--- SERVIDOR INICIADO: " + item + " (R$ " + preco + ") ---");
            System.out.println("Digite 'fim' a qualquer momento para encerrar o leilão.\n");
            logService.registrar(Protocolo.BEM_VINDO, "SISTEMA", state.getMaiorLance());

            while (state.isLeilaoAtivo()) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                ClientHandler handler = new ClientHandler(clientSocket, this);
                clientes.add(handler);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            if (state.isLeilaoAtivo()) {
                System.err.println("Erro Fatal: " + e.getMessage());
            }
        }
    }

    private void finalizar() {
        state.encerrarLeilao();
        String msg = Protocolo.FIM_LEILAO + state.getVencedorAtual() + ":" + state.getMaiorLance();
        broadcast(msg);
        logService.registrar("FINALIZADO", state.getVencedorAtual(), state.getMaiorLance());
        System.out.println("\n--- LEILÃO ENCERRADO ---");
        System.out.println("Vencedor: " + state.getVencedorAtual());
        System.out.println("Valor Final: R$ " + state.getMaiorLance());
        
        // Pequeno delay para garantir que a mensagem foi enviada antes de fechar tudo
        try { Thread.sleep(2000); } catch (InterruptedException e) {}
        System.exit(0);
    }

    public AuctionState getState() { return state; }
    public LogService getLogService() { return logService; }

    public void broadcast(String msg) {
        for (ClientHandler c : clientes) c.enviarMensagem(msg);
    }

    public void removerCliente(ClientHandler c) { clientes.remove(c); }
}
