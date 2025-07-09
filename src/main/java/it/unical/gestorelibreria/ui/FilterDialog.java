package it.unical.gestorelibreria.ui;

import it.unical.gestorelibreria.filter.*;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.utils.LibraryUtils;

import javax.swing.*;
import java.awt.*;

public class FilterDialog extends JDialog {
    private BookFilterHandler chain = null;

    public FilterDialog(Window owner) {
        super(owner, "Configura Filtri", ModalityType.APPLICATION_MODAL);
        setLayout(new GridLayout(0,2,5,5));

        JCheckBox cbGenre = new JCheckBox("Genere");
        JComboBox<String> genreBox = new JComboBox<>(LibraryUtils.GENERI);
        genreBox.setEnabled(false);

        JCheckBox cbState = new JCheckBox("Stato");
        JComboBox<String> stateBox = new JComboBox<>(new String[]{"TO_READ","READING","READ"});
        stateBox.setEnabled(false);

        JCheckBox cbRating = new JCheckBox("Min Rating");
        JSpinner spMin = new JSpinner(new SpinnerNumberModel(1,1,10,1));
        spMin.setEnabled(false);

        cbGenre.addItemListener(e -> genreBox.setEnabled(cbGenre.isSelected()));
        cbState.addItemListener(e -> stateBox.setEnabled(cbState.isSelected()));
        cbRating.addItemListener(e -> spMin.setEnabled(cbRating.isSelected()));

        add(cbGenre);  add(genreBox);
        add(cbState);  add(stateBox);
        add(cbRating); add(spMin);

        JButton ok = new JButton("Applica");
        add(new JLabel()); add(ok);

        ok.addActionListener(e -> {
            BookFilterHandler head = null, tail = null;
            if (cbGenre.isSelected()) {
                BookFilterHandler f = new GenreFilter((String)genreBox.getSelectedItem());
                head = tail = f;
            }
            if (cbState.isSelected()) {
                BookFilterHandler f = new StateFilter((String)stateBox.getSelectedItem());
                if (head==null) head = tail = f;
                else { tail.setNext(f); tail = f; }
            }
            if (cbRating.isSelected()) {
                BookFilterHandler f = new RatingFilter((Integer)spMin.getValue());
                if (head==null) head = tail = f;
                else { tail.setNext(f); tail = f; }
            }
            this.chain = head; // null = nessun filtro
            dispose();
        });

        pack();
        setLocationRelativeTo(owner);
    }

    /** Ritorna la catena di filtri, o null se nessun filtro. */
    public BookFilterHandler getFilterChain() {
        return chain;
    }
}
