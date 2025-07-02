package it.unical.gestorelibreria.command;

import it.unical.gestorelibreria.controller.LibraryManagerInstance;

import java.util.Stack;

public class CommandInvoker {

    private final Stack<Command> history = new Stack<>();

    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
    }

    public void undoLastCommand() {
        LibraryManagerInstance.INSTANCE.undo();
    }

    public void clearHistory() {
        history.clear();
    }
}
