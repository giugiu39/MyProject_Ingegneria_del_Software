package it.unical.gestorelibreria.filter;

import it.unical.gestorelibreria.model.IBook;

import java.util.List;

public abstract class BookFilter implements BookFilterHandler {

    protected BookFilterHandler next;

    @Override
    public void setNext(BookFilterHandler next) {
        this.next = next;
    }

    @Override
    public List<IBook> handle(List<IBook> books) {
        List<IBook> filtered = applyFilter(books);
        if (next != null) {
            return next.handle(filtered);
        }
        return filtered;
    }

    protected abstract List<IBook> applyFilter(List<IBook> books);
}
