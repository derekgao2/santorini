package santorini;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeuristicAIPlayer implements PlayerStrategy {
    private static final int WINNING_MOVE_SCORE = 1_000_000;

    private final Team team;
    private final Random rng;
    private final int c1;
    private final int c2;
    private final int c3;

    public HeuristicAIPlayer(Team team) {
        this(team, new Random(), 3, 2, 1);
    }

    public HeuristicAIPlayer(Team team, Random rng, int c1, int c2, int c3) {
        this.team = team;
        this.rng = rng;
        this.c1 = c1;
        this.c2 = c2;
        this.c3 = c3;
    }

    @Override
    public Move chooseMove(Board board, GameState state, SantoriniRules rules, UserInterface ui) {
        List<Move> legalMoves = rules.generateLegalMoves(state, team);
        int bestScore = Integer.MIN_VALUE;
        List<Move> bestMoves = new ArrayList<>();
        for (Move move : legalMoves) {
            int moveScore = scoreMove(board, move);
            if (moveScore > bestScore) {
                bestScore = moveScore;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (moveScore == bestScore) {
                bestMoves.add(move);
            }
        }
        return bestMoves.get(rng.nextInt(bestMoves.size()));
    }

    public ScoreBreakdown evaluatePosition(Board board, Team evaluatedTeam) {
        return board.scoreFor(evaluatedTeam);
    }

    private int scoreMove(Board board, Move move) {
        if (board.moveReachesLevelThree(move)) {
            return WINNING_MOVE_SCORE;
        }
        ScoreBreakdown scoreBreakdown = board.scoreAfterMove(team, move);
        return scoreBreakdown.total(c1, c2, c3);
    }
}
