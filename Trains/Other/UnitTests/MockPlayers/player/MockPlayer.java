package player;

import com.google.gson.JsonStreamParser;
import game_state.IPlayerGameState;
import game_state.RailCard;
import json.MapJson;
import map.Destination;
import map.ITrainMap;
import strategy.TurnAction;

import java.io.FileReader;
import java.util.List;
import java.util.Set;

public class MockPlayer implements IPlayer {
    IPlayer player;

    public MockPlayer(IPlayer player) {
        this.player = player;
    }

    @Override
    public void setup(ITrainMap map, int numRails, List<RailCard> cards) {
    }

    @Override
    public Set<Destination> chooseDestinations(Set<Destination> options) {
        return player.chooseDestinations(options);
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState playerGameState) {
        return player.takeTurn(playerGameState);
    }

    @Override
    public void receiveCards(List<RailCard> drawnCards) {
    }

    @Override
    public void winNotification(boolean thisPlayerWon) {
    }

    @Override
    public ITrainMap tournamentStart() {
        return readAndParseTestMap("map-enough-destinations.json");
    }

    public static ITrainMap readAndParseTestMap(String jsonFileName) {
        try {
            return MapJson.mapFromJson(
                    new JsonStreamParser(
                            new FileReader("Other/UnitTests/MapRenderedJsonInput/" + jsonFileName))
                            .next());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void tournamentResult(boolean winner) {
    }
}
