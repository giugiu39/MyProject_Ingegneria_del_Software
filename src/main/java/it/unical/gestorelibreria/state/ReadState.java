package it.unical.gestorelibreria.state;

import it.unical.gestorelibreria.model.Book;

public class ReadState implements ReadingState {

    @Override
    public void nextState(Book book) {
        System.out.println("Già nello stato finale: Letto.");
    }

    @Override
    public void previousState(Book book) {
        book.setState(new ReadingInProgressState());
    }

    @Override
    public String getStateName() {
        return "READ";
    }
}
