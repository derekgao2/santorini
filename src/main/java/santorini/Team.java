package santorini;

public enum Team {
    WHITE("white", "(AB)"),
    BLUE("blue", "(YZ)");

    private final String label;
    private final String workerLabel;

    Team(String label, String workerLabel) {
        this.label = label;
        this.workerLabel = workerLabel;
    }

    public String label() {
        return label;
    }

    public String workerLabel() {
        return workerLabel;
    }

    public Team opponent() {
        return this == WHITE ? BLUE : WHITE;
    }
}
