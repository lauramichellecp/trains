package referee;

import game_state.RailCard;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import map.Destination;
import map.IRailConnection;

/**
 * A direct implementation of the IPlayerData interface where fields are copied defensively in
 * constructor but returned as mutable references through getters.
 */
public class PlayerData implements IPlayerData {

  private final IPlayerHand<RailCard> hand;
  private int numRails;
  private final Set<Destination> destinations;
  private final Set<IRailConnection> ownedConnections;

  /**
   * Initializes this from the given fields directly, making defensive copies for all mutable types.
   *
   * All fields must be non-null.
   *
   * @param hand hand of cards.
   * @param numRails number of rails.
   * @param destinations set of destinations.
   * @param ownedConnections set of owned connections.
   */
  public PlayerData(
      IPlayerHand<RailCard> hand,
      int numRails,
      Set<Destination> destinations,
      Set<IRailConnection> ownedConnections) {
    Objects.requireNonNull(hand);
    Objects.requireNonNull(destinations);
    Objects.requireNonNull(ownedConnections);

    this.hand = new TrainsPlayerHand(hand.getHand());
    this.numRails = numRails;
    this.destinations = new HashSet<>(destinations);
    this.ownedConnections = new HashSet<>(ownedConnections);
  }

  /**
   * Creates this PlayerData by copying defensively from the given IPlayerData.
   * @param toCopy the IPlayerData to copy fields from when creating this PlayerData.
   */
  public PlayerData(IPlayerData toCopy) {
    this(
        toCopy.getPlayerHand(),
        toCopy.getNumRails(),
        toCopy.getDestinations(),
        toCopy.getOwnedConnections());
  }

  @Override
  public IPlayerHand<RailCard> getPlayerHand() {
    return this.hand;
  }

  @Override
  public int getNumRails() {
    return this.numRails;
  }

  @Override
  public void setNumRails(int numRails) throws IllegalArgumentException {
    if (numRails < 0) {
      throw new IllegalArgumentException("The number of rails must be set to a natural number");
    }
    this.numRails = numRails;
  }

  @Override
  public Set<Destination> getDestinations() {
    return this.destinations;
  }

  @Override
  public Set<IRailConnection> getOwnedConnections() {
    return this.ownedConnections;
  }
}
