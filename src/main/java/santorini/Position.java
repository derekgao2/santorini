package santorini;

public record Position(int row, int col) {
    public Position move(Direction direction) {
        return new Position(row + direction.rowDelta(), col + direction.colDelta());
    }
}
