package it.unical.gestorelibreria.persistence;

import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.model.Book;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class CsvPersistenceStrategy implements PersistenceStrategy {

    @Override
    public void save(List<IBook> books, String filepath) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            // Header CSV
            writer.write("title,author,isbn,genre,rating");
            writer.newLine();
            for (IBook book : books) {
                writer.write(String.format("%s,%s,%s,%s,%d",
                        escapeCsv(book.getTitle()),
                        escapeCsv(book.getAuthor()),
                        escapeCsv(book.getIsbn()),
                        escapeCsv(book.getGenre()),
                        book.getRating()));
                writer.newLine();
            }
        }
    }

    @Override
    public List<IBook> load(String filepath) throws Exception {
        List<IBook> books = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length == 5) {
                    Book book = new Book(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]));
                    books.add(book);
                }
            }
        }
        return books;
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        return value;
    }
}
