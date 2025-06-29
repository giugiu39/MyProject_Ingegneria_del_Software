package it.unical.gestorelibreria.filter;

import it.unical.gestorelibreria.model.IBook;

import java.util.List;
import java.util.stream.Collectors;

public class GenreFilter extends BookFilter {

    private final String genre;

    public GenreFilter(String genre) {
        this.genre = genre;
    }

    @Override
    protected List<IBook> applyFilter(List<IBook> books) {
        return books.stream()
                .filter(book -> book.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }
}
