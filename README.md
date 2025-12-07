# **Zarpar - Plataforma de Turismo**

Bem-vindo ao **Zarpar**, uma aplicação Fullstack robusta para gestão, avaliação e descoberta de pontos turísticos. O projeto utiliza uma arquitetura de **Persistência Poliglota**, extraindo o melhor desempenho de diferentes tecnologias de banco de dados.

## **Sobre o Projeto**

O Zarpar permite que viajantes descubram destinos, vejam fotos, leiam avaliações e compartilhem suas próprias experiências.

- **Frontend:** Angular. 
- **Backend:** Java Spring Boot.
- **Arquitetura:** Híbrida (SQL + NoSQL + Cache + Filesystem).

## **Tecnologias e Arquitetura de Dados**

O diferencial deste projeto é o uso estratégico de 4 formas de armazenamento:

### **1\. PostgreSQL (Relacional)**

- **Função:** Armazena o "Core" do sistema (Usuários, Pontos Turísticos).
- **Por que:** Garante integridade referencial (ACID), relacionamentos fortes e segurança nos dados cadastrais.

### **2\. MongoDB (NoSQL)**

- **Função:** Armazena Avaliações (Comentários + Notas) e Metadados das Fotos.
- **Por que:** Ideal para dados volumosos que crescem indefinidamente (feeds) e flexibilidade de esquema.

### **3\. Redis (Cache In-Memory)**

- **Função:** Cache de listagem e busca de pontos turísticos.
- **Por que:** Acelera drasticamente a Home Page. O cache guarda o resultado das buscas (com filtros) e é invalidado automaticamente (TTL 10min) ou quando um dado é alterado.

### **4\. Filesystem (Disco Local)**

- **Função:** Armazena os arquivos físicos das fotos (.jpg, .png).
- **Por que:** Armazenar imagens dentro de bancos de dados é ineficiente. Salvamos o arquivo na pasta /uploads na raiz do projeto e apenas o link no banco.

## **Pré-requisitos (Instalação Local)**

Este projeto foi configurado para rodar **localmente** (sem Docker). Certifique-se de ter instalado:

### **1\. Java & Ferramentas**

- **Java JDK:** Versão 21 ou superior
- **Maven:** Incluído no projeto (wrapper `./mvnw`)
- **IDE:** IntelliJ IDEA, Eclipse ou VS Code

### **2\. Node.js & Angular**

- **Node.js:** Versão LTS (v18 ou v20).
- **Angular CLI:** Versão 19+ (`npm install -g @angular/cli`)

### **3\. Banco de Dados (Instalação Obrigatória)**

- **PostgreSQL:**
  - Crie um banco de dados chamado zarpar.
  - Porta padrão: 5432.
- **MongoDB:**
  - Versão Community Server.
  - Porta padrão: 27017.
- **Redis:**
  - Instale o Redis Server (Windows .msi ou via WSL/Linux).
  - Porta padrão: 6379.

## **Como Rodar a Aplicação**

### **Passo 1: Criar Banco de Dados**

```sql
-- No PostgreSQL, criar o banco:
CREATE DATABASE zarpar;
```

### **Passo 2: Configurar o Backend**

Edite `Back-end/zarpar/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/zarpar
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_AQUI
spring.jpa.hibernate.ddl-auto=update

spring.data.mongodb.uri=mongodb://localhost:27017/zarpar_docs
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### **Passo 3: Instalar Dependências e Iniciar Backend**

```bash
cd Back-end/zarpar
./mvnw clean install
./mvnw spring-boot:run
```
**Backend:** http://localhost:8080

### **Passo 4: Instalar Dependências e Iniciar Frontend**

```bash
cd Front-end/zarpar
npm install
ng serve --port 4200
```
**Frontend:** http://localhost:4200

### **Passo 5: Criar Usuário Administrador**

Após criar uma conta pelo sistema, altere a role no banco:

```sql
-- Conectar ao PostgreSQL
psql -U postgres -d zarpar

-- Alterar usuário para ADMIN
UPDATE usuarios SET role = 'ADMIN' WHERE email = 'seu-email@exemplo.com';
```

**OU** use o usuário padrão já existente:
- Email: `nuninhos@zarpar.local`
- Senha: `dados1`

## **Requisitos Funcionais Implementados**

## **Testando o Cache (Redis)**

Para verificar se o Redis está funcionando:

- Abra o **redis-cli** no seu terminal.
- Acesse a Home Page do sistema no navegador (<http://localhost:4200>).
- No terminal do Redis, digite: `KEYS *`
- Se vir algo como `"pontos::SimpleKey []"`, o cache foi salvo com sucesso!
- Na próxima atualização da página (F5), o Backend não fará consulta ao SQL, retornando o dado do Redis instantaneamente.
  - _Para limpar o cache manualmente, use: `FLUSHALL`_
