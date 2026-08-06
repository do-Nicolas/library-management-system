package com.nicolasDomingos.biblioteca.model;

import java.time.LocalDate;


public class Loan {
    public static final int LOAN_PERIOD_DAYS = 7;
    public static final int LOAN_LIMIT = 3;

    private Long id;
    private final Book book;
    private final User user;
    private final LocalDate loanDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(Book book, User user, LocalDate loanDate) {
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.dueDate = loanDate.plusDays(LOAN_PERIOD_DAYS);
        this.returnDate = null;
    }
    public Loan(Long id, Book book, User user, LocalDate loanDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.book = book;
        this.user = user;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

        public LocalDate getReturnDate() {
        return returnDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }
    public void setReturnToday() {
        returnDate = LocalDate.now();
    }
    public void setReturn(LocalDate lD){
        returnDate = lD;
    }
    public boolean isOverdue(){
        return LocalDate.now().isAfter(dueDate);
    }
    public boolean isReturned(){
        return returnDate != null;
    }
}
