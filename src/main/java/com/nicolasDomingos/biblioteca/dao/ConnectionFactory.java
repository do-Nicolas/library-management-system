package com.nicolasDomingos.biblioteca.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private static final String URL = "jdbc:sqlite:biblioteca.db";
    public static Connection criarConexao() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
