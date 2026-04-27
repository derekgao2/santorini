package santorini;

public class GameState {
    private final Board board;
    private int turnNumber;
    private Team currentTeam;

    public GameState() {
        this.board = new Board();
        this.turnNumber = 1;
        this.currentTeam = Team.WHITE;
    }

    public GameState(Board board, int turnNumber, Team currentTeam) {
        this.board = board;
        this.turnNumber = turnNumber;
        this.currentTeam = currentTeam;
    }

    public Board board() {
        return board;
    }

    public int turnNumber() {
        return turnNumber;
    }

    public Team currentTeam() {
        return currentTeam;
    }

    public void advanceTurn() {
        turnNumber += 1;
        switchCurrentTeam();
    }

    public void switchCurrentTeam() {
        currentTeam = currentTeam.opponent();
    }

    public GameState clone() {
        Board clonedBoard = new Board();
        clonedBoard.restoreSnapshot(board.cloneSnapshot());
        return new GameState(clonedBoard, turnNumber, currentTeam);
    }
}
