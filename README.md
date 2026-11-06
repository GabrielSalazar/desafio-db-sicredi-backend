# Desafio de Automação de API – dummyjson

Testes automatizados da **autenticação** da API pública [dummyjson](https://dummyjson.com/docs/auth), escritos em **Java + RestAssured**.

Este projeto é uma versão simplificada do `api-dummyjson`: mantém os cenários de autenticação e deixa de fora os de produtos.

## Objetivo

Garantir que a autenticação da API:

- aceite credenciais válidas e devolva os tokens, sem expor a senha
- recuse uma senha errada
- libere o recurso protegido `/auth/me` somente com um token válido

## Sobre a API

| Item | Detalhe |
|------|---------|
| Nome | dummyjson |
| Descrição | API REST pública e fake, voltada para testes |
| URL base | https://dummyjson.com |
| Documentação | https://dummyjson.com/docs/auth |

Endpoints testados:

| Método | Endpoint | Função |
|--------|----------|--------|
| POST | `/auth/login` | Autentica o usuário e devolve `accessToken` e `refreshToken` |
| GET | `/auth/me` | Devolve o usuário dono do token enviado no header `Authorization: Bearer <token>` |

## Tecnologias

| Ferramenta | Versão | Uso |
|------------|--------|-----|
| Java | 17 | Linguagem |
| Maven | 3.9+ | Build e execução |
| RestAssured | 5.5.0 | Requisições HTTP e validação das respostas |
| JUnit 5 | 5.11.3 | Execução dos testes |
| Jackson | 2.18.1 | Conversão do body da requisição para JSON |
| Allure | 2.29.0 | Relatório de execução |

## Pré-requisitos

- JDK 17 ou superior
- Maven 3.9 ou superior
- Acesso à internet, pois os testes chamam a API real

## Estrutura

```
api-dummyjson-v2/
├── .gitignore
├── pom.xml
├── README.md
└── src/test/
    ├── java/br/com/desafio/dummyjson/tests/
    │   ├── BaseTest.java                   # configuração do RestAssured e leitura das credenciais
    │   └── AuthTest.java                   # cenários de autenticação
    └── resources/
        ├── credentials.properties.example  # modelo do arquivo de credenciais
        └── credentials.properties          # credenciais locais, criado por você e ignorado pelo git
```

## Como funciona

O `BaseTest` roda uma vez antes dos testes (`@BeforeAll`) e:

1. Lê o usuário e a senha do `credentials.properties`. Se faltar algum valor, a execução para com uma mensagem explicando o que configurar.
2. Monta o `requestSpec`, usado em todas as chamadas, com a URL base, o formato JSON, timeouts de 10 segundos e o filtro do Allure.
3. Ativa a impressão de request e response no console quando uma validação falha.

O `AuthTest` herda essa configuração, faz as chamadas à API e valida as respostas.

## Configuração

1. Crie o arquivo de credenciais a partir do modelo, na raiz do projeto:

   ```bash
   # Linux, macOS ou Git Bash
   cp src/test/resources/credentials.properties.example src/test/resources/credentials.properties
   ```

   ```powershell
   # Windows PowerShell
   Copy-Item src/test/resources/credentials.properties.example src/test/resources/credentials.properties
   ```

2. Preencha o arquivo com um usuário de teste:

   ```properties
   dummyjson.username=<usuário>
   dummyjson.password=<senha>
   ```

   Os usuários públicos de teste estão na [documentação de autenticação](https://dummyjson.com/docs/auth) e em https://dummyjson.com/users.

O `credentials.properties` é ignorado pelo git, portanto as credenciais não entram no repositório.

## Como executar

Todos os testes:

```bash
mvn test
```

Resultado esperado:

```
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Apenas um cenário:

```bash
mvn test -Dtest=AuthTest#shouldLoginWithValidCredentials
```

## Relatório Allure

O `mvn test` grava os resultados em `target/allure-results`. Para gerar o relatório e abri-lo no navegador:

```bash
mvn io.qameta.allure:allure-maven:2.15.0:serve
```

O relatório mostra o request e o response de cada chamada. Na primeira execução, o Allure é baixado para a pasta `.allure/`, que é ignorada pelo git.

> **Atenção:** o relatório inclui o body do login, com a senha do usuário de teste. Não compartilhe a pasta `target/`.

## Cenários

| Método de teste | Cenário | Status | O que também é validado |
|-----------------|---------|:------:|-------------------------|
| `shouldLoginWithValidCredentials` | Login com credenciais válidas | 200 | `username` correto, `accessToken` e `refreshToken` preenchidos, senha ausente da resposta |
| `shouldRejectInvalidPassword` | Login com senha errada | 400 | Mensagem `Invalid credentials`, `accessToken` ausente da resposta |
| `shouldAccessProtectedResourceWithToken` | `/auth/me` com token válido | 200 | `username` do usuário logado, `id` preenchido |
| `shouldDenyAccessWithoutToken` | `/auth/me` sem token | 401 | Mensagem `Access Token is required` |
| `shouldDenyAccessWithInvalidToken` | `/auth/me` com token inválido | 401 | Mensagem contendo `Invalid` |

## Problemas comuns

| Sintoma | Causa provável | Solução |
|---------|----------------|---------|
| `Propriedade 'dummyjson.username' não configurada` | O `credentials.properties` não existe ou está incompleto | Siga os passos de [Configuração](#configuração) |
| Os testes com credenciais válidas falham com status 400 | Usuário ou senha incorretos no `credentials.properties` | Confira os dados do usuário de teste |
| Erro de conexão ou timeout | Sem acesso à internet, bloqueio de proxy ou API indisponível | Confira a rede e se https://dummyjson.com responde |

## Diferenças para o `api-dummyjson`

| Item | `api-dummyjson` | `api-dummyjson-v2` |
|------|-----------------|--------------------|
| Escopo | Autenticação e produtos (13 testes) | Somente autenticação (5 testes) |
| Chamadas à API | Classes `AuthClient` e `ProductsClient` | Diretamente no `AuthTest` |
| Body do login | Record `LoginRequest` | `Map` |
| Credenciais | Classe `Credentials`, com variáveis de ambiente ou arquivo | `BaseTest`, somente com arquivo |
| Tempo de resposta | Valida o limite de 5 segundos em cada resposta verificada | Não valida |
| Validade do token | Pede 30 minutos com `expiresInMins` | Usa o padrão da API |

## Fora do escopo

- Endpoints de produtos (`/products`)
- Renovação de token (`/auth/refresh`)
- Pipeline de CI
