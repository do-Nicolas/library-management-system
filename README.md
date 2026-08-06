# Library Management System

Sistema de gerenciamento de biblioteca em Java com persistência em SQLite, CRUD de livros e usuários, controle de empréstimos/devoluções e validação de CPF.

Projeto pessoal desenvolvido para praticar orientação a objetos, persistência de dados com JDBC e arquitetura em camadas (UI, Service, DAO, Banco de Dados).

## Funcionalidades

- Cadastro, listagem e remoção de usuários (com validação de CPF)
- Cadastro, listagem e remoção de livros
- Registro de empréstimos, com controle de disponibilidade e limite por usuário
- Registro de devoluções
- Consulta de livros disponíveis e empréstimos ativos por usuário
- Cálculo de multa por atraso na devolução (em desenvolvimento)
- Persistência local em SQLite; o banco é criado automaticamente na primeira execução

## Tecnologias

- Java 25
- Maven (gerenciamento de dependências e build)
- JDBC + SQLite (persistência)

## Estrutura do projeto

```
src/main/java/com/nicolasDomingos/biblioteca/
├── Main.java                 # ponto de entrada da aplicação
├── model/                    # entidades: Book, User, Loan
├── service/                  # regras de negócio: Library
├── dao/                      # acesso a dados: BookDAO, UserDAO, LoanDAO, ConnectionFactory
├── exception/                # exceções customizadas de regra de negócio
├── util/                     # utilitários: CpfValidator
└── ui/                       # interação com o usuário: Menu
```

A aplicação segue uma separação em camadas: a UI (menu) só conversa com o Service (Library), que aplica as regras de negócio e delega o acesso a dados para os DAOs. Isso mantém cada camada com uma única responsabilidade.

## Como executar

Pré-requisitos: JDK 25 e Maven instalados.

```
git clone https://github.com/do-Nicolas/library-management-system.git
cd library-management-system

mvn clean compile

mvn exec:java -Dexec.mainClass="com.nicolasDomingos.biblioteca.Main"
```

O arquivo biblioteca.db (SQLite) é criado automaticamente na primeira execução, junto com as tabelas necessárias.

## Regras de negócio

### Livros
- Um livro não pode ser cadastrado com ISBN duplicado.
- A quantidade disponível nunca pode ser maior que a quantidade total.
- A quantidade disponível nunca pode ser negativa.
- Não é possível excluir um livro que tenha empréstimos ativos.

### Usuários
- Um usuário não pode ser cadastrado com CPF inválido.
- Um usuário não pode ser cadastrado com CPF duplicado.
- Não é possível excluir um usuário que tenha empréstimos ativos.

### Empréstimos
- Um livro só pode ser emprestado se houver exemplar disponível.
- Um usuário não pode ter mais que 3 empréstimos ativos ao mesmo tempo.
- O prazo de devolução é calculado automaticamente (7 dias a partir do empréstimo).
- Multa por atraso na devolução.

### Devoluções
- Só é possível devolver um empréstimo ativo.
- Ao devolver, a quantidade disponível do livro é atualizada.

## Decisões de design

- Controle de estoque por título, não por exemplar: o Book guarda uma quantidade total e disponível, em vez de rastrear cada cópia física individualmente. Escolha consciente por simplicidade, adequada ao escopo do projeto.
- CPF como identificador do usuário: optou-se por usar o CPF como chave primária, em vez de um ID técnico gerado pelo banco, já que é um identificador natural e único no mundo real.
- Exceções customizadas por regra de negócio: cada violação de regra (CPF inválido, ISBN duplicado, livro indisponível etc.) tem sua própria exceção, lançada na camada de serviço e tratada na camada de UI com mensagens amigáveis.
- Injeção de dependência manual: os DAOs recebem a Connection (e, quando necessário, outros DAOs) via construtor, em vez de instanciar suas dependências internamente.

## Autor

Nicolas Domingos
https://github.com/do-Nicolas
