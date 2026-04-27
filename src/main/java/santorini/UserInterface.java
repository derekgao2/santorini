package santorini;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class UserInterface {
    private final Scanner scanner;
    private boolean inputClosed;

    public UserInterface(Scanner scanner) {
        this.scanner = scanner;
        this.inputClosed = false;
    }

    public void printBoard(Board board) {
        System.out.println(board.renderAscii());
    }

    public void printTurn(int turnNumber, Team team, ScoreBreakdown score) {
        if (score == null) {
            System.out.println("Turn: " + turnNumber + ", " + team.label() + " " + team.workerLabel());
        } else {
            System.out.println("Turn: " + turnNumber + ", " + team.label() + " " + team.workerLabel() + ", " + score);
        }
    }

    public char promptWorker(Team team) {
        System.out.println("Select a worker to move");
        String input = readLine();
        if (input == null || input.trim().length() != 1) {
            return '\0';
        }
        return Character.toUpperCase(input.trim().charAt(0));
    }

    public Direction promptDirection(String message) {
        System.out.println(message);
        String input = readLine();
        if (input == null) {
            return null;
        }
        return Direction.fromInput(input);
    }

    public String promptHistoryAction() {
        System.out.println("undo, redo, or next");
        String input = readLine();
        if (input == null) {
            return null;
        }
        return input.trim().toLowerCase();
    }

    public boolean promptPlayAgain() {
        System.out.println("Play again?");
        String response = readLine();
        return response != null && response.trim().equalsIgnoreCase("yes");
    }

    public void printMove(Move move, ScoreBreakdown score) {
        if (score == null) {
            System.out.println(move);
        } else {
            System.out.println(move + " " + score);
        }
    }

    public void printWinner(Team winner) {
        System.out.println(winner.label() + " has won");
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public boolean isInputClosed() {
        return inputClosed;
    }

    private String readLine() {
        try {
            return scanner.nextLine();
        } catch (NoSuchElementException ex) {
            inputClosed = true;
            return null;
        }
    }
}
