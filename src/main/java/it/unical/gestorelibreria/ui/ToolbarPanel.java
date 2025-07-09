package it.unical.gestorelibreria.ui;

import it.unical.gestorelibreria.command.*;
import it.unical.gestorelibreria.controller.LibraryManagerInstance;
import it.unical.gestorelibreria.model.IBook;

import javax.swing.*;
import java.awt.*;

public class ToolbarPanel extends JPanel {

    private final CommandInvoker invoker = new CommandInvoker();
    private final BookTablePanel tablePanel;

    private final JButton undoBtn = new JButton("Undo");
    private final JButton redoBtn = new JButton("Redo");

    public ToolbarPanel(BookTablePanel tablePanel) {
        this.tablePanel = tablePanel;
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JButton addBtn = new JButton("Aggiungi");
        JButton editBtn = new JButton("Modifica");
        JButton delBtn = new JButton("Rimuovi");

        add(addBtn); add(editBtn); add(delBtn); add(undoBtn); add(redoBtn);

        addBtn.addActionListener(e -> {
            BookFormDialog dlg = new BookFormDialog(null, null);
            dlg.setVisible(true);
            IBook b = dlg.getBook();
            if (b != null) {
                invoker.executeCommand(new AddBookCommand(b));
                refreshTable();
            }
        });

        editBtn.addActionListener(e -> {
            IBook sel = tablePanel.getSelectedBook();
            if (sel != null) {
                BookFormDialog dlg = new BookFormDialog(null, sel);
                dlg.setVisible(true);
                IBook modified = dlg.getBook();
                if (modified != null) {
                    LibraryManagerInstance.INSTANCE.saveLibrary();
                    refreshTable();
                }
            }
        });

        delBtn.addActionListener(e -> {
            IBook sel = tablePanel.getSelectedBook();
            if (sel != null) {
                invoker.executeCommand(new RemoveBookCommand(sel));
                refreshTable();
            }
        });

        undoBtn.addActionListener(e -> {
            invoker.undoLastCommand();
            refreshTable();
        });

        redoBtn.addActionListener(e -> {
            invoker.redoLastCommand();
            refreshTable();
        });

        refreshTable(); // inizializza lo stato dei pulsanti
    }

    private void refreshTable() {
        tablePanel.refreshData(
                LibraryManagerInstance.INSTANCE.getBooks(null)
        );

        // Aggiorna stato pulsanti Undo/Redo
        undoBtn.setEnabled(LibraryManagerInstance.INSTANCE.canUndo());
        redoBtn.setEnabled(LibraryManagerInstance.INSTANCE.canRedo());
    }
}