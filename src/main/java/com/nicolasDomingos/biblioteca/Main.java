package com.nicolasDomingos.biblioteca;
import com.nicolasDomingos.biblioteca.service.Library;
import com.nicolasDomingos.biblioteca.ui.Menu;

public class Main {
    public static void main(String[] args){
        Library library = new Library();
        Menu menu = new Menu(library);
        menu.iniciar();
    }
}
