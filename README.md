# 🔨 Sistema de Leilão Eletrônico Distribuído (Prova G1)

Plataforma distribuída de leilões eletrônicos desenvolvida para a disciplina de **Sistemas Paralelos e Distribuídos**. O sistema permite que múltiplos compradores participem de um leilão em tempo real com coordenação centralizada e comunicação segura.

## 🚀 Requisitos Atendidos

### 🔧 Técnicos
- **Comunicação:** Sockets TCP com suporte a broadcast.
- **Concorrência:** Gerenciamento de múltiplas conexões via **Multithreading** (Thread Pool).
- **Segurança (Bônus):** Criptografia total via **SSL/TLS** e sistema de **Autenticação**.
- **Persistência:** Histórico completo salvo em `logs/history.json`.

### 📱 Funcionalidades
- **Cadastro Dinâmico:** O servidor permite configurar o item e o preço base no momento da inicialização.
- **Validação de Lances:** Somente lances maiores que o atual são aceitos.
- **Monitoramento Real-time:** Todos os participantes recebem notificações instantâneas de novos lances e status do leilão.
- **Encerramento Controlado:** Comando administrativo para finalizar o leilão e declarar o vencedor.

---

## 🛠️ Como Executar

### 1. Pré-requisitos
- Java JDK 8 ou superior instalado.
- Certificados `keystore.p12` e `truststore.p12` na pasta `resources/`.

### 2. Compilação
Abra o terminal na raiz do projeto e execute:
```bash
javac -d . src/shared/*.java src/security/*.java src/server/*.java src/server/persistence/*.java src/client/*.java
```

### 3. Execução do Servidor
```bash
java server.AuctionServer
```
1. No início, digite o **Nome do Item** (ex: Notebook).
2. Digite o **Preço Inicial** (ex: 1000).
3. O servidor passará a aguardar conexões.
4. Digite `fim` no console do servidor para encerrar o leilão a qualquer momento.

### 4. Execução do Cliente
Abra novos terminais e execute:
```bash
java client.AuctionClient
```
- **Login:** Use `gabriel` | `123` ou `admin` | `admin`.
- **Lances:** Digite apenas o valor numérico e aperte Enter.

---

## 🔒 Dados de Teste
| Usuário | Senha |
| :--- | :--- |
| `gabriel` | `123` |
| `admin` | `admin` |
| `comprador1` | `senha123` |

---

## 📂 Estrutura do Projeto
- `src/server/`: Lógica central, estado do leilão e handlers de clientes.
- `src/client/`: Interface do comprador e listener de notificações.
- `src/shared/`: Protocolo de comunicação e constantes.
- `src/security/`: Configurações de SSL e Autenticação.
- `resources/`: Certificados de segurança.
- `logs/`: Persistência histórica.

---

## 👥 Autores
- **Gabriel Aires** - [Gabrielz11](https://github.com/Gabrielz11)
- **Riquelmmy Pedrosa** - [Riquelmmy](https://github.com/Riquelmmy)
