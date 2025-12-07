# Documento de Requisitos do Sistema

## 1. Introdução

### 1.1 Objetivo
Este documento descreve os requisitos funcionais e não-funcionais do sistema **Zarpar**, uma plataforma web para gestão, descoberta e avaliação de pontos turísticos. O sistema permite que usuários registrados cadastrem destinos turísticos, compartilhem fotos, avaliem locais e consultem informações sobre hospedagens próximas.

### 1.2 Escopo
O Zarpar é uma aplicação fullstack desenvolvida como projeto final da disciplina de Persistência de Dados, implementando uma arquitetura de **Persistência Poliglota** que integra bancos de dados relacionais (PostgreSQL), NoSQL (MongoDB), cache em memória (Redis) e armazenamento em disco (Filesystem).

### 1.3 Definições e Acrônimos
- **CRUD**: Create, Read, Update, Delete
- **ADMIN**: Usuário com privilégios administrativos
- **USER**: Usuário comum do sistema
- **API**: Application Programming Interface
- **TTL**: Time To Live (tempo de vida do cache)

---

## 2. Requisitos Funcionais

### RF01 - Autenticação de Usuários
**Descrição:** O sistema deve permitir o cadastro e autenticação de usuários.

**Critérios de Aceitação:**
- O sistema deve permitir registro de novos usuários informando nome, email e senha
- O sistema deve validar unicidade do email
- O sistema deve permitir login com email e senha
- O sistema deve manter sessão do usuário autenticado
- O sistema deve suportar dois tipos de perfis: USER e ADMIN
- O sistema deve permitir logout do usuário

### RF02 - Gerenciamento de Pontos Turísticos
**Descrição:** O sistema deve permitir operações de CRUD sobre pontos turísticos.

**Critérios de Aceitação:**
- Usuários autenticados podem criar novos pontos turísticos
- Todos os usuários (inclusive anônimos) podem visualizar a lista de pontos
- Todos os usuários podem visualizar detalhes de um ponto específico
- Usuários autenticados podem editar pontos que criaram
- Usuários ADMIN podem editar qualquer ponto
- Usuários autenticados podem excluir apenas seus próprios pontos
- Usuários ADMIN podem excluir qualquer ponto
- Campos obrigatórios: nome, descrição, cidade, latitude, longitude, categoria

### RF03 - Busca e Filtros de Pontos Turísticos
**Descrição:** O sistema deve permitir busca e filtragem de pontos turísticos.

**Critérios de Aceitação:**
- O sistema deve permitir filtro por cidade
- O sistema deve permitir filtro por categoria (Natureza, Cultura, História, Gastronomia, Aventura)
- O sistema deve permitir filtro por avaliação mínima
- O sistema deve implementar paginação de resultados
- O sistema deve cachear resultados de buscas frequentes

### RF04 - Upload e Gestão de Fotos
**Descrição:** O sistema deve permitir upload de fotos para pontos turísticos.

**Critérios de Aceitação:**
- Usuários autenticados podem fazer upload de fotos para pontos turísticos
- O sistema deve armazenar arquivos de imagem em disco local
- O sistema deve armazenar metadados das fotos em MongoDB
- O sistema deve suportar formatos JPG, JPEG e PNG
- O sistema deve permitir visualização das fotos de cada ponto
- Usuários podem excluir fotos que enviaram
- ADMIN pode excluir qualquer foto

### RF05 - Avaliações e Comentários
**Descrição:** O sistema deve permitir que usuários avaliem e comentem pontos turísticos.

**Critérios de Aceitação:**
- Usuários autenticados podem avaliar pontos com nota de 1 a 5
- Usuários autenticados podem adicionar comentário textual à avaliação
- Cada usuário pode fazer apenas uma avaliação por ponto
- O sistema deve calcular média de avaliações de cada ponto
- Todos os usuários podem visualizar avaliações e comentários
- Usuários podem editar suas próprias avaliações
- Usuários podem excluir suas próprias avaliações

### RF06 - Cadastro de Hospedagens
**Descrição:** O sistema deve permitir cadastro de hospedagens relacionadas a pontos turísticos.

**Critérios de Aceitação:**
- Usuários autenticados podem cadastrar hospedagens
- Cada hospedagem deve estar vinculada a um ponto turístico
- Campos da hospedagem: nome, endereço, preço médio, tipo, telefone, link
- Todos os usuários podem visualizar hospedagens
- Usuários podem editar hospedagens que cadastraram
- Usuários podem excluir hospedagens que cadastraram
- ADMIN pode editar/excluir qualquer hospedagem

### RF07 - Informações de Localização
**Descrição:** O sistema deve fornecer informações sobre localização e como chegar aos pontos.

**Critérios de Aceitação:**
- Cada ponto deve ter coordenadas geográficas (latitude e longitude)
- O sistema deve permitir cadastro de texto orientativo "Como Chegar"
- O sistema deve integrar com Google Maps para visualização de localização
- O sistema deve exibir endereço completo quando disponível

### RF08 - Exportação de Dados
**Descrição:** O sistema deve permitir exportação de dados de pontos turísticos (apenas ADMIN).

**Critérios de Aceitação:**
- Apenas usuários ADMIN podem acessar funcionalidade de exportação
- O sistema deve suportar exportação em formato JSON
- O sistema deve suportar exportação em formato CSV
- O sistema deve suportar exportação em formato XML
- O sistema deve permitir exportação de todos os pontos
- O sistema deve permitir exportação de um ponto específico
- Arquivo exportado deve conter todos os dados do ponto (exceto fotos binárias)

### RF09 - Importação de Dados
**Descrição:** O sistema deve permitir importação em lote de pontos turísticos (apenas ADMIN).

**Critérios de Aceitação:**
- Apenas usuários ADMIN podem acessar funcionalidade de importação
- O sistema deve suportar upload de arquivos JSON, CSV e XML
- O sistema deve validar estrutura do arquivo antes de processar
- O sistema deve validar campos obrigatórios de cada registro
- Para pontos novos, o sistema deve criar no banco de dados
- Para pontos existentes (mesmo nome e cidade), o sistema deve atualizar
- O sistema deve gerar relatório de importação com:
  - Quantidade total de registros processados
  - Quantidade de sucessos
  - Quantidade de erros
  - Lista detalhada de erros encontrados
- O sistema deve aplicar mesmas validações do cadastro manual

---

## 3. Requisitos Não-Funcionais

### RNF01 - Performance
- O sistema deve utilizar cache Redis para otimizar consultas frequentes
- Cache de listagem de pontos deve ter TTL de 10 minutos
- Cache deve ser invalidado automaticamente em operações de criação/atualização/exclusão

### RNF02 - Segurança
- Senhas devem ser armazenadas de forma criptografada
- Sessões devem ter timeout configurável
- Operações sensíveis devem validar permissões do usuário

### RNF03 - Escalabilidade
- Arquitetura poliglota permite escalar componentes independentemente
- MongoDB preparado para alto volume de avaliações
- Redis permite distribuição de cache

### RNF04 - Manutenibilidade
- Código organizado em camadas (Controller, Service, Repository)
- Uso de DTOs para transferência de dados
- Documentação técnica disponível

### RNF05 - Usabilidade
- Interface responsiva (Bootstrap 5)
- Feedback visual para operações do usuário
- Mensagens de erro claras e informativas

---

## 4. Regras de Negócio

### RN01 - Controle de Acesso
- Operações de criação, edição e exclusão exigem usuário autenticado
- Consultas públicas (listagem, detalhes, avaliações) são acessíveis sem autenticação
- Apenas ADMIN pode exportar/importar dados
- Apenas ADMIN pode excluir pontos de outros usuários
- Usuários comuns só podem editar/excluir recursos que criaram

### RN02 - Validação de Dados
- Nome, descrição e cidade são campos obrigatórios
- Latitude deve estar entre -90 e 90
- Longitude deve estar entre -180 e 180
- Email deve ser único no sistema
- Nota de avaliação deve ser inteira entre 1 e 5

### RN03 - Integridade de Dados
- Exclusão de ponto turístico deve excluir em cascata: hospedagens, fotos e avaliações
- Exclusão de usuário deve manter seus pontos no sistema (ou transferir para ADMIN)

---

## 5. Regras de Acesso

| Funcionalidade | Anônimo | USER | ADMIN |
|---|---|---|---|
| Visualizar pontos | ✓ | ✓ | ✓ |
| Criar ponto | ✗ | ✓ | ✓ |
| Editar próprio ponto | ✗ | ✓ | ✓ |
| Editar ponto de outros | ✗ | ✗ | ✓ |
| Excluir próprio ponto | ✗ | ✓ | ✓ |
| Excluir ponto de outros | ✗ | ✗ | ✓ |
| Avaliar ponto | ✗ | ✓ | ✓ |
| Upload de fotos | ✗ | ✓ | ✓ |
| Cadastrar hospedagem | ✗ | ✓ | ✓ |
| Exportar dados | ✗ | ✗ | ✓ |
| Importar dados | ✗ | ✗ | ✓ |

## 6. Arquitetura e Tecnologias

### 6.1 Arquitetura do Sistema
O sistema utiliza arquitetura **Persistência Poliglota**, combinando diferentes tecnologias de armazenamento de acordo com as características dos dados:

### 6.2 Camada de Persistência

#### PostgreSQL (Banco Relacional)
- **Função:** Armazenamento do core do sistema
- **Entidades:** Usuários, Pontos Turísticos, Hospedagens
- **Justificativa:** Garante integridade referencial (ACID), suporta relacionamentos complexos e transações confiáveis

#### MongoDB (Banco NoSQL - Documentos)
- **Função:** Armazenamento de dados volumosos e semi-estruturados
- **Coleções:** Avaliações (comentários + notas), Metadados de Fotos
- **Justificativa:** Flexibilidade de esquema, alta performance para leitura, ideal para dados que crescem indefinidamente

#### Redis (Cache In-Memory)
- **Função:** Cache de consultas frequentes
- **Dados:** Listagens e buscas de pontos turísticos
- **Justificativa:** Reduz drasticamente tempo de resposta, diminui carga no banco de dados principal

#### Filesystem (Disco Local)
- **Função:** Armazenamento de arquivos binários
- **Dados:** Imagens (fotos dos pontos turísticos)
- **Justificativa:** Armazenar BLOBs em bancos de dados é ineficiente; filesystem é mais adequado para arquivos grandes

### 6.3 Stack Tecnológica

#### Backend
- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.x
- **ORM:** Spring Data JPA (Hibernate)
- **NoSQL:** Spring Data MongoDB
- **Cache:** Spring Cache + Redis
- **Build:** Maven

#### Frontend
- **Framework:** Angular 19
- **Linguagem:** TypeScript
- **UI:** Bootstrap 5
- **Programação Reativa:** RxJS
- **HTTP Client:** Angular HttpClient

### 6.4 Padrões de Projeto Utilizados
- **MVC (Model-View-Controller):** Separação de responsabilidades
- **DTO (Data Transfer Object):** Transferência de dados entre camadas
- **Repository Pattern:** Abstração da camada de persistência
- **Dependency Injection:** Inversão de controle com Spring IoC

### 6.5 Requisitos de Infraestrutura
- **Java JDK:** 21+
- **Node.js:** 20+ LTS
- **PostgreSQL:** 12+
- **MongoDB:** 5.0+
- **Redis:** 5.0+
- **Espaço em disco:** ~500MB para aplicação + variável para uploads de fotos


