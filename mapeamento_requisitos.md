# 📖 Mapeamento de Requisitos - Sistema de Leilão

Este documento vincula as exigências do professor ao código implementado, facilitando a defesa técnica do projeto.

---

### 1. Requisitos Técnicos de Infraestrutura

#### 📡 Comunicação via Sockets TCP/UDP
*   **Onde está:** `src/server/AuctionServer.java` e `src/client/AuctionClient.java`.
*   **Código:**
    ```java
    // Servidor utilizando SSL sobre TCP (AuctionServer.java:54)
    try (SSLServerSocket serverSocket = (SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket(PORT))

    // Cliente conectando ao servidor (AuctionClient.java:12)
    try (SSLSocket socket = (SSLSocket) SSLSocketFactory.getDefault().createSocket("127.0.0.1", 12345))
    ```

#### 🧵 Gerenciamento de Threads para Conexões Concorrentes
*   **Onde está:** `src/server/AuctionServer.java`.
*   **Código:**
    ```java
    // Aceita múltiplos clientes e cria uma thread para cada (AuctionServer.java:60-63)
    SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
    ClientHandler handler = new ClientHandler(clientSocket, this);
    clientes.add(handler);
    new Thread(handler).start();
    ```

---

### 2. Funcionalidades do Servidor

#### a) Cadastrar Item de Leilão
*   **Onde está:** `src/server/AuctionServer.java`.
*   **Código:**
    ```java
    // Cadastro dinâmico via console (AuctionServer.java:28-34)
    System.out.print("Digite o nome do item para leilão: ");
    String item = sc.nextLine();
    System.out.print("Digite o preço inicial: ");
    double preco = Double.parseDouble(sc.nextLine());
    state = new AuctionState(item, preco);
    ```

#### b) Receber e Armazenar os Lances (Identificando o Autor)
*   **Onde está:** `src/server/ClientHandler.java`.
*   **Código:**
    ```java
    // Identifica o usuário e processa o valor (ClientHandler.java:83-95)
    private void processarLance(String valorStr) {
        double v = Double.parseDouble(valorStr);
        if (server.getState().processarLance(v, usuario)) { // "usuario" identifica o autor
            // ...
        }
    }
    ```

#### i. Informar aos compradores o lance atual (Quem enviou e o Valor)
*   **Onde está:** `src/server/ClientHandler.java`.
*   **Código:**
    ```java
    // Broadcast para informar todos os conectados (ClientHandler.java:88)
    server.broadcast(Protocolo.NOVO_LANCE + usuario + ":" + v);
    ```

#### c) Encerrar o Leilão do Item
*   **Onde está:** `src/server/AuctionServer.java`.
*   **Código:**
    ```java
    // Comando administrativo para fechar o leilão (AuctionServer.java:72-84)
    private void finalizar() {
        state.encerrarLeilao();
        String msg = Protocolo.FIM_LEILAO + state.getVencedorAtual() + ":" + state.getMaiorLance();
        broadcast(msg);
        // ...
    }
    ```

#### ✅ Validar Lances (Deve ser maior que o anterior)
*   **Onde está:** `src/server/AuctionState.java`.
*   **Código:**
    ```java
    // Regra de negócio do lance (AuctionState.java:27)
    if (valor > maiorLance && leilaoAtivo) {
        maiorLance = valor;
        // ...
    }
    ```

---

### 3. Funcionalidades dos Clientes

#### 💻 Interface para enviar lances e Painel em Tempo Real
*   **Onde está:** `src/client/AuctionClient.java` (Envio) e `src/client/ServerListener.java` (Painel).
*   **Código:**
    ```java
    // Envio de lance (AuctionClient.java:27)
    if (!cmd.isEmpty()) out.println(Protocolo.LANCE + cmd);

    // Painel de Monitoramento Real-time (ServerListener.java:14-34)
    while ((msg = in.readLine()) != null) {
        // Exibe lances, status e erros conforme chegam do servidor
    }
    ```

#### 👤 Cadastro Dinâmico de Usuários
*   **Onde está:** `src/client/AuctionClient.java` e `src/security/SecurityHelper.java`.
*   **Código:**
    ```java
    // Cliente envia requisição de cadastro (AuctionClient.java:52)
    out.println(Protocolo.CADASTRO + u + ":" + p);

    // Servidor valida e persiste o novo usuário (SecurityHelper.java:86-93)
    public static synchronized boolean cadastrar(String usuario, String senha) {
        if (USUARIOS.containsKey(usuario)) return false;
        USUARIOS.put(usuario, senha);
        salvarTodosUsuarios(); // Persiste em resources/usuarios.json
        return true;
    }
    ```

---

### 4. Persistência e Segurança

#### 💾 Registro Histórico (Arquivo)
*   **Onde está:** `src/server/persistence/LogService.java`.
*   **Código:**
    ```java
    // Gravação em arquivo JSON (LogService.java:26-28)
    try (FileWriter fw = new FileWriter(LOG_PATH, true); PrintWriter pw = new PrintWriter(fw)) {
        pw.println(entry);
    }
    ```

#### 🔒 Segurança (Bônus: Autenticação e Criptografia)
*   **Onde está:** `src/security/SecurityHelper.java`.
*   **Código:**
    ```java
    // Configuração SSL do servidor (SecurityHelper.java:71-73)
    System.setProperty("javax.net.ssl.keyStore", "resources/keystore.p12");
    System.setProperty("javax.net.ssl.keyStorePassword", "123456");
    System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");

    // Autenticação (SecurityHelper.java:82-84)
    public static boolean autenticar(String usuario, String senha) {
        return USUARIOS.containsKey(usuario) && USUARIOS.get(usuario).equals(senha);
    }
    ```
