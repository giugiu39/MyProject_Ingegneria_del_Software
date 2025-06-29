package it.unical.gestorelibreria.filter;

import it.unical.gestorelibreria.model.IBook;

import java.util.List;
import java.util.stream.Collectors;

public class RatingFilter extends BookFilter {

    private final int minRating;

    public RatingFilter(int minRating) {
        this.minRating = minRating;
    }

    @Override
    protected List<IBook> applyFilter(List<IBook> books) {
        return books.stream()
                .filter(book -> book.getRating() >= minRating)
                .collect(Collectors.toList());
    }
}
