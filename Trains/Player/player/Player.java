package player;

import map.ITrainMap;
import strategy.IStrategy;

/**
 * A player that uses a strategy and a map to play in a tournament of Trains.
 */
public class Player extends RefereePlayer implements IPlayer {
    ITrainMap map;


    public Player(IStrategy strategy, ITrainMap map) {
        super(strategy);
        this.map = map;
    }

    public Player(String strategyFilePath, ITrainMap map) {
        super(strategyFilePath);
        this.map = map;
    }

    @Override
    public ITrainMap tournamentStart() {
        return this.map;
    }

    @Override
    public void tournamentResult(boolean winner) {

    }
}
