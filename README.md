# 🔨 Sistema de Leilão Eletrônico Distribuído — Prova G1

Plataforma distribuída de leilões eletrônicos desenvolvida para a disciplina de **Sistemas Paralelos e Distribuídos (SPD)**. Permite que múltiplos compradores participem de um leilão em tempo real, com coordenação centralizada, comunicação segura via SSL/TLS e autenticação de usuários.

---

## ✅ Pré-requisitos

- **Java JDK 8** ou superior instalado
- Terminal aberto na **raiz do projeto** (`PROVA G1 SPD/`)

---

## 🚀 Como Executar (Passo a Passo)

### 1. Compilar o projeto

Execute **uma única vez** para gerar os `.class`:

```bash
javac -d . src/shared/Protocolo.java src/security/SecurityHelper.java src/server/persistence/LogService.java src/server/AuctionState.java src/server/ClientHandler.java src/server/AuctionServer.java src/client/ServerListener.java src/client/AuctionClient.java
```

---

### 2. Iniciar o Servidor

**Abra um terminal** e rode:

```bash
java server.AuctionServer
```

O servidor vai pedir:
```
--- CONFIGURAÇÃO DO LEILÃO ---
Digite o nome do item para leilão: Notebook
Digite o preço inicial: 1000
```

Após isso, aguarda conexões de clientes. Para encerrar o leilão, digite `fim` e pressione Enter.

---

### 3. Conectar um Cliente

**Abra outro terminal** (pode abrir quantos quiser para simular vários compradores):

```bash
java client.AuctionClient
```

O fluxo de login será exibido:
```
Escolha uma opção:
1 - Entrar (Login)
2 - Criar Conta (Cadastro)
> 1
Usuário: gabriel
Senha: 123
[SISTEMA] Autenticado com sucesso!
```

Para dar um lance, basta digitar o **valor numérico** e pressionar Enter:
```
Seu Lance: 1500
```

Para sair do cliente, digite `sair`.

---

## 🔐 Usuários de Teste

| Usuário | Senha |
| :--- | :--- |
| `gabriel` | `123` |
| `admin` | `admin` |
| `comprador1` | `senha123` |
| `riquelmmy` | `123` |

> Novos usuários podem ser criados diretamente pelo cliente escolhendo a opção **"2 - Criar Conta"**.

---

## 📂 Estrutura do Projeto

```
PROVA G1 SPD/
├── src/
│   ├── server/
│   │   ├── AuctionServer.java        # Ponto de entrada do servidor
│   │   ├── AuctionState.java         # Estado thread-safe do leilão
│   │   ├── ClientHandler.java        # Gerencia cada cliente conectado
│   │   └── persistence/
│   │       └── LogService.java       # Salva o histórico em JSON
│   ├── client/
│   │   ├── AuctionClient.java        # Ponto de entrada do cliente
│   │   └── ServerListener.java       # Recebe notificações do servidor
│   ├── shared/
│   │   └── Protocolo.java            # Constantes de comunicação cliente-servidor
│   └── security/
│       └── SecurityHelper.java       # Configuração SSL e autenticação
├── resources/
│   ├── keystore.p12                  # Certificado do servidor (SSL)
│   ├── truststore.p12                # Certificado confiável do cliente (SSL)
│   └── usuarios.json                 # Base de usuários cadastrados
└── logs/
    └── history.json                  # Histórico gerado em runtime
```

---

## 📚 Documentação do Projeto

Acesse os arquivos abaixo para entender os detalhes técnicos e as decisões de arquitetura:

- [**📄 Mapeamento de Requisitos**](./mapeamento_requisitos.md): Este arquivo descreve como cada requisito solicitado pelo professor foi implementado no código, citando os arquivos e as linhas exatas.
- [**📄 Justificativa: Java vs Python**](./Python_x_Java.md): Este documento explica por que optamos por Java em vez de Python, abordando conceitos como Threads Reais do SO vs. GIL do Python.
- [**📄 Enunciado da Prova**](./G1-SPD-2026-1.pdf): Arquivo PDF com as instruções originais da Prova G1.

### ☕ Por que Java?
A utilização do **Java** foi essencial para garantir o **paralelismo real**. Diferente do Python (que é limitado pelo Global Interpreter Lock - GIL), o Java permite o gerenciamento de múltiplas threads nativas do sistema operacional. Isso garante que cada comprador no leilão seja processado de forma independente e simultânea, sem gargalos de execução, atendendo plenamente aos objetivos da disciplina de Sistemas Paralelos e Distribuídos.

---

## 🛠️ Funcionalidades

- **Múltiplos clientes simultâneos** — cada conexão roda em sua própria thread
- **Validação de lances** — somente valores maiores que o lance atual são aceitos
- **Broadcast em tempo real** — todos os participantes recebem notificações instantâneas
- **Autenticação + Cadastro** — sistema de login com persistência em JSON
- **Comunicação segura** — SSL/TLS com certificados PKCS12
- **Persistência de histórico** — todos os lances são gravados em `logs/history.json`
- **Encerramento controlado** — o servidor declara o vencedor e notifica todos os clientes

---

## 👥 Autores

- **Gabriel Aires** — [Gabrielz11](https://github.com/Gabrielz11)
- **Riquelmmy Pedrosa** — [Riquelmmy](https://github.com/Riquelmmy)
