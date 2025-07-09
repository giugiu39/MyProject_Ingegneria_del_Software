package it.unical.gestorelibreria.memento;

import java.util.Stack;

public class LibraryCareTaker {

    private final Stack<LibraryMemento> undoStack = new Stack<>();
    private final Stack<LibraryMemento> redoStack = new Stack<>();

    public void saveState(LibraryMemento memento) {
        undoStack.push(memento);
        redoStack.clear(); // invalida redo perché nuova azione
    }

    public LibraryMemento undo(LibraryMemento currentState) {
        if (!undoStack.isEmpty()) {
            // Sposta lo stato corrente in redoStack
            redoStack.push(currentState);
            // Prendi lo stato precedente da undoStack
            return undoStack.pop();
        }
        return null;
    }

    public LibraryMemento redo(LibraryMemento currentState) {
        if (!redoStack.isEmpty()) {
            // Sposta lo stato corrente in undoStack
            undoStack.push(currentState);
            // Prendi lo stato da redoStack
            return redoStack.pop();
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