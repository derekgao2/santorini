package santorini;

public record ScoreBreakdown(int heightScore, int centerScore, int distanceScore) {
    @Override
    public String toString() {
        return "(" + heightScore + ", " + centerScore + ", " + distanceScore + ")";
    }
}
