package referee;

import game_state.IOpponentInfo;
import game_state.IPlayerGameState;
import game_state.OpponentInfo;
import game_state.PlayerGameState;
import game_state.RailCard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import map.IRailConnection;
import map.ITrainMap;
import player.IPlayer;
import utils.RailCardUtils;

/**
 * Represents a viewable and modifiable game state for the game Trains starting from after the
 * players have selected their destinations (just before the first turn) until the game has ended
 * (after the last turn concludes). It contains both static information, such as the ITrainMap game
 * board and the order of the players in addition to dynamic information, such as the cards in each
 * player's hand, the occupation status of each rail connection, and which players have been
 * removed.
 *
 * <p>NOTE: The only reason this exists as a separate class in a separate file is for organization
 * and testing purposes rather than logical/design purposes.
 */
class RefereeGameState {

  private static final int PLAYER_NUM_RAILS_GAME_OVER = 2;
  private static final int PLAYER_NUM_CARDS_PER_DRAW = 2;

  private final List<PlayerInfoPair> playersInTurnOrder;
  private final Set<IPlayer> removedPlayers;
  private int numConsecutiveInsignificantTurns;
  private int indexOfCurrentPlayer;
  private final List<RailCard> deck;
  private final ITrainMap map;

  /**
   * Constructs this RefereeGameState with defensive copies where appropriate.
   *
   * @param playersInTurnOrder the players in turn order that are present at the start of the game.
   * @param removedPlayers the players who were removed prior to the start of the game.
   * @param deck the deck of cards at the start of the game.
   * @param map the unchanging map for the game.
   */
  RefereeGameState(
      List<PlayerInfoPair> playersInTurnOrder,
      Set<IPlayer> removedPlayers,
      List<RailCard> deck,
      ITrainMap map) {
    this.playersInTurnOrder = new ArrayList<>(playersInTurnOrder);
    this.removedPlayers = new HashSet<>(removedPlayers);
    this.deck = new ArrayList<>(deck);
    this.indexOfCurrentPlayer = 0;
    this.numConsecutiveInsignificantTurns = 0;
    this.map = map;
  }

  //region Getters

  /**
   * Returns a copy of the ActionChecker object that this class uses to determine if an player's
   * action follows the rules of the game.
   *
   * @return The ActionChecker used by this class.
   */
  ActionChecker getActionChecker() {
    return new ActionChecker();
  }

  /**
   * Returns the playerData of the currently active player.
   *
   * @return player data of the currently active player
   */
  PlayerInfoPair getActivePlayer() {
    return this.playersInTurnOrder.get(this.indexOfCurrentPlayer);
  }

  /**
   * Returns active players in turn order, defensively copied.
   *
   * @return defensively copied list of PlayerInfoPair in turn order.
   */
  List<PlayerInfoPair> getPlayersInTurnOrder() {
    return new ArrayList<>(this.playersInTurnOrder);
  }

  /**
   * Gets the set of removed players.
   *
   * @return defensively copied set of removed IPlayer.
   */
  Set<IPlayer> getRemovedPlayers() {
    return new HashSet<>(this.removedPlayers);
  }

  /**
   * Gets the number of consecutive insignificant turns tracked by this game state.
   *
   * @return integer >= 0 for number of consecutive insignificant turns that have elapsed prior to
   *     the current player's turn.
   */
  int getNumConsecutiveInsignificantTurns() {
    return this.numConsecutiveInsignificantTurns;
  }
  //endregion

  /**
   * Advance to the next turn and update tracker of whether the turn was significant.
   *
   * @param significant whether the past turn was significant. If true, set counter to 0, if false
   *     increment by 1.
   */
  void advanceTurn(boolean significant) {
    this.indexOfCurrentPlayer = (this.indexOfCurrentPlayer + 1) % this.playersInTurnOrder.size();
    if (significant) {
      this.numConsecutiveInsignificantTurns = 0;
    } else {
      this.numConsecutiveInsignificantTurns += 1;
    }
  }

  /**
   * Removes the active player so it does not take any more turns. This automatically advances to
   * the next turn and is counted as a significant turn (resets counter to 0).
   */
  void removeActivePlayer() {
    // Because a player's rails, cards, destinations, and connections are calculated
    // from the playerData, this removal automatically discards/removes those things as well
    PlayerInfoPair removed = this.playersInTurnOrder.remove(this.indexOfCurrentPlayer);
    this.removedPlayers.add(removed.playerCommunication);
    this.numConsecutiveInsignificantTurns = 0;

    if (this.playersInTurnOrder.size() != 0) {
      this.indexOfCurrentPlayer %= this.playersInTurnOrder.size();
    }
  }

  /**
   * Gets the state of the game that is visible to the currently active player. This includes all of
   * the active player's private information, and public information about each other player in the
   * game.
   *
   * @return The game state that is visible to the currently active player.
   */
  IPlayerGameState getActivePlayerState() {
    return new PlayerGameState(this.getActivePlayer().playerData, this.calculateOpponentInfo());
  }

  /** Calculates opponent info for creating the active player game state. */
  private List<IOpponentInfo> calculateOpponentInfo() {
    List<IOpponentInfo> result = new ArrayList<>();
    for (int index = 0; index < this.playersInTurnOrder.size(); index += 1) {
      if (index != this.indexOfCurrentPlayer) {
        result.add(
            new OpponentInfo(this.playersInTurnOrder.get(index).playerData.getOwnedConnections()));
      }
    }
    return result;
  }

  /**
   * Represents the active player choosing to draw cards as their action for their turn. This method
   * will add the given number of cards from the top of the deck, add them to the active player's
   * hand. Will draw all remaining cards if PLAYER_NUM_CARDS_PER_DRAW is > number of cards
   * remaining.
   *
   * @return a copy of the cards given to the player.
   */
  List<RailCard> drawCardsForActivePlayer() {
    int numCards = PLAYER_NUM_CARDS_PER_DRAW;
    if (numCards > this.deck.size()) {
      numCards = this.deck.size();
    }

    IPlayerHand<RailCard> activePlayerHand = getActivePlayer().playerData.getPlayerHand();
    List<RailCard> drawnCards = new ArrayList<>();
    for (int ii = 0; ii < numCards; ii++) {
      RailCard oneCard = this.deck.remove(0);
      drawnCards.add(oneCard);
      activePlayerHand.addCardsToHand(oneCard, 1);
    }

    return drawnCards;
  }

  /**
   * Represents the active player choosing to acquire a connection as their action for their turn.
   * This method will attempt to acquire the connection for the active player by removing the
   * appropriate number of rails and cards from the players resources.
   *
   * <p>If the active player is not able to acquire the given connection, the game state is not
   * modified.
   *
   * @param desiredConnection The connection that the active player would like to acquire.
   * @return whether the connection was able to be acquired according to the action checker.
   */
  boolean acquireConnectionForActivePlayer(IRailConnection desiredConnection)
      throws IllegalArgumentException {
    if (!this.getActionChecker()
        .canAcquireConnection(this.getActivePlayerState(), map, desiredConnection)) {
      return false;
    } else {
      IPlayerData playerData = this.getActivePlayer().playerData;
      // Remove rails and cards from player's hand
      playerData.setNumRails(playerData.getNumRails() - desiredConnection.getLength());
      playerData
          .getPlayerHand()
          .removeCardsFromHand(
              RailCardUtils.railCardFromColor(desiredConnection.getColor()),
              desiredConnection.getLength());
      // Add connection to player's list of connections
      playerData.getOwnedConnections().add(desiredConnection);
      return true;
    }
  }

  /**
   * Returns whether the game is over. The game is over if the number of consecutive insignificant
   * turns is equal to the total number of players or if the active player has too few rails (less
   * than 3).
   *
   * <p>NOTE: This method ought to be called at the beginning of a turn, since it is a snapshot
   * calculation, and will have unpredictable results otherwise.
   *
   * @return true if game is over before active player takes turn, false otherwise.
   */
  boolean isGameOver() {
    return numConsecutiveInsignificantTurns == this.playersInTurnOrder.size()
        || this.getActivePlayerState().getNumRails() <= PLAYER_NUM_RAILS_GAME_OVER;
  }

  /**
   * Calculate scores for each player remaining in the game.
   *
   * @return a mapping from IPlayer (hashed by reference hashing) to their score. Only players that
   *     made it to the end of the game are present - cheating players can be accessed from this'
   *     maintained set.
   */
  Map<IPlayer, Integer> calculatePlayerScores() {
    List<Integer> scoresInOrder =
        ScoreCalculator.scorePlayers(
            new ArrayList<>(
                this.playersInTurnOrder.stream()
                    .map((p) -> p.playerData)
                    .collect(Collectors.toList())));

    Map<IPlayer, Integer> result = new HashMap<>();
    for (int index = 0; index < scoresInOrder.size(); index += 1) {
      result.put(this.playersInTurnOrder.get(index).playerCommunication, scoresInOrder.get(index));
    }

    return result;
  }
}

/**
 * Contains the info about a player to associate the game state data with the component that
 * implements the Player API for communication.
 */
class PlayerInfoPair {
  IPlayer playerCommunication;
  IPlayerData playerData;

  /** Constructor assigns fields directly. */
  PlayerInfoPair(IPlayer playerCommunication, IPlayerData playerData) {
    this.playerCommunication = playerCommunication;
    this.playerData = playerData;
  }
}
