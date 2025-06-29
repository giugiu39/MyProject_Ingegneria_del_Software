package it.unical.gestorelibreria.state;

import it.unical.gestorelibreria.model.Book;

public class ToReadState implements ReadingState {

    @Override
    public void nextState(Book book) {
        book.setState(new ReadingInProgressState());
    }

    @Override
    public void previousState(Book book) {
        System.out.println("Già nello stato iniziale: Da leggere.");
    }

    @Override
    public String getStateName() {
        return "TO_READ";
    }
}
