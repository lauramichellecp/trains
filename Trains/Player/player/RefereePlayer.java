package player;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import referee.TrainsPlayerHand;
import strategy.IStrategy;
import strategy.TurnAction;

/**
 * A player that only relies on one strategy, computes moves as they are requested, and otherwise
 * stores the minimal amount of information necessary for the strategy to make decisions.
 */
public class RefereePlayer implements IPlayer {

  private static final int NUM_DESTINATIONS_TO_CHOOSE = 2;

  private final IStrategy playerStrategy;

  private ITrainMap map;

  private int numStartRails;

  private Map<RailCard, Integer> startingHand;

  private Set<Destination> chosenDestinations;

  /**
   * Constructs this player from the given strategy.
   *
   * @param strategy the strategy.
   */
  public RefereePlayer(IStrategy strategy) {
    this.playerStrategy = strategy;
  }

  /**
   * Construc this player with the given strategy and the given map
   * @param strategyFilePath
   * @param map
   */
  public RefereePlayer(IStrategy strategy, ITrainMap map) {
    this.playerStrategy = strategy;
    this.map = map;
  }

  /**
   * Construc this player with the given strategy and the given map
   * @param strategyFilePath
   * @param map
   */
  public RefereePlayer(String strategyFilePath, ITrainMap map) {
    try {
      Class<IStrategy> strategyClass = loadStrategyClass(strategyFilePath);
      this.playerStrategy = strategyClass.getConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Could not instantiate strategy class: " + e.getMessage());
    }
    this.map = map;
  }

  /**
   * Constructs this player from the strategy contained in the specified .class file. The .class
   * file should have only one strategy class contained in it, and should not have external
   * dependencies to ensure smooth construction.
   *
   * @param strategyFilePath the path to the .class file containing the strategy.
   * @throws RuntimeException if there is any issue loading the strategy from the class file.
   */
  public RefereePlayer(String strategyFilePath) throws RuntimeException {
    try {
      Class<IStrategy> strategyClass = loadStrategyClass(strategyFilePath);
      this.playerStrategy = strategyClass.getConstructor().newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Could not instantiate strategy class: " + e.getMessage());
    }
  }

  /**
   * Loads the strategy class from the given file path using the {@link StrategyClassLoader}.
   * @param strategyFilePath the file path to the .class file containing the strategy class.
   * @return the Class<IStrategy> loaded from the given file path.
   * @throws IOException if thrown by the StrategyClassLoader.
   */
  private static Class<IStrategy> loadStrategyClass(String strategyFilePath) throws IOException {
    return (Class<IStrategy>) new StrategyClassLoader().loadClassFromFile(strategyFilePath);
  }

  @Override
  public ITrainMap tournamentStart() {
    if(map == null) {
      throw new RuntimeException("A Referee player does not need to provide a map for a tournament");
    } else {
      return map;
    }
  }

  @Override
  public void tournamentResult(boolean winner) {
    //Do nothing
  }

  /**
   * Stores the given map, number of starting rails, and the starting hand as a TrainsPlayerHand for
   * use in choosing destinations.
   * @param map the map for the entire game.
   * @param numRails the initial number of rails for this player.
   * @param cards the starting hand of rail cards.
   */
  @Override
  public void setup(ITrainMap map, int numRails, List<RailCard> cards) {
    this.map = map;
    this.numStartRails = numRails;
    this.startingHand = new TrainsPlayerHand(cards).getHand();
  }

  /**
   * Returns destinations not preferred by this player's strategy.
   * @param options the possible destinations to choose from.
   * @return the set of 3 destinations this player does NOT want to keep for the game.
   */
  @Override
  public Set<Destination> chooseDestinations(Set<Destination> options) {
    Set<Destination> result = new HashSet<>(options);
    this.chosenDestinations =
        this.playerStrategy.chooseDestinations(
            options, NUM_DESTINATIONS_TO_CHOOSE, this.map, this.numStartRails, this.startingHand);
    result.removeAll(this.chosenDestinations);

    return result;
  }

  /**
   * Returns the TurnAction chosen by this player's strategy.
   * @param playerGameState the state of the game from this player's perspective at the time the turn action is requested.
   * @return the TurnAction chosen by this player's strategy.
   */
  @Override
  public TurnAction takeTurn(IPlayerGameState playerGameState) {
    return this.playerStrategy.takeTurn(
      playerGameState, this.map, new HashSet<>(this.chosenDestinations));
  }

  /**
   * Performs no action to receive cards.
   * @param drawnCards the list of cards drawn this turn.
   */
  @Override
  public void receiveCards(List<RailCard> drawnCards) {}

  /**
   * Performs no action.
   * @param thisPlayerWon true if this player won, false otherwise.
   */
  @Override
  public void winNotification(boolean thisPlayerWon) {}
}
