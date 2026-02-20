# TodoList API

Sistema de gerenciamento de tarefas desenvolvido com Spring Boot, implementando autenticação segura, persistência de dados e conteinerização.

## Arquitetura e Tecnologias

O projeto utiliza o ecossistema Spring para fornecer uma API RESTful robusta, com as seguintes especificações:

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3.x
* **Persistência:** Spring Data JPA com banco de dados H2 (ambiente de desenvolvimento)
* **Segurança:** Autenticação Basic Auth via filtros customizados e criptografia BCrypt
* **Utilitários:** Lombok para redução de código boilerplate e Java UUID para identificadores únicos

## Funcionalidades e Regras de Negócio

### Gerenciamento de Usuários
* Cadastro de usuários com armazenamento de senhas utilizando hashing BCrypt (fator de custo 10) para garantir a segurança dos dados.

### Sistema de Autenticação e Autorização
* **Filtro de Segurança:** Implementação da classe `FilterTaskAuth` estendendo `OncePerRequestFilter`. O filtro intercepta requisições para rotas protegidas, extrai as credenciais do cabeçalho `Authorization` e valida a identidade do usuário antes de permitir o acesso ao Controller.
* **Isolamento de Dados:** Cada tarefa é vinculada a um `idUser`. O sistema garante que um usuário autenticado possa gerenciar apenas suas próprias tarefas, prevenindo acesso não autorizado a dados de terceiros.

### Gerenciamento de Tarefas
* **Criação:** Registro de tarefas com título, descrição, prioridade e períodos (data de início e término).
* **Validação de Título:** Limitação estrita de 50 caracteres para o campo `title`, validada diretamente no modelo de dados para garantir a integridade do banco de dados.
* **Tratamento de Exceções:** Implementação de um `ExceptionHandlerController` global para capturar e formatar erros de validação (como títulos excedendo o limite) em respostas amigáveis ao cliente.
* **Atualização Parcial:** Implementação de método para atualizar propriedades de forma dinâmica, preservando dados não enviados na requisição através de utilitários de cópia de propriedades.

## Endpoints da API

| Método | Rota | Descrição | Autenticação |
| :--- | :--- | :--- | :--- |
| POST | `/users/` | Cadastro de novo usuário | Não |
| POST | `/tasks/` | Criação de nova tarefa | Sim |
| GET | `/tasks/` | Listagem de tarefas do usuário | Sim |
| PUT | `/tasks/{id}` | Atualização de tarefa específica | Sim |

## Configuração de Deploy

A aplicação está configurada para deploy via Docker, utilizando builds de múltiplos estágios para otimização de imagem e compatibilidade com provedores Cloud (como Render.com).

### Dockerfile
O processo de build consiste em:
1.  **Build Stage:** Compilação da aplicação utilizando Maven 3.9 e Eclipse Temurin 21.
2.  **Run Stage:** Execução do artefato `.jar` em uma imagem leve JRE baseada em Jammy.

```dockerfile
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-jammy
EXPOSE 8080
COPY --from=build /target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
