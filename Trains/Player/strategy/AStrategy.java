package strategy;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import map.Destination;
import map.IRailConnection;
import map.ITrainMap;
import utils.RailCardUtils;

/**
 * Represents any strategy that can decide a preference ordering for destinations, choose whether to
 * draw cards, or choose a most preferred connection to acquire given a set of possible connections.
 */
public abstract class AStrategy implements IStrategy {

  /**
   * Determines a preference ordering for destinations and chooses the 'numToChoose' most preferred
   * destinations based off of the given initial game information.
   *
   * @param destinationOptions the set of possible destinations to choose from.
   * @param numToChoose the number of destinations to select.
   * @param map the map for the game.
   * @param numStartingRails the number of starting rails.
   * @param startingHand the starting hand of the player.
   * @return a set of Destination that is a subset of destinationOptions of size numToChoose.
   */
  @Override
  public Set<Destination> chooseDestinations(
      Set<Destination> destinationOptions,
      int numToChoose,
      ITrainMap map,
      int numStartingRails,
      Map<RailCard, Integer> startingHand) {

    if (destinationOptions.size() < numToChoose) {
      throw new IllegalArgumentException(
          "Must provide a number of destination options greater than or equal to the number of them to choose.");
    }

    List<Destination> sortedDestinations =
        this.getPreferredDestinations(destinationOptions, map, numStartingRails, startingHand);
    // Return the most preferred destinations
    return new HashSet<>(sortedDestinations.subList(0, numToChoose));
  }

  /**
   * Returns an action to draw cards if the strategy prefers it or if it is the only option. Returns
   * an action to acquire the most preferred affordable/available connection otherwise.
   *
   * @param currentPlayerGameState the state of the game on which the turn is taken.
   * @param map the game map for this game of Trains.
   * @param chosenDestinations the set of destinations chosen at the beginning of the game.
   * @return a strategy.TurnAction representing this strategy's choice of action.
   */
  @Override
  public TurnAction takeTurn(
      IPlayerGameState currentPlayerGameState, ITrainMap map, Set<Destination> chosenDestinations) {

    Set<IRailConnection> canAcquire = calculateAcquirableConnections(currentPlayerGameState, map);
    // Automatically draw cards if no connections can be bought,
    // or if strategy specifically requests it
    if (canAcquire.isEmpty()
        || this.chooseDrawCards(canAcquire, currentPlayerGameState, chosenDestinations)) {
      return TurnAction.createDrawCards();
    } else {
      return TurnAction.createAcquireConnection(
          this.getPreferredConnection(canAcquire, currentPlayerGameState, chosenDestinations));
    }
  }

  /**
   * Calculates which unoccupied connections this player can currently afford to acquire. A player
   * can afford to acquire a connection when they have at least as many rails in their bank and at
   * least as many cards in hand of the corresponding color as the length of the connection.
   *
   * @param currentPlayerGameState the state of the game on which the turn is taken.
   * @param map the game map for this game of Trains.
   * @return The set of connections that this player can afford to acquire and are unoccupied.
   */
  private static Set<IRailConnection> calculateAcquirableConnections(
      IPlayerGameState currentPlayerGameState, ITrainMap map) {

    Predicate<IRailConnection> canAffordConnection =
        (railConnection) ->
            AStrategy.canAfford(
                currentPlayerGameState.getCardsInHand(),
                currentPlayerGameState.getNumRails(),
                railConnection);
    return currentPlayerGameState.calculateUnoccupiedConnections(map).stream()
        .filter(canAffordConnection)
        .collect(Collectors.toSet());
  }

  /**
   * Determines if the given hand of cards can be used to purchase the given IRailConnection.
   *
   * @param cardsInHand the hand of cards to query.
   * @param railConnection the connection in question.
   * @return true if connection can be acquired, false otherwise.
   */
  private static boolean canAfford(
      Map<RailCard, Integer> cardsInHand, int numRails, IRailConnection railConnection) {
    boolean enoughCards =
        cardsInHand.get(RailCardUtils.railCardFromColor(railConnection.getColor()))
            >= railConnection.getLength();
    boolean enoughRails = numRails >= railConnection.getLength();
    return enoughCards && enoughRails;
  }

  /**
   * Request for strategy to produce a list of destinations in decreasing order of preference.
   * Returned list of destinations must have a 1-1 correspondence with destinations in the given
   * set.
   *
   * @param destinationOptions the set of destinations from which to choose.
   * @param map the map for the game.
   * @param numStartingRails the number of starting rails.
   * @param startingHand the starting hand of the player.
   * @return the ranking of destinations from most to least preferred.
   */
  protected abstract List<Destination> getPreferredDestinations(
      Set<Destination> destinationOptions,
      ITrainMap map,
      int numStartingRails,
      Map<RailCard, Integer> startingHand);

  /**
   * Request for strategy to produce the most preferred connection to acquire.
   *
   * @param affordableConnections a non-empty set of connections that can be purchased by the
   *     player.
   * @param currentPlayerGameState the game state to inform the decision of turn action.
   * @param chosenDestinations the set of destinations chosen at the beginning of the game.
   * @return the most preferred connection to acquire.
   */
  protected abstract IRailConnection getPreferredConnection(
      Set<IRailConnection> affordableConnections,
      IPlayerGameState currentPlayerGameState,
      Set<Destination> chosenDestinations);

  /**
   * Request for whether strategy would have the player choose to draw cards, even if player can
   * afford a connection.
   *
   * @param affordableConnections a non-empty set of connections that can be purchased by the *
   *     player.
   * @param currentPlayerGameState the game state to inform the decision of turn action. * @return
   *     the most preferred connection to acquire.
   * @param chosenDestinations the set of destinations chosen at the beginning of the game.
   * @return whether to draw cards even if there are affordable connections.
   */
  protected abstract boolean chooseDrawCards(
      Set<IRailConnection> affordableConnections,
      IPlayerGameState currentPlayerGameState,
      Set<Destination> chosenDestinations);
}
