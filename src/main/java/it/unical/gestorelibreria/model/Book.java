package it.unical.gestorelibreria.model;

import it.unical.gestorelibreria.factory.BookAbstract;
import it.unical.gestorelibreria.state.*;

import java.util.Objects;

public class Book extends BookAbstract {

    private String title;
    private String author;
    private String isbn;
    private String genre;
    private int rating;
    private transient ReadingState state;
    private String stateName;

    public Book(String title, String author, String isbn, String genre, int rating, ReadingState state) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.rating = rating;
        this.state = state;
        this.stateName = state.getStateName();
    }

    @Override
    protected Book createBook(String title, String author, String isbn, String genre, int rating, ReadingState state) {
        return new Book(title, author, isbn, genre, rating, state);
    }

    // Getters
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getIsbn() { return isbn; }

    @Override
    public String getGenre() {
        return genre;
    }

    @Override
    public int getRating() {
        return rating;
    }

    // Setters
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setState(ReadingState state) {
        this.state = state;
        this.stateName = state.getStateName();
    }

    public ReadingState getState() {
        // ricostruisco l'oggetto state da stateName
        return state = switch(stateName) {
            case "READING" -> new ReadingInProgressState();
            case "READ"    -> new ReadState();
            default        -> new ToReadState();
        };
    }

    public void next() {
        state.nextState(this);
    }

    public void previous() {
        state.previousState(this);
    }

    public String getStateName() {
        return stateName;
    }

    @Override
    public String toString() {
        return "[" + title + " by " + author + ", ISBN: " + isbn + ", Genre: " + genre + ", Rating: " + rating + ", State: " + getStateName() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book other)) return false;
        return Objects.equals(isbn, other.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

}
