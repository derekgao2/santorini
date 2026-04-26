package santorini;

import java.util.ArrayDeque;
import java.util.Deque;

public class HistoryManager {
    private final Deque<GameMemento> undoStack;
    private final Deque<GameMemento> redoStack;

    public HistoryManager() {
        this.undoStack = new ArrayDeque<>();
        this.redoStack = new ArrayDeque<>();
    }

    public void pushUndo(GameMemento memento) {
        undoStack.push(memento);
        redoStack.clear();
    }

    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public GameMemento undo(GameMemento current) {
        if (undoStack.isEmpty()) {
            return current;
        }
        redoStack.push(current);
        return undoStack.pop();
    }

    public GameMemento redo(GameMemento current) {
        if (redoStack.isEmpty()) {
            return current;
        }
        undoStack.push(current);
        return redoStack.pop();
    }
}
