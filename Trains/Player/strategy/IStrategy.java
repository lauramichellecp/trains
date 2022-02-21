package strategy;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.Map;
import java.util.Set;
import map.Destination;
import map.ITrainMap;

/**
 * Provides decisions about what actions a player would take in choosing destinations or taking a
 * turn.
 */
public interface IStrategy {

    /**
     * Chooses the specified amount of destinations to keep.
     *
     * @param destinationOptions the set of possible destinations to choose from.
     * @param numToChoose the number of destinations to select.
     * @param map the map for the game.
     * @param numStartingRails the number of starting rails.
     * @param startingHand the starting hand of the player.
     * @return a set of Destination that is a subset of destinationOptions of size numToChoose.
     */
    Set<Destination> chooseDestinations(Set<Destination> destinationOptions,
        int numToChoose, ITrainMap map, int numStartingRails,
        Map<RailCard, Integer> startingHand);

    /**
     * Chooses the action to take on a turn given the state of the game.
     *
     * @param currentPlayerGameState the state of the game on which the turn is taken.
     * @param map the game map for this game of Trains.
     * @param chosenDestinations the set of destinations chosen at the beginning of the game.
     * @return a strategy.TurnAction specifying the selected move to take on the turn.
     */
    TurnAction takeTurn(IPlayerGameState currentPlayerGameState, ITrainMap map,
        Set<Destination> chosenDestinations);
}
