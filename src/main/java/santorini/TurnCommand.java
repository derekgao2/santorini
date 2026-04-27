package santorini;

public record TurnCommand(Worker worker, Direction moveDirection, Direction buildDirection) {
    public void execute(Board board) {
        board.applyMove(this);
    }

    @Override
    public String toString() {
        return worker.symbol() + "," + moveDirection.token() + "," + buildDirection.token();
    }
}
