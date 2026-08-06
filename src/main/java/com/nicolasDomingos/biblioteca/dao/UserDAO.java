package com.nicolasDomingos.biblioteca.dao;

import com.nicolasDomingos.biblioteca.exception.DuplicateCpfException;
import com.nicolasDomingos.biblioteca.exception.UserNotFoundException;
import com.nicolasDomingos.biblioteca.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private final Connection connection;
    public UserDAO(Connection connection){
        this.connection = connection;
    }
    public void saveUser(String cpf, String name) throws SQLException, DuplicateCpfException {
        String sql = "INSERT INTO users (cpf, name) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            stmt.setString(2, name);
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                throw new DuplicateCpfException("CPF já existe no sistema");
            }
            throw e;
        }
    }
    public void removeUser(String cpf) throws SQLException{
            String sql = "DELETE FROM users WHERE cpf = ?";
            try(PreparedStatement stmt = connection.prepareStatement(sql)){
                stmt.setString(1, cpf);
                stmt.executeUpdate();
            }
    }
    public User searchByCpf(String cpf) throws SQLException{
        String sql = "SELECT * FROM users WHERE cpf = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getString("cpf"), rs.getString("name"));
                }
            }
        }
        return null;
    }
    public boolean existsByCpf(String cpf) throws SQLException {
        String sql = "SELECT 1 FROM users WHERE cpf = ? LIMIT 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

}
