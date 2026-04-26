package santorini;

import java.util.Scanner;
import java.util.NoSuchElementException;

public class Main {
    public static void main(String[] args) {
        GameConfig config;
        try {
            config = parseConfig(args);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            printUsage();
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            PlayerFactory playerFactory = new PlayerFactory();
            boolean playAgain = true;
            while (playAgain) {
                Player whitePlayer = playerFactory.createPlayer(config.whiteType());
                Player bluePlayer = playerFactory.createPlayer(config.blueType());

                SantoriniGame game = new SantoriniGame(
                        scanner,
                        whitePlayer,
                        bluePlayer,
                        config.printScores(),
                        config.historyEnabled());
                game.play();
                if (game.isInputClosed()) {
                    return;
                }
                System.out.println("Play again?");
                String response;
                try {
                    response = scanner.nextLine().trim();
                } catch (NoSuchElementException ex) {
                    return;
                }
                playAgain = response.equalsIgnoreCase("yes");
            }
        }
    }

    private static GameConfig parseConfig(String[] args) {
        if (args.length > 4) {
            throw new IllegalArgumentException("Too many arguments.");
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null || args[i].isBlank()) {
                throw new IllegalArgumentException("Arguments must be provided in order with no blanks.");
            }
        }

        Player.Type whiteType = args.length >= 1 ? parsePlayerType(args[0], "white") : Player.Type.HUMAN;
        Player.Type blueType = args.length >= 2 ? parsePlayerType(args[1], "blue") : Player.Type.HUMAN;
        boolean historyEnabled = args.length >= 3 ? parseOnOff(args[2], "history") : false;
        boolean printScores = args.length >= 4 ? parseOnOff(args[3], "score display") : false;

        return new GameConfig(whiteType, blueType, historyEnabled, printScores);
    }

    private static Player.Type parsePlayerType(String rawValue, String playerLabel) {
        String normalized = rawValue.trim().toLowerCase();
        switch (normalized) {
            case "human":
                return Player.Type.HUMAN;
            case "heuristic":
                return Player.Type.HEURISTIC;
            case "random":
                return Player.Type.RANDOM;
            default:
                throw new IllegalArgumentException(
                        "Invalid " + playerLabel + " player type: " + rawValue + ".");
        }
    }

    private static boolean parseOnOff(String rawValue, String label) {
        String normalized = rawValue.trim().toLowerCase();
        switch (normalized) {
            case "on":
                return true;
            case "off":
                return false;
            default:
                throw new IllegalArgumentException("Invalid value for " + label + ": " + rawValue + ".");
        }
    }

    private static void printUsage() {
        System.out.println("Usage: [whitePlayer] [bluePlayer] [history] [score]");
        System.out.println("whitePlayer, bluePlayer: human | heuristic | random");
        System.out.println("history, score: on | off");
    }

    private record GameConfig(
            Player.Type whiteType,
            Player.Type blueType,
            boolean historyEnabled,
            boolean printScores) {
    }
}
