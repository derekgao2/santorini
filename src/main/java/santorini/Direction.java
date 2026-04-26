package santorini;

import java.util.Locale;

public enum Direction {
    N(-1, 0, "n"),
    NE(-1, 1, "ne"),
    E(0, 1, "e"),
    SE(1, 1, "se"),
    S(1, 0, "s"),
    SW(1, -1, "sw"),
    W(0, -1, "w"),
    NW(-1, -1, "nw");

    private final int rowDelta;
    private final int colDelta;
    private final String token;

    Direction(int rowDelta, int colDelta, String token) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
        this.token = token;
    }

    public int rowDelta() {
        return rowDelta;
    }

    public int colDelta() {
        return colDelta;
    }

    public String token() {
        return token;
    }

    public static Direction fromInput(String input) {
        String normalized = input.trim().toLowerCase(Locale.ROOT);
        for (Direction direction : values()) {
            if (direction.token.equals(normalized)) {
                return direction;
            }
        }
        return null;
    }
}
