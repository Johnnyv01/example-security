# OAuth2 Security Example - Spring Boot 3.x

Projeto completo demonstrando implementação de OAuth2 com Spring Boot 3.x, incluindo Authorization Server, Resource Server e Client.

## 🚀 Características

- **OAuth2 Authorization Server**: Emite tokens JWT com suporte a refresh token
- **OAuth2 Resource Server**: Protege endpoints REST com validação JWT
- **OAuth2 Client**: Demonstra consumo de APIs protegidas
- **Banco de dados H2**: Armazenamento em memória para desenvolvimento
- **Interface Web**: Páginas HTML para testar funcionalidades
- **Múltiplos Grant Types**: Authorization Code, Client Credentials, Refresh Token
- **Scopes e Roles**: Controle granular de acesso

## 📋 Pré-requisitos

- Java 17+
- Maven 3.6+

## 🛠️ Como Executar

1. **Clone o projeto**:
   ```bash
   git clone <repository-url>
   cd oauth2-security-example
   ```

2. **Execute a aplicação**:
   ```bash
   mvn spring-boot:run
   ```

3. **Acesse a aplicação**:
   - Interface principal: http://localhost:8081
   - Página de teste: http://localhost:8081/test.html
   - H2 Console: http://localhost:8081/h2-console

## 👤 Credenciais de Teste

### Usuários
- **user** / password (ROLE_USER)
- **admin** / admin (ROLE_USER, ROLE_ADMIN)

### Clientes OAuth2
- **client-app** / secret (cliente confidencial)
- **public-client** (cliente público, sem secret)

### Banco H2
- **URL**: jdbc:h2:mem:oauth2db
- **Username**: sa
- **Password**: password

## 🔗 Endpoints Principais

### Públicos (sem autenticação)
- `GET /api/public/info` - Informações públicas
- `GET /h2-console` - Console do banco H2

### Protegidos (requer token JWT)
- `GET /api/user/profile` - Perfil do usuário (scope: read)
- `GET /api/user/data` - Dados do usuário (scope: read)
- `GET /api/admin/users` - Lista usuários (scope: write)
- `GET /api/admin/system` - Info do sistema (scope: write)
- `GET /api/protected/info` - Info protegida (qualquer token válido)

### OAuth2
- `POST /oauth2/token` - Obter token de acesso
- `GET /oauth2/authorize` - Endpoint de autorização
- `GET /oauth2/client-credentials-example` - Exemplo automático
- `GET /oauth2/authorization-url` - URL de autorização
- `POST /oauth2/test-api` - Testar API com token

## 🎯 Como Acessar Endpoints Privados

### ❌ Por que não irá funcionar?

Os endpoints privados (como `/api/user/profile`) **NÃO PODEM** ser acessados diretamente pelo navegador porque:
- Requerem um **token JWT válido** no header `Authorization: Bearer {token}`
- O navegador não envia automaticamente este header
- Sem o token, você recebe erro 401 (Unauthorized) ou 404 (Not Found)

### ✅ Processo Correto para Acessar Rotas Privadas

#### **Método 1: Client Credentials (Mais Simples)**

1. **Obter token automaticamente**:
   ```
   GET http://localhost:8081/oauth2/client-credentials-example
   ```
   Ou manualmente:
   ```bash
   curl -X POST http://localhost:8081/oauth2/token \
     -H "Authorization: Basic Y2xpZW50LWFwcDpzZWNyZXQ=" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "grant_type=client_credentials&scope=read write"
   ```

2. **Usar o token para acessar API**:
   ```bash
   curl -X GET http://localhost:8081/api/user/profile \
     -H "Authorization: Bearer {SEU_TOKEN_AQUI}"
   ```

#### **Método 2: Authorization Code Flow (Completo)**

1. **Obter URL de autorização**:
   ```
   GET http://localhost:8081/oauth2/authorization-url
   ```

2. **Acessar URL no navegador** e fazer login com:
   - **user** / password (ROLE_USER)
   - **admin** / admin (ROLE_ADMIN)

3. **Após autorizar**, você será redirecionado para:
   ```
   http://localhost:8081/authorized?code=SEU_CODIGO_AQUI
   ```

4. **Trocar código por token**:
   ```bash
   curl -X POST http://localhost:8081/oauth2/token \
     -H "Authorization: Basic Y2xpZW50LWFwcDpzZWNyZXQ=" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "grant_type=authorization_code&code=SEU_CODIGO&redirect_uri=http://localhost:8081/authorized"
   ```

5. **Usar token para acessar APIs**:
   ```bash
   curl -X GET http://localhost:8081/api/user/profile \
     -H "Authorization: Bearer {TOKEN_OBTIDO}"
   ```

#### **Método 3: Usando a Interface Web**

1. Acesse: http://localhost:8081
2. Clique em "Obter Token Automaticamente" 
3. Copie o `access_token` da resposta
4. Use ferramentas como Postman, Insomnia ou curl:
   ```
   GET http://localhost:8081/api/user/profile
   Authorization: Bearer {SEU_TOKEN}
   ```

### 🔧 Testando com Ferramentas

#### **Postman/Insomnia:**
1. Método: GET
2. URL: `http://localhost:8081/api/user/profile`
3. Headers: `Authorization: Bearer {seu_token_aqui}`

#### **Navegador (com extensão):**
- Use extensões como "ModHeader" para adicionar o header Authorization

#### **JavaScript (Frontend):**
```javascript
fetch('http://localhost:8081/api/user/profile', {
  headers: {
    'Authorization': 'Bearer ' + token
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

3. **Autorizar cliente** e obter código

4. **Trocar código por token**:
   ```bash
   curl -X POST http://localhost:8081/oauth2/token \
     -H "Authorization: Basic Y2xpZW50LWFwcDpzZWNyZXQ=" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "grant_type=authorization_code&code={CODE}&redirect_uri=http://localhost:8081/authorized"
   ```

### 3. Refresh Token

```bash
curl -X POST http://localhost:8081/oauth2/token \
  -H "Authorization: Basic Y2xpZW50LWFwcDpzZWNyZXQ=" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=refresh_token&refresh_token={REFRESH_TOKEN}"
```

## 🏗️ Arquitetura

### Authorization Server
- Configuração em `AuthorizationServerConfig.java`
- Emite tokens JWT assinados com RSA
- Suporte a múltiplos grant types
- Configuração de clientes em memória

### Resource Server
- Configuração em `ResourceServerConfig.java`
- Validação de tokens JWT
- Controle de acesso baseado em scopes
- Endpoints protegidos em `/api/**`

### Entidades JPA
- `User`: Usuários do sistema
- `Role`: Roles/permissões
- `OAuth2Client`: Clientes OAuth2

## 🔧 Configurações

### Token Settings
- **Access Token**: 1 hora (3600s)
- **Refresh Token**: 24 horas (86400s)
- **Algoritmo**: RS256 (RSA)
- **Refresh Token Reuse**: Desabilitado

### Scopes Disponíveis
- `openid`: OpenID Connect
- `profile`: Informações do perfil
- `read`: Leitura de recursos
- `write`: Escrita de recursos (admin)

### Grant Types Suportados
- `authorization_code`: Fluxo de código de autorização
- `client_credentials`: Credenciais do cliente
- `refresh_token`: Renovação de token

## 📁 Estrutura do Projeto

```
src/main/java/com/example/oauth2/
├── config/
│   ├── AuthorizationServerConfig.java  # Configuração do Authorization Server
│   ├── ResourceServerConfig.java       # Configuração do Resource Server
│   └── DataInitializer.java           # Inicialização de dados
├── controller/
│   ├── ApiController.java             # Endpoints protegidos
│   ├── OAuth2ClientController.java    # Endpoints do cliente OAuth2
│   └── HomeController.java            # Páginas web
├── entity/
│   ├── User.java                      # Entidade usuário
│   ├── Role.java                      # Entidade role
│   ├── ERole.java                     # Enum de roles
│   └── OAuth2Client.java              # Entidade cliente OAuth2
├── repository/
│   ├── UserRepository.java            # Repositório de usuários
│   ├── RoleRepository.java            # Repositório de roles
│   └── OAuth2ClientRepository.java    # Repositório de clientes
└── OAuth2SecurityExampleApplication.java

src/main/resources/
├── application.yml                     # Configurações da aplicação
└── templates/
    ├── index.html                      # Página principal
    └── authorized.html                 # Página de callback
```

## 🐛 Troubleshooting

### ❌ Erro "Whitelabel Error Page" ao acessar endpoints privados

**Problema:** Ao tentar acessar `/api/user/profile` diretamente no navegador, aparece:
```
Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.
There was an unexpected error (type=Not Found, status=404).
```

**Causa:** O endpoint está protegido e requer autenticação JWT. Sem o token, o Spring Security bloqueia o acesso.

**Solução:** 
1. **NUNCA** acesse endpoints privados diretamente no navegador
2. Primeiro obtenha um token OAuth2
3. Use o token no header `Authorization: Bearer {token}`
4. Use ferramentas como Postman, curl ou código JavaScript

### 🔑 Você tem o código de autorização? Use-o!

Se você já tem um código como: `http://localhost:8081/authorized?code=fZJ3wr1y9T-UsFsMEDyllUosSONVC6Dvu2bx8QNy7YtfPMe1OhQbdUMzU03Vvgf7cNYkBoqo3SkWNohBzC5dOXDDYWEzCAqpRRT0dnPeUdWinr3gxqPiY6VSMaysTw1G`

**Troque o código por um token:**
```bash
curl -X POST http://localhost:8081/oauth2/token \
  -H "Authorization: Basic Y2xpZW50LWFwcDpzZWNyZXQ=" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=authorization_code&code=fZJ3wr1y9T-UsFsMEDyllUosSONVC6Dvu2bx8QNy7YtfPMe1OhQbdUMzU03Vvgf7cNYkBoqo3SkWNohBzC5dOXDDYWEzCAqpRRT0dnPeUdWinr3gxqPiY6VSMaysTw1G&redirect_uri=http://localhost:8081/authorized"
```

**Depois use o token obtido:**
```bash
curl -X GET http://localhost:8081/api/user/profile \
  -H "Authorization: Bearer {TOKEN_OBTIDO_ACIMA}"
```

### Token Inválido
- Verifique se o token não expirou (válido por 1 hora)
- Confirme se está usando o header correto: `Authorization: Bearer {token}`
- Verifique se o scope do token permite acesso ao endpoint
- Tokens com scope 'read': podem acessar `/api/user/*`
- Tokens com scope 'write': podem acessar `/api/admin/*`

### Erro de Autorização
- Confirme as credenciais do cliente (client-app:secret)
- Verifique se o redirect_uri está correto
- Confirme se o grant_type é suportado pelo cliente
- Use `Basic Y2xpZW50LWFwcDpzZWNyZXQ=` (client-app:secret em Base64)

### Problemas de CORS
- Para desenvolvimento, CORS está desabilitado
- Em produção, configure adequadamente

## 📚 Referências

- [Spring Authorization Server](https://spring.io/projects/spring-authorization-server)
- [OAuth 2.0 RFC](https://tools.ietf.org/html/rfc6749)
- [JWT RFC](https://tools.ietf.org/html/rfc7519)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)

## 📄 Licença

Este projeto é fornecido como exemplo educacional.

---

**Desenvolvido com Spring Boot 3.x + Spring Security + OAuth2** 🚀
