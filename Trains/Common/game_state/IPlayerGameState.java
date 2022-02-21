package game_state;

import java.util.List;
import java.util.Map;
import java.util.Set;
import map.IRailConnection;
import map.ITrainMap;
import map.Destination;

/**
 * Represents a read-only view of the state of a game of Trains that is visible to one specific
 * player. This means all public information in the game, and private information that is visible to
 * this specific player. This only shows a view of the state at one point in time, and will not be
 * updated as the game progresses.
 * <p>
 * This object also excludes the following pieces of information about the game that the player is
 * expected to track separately:
 * <ul>
 *     <li>The game map</li>
 *     <li>Destinations</li>
 * </ul>
 */
public interface IPlayerGameState {

    /**
     * Gets the set of connections that are owned by the player that corresponds to this
     * IPlayerGameState.
     *
     * @return The IRailConnections owned by this player.
     */
    Set<IRailConnection> getOwnedConnections();

    /**
     * Calculates all connections in the given map that are not occupied by any player in the game.
     *
     * @param map The map for this game of Trains.
     * @return All connections in the given map that are not owned by any player in this game.
     */
    Set<IRailConnection> calculateUnoccupiedConnections(ITrainMap map);

    /**
     * Gets the cards that are in the hand of the player that this IPlayerGameState corresponds to.
     *
     * @return A map of RailCard to the number of cards of that color in this player's hand. An
     * entry is present for every type of RailCard.
     */
    Map<RailCard, Integer> getCardsInHand();

    Set<Destination> getDestinations();

    /**
     * Gets the number of rails left in the bank of the player that this IPlayerGameState
     * corresponds to.
     *
     * @return the number of rails.
     */
    int getNumRails();

    /**
     * Gets the public information about each other player that is known by the player that this
     * IPlayerGameState corresponds to.
     *
     * @return Public information about this player's opponents.
     */
    List<IOpponentInfo> getOpponentInfo();
}