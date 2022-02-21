package map;

/**
 * The valid Colors for RailConnections and Cards in the game Trains.
 */
public enum RailColor {
    RED("red"),
    BLUE("blue"),
    GREEN("green"),
    WHITE("white");

    private String color;

    public String toString() {
        return color;
    }

    private RailColor(String color) {
        this.color = color;
    }

    public static RailColor fromString(String color) {
        for(RailColor value : RailColor.values()) {
            if(value.toString().equals(color)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid color");
    }
}
