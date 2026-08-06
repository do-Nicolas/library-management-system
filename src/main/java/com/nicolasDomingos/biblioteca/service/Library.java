package com.nicolasDomingos.biblioteca.service;
import com.nicolasDomingos.biblioteca.exception.*;
import com.nicolasDomingos.biblioteca.dao.BookDAO;
import com.nicolasDomingos.biblioteca.dao.ConnectionFactory;
import com.nicolasDomingos.biblioteca.dao.LoanDAO;
import com.nicolasDomingos.biblioteca.dao.UserDAO;
import com.nicolasDomingos.biblioteca.model.Book;
import com.nicolasDomingos.biblioteca.model.Loan;
import com.nicolasDomingos.biblioteca.model.User;
import com.nicolasDomingos.biblioteca.util.CpfValidator;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

public class Library {
    BookDAO bookDAO;
    LoanDAO loanDAO;
    UserDAO userDAO;

    public Library()  {
        try {
            startDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void startDB() throws SQLException {
        Connection connection = ConnectionFactory.criarConexao();
        criarTabelas(connection);
        bookDAO = new BookDAO(connection);
        userDAO = new UserDAO(connection);
        loanDAO = new LoanDAO(connection, bookDAO, userDAO);
    }

    private void criarTabelas(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS books (
                isbn TEXT PRIMARY KEY,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                total_qtt INTEGER NOT NULL,
                available_qtt INTEGER NOT NULL
            )
        """);
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS users (
                cpf TEXT PRIMARY KEY,
                name TEXT NOT NULL
            )
        """);
            stmt.execute("""
            CREATE TABLE IF NOT EXISTS loans (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                book_isbn TEXT NOT NULL,
                user_cpf TEXT NOT NULL,
                loan_date TEXT NOT NULL,
                due_date TEXT NOT NULL,
                return_date TEXT,
                FOREIGN KEY (book_isbn) REFERENCES books(isbn),
                FOREIGN KEY (user_cpf) REFERENCES users(cpf)
            )
        """);
        }
    }
    public void createUser(String cpf, String name) throws SQLException, InvalidCpfException, DuplicateCpfException {
        if(!CpfValidator.isValid(cpf)) throw new InvalidCpfException("CPF INVALIDO");
        if (userDAO.existsByCpf(cpf)) throw new DuplicateCpfException("CPF já existe no sistema");
        userDAO.saveUser(cpf, name);
    }
    public void deleteUser(String cpf) throws SQLException, ActiveLoanExistsException, UserNotFoundException {
        if(!userDAO.existsByCpf(cpf)) throw new UserNotFoundException("Nenhum usuário ativo encontrado para esse CPF");
        if(loanDAO.doesUserHaveActiveLoans(cpf)) throw new ActiveLoanExistsException("Impossivel deletar usuario com emprestimos ativos");
        userDAO.removeUser(cpf);
    }
    public void createBook(String title, String author, String isbn, int totalqtt) throws SQLException, DuplicateIsbnException {
        if(bookDAO.existsByIsbn(isbn)) throw new DuplicateIsbnException("ISBN já existe no sistema");
        bookDAO.saveBook(title, author, isbn, totalqtt);
    }
    public void deleteBook(String isbn) throws SQLException, ActiveLoanExistsException, BookNotFoundException {
        if(!bookDAO.existsByIsbn(isbn)) throw new BookNotFoundException("Nenhum livro ativo encontrado para esse isbn");
        if(loanDAO.doesBookHaveActiveLoans(isbn)) throw new ActiveLoanExistsException("Impossivel deletar livro com emprestimos ativos");
        bookDAO.removeBook(isbn);
    }
    public void registerLoan(String userCpf, String bookIsbn) throws SQLException, BookUnavailableException, LoanLimitExceededException, UserNotFoundException, BookNotFoundException {
        Book book = bookDAO.searchByISBN(bookIsbn);
        User user = userDAO.searchByCpf(userCpf);
        if (book == null) throw new BookNotFoundException("Nenhum livro encontrado para esse isbn");
        if (user == null) throw new UserNotFoundException("Nenhum usuário ativo encontrado para esse cpf");
        if(!book.isAvailable()) throw new BookUnavailableException("O livro não está disponivel para emprestismo");
        if(loanDAO.countActiveLoansByUser(userCpf) >= Loan.LOAN_LIMIT) throw new LoanLimitExceededException("limite de emprestimo alcançado");

        LocalDate today = LocalDate.now();
        Loan loan = new Loan(book, user, today);
        loanDAO.saveLoan(loan);
        book.decreaseAvailableqtt();
        bookDAO.updateAvailableQtt(book.getIsbn(), book.getAvailableqtt());
    }
    public void returnBook(String userCpf, String bookIsbn) throws SQLException, LoanNotFoundException {
        Loan loan = loanDAO.findActiveLoan(userCpf, bookIsbn);
        if (loan == null) throw new LoanNotFoundException("Nenhum empréstimo ativo encontrado para esse usuário e livro");
        loan.setReturnToday();
        loanDAO.updateReturnDate(loan.getId(), loan.getReturnDate());

        Book book = loan.getBook();
        book.increaseAvailableqtt();
        bookDAO.updateAvailableQtt(book.getIsbn(), book.getAvailableqtt());
    }
    public List<Book> listAllBooks() throws SQLException {
        return bookDAO.listAllBooks();
    }

    public List<Book> listAvailableBooks() throws SQLException {
        return bookDAO.listAllAvailableBooks();
    }

    public List<Loan> listActiveLoansByUser(String cpf) throws SQLException {
        return loanDAO.findAllActiveLoanByUser(cpf);
    }


}
