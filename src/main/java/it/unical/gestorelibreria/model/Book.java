package it.unical.gestorelibreria.model;

import it.unical.gestorelibreria.factory.BookAbstract;
import it.unical.gestorelibreria.state.ReadingState;
import it.unical.gestorelibreria.state.ToReadState;

import java.util.Objects;

public class Book extends BookAbstract {

    private String title;
    private String author;
    private String isbn;
    private String genre;
    private int rating;
    private ReadingState state;

    public Book(String title, String author, String isbn, String genre, int rating) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.genre = genre;
        this.rating = rating;
        this.state = new ToReadState();
    }

    @Override
    protected Book createBook(String title, String author, String isbn, String genre, int rating) {
        return new Book(title, author, isbn, genre, rating);
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
    }

    public ReadingState getState() {
        return state;
    }

    public void next() {
        state.nextState(this);
    }

    public void previous() {
        state.previousState(this);
    }

    public String getStateName() {
        return state.getStateName();
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
