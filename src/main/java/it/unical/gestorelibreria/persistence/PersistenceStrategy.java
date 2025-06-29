package it.unical.gestorelibreria.persistence;

import it.unical.gestorelibreria.model.IBook;
import java.util.List;

public interface PersistenceStrategy {
    void save(List<IBook> books, String filepath) throws Exception;
    List<IBook> load(String filepath) throws Exception;
}
