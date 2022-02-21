package game_state;

import java.util.HashSet;
import java.util.Set;
import map.IRailConnection;

/**
 * Represents information about an opponent that should be visible to other players
 */
public class OpponentInfo implements IOpponentInfo {
  private Set<IRailConnection> ownedConnections;

  /**
   * Constructs an OpponentInfo from a given set of owned rail connections.
   * @param ownedConnections the own connections for the opponent
   */
  public OpponentInfo(Set<IRailConnection> ownedConnections) {
    this.ownedConnections = new HashSet<>(ownedConnections);
  }

  @Override
  public Set<IRailConnection> getOwnedConnections() {
    return new HashSet<>(this.ownedConnections);
  }
}
