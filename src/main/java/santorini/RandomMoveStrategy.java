package santorini;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RandomMoveStrategy implements MoveStrategy {
    private final Random random;

    public RandomMoveStrategy() {
        this(new Random());
    }

    public RandomMoveStrategy(Random random) {
        this.random = random;
    }

    @Override
    public TurnCommand selectMove(Board board, PlayerColor playerColor, Scanner scanner) {
        List<TurnCommand> legalMoves = board.legalMovesFor(playerColor);
        int choice = random.nextInt(legalMoves.size());
        return legalMoves.get(choice);
    }
}
