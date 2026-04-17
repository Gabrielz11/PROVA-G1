package client;

import security.SecurityHelper;
import shared.Protocolo;
import javax.net.ssl.*;
import java.io.*;
import java.util.Scanner;

public class AuctionClient {
    public static void main(String[] args) {
        SecurityHelper.configurarSSLCliente();
        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket("127.0.0.1", 12345)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);

            System.out.println("--- LEILÃO DISTRIBUÍDO INICIADO ---");

            if (realizarLogin(in, out, scanner)) {
                new Thread(new ServerListener(in)).start();
                while (scanner.hasNextLine()) {
                    String cmd = scanner.nextLine();
                    if (cmd.equalsIgnoreCase("sair")) {
                        out.println(Protocolo.SAIR);
                        break;
                    }
                    if (!cmd.isEmpty()) out.println(Protocolo.LANCE + cmd);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro de conexão: " + e.getMessage());
        }
    }

    private static boolean realizarLogin(BufferedReader in, PrintWriter out, Scanner sc) throws IOException {
        if (Protocolo.AUTH_REQ.equals(in.readLine())) {
            System.out.print("Usuário: "); String u = sc.nextLine();
            System.out.print("Senha: "); String p = sc.nextLine();
            out.println(u + ":" + p);
            return Protocolo.AUTH_SUCCESS.equals(in.readLine());
        }
        return false;
    }
}
