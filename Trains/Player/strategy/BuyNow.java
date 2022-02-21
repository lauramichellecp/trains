package strategy;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import map.Destination;
import map.IRailConnection;
import map.ITrainMap;
import utils.ComparatorUtils;

/**
 * A strategy that chooses destinations in reverse lexicographic order and preferred connections in
 * lexicographic order for pair of cities, but and never chooses to draw cards over purchasing a
 * connection. See {@link Destination#compareTo(Destination)} and {@link
 * ComparatorUtils#lexicographicCompareConnection(IRailConnection, IRailConnection)} for information
 * about ordering.
 *
 * <p>Uses {@link Hold10} methods for abstraction where appropriate.
 */
public class BuyNow extends AStrategy {

  /**
   * Returns list of destinations in reverse lexicographic order. (Reverse of {@link
   * Hold10#getPreferredDestinations(Set, ITrainMap, int, Map)}.
   *
   * @param destinationOptions the set of destinations from which to choose.
   * @param map the map for the game.
   * @param numStartingRails the number of starting rails.
   * @param startingHand the starting hand of the player.
   * @return list of destinations in reverse lexicographic order.
   */
  @Override
  protected List<Destination> getPreferredDestinations(
      Set<Destination> destinationOptions,
      ITrainMap map,
      int numStartingRails,
      Map<RailCard, Integer> startingHand) {
    List<Destination> destinationsSorted =
        destinationOptions.stream().sorted().collect(Collectors.toList());
    Collections.reverse(destinationsSorted);
    return destinationsSorted;
  }

  /**
   * Returns connection that is lexicographically first, copying the Hold10 strategy.
   *
   * @param affordableConnections a non-empty set of connections that can be purchased by the
   *     player.
   * @param currentPlayerGameState the game state to inform the decision of turn action.
   * @param chosenDestinations the set of destinations chosen at the beginning of the game.
   * @return lexicographically first IRailConnection from the given set.
   */
  @Override
  protected IRailConnection getPreferredConnection(
      Set<IRailConnection> affordableConnections,
      IPlayerGameState currentPlayerGameState,
      Set<Destination> chosenDestinations) {
    return affordableConnections.stream()
        .min(ComparatorUtils::lexicographicCompareConnection)
        .get();
  }

  /**
   * Returns false since this strategy never chooses drawing cards over acquiring connections.
   *
   * @param affordableConnections a non-empty set of connections that can be purchased by the *
   *     player.
   * @param currentPlayerGameState the game state to inform the decision of turn action. * @return
   *     the most preferred connection to acquire.
   * @param chosenDestinations the set of destinations chosen at the beginning of the game.
   * @return false.
   */
  @Override
  protected boolean chooseDrawCards(
      Set<IRailConnection> affordableConnections,
      IPlayerGameState currentPlayerGameState,
      Set<Destination> chosenDestinations) {
    return false;
  }
}
