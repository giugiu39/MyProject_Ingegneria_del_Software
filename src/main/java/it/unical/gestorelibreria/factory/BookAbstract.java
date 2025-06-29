package it.unical.gestorelibreria.factory;

import it.unical.gestorelibreria.model.IBook;

public abstract class BookAbstract implements IBook {

    //factory method
    protected abstract IBook createBook(String title, String author, String isbn, String genre, int rating);
}
