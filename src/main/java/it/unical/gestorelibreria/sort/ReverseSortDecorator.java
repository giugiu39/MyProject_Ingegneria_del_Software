package it.unical.gestorelibreria.sort;

import it.unical.gestorelibreria.model.IBook;
import java.util.Collections;
import java.util.List;

public class ReverseSortDecorator implements SortStrategy {
    private final SortStrategy inner;
    public ReverseSortDecorator(SortStrategy inner) {
        this.inner = inner;
    }
    @Override
    public List<IBook> sort(List<IBook> books) {
        List<IBook> list = inner.sort(books);
        Collections.reverse(list);
        return list;
    }
    public SortStrategy getInner() { return inner; }
}
