package referee;

import game_state.RailCard;
import java.util.Set;
import map.Destination;
import map.IRailConnection;

/**
 * A mutable set of information that the Referee keeps track of about a player in a game, such as
 * their hand of cards, number of rails, destinations, and occupied connections.
 *
 * <p>It represents a player's resources starting after the destinations have been selected, just
 * before the first turn of play.
 */
public interface IPlayerData {

  /**
   * Gets a mutable reference to the player's hand of cards.
   *
   * @return the hand of cards corresponding to this player.
   */
  IPlayerHand<RailCard> getPlayerHand();

  /**
   * Returns the number of rails this player has.
   *
   * @return int number of rails.
   */
  int getNumRails();

  /**
   * Sets the number of rails for this player to have left in their bank.
   *
   * @param numRails the new number of rails.
   * @throws IllegalArgumentException if the given number of rails is negative.
   */
  void setNumRails(int numRails) throws IllegalArgumentException;

  /**
   * Retrieves a mutable set of this player's chosen destinations.
   *
   * @return the mutable set of destinations.
   */
  Set<Destination> getDestinations();

  /**
   * Retrieves a mutable set of this player's owned rail connections.
   *
   * @return the mutable set of owned connections.
   */
  Set<IRailConnection> getOwnedConnections();
}
