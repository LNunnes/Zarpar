# **Zarpar - Plataforma de Turismo**

Bem-vindo ao **Zarpar**, uma aplicação Fullstack robusta para gestão, avaliação e descoberta de pontos turísticos. O projeto utiliza uma arquitetura de **Persistência Poliglota**, extraindo o melhor desempenho de diferentes tecnologias de banco de dados.

## **Sobre o Projeto**

O Zarpar permite que viajantes descubram destinos, vejam fotos, leiam avaliações e compartilhem suas próprias experiências.

- **Frontend:** Angular. 
- **Backend:** Java Spring Boot.
- **Arquitetura:** Cliente-Servidor, composta por um backend em Java (Spring) e um FrontEnd em TypeScript (Angular). 
- **Solução de persistência:** Híbrida (SQL + NoSQL + Cache + Filesystem).
- **Solução de deploy:** Docker

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

## Instalação
Esse projeto pode ser executado de duas formas diferentes:
- A partir do uso de containers (Docker) **(Recomendado)**; ou
- A partir de uma instalação manual.
---
### Instalação e execução via Docker (Recomendado)
Para promover a facilidade de uso, a aplicação foi containerizada utilizando o Docker e os containers foram orquestrados utilizando Docker-Compose.  

#### Dependências
Para utilizar esse meio de execução, são necessárias as ferramentas:
- Docker; e
- Docker-Compose.

Caso necessite de ajuda para instalar o [Docker](https://docs.docker.com/engine/install/) ou o [Docker Compose](https://docs.docker.com/compose/install/), clique no nome da ferramenta para ver a documentação.

#### **Passo 1: Compilar as imagens que não estão disponibilizadas em um registro de container (Back e front end)**  
Para executar a aplicação, primeiro é necessário compilar as duas imagens relativas aos serviços de frontend e backend da aplicação; para isso, abra um terminal na pasta raiz do repositório e utilize o seguinte comando:
```bash
docker compose build
```
Caso efetue mudanças no código da aplicação e queira recompilar, adicione a opção `--no-cache` ao comando para garantir que não seja utilizada uma imagem previamente compilada.

#### **Passo 2: Executar os conteineres que apoiam e compõem a aplicação**
Após compilar as imagens necessárias, você pode executá-la utilizando o comando:
```bash
docker compose up
```

Caso utilize o comando acima, os containers permanecerão ativos enquanto o terminal estiver aberto e executando o comando, e será mostrado o log de execução de todos os containers, sendo possível visualizar a atividade dos serviços.  
Caso esse comportamento não seja desejado, utilize o comando abaixo:
```bash
docker compose up -d
```

Ambos os comandos iniciarão automaticamente os serviços que compõem a aplicação e das quais elas dependem.  
Ao fim da inicialização, que pode demorar alguns segundos, os serviços estarão disponíveis nas seguintes portas:

| Serviço    | Porta                         |
|------------|-------------------------------|
| PostgreSQL | 5432                          |
| MongoDB    | 27017                         |
| Redis      | 6379                          |
| Backend    | [8080](http://localhost:8080) |
| Frontend   | [4200](http://localhost:4200) |

Obs.: Os mapeamentos de portas podem ser alterados no arquivo docker-compose.yml.

#### **Passo 3: Crie um usuário ou utilize o usuário já criado por padrão**
O seguinte usuário é criado por padrão, automaticamente, e pode ser utilizado para interagir com a aplicação:
- Email: `nuninhos@zarpar.local`
- Senha: `dados1`

Alternativamente, utilize a opção [Criar conta](http://localhost:4200/register), no aplicativo.

#### **Passo 4: Desabilite os serviços e remova os volumes após o uso**
Caso tenha utilizado o comando `docker compose up` para iniciar a aplicação, no mesmo terminal, precione as teclas CTRL+C; 

Caso tenha utilizado o comando `docker compose up -d` para executar os containers em segundo plano, utilize o comando a seguir para encerrar a execução:
```bash
docker compose down
```

Para remover os volumes criados a fim de persistir os dados da aplicação, adicione a opção `-v` ao comando:
```bash
docker compose down -v
```

Por fim, utilize comando abaixo para remover os containers que não estão mais sendo executados:
```bash
docker compose rm
```
---
### Instalação e execução manual
A seguinte seção aborda a instalação local da aplicação, sem a utilização de conteineres.
#### Dependências
Para instalar o projeto localmente, certifique-se de instalar as seguintes dependências:
- **1\. Java & Ferramentas**
  - **Java JDK:** Versão 21 ou superior
  - **Maven:** Incluído no projeto (wrapper `./mvnw`)
  - **IDE:** IntelliJ IDEA, Eclipse ou VS Code

- **2\. Node.js & Angular**
  - **Node.js:** Versão LTS (v18 ou v20).
  - **Angular CLI:** Versão 19+ (`npm install -g @angular/cli`)
- **3\. Banco de Dados (Instalação Obrigatória)**
  - **PostgreSQL:**
    - Crie um banco de dados chamado zarpar.
    - Porta padrão: 5432.
  - **MongoDB:**
    - Versão Community Server.
    - Porta padrão: 27017.
  - **Redis:**
    - Instale o Redis Server (Windows .msi ou via WSL/Linux).
    - Porta padrão: 6379.

#### Processo de instalação
##### **Passo 1: Criar Banco de Dados**

```sql
-- No PostgreSQL, criar o banco:
CREATE DATABASE zarpar;
```

##### **Passo 2: Configurar o Backend**

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

##### **Passo 3: Instalar Dependências e Iniciar Backend**

```bash
cd Back-end/zarpar
./mvnw clean install
./mvnw spring-boot:run
```
**Backend:** http://localhost:8080

##### **Passo 4: Instalar Dependências e Iniciar Frontend**

```bash
cd Front-end/zarpar
npm install
ng serve --port 4200
```
**Frontend:** http://localhost:4200

##### **Passo 5: Criar Usuário Administrador**

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
---
## **Testes Automatizados**

O projeto inclui 44 testes unitários que cobrem autenticação, CRUD de pontos, avaliações, upload de fotos, filtros e exportação de dados.

```bash
# Backend
cd Back-end/zarpar
./mvnw test

# Frontend
cd Front-end/zarpar
npm test
```

---
## **Requisitos Funcionais Implementados**

## **Testando o Cache (Redis)**

Para verificar se o Redis está funcionando:

- Abra o **redis-cli** no seu terminal.
- Acesse a Home Page do sistema no navegador (<http://localhost:4200>).
- No terminal do Redis, digite: `KEYS *`
- Se vir algo como `"pontos::SimpleKey []"`, o cache foi salvo com sucesso!
- Na próxima atualização da página (F5), o Backend não fará consulta ao SQL, retornando o dado do Redis instantaneamente.
  - _Para limpar o cache manualmente, use: `FLUSHALL`_
