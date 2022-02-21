package referee;

import game_state.IPlayerGameState;
import map.IRailConnection;
import map.ITrainMap;

/**
 * This class contains methods that can be used to determine if it is legal, according to the rules
 * of the game Trains, for a certain player to take a certain action.
 */
public interface IActionChecker {

    /**
     * Determines if the player that corresponds to the given IPlayerGameState is allowed to acquire
     * the given IRailConnection given the state of the game described in the IPlayerGameState.
     *
     * @param gameStateForPlayer An object that contains the information visible to one player in a
     *                           game of Trains.
     * @param desiredConnection  The Connection that the given player would like to check if they
     *                           can acquire.
     * @return Whether or not the player that corresponds to the given IPlayerGameState is allowed
     * to acquire the given IRailConnection.
     */
    boolean canAcquireConnection(IPlayerGameState gameStateForPlayer, ITrainMap map,
        IRailConnection desiredConnection);
}
