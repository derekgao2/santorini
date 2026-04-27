package santorini;

public class PlayerFactory {
    public PlayerStrategy create(String type, Team team) {
        return switch (type.trim().toLowerCase()) {
            case "human" -> new HumanPlayer(team);
            case "random" -> new RandomAIPlayer(team);
            case "heuristic" -> new HeuristicAIPlayer(team);
            default -> throw new IllegalArgumentException("Unknown player type: " + type);
        };
    }
}
