package santorini;

public enum PlayerColor {
    WHITE("white"),
    BLUE("blue");

    private final String label;

    PlayerColor(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public String workerLabel() {
        return this == WHITE ? "(AB)" : "(YZ)";
    }

    public PlayerColor opponent() {
        return this == WHITE ? BLUE : WHITE;
    }
}
