package it.unical.gestorelibreria.ui;

import it.unical.gestorelibreria.controller.LibraryManagerInstance;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class SearchPanel extends JPanel {
    private final BookTablePanel tablePanel;
    public SearchPanel(BookTablePanel tablePanel) {
        this.tablePanel = tablePanel;
        setLayout(new FlowLayout(FlowLayout.RIGHT));

        JTextField searchF = new JTextField(20);
        add(new JLabel("Cerca:"));
        add(searchF);

        searchF.getDocument().addDocumentListener(new DocumentListener() {
            void update() {
                String txt = searchF.getText().trim();
                tablePanel.refreshData(
                        LibraryManagerInstance.INSTANCE.searchByTitle(txt)
                );
            }
            @Override public void insertUpdate(DocumentEvent e) { update(); }
            @Override public void removeUpdate(DocumentEvent e) { update(); }
            @Override public void changedUpdate(DocumentEvent e) { update(); }
        });
    }
}
