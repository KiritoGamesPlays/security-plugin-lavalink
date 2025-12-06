# Lavalink Security Plugin

Plugin de segurança para Lavalink que fornece funcionalidades de bloqueio de IP e logging de buscas usando MongoDB.

## Características

- ✅ Bloqueio e desbloqueio de IPs
- ✅ Logging de consultas de busca no MongoDB
- ✅ API REST completa para gerenciamento
- ✅ Validação de IPs (IPv4 e IPv6)
- ✅ Tratamento robusto de erros
- ✅ Compatível com Lavalink v4
- ✅ Health check endpoint

## Requisitos

- Lavalink v4.0.0 ou superior
- MongoDB 4.0 ou superior
- Java 17 ou superior

## Instalação

1. Baixe o arquivo `lavalink-security-plugin-1.0.0.jar`
2. Coloque o arquivo na pasta `plugins` do seu servidor Lavalink
3. Configure o plugin no arquivo `application.yml`
4. Reinicie o servidor Lavalink

## Configuração

Adicione as seguintes configurações ao seu arquivo `application.yml`:

```yaml
plugins:
  lavalink-security-plugin:
    mongodbUri: "mongodb://localhost:27017"
    database: "lavalink_security"
    collections:
      searches: "search_logs"
      ipBlocks: "ip_blocks"
```

### Parâmetros de Configuração

| Parâmetro | Descrição | Padrão |
|-----------|-----------|--------|
| `mongodbUri` | URI de conexão do MongoDB | `mongodb://localhost:27017` |
| `database` | Nome do banco de dados | `lavalink_security` |
| `collections.searches` | Nome da coleção de logs de busca | `search_logs` |
| `collections.ipBlocks` | Nome da coleção de IPs bloqueados | `ip_blocks` |

### Exemplo com MongoDB Atlas

```yaml
plugins:
  lavalink-security-plugin:
    mongodbUri: "mongodb+srv://usuario:senha@cluster.mongodb.net/?retryWrites=true&w=majority"
    database: "lavalink_security"
    collections:
      searches: "search_logs"
      ipBlocks: "ip_blocks"
```

## API REST

Todos os endpoints estão disponíveis em `/v4/security`.

### Endpoints Disponíveis

#### 1. Listar IPs Bloqueados

```http
GET /v4/security/blocked
```

**Resposta de Sucesso:**
```json
{
  "success": true,
  "count": 2,
  "ips": ["192.168.1.100", "10.0.0.50"]
}
```

#### 2. Bloquear IP

```http
POST /v4/security/block/{ip}
```

**Exemplo:**
```bash
curl -X POST http://localhost:2333/v4/security/block/192.168.1.100
```

**Resposta de Sucesso:**
```json
{
  "success": true,
  "message": "IP blocked successfully",
  "ip": "192.168.1.100"
}
```

#### 3. Desbloquear IP

```http
POST /v4/security/unblock/{ip}
DELETE /v4/security/block/{ip}
```

**Exemplo:**
```bash
curl -X POST http://localhost:2333/v4/security/unblock/192.168.1.100
```

**Resposta de Sucesso:**
```json
{
  "success": true,
  "message": "IP unblocked successfully",
  "ip": "192.168.1.100"
}
```

#### 4. Verificar Status de IP

```http
GET /v4/security/check/{ip}
```

**Exemplo:**
```bash
curl http://localhost:2333/v4/security/check/192.168.1.100
```

**Resposta:**
```json
{
  "success": true,
  "ip": "192.168.1.100",
  "blocked": true
}
```

#### 5. Health Check

```http
GET /v4/security/health
```

**Resposta:**
```json
{
  "status": "healthy",
  "plugin": "lavalink-security-plugin",
  "version": "1.0.0",
  "mongodb": "connected"
}
```

## Estrutura do MongoDB

### Coleção: ip_blocks

```json
{
  "_id": ObjectId("..."),
  "ip": "192.168.1.100",
  "blockedAt": 1701234567890
}
```

### Coleção: search_logs

```json
{
  "_id": ObjectId("..."),
  "guildId": "123456789012345678",
  "query": "ytsearch:music",
  "timestamp": 1701234567890
}
```

## Validação de IPs

O plugin valida automaticamente os IPs antes de bloqueá-los:

- ✅ IPv4: `192.168.1.1`
- ✅ IPv6: `2001:0db8:85a3:0000:0000:8a2e:0370:7334`
- ❌ Formatos inválidos são rejeitados

## Tratamento de Erros

O plugin possui tratamento robusto de erros:

- Todas as operações do MongoDB são protegidas com try-catch
- Erros são logados com detalhes
- Respostas HTTP apropriadas para cada tipo de erro
- Validação de entrada em todos os endpoints

## Logging

O plugin utiliza SLF4J para logging. Os logs incluem:

- Inicialização do plugin
- Conexão com MongoDB
- Operações de bloqueio/desbloqueio
- Erros e exceções
- Buscas realizadas (nível DEBUG)

## Compilação

Para compilar o plugin a partir do código fonte:

```bash
./gradlew shadowJar
```

O arquivo JAR será gerado em: `build/libs/lavalink-security-plugin-1.0.0.jar`

## Desenvolvimento

### Estrutura do Projeto

```
lavalink-security-plugin/
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/arthur/security/
│       │       ├── MongoManager.kt
│       │       ├── PluginConfig.kt
│       │       ├── SearchEventListener.kt
│       │       ├── SecurityPlugin.kt
│       │       ├── SecurityRestController.kt
│       │       └── SharedConfig.kt
│       └── resources/
│           ├── application.yml.example
│           └── lavalink-plugins.properties
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── README.md
```

### Tecnologias Utilizadas

- **Kotlin** 1.9.22
- **Lavalink API** 4.0.7
- **MongoDB Driver** 4.11.1
- **Spring Boot** (fornecido pelo Lavalink)
- **SLF4J** (fornecido pelo Lavalink)

## Segurança

⚠️ **Importante**: Este plugin não implementa autenticação nos endpoints REST. Certifique-se de:

1. Usar a autenticação do Lavalink (header `Authorization`)
2. Configurar firewall para restringir acesso aos endpoints
3. Usar HTTPS em produção
4. Proteger as credenciais do MongoDB

## Suporte

Para reportar bugs ou solicitar funcionalidades, abra uma issue no repositório do projeto.

## Licença

Este projeto é fornecido "como está", sem garantias de qualquer tipo.

## Changelog

### v1.0.0 (2025-12-06)

- ✅ Versão inicial
- ✅ Bloqueio/desbloqueio de IPs
- ✅ Logging de buscas
- ✅ API REST completa
- ✅ Validação de IPs
- ✅ Tratamento de erros robusto
- ✅ Health check endpoint
- ✅ Compatibilidade com Lavalink v4

## Autor

Arthur - Lavalink Security Plugin
