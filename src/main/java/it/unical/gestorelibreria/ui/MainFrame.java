package it.unical.gestorelibreria.ui;

import it.unical.gestorelibreria.controller.LibraryManagerInstance;

import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Gestore Libreria");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900,600);
        setLocationRelativeTo(null);

        BookTablePanel table = new BookTablePanel();
        ToolbarPanel toolbar = new ToolbarPanel(table);
        SearchPanel search = new SearchPanel(table);

        add(toolbar, "North");
        add(table, "Center");
        add(search, "South");

        // carica iniziale
        table.refreshData(LibraryManagerInstance.INSTANCE.getBooks(null));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
