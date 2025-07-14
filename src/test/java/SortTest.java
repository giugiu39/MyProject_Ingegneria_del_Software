import it.unical.gestorelibreria.model.Book;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.sort.*;
import it.unical.gestorelibreria.state.ReadState;
import it.unical.gestorelibreria.state.ToReadState;
import it.unical.gestorelibreria.utils.LibraryUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test per le strategie di ordinamento")
public class SortTest {

    private IBook book1;
    private IBook book2;
    private IBook book3;
    private List<IBook> books;

    @BeforeEach
    void setUp() {
        // Libri con ISBN validi (uno ISBN-10 e due ISBN-13)
        book1 = new Book(
                "Zorro",
                "Autore Z",
                "0-306-40615-2", // ISBN-10 valido
                LibraryUtils.GENERI[8], // "Avventura"
                3,
                new ReadState()
        );

        book2 = new Book(
                "Alice",
                "Autore A",
                "978-3-16-148410-0", // ISBN-13 valido
                LibraryUtils.GENERI[2], // "Fantasy"
                5,
                new ToReadState()
        );

        book3 = new Book(
                "Moby Dick",
                "Autore M",
                "978-0-545-01022-1", // ISBN-13 valido
                LibraryUtils.GENERI[0], // "Romanzo"
                4,
                new ReadState()
        );

        books = Arrays.asList(book1, book2, book3);
    }

    @Nested
    @DisplayName("Test SortByTitle")
    class SortByTitleTest {
        @Test
        @DisplayName("Ordinamento per titolo (A-Z)")
        void testSortByTitle() {
            SortStrategy sorter = new SortByTitle();
            List<IBook> sorted = sorter.sort(books);

            assertEquals("Alice", sorted.get(0).getTitle());
            assertEquals("Moby Dick", sorted.get(1).getTitle());
            assertEquals("Zorro", sorted.get(2).getTitle());
        }
    }

    @Nested
    @DisplayName("Test SortByAuthor")
    class SortByAuthorTest {
        @Test
        @DisplayName("Ordinamento per autore (A-Z)")
        void testSortByAuthor() {
            SortStrategy sorter = new SortByAuthor();
            List<IBook> sorted = sorter.sort(books);

            assertEquals("Autore A", sorted.get(0).getAuthor());
            assertEquals("Autore M", sorted.get(1).getAuthor());
            assertEquals("Autore Z", sorted.get(2).getAuthor());
        }
    }

    @Nested
    @DisplayName("Test SortByRating")
    class SortByRatingTest {
        @Test
        @DisplayName("Ordinamento per valutazione (decrescente)")
        void testSortByRating() {
            SortStrategy sorter = new SortByRating();
            List<IBook> sorted = sorter.sort(books);

            assertEquals(3, sorted.get(0).getRating());
            assertEquals(4, sorted.get(1).getRating());
            assertEquals(5, sorted.get(2).getRating());
        }
    }

    @Nested
    @DisplayName("Test ReverseSortDecorator")
    class ReverseSortDecoratorTest {
        @Test
        @DisplayName("Inversione dell'ordinamento base per titolo")
        void testReverseTitleSort() {
            SortStrategy sorter = new ReverseSortDecorator(new SortByTitle());
            List<IBook> sorted = sorter.sort(books);

            assertEquals("Zorro", sorted.get(0).getTitle());
            assertEquals("Moby Dick", sorted.get(1).getTitle());
            assertEquals("Alice", sorted.get(2).getTitle());
        }

        @Test
        @DisplayName("Inversione ordinamento per valutazione")
        void testReverseRatingSort() {
            SortStrategy sorter = new ReverseSortDecorator(new SortByRating());
            List<IBook> sorted = sorter.sort(books);

            assertEquals(5, sorted.get(0).getRating());
            assertEquals(4, sorted.get(1).getRating());
            assertEquals(3, sorted.get(2).getRating());
        }

        @Test
        @DisplayName("Check decoratore incapsula correttamente la strategia")
        void testInnerStrategy() {
            SortStrategy base = new SortByAuthor();
            ReverseSortDecorator reversed = new ReverseSortDecorator(base);
            assertEquals(base, reversed.getInner());
        }

        @ParameterizedTest
        @CsvSource({
                "Zorro,Alice,Alice",   // Alice < Zorro
                "book,Book,book",      // book = Book → stabile, ma book dopo
                "alpha,Beta,alpha",    // alpha < Beta
                "Zoo,Zebra,Zebra"      // Zebra < Zoo
        })
        @DisplayName("Test parametrico: ordinamento per titolo")
        void testSortByTitleParameterized(String title1, String title2, String expectedFirstTitle) {
            IBook book1 = new Book(title1, "Autore1", "978-0-14-044913-6", LibraryUtils.GENERI[0], 3, new ReadState());
            IBook book2 = new Book(title2, "Autore2", "978-1-86197-876-9", LibraryUtils.GENERI[1], 4, new ReadState());

            List<IBook> books = Arrays.asList(book1, book2);
            SortStrategy sorter = new SortByTitle();

            List<IBook> sorted = sorter.sort(books);

            assertEquals(expectedFirstTitle, sorted.get(0).getTitle());
        }


    }
}
