package it.unical.gestorelibreria.sort;

import it.unical.gestorelibreria.model.IBook;
import java.util.List;
import java.util.stream.Collectors;

public class SortByRating implements SortStrategy {

    @Override
    public List<IBook> sort(List<IBook> books) {
        return books.stream()
                .sorted((b1, b2) -> Integer.compare(b1.getRating(), b2.getRating()))
                .collect(Collectors.toList());
    }
}
