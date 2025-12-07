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

- **Java JDK:** Versão 21
- **Maven:** Para gerenciar dependências do Backend.
- **IDE:** IntelliJ IDEA (Recomendado) ou Eclipse/VS Code.

### **2\. Node.js & Angular**

- **Node.js:** Versão LTS (v18 ou v20).
- Angular CLI: Instale via terminal com o comando:  
    npm install -g @angular/cli

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

### **Passo 1: Configurar o Backend**

- Clone este repositório.
- Navegue até a pasta do backend.
- Abra o arquivo src/main/resources/application.properties.

**Configure suas credenciais do PostgreSQL**:  
**Properties**  
spring.datasource.url=jdbc:postgresql://localhost:5432/zarpar

spring.datasource.username=postgres

spring.datasource.password=SUA_SENHA_AQUI

\# O Hibernate cria as tabelas automaticamente

spring.jpa.hibernate.ddl-auto=update

### **Passo 2: Iniciar o Backend**

No terminal (na pasta do projeto Java), execute:

mvn spring-boot:run

- O servidor iniciará em: <http://localhost:8080>
- Uma pasta uploads será criada automaticamente na raiz do projeto.

### **Passo 3: Iniciar o Frontend**

- Abra um novo terminal.
- Navegue até a pasta do frontend.
- Instale as dependências:  
    npm install
- Rode o servidor de desenvolvimento:  
    ng serve
- Acesse no navegador: <http://localhost:4200>

## **Testando o Cache (Redis)**

Para verificar se o Redis está funcionando:

- Abra o **redis-cli** no seu terminal (CMD ou PowerShell).
- Acesse a Home Page do sistema no navegador (<http://localhost:4200>).
- No terminal do Redis, digite: KEYS \*
- Se vir algo como "pontos::SimpleKey \[\]", o cache foi salvo com sucesso!
- Na próxima atualização da página (F5), o Backend não fará consulta ao SQL, retornando o dado do Redis instantaneamente.
  - _Para limpar o cache manualmente, use o comando: FLUSHALL_
