package it.unical.gestorelibreria.command;

public interface Command {
    void execute();
    void undo();
}
