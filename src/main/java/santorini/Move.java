package santorini;

public record Move(char workerSymbol, Direction moveDirection, Direction buildDirection) {
    @Override
    public String toString() {
        return workerSymbol + "," + moveDirection.token() + "," + buildDirection.token();
    }
}
