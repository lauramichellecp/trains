package strategy;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import org.junit.jupiter.api.Assertions;
import strategy.IStrategy;
import strategy.TurnAction;

public class MockStrategy implements IStrategy {

    int expectedStartingRails;
    ITrainMap expectedTrainMap;
    int expectedNumToChoose;
    Set<Destination> expectedDestinations;
    Map<RailCard, Integer> expectedStartingHand;

    public MockStrategy(
        Set<Destination> expectedDestinations,
        int expectedNumToChoose,
        ITrainMap expectedTrainMap,
        int expectedStartingRails,
        Map<RailCard, Integer> expectedStartingHand) {
        this.expectedDestinations = expectedDestinations;
        this.expectedNumToChoose = expectedNumToChoose;
        this.expectedStartingRails = expectedStartingRails;
        this.expectedTrainMap = expectedTrainMap;
        this.expectedStartingHand = expectedStartingHand;
    }

    @Override
    public Set<Destination> chooseDestinations(
        Set<Destination> destinationOptions,
        int numToChoose,
        ITrainMap map,
        int numStartingRails,
        Map<RailCard, Integer> startingHand) {
        Assertions.assertEquals(expectedNumToChoose, numToChoose);
        Assertions.assertEquals(expectedStartingRails, numStartingRails);
        assertMap(map);
        Assertions.assertEquals(expectedStartingHand, startingHand);
        return new HashSet<>();
    }

    @Override
    public TurnAction takeTurn(
        IPlayerGameState currentPlayerGameState,
        ITrainMap map,
        Set<Destination> chosenDestinations) {
        assertMap(map);
        return null;
    }

    private void assertMap(ITrainMap map) {
        if (this.expectedTrainMap == null) {
            Assertions.assertNull(map);
        } else {
            Assertions.assertEquals(expectedTrainMap.getCities(), map.getCities());
            Assertions
                .assertEquals(expectedTrainMap.getRailConnections(), map.getRailConnections());
            Assertions.assertEquals(expectedTrainMap.getMapDimension(), map.getMapDimension());
        }
    }
}
