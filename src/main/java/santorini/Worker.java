package santorini;

public enum Worker {
    A('A', PlayerColor.WHITE),
    B('B', PlayerColor.WHITE),
    Y('Y', PlayerColor.BLUE),
    Z('Z', PlayerColor.BLUE);

    private final char symbol;
    private final PlayerColor owner;

    Worker(char symbol, PlayerColor owner) {
        this.symbol = symbol;
        this.owner = owner;
    }

    public char symbol() {
        return symbol;
    }

    public PlayerColor owner() {
        return owner;
    }

    public static Worker[] forPlayer(PlayerColor color) {
        if (color == PlayerColor.WHITE) {
            return new Worker[] {A, B};
        }
        return new Worker[] {Y, Z};
    }

    public static Worker fromInput(String input) {
        if (input == null) {
            return null;
        }
        String trimmed = input.trim();
        if (trimmed.length() != 1) {
            return null;
        }
        char candidate = Character.toUpperCase(trimmed.charAt(0));
        for (Worker worker : values()) {
            if (worker.symbol == candidate) {
                return worker;
            }
        }
        return null;
    }
}
