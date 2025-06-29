package it.unical.gestorelibreria.sort;

import it.unical.gestorelibreria.model.IBook;
import java.util.List;
import java.util.stream.Collectors;

public class SortByTitle implements SortStrategy {

    @Override
    public List<IBook> sort(List<IBook> books) {
        return books.stream()
                .sorted((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()))
                .collect(Collectors.toList());
    }
}
