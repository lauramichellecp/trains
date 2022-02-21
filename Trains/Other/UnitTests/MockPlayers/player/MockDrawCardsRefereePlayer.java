package player;

import static org.junit.jupiter.api.Assertions.assertEquals;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import strategy.TurnAction;

public class MockDrawCardsRefereePlayer implements IPlayer {
    private int numCardsDrawn;
    private final List<List<RailCard>> expectedDrawnCards;

    public MockDrawCardsRefereePlayer(List<List<RailCard>> expectedDrawnCards) {
        this.numCardsDrawn = 0;
        this.expectedDrawnCards = expectedDrawnCards;
    }

    @Override
    public void setup(ITrainMap map, int numRails, List<RailCard> cards) {

    }

    @Override
    public Set<Destination> chooseDestinations(Set<Destination> options) {
        return new HashSet<>(new ArrayList<>(options).subList(0, 2));
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState playerGameState) {
        return TurnAction.createDrawCards();
    }

    @Override
    public void receiveCards(List<RailCard> drawnCards) {
        assertEquals(this.expectedDrawnCards.get(this.numCardsDrawn), drawnCards);
        this.numCardsDrawn++;
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
