package game_state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import map.Destination;
import map.IRailConnection;
import map.ITrainMap;
import referee.IPlayerData;

/**
 * Represents a read-only view of the current state of the game for a player of the game Trains.
 */
public class PlayerGameState implements IPlayerGameState {
  private final Set<IRailConnection> ownedConnections;
  private final Map<RailCard, Integer> cardsInHand;
  private final int numRails;
  private final List<IOpponentInfo> opponentInfo;
  private final Set<Destination> destinations;

  /**
   * Constructs this PlayerGameState from the corresponding player data and the opponent information.
   * @param playerData an IPlayerData representing the resources of a player
   * @param opponentInfo a List of IOpponentInformation representing the information about other players
   */
  public PlayerGameState(IPlayerData playerData, List<IOpponentInfo> opponentInfo) {
    Objects.requireNonNull(playerData);
    Objects.requireNonNull(opponentInfo);

    this.ownedConnections = new HashSet<>(playerData.getOwnedConnections());
    this.cardsInHand = new HashMap<>(playerData.getPlayerHand().getHand());
    this.numRails = playerData.getNumRails();
    this.opponentInfo = new ArrayList<>(opponentInfo);
    this.destinations = new HashSet<>(playerData.getDestinations());
  }

  @Override
  public Set<IRailConnection> getOwnedConnections() {
    return new HashSet<>(this.ownedConnections);
  }

  @Override
  public Set<IRailConnection> calculateUnoccupiedConnections(ITrainMap map) {
    Set<IRailConnection> unoccupiedConnections = map.getRailConnections();

    unoccupiedConnections.removeAll(this.ownedConnections);
    for (IOpponentInfo oneOpponentInfo : this.opponentInfo) {
      unoccupiedConnections.removeAll(oneOpponentInfo.getOwnedConnections());
    }

    return unoccupiedConnections;
  }

  @Override
  public Map<RailCard, Integer> getCardsInHand() {
    return new HashMap<>(this.cardsInHand);
  }

  @Override
  public int getNumRails() {
    return this.numRails;
  }

  @Override
  public List<IOpponentInfo> getOpponentInfo() {
    return new ArrayList<>(this.opponentInfo);
  }

  @Override
  public Set<Destination> getDestinations() {
    return destinations;
  }
}