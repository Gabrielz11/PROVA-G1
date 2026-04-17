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
                    System.out.println("\n[MURAL] " + p[1] + " lance: R$ " + p[2]);
                } else if (msg.startsWith(Protocolo.STATUS)) {
                    String[] p = msg.split(":");
                    System.out.println("Status: R$ " + p[1] + " (" + p[2] + ")");
                } else if (msg.startsWith(Protocolo.ERRO)) {
                    System.out.println("\n[!] " + msg.substring(Protocolo.ERRO.length()));
                } else {
                    System.out.println("\n[SISTEMA] " + msg);
                }
                System.out.print("Lance: ");
            }
        } catch (IOException e) {
            System.out.println("Fim da conexão.");
        }
    }
}
