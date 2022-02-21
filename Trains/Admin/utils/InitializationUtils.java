package utils;

import game_state.RailCard;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import map.Destination;
import map.ITrainMap;

/** Utility class for initialization related to referees and managers. */
public class InitializationUtils {


  /**
   * Computes the minimum number of destinations needed to play a game of trains
   *
   * @param numPlayers total number of players in the game
   * @param numDestOptionsPerPlayer the number of options of destinations for each player
   * @param numDestKeptPerPlayer the number of destinations chosen for each player
   * @return the number of destinations kept for each player (- 1) + the number of total options given to the last player
   */
  public static int minDestinationsToPlay(
      int numPlayers, int numDestOptionsPerPlayer, int numDestKeptPerPlayer) {
    // for every player except last, need 2 destinations, and last player needs 5 options
    return numDestKeptPerPlayer * (numPlayers - 1) + numDestOptionsPerPlayer;
  }

  /**
   * Returns whether the given number of destinations is not enough to play a game of Trains.
   *
   * @param totalNumDestinations the total number of destinations to check for
   * @param numPlayers the number of players in the game
   * @param numDestOptionsPerPlayer the number of options of destinations for each player
   * @param numDestKeptPerPlayer the number of destinations chosen for each player
   * @return true if the total number of destinations is less than the minimum number of destinations needed to play.
   */
  public static boolean notEnoughDestinations(int totalNumDestinations, int numPlayers,
                                              int numDestOptionsPerPlayer, int numDestKeptPerPlayer) {
    return totalNumDestinations < minDestinationsToPlay(numPlayers, numDestOptionsPerPlayer, numDestKeptPerPlayer);

  }

  /**
   * Provides a new List of Destinations given a TrainsMap.
   *
   * @param map a TrainsMap of the game
   * @return a shuffled list of all feasible destinations formed from the rail connections in a map
   */
  public static List<Destination> defaultDestinationProvider(ITrainMap map) {
    List<Destination> result =
        map.getAllPossibleDestinations().stream()
            .map((pair) -> new Destination(pair))
            .collect(Collectors.toList());
    Collections.shuffle(result);
    return result;
  }

  /**
   * Creates a new list of randomly generally RailCard that represents a deck of cards.
   *
   * @return a List of RailCard
   */
  public static List<RailCard> defaultDeckSupplier(int numCards) {
    List<RailCard> result = new ArrayList<>();
    Random cardSelector = new Random();
    RailCard[] railCardOptions = RailCard.values();
    for (int cardNumber = 0; cardNumber < numCards; cardNumber += 1) {
      result.add(railCardOptions[cardSelector.nextInt(railCardOptions.length)]);
    }
    return result;
  }

  /**
   * Creates a list of list of players representing all the players for every game of Trains, under
   * the condition that the given maxGroupSize is greater than or equal to 2 * minGroupSize - 1
   *
   * @param items a List of players
   * @param minGroupSize the minimum size of a group of players
   * @param maxGroupSize the maximum size of a group of players
   * @throws IllegalArgumentException if there are not more items than can fit in one group.
   * @return a List of List of players for each game.
   */
  public static <T> List<List<T>> orderedMaximumGrouping(
      List<T> items, int minGroupSize, int maxGroupSize) throws IllegalArgumentException {
    if (minGroupSize < 1 || maxGroupSize < 1 || maxGroupSize < 2 * minGroupSize - 1) {
      throw new IllegalArgumentException("Group sizes are invalid or incompatible.");
    }
    if (items.size() <= maxGroupSize) {
      throw new IllegalArgumentException("Must have more items than can fit in one group.");
    }
    List<List<T>> result = new ArrayList<>();

    for (int i = 0; i < items.size(); i += maxGroupSize) {
      int endIndex = Math.min(i + maxGroupSize, items.size());
      // Concurrent modification exception if array list is not copied
      result.add(new ArrayList<>(items.subList(i, endIndex)));
    }

    List<T> lastGame = result.get(result.size() - 1);

    if (lastGame.size() < minGroupSize) {
      List<T> secondToLast = result.get(result.size() - 2);
      transferGrouping(secondToLast, lastGame, minGroupSize);
    }
    return result;
  }

  /**
   * Backtracks the completed groupings to fill up the last grouping to fit the minimum group size allowed.
   *
   * @param completeGrouping the List for the complete grouping
   * @param incompleteGrouping the list for the incomplete grouping (that is its size < minGroupSize)
   * @param minGroupSize the minimum size for each grouping
   * @param <T> the item that should be taken from the complete grouping to satisfy the
   *           minimum condition for the incomplete grouping.
   */
  private static <T> void transferGrouping(List<T> completeGrouping, List<T> incompleteGrouping, int minGroupSize) {
      int numToTransfer = minGroupSize - incompleteGrouping.size();
      for (int numTransferred = 0;
          numTransferred < numToTransfer;
          numTransferred += 1) {
        T transferred = completeGrouping.remove(completeGrouping.size() - 1);
        incompleteGrouping.add(0, transferred);
      }
  }
}
