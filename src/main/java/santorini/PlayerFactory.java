package santorini;

public class PlayerFactory {
    public Player createPlayer(Player.Type type) {
        MoveStrategy strategy = switch (type) {
            case HUMAN -> new HumanInputStrategy();
            case RANDOM -> new RandomMoveStrategy();
            case HEURISTIC -> new HeuristicMoveStrategy();
        };
        return new Player(strategy);
    }
}
