package com.nicolasDomingos.biblioteca.model;

public class User {
    private final String cpf;
    private String name;

    public User(String cpf, String name) {
        this.cpf = cpf;
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}