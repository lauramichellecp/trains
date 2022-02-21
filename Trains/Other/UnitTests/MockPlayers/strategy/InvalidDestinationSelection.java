package strategy;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.Map;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import strategy.IStrategy;
import strategy.TurnAction;

public class InvalidDestinationSelection implements IStrategy {

    @Override
    public Set<Destination> chooseDestinations(Set<Destination> destinationOptions, int numToChoose,
        ITrainMap map, int numStartingRails, Map<RailCard, Integer> startingHand) {
        return destinationOptions;
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState currentPlayerGameState, ITrainMap map,
        Set<Destination> chosenDestinations) {
        return TurnAction
            .createAcquireConnection(map.getRailConnections().stream().findFirst().get());
    }
}
