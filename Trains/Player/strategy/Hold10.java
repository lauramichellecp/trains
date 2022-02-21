package strategy;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import map.Destination;
import map.IRailConnection;
import map.ITrainMap;
import utils.ComparatorUtils;


/**
 * A strategy that chooses destination and preferred connections in lexicographic order for pair of
 * cities, but keeps drawing cards until it has more than ten. See {@link
 * Destination#compareTo(Destination)} and {@link ComparatorUtils#lexicographicCompareConnection(IRailConnection,
 * IRailConnection)} for information about ordering.
 */
public class Hold10 extends AStrategy {

    /**
     * Returns list of destinations in lexicographic order.
     * @param destinationOptions the set of destinations from which to choose.
     * @param map the map for the game.
     * @param numStartingRails the number of starting rails.
     * @param startingHand the starting hand of the player.
     * @return list of destinations sorted according to destination comparator.
     */
    @Override
    protected List<Destination> getPreferredDestinations(
        Set<Destination> destinationOptions, ITrainMap map, int numStartingRails,
        Map<RailCard, Integer> startingHand) {
        return destinationOptions.stream().sorted().collect(Collectors.toList());
    }

    /**
     * Returns connection that is lexicographically first.
     * @param affordableConnections a non-empty set of connections that can be purchased by the
     *     player.
     * @param currentPlayerGameState the game state to inform the decision of turn action.
     * @param chosenDestinations the set of destinations chosen at the beginning of the game.
     * @return the rail connection that is lexicographically first in the given set.
     */
    @Override
    protected IRailConnection getPreferredConnection(
        Set<IRailConnection> affordableConnections, IPlayerGameState currentPlayerGameState,
        Set<Destination> chosenDestinations) {
        return affordableConnections.stream().min(ComparatorUtils::lexicographicCompareConnection)
            .get();
    }

    /**
     * Chooses to draw cards if player has <= 10 cards.
     * @param affordableConnections a non-empty set of connections that can be purchased by the *
     *     player.
     * @param currentPlayerGameState the game state to inform the decision of turn action. * @return
     *     the most preferred connection to acquire.
     * @param chosenDestinations the set of destinations chosen at the beginning of the game.
     * @return whether player has <= 10 total cards.
     */
    @Override
    protected boolean chooseDrawCards(
        Set<IRailConnection> affordableConnections, IPlayerGameState currentPlayerGameState,
        Set<Destination> chosenDestinations) {
        Collection<Integer> cardCounts = currentPlayerGameState.getCardsInHand().values();
        return cardCounts.stream().mapToInt(Integer::intValue).sum() <= 10;
    }
}
