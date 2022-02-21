package referee;

import player.IPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A value class for a game-agnostic report of the players' scores, rankings, and removal status.
 *
 * <p>Players are identified by their integer index in a list of players that is maintained by the
 * creator of this object. No validity checks are made to ensure validity of scores, indices, or
 * uniqueness of indices since this is a simple value class.
 */
public class GameEndReport {

  /** List of players who were not removed, in descending order of their score. */
  public List<PlayerScore> playerRanking;

  /** The indices of removed players. */
  public Set<IPlayer> removedPlayers;

  public GameEndReport(
      List<PlayerScore> playerRanking, Set<IPlayer> removedPlayers) {
    this.playerRanking = playerRanking;
    this.removedPlayers = removedPlayers;
  }

  /**
   * Creates a ranking of players from a list of player scores.
   *
   * @param playerRanking a List of PlayerScore representing the scores for each IPlayer.
   * @return a List of Set of IPlayer representing the ranking per score, where players with the same score
   * are put in the same set in the ranking.
   */
  public static List<Set<IPlayer>> reportToOutcome(List<GameEndReport.PlayerScore> playerRanking) {
    // Sort the player rankings by descending score into a list
    List<Set<IPlayer>> rankingResult = new ArrayList<>();

    List<Integer> playerScores = playerRanking.stream().map(s -> s.score).collect(Collectors.toList());
    for (Integer s : playerScores) {
        List<GameEndReport.PlayerScore> scores = playerRanking.stream().filter(p -> s.equals(p.score)).collect(Collectors.toList());
        rankingResult.add(scores.stream().map(p -> p.player).collect(Collectors.toSet()));
    }

    return rankingResult;
  }

  /**
   * A class representing a Player with a score, which holds an IRefereePlayer and their score.
   */
  public static class PlayerScore {
    public IPlayer player;
    public int score;


    public PlayerScore(IPlayer player, int score) {
      this.player = player;
      this.score = score;
    }
  }
}
