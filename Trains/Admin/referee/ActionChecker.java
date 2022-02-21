package referee;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.Map;
import java.util.Optional;
import map.IRailConnection;
import map.ITrainMap;
import utils.RailCardUtils;

/**
 * This class contains methods that can be used to determine if it is legal, according to the rules
 * of the game Trains, for a certain player to take a certain action.
 */
public class ActionChecker implements IActionChecker {

    /**
     * Determines if the player that corresponds to the given IPlayerGameState is allowed to acquire
     * the given IRailConnection given the state of the game described in the IPlayerGameState.
     *
     * @param gameStateForPlayer An object that contains the information visible to one player in a
     *                           game of Trains.
     * @param desiredConnection  The Connection that the given player would like to check if they
     *                           can acquire.
     * @return Whether or not the player that corresponds to the given IPlayerGameState is allowed
     * to acquire the given IRailConnection.
     */
    @Override
    public boolean canAcquireConnection(IPlayerGameState gameStateForPlayer, ITrainMap map,
        IRailConnection desiredConnection) {
        boolean exists = connectionExists(map, desiredConnection);
        boolean connectionAvailable = connectionAvailable(gameStateForPlayer, desiredConnection,
            map);
        boolean hasEnoughRails = hasEnoughRails(gameStateForPlayer, desiredConnection.getLength());
        boolean hasEnoughCards = hasEnoughCards(gameStateForPlayer, desiredConnection);
        return exists && connectionAvailable && hasEnoughRails && hasEnoughCards;
    }

    /**
     * Determines if the given connection exists inside of the given ITrainMap.
     *
     * @param map               The map in a game of Trains.
     * @param desiredConnection The connection that may or may not exist in the map.
     * @return Whether the connection exists in the map.
     */
    private static boolean connectionExists(ITrainMap map, IRailConnection desiredConnection) {
        Optional<IRailConnection> desiredConnectionInMap =
            map.getRailConnections().stream()
                .filter((connection) -> connection.sameRailConnection(desiredConnection))
                .findFirst();
        return desiredConnectionInMap.isPresent()
            && desiredConnectionInMap.get().getLength() == desiredConnection.getLength();
    }

    /**
     * Determines if the given connection is already occupied in the game state represented in the
     * given IPlayerGameState.
     *
     * @param gameStateForPlayer The state of a game of Trains that is visible to one player.
     * @param desiredConnection  The connection that may or may not already be acquired.
     * @return Whether or not the connection is already occupied.
     */
    private static boolean connectionAvailable(
        IPlayerGameState gameStateForPlayer, IRailConnection desiredConnection, ITrainMap map) {
        return gameStateForPlayer.calculateUnoccupiedConnections(map).contains(desiredConnection);
    }

    /**
     * Determines if the player that corresponds to the given IPlayerGameState has enough rails in
     * their bank to occupy a connection of the given length.
     *
     * @param gameStateForPlayer The state of a game of Trains that is visible to one player.
     * @param length             The length of the connection.
     * @return Whether or not the player has enough rails in their bank to occupy the connection.
     */
    private static boolean hasEnoughRails(IPlayerGameState gameStateForPlayer, int length) {
        return gameStateForPlayer.getNumRails() >= length;
    }

    /**
     * Determines if the player that corresponds to the given IPlayerGameState has enough cards of
     * the correct color in their hand to acquire the given connection.
     *
     * @param gameStateForPlayer The state of a game of Trains that is visible to one player.
     * @param desiredConnection  The connection that the player may or may not have enough cards to
     *                           acquire.
     * @return Whether the player that corresponds to the given IPlayerGameState has enough cards of
     * the correct color in their hand to acquire the given connection.
     */
    private static boolean hasEnoughCards(
        IPlayerGameState gameStateForPlayer, IRailConnection desiredConnection) {
        RailCard cardForConnection = RailCardUtils.railCardFromColor(desiredConnection.getColor());
        Map<RailCard, Integer> playerHand = gameStateForPlayer.getCardsInHand();

        return playerHand.containsKey(cardForConnection)
            && playerHand.get(cardForConnection) >= desiredConnection.getLength();
    }
}
