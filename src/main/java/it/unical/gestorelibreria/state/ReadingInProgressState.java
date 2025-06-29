package it.unical.gestorelibreria.state;

import it.unical.gestorelibreria.model.Book;

public class ReadingInProgressState implements ReadingState {

    @Override
    public void nextState(Book book) {
        book.setState(new ReadState());
    }

    @Override
    public void previousState(Book book) {
        book.setState(new ToReadState());
    }

    @Override
    public String getStateName() {
        return "READING";
    }
}
