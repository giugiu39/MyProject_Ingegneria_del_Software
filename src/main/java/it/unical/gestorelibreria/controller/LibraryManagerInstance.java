package it.unical.gestorelibreria.controller;

import it.unical.gestorelibreria.filter.BookFilterHandler;
import it.unical.gestorelibreria.memento.LibraryCareTaker;
import it.unical.gestorelibreria.memento.LibraryMemento;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.persistence.JsonLibraryPersistence;
import it.unical.gestorelibreria.sort.SortStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public enum LibraryManagerInstance {

    INSTANCE;

    private final List<IBook> books = new ArrayList<>();
    private final LibraryCareTaker caretaker = new LibraryCareTaker();
    private SortStrategy sortStrategy;
    private final JsonLibraryPersistence persistence = JsonLibraryPersistence.INSTANCE;

    // blocco di inizializzazione eseguito all'istanziazione di INSTANCE
    {
        List<IBook> loadedBooks = persistence.loadLibrary(null);
        books.addAll(loadedBooks);
    }

    public void setSortStrategy(SortStrategy strategy) {
        this.sortStrategy = strategy;
    }

    public List<IBook> getSortedBooks() {
        if (sortStrategy == null) {
            return getBooks();
        }
        return sortStrategy.sort(getBooks());
    }

    public void addBook(IBook book) {
        caretaker.saveState(new LibraryMemento(new ArrayList<>(books))); // salva snapshot prima
        books.add(book);
        persistence.saveLibrary(books);  // salva tutta la lista aggiornata
    }

    public void removeBook(IBook book) {
        caretaker.saveState(new LibraryMemento(new ArrayList<>(books))); // salva snapshot prima
        books.remove(book);
        persistence.saveLibrary(books);
    }

    public List<IBook> getBooks(BookFilterHandler filter) {
        return persistence.loadLibrary(filter);
    }

    // Restituisce una copia immutabile della lista dei libri
    public List<IBook> getBooks() {
        return Collections.unmodifiableList(books);
    }

    // Operazioni Undo/Redo
    public boolean undo() {
        LibraryMemento previous = caretaker.undo();
        if (previous != null) {
            books.clear();
            books.addAll(previous.getSavedState());
            persistence.saveLibrary(books);  // aggiorna file JSON
            return true;
        }
        return false;
    }

    public boolean redo() {
        LibraryMemento next = caretaker.redo();
        if (next != null) {
            books.clear();
            books.addAll(next.getSavedState());
            persistence.saveLibrary(books);
            return true;
        }
        return false;
    }

    public boolean canUndo() {
        return caretaker.canUndo();
    }

    public boolean canRedo() {
        return caretaker.canRedo();
    }

    public void clearLibrary() {
        caretaker.saveState(new LibraryMemento(books));
        books.clear();
        persistence.saveLibrary(books);
    }

    // Ricerca per titolo
    public List<IBook> searchByTitle(String partialTitle) {
        return books.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(partialTitle.toLowerCase()))
                .collect(Collectors.toList());
    }

    // Ricerca per autore
    public List<IBook> searchByAuthor(String partialAuthor) {
        return books.stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(partialAuthor.toLowerCase()))
                .collect(Collectors.toList());
    }

}
