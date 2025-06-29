package it.unical.gestorelibreria.sort;

import it.unical.gestorelibreria.model.IBook;
import java.util.List;

public interface SortStrategy {
    List<IBook> sort(List<IBook> books);
}
