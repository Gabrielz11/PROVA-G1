package client;

import shared.Protocolo;
import java.io.*;

public class ServerListener implements Runnable {
    private BufferedReader in;
    public ServerListener(BufferedReader in) { this.in = in; }

    @Override
    public void run() {
        try {
            String msg;
            while ((msg = in.readLine()) != null) {
                if (msg.startsWith(Protocolo.NOVO_LANCE)) {
                    String[] p = msg.split(":");
                    System.out.println("\n[MURAL] " + p[1] + " deu um lance de: R$ " + p[2]);
                } else if (msg.startsWith(Protocolo.STATUS)) {
                    String[] p = msg.split(":");
                    System.out.println("Item: " + (p.length > 3 ? p[3] : "Leilão") + " | Lance Atual: R$ " + p[1] + " (" + p[2] + ")");
                } else if (msg.startsWith(Protocolo.FIM_LEILAO)) {
                    String[] p = msg.split(":");
                    System.out.println("\n***********************************");
                    System.out.println("      LEILÃO ENCERRADO!");
                    System.out.println("VENCEDOR: " + p[1]);
                    System.out.println("VALOR PAGO: R$ " + p[2]);
                    System.out.println("***********************************");
                    System.out.println("Pressione qualquer tecla para sair...");
                    break;
                } else if (msg.startsWith(Protocolo.ERRO)) {
                    System.out.println("\n[!] " + msg.substring(Protocolo.ERRO.length()));
                } else {
                    System.out.println("\n[SISTEMA] " + msg);
                }
                System.out.print("Seu Lance: ");
            }
        } catch (IOException e) {
            System.out.println("\nConexão com o servidor encerrada.");
        }
    }
}
