package santorini;

import java.util.Scanner;

public record Player(MoveStrategy strategy) {
    public enum Type {
        HUMAN,
        RANDOM,
        HEURISTIC
    }

    public TurnCommand chooseTurn(Board board, PlayerColor playerColor, Scanner scanner) {
        return strategy.selectMove(board, playerColor, scanner);
    }
}
