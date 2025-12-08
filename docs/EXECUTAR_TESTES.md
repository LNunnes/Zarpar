# Como Executar os Testes

Este documento descreve como executar os testes automatizados do projeto Zarpar.

## Pré-requisitos

Antes de executar os testes, certifique-se de que:

1. **PostgreSQL** está rodando e há um banco de dados de testes:
   ```bash
   # Criar banco de testes
   sudo -u postgres psql -c "CREATE DATABASE zarpar_test;"
   ```

2. **MongoDB** está rodando (versão 5.0+)
   ```bash
   sudo systemctl start mongod
   ```

3. **Redis** está rodando
   ```bash
   sudo systemctl start redis
   ```

## Executar Testes do Backend

### Todos os testes

```bash
cd Back-end/zarpar
./mvnw test
```

### Testes específicos

```bash
# Testes de autenticação
./mvnw test -Dtest=UsuarioControllerTest

# Testes de pontos turísticos
./mvnw test -Dtest=PontoTuristicoControllerTest

# Testes de avaliações
./mvnw test -Dtest=AvaliacaoControllerTest

# Testes de fotos
./mvnw test -Dtest=FotoControllerTest

# Testes de exportação
./mvnw test -Dtest=ExportControllerTest

# Testes de importação
./mvnw test -Dtest=ImportControllerTest
```

### Executar com relatório de cobertura

```bash
./mvnw clean test jacoco:report
```

O relatório será gerado em: `target/site/jacoco/index.html`

## Executar Testes do Frontend

```bash
cd Front-end/zarpar
npm test
```

### Executar testes com cobertura

```bash
npm run test:coverage
```

## Cobertura dos Testes

Os testes cobrem os seguintes cenários:

### ✅ Autenticação (UsuarioControllerTest)
- ✅ Criar conta com sucesso
- ✅ Erro ao criar conta com email duplicado
- ✅ Erro ao criar conta sem campos obrigatórios
- ✅ Login com sucesso
- ✅ Erro ao fazer login com senha incorreta
- ✅ Erro ao fazer login com email inexistente

### ✅ CRUD de Pontos Turísticos (PontoTuristicoControllerTest)
- ✅ Criar ponto turístico com sucesso
- ✅ Erro ao criar ponto sem campos obrigatórios
- ✅ Erro ao criar ponto sem autenticação
- ✅ Listar pontos turísticos
- ✅ Atualizar ponto turístico
- ✅ Excluir ponto turístico
- ✅ Filtrar pontos por cidade
- ✅ Filtrar pontos por estado
- ✅ Filtrar pontos por país

### ✅ Avaliações (AvaliacaoControllerTest)
- ✅ Adicionar avaliação com sucesso
- ✅ Erro ao adicionar avaliação duplicada
- ✅ Erro ao adicionar avaliação com nota inválida
- ✅ Erro ao adicionar avaliação sem autenticação
- ✅ Listar avaliações de um ponto
- ✅ Excluir avaliação própria

### ✅ Upload de Fotos (FotoControllerTest)
- ✅ Upload de foto com sucesso
- ✅ Erro ao fazer upload sem autenticação
- ✅ Erro ao fazer upload de arquivo inválido
- ✅ Listar fotos de um ponto
- ✅ Excluir foto própria

### ✅ Exportação de Dados (ExportControllerTest)
- ✅ Exportar todos os pontos em JSON
- ✅ Exportar todos os pontos em CSV
- ✅ Exportar todos os pontos em XML
- ✅ Exportar ponto específico em JSON
- ✅ Erro ao exportar sem permissão ADMIN
- ✅ Erro ao exportar sem autenticação
- ✅ Erro ao exportar com formato inválido
- ✅ Verificar dados completos no JSON

### ✅ Importação de Dados (ImportControllerTest)
- ✅ Importar arquivo JSON com sucesso
- ✅ Importar arquivo CSV com sucesso
- ✅ Importar arquivo XML com sucesso
- ✅ Erro ao importar sem permissão ADMIN
- ✅ Erro ao importar sem autenticação
- ✅ Reportar erros de validação
- ✅ Erro ao importar com formato inválido

## Resultados Esperados

Todos os testes devem passar com sucesso:

```
Tests run: XX, Failures: 0, Errors: 0, Skipped: 0
```

## Troubleshooting

### Erro: "Connection refused" ao executar testes
- Verifique se PostgreSQL, MongoDB e Redis estão rodando
- Confirme as portas: PostgreSQL (5432), MongoDB (27017), Redis (6379)

### Erro: "Database zarpar_test does not exist"
- Execute: `sudo -u postgres psql -c "CREATE DATABASE zarpar_test;"`

### Testes lentos
- Configure cache type como `simple` no `application-test.properties` (já configurado)
- Use `@Transactional` para rollback automático (já aplicado)

## Comandos Úteis

```bash
# Limpar e recompilar antes dos testes
./mvnw clean compile test

# Executar apenas testes que falharam
./mvnw test -Dsurefire.rerunFailingTestsCount=2

# Executar testes em modo verbose
./mvnw test -X

# Pular testes (não recomendado)
./mvnw install -DskipTests
```

