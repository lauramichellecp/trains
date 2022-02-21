package player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.List;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import strategy.Hold10;
import strategy.TurnAction;

public class MockEndGameCorrectlyRefereePlayer implements IPlayer {
    private final RefereePlayer underlyingPlayer;

    public MockEndGameCorrectlyRefereePlayer() {
        this.underlyingPlayer = new RefereePlayer(new Hold10());
    }

    @Override
    public void setup(ITrainMap map, int numRails, List<RailCard> cards) {
        this.underlyingPlayer.setup(map, numRails, cards);
    }

    @Override
    public Set<Destination> chooseDestinations(Set<Destination> options) {
        return this.underlyingPlayer.chooseDestinations(options);
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState playerGameState) {
        assertTrue(playerGameState.getNumRails() > 2);
        return this.underlyingPlayer.takeTurn(playerGameState);
    }

    @Override
    public void receiveCards(List<RailCard> drawnCards) {
        this.underlyingPlayer.receiveCards(drawnCards);
    }

    @Override
    public void winNotification(boolean thisPlayerWon) {
        this.underlyingPlayer.winNotification(thisPlayerWon);
    }

    @Override
    public ITrainMap tournamentStart() {
        return this.underlyingPlayer.tournamentStart();
    }

    @Override
    public void tournamentResult(boolean winner) {
        this.underlyingPlayer.tournamentResult(winner);
    }
}