package it.unical.gestorelibreria.filter;

import it.unical.gestorelibreria.model.IBook;
import java.util.List;
import java.util.stream.Collectors;

public class TextFilter extends BookFilter {
    private final String text;
    public TextFilter(String text) { this.text = text.toLowerCase(); }
    @Override
    protected List<IBook> applyFilter(List<IBook> books) {
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(text)
                        || b.getAuthor().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }
}
