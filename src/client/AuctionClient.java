package client;

import security.SecurityHelper;
import shared.Protocolo;
import javax.net.ssl.*;
import java.io.*;
import java.util.Scanner;

public class AuctionClient {
    public static void main(String[] args) {
        SecurityHelper.configurarSSLCliente();
        try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket("0.0.0.0", 12345)) {
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
                    if (!cmd.isEmpty())
                        out.println(Protocolo.LANCE + cmd);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro de conexão: " + e.getMessage());
        }
    }

    private static boolean realizarLogin(BufferedReader in, PrintWriter out, Scanner sc) throws IOException {
        while (true) {
            String serverReq = in.readLine();
            if (serverReq == null || !serverReq.equals(Protocolo.AUTH_REQ))
                return false;

            System.out.println("\n--- AUTENTICAÇÃO ---");
            System.out.println("1 - Entrar (Login)");
            System.out.println("2 - Criar Conta (Cadastro)");
            System.out.print("Escolha uma opção: ");
            String opcao = sc.nextLine();

            System.out.print("Usuário: ");
            String u = sc.nextLine();
            System.out.print("Senha: ");
            String p = sc.nextLine();

            if (opcao.equals("2")) {
                out.println(Protocolo.CADASTRO + u + ":" + p);
            } else {
                out.println(Protocolo.LOGIN + u + ":" + p);
            }

            String resultado = in.readLine();
            if (Protocolo.AUTH_SUCCESS.equals(resultado)) {
                System.out.println("[SISTEMA] Autenticado com sucesso!");
                return true;
            } else {
                System.out.println("[ERRO] Falha na autenticação/cadastro. Tente novamente.");
            }
        }
    }
}
