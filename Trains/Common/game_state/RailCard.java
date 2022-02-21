package game_state;

/**
 * The valid Colors for Cards in the game Trains.
 */
public enum RailCard {
    RED,
    BLUE,
    GREEN,
    WHITE;

    public String toString() {
        switch(this) {
            case RED:
                return "red";
            case BLUE:
                return "blue";
            case GREEN:
                return "green";
            case WHITE:
                return "white";
            default:
                throw new IllegalStateException("Unknown Rail Card");
        }
    }
}
