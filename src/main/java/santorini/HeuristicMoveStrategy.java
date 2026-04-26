package santorini;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HeuristicMoveStrategy implements MoveStrategy {
    private static final int HEIGHT_WEIGHT = 3;
    private static final int CENTER_WEIGHT = 2;
    private static final int DISTANCE_WEIGHT = 1;
    private static final int WINNING_MOVE_SCORE = 1_000_000;

    private final Random random;

    public HeuristicMoveStrategy() {
        this(new Random());
    }

    public HeuristicMoveStrategy(Random random) {
        this.random = random;
    }

    @Override
    public TurnCommand selectMove(Board board, PlayerColor playerColor, Scanner scanner) {
        List<TurnCommand> legalMoves = board.legalMovesFor(playerColor);
        int bestScore = Integer.MIN_VALUE;
        List<TurnCommand> bestMoves = new ArrayList<>();

        for (TurnCommand move : legalMoves) {
            int score = scoreMove(board, playerColor, move);
            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        return bestMoves.get(random.nextInt(bestMoves.size()));
    }

    private int scoreMove(Board board, PlayerColor playerColor, TurnCommand move) {
        if (board.moveReachesLevelThree(move)) {
            return WINNING_MOVE_SCORE;
        }
        ScoreBreakdown score = board.scoreAfterMove(playerColor, move);
        return HEIGHT_WEIGHT * score.heightScore()
                + CENTER_WEIGHT * score.centerScore()
                + DISTANCE_WEIGHT * score.distanceScore();
    }
}
