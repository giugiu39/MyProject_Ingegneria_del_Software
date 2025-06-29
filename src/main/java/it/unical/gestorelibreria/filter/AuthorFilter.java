package it.unical.gestorelibreria.filter;

import it.unical.gestorelibreria.model.IBook;

import java.util.List;
import java.util.stream.Collectors;

public class AuthorFilter extends BookFilter {

    private final String author;

    public AuthorFilter(String author) {
        this.author = author;
    }

    @Override
    protected List<IBook> applyFilter(List<IBook> books) {
        return books.stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }
}
