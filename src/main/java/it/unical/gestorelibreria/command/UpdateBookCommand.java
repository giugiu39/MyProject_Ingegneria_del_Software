package it.unical.gestorelibreria.command;

import it.unical.gestorelibreria.controller.LibraryManagerInstance;
import it.unical.gestorelibreria.model.IBook;

public class UpdateBookCommand implements Command {

    private final IBook oldBook;
    private final IBook newBook;

    public UpdateBookCommand(IBook oldBook, IBook newBook) {
        this.oldBook = oldBook;
        this.newBook = newBook;
    }

    @Override
    public void execute() {
        LibraryManagerInstance.INSTANCE.updateBook(oldBook, newBook);
    }

}
