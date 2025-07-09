package it.unical.gestorelibreria.filter;

import it.unical.gestorelibreria.model.IBook;
import java.util.List;
import java.util.stream.Collectors;

public class StateFilter extends BookFilter {
    private final String stateName;
    public StateFilter(String stateName) { this.stateName = stateName; }
    @Override
    protected List<IBook> applyFilter(List<IBook> books) {
        return books.stream()
                .filter(b -> b.getStateName().equalsIgnoreCase(stateName))
                .collect(Collectors.toList());
    }
}
