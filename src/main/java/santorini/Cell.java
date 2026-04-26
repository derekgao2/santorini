package santorini;

public class Cell {
    private int level;
    private Worker occupant;

    public int getLevel() {
        return level;
    }

    public Worker getOccupant() {
        return occupant;
    }

    public void setOccupant(Worker occupant) {
        this.occupant = occupant;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean hasDome() {
        return level >= 4;
    }

    public void build() {
        if (level < 4) {
            level += 1;
        }
    }
}
