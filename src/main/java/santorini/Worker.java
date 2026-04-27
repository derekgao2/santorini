package santorini;

public enum Worker {
    A('A', Team.WHITE),
    B('B', Team.WHITE),
    Y('Y', Team.BLUE),
    Z('Z', Team.BLUE);

    private final char symbol;
    private final Team team;

    Worker(char symbol, Team team) {
        this.symbol = symbol;
        this.team = team;
    }

    public char symbol() {
        return symbol;
    }

    public Team team() {
        return team;
    }

    public static Worker[] forTeam(Team team) {
        if (team == Team.WHITE) {
            return new Worker[] {A, B};
        }
        return new Worker[] {Y, Z};
    }

    public static Worker fromToken(String input) {
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

    public static Worker fromSymbol(char symbol) {
        char candidate = Character.toUpperCase(symbol);
        for (Worker worker : values()) {
            if (worker.symbol == candidate) {
                return worker;
            }
        }
        return null;
    }
}
