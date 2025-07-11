import it.unical.gestorelibreria.command.*;
import it.unical.gestorelibreria.controller.LibraryManagerInstance;
import it.unical.gestorelibreria.model.Book;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.state.ToReadState;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test CommandInvoker e comandi Add/Remove")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class CommandInvokerTest {

    private CommandInvoker invoker;
    private LibraryManagerInstance manager;

    @BeforeEach
    void setUp() {
        manager = LibraryManagerInstance.INSTANCE;
        manager.clearLibrary();          // Reset dello stato della libreria
        invoker = new CommandInvoker();  // Nuovo invoker per ogni test
    }

    @Test
    @DisplayName("AddBookCommand aggiunge un libro")
    void testAddBookCommand() {
        IBook book = new Book("Dune", "Frank Herbert", "9780441172719",
                "Fantascienza", 4, new ToReadState());
        // Esecuzione comando
        invoker.executeCommand(new AddBookCommand(book));

        List<IBook> books = manager.getBooks();
        assertEquals(1, books.size(), "Dovrebbe esserci 1 libro");
        assertEquals("Dune", books.get(0).getTitle());
    }

    @Test
    @DisplayName("RemoveBookCommand rimuove un libro")
    void testRemoveBookCommand() {
        // Prepara stato: inserisco prima un libro
        IBook book = new Book("Neuromante", "William Gibson", "9780441569595",
                "Altro", 5, new ToReadState());
        manager.addBook(book);
        assertEquals(1, manager.getBooks().size());

        // Eseguo il remove-command
        invoker.executeCommand(new RemoveBookCommand(book));
        assertTrue(manager.getBooks().isEmpty(), "La libreria dovrebbe essere vuota dopo la rimozione");
    }

    @Test
    @DisplayName("Undo/Redo tramite CommandInvoker")
    void testUndoRedo() {
        IBook b1 = new Book("Fahrenheit 451", "Ray Bradbury", "9781451673319",
                "Altro", 5, new ToReadState());
        IBook b2 = new Book("Il Gattopardo", "Tomasi di Lampedusa", "9788845291593",
                "Romanzo", 4, new ToReadState());

        // Aggiungo due libri
        invoker.executeCommand(new AddBookCommand(b1));
        invoker.executeCommand(new AddBookCommand(b2));
        assertEquals(2, manager.getBooks().size());

        // Undo dell'ultima aggiunta
        invoker.undoLastCommand();
        List<IBook> afterUndo = manager.getBooks();
        assertEquals(1, afterUndo.size(), "Undo dovrebbe rimuovere l'ultima aggiunta");
        assertEquals("Fahrenheit 451", afterUndo.get(0).getTitle());

        // Redo
        invoker.redoLastCommand();
        List<IBook> afterRedo = manager.getBooks();
        assertEquals(2, afterRedo.size(), "Redo dovrebbe ripristinare il secondo libro");
    }

    @Test
    @DisplayName("Remove + Undo + Redo")
    void testRemoveUndoRedo() {
        IBook b = new Book("1984", "Orwell", "0451524934",
                "Romanzo", 5, new ToReadState());
        // Aggiungo e poi rimuovo
        invoker.executeCommand(new AddBookCommand(b));
        invoker.executeCommand(new RemoveBookCommand(b));
        assertTrue(manager.getBooks().isEmpty());

        // Undo (della remove)
        invoker.undoLastCommand();
        assertEquals(1, manager.getBooks().size(), "Undo remove deve ripristinare il libro");

        // Redo (della remove)
        invoker.redoLastCommand();
        assertTrue(manager.getBooks().isEmpty(), "Redo remove deve rimuovere di nuovo il libro");
    }

    @ParameterizedTest
    @CsvSource({
            "Solaris, Stanislaw Lem, 9780156027601",
            "La svastica sul sole, Philip K. Dick, 9780132350884",
            "Hyperion, Dan Simmons, 9780553283686"
    })
    @DisplayName("AddBookCommand con vari libri")
    void testParameterizedAddBook(String title, String author, String isbn) {
        IBook book = new Book(title, author, isbn, "Fantascienza", 4, new ToReadState());
        invoker.executeCommand(new AddBookCommand(book));

        List<IBook> books = manager.getBooks();
        assertTrue(books.stream().anyMatch(b -> b.getIsbn().equals(isbn)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Metro 2033", "Metro 2034"})
    @DisplayName("RemoveBookCommand rimuove solo il libro giusto")
    void testParameterizedRemove(String titleToRemove) {
        IBook b1 = new Book("Metro 2033", "Dmitry Glukhovsky", "9780575086258", "Altro", 4, new ToReadState());
        IBook b2 = new Book("Metro 2034", "Dmitry Glukhovsky", "9780575091221", "Altro", 4, new ToReadState());

        manager.addBook(b1);
        manager.addBook(b2);
        invoker.executeCommand(new RemoveBookCommand(titleToRemove.equals(b1.getTitle()) ? b1 : b2));

        assertEquals(1, manager.getBooks().size());
        assertNotEquals(titleToRemove, manager.getBooks().get(0).getTitle());
    }

    @Test
    @DisplayName("Undo di Remove seguito da Add crea due copie del libro")
    void testUndoRemoveThenAdd() {
        IBook book = new Book("Ubik", "Philip K. Dick", "9780547572291", "Fantascienza", 5, new ToReadState());

        invoker.executeCommand(new AddBookCommand(book));
        invoker.executeCommand(new RemoveBookCommand(book));
        invoker.undoLastCommand(); // ripristina
        invoker.executeCommand(new AddBookCommand(book)); // aggiunta ulteriore

        assertEquals(2, manager.getBooks().size(), "Dovrebbero esserci 2 copie del libro");
    }

    @Test
    @DisplayName("Alternanza Undo/Redo con AddBookCommand")
    void testAddUndoRedoAlternanza() {
        IBook book = new Book("I Robot", "Isaac Asimov", "9780553382563", "Fantascienza", 5, new ToReadState());

        invoker.executeCommand(new AddBookCommand(book)); // ADD
        assertEquals(1, manager.getBooks().size());

        invoker.undoLastCommand(); // UNDO
        assertTrue(manager.getBooks().isEmpty());

        invoker.redoLastCommand(); // REDO
        assertEquals(1, manager.getBooks().size());

        invoker.undoLastCommand(); // UNDO again
        assertTrue(manager.getBooks().isEmpty());
    }

    @Test
    @DisplayName("CommandInvoker gestisce comandi null")
    void testNullCommandThrows() {
        assertThrows(IllegalArgumentException.class, () -> invoker.executeCommand(null));
    }

    @Test
    @DisplayName("Undo senza comandi non causa eccezione")
    void testUndoEmptyStackSafe() {
        assertDoesNotThrow(() -> invoker.undoLastCommand());
        assertTrue(manager.getBooks().isEmpty());
    }

    @Test
    @DisplayName("Redo senza Undo precedente non fa nulla")
    void testRedoWithoutUndo() {
        IBook book = new Book("Fondazione", "Isaac Asimov", "9788804704454", "Fantascienza", 5, new ToReadState());
        invoker.executeCommand(new AddBookCommand(book));

        // Nessun undo, quindi redo non deve fare nulla
        invoker.redoLastCommand();
        assertEquals(1, manager.getBooks().size());
    }

    @Test
    @DisplayName("Undo multipli funzionano in ordine inverso")
    void testUndoOrder() {
        IBook b1 = new Book("A", "Autore", "9788804704454", "Genere", 1, new ToReadState());
        IBook b2 = new Book("B", "Autore", "9780547572291", "Genere", 1, new ToReadState());
        IBook b3 = new Book("C", "Autore", "9780140449136", "Genere", 1, new ToReadState());

        invoker.executeCommand(new AddBookCommand(b1));
        invoker.executeCommand(new AddBookCommand(b2));
        invoker.executeCommand(new AddBookCommand(b3));

        invoker.undoLastCommand(); // rimuove C
        assertFalse(manager.getBooks().stream().anyMatch(b -> b.getTitle().equals("C")));

        invoker.undoLastCommand(); // rimuove B
        assertFalse(manager.getBooks().stream().anyMatch(b -> b.getTitle().equals("B")));

        invoker.undoLastCommand(); // rimuove A
        assertTrue(manager.getBooks().isEmpty());
    }

}