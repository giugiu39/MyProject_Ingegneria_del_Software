package it.unical.gestorelibreria.command;

import it.unical.gestorelibreria.controller.LibraryManagerInstance;
import it.unical.gestorelibreria.model.IBook;

public class AddBookCommand implements Command {

    private final IBook book;

    public AddBookCommand(IBook book) {
        this.book = book;
    }

    @Override
    public void execute() {
        LibraryManagerInstance.INSTANCE.addBook(book);
    }

    @Override
    public void undo() {
        LibraryManagerInstance.INSTANCE.removeBook(book);
    }
}
