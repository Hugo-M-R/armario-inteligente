# Armário Inteligente

Backend em Java + Spring Boot para o sistema **Armário Inteligente**.

## Sobre o projeto

O Armário Inteligente é uma solução completa para gerenciamento de armários de encomendas em condomínios. O sistema permite o controle de armários, compartimentos, encomendas e usuários, além de oferecer notificações e registro de auditoria.

Este repositório contém a API REST consumida pelo frontend e oferece autenticação JWT, gestão de encomendas com fluxo de retirada por código, notificações, auditoria automática e rate limit distribuído via Redis.

[![Frontend](https://img.shields.io/badge/Frontend-visual--armario--inteligente-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Hugo-M-R/visual-armario-inteligente)

## Stack

[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-8-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)](https://maven.apache.org/)

## 🚀 Funcionalidades

- **Gestão de Usuários**
  - Cadastro de moradores, porteiros e administradores
  - Autenticação JWT
  - Controle de permissões por tipo de usuário
  - Ativação/desativação de usuários
  - Registro de data de criação e atualização

- **Gestão de Armários**
  - Cadastro de armários com localização
  - Controle de status (DISPONÍVEL, OCUPADO, MANUTENÇÃO)
  - Gerenciamento de compartimentos
  - Histórico de alterações

- **Gestão de Encomendas**
  - Registro de encomendas
  - Associação com armários e usuários
  - Controle de entrega
  - Notificações automáticas

- **Notificações**
  - Sistema de notificações para usuários
  - Alertas de novas encomendas
  - Histórico de notificações
  - Status de leitura

- **Auditoria**
  - Registro automático de todas as operações
  - Histórico detalhado de ações
  - Rastreabilidade completa
  - Logs de sistema

## 🚀 Roadmap para Ambiente de Produção

Este projeto foi desenvolvido como um trabalho acadêmico, mas para um ambiente de produção real, as seguintes funcionalidades seriam necessárias:

### 1. Sistema de Retirada de Encomendas
- **Endpoints de Retirada**:
  - `POST /api/encomendas/{id}/retirar`: Endpoint para solicitar retirada de encomenda
  - `POST /api/encomendas/{id}/gerar-codigo`: Geração de código de acesso temporário
  - `POST /api/encomendas/{id}/validar-codigo`: Validação do código de acesso

- **Campos Adicionais na Entidade Encomenda**:
  ```java
  private LocalDateTime dataRetirada;
  private StatusRetirada statusRetirada;
  private String codigoAcesso;
  private LocalDateTime dataExpiracaoCodigo;
  ```

### 2. Integração com Hardware
- **Sistema de Trancas Eletrônicas**:
  - Integração com APIs de trancas inteligentes
  - Sistema de liberação remota de compartimentos
  - Monitoramento de status das trancas
  - Sistema de backup para falhas de energia

- **Serviços de Hardware**:
  ```java
  public class HardwareService {
      public void liberarCompartimento(UUID compartimentoId, String codigo);
      public void verificarStatusTranca(UUID compartimentoId);
      public void registrarEventoTranca(UUID compartimentoId, EventoTranca evento);
  }
  ```

### 3. Segurança e Autenticação
- **Autenticação Multi-fator**:
  - Implementação de 2FA para acesso ao sistema
  - Validação por SMS/Email para retiradas
  - Biometria para acesso aos armários

- **Controle de Acesso Granular**:
  - Papéis adicionais (porteiro, zelador, entregador)
  - Permissões específicas por tipo de operação
  - Registro detalhado de tentativas de acesso

### 4. Notificações e Comunicação
- **Sistema de Notificações Avançado**:
  - Notificações push para dispositivos móveis
  - Integração com WhatsApp/Telegram
  - Templates personalizados por tipo de notificação
  - Sistema de confirmação de leitura

- **Comunicação com Moradores**:
  ```java
  public class NotificacaoService {
      public void enviarNotificacaoRetirada(Encomenda encomenda);
      public void enviarLembreteEncomenda(Encomenda encomenda);
      public void enviarAlertaTempoExcedido(Encomenda encomenda);
  }
  ```

### 5. Monitoramento e Manutenção
- **Sistema de Monitoramento**:
  - Dashboard em tempo real
  - Alertas de sistema
  - Métricas de uso
  - Relatórios de ocupação

- **Manutenção Preventiva**:
  - Agendamento de manutenção
  - Registro de problemas
  - Histórico de manutenções
  - Previsão de falhas

### 6. Integração com Sistemas Externos
- **APIs de Entregadores**:
  - Integração com sistemas de entregas
  - Rastreamento de encomendas
  - Confirmação de entrega
  - Notificações automáticas

- **Sistemas de Condomínio**:
  - Integração com portaria
  - Controle de acesso
  - Registro de visitantes
  - Comunicação com síndico

### 7. Backup e Recuperação
- **Sistema de Backup**:
  - Backup automático do banco de dados
  - Backup de configurações
  - Sistema de recuperação de desastres
  - Versionamento de dados

### 8. Documentação e Suporte
- **Documentação Técnica**:
  - Manual de instalação
  - Guia de configuração
  - Documentação de APIs
  - Guia de troubleshooting

- **Suporte ao Usuário**:
  - Sistema de tickets
  - FAQ automatizado
  - Chat de suporte
  - Base de conhecimento

### 9. Escalabilidade
- **Arquitetura Distribuída**:
  - Load balancing
  - Cache distribuído
  - Filas de mensagens
  - Microserviços

- **Otimizações**:
  - Indexação de banco de dados
  - Cache de consultas
  - Compressão de dados
  - CDN para arquivos estáticos

### 10. Conformidade e Auditoria
- **LGPD e Privacidade**:
  - Política de retenção de dados
  - Exportação de dados pessoais
  - Registro de consentimento
  - Anonimização de dados

- **Auditoria Avançada**:
  - Logs detalhados de operações
  - Rastreamento de mudanças
  - Relatórios de conformidade
  - Alertas de segurança

Estas funcionalidades representam um roadmap para transformar o projeto acadêmico em uma solução robusta e pronta para produção. A implementação deve ser priorizada com base nas necessidades específicas do ambiente de uso.

## 📦 Pré-requisitos

- JDK 25 ou superior
- Maven 3.8+
- Docker e Docker Compose
- Banco PostgreSQL externo (ex.: Neon)
- IDE (recomendado: IntelliJ IDEA ou Eclipse)
- Postman (para testes da API)

## 🔧 Instalação

1. Clone o repositório:
```bash
git clone https://github.com/Tokseg/armario-inteligente.git
cd armario-inteligente
```

2. Configure as variáveis de ambiente:
```bash
cp .env.example .env
```

3. Ajuste no arquivo `.env`:
- `DATABASE_URL` para a URL JDBC do Neon (com `sslmode=require`)
- `JWT_SECRET` em Base64 com no mínimo 256 bits (gere com `openssl rand -base64 32`)
- `REDIS_HOST` (`localhost` localmente; `redis` no Docker Compose)

4. Execute com Docker:
```bash
docker compose up -d --build
```

O sistema estará disponível em:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Health check: http://localhost:8080/actuator/health
- Redis: localhost:6379

## 📦 Estrutura do Projeto

```
.
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/unit/tokseg/armario_inteligente/
│   │   │       ├── aspect/          # Aspectos (Auditoria)
│   │   │       ├── config/          # Configurações (Security, JWT, OpenAPI)
│   │   │       ├── controller/      # Controllers REST e GlobalExceptionHandler
│   │   │       ├── dto/             # Objetos de transferência
│   │   │       ├── exception/       # Exceções customizadas
│   │   │       ├── model/           # Entidades
│   │   │       ├── repository/      # Repositórios JPA
│   │   │       ├── service/         # Serviços
│   │   │       └── util/            # Utilitários (SecurityUtils)
│   │   └── resources/
│   │       ├── db/migration/       # Scripts Flyway
│   │       ├── application.properties
│   │       └── application-docker.properties
│   └── test/                       # Testes
├── Dockerfile                     # Dockerfile da aplicação
├── docker-compose.yml             # Configuração dos containers (app + redis)
├── .dockerignore                  # Itens ignorados no build Docker
├── .gitignore                     # Arquivos ignorados pelo Git
├── .gitattributes                 # Configurações do Git
├── pom.xml                        # Configuração Maven
└── README.md                      # Este arquivo
```

## 📚 Testes da API

### Autenticação

#### Registrar Morador (público)
```http
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
    "nome": "Morador Teste",
    "email": "morador@teste.com",
    "senha": "Morador@123",
    "telefone": "11999999999"
}
```

> Registro público aceita apenas usuários do tipo `MORADOR`. `ADMIN` e `PORTEIRO` são criados por administradores autenticados.

#### Login
```http
POST http://localhost:8080/api/v1/auth/authenticate
Content-Type: application/json

{
    "email": "morador@teste.com",
    "senha": "Morador@123"
}
```

#### Usuário autenticado
```http
GET http://localhost:8080/api/v1/auth/me
Authorization: Bearer {token}
```

### Usuários

#### Criar Morador (admin/porteiro autenticado)
```http
POST http://localhost:8080/api/usuarios
Authorization: Bearer {token}
Content-Type: application/json

{
    "nome": "Morador Teste",
    "email": "morador@teste.com",
    "senha": "Morador@123",
    "telefone": "11988888888",
    "tipo": "MORADOR"
}
```

#### Ativar/desativar usuário (admin)
```http
PATCH http://localhost:8080/api/usuarios/{id}/ativo
Authorization: Bearer {token}
Content-Type: application/json

{
    "ativo": false
}
```

#### Listar Usuários
```http
GET http://localhost:8080/api/usuarios
Authorization: Bearer {token}
```

### Armários

#### Criar Armário
```http
POST http://localhost:8080/api/armarios
Authorization: Bearer {token}
Content-Type: application/json

{
    "numero": "A1",
    "localizacao": "Portaria",
    "status": "DISPONIVEL"
}
```

#### Listar Armários
```http
GET http://localhost:8080/api/armarios
Authorization: Bearer {token}
```

### Encomendas

#### Registrar Encomenda (admin/porteiro)
```http
POST http://localhost:8080/api/encomendas
Authorization: Bearer {token}
Content-Type: application/json

{
    "idEncomenda": "E001",
    "descricao": "Caixa Amazon",
    "remetente": "Amazon",
    "armarioId": "uuid-do-armario",
    "usuarioId": "uuid-do-usuario"
}
```

#### Gerar código de retirada (admin/porteiro)
```http
POST http://localhost:8080/api/encomendas/{id}/gerar-codigo
Authorization: Bearer {token}
```

#### Validar código (morador autenticado)
```http
POST http://localhost:8080/api/encomendas/{id}/validar-codigo
Authorization: Bearer {token}
Content-Type: application/json

{
    "codigo": "123456"
}
```

#### Retirar encomenda
```http
POST http://localhost:8080/api/encomendas/{id}/retirar
Authorization: Bearer {token}
Content-Type: application/json

{
    "codigo": "123456"
}
```

#### Listar Encomendas
```http
GET http://localhost:8080/api/encomendas
Authorization: Bearer {token}
```

### Notificações

#### Listar Notificações
```http
GET http://localhost:8080/api/notificacoes
Authorization: Bearer {token}
```

#### Marcar notificação como lida
```http
PATCH http://localhost:8080/api/notificacoes/{id}/lida
Authorization: Bearer {token}
```

### Compartimentos

#### Listar Compartimentos
```http
GET http://localhost:8080/api/compartimentos
Authorization: Bearer {token}
```

### Auditoria

#### Listar Registros de Auditoria
```http
GET http://localhost:8080/api/auditoria
Authorization: Bearer {token}
```

## 🔐 Segurança

- Autenticação JWT com expiração configurável
- Senhas criptografadas com BCrypt
- Controle de acesso baseado em roles (ADMIN, PORTEIRO, MORADOR)
- Validação de dados com Bean Validation
- Rate limit no endpoint público de autenticação (via Redis)
- CORS com origins explícitas por configuração
- Headers de segurança (CSP, HSTS configurável, X-Frame-Options, X-Content-Type-Options)
- Registro de auditoria automático
- Logs de segurança

- Registro público restrito a `MORADOR`
- Ownership de encomendas e notificações por usuário autenticado
- Auditoria somente leitura via API (escrita automática por AOP)
- Documentação interativa via Swagger/OpenAPI
- Health check via Spring Boot Actuator

## 🧪 Testes

Execute os testes com:
```bash
./mvnw test
./mvnw verify
```

A suíte inclui testes unitários de segurança, ownership, rate limit e fluxo de retirada de encomendas.

## 📝 Notas de Atualização

### Versão 1.1.0
- Segurança reforçada: registro público MORADOR, hash BCrypt centralizado, IDOR corrigido
- Swagger/OpenAPI, rate limit Redis, CORS e headers de segurança configuráveis
- Fluxo MVP de retirada de encomendas (gerar/validar/retirar código)
- Endpoints de ativar usuário e marcar notificação como lida
- Spring Boot Actuator, CI GitHub Actions e testes automatizados

### Versão 1.0.0
- Implementação do sistema de auditoria automática
- Adição de notificações automáticas
- Melhorias na segurança com JWT
- Suporte a Docker com Redis e PostgreSQL externo (Neon)
- Migração de banco de dados com Flyway
- Documentação atualizada com exemplos Postman

## ✨ Autor
- Hugo Machado Ramos - [GitHub](https://github.com/Hugo-M-R)
