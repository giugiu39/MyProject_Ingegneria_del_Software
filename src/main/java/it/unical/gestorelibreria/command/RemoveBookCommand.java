package it.unical.gestorelibreria.command;

import it.unical.gestorelibreria.controller.LibraryManagerInstance;
import it.unical.gestorelibreria.model.IBook;

public class RemoveBookCommand implements Command {

    private final IBook book;

    public RemoveBookCommand(IBook book) {
        this.book = book;
    }

    @Override
    public void execute() {
        LibraryManagerInstance.INSTANCE.removeBook(book);
    }

    @Override
    public void undo() {
        LibraryManagerInstance.INSTANCE.addBook(book);
    }
}
