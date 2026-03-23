# Spring Boot + Spring Data MongoDB + MongoDB Atlas (Java 21)

Projeto de exemplo com:

- Java 21
- Spring Boot
- Spring Data MongoDB
- CRUD REST completo
- camada de serviço
- DTOs de entrada e saída
- tratamento global de exceções
- paginação, ordenação e filtros
- perfil local com MongoDB via Docker Compose
- perfil Atlas com variável de ambiente

## Estrutura

- `entity`: documento MongoDB
- `dto`: objetos de request, response e paginação
- `repository`: acesso a dados
- `service`: regras de negócio
- `controller`: endpoints REST
- `exception`: exceções e handler global

## Endpoints

### Criar estudante

```http
POST /api/students
Content-Type: application/json
```

Exemplo:

```json
{
  "name": "Orlando Patriarcha",
  "course": "Inteligência Artificial",
  "age": 39,
  "email": "orlando@example.com"
}
```

### Buscar por id

```http
GET /api/students/{id}
```

### Listar com paginação e filtros

```http
GET /api/students?page=0&size=10&sortBy=name&direction=asc&name=orlando&course=ia
```

### Atualizar

```http
PUT /api/students/{id}
Content-Type: application/json
```

### Remover

```http
DELETE /api/students/{id}
```

## Como executar localmente com Docker Compose

Suba o MongoDB local:

```bash
docker compose up -d
```

Depois rode a aplicação com o profile local:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

## Como executar com MongoDB Atlas

### 1. No Atlas

- crie o cluster
- libere o seu IP
- crie um database user
- copie a connection string

Exemplo de URI:

```text
mongodb+srv://USUARIO:SENHA@cluster0.xxxxx.mongodb.net/schooldb?retryWrites=true&w=majority
```

### 2. Exporte a variável de ambiente

macOS/Linux:

```bash
export MONGODB_ATLAS_URI='mongodb+srv://USUARIO:SENHA@cluster0.xxxxx.mongodb.net/schooldb?retryWrites=true&w=majority'
```

Windows PowerShell:

```powershell
$env:MONGODB_ATLAS_URI='mongodb+srv://USUARIO:SENHA@cluster0.xxxxx.mongodb.net/schooldb?retryWrites=true&w=majority'
```

### 3. Rode a aplicação com o profile atlas

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=atlas
```

## Build

```bash
mvn clean package
```

## Testes

```bash
mvn test
```

## Observações

- O arquivo `application.properties` usa `MONGODB_ATLAS_URI` quando ela existir.
- Sem variável definida, o fallback é `mongodb://localhost:27017/schooldb`.
- O projeto já está preparado para uso didático e pode ser expandido com autenticação, Swagger/OpenAPI, testes de integração e auditoria.
