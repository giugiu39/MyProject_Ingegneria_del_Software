package it.unical.gestorelibreria.ui;

import it.unical.gestorelibreria.model.IBook;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BookTablePanel extends JPanel {

    private final BookTableModel model = new BookTableModel();
    private final JTable table = new JTable(model);

    public BookTablePanel() {
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void refreshData(List<IBook> books) {
        model.setBooks(books);
    }

    public IBook getSelectedBook() {
        int r = table.getSelectedRow();
        if (r >= 0)
            return model.getBookAt(r);
        return null;
    }
}
