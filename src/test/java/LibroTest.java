import it.unical.gestorelibreria.controller.LibraryManagerInstance;
import it.unical.gestorelibreria.model.Book;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.state.*;
import it.unical.gestorelibreria.utils.ISBNUtils;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

@DisplayName("Test per la classe Book")
public class LibroTest {

    private LibraryManagerInstance manager;

    @BeforeEach
    void setUp() {
        manager = LibraryManagerInstance.INSTANCE;
        manager.clearLibrary();
    }

    @Nested
    @DisplayName("Test di costruzione e validazione")
    class CostruzioneTest {

        @Test
        @DisplayName("Creazione libro valido con ISBN")
        void testCreazioneLibroValido() {
            Book book = new Book("Il Signore degli Anelli", "J.R.R. Tolkien", "9780261102385",
                    "Fantasy", 5, new ToReadState());

            assertEquals("Il Signore degli Anelli", book.getTitle());
            assertEquals("J.R.R. Tolkien", book.getAuthor());
            assertTrue(ISBNUtils.isValidIsbn(book.getIsbn()));
            assertEquals("Fantasy", book.getGenre());
            assertEquals(5, book.getRating());
            assertEquals("TO_READ", book.getStateName());
        }

        @Test
        @DisplayName("Titolo nullo o vuoto")
        void testTitoloInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Book(null, "Autore", "9780261102385", "Fantasy", 3, new ToReadState())
            );
            assertThrows(IllegalArgumentException.class, () ->
                    new Book("", "Autore", "9780261102385", "Fantasy", 3, new ToReadState())
            );
        }

        @Test
        @DisplayName("Autore nullo o vuoto")
        void testAutoreInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Book("Titolo", null, "9780261102385", "Fantasy", 3, new ToReadState())
            );
            assertThrows(IllegalArgumentException.class, () ->
                    new Book("Titolo", "", "9780261102385", "Fantasy", 3, new ToReadState())
            );
        }

        @Test
        @DisplayName("ISBN invalido")
        void testISBNInvalido() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Book("Titolo", "Autore", "123", "Fantasy", 3, new ToReadState())
            );
            assertThrows(IllegalArgumentException.class, () ->
                    new Book("Titolo", "Autore", "ABCDEF12345", "Fantasy", 3, new ToReadState())
            );
        }

        @Test
        @DisplayName("Valutazione invalida")
        void testValutazioneInvalida() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Book("Titolo", "Autore", "9780261102385", "Fantasy", 0, new ToReadState())
            );
            assertThrows(IllegalArgumentException.class, () ->
                    new Book("Titolo", "Autore", "9780261102385", "Fantasy", 6, new ToReadState())
            );
        }
    }

    @Nested
    @DisplayName("Test di modifica stato")
    class ModificaStatoTest {
        private Book book;

        @BeforeEach
        void initBook() {
            book = new Book("Test", "Autore Test", "9780261102385",
                    "Fantasy", 3, new ToReadState());
        }

        @Test
        @DisplayName("Modifica stato avanzamento")
        void testAvanzamentoStato() {
            assertEquals("TO_READ", book.getStateName());

            book.next();
            assertEquals("READING", book.getStateName());

            book.next();
            assertEquals("READ", book.getStateName());
        }

        @Test
        @DisplayName("Modifica stato regressione")
        void testRegressioneStato() {
            book.next(); // TO_READ -> READING
            book.next(); // READING -> READ
            assertEquals("READ", book.getStateName());

            book.previous();
            assertEquals("READING", book.getStateName());

            book.previous();
            assertEquals("TO_READ", book.getStateName());
        }
    }

    @Nested
    @DisplayName("Test gestione libreria")
    class GestioneLibreriaTest {

        @Test
        @DisplayName("Aggiunta libro e verifica")
        void testAggiungiLibro() {
            Book book = new Book("Dune", "Frank Herbert", "9780441172719",
                    "Fantascienza", 4, new ToReadState());
            manager.addBook(book);

            List<IBook> libri = manager.getBooks();
            assertEquals(1, libri.size());
            assertEquals("Dune", libri.get(0).getTitle());
        }

        @Test
        @DisplayName("Rimozione libro e verifica")
        void testRimuoviLibro() {
            Book book = new Book("Neuromante", "William Gibson", "9780441569595",
                    "Cyberpunk", 5, new ToReadState());
            manager.addBook(book);
            assertEquals(1, manager.getBooks().size());

            manager.removeBook(book);
            assertTrue(manager.getBooks().isEmpty());
        }

        @Test
        @DisplayName("Undo e redo operazioni di libreria")
        void testUndoRedo() {
            Book book1 = new Book("Fahrenheit 451", "Ray Bradbury", "9781451673319",
                    "Distopico", 5, new ToReadState());
            Book book2 = new Book("Il Gattopardo", "Giuseppe Tomasi di Lampedusa", "9788845291593",
                    "Romanzo", 4, new ToReadState());

            manager.addBook(book1);
            manager.addBook(book2);
            assertEquals(2, manager.getBooks().size());

            assertTrue(manager.undo());
            assertEquals(1, manager.getBooks().size());
            assertEquals("Fahrenheit 451", manager.getBooks().get(0).getTitle());

            assertTrue(manager.redo());
            assertEquals(2, manager.getBooks().size());
        }

        @Test
        @DisplayName("ClearLibrary svuota completamente la libreria")
        void testClearLibrary() {
            Book book = new Book("La Divina Commedia", "Dante Alighieri", "9788804707882",
                    "Classico", 5, new ToReadState());
            manager.addBook(book);

            assertFalse(manager.getBooks().isEmpty());
            manager.clearLibrary();
            assertTrue(manager.getBooks().isEmpty());
        }
    }

    @Nested
    @DisplayName("Test ricerche libreria")
    class RicercaLibreriaTest {

        @Test
        @DisplayName("Ricerca per titolo")
        void testSearchByTitle() {
            Book book1 = new Book("Harry Potter e la Pietra Filosofale", "J.K. Rowling", "9780747532699",
                    "Fantasy", 5, new ToReadState());
            Book book2 = new Book("Harry Potter e la Camera dei Segreti", "J.K. Rowling", "9780747538493",
                    "Fantasy", 5, new ToReadState());
            Book book3 = new Book("Il Codice Da Vinci", "Dan Brown", "9780307474278",
                    "Thriller", 4, new ToReadState());

            manager.addBook(book1);
            manager.addBook(book2);
            manager.addBook(book3);

            List<IBook> results = manager.searchByTitle("Harry Potter");
            assertEquals(2, results.size());

            results = manager.searchByTitle("Da Vinci");
            assertEquals(1, results.size());
        }

        @Test
        @DisplayName("Ricerca per autore")
        void testSearchByAuthor() {
            Book book1 = new Book("Il vecchio e il mare", "Ernest Hemingway", "9780684801223",
                    "Romanzo", 4, new ToReadState());
            Book book2 = new Book("Addio alle armi", "Ernest Hemingway", "9780684801469",
                    "Romanzo", 5, new ToReadState());
            Book book3 = new Book("Cime tempestose", "Emily Brontë", "9780141439556",
                    "Romanzo", 4, new ToReadState());

            manager.addBook(book1);
            manager.addBook(book2);
            manager.addBook(book3);

            List<IBook> results = manager.searchByAuthor("Hemingway");
            assertEquals(2, results.size());

            results = manager.searchByAuthor("Brontë");
            assertEquals(1, results.size());
        }
    }
}