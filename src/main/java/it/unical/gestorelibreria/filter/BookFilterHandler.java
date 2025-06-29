package it.unical.gestorelibreria.filter;

import it.unical.gestorelibreria.model.IBook;

import java.util.List;

public interface BookFilterHandler {
    void setNext(BookFilterHandler next);
    List<IBook> handle(List<IBook> books);
}
