package it.unical.gestorelibreria.command;

import java.util.Stack;

public class CommandInvoker {

    private final Stack<Command> history = new Stack<>();

    public void executeCommand(Command command) {
        command.execute();
        history.push(command);
    }

    public void undoLastCommand() {
        if (!history.isEmpty()) {
            Command last = history.pop();
            last.undo();
        }
    }

    public void clearHistory() {
        history.clear();
    }
}
