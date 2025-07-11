import it.unical.gestorelibreria.controller.LibraryManagerInstance;
import it.unical.gestorelibreria.filter.*;
import it.unical.gestorelibreria.model.Book;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.state.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookFilterTest {

    private LibraryManagerInstance manager;

    @BeforeEach
    void setUp() {
        manager = LibraryManagerInstance.INSTANCE;
        manager.clearLibrary();

        manager.addBook(new Book("Clean Code", "Robert Martin", "9780132350884", "Software", 5, new ToReadState()));
        manager.addBook(new Book("Effective Java", "Joshua Bloch", "9780134685991", "Software", 4, new ToReadState()));
        manager.addBook(new Book("Il Signore degli Anelli", "J.R.R. Tolkien", "9780261102385", "Fantasy", 3, new ReadingInProgressState()));
        manager.addBook(new Book("Harry Potter", "J.K. Rowling", "9780747532743", "Fantasy", 2, new ReadingInProgressState()));
        manager.addBook(new Book("The Pragmatic Programmer", "Andrew Hunt", "9780201616224", "Software", 1, new ToReadState()));
    }

    @ParameterizedTest
    @CsvSource({
            "Fantasy,2",
            "Software,3",
            "NonEsistente,0"
    })
    @DisplayName("Filtro per genere")
    @Order(1)
    void testGenreFilter(String genre, int expectedCount) {
        BookFilter genreFilter = new GenreFilter(genre);
        List<IBook> result = genreFilter.handle(manager.getBooks());
        assertEquals(expectedCount, result.size());
    }

    @ParameterizedTest
    @CsvSource({
            "Robert Martin,1",
            "J.R.R. Tolkien,1",
            "AutoreInesistente,0"
    })
    @DisplayName("Filtro per autore")
    @Order(2)
    void testAuthorFilter(String author, int expectedCount) {
        BookFilter authorFilter = new AuthorFilter(author);
        List<IBook> result = authorFilter.handle(manager.getBooks());
        assertEquals(expectedCount, result.size());
    }

    @ParameterizedTest
    @CsvSource({
            "TO_READ,3",
            "READING,2",
            "READ,0"
    })
    @DisplayName("Filtro per stato")
    @Order(3)
    void testStateFilter(String state, int expectedCount) {
        BookFilter stateFilter = new StateFilter(state);
        List<IBook> result = stateFilter.handle(manager.getBooks());
        assertEquals(expectedCount, result.size());
    }

    @ParameterizedTest
    @CsvSource({
            "5,1",
            "4,2",
            "2,4",
            "0,5",
            "6,0"
    })
    @DisplayName("Filtro per rating minimo")
    @Order(4)
    void testRatingFilter(int minRating, int expectedCount) {
        BookFilter ratingFilter = new RatingFilter(minRating);
        List<IBook> result = ratingFilter.handle(manager.getBooks());
        assertEquals(expectedCount, result.size());
    }

    @ParameterizedTest
    @CsvSource({
            "code,1",
            "java,1",
            "robert,1",
            "the,1",
            "inesistente,0"
    })
    @DisplayName("Filtro per testo (titolo o autore)")
    @Order(5)
    void testTextFilter(String keyword, int expectedCount) {
        BookFilter textFilter = new TextFilter(keyword);
        List<IBook> result = textFilter.handle(manager.getBooks());
        assertEquals(expectedCount, result.size());
    }

    @Test
    @DisplayName("Filtro concatenato: Software + Rating >= 4")
    @Order(6)
    void testCombinedFilterChain() {
        BookFilter genreFilter = new GenreFilter("Software");
        BookFilter ratingFilter = new RatingFilter(4);
        genreFilter.setNext(ratingFilter);

        List<IBook> result = genreFilter.handle(manager.getBooks());
        assertEquals(2, result.size());
        for (IBook book : result) {
            assertEquals("Software", book.getGenre());
            assertTrue(book.getRating() >= 4);
        }
    }

    @Test
    @DisplayName("Filtro concatenato: Autore + Stato")
    @Order(7)
    void testAuthorAndStateChain() {
        BookFilter authorFilter = new AuthorFilter("Robert Martin");
        BookFilter stateFilter = new StateFilter("TO_READ");
        authorFilter.setNext(stateFilter);

        List<IBook> result = authorFilter.handle(manager.getBooks());
        assertEquals(1, result.size());
        assertEquals("Robert Martin", result.get(0).getAuthor());
        assertEquals("TO_READ", result.get(0).getStateName());
    }

    @RepeatedTest(5)
    @DisplayName("Filtro per rating minimo è deterministico")
    void repeatedTestRatingFilter() {
        BookFilter ratingFilter = new RatingFilter(4);
        List<IBook> result = ratingFilter.handle(manager.getBooks());
        assertEquals(2, result.size());
        for (IBook book : result) {
            assertTrue(book.getRating() >= 4);
        }
    }

    @TestFactory
    @DisplayName("Test dinamici per filtro autore")
    Collection<DynamicTest> dynamicTestsForAuthorFilter() {
        Map<String, Integer> authorExpectations = Map.of(
                "Robert Martin", 1,
                "Joshua Bloch", 1,
                "J.K. Rowling", 1,
                "Andrew Hunt", 1,
                "AutoreInesistente", 0
        );

        return authorExpectations.entrySet().stream()
                .map(entry -> DynamicTest.dynamicTest(
                        "Filtro autore: " + entry.getKey(),
                        () -> {
                            BookFilter filter = new AuthorFilter(entry.getKey());
                            List<IBook> result = filter.handle(manager.getBooks());
                            assertEquals(entry.getValue(), result.size());
                        }
                )).collect(Collectors.toList());
    }



}