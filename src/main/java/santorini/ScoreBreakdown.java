package santorini;

public record ScoreBreakdown(int heightScore, int centerScore, int distanceScore) {
    public int total(int c1, int c2, int c3) {
        return c1 * heightScore + c2 * centerScore + c3 * distanceScore;
    }

    @Override
    public String toString() {
        return "(" + heightScore + ", " + centerScore + ", " + distanceScore + ")";
    }
}
