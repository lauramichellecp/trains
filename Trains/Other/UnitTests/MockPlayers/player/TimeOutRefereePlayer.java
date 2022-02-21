package player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.List;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import strategy.TurnAction;

public class TimeOutRefereePlayer implements IPlayer {

  public TimeOutRefereePlayer() {

  }

  @Override
  public void setup(ITrainMap map, int numRails, List<RailCard> cards) {

  }

  @Override
  public Set<Destination> chooseDestinations(Set<Destination> options) {
    try {
      Thread.sleep(1000000000);
    } catch (Exception e) {
      fail();
    }
    return null;
  }

  @Override
  public TurnAction takeTurn(IPlayerGameState playerGameState) {
    return TurnAction.createDrawCards();
  }

  @Override
  public void receiveCards(List<RailCard> drawnCards) {

  }

  @Override
  public void winNotification(boolean thisPlayerWon) {

  }

  @Override
  public ITrainMap tournamentStart() {
    throw new RuntimeException("Should not get here");
  }

  @Override
  public void tournamentResult(boolean winner) {

  }
}
