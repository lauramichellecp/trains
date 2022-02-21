package strategy;

import game_state.IPlayerGameState;
import game_state.RailCard;

import java.util.*;
import java.util.stream.Collectors;

import map.*;
import strategy.AStrategy;
import strategy.Hold10;
import utils.ComparatorUtils;
import utils.UnorderedPair;

/**
 * A strategy that cheats.
 */
public class Cheat extends BuyNow {

    @Override
    protected IRailConnection getPreferredConnection(
            Set<IRailConnection> affordableConnections,
            IPlayerGameState currentPlayerGameState,
            Set<Destination> chosenDestinations) {
        IRailConnection aConnection = affordableConnections.iterator().next();
        int impossibleLength = (aConnection.getLength() - 3 + 1) % 3 + 3;
        return new RailConnection(aConnection.getCities(), impossibleLength, aConnection.getColor());
    }
}
