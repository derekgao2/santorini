package santorini;

import java.util.List;
import java.util.Optional;

public class SantoriniRules {
    public List<Move> generateLegalMoves(GameState state, Team team) {
        return state.board().legalMovesFor(team);
    }

    public boolean canWorkerMove(GameState state, Worker worker) {
        return state.board().workerCanMoveAndBuild(worker);
    }

    public boolean canMove(GameState state, Worker worker, Direction direction) {
        return state.board().canMove(worker, direction);
    }

    public boolean canBuildAfterMove(GameState state, Worker worker, Direction moveDirection, Direction buildDirection) {
        return state.board().canBuildAfterMove(worker, moveDirection, buildDirection);
    }

    public void applyMove(GameState state, Move move) {
        state.board().applyMove(move);
    }

    public Optional<Team> winner(GameState state) {
        return state.board().winnerByLevelThree();
    }

    public boolean isStuck(GameState state, Team team) {
        return generateLegalMoves(state, team).isEmpty();
    }
}
