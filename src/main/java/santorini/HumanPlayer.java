package santorini;

public class HumanPlayer implements PlayerStrategy {
    private static final String MOVE_PROMPT = "Select a direction to move (n, ne, e, se, s, sw, w, nw)";
    private static final String BUILD_PROMPT = "Select a direction to build (n, ne, e, se, s, sw, w, nw)";

    private final Team team;

    public HumanPlayer(Team team) {
        this.team = team;
    }

    @Override
    public Move chooseMove(Board board, GameState state, SantoriniRules rules, UserInterface ui) {
        Worker worker = chooseWorker(board, state, rules, ui);
        if (worker == null) {
            return null;
        }

        Direction moveDirection = chooseMoveDirection(state, rules, worker, ui);
        if (moveDirection == null) {
            return null;
        }

        Direction buildDirection = chooseBuildDirection(state, rules, worker, moveDirection, ui);
        if (buildDirection == null) {
            return null;
        }

        return new Move(worker.symbol(), moveDirection, buildDirection);
    }

    private Worker chooseWorker(Board board, GameState state, SantoriniRules rules, UserInterface ui) {
        while (true) {
            char selected = ui.promptWorker(team);
            if (ui.isInputClosed()) {
                return null;
            }
            Worker worker = board.getWorker(selected);
            if (worker == null) {
                ui.printMessage("Not a valid worker");
                continue;
            }
            if (worker.team() != team) {
                ui.printMessage("That is not your worker");
                continue;
            }
            if (!rules.canWorkerMove(state, worker)) {
                ui.printMessage("That worker cannot move");
                continue;
            }
            return worker;
        }
    }

    private Direction chooseMoveDirection(GameState state, SantoriniRules rules, Worker worker, UserInterface ui) {
        while (true) {
            Direction direction = ui.promptDirection(MOVE_PROMPT);
            if (ui.isInputClosed()) {
                return null;
            }
            if (direction == null) {
                ui.printMessage("Not a valid direction");
                continue;
            }
            if (!rules.canMove(state, worker, direction)) {
                ui.printMessage("Cannot move " + direction.token());
                continue;
            }
            return direction;
        }
    }

    private Direction chooseBuildDirection(
            GameState state,
            SantoriniRules rules,
            Worker worker,
            Direction moveDirection,
            UserInterface ui) {
        while (true) {
            Direction direction = ui.promptDirection(BUILD_PROMPT);
            if (ui.isInputClosed()) {
                return null;
            }
            if (direction == null) {
                ui.printMessage("Not a valid direction");
                continue;
            }
            if (!rules.canBuildAfterMove(state, worker, moveDirection, direction)) {
                ui.printMessage("Cannot build " + direction.token());
                continue;
            }
            return direction;
        }
    }
}
