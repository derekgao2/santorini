package santorini;

public interface PlayerStrategy {
    Move chooseMove(Board board, GameState state, SantoriniRules rules, UserInterface ui);
}
