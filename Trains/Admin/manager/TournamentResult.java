package manager;

import java.util.Set;

import player.IPlayer;
import utils.OrderedPair;

/**
 * A class representing the result of a tournament, which is a pair of List<ITournamentPlayer> winners and cheaters
 */
public class TournamentResult extends OrderedPair<Set<IPlayer>> {

  public TournamentResult(Set<IPlayer> winners, Set<IPlayer> cheaters)
      throws NullPointerException {
    super(winners, cheaters);
  }
}
