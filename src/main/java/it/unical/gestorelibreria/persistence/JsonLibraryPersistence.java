package it.unical.gestorelibreria.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.model.Book;
import it.unical.gestorelibreria.filter.BookFilterHandler;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public enum JsonLibraryPersistence implements LibraryPersistence {

    INSTANCE;

    private static final String FILE_PATH = "library.json";
    private final Set<IBook> cache;
    private final Gson gson;

    JsonLibraryPersistence() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        cache = loadFromDisk();
    }

    @Override
    public synchronized void saveBook(IBook book) {
        if (cache.contains(book)) throw new IllegalArgumentException("Book already exists");
        cache.add(book);
        saveToDisk();
    }

    @Override
    public synchronized void removeBook(IBook book) {
        if (!cache.contains(book)) throw new IllegalArgumentException("Book not found");
        cache.remove(book);
        saveToDisk();
    }

    @Override
    public synchronized List<IBook> loadLibrary(BookFilterHandler filter) {
        Set<IBook> booksToFilter = loadFromDisk();
        if (filter == null) {
            return new ArrayList<>(booksToFilter);
        } else {
            return filter.handle(new ArrayList<>(booksToFilter));
        }
    }

    private Set<IBook> loadFromDisk() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<List<Book>>(){}.getType();
            List<Book> loadedBooks = gson.fromJson(reader, new TypeToken<List<Book>>(){}.getType());
            return loadedBooks != null ? new HashSet<>(loadedBooks) : new HashSet<>();
        } catch (Exception e) {
            return new HashSet<>();
        }
    }

    public synchronized void saveLibrary(List<IBook> books) {
        cache.clear();
        cache.addAll(books);
        saveToDisk();
    }

    private void saveToDisk() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(cache, writer);
        } catch (Exception e) {
            throw new RuntimeException("Error saving JSON", e);
        }
    }
}
