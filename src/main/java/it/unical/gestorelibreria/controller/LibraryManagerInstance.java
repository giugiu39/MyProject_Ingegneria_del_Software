package it.unical.gestorelibreria.controller;

import it.unical.gestorelibreria.memento.LibraryCareTaker;
import it.unical.gestorelibreria.memento.LibraryMemento;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.persistence.PersistenceStrategy;
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
    private PersistenceStrategy persistenceStrategy;

    public void setSortStrategy(SortStrategy strategy) {
        this.sortStrategy = strategy;
    }

    public List<IBook> getSortedBooks() {
        if (sortStrategy == null) {
            return getBooks();
        }
        return sortStrategy.sort(getBooks());
    }

    public void setPersistenceStrategy(PersistenceStrategy strategy) {
        this.persistenceStrategy = strategy;
    }

    public void saveLibrary(String filepath) throws Exception {
        if (persistenceStrategy == null) {
            throw new IllegalStateException("Persistence strategy non impostata");
        }
        persistenceStrategy.save(books, filepath);
    }

    public void loadLibrary(String filepath) throws Exception {
        if (persistenceStrategy == null) {
            throw new IllegalStateException("Persistence strategy non impostata");
        }
        List<IBook> loadedBooks = persistenceStrategy.load(filepath);
        books.clear();
        books.addAll(loadedBooks);
    }

    // Aggiunge un libro e salva lo stato
    public void addBook(IBook book) {
        caretaker.saveState(new LibraryMemento(books)); // Salva prima di modificare
        books.add(book);
    }

    // Rimuove un libro e salva lo stato
    public void removeBook(IBook book) {
        caretaker.saveState(new LibraryMemento(books));
        books.remove(book);
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
            return true;
        }
        return false;
    }

    public boolean redo() {
        LibraryMemento next = caretaker.redo();
        if (next != null) {
            books.clear();
            books.addAll(next.getSavedState());
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
