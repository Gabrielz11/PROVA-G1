# 🔨 Sistema de Leilão Eletrônico Distribuído

Trabalho prático desenvolvido para a disciplina de **Sistemas Paralelos e Distribuídos** (Prova G1). O sistema consiste em uma plataforma de leilões eletrônicos onde múltiplos servidores (clientes) coordenam o processo de licitação em tempo real.

## 🚀 Funcionalidades
- **Comunicação Segura:** Implementação de Sockets TCP com camada de criptografia **SSL/TLS**.
- **Multithreading:** Gerenciamento de múltiplas conexões simultâneas via Thread Pool no servidor e monitoramento em tempo real no cliente.
- **Autenticação:** Sistema de login (Usuário/Senha) para participação no leilão.
- **Lances em Tempo Real:** Validação de lances e notificação instantânea (broadcast) para todos os participantes conectados.
- **Persistência:** Registro histórico completo de todas as atividades em arquivo de log.

## 🔒 Nota sobre Segurança e Boas Práticas
> [!IMPORTANT]
> Este repositório contém o arquivo `keystore.p12` (chave privada/certificado). **Subir arquivos de chaves para o GitHub não é uma boa prática de segurança em ambiente profissional.** 
> No entanto, este arquivo foi incluído propositalmente para fins acadêmicos e de avaliação (Prova G1), visando facilitar a execução imediata e a demonstração do conhecimento sobre comunicações criptografadas em Java.

---

## 🛠️ Como Executar

### 1. Pré-requisitos
- Ter o Java JDK instalado (versão 8 ou superior).
- O arquivo `keystore.p12` deve estar na mesma pasta dos arquivos `.java`.

### 2. Compilação
Abra o terminal na pasta do projeto e execute:
```bash
javac ServidorLeilao.java ClienteLeilao.java
```

### 3. Execução do Servidor
Inicie o servidor primeiro para que ele aguarde conexões:
```bash
java ServidorLeilao
```

### 4. Execução do Cliente
Abra novos terminais para cada comprador e execute:
```bash
java ClienteLeilao
```

---

## 🔑 Dados para Teste (Autenticação)
Você pode utilizar as seguintes credenciais pré-cadastradas no código:
- **Usuário:** `gabriel` | **Senha:** `123`
- **Usuário:** `admin` | **Senha:** `admin`
- **Usuário:** `comprador1` | **Senha:** `senha123`

## 📜 Histórico e Logs
Os logs de todos os lances aceitos e conexões são salvos no arquivo `historico_leilao.txt` na raiz do diretório.

---

## 🛠️ Referência: Gerando um novo Certificado
Caso queira gerar um novo certificado `keystore.p12`, o comando utilizado foi:
```bash
keytool -genkeypair -alias leilao -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650 -storepass 123456 -keypass 123456 -dname "CN=Leilao, OU=TI, O=Ulbra, L=Canoas, ST=RS, C=BR" -noprompt
```
