package it.unical.gestorelibreria.ui;

import it.unical.gestorelibreria.model.IBook;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BookTableModel extends AbstractTableModel {
    private final String[] columns = {
            "Titolo","Autore","ISBN","Genere","Rating","Stato lettura"
    };
    private List<IBook> books = new ArrayList<>();

    public void setBooks(List<IBook> books) {
        this.books = books;
        fireTableDataChanged();
    }

    public IBook getBookAt(int row) {
        return books.get(row);
    }

    @Override public int getRowCount() {
        return books.size();
    }

    @Override public int getColumnCount() {
        return columns.length;
    }

    @Override public String getColumnName(int c) {
        return columns[c];
    }

    @Override
    public Object getValueAt(int r, int c) {
        IBook b = books.get(r);
        return switch(c) {
            case 0 -> b.getTitle();
            case 1 -> b.getAuthor();
            case 2 -> b.getIsbn();
            case 3 -> b.getGenre();
            case 4 -> b.getRating();
            case 5 -> b.getStateName();
            default -> null;
        };
    }
}
