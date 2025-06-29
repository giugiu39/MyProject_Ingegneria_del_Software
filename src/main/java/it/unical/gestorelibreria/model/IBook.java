package it.unical.gestorelibreria.model;

public interface IBook {
    String getTitle();
    String getAuthor();
    String getIsbn();
    String getGenre();
    int getRating();

    void setTitle(String title);
    void setAuthor(String author);
    void setIsbn(String isbn);
    void setGenre(String genre);
    void setRating(int rating);

}
