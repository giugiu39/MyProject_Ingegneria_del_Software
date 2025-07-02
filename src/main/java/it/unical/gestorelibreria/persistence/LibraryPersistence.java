package it.unical.gestorelibreria.persistence;

import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.filter.BookFilterHandler;

import java.util.List;

public interface LibraryPersistence {

    void saveBook(IBook book);

    void removeBook(IBook book);

    /**
     * Carica la libreria filtrata secondo il filtro, senza ordinamento.
     * Se filtro è null, restituisce tutti.
     */
    List<IBook> loadLibrary(BookFilterHandler filter);

    void saveLibrary(List<IBook> books);
}
