package it.unical.gestorelibreria.ui;

import it.unical.gestorelibreria.controller.LibraryManagerInstance;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.model.Book;
import it.unical.gestorelibreria.state.ReadState;
import it.unical.gestorelibreria.state.ReadingInProgressState;
import it.unical.gestorelibreria.state.ReadingState;
import it.unical.gestorelibreria.state.ToReadState;
import it.unical.gestorelibreria.utils.LibraryUtils;
import it.unical.gestorelibreria.utils.ISBNUtils;

import javax.swing.*;
import java.awt.*;

public class BookFormDialog extends JDialog {
    private JTextField titleF, authorF, isbnF, ratingF;
    private JComboBox<String> genreC;
    private JComboBox<String> stateC;
    private IBook book;

    public BookFormDialog(Frame owner, IBook existing) {
        super(owner, true);
        setTitle(existing == null ? "Nuovo Libro" : "Modifica Libro");
        setLayout(new GridLayout(7,2,5,5));

        titleF  = new JTextField();
        authorF = new JTextField();
        isbnF   = new JTextField();
        ratingF = new JTextField();

        // nuovo combo per genere
        genreC = new JComboBox<>(LibraryUtils.GENERI);

        stateC  = new JComboBox<>(new String[]{"TO_READ","READING","READ"});

        if (existing != null) {
            titleF.setText(existing.getTitle());
            authorF.setText(existing.getAuthor());
            isbnF.setText(existing.getIsbn());
            isbnF.setEditable(false); // ISBN non modificabile
            ratingF.setText(String.valueOf(existing.getRating()));
            genreC.setSelectedItem(existing.getGenre());
            stateC.setSelectedItem(existing.getStateName());
        }

        add(new JLabel("Titolo:"));       add(titleF);
        add(new JLabel("Autore:"));       add(authorF);
        add(new JLabel("ISBN:"));
        JPanel isbnPanel = new JPanel(new BorderLayout());
        isbnPanel.add(isbnF, BorderLayout.CENTER);
        if (existing != null) {
            JLabel note = new JLabel(" (non modificabile)");
            note.setFont(note.getFont().deriveFont(Font.ITALIC, 10f));
            note.setForeground(Color.GRAY);
            isbnPanel.add(note, BorderLayout.EAST);
        }
        add(isbnPanel);

        add(new JLabel("Genere:"));       add(genreC);
        add(new JLabel("Valutazione (1-5):"));  add(ratingF);
        add(new JLabel("Stato lettura:"));add(stateC);

        JButton ok     = new JButton("OK");
        JButton cancel = new JButton("Annulla");
        add(ok); add(cancel);

        ok.addActionListener(e -> onOK(existing));
        cancel.addActionListener(e -> onCancel());

        pack();
        setLocationRelativeTo(owner);
    }

    private void onOK(IBook existing) {
        try {
            String t = titleF.getText().trim();
            String a = authorF.getText().trim();
            String iRaw = isbnF.getText().trim();
            String i = ISBNUtils.cleanIsbn(iRaw);
            String g = (String)genreC.getSelectedItem();
            String rText = ratingF.getText().trim();
            String s = (String)stateC.getSelectedItem();

            // validazione campi obbligatori
            if (t.isEmpty()||a.isEmpty()||i.isEmpty()||g.isEmpty()||rText.isEmpty()) {
                throw new IllegalArgumentException("Tutti i campi sono obbligatori.");
            }

            // Validazione ISBN-10 o ISBN-13
            if (!ISBNUtils.isValidIsbn(i)) {
                throw new IllegalArgumentException("ISBN non valido. Deve essere ISBN-10 o ISBN-13 corretto.");
            }

            // rating intero 0–10
            int r = Integer.parseInt(rText);
            if (r<1||r>5) {
                throw new IllegalArgumentException("Rating deve essere tra 1 e 5.");
            }

            // validazione ISBN univoco se nuovo
            if (existing==null) {
                for (IBook b : LibraryManagerInstance.INSTANCE.getBooks(null)) {
                    if (ISBNUtils.cleanIsbn(b.getIsbn()).equals(i))
                        throw new IllegalArgumentException("ISBN già presente.");
                }
            }

            // stato via state pattern
            ReadingState state = switch (s) {
                case "READING" -> new ReadingInProgressState();
                case "READ"    -> new ReadState();
                default        -> new ToReadState();
            };

            if (existing==null) {
                Book newBook = new Book(t,a,i,g,r);
                newBook.setState(state);
                book = newBook;
            } else {
                existing.setTitle(t);
                existing.setAuthor(a);
                existing.setGenre(g);
                existing.setRating(r);
                existing.setState(state);
                book = existing;
            }
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Dati non validi: " + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        book = null;
        dispose();
    }

    public IBook getBook() {
        return book;
    }
}
