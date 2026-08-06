package com.nicolasDomingos.biblioteca.dao;

import com.nicolasDomingos.biblioteca.exception.BookNotFoundException;
import com.nicolasDomingos.biblioteca.model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private final Connection connection;

    public BookDAO(Connection connection) {
        this.connection = connection;
    }

    public void saveBook(String title, String author, String isbn, int totalqtt) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, author, total_qtt, available_qtt) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.setString(2, title);
            stmt.setString(3, author);
            stmt.setInt(4, totalqtt);
            stmt.setInt(5, totalqtt);
            stmt.executeUpdate();
        }
    }
    public void removeBook(String isbn) throws SQLException {
        String sql = "DELETE FROM books WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            stmt.executeUpdate();
        }
    }

    public Book searchByISBN(String isbn) throws SQLException {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try(ResultSet rs = stmt.executeQuery();){
                if (rs.next()) {
                    Book book = new Book(rs.getString("title"), rs.getString("author"), rs.getString("isbn"), rs.getInt("total_qtt"));
                    book.setAvailableqtt(rs.getInt("available_qtt"));
                    return book;
                }
            }
            return null;
        }
    }

    public List<Book> listAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(rs.getString("title"), rs.getString("author"), rs.getString("isbn"), rs.getInt("total_qtt"));
                book.setAvailableqtt(rs.getInt("available_qtt"));
                books.add(book);
            }
        }
        return books;
    }
    public List<Book> listAllAvailableBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE available_qtt > 0";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(rs.getString("title"), rs.getString("author"), rs.getString("isbn"), rs.getInt("total_qtt"));
                book.setAvailableqtt(rs.getInt("available_qtt"));
                books.add(book);
            }
        }
        return books;
    }
    public void updateAvailableQtt(String isbn, int novaQtt) throws SQLException {
        String sql = "UPDATE books SET available_qtt = ? WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, novaQtt);
            stmt.setString(2, isbn);
            stmt.executeUpdate();
        }
    }
    public boolean existsByIsbn(String isbn) throws SQLException {
        String sql = "SELECT 1 FROM books WHERE isbn = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}