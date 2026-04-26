package santorini;

public record GameMemento(BoardSnapshot boardSnapshot, PlayerColor currentPlayer, int turnNumber) {
}
