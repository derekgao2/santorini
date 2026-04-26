package santorini;

import java.util.Scanner;

public class HumanInputStrategy implements MoveStrategy {
    @Override
    public TurnCommand selectMove(Board board, PlayerColor playerColor, Scanner scanner) {
        Worker worker = promptWorker(board, playerColor, scanner);
        Direction moveDirection = promptMoveDirection(board, worker, scanner);
        Direction buildDirection = promptBuildDirection(board, worker, moveDirection, scanner);
        return new TurnCommand(worker, moveDirection, buildDirection);
    }

    private Worker promptWorker(Board board, PlayerColor playerColor, Scanner scanner) {
        while (true) {
            System.out.println("Select a worker to move");
            String input = readLineOrThrow(scanner);
            Worker worker = Worker.fromInput(input);
            if (worker == null) {
                System.out.println("Not a valid worker");
                continue;
            }
            if (worker.owner() != playerColor) {
                System.out.println("That is not your worker");
                continue;
            }
            if (!board.workerCanMoveAndBuild(worker)) {
                System.out.println("That worker cannot move");
                continue;
            }
            return worker;
        }
    }

    private Direction promptMoveDirection(Board board, Worker worker, Scanner scanner) {
        while (true) {
            System.out.println("Select a direction to move (n, ne, e, se, s, sw, w, nw)");
            String input = readLineOrThrow(scanner);
            Direction direction = Direction.fromInput(input);
            if (direction == null) {
                System.out.println("Not a valid direction");
                continue;
            }
            if (!board.canMove(worker, direction)) {
                System.out.println("Cannot move " + direction.token());
                continue;
            }
            return direction;
        }
    }

    private Direction promptBuildDirection(Board board, Worker worker, Direction moveDirection, Scanner scanner) {
        while (true) {
            System.out.println("Select a direction to build (n, ne, e, se, s, sw, w, nw)");
            String input = readLineOrThrow(scanner);
            Direction direction = Direction.fromInput(input);
            if (direction == null) {
                System.out.println("Not a valid direction");
                continue;
            }
            if (!board.canBuildAfterMove(worker, moveDirection, direction)) {
                System.out.println("Cannot build " + direction.token());
                continue;
            }
            return direction;
        }
    }

    private String readLineOrThrow(Scanner scanner) {
        return scanner.nextLine();
    }
}
