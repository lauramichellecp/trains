package strategy;

import map.IRailConnection;

/**
 * Represents one player's action on their turn in a game of Trains.
 */
public class TurnAction {

    private final Action actionType;
    private final IRailConnection railConnection;

    /**
     * Private constructor directly initializes fields without validation
     * @param action the type of action this turn represents.
     * @param railConnection the IRailConnection for an ACQUIRE_CONNECTION action, or null otherwise.
     */
    private TurnAction(Action action, IRailConnection railConnection) {
        this.actionType = action;
        this.railConnection = railConnection;
    }

    /**
     * Creates a strategy.TurnAction that represents drawing cards from the central deck of rail cards.
     *
     * @return A strategy.TurnAction representing acquiring the given connection.
     */
    public static TurnAction createDrawCards() {
        return new TurnAction(Action.DRAW_CARDS, null);
    }

    /**
     * Creates a strategy.TurnAction that represents acquiring a rail connection on the map in a game of
     * Trains. The acquire connection action also requires the player to specify which connection
     * they would like to acquire.
     *
     * @param railConnection The connection that the player would like to acquire.
     * @return A strategy.TurnAction representing acquiring the given connection.
     */
    public static TurnAction createAcquireConnection(IRailConnection railConnection) {
        return new TurnAction(Action.ACQUIRE_CONNECTION, railConnection);
    }
    
    /**
     * Gets what type of action the player wishes to perform on their turn.
     *
     * @return One of the valid action types for a turn in the game Trains.
     */
    public Action getActionType() {
        return this.actionType;
    }

    /**
     * Gets the connection that the player would like to acquire, only if this Turn action
     * represents acquiring a connection. Otherwise throw an exception.
     *
     * @return The connection to acquire or empty.
     * @throws IllegalStateException if the TurnAction does not have a rail connection.
     */
    public IRailConnection getRailConnection() throws IllegalStateException {
        if (this.railConnection == null) {
            throw new IllegalStateException("This TurnAction does not have n IRailConnection.");
        }
        return this.railConnection;
    }
}
