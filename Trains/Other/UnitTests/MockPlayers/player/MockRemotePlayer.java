package player;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import game_state.IPlayerGameState;
import game_state.RailCard;
import map.Destination;
import map.ITrainMap;
import strategy.TurnAction;

public class MockRemotePlayer implements IPlayer {
    public int numberOperations;
    public String lastCalledFunc;
    public ITrainMap map;
    public int rails;
    public List<RailCard> cards;
    public Set<Destination> destinations;
    public IPlayerGameState gamestate;
    public boolean flag;

    public ITrainMap responseMap;
    public TurnAction responseAction = TurnAction.createDrawCards();

    public MockRemotePlayer(ITrainMap map) {
        this.responseMap = map;
        numberOperations = 0;
    }

    @Override
    public ITrainMap tournamentStart() {
        numberOperations++;
        this.lastCalledFunc = "tournamentStart";
        return responseMap;
    }

    @Override
    public void tournamentResult(boolean winner) {
        numberOperations++;
        this.lastCalledFunc = "tournamentResult";
        this.flag = winner;
    }

    @Override
    public void setup(ITrainMap map, int numRails, List<RailCard> cards) {
        numberOperations++;
        this.lastCalledFunc = "setup";
        this.map = map;
        this.rails = numRails;
        this.cards = cards;
    }

    @Override
    public Set<Destination> chooseDestinations(Set<Destination> options) {
        numberOperations++;
        this.lastCalledFunc = "chooseDestinations";
        this.destinations = options;
        Iterator<Destination> optionsIterator = options.iterator();
        return Set.of(optionsIterator.next(), optionsIterator.next(), optionsIterator.next());
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState playerGameState) {
        numberOperations++;
        this.lastCalledFunc = "takeTurn";
        this.gamestate = playerGameState;
        return this.responseAction;
    }

    @Override
    public void receiveCards(List<RailCard> drawnCards) {
        numberOperations++;
        this.lastCalledFunc = "receiveCards";
        this.cards = drawnCards;
    }

    @Override
    public void winNotification(boolean thisPlayerWon) {
        numberOperations++;
        this.lastCalledFunc = "winNotification";
        this.flag = thisPlayerWon;
    }
}