import it.unical.gestorelibreria.controller.LibraryManagerInstance;
import it.unical.gestorelibreria.model.Book;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.state.ToReadState;
import it.unical.gestorelibreria.state.ReadState;

import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JsonLibraryPersistenceTest {

    private LibraryManagerInstance manager;

    @BeforeEach
    void setup() {
        manager = LibraryManagerInstance.INSTANCE;
        manager.clearLibrary(); // pulizia iniziale
    }

    @Test
    @DisplayName("Persistenza: salvataggio e caricamento da file JSON")
    void testJsonPersistence() {
        // 1. Crea due libri
        IBook book1 = new Book("Persistente 1", "Autore A", "9788804668794", "Romanzo", 4, new ToReadState());
        IBook book2 = new Book("Persistente 2", "Autore B", "9780134685991", "Fantasy", 5, new ReadState());

        // 2. Aggiungili e salva
        manager.addBook(book1);
        manager.addBook(book2);
        manager.saveLibrary(); // forziamo salvataggio su disco

        // 3. "Simula" un nuovo avvio: svuota e ricrea INSTANCE
        manager.clearLibrary(); // svuota lista attuale

        // 4. Carica nuovamente dalla persistenza
        List<IBook> reloadedBooks = manager.getBooks();

        // 5. Verifiche
        assertEquals(2, reloadedBooks.size(), "Devono essere ricaricati 2 libri");

        assertTrue(reloadedBooks.stream().anyMatch(b -> b.getTitle().equals("Persistente 1")));
        assertTrue(reloadedBooks.stream().anyMatch(b -> b.getTitle().equals("Persistente 2")));
    }

    @AfterEach
    void tearDown() {
        manager.clearLibrary();
    }
}
