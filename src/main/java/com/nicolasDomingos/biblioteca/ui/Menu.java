package com.nicolasDomingos.biblioteca.ui;

import com.nicolasDomingos.biblioteca.exception.*;
import com.nicolasDomingos.biblioteca.model.Book;
import com.nicolasDomingos.biblioteca.model.Loan;
import com.nicolasDomingos.biblioteca.service.Library;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final Library library;
    private final Scanner scanner;

    public Menu(Library library) {
        this.library = library;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcao;
        do {
            exibirMenuPrincipal();
            opcao = lerOpcao();

            switch (opcao) {
                case 1 -> cadastrarUsuario();
                case 2 -> cadastrarLivro();
                case 3 -> registrarEmprestimo();
                case 4 -> registrarDevolucao();
                case 5 -> listarTodosLivros();
                case 6 -> listarLivrosDisponiveis();
                case 7 -> listarEmprestimosAtivosDeUsuario();
                case 8 -> removerUsuario();
                case 9 -> removerLivro();
                case 0 -> System.out.println("Encerrando...");
                default -> System.out.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void exibirMenuPrincipal() {
        System.out.println("\n=== BIBLIOTECA ===");
        System.out.println("1 - Cadastrar usuário");
        System.out.println("2 - Cadastrar livro");
        System.out.println("3 - Registrar empréstimo");
        System.out.println("4 - Registrar devolução");
        System.out.println("5 - Listar todos os livros");
        System.out.println("6 - Listar livros disponíveis");
        System.out.println("7 - Listar empréstimos ativos de um usuário");
        System.out.println("8 - Remover usuário");
        System.out.println("9 - Remover livro");
        System.out.println("0 - Sair");
        System.out.print("Escolha: ");
    }

    private int lerOpcao() {
        try {
            int opcao = Integer.parseInt(scanner.nextLine().trim());
            return opcao;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void cadastrarUsuario() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("CPF (somente números ou com pontuação): ");
        String cpf = scanner.nextLine().trim();

        try {
            library.createUser(cpf, nome);
            System.out.println("Usuário cadastrado com sucesso!");
        } catch (InvalidCpfException | DuplicateCpfException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco de dados.");
        }
    }

    private void cadastrarLivro() {
        System.out.print("Título: ");
        String titulo = scanner.nextLine().trim();

        System.out.print("Autor: ");
        String autor = scanner.nextLine().trim();

        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();

        System.out.print("Quantidade de exemplares: ");
        int quantidade = lerQuantidade();
        if (quantidade < 0) return;

        try {
            library.createBook(titulo, autor, isbn, quantidade);
            System.out.println("Livro cadastrado com sucesso!");
        } catch (DuplicateIsbnException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco de dados.");
        }
    }

    private int lerQuantidade() {
        try {
            int quantidade = Integer.parseInt(scanner.nextLine().trim());
            if (quantidade <= 0) {
                System.out.println("A quantidade deve ser maior que zero.");
                return -1;
            }
            return quantidade;
        } catch (NumberFormatException e) {
            System.out.println("Quantidade inválida.");
            return -1;
        }
    }

    private void registrarEmprestimo() {
        System.out.print("CPF do usuário: ");
        String cpf = scanner.nextLine().trim();

        System.out.print("ISBN do livro: ");
        String isbn = scanner.nextLine().trim();

        try {
            library.registerLoan(cpf, isbn);
            System.out.println("Empréstimo registrado com sucesso!");
        } catch (BookNotFoundException | UserNotFoundException | BookUnavailableException | LoanLimitExceededException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco de dados.");
        }
    }

    private void registrarDevolucao() {
        System.out.print("CPF do usuário: ");
        String cpf = scanner.nextLine().trim();

        System.out.print("ISBN do livro: ");
        String isbn = scanner.nextLine().trim();

        try {
            library.returnBook(cpf, isbn);
            System.out.println("Devolução registrada com sucesso!");
        } catch (LoanNotFoundException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco de dados.");
        }
    }
    private void listarTodosLivros() {
        try {
            List<Book> livros = library.listAllBooks();
            exibirLivros(livros);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco de dados.");
        }
    }

    private void listarLivrosDisponiveis() {
        try {
            List<Book> livros = library.listAvailableBooks();
            exibirLivros(livros);
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco de dados.");
        }
    }

    private void exibirLivros(List<Book> livros) {
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro encontrado.");
            return;
        }
        for (Book livro : livros) {
            System.out.printf("[%s] %s — %s (disponível: %d/%d)%n",
                    livro.getIsbn(), livro.getTitle(), livro.getAuthor(),
                    livro.getAvailableqtt(), livro.getTotalqtt());
        }
    }

    private void listarEmprestimosAtivosDeUsuario() {
        System.out.print("CPF do usuário: ");
        String cpf = scanner.nextLine().trim();

        try {
            List<Loan> emprestimos = library.listActiveLoansByUser(cpf);
            if (emprestimos.isEmpty()) {
                System.out.println("Nenhum empréstimo ativo para esse usuário.");
                return;
            }
            for (Loan emprestimo : emprestimos) {
                System.out.printf("Livro: %s — Emprestado em: %s — Prazo: %s%n",
                        emprestimo.getBook().getTitle(), emprestimo.getLoanDate(), emprestimo.getDueDate());
            }
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco de dados.");
        }
    }
    private void removerUsuario() {
        System.out.print("CPF do usuário: ");
        String cpf = scanner.nextLine().trim();

        try {
            library.deleteUser(cpf);
            System.out.println("Usuário removido com sucesso!");
        } catch (UserNotFoundException | ActiveLoanExistsException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco de dados.");
        }
    }

    private void removerLivro() {
        System.out.print("ISBN do livro: ");
        String isbn = scanner.nextLine().trim();

        try {
            library.deleteBook(isbn);
            System.out.println("Livro removido com sucesso!");
        } catch (BookNotFoundException | ActiveLoanExistsException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro ao acessar o banco de dados.");
        }
    }
}