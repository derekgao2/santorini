package santorini;

import java.util.ArrayList;
import java.util.List;

public class HistoryManager {
    private final List<GameState> snapshots;
    private int cursor;

    public HistoryManager() {
        this.snapshots = new ArrayList<>();
        this.cursor = -1;
    }

    public void reset(GameState initialState) {
        snapshots.clear();
        snapshots.add(initialState.clone());
        cursor = 0;
    }

    public void record(GameState state) {
        truncateFuture();
        snapshots.add(state.clone());
        cursor = snapshots.size() - 1;
    }

    public GameState undo() {
        if (canUndo()) {
            cursor -= 1;
        }
        return current();
    }

    public GameState redo() {
        if (canRedo()) {
            cursor += 1;
        }
        return current();
    }

    public boolean canUndo() {
        return cursor > 0;
    }

    public boolean canRedo() {
        return cursor >= 0 && cursor < snapshots.size() - 1;
    }

    public void truncateFuture() {
        if (cursor < 0) {
            return;
        }
        while (snapshots.size() > cursor + 1) {
            snapshots.remove(snapshots.size() - 1);
        }
    }

    private GameState current() {
        if (snapshots.isEmpty()) {
            throw new IllegalStateException("HistoryManager has no snapshots.");
        }
        return snapshots.get(cursor).clone();
    }
}
