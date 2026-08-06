package com.nicolasDomingos.biblioteca.dao;

import com.nicolasDomingos.biblioteca.model.Book;
import com.nicolasDomingos.biblioteca.model.Loan;
import com.nicolasDomingos.biblioteca.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanDAO {
    private final Connection connection;
    private final BookDAO bookDAO;
    private final UserDAO userDAO;

    public LoanDAO(Connection connection, BookDAO bookDAO, UserDAO userDAO) {
        this.connection = connection;
        this.bookDAO = bookDAO;
        this.userDAO = userDAO;
    }
    public void saveLoan(Loan loan) throws SQLException {
        String sql = "INSERT INTO loans (book_isbn, user_cpf, loan_date, due_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, loan.getBook().getIsbn());
            stmt.setString(2, loan.getUser().getCpf());
            stmt.setString(3, loan.getLoanDate().toString());
            stmt.setString(4, loan.getDueDate().toString());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                loan.setId(generatedKeys.getLong(1));
            }
        }
    }
    public int countActiveLoansByUser(String cpf) throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans WHERE user_cpf = ? AND return_date IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
    public void updateReturnDate(Long loanId, LocalDate returnDate) throws SQLException {
        String sql = "UPDATE loans SET return_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, returnDate.toString());
            stmt.setLong(2, loanId);
            stmt.executeUpdate();
        }
    }
    public boolean doesUserHaveActiveLoans(String cpf) throws SQLException {
        String sql = "SELECT 1 FROM loans WHERE user_cpf = ? AND return_date IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean doesBookHaveActiveLoans(String isbn) throws SQLException {
        String sql = "SELECT 1 FROM loans WHERE book_isbn = ? AND return_date IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }
    public Loan findActiveLoan(String userCpf, String bookIsbn) throws SQLException {
        String sql = "SELECT * FROM loans WHERE user_cpf = ? AND book_isbn = ? AND return_date IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userCpf);
            stmt.setString(2, bookIsbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Book book = bookDAO.searchByISBN(bookIsbn);
                    User user = userDAO.searchByCpf(userCpf);
                    Loan loan = new Loan(book, user, LocalDate.parse(rs.getString("loan_date")));
                    loan.setId(rs.getLong("id"));
                    return loan;
                }
            }
        }
        return null;
    }
    public boolean doesUserHaveActiveLoanForBook(String userCPF, String bookIsbn) throws SQLException {
        String sql = "SELECT 1 FROM loans WHERE user_cpf = ? AND book_isbn = ? AND return_date IS NULL";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userCPF);
            stmt.setString(2, bookIsbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        }
        return false;
    }
    public List<Loan> findAllActiveLoanByUser(String userCpf) throws SQLException {
        String sql = "SELECT * FROM loans WHERE user_cpf = ? AND return_date IS NULL";
        List<Loan> loans = new ArrayList<>();
        User user = userDAO.searchByCpf(userCpf);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userCpf);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = bookDAO.searchByISBN(rs.getString("book_isbn"));
                    Loan loan = new Loan(book, user, LocalDate.parse(rs.getString("loan_date")));
                    loan.setId(rs.getLong("id"));
                    loans.add(loan);
                }
            }
        }
        return loans;
    }
    public List<Loan> findActiveLoansByBook(String bookIsbn) throws SQLException {
        String sql = "SELECT * FROM loans WHERE book_isbn = ? AND return_date IS NULL";
        List<Loan> loans = new ArrayList<>();
        Book book = bookDAO.searchByISBN(bookIsbn);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bookIsbn);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = userDAO.searchByCpf(rs.getString("user_cpf"));
                    Loan loan = new Loan(book, user, LocalDate.parse(rs.getString("loan_date")));
                    loan.setId(rs.getLong("id"));
                    loans.add(loan);
                }
            }
        }
        return loans;
    }
    public List<Loan> findAllLoansByBook(String bookIsbn) throws SQLException {
        String sql = "SELECT * FROM loans WHERE book_isbn = ?";
        List<Loan> loans = new ArrayList<>();
        Book book = bookDAO.searchByISBN(bookIsbn);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, bookIsbn);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User user = userDAO.searchByCpf(rs.getString("user_cpf"));
                    Loan loan = new Loan(rs.getLong("id"),
                            book, user, LocalDate.parse(rs.getString("loan_date")),
                            LocalDate.parse(rs.getString("due_date")),
                            parseDateOrNull(rs.getString("return_date")));
                    loans.add(loan);
                }
            }
        }
        return loans;
    }
    private LocalDate parseDateOrNull(String value) {
        return (value == null) ? null : LocalDate.parse(value);
    }
}
