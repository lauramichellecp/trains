package game_state;

import java.util.Set;
import map.IRailConnection;

/**
 * Represents information about a single player (an opponent) visible to a different player
 */
public interface IOpponentInfo {

  /**
   * Gets the set of connections that are owned by an opponent player.
   *
   * @return The IRailConnections owned by a particular opponent
   */
  Set<IRailConnection> getOwnedConnections();
}
