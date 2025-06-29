package it.unical.gestorelibreria.persistence;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unical.gestorelibreria.model.IBook;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;

public class JsonPersistenceStrategy implements PersistenceStrategy {

    private final Gson gson = new Gson();

    @Override
    public void save(List<IBook> books, String filepath) throws Exception {
        try (FileWriter writer = new FileWriter(filepath)) {
            gson.toJson(books, writer);
        }
    }

    @Override
    public List<IBook> load(String filepath) throws Exception {
        try (FileReader reader = new FileReader(filepath)) {
            Type listType = new TypeToken<List<IBook>>(){}.getType();
            return gson.fromJson(reader, listType);
        }
    }
}
