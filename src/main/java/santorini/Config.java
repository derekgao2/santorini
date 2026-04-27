package santorini;

public record Config(String whiteType, String blueType, boolean historyEnabled, boolean scoreEnabled) {
    public static Config fromArgs(String[] args) {
        if (args.length > 4) {
            throw new IllegalArgumentException("Too many arguments.");
        }
        for (String arg : args) {
            if (arg == null || arg.isBlank()) {
                throw new IllegalArgumentException("Arguments must be provided in order with no blanks.");
            }
        }

        String whiteType = args.length >= 1 ? parsePlayerType(args[0], "white") : "human";
        String blueType = args.length >= 2 ? parsePlayerType(args[1], "blue") : "human";
        boolean historyEnabled = args.length >= 3 ? parseOnOff(args[2], "history") : false;
        boolean scoreEnabled = args.length >= 4 ? parseOnOff(args[3], "score display") : false;
        return new Config(whiteType, blueType, historyEnabled, scoreEnabled);
    }

    private static String parsePlayerType(String rawValue, String playerLabel) {
        String normalized = rawValue.trim().toLowerCase();
        if ("human".equals(normalized) || "heuristic".equals(normalized) || "random".equals(normalized)) {
            return normalized;
        }
        throw new IllegalArgumentException("Invalid " + playerLabel + " player type: " + rawValue + ".");
    }

    private static boolean parseOnOff(String rawValue, String label) {
        String normalized = rawValue.trim().toLowerCase();
        return switch (normalized) {
            case "on" -> true;
            case "off" -> false;
            default -> throw new IllegalArgumentException("Invalid value for " + label + ": " + rawValue + ".");
        };
    }
}
