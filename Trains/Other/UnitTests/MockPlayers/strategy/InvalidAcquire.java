package strategy;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import strategy.IStrategy;
import strategy.TurnAction;

public class InvalidAcquire implements IStrategy {

    @Override
    public Set<Destination> chooseDestinations(Set<Destination> destinationOptions, int numToChoose,
        ITrainMap map, int numStartingRails, Map<RailCard, Integer> startingHand) {
        return new HashSet<>(new ArrayList<>(destinationOptions).subList(0, 2));
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState currentPlayerGameState, ITrainMap map,
        Set<Destination> chosenDestinations) {
        return TurnAction
            .createAcquireConnection(map.getRailConnections().stream().findFirst().get());
    }
}
