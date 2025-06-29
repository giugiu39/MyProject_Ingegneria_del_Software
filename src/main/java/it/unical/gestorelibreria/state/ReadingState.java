package it.unical.gestorelibreria.state;

import it.unical.gestorelibreria.model.Book;

public interface ReadingState {
    void nextState(Book book);      // Passa allo stato successivo
    void previousState(Book book);  // Torna allo stato precedente
    String getStateName();     // Nome leggibile dello stato
}
