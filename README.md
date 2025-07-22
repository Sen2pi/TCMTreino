# KPSTreasury - Sistema de Gestão Treasury e Collateral

Sistema completo de gestão financeira para Treasury e Collateral Management, desenvolvido com Spring Boot 3 e React, demonstrando conhecimento avançado em tecnologias enterprise.

## 🏗️ Arquitetura do Backend

### 1. Camada de Apresentação (Controllers)
- **TreasuryController**: Endpoints REST para gestão de treasury
- **CollateralController**: Endpoints REST para gestão de collateral  
- **AuthController**: Endpoints de autenticação e autorização

### 2. Camada de Negócio (Services)
- **TreasuryService**: Lógica de negócio para treasury
- **CollateralService**: Lógica de negócio para collateral
- **UserService**: Gestão de utilizadores

### 3. Camada de Dados (Repository)
- **TreasuryRepository**: Acesso a dados treasury
- **CollateralRepository**: Acesso a dados collateral
- **UserRepository**: Acesso a dados de utilizadores

### 4. Segurança
- **SecurityConfig**: Configuração Spring Security com JWT
- **JwtAuthenticationFilter**: Filtro para validação de tokens
- **JwtUtil**: Utilities para geração e validação de JWT

### 5. Messaging (Kafka)
- **TreasuryProducer**: Producer para eventos de treasury
- **TreasuryConsumer**: Consumer para processar eventos
- **KafkaConfig**: Configuração do Kafka

## 🎨 Arquitetura do Frontend

### 1. Componentes de Layout
- **Layout**: Layout principal com sidebar e header
- **Header**: Cabeçalho com informações do utilizador
- **Sidebar**: Menu lateral de navegação

### 2. Páginas Principais
- **Dashboard**: Visão geral com métricas importantes
- **Treasury**: Gestão de contas treasury
- **Collateral**: Gestão de collateral
- **Login**: Página de autenticação

### 3. Serviços
- **api.js**: Configuração base do Axios
- **treasury.js**: Serviços para treasury
- **collateral.js**: Serviços para collateral
- **auth.js**: Serviços de autenticação

### 4. Context e Estado
- **AuthContext**: Context para gestão de autenticação
- **ProtectedRoute**: Componente para rotas protegidas

## ✅ Funcionalidades Implementadas

### Treasury Management
- ✅ CRUD completo para contas treasury
- ✅ Dashboard com métricas em tempo real
- ✅ Filtros e paginação para listagem
- ✅ Validação de dados com feedback visual
- ✅ Notificações via Kafka para eventos

### Collateral Management
- ✅ Registo e manutenção de collateral
- ✅ Cálculo automático de valores elegíveis
- ✅ Monitorização de margens e haircuts
- ✅ Gestão de elegibilidade por critérios
- ✅ Relatórios de exposição ao risco

### Segurança e Autenticação
- ✅ JWT Authentication com Spring Security
- ✅ Autorização baseada em roles (TREASURY, COLLATERAL, ADMIN)
- ✅ Proteção CSRF e CORS configurado
- ✅ Interceptors para token management
- ✅ Logout automático em caso de token expirado

### Integração e Comunicação
- ✅ REST APIs com documentação OpenAPI
- ✅ Kafka messaging para eventos assíncronos
- ✅ Real-time updates via WebSocket (preparado)
- ✅ Error handling centralizado
- ✅ Logging estruturado

## 🚀 Tecnologias Utilizadas

### Backend
- **Java 17**: Features modernas (sealed classes, pattern matching, records)
- **Spring Boot 3**: Framework principal com auto-configuration
- **Spring Security**: Autenticação JWT e autorização
- **JPA/Hibernate**: Mapeamento objeto-relacional
- **Apache Kafka**: Messaging assíncrono
- **Maven**: Gestão de dependências

### Frontend
- **ReactJS**: Biblioteca moderna com hooks e context API
- **Material UI**: Interface elegante e responsiva
- **Axios**: Cliente HTTP para APIs REST
- **React Router**: Navegação e rotas protegidas

### Infraestrutura
- **REST APIs**: Endpoints bem estruturados
- **JWT**: Autenticação stateless
- **CORS**: Configuração cross-origin
- **OpenAPI**: Documentação automática

## 🎯 Pontos de Destaque

### 1. Conhecimento Técnico
- Demonstração de Spring Boot 3 com Java 17
- Implementação de JWT authentication do zero
- Integração Kafka para messaging assíncrono
- Material UI com componentes modernos

### 2. Boas Práticas
- Arquitetura em camadas bem definida
- Separação de responsabilidades (SoC)
- Tratamento de erros centralizado
- Validação de dados em múltiplas camadas

### 3. Experiência Financeira
- Conhecimento de Treasury Management
- Compreensão de Collateral Management
- Implementação de cálculos financeiros
- Gestão de risco e elegibilidade

### 4. Desenvolvimento Full-Stack
- Backend robusto com Spring Boot
- Frontend moderno com React
- Integração seamless entre camadas
- Deployment e configuração

## 📦 Estrutura do Projeto

```
KPSTreasury/
├── backend/
│   ├── src/main/java/
│   │   ├── controllers/
│   │   ├── services/
│   │   ├── repositories/
│   │   ├── config/
│   │   └── entities/
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   └── context/
│   └── package.json
└── README.md
```

## 🏃‍♂️ Como Executar

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm start
```

## 📋 Próximos Passos

- [ ] Implementação de testes unitários e integração
- [ ] CI/CD pipeline com GitHub Actions
- [ ] Containerização com Docker
- [ ] Monitorização com Spring Actuator
- [ ] Métricas com Micrometer

---

**Desenvolvido para demonstrar competências técnicas avançadas em desenvolvimento full-stack com foco em sistemas financeiros.**