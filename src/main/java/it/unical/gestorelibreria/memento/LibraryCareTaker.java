package it.unical.gestorelibreria.memento;

import java.util.Stack;

public class LibraryCareTaker {

    private final Stack<LibraryMemento> undoStack = new Stack<>();
    private final Stack<LibraryMemento> redoStack = new Stack<>();

    public void saveState(LibraryMemento memento) {
        undoStack.push(memento);
        redoStack.clear(); // Invalida redo su nuova azione
    }

    public LibraryMemento undo() {
        if (!undoStack.isEmpty()) {
            LibraryMemento memento = undoStack.pop();
            redoStack.push(memento);
            return memento;
        }
        return null;
    }

    public LibraryMemento redo() {
        if (!redoStack.isEmpty()) {
            LibraryMemento memento = redoStack.pop();
            undoStack.push(memento);
            return memento;
        }
        return null;
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }
}
