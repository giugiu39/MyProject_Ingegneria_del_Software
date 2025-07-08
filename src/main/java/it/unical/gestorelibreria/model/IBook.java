package it.unical.gestorelibreria.model;

import it.unical.gestorelibreria.state.ReadingState;

public interface IBook {
    String getTitle();
    String getAuthor();
    String getIsbn();
    String getGenre();
    int getRating();
    ReadingState getState();
    String getStateName();

    void setTitle(String title);
    void setAuthor(String author);
    void setIsbn(String isbn);
    void setGenre(String genre);
    void setRating(int rating);
    void setState(ReadingState state);

}
