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
        initializeDefaultSetup();
    }

    public final void initializeDefaultSetup() {
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

    public Cell getCellAt(int row, int col) {
        return cells[row][col];
    }

    public Worker getWorker(char symbol) {
        return Worker.fromSymbol(symbol);
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

    public boolean moveReachesLevelThree(Move move) {
        Worker worker = Worker.fromSymbol(move.workerSymbol());
        Position from = getWorkerPosition(worker);
        Position to = from.move(move.moveDirection());
        return isInBounds(to) && cell(to).getLevel() == 3;
    }

    public void applyMove(Move move) {
        Worker worker = Worker.fromSymbol(move.workerSymbol());
        Direction moveDirection = move.moveDirection();
        Direction buildDirection = move.buildDirection();

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

    public List<Move> legalMovesFor(Team team) {
        List<Move> legalMoves = new ArrayList<>();
        for (Worker worker : Worker.values()) {
            if (worker.team() != team) {
                continue;
            }
            for (Direction moveDirection : Direction.values()) {
                if (!canMove(worker, moveDirection)) {
                    continue;
                }
                for (Direction buildDirection : Direction.values()) {
                    if (canBuildAfterMove(worker, moveDirection, buildDirection)) {
                        legalMoves.add(new Move(worker.symbol(), moveDirection, buildDirection));
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

    public ScoreBreakdown scoreFor(Team team) {
        return scoreForProjectedPositions(team, workerPositions);
    }

    public ScoreBreakdown scoreAfterMove(Team team, Move move) {
        Map<Worker, Position> projected = new EnumMap<>(Worker.class);
        projected.putAll(workerPositions);
        Worker worker = Worker.fromSymbol(move.workerSymbol());
        Position from = projected.get(worker);
        projected.put(worker, from.move(move.moveDirection()));
        return scoreForProjectedPositions(team, projected);
    }

    public Optional<Team> winnerByLevelThree() {
        for (Worker worker : Worker.values()) {
            Position pos = getWorkerPosition(worker);
            if (cell(pos).getLevel() == 3) {
                return Optional.of(worker.team());
            }
        }
        return Optional.empty();
    }

    public String renderAscii() {
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

    public String render() {
        return renderAscii();
    }

    private boolean isInBounds(Position position) {
        return position.row() >= 0 && position.row() < SIZE && position.col() >= 0 && position.col() < SIZE;
    }

    private Cell cell(Position position) {
        return cells[position.row()][position.col()];
    }

    private ScoreBreakdown scoreForProjectedPositions(Team team, Map<Worker, Position> projectedPositions) {
        Worker[] myWorkers = Worker.forTeam(team);
        Worker[] opponentWorkers = Worker.forTeam(team.opponent());

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
