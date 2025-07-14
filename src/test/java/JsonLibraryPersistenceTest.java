import it.unical.gestorelibreria.controller.LibraryManagerInstance;
import it.unical.gestorelibreria.model.Book;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.state.ToReadState;
import it.unical.gestorelibreria.state.ReadState;
import it.unical.gestorelibreria.state.ReadingInProgressState;
import it.unical.gestorelibreria.persistence.JsonLibraryPersistence;
import it.unical.gestorelibreria.filter.BookFilterHandler;
import it.unical.gestorelibreria.filter.AuthorFilter;

import org.junit.jupiter.api.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JsonLibraryPersistenceTest {

    private LibraryManagerInstance manager;

    @BeforeEach
    void setUp() {
        manager = LibraryManagerInstance.INSTANCE;
        manager.clearLibrary(); // svuota cache e file
    }

    @AfterEach
    void tearDown() {
        manager.clearLibrary();
        File jsonFile = new File("library.json");
        if (jsonFile.exists())
            jsonFile.delete();
    }

    @Test
    @DisplayName("Persistenza corretta di 100 libri diversi")
    void testPersistenceOfMultipleBooks() {
        Set<String> isbns = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            String isbn = generateUniqueIsbn13(isbns);
            Book book = new Book(
                    "Titolo " + i,
                    "Autore " + (i % 10),
                    isbn,
                    "Genere" + (i % 5),
                    (i % 5) + 1,
                    new ToReadState()
            );
            manager.addBook(book);
        }

        List<IBook> loaded = JsonLibraryPersistence.INSTANCE.loadLibrary(null);
        assertEquals(100, loaded.size());

        Set<String> loadedIsbns = loaded.stream().map(IBook::getIsbn).collect(Collectors.toSet());
        assertTrue(loadedIsbns.containsAll(isbns));
    }

    @Test
    @DisplayName("Rimozione libro dalla persistenza")
    void testRemoveBookPersists() {
        String isbn = "9780132350884";

        Book book = new Book("Test Remove", "Autore", isbn, "Saggio", 4, new ReadState());
        manager.addBook(book);
        assertEquals(1, manager.getBooks().size(), "Libro non aggiunto correttamente");

        manager.removeBook(book);
        assertEquals(0, manager.getBooks().size(), "Libro non rimosso correttamente");

        List<IBook> loaded = JsonLibraryPersistence.INSTANCE.loadLibrary(null);
        assertTrue(loaded.isEmpty(), "La persistenza non è stata aggiornata correttamente");
    }

    // generazione di ISBN validi univoci
    private String generateUniqueIsbn13(Set<String> existing) {
        String isbn;
        do {
            isbn = generateIsbn13FromSeed(new Random().nextInt(1_000_000));
        } while (existing.contains(isbn));
        existing.add(isbn);
        return isbn;
    }

    private String generateIsbn13FromSeed(int seed) {
        String base = String.format("978%09d", seed);
        int check = calculateIsbn13CheckDigit(base);
        return base + check;
    }

    private int calculateIsbn13CheckDigit(String base12) {
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = base12.charAt(i) - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int mod = sum % 10;
        return (mod == 0) ? 0 : 10 - mod;
    }
}
