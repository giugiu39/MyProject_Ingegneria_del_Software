import it.unical.gestorelibreria.controller.LibraryManagerInstance;
import it.unical.gestorelibreria.model.Book;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.state.*;
import it.unical.gestorelibreria.utils.LibraryUtils;
import it.unical.gestorelibreria.utils.ISBNUtils;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LibraryManagerInstanceTest {

    private LibraryManagerInstance manager;

    @BeforeAll
    void beforeAll() {
        LibraryManagerInstance.INSTANCE.clearLibrary();
    }

    @AfterAll
    void afterAll() {
        LibraryManagerInstance.INSTANCE.clearLibrary();
    }

    @BeforeEach
    void setUp() {
        manager = LibraryManagerInstance.INSTANCE;
        manager.clearLibrary();
    }

    @AfterEach
    void tearDown() {
        manager.clearLibrary();
    }

    @Test
    @DisplayName("Aggiunta di 100 libri con titoli univoci e stati diversi")
    void testAdd100BooksWithUniqueIsbn() {
        int initialSize = manager.getBooks().size();

        ReadingState[] states = {
                new ToReadState(),
                new ReadingInProgressState(),
                new ReadState()
        };

        for (int i = 0; i < 100; i++) {
            String isbn = generateIsbn13(978000000000L + i); // definita più in basso

            Book book = new Book(
                    "Titolo " + i,
                    "Autore " + i,
                    isbn,
                    LibraryUtils.GENERI[i % LibraryUtils.GENERI.length],
                    (i % 5) + 1,
                    states[i % states.length]
            );

            manager.addBook(book);
        }

        List<IBook> books = manager.getBooks();
        assertEquals(initialSize + 100, books.size());

        for (int i = 0; i < 100; i++) {
            String expectedTitle = "Titolo " + i;
            assertTrue(
                    books.stream().anyMatch(b -> b.getTitle().equals(expectedTitle)),
                    "Libro con titolo '" + expectedTitle + "' non trovato"
            );
        }
    }

    /**
     * Genera un ISBN-13 valido a partire da un numero base (12 cifre), calcolando il check digit.
     */
    private String generateIsbn13(long base) {
        String baseStr = String.format("%012d", base);
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(baseStr.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        int checkDigit = (10 - (sum % 10)) % 10;
        return baseStr + checkDigit;
    }

    @Test
    @DisplayName("Undo e redo funzionano correttamente")
    void testUndoRedoFunctionality() {
        Book book1 = new Book("Libro 1", "Autore 1", "9788804668794", "Romanzo", 4, new ToReadState());
        Book book2 = new Book("Libro 2", "Autore 2", "0306406152", "Fantasy", 5, new ReadState());

        manager.addBook(book1);
        manager.addBook(book2);

        assertEquals(2, manager.getBooks().size());

        assertTrue(manager.undo());
        assertEquals(1, manager.getBooks().size());

        assertTrue(manager.redo());
        assertEquals(2, manager.getBooks().size());
    }

    @Test
    @DisplayName("clearLibrary svuota correttamente la libreria")
    void testClearLibrary() {
        Book book = new Book("Titolo1", "Autore1", "0451524934", "Romanzo", 5, new ToReadState());
        manager.addBook(book);
        assertFalse(manager.getBooks().isEmpty());

        manager.clearLibrary();
        assertTrue(manager.getBooks().isEmpty());
    }

    @ParameterizedTest(name = "{index} => ISBN pulito e valido: ''{0}''")
    @ValueSource(strings = {
            "0471958697",
            "9780470059029",
            "0-321-14653-0",
            "978 0 471 48648 0"
    })
    @DisplayName("Pulizia e validazione ISBN")
    void testIsbnCleaningAndValidation(String rawIsbn) {
        String cleaned = ISBNUtils.cleanIsbn(rawIsbn);
        assertTrue(ISBNUtils.isValidIsbn(cleaned), "ISBN non valido: " + rawIsbn);
    }

    @ParameterizedTest(name = "{index} => query=''{0}'' restituisce tutti i libri")
    @NullAndEmptySource
    @DisplayName("Ricerca per titolo null o vuoto restituisce tutti i libri")
    void testSearchEmptyOrNull(String query) {
        var b1 = new Book("AAA", "X", "0306406152", "Romanzo", 3, new ToReadState());
        var b2 = new Book("BBB", "Y", "9780306406157", "Romanzo", 4, new ToReadState());
        manager.addBook(b1);
        manager.addBook(b2);

        List<IBook> result = query == null
                ? manager.searchByTitle("")
                : manager.searchByTitle(query);

        assertEquals(2, result.size());
    }

    @TestFactory
    @DisplayName("Test dinamici: transizione tra stati di lettura")
    Stream<DynamicTest> dynamicStateTransitionTests() {
        Book book = new Book("StateTest", "Z", "0306406152", "Giallo", 2, new ToReadState());
        return Stream.of(
                DynamicTest.dynamicTest("Stato iniziale: TO_READ", () -> {
                    assertEquals("TO_READ", book.getStateName());
                }),
                DynamicTest.dynamicTest("next -> READING", () -> {
                    book.next();
                    assertEquals("READING", book.getStateName());
                }),
                DynamicTest.dynamicTest("next -> READ", () -> {
                    book.next();
                    assertEquals("READ", book.getStateName());
                }),
                DynamicTest.dynamicTest("previous -> READING", () -> {
                    book.previous();
                    assertEquals("READING", book.getStateName());
                })
        );
    }

    @EnabledOnOs(OS.WINDOWS)
    @Test
    @DisplayName("Test eseguito solo su sistema Windows")
    void testOnlyOnWindows() {
        assertTrue(System.getProperty("os.name").toLowerCase().contains("win"));
    }

}