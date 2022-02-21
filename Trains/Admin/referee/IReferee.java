package referee;

/**
 * Represents a game-agnostic referee that is capable of running a game from a single method call
 * and providing a ranking/scoring of participating players and the set of players that were
 * removed.
 *
 * <p>Specific implementations of the referee will exist for different games or variants of games.
 */
public interface IReferee {

  /**
   * Single call to this method plays the entire game from start to finish and yields a report of
   * the rankings, scores, and removed players.
   *
   * @return the rankings, scores, and removed players as a GameEndReport.
   */
  GameEndReport playGame();
}
