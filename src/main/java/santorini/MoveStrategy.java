package santorini;

import java.util.Scanner;

public interface MoveStrategy {
    TurnCommand selectMove(Board board, PlayerColor playerColor, Scanner scanner);
}
