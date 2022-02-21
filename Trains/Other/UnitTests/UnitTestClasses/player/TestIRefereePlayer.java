package player;

import game_state.IPlayerGameState;
import game_state.RailCard;
import map.Destination;
import map.ITrainMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import strategy.TurnAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

class DummyPlayer implements IPlayer {

    @Override
    public void setup(ITrainMap map, int numRails, List<RailCard> cards) {

    }

    @Override
    public Set<Destination> chooseDestinations(Set<Destination> options) {
        return null;
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState playerGameState) {
        return null;
    }

    @Override
    public void receiveCards(List<RailCard> drawnCards) {

    }

    @Override
    public void winNotification(boolean thisPlayerWon) {

    }

    @Override
    public ITrainMap tournamentStart() {
        return null;
    }

    @Override
    public void tournamentResult(boolean winner) {

    }
}

public class TestIRefereePlayer {

    @Test
    public void testPlayerEquality() {
        IPlayer dummyRefPlayer1 = new DummyPlayer();
        IPlayer dummyRefPlayer2 = new DummyPlayer();
        IPlayer dummyTournamentPlayer1 = new DummyPlayer();
        IPlayer dummyTournamentPlayer2 = new DummyPlayer();

        List<IPlayer> refereePlayers = new ArrayList<>(Arrays.asList(dummyRefPlayer1, dummyRefPlayer2, dummyTournamentPlayer1, dummyTournamentPlayer2));
        Assertions.assertNotEquals(dummyRefPlayer1, dummyRefPlayer2);
        Assertions.assertNotEquals(dummyTournamentPlayer1, dummyTournamentPlayer2);
        Assertions.assertEquals(dummyTournamentPlayer1, dummyTournamentPlayer1);
        Assertions.assertEquals(refereePlayers.indexOf(dummyTournamentPlayer1), 2);
        Assertions.assertEquals(refereePlayers.indexOf(dummyTournamentPlayer2), 3);
    }
}
