package it.unical.gestorelibreria.memento;

import it.unical.gestorelibreria.model.IBook;

import java.util.ArrayList;
import java.util.List;

public class LibraryMemento {

    private final List<IBook> booksSnapshot;

    public LibraryMemento(List<IBook> books) {
        // Copia stato attuale
        this.booksSnapshot = new ArrayList<>(books);
    }

    public List<IBook> getSavedState() {
        return new ArrayList<>(booksSnapshot);
    }
}
