package santorini;

import java.util.Scanner;

public class SantoriniApp {
    public static void main(String[] args) {
        Config config;
        try {
            config = Config.fromArgs(args);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            printUsage();
            return;
        }

        try (Scanner scanner = new Scanner(System.in)) {
            UserInterface ui = new UserInterface(scanner);
            PlayerFactory playerFactory = new PlayerFactory();
            GameManager gameManager = new GameManager(config, playerFactory, ui);
            gameManager.playAgainLoop();
        }
    }

    private static void printUsage() {
        System.out.println("Usage: [whitePlayer] [bluePlayer] [history] [score]");
        System.out.println("whitePlayer, bluePlayer: human | heuristic | random");
        System.out.println("history, score: on | off");
    }
}
