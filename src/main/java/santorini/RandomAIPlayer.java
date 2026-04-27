package santorini;

import java.util.List;
import java.util.Random;

public class RandomAIPlayer implements PlayerStrategy {
    private final Team team;
    private final Random rng;

    public RandomAIPlayer(Team team) {
        this(team, new Random());
    }

    public RandomAIPlayer(Team team, Random rng) {
        this.team = team;
        this.rng = rng;
    }

    @Override
    public Move chooseMove(Board board, GameState state, SantoriniRules rules, UserInterface ui) {
        List<Move> legalMoves = rules.generateLegalMoves(state, team);
        return legalMoves.get(rng.nextInt(legalMoves.size()));
    }
}
