package santorini;

import java.util.Locale;
import java.util.Optional;

public class GameManager {
    private GameState state;
    private final SantoriniRules rules;
    private final HistoryManager historyManager;
    private final PlayerStrategy whitePlayer;
    private final PlayerStrategy bluePlayer;
    private final boolean scoreEnabled;
    private final boolean historyEnabled;
    private final UserInterface ui;

    public GameManager(Config config, PlayerFactory playerFactory, UserInterface ui) {
        this.state = new GameState();
        this.rules = new SantoriniRules();
        this.historyManager = new HistoryManager();
        this.whitePlayer = playerFactory.create(config.whiteType(), Team.WHITE);
        this.bluePlayer = playerFactory.create(config.blueType(), Team.BLUE);
        this.scoreEnabled = config.scoreEnabled();
        this.historyEnabled = config.historyEnabled();
        this.ui = ui;
        if (historyEnabled) {
            historyManager.reset(state);
        }
    }

    public void playAgainLoop() {
        boolean playAgain = true;
        while (playAgain && !ui.isInputClosed()) {
            run();
            if (ui.isInputClosed()) {
                return;
            }
            playAgain = ui.promptPlayAgain();
            if (playAgain) {
                resetGame();
            }
        }
    }

    public void run() {
        while (!ui.isInputClosed()) {
            Board board = state.board();
            Team currentTeam = state.currentTeam();

            ui.printBoard(board);
            ui.printTurn(state.turnNumber(), currentTeam, scoreEnabled ? board.scoreFor(currentTeam) : null);

            Optional<Team> winner = rules.winner(state);
            if (winner.isPresent()) {
                ui.printWinner(winner.get());
                return;
            }
            if (rules.isStuck(state, currentTeam)) {
                ui.printWinner(currentTeam.opponent());
                return;
            }

            if (historyEnabled && handleHistoryAction()) {
                continue;
            }

            Move move = activePlayer(currentTeam).chooseMove(board, state, rules, ui);
            if (ui.isInputClosed()) {
                return;
            }

            rules.applyMove(state, move);
            ui.printMove(move, scoreEnabled ? board.scoreFor(currentTeam) : null);
            state.advanceTurn();

            if (historyEnabled) {
                historyManager.record(state);
            }
        }
    }

    private boolean handleHistoryAction() {
        while (true) {
            String action = ui.promptHistoryAction();
            if (ui.isInputClosed()) {
                return true;
            }
            if (action == null) {
                ui.printMessage("Not a valid history option");
                continue;
            }
            switch (action.toLowerCase(Locale.ROOT)) {
                case "next":
                    return false;
                case "undo":
                    state = historyManager.undo();
                    return true;
                case "redo":
                    state = historyManager.redo();
                    return true;
                default:
                    ui.printMessage("Not a valid history option");
            }
        }
    }

    private PlayerStrategy activePlayer(Team team) {
        return team == Team.WHITE ? whitePlayer : bluePlayer;
    }

    private void resetGame() {
        state = new GameState();
        if (historyEnabled) {
            historyManager.reset(state);
        }
    }
}
