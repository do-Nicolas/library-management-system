package com.nicolasDomingos.biblioteca.model;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private int totalqtt;
    private int availableqtt;

    public Book(String title, String author, String isbn, int totalqtt) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalqtt = totalqtt;
        availableqtt = totalqtt;
    }
    public boolean isAvailable(){
        return (availableqtt > 0);
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getTotalqtt() {
        return totalqtt;
    }

    public void setTotalqtt(int totalqtt) {
        int difference = this.totalqtt - totalqtt;
        subAvailableqtt(difference);
        this.totalqtt = totalqtt;
    }

    public int getAvailableqtt() {
        return availableqtt;
    }

    public void decreaseAvailableqtt() {
        availableqtt--;
        if(availableqtt < 0) availableqtt = 0;
    }

    public void subAvailableqtt(int subBy) {
        availableqtt -= subBy;
        if(availableqtt < 0) availableqtt = 0;
    }

    public void setAvailableqtt(int availableQtt) {
        this.availableqtt = availableQtt;
        if(availableqtt < 0) availableqtt = 0;
    }
    public void increaseAvailableqtt(){
        this.availableqtt++;
        if(availableqtt > totalqtt) availableqtt = totalqtt;
    }
}
