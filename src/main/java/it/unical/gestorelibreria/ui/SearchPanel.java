package it.unical.gestorelibreria.ui;

import it.unical.gestorelibreria.filter.*;
import it.unical.gestorelibreria.model.IBook;
import it.unical.gestorelibreria.sort.*;
import it.unical.gestorelibreria.controller.LibraryManagerInstance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SearchPanel extends JPanel {
    private final BookTablePanel table;
    private BookFilterHandler currentFilter = null; // catena di filtri
    private SortStrategy sortStrategy = null;
    private SortStrategy baseSortStrategy = null; //per effettuare la reverse più volte
    private boolean reverse = false;

    public SearchPanel(BookTablePanel table) {
        this.table = table;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createTitledBorder("Ricerca & Filtri"));

        // testo + filtri
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txt = new JTextField(20);
        JButton btnSearch = new JButton("Cerca");
        JButton btnFilter = new JButton("Filtri");
        top.add(new JLabel("Testo:")); top.add(txt);
        top.add(btnSearch); top.add(btnFilter);

        // ordinamento + reverse
        JPanel bot = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<String> cbOrder = new JComboBox<>(new String[]{"Titolo","Autore","Valutazione"});
        JButton btnReverse = new JButton("↑");
        bot.add(new JLabel("Ordina per:")); bot.add(cbOrder); bot.add(btnReverse);

        JLabel lblSortInfo = new JLabel("Ordinamento crescente (clicca per invertire)");
        bot.add(lblSortInfo);

        add(top); add(bot);

        // listener Cerca
        btnSearch.addActionListener(e -> {
            String q = txt.getText().trim();
            BookFilterHandler f = currentFilter;
            if (!q.isEmpty()) {
                // testo su titolo o autore
                TextFilter tf = new TextFilter(q);
                if (f!=null) tf.setNext(f);
                f = tf;
            }
            applySearch(f);
        });

        // listener Filtri
        btnFilter.addActionListener(e -> {
            FilterDialog dlg = new FilterDialog((Window) SwingUtilities.getWindowAncestor(this));
            dlg.setVisible(true);
            currentFilter = dlg.getFilterChain();
        });

        // listener Ordine
        cbOrder.addActionListener(e -> {
            String sel = (String) cbOrder.getSelectedItem();
            baseSortStrategy = switch(sel) {
                case "Autore"      -> new SortByAuthor();
                case "Valutazione" -> new SortByRating();
                default            -> new SortByTitle();
            };

            // Applica la strategia corrente in base allo stato del reverse
            sortStrategy = reverse
                    ? new ReverseSortDecorator(baseSortStrategy)
                    : baseSortStrategy;

            applySearch(currentFilter);
        });


        // listener toggle reverse
        btnReverse.addActionListener(e -> {
            reverse = !reverse;

            btnReverse.setText(reverse ? "↓" : "↑");

            lblSortInfo.setText(reverse
                    ? "Ordinamento decrescente (clicca per invertire)"
                    : "Ordinamento crescente (clicca per invertire)");

            if (baseSortStrategy != null) {
                sortStrategy = reverse
                        ? new ReverseSortDecorator(baseSortStrategy)
                        : baseSortStrategy;
            }

            applySearch(currentFilter);
        });


    }

    private void applySearch(BookFilterHandler filter) {
        // imposta strategia su controller
        LibraryManagerInstance.INSTANCE.setSortStrategy(sortStrategy);
        // recupera e mostra
        java.util.List<IBook> ris = LibraryManagerInstance.INSTANCE.getBooks(filter);
        table.refreshData(ris);
    }
}
