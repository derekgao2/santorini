package santorini;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

public class SantoriniGame {
    private final Board board;
    private final Scanner scanner;
    private final Player whitePlayer;
    private final Player bluePlayer;
    private final boolean printScores;
    private final boolean historyEnabled;
    private final HistoryManager historyManager;
    private PlayerColor currentPlayer;
    private int turnNumber;
    private boolean inputClosed;

    public SantoriniGame(
            Scanner scanner,
            Player whitePlayer,
            Player bluePlayer,
            boolean printScores,
            boolean historyEnabled) {
        this.scanner = scanner;
        this.board = new Board();
        this.whitePlayer = whitePlayer;
        this.bluePlayer = bluePlayer;
        this.printScores = printScores;
        this.historyEnabled = historyEnabled;
        this.historyManager = new HistoryManager();
        this.currentPlayer = PlayerColor.WHITE;
        this.turnNumber = 1;
        this.inputClosed = false;
    }

    public void play() {
        while (!inputClosed) {
            printTurnHeader();

            Optional<PlayerColor> winnerByLevel = board.winnerByLevelThree();
            if (winnerByLevel.isPresent()) {
                announceWinner(winnerByLevel.get());
                return;
            }

            List<TurnCommand> legalMoves = board.legalMovesFor(currentPlayer);
            if (legalMoves.isEmpty()) {
                announceWinner(currentPlayer.opponent());
                return;
            }

            if (historyEnabled && handleHistoryChoice()) {
                continue;
            }

            PlayerColor movingPlayer = currentPlayer;
            TurnCommand command;
            try {
                command = activePlayer().chooseTurn(board, currentPlayer, scanner);
            } catch (NoSuchElementException ex) {
                inputClosed = true;
                continue;
            }
            if (historyEnabled) {
                historyManager.pushUndo(createMemento());
            }
            command.execute(board);
            if (printScores) {
                System.out.println(command + " " + board.scoreFor(movingPlayer));
            } else {
                System.out.println(command);
            }
            currentPlayer = currentPlayer.opponent();
            turnNumber += 1;
        }
    }

    public boolean isInputClosed() {
        return inputClosed;
    }

    private void printTurnHeader() {
        System.out.println(board.render());
        if (printScores) {
            System.out.println(
                    "Turn: "
                            + turnNumber
                            + ", "
                            + currentPlayer.label()
                            + " "
                            + currentPlayer.workerLabel()
                            + ", "
                            + board.scoreFor(currentPlayer));
        } else {
            System.out.println("Turn: " + turnNumber + ", " + currentPlayer.label() + " " + currentPlayer.workerLabel());
        }
    }

    private Player activePlayer() {
        return currentPlayer == PlayerColor.WHITE ? whitePlayer : bluePlayer;
    }

    private void announceWinner(PlayerColor winner) {
        System.out.println(winner.label() + " has won");
    }

    private boolean handleHistoryChoice() {
        while (true) {
            System.out.println("undo, redo, or next");
            String input;
            try {
                input = scanner.nextLine().trim().toLowerCase();
            } catch (NoSuchElementException ex) {
                inputClosed = true;
                return true;
            }
            switch (input) {
                case "next":
                    return false;
                case "undo":
                    restoreFrom(historyManager.undo(createMemento()));
                    return true;
                case "redo":
                    restoreFrom(historyManager.redo(createMemento()));
                    return true;
                default:
                    System.out.println("Not a valid history option");
            }
        }
    }

    private GameMemento createMemento() {
        return new GameMemento(board.cloneSnapshot(), currentPlayer, turnNumber);
    }

    private void restoreFrom(GameMemento memento) {
        board.restoreSnapshot(memento.boardSnapshot());
        currentPlayer = memento.currentPlayer();
        turnNumber = memento.turnNumber();
    }
}
