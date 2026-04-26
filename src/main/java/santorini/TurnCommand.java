package santorini;

public record TurnCommand(Worker worker, Direction moveDirection, Direction buildDirection) {
    @Override
    public String toString() {
        return worker.symbol() + "," + moveDirection.token() + "," + buildDirection.token();
    }
}
