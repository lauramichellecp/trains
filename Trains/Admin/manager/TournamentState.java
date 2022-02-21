package manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import map.ITrainMap;
import player.IPlayer;

/**
 * Represents a viewable and modifiable tournament state for the game Trains starting from after the
 * players have submitted their maps (just before the start of the first round of games) until the last round
 * of games has concluded and the tournament is finished.
 *
 * It contains information about the map for the tournament, which players are still playing in the tournament, and
 * which players have been eliminated. It also stores information about the number of consecutive "useless rounds"
 * there have been
 *
 * <p>NOTE: The only reason this exists as a separate class in a separate file to the manager is for organization
 * and testing purposes rather than logical/design purposes.
 */
class TournamentState {
  ITrainMap tournamentMap;
  List<IPlayer> stillAlive;
  Set<IPlayer> cheaters;
  int numUselessRounds;

  TournamentState(ITrainMap tournamentMap, List<IPlayer> initialPlayers, Set<IPlayer> initialCheaters) {
    this.tournamentMap = tournamentMap;
    this.stillAlive = initialPlayers;
    this.cheaters = initialCheaters;
    this.numUselessRounds = 0;
  }

  /**
   * Updates the tournament players that are still alive in the tournament, as well as the cheaters.
   * Also increments the number of consecutive useless rounds by one given that there was a useless round.
   *
   * @param roundSurvivors the ITournamentPlayer(s) still active after the last round.
   * @param roundCheaters the ITournamentPlayer(s) that have cheated in the last round.
   * @param uselessRound whether the last round was "useless" or not.
   */
  void updateOnRoundEnd(List<IPlayer> roundSurvivors, Set<IPlayer> roundCheaters, boolean uselessRound) {
    Set<IPlayer> noCheaters = new HashSet<>(roundSurvivors);
    noCheaters.removeAll(roundCheaters);
    if (noCheaters.size() < roundSurvivors.size()) {
      throw new RuntimeException("There are cheaters in your round survivors");
    }
    this.stillAlive = roundSurvivors;
    this.cheaters.addAll(roundCheaters);

    if (uselessRound) {
      this.numUselessRounds += 1;
    } else {
      this.numUselessRounds = 0;
    }
  }

  /**
   * Determines if the tournament is still active, meaning you can play more rounds of games.
   * @return whether there are enough players alive for one game or if there haven't been more useless rounds than the max.
   */
  boolean tournamentStillActive(int maxNumberOfUselessRounds, int minPlayersInGame) {
    return this.numUselessRounds <= maxNumberOfUselessRounds && this.stillAlive.size() >= minPlayersInGame;
  }

  /**
   * Determines if there are enough alive players for exactly one more game in the next round.
   * @param minPlayersInGame the minimum number of players allowed in a single game
   * @param maxPlayersInGame the maximum number of players allowed in a single game
   * @return whether there are enough players for one more game.
   */
  boolean oneMoreGame(int minPlayersInGame, int maxPlayersInGame) {
    int numAlive = this.stillAlive.size();
    return numAlive >= minPlayersInGame && numAlive <= maxPlayersInGame;
  }
}
