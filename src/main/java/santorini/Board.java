package santorini;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Board {
    private static final int SIZE = 5;

    private final Cell[][] cells;
    private final Map<Worker, Position> workerPositions;

    public Board() {
        cells = new Cell[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new Cell();
            }
        }
        workerPositions = new EnumMap<>(Worker.class);
        reset();
    }

    public final void reset() {
        workerPositions.clear();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new Cell();
            }
        }

        placeWorker(Worker.Y, new Position(1, 1));
        placeWorker(Worker.B, new Position(1, 3));
        placeWorker(Worker.A, new Position(3, 1));
        placeWorker(Worker.Z, new Position(3, 3));
    }

    private void placeWorker(Worker worker, Position position) {
        workerPositions.put(worker, position);
        cell(position).setOccupant(worker);
    }

    public Position getWorkerPosition(Worker worker) {
        return workerPositions.get(worker);
    }

    public boolean canMove(Worker worker, Direction moveDirection) {
        Position from = getWorkerPosition(worker);
        Position to = from.move(moveDirection);
        if (!isInBounds(to)) {
            return false;
        }
        if (cell(to).getOccupant() != null) {
            return false;
        }
        if (cell(to).hasDome()) {
            return false;
        }
        int fromLevel = cell(from).getLevel();
        int toLevel = cell(to).getLevel();
        return toLevel <= fromLevel + 1;
    }

    public boolean canBuildAfterMove(Worker worker, Direction moveDirection, Direction buildDirection) {
        if (!canMove(worker, moveDirection)) {
            return false;
        }

        Position from = getWorkerPosition(worker);
        Position movedTo = from.move(moveDirection);
        Position buildAt = movedTo.move(buildDirection);

        if (!isInBounds(buildAt)) {
            return false;
        }
        if (buildAt.equals(movedTo)) {
            return false;
        }
        if (cell(buildAt).hasDome()) {
            return false;
        }
        if (!buildAt.equals(from) && cell(buildAt).getOccupant() != null) {
            return false;
        }
        return true;
    }

    public boolean workerCanMoveAndBuild(Worker worker) {
        for (Direction moveDirection : Direction.values()) {
            if (!canMove(worker, moveDirection)) {
                continue;
            }
            for (Direction buildDirection : Direction.values()) {
                if (canBuildAfterMove(worker, moveDirection, buildDirection)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean moveReachesLevelThree(TurnCommand command) {
        Position from = getWorkerPosition(command.worker());
        Position to = from.move(command.moveDirection());
        return isInBounds(to) && cell(to).getLevel() == 3;
    }

    public void applyMove(TurnCommand command) {
        Worker worker = command.worker();
        Direction moveDirection = command.moveDirection();
        Direction buildDirection = command.buildDirection();

        if (!canMove(worker, moveDirection)) {
            throw new IllegalArgumentException("Illegal move direction");
        }
        if (!canBuildAfterMove(worker, moveDirection, buildDirection)) {
            throw new IllegalArgumentException("Illegal build direction");
        }

        Position from = getWorkerPosition(worker);
        Position to = from.move(moveDirection);
        Position buildAt = to.move(buildDirection);

        cell(from).setOccupant(null);
        cell(to).setOccupant(worker);
        workerPositions.put(worker, to);

        cell(buildAt).build();
    }

    public List<TurnCommand> legalMovesFor(PlayerColor playerColor) {
        List<TurnCommand> legalMoves = new ArrayList<>();
        for (Worker worker : Worker.values()) {
            if (worker.owner() != playerColor) {
                continue;
            }
            for (Direction moveDirection : Direction.values()) {
                if (!canMove(worker, moveDirection)) {
                    continue;
                }
                for (Direction buildDirection : Direction.values()) {
                    if (canBuildAfterMove(worker, moveDirection, buildDirection)) {
                        legalMoves.add(new TurnCommand(worker, moveDirection, buildDirection));
                    }
                }
            }
        }
        return legalMoves;
    }

    public BoardSnapshot cloneSnapshot() {
        int[][] heights = new int[SIZE][SIZE];
        Worker[][] workers = new Worker[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                heights[row][col] = cells[row][col].getLevel();
                workers[row][col] = cells[row][col].getOccupant();
            }
        }
        return new BoardSnapshot(heights, workers);
    }

    public void restoreSnapshot(BoardSnapshot snapshot) {
        workerPositions.clear();
        int[][] heights = snapshot.heightGrid();
        Worker[][] workers = snapshot.workerGrid();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setLevel(heights[row][col]);
                cells[row][col].setOccupant(workers[row][col]);
                Worker occupant = workers[row][col];
                if (occupant != null) {
                    workerPositions.put(occupant, new Position(row, col));
                }
            }
        }
    }

    public ScoreBreakdown scoreFor(PlayerColor playerColor) {
        return scoreForProjectedPositions(playerColor, workerPositions);
    }

    public ScoreBreakdown scoreAfterMove(PlayerColor playerColor, TurnCommand command) {
        Map<Worker, Position> projected = new EnumMap<>(Worker.class);
        projected.putAll(workerPositions);
        Position from = projected.get(command.worker());
        projected.put(command.worker(), from.move(command.moveDirection()));
        return scoreForProjectedPositions(playerColor, projected);
    }

    public Optional<PlayerColor> winnerByLevelThree() {
        for (Worker worker : Worker.values()) {
            Position pos = getWorkerPosition(worker);
            if (cell(pos).getLevel() == 3) {
                return Optional.of(worker.owner());
            }
        }
        return Optional.empty();
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < SIZE; row++) {
            sb.append("+--+--+--+--+--+\n");
            for (int col = 0; col < SIZE; col++) {
                Cell current = cells[row][col];
                sb.append("|");
                sb.append(current.getLevel());
                Worker occupant = current.getOccupant();
                sb.append(occupant == null ? " " : occupant.symbol());
            }
            sb.append("|\n");
        }
        sb.append("+--+--+--+--+--+");
        return sb.toString();
    }

    private boolean isInBounds(Position position) {
        return position.row() >= 0 && position.row() < SIZE && position.col() >= 0 && position.col() < SIZE;
    }

    private Cell cell(Position position) {
        return cells[position.row()][position.col()];
    }

    private ScoreBreakdown scoreForProjectedPositions(PlayerColor playerColor, Map<Worker, Position> projectedPositions) {
        Worker[] myWorkers = Worker.forPlayer(playerColor);
        Worker[] opponentWorkers = Worker.forPlayer(playerColor.opponent());

        int heightScore = 0;
        int centerScore = 0;
        for (Worker worker : myWorkers) {
            Position pos = projectedPositions.get(worker);
            heightScore += cell(pos).getLevel();
            centerScore += centerValue(pos);
        }

        int rawDistanceScore = 0;
        for (Worker opponent : opponentWorkers) {
            Position opponentPos = projectedPositions.get(opponent);
            int best = Integer.MAX_VALUE;
            for (Worker mine : myWorkers) {
                Position myPos = projectedPositions.get(mine);
                int distance = movementDistance(myPos, opponentPos);
                if (distance < best) {
                    best = distance;
                }
            }
            rawDistanceScore += best;
        }
        int distanceScore = 8 - rawDistanceScore;

        return new ScoreBreakdown(heightScore, centerScore, distanceScore);
    }

    private int centerValue(Position position) {
        if (position.row() == 2 && position.col() == 2) {
            return 2;
        }
        if (position.row() == 0 || position.row() == 4 || position.col() == 0 || position.col() == 4) {
            return 0;
        }
        return 1;
    }

    private int movementDistance(Position a, Position b) {
        int rowDistance = Math.abs(a.row() - b.row());
        int colDistance = Math.abs(a.col() - b.col());
        return Math.max(rowDistance, colDistance);
    }
}
