package it.unical.gestorelibreria.ui;

import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.model.Book;
import it.unical.gestorelibreria.state.ReadState;
import it.unical.gestorelibreria.state.ReadingInProgressState;
import it.unical.gestorelibreria.state.ReadingState;
import it.unical.gestorelibreria.state.ToReadState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BookFormDialog extends JDialog {

    private JTextField titleF, authorF, isbnF, genreF, ratingF;
    private JComboBox<String> stateC;
    private IBook book;

    public BookFormDialog(Frame owner, IBook existing) {
        super(owner, true);
        setTitle(existing == null ? "Nuovo Libro" : "Modifica Libro");
        setLayout(new GridLayout(7,2,5,5));

        titleF  = new JTextField();
        authorF = new JTextField();
        isbnF   = new JTextField();
        genreF  = new JTextField();
        ratingF = new JTextField();
        stateC  = new JComboBox<>(new String[]{"TO_READ","READING","READ"});

        if (existing != null) {
            titleF.setText(existing.getTitle());
            authorF.setText(existing.getAuthor());
            isbnF.setText(existing.getIsbn());
            genreF.setText(existing.getGenre());
            ratingF.setText(String.valueOf(existing.getRating()));
            stateC.setSelectedItem(existing.getStateName());
        }

        add(new JLabel("Titolo:"));  add(titleF);
        add(new JLabel("Autore:")); add(authorF);
        add(new JLabel("ISBN:"));

        JPanel isbnPanel = new JPanel(new BorderLayout());
        isbnPanel.add(isbnF, BorderLayout.CENTER);

        if (existing != null) {
            isbnF.setEditable(false); // Rende il campo non modificabile
            JLabel note = new JLabel(" (non modificabile)");
            note.setFont(note.getFont().deriveFont(Font.ITALIC, 10f));
            note.setForeground(Color.GRAY); // colore grigio per renderlo discreto
            isbnPanel.add(note, BorderLayout.EAST);
        }

        add(isbnPanel);

        add(new JLabel("Genere:"));  add(genreF);
        add(new JLabel("Valutazione:")); add(ratingF);
        add(new JLabel("Stato lettura:")); add(stateC);

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Annulla");
        add(ok); add(cancel);

        ok.addActionListener(e -> onOK(existing));
        cancel.addActionListener(e -> onCancel());

        pack(); // ridimensiona finestra
        setLocationRelativeTo(owner); // la centra rispetto alla finestra padre
    }

    private void onOK(IBook existing) {
        try {
            String t = titleF.getText().trim();
            String a = authorF.getText().trim();
            String i = isbnF.getText().trim();
            String g = genreF.getText().trim();
            String rText = ratingF.getText().trim();
            String s = (String)stateC.getSelectedItem();

            // Validazioni campi obbligatori
            if (t.isEmpty() || a.isEmpty() || i.isEmpty() || g.isEmpty() || rText.isEmpty()) {
                throw new IllegalArgumentException("Tutti i campi sono obbligatori.");
            }

            // ISBN deve essere numerico
            if (!i.matches("\\d+")) {
                throw new IllegalArgumentException("L'ISBN deve contenere solo cifre.");
            }

            // Genere non deve essere numerico
            if (g.matches("\\d+")) {
                throw new IllegalArgumentException("Il genere non può essere solo numerico.");
            }

            // Rating deve essere un intero tra 0 e 10
            int r = Integer.parseInt(rText);
            if (r < 0 || r > 10) {
                throw new IllegalArgumentException("Il rating deve essere compreso tra 0 e 10.");
            }

            // Stato lettura fallback
            if (s == null) {
                s = "TO_READ";
            }

            // Verifica ISBN univoco solo per nuovi libri
            if (existing == null) {
                for (IBook b : it.unical.gestorelibreria.controller.LibraryManagerInstance.INSTANCE.getBooks(null)) {
                    if (b.getIsbn().equals(i)) {
                        throw new IllegalArgumentException("Esiste già un libro con questo ISBN.");
                    }
                }
            }

            // Stato lettura
            ReadingState state = switch (s) {
                case "READING" -> new ReadingInProgressState();
                case "READ"    -> new ReadState();
                default        -> new ToReadState();
            };

            // Crea o aggiorna
            if (existing == null) {
                Book newBook = new Book(t, a, i, g, r);
                newBook.setState(state);
                book = newBook;
            } else {
                existing.setTitle(t);
                existing.setAuthor(a);
                existing.setIsbn(i);
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

    /** Restituisce il libro creato o modificato, o null se annullato */
    public IBook getBook() {
        return book;
    }
}
