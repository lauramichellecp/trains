package json;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import game_state.*;
import map.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import referee.IPlayerData;
import referee.PlayerData;
import referee.TrainsPlayerHand;
import utils.UnorderedPair;

import java.util.*;


public class TestPlayerStateJson {
    private final JsonObject playerStateJson;
    private final IPlayerGameState playerGameState;

    ICity boston = new City("Boston", 0, 1);
    ICity seattle = new City("Seattle", 0, 0);
    ICity texas = new City("Texas", 1, 0);

    IPlayerData data;
    IOpponentInfo opponentInfo = new OpponentInfo(new HashSet<>());


    public TestPlayerStateJson() {
        String jsonState = "{\n" +
                "  \"this\": {\n" +
                "    \"destination1\": [\"Boston\", \"Seattle\"],\n" +
                "    \"destination2\": [\"Seattle\", \"Texas\"],\n" +
                "    \"rails\": 3,\n" +
                "    \"cards\": {\n" +
                "      \"red\": 3,\n" +
                "      \"white\": 1\n" +
                "    },\n" +
                "    \"acquired\": [[\"Boston\", \"Seattle\", \"blue\", 4]]\n" +
                "  },\n" +
                "  \"acquired\": [[[\"Boston\", \"Seattle\", \"white\", 5], [\"Boston\", \"Texas\", \"green\", 5]], [], [[\"Seattle\", \"Texas\", \"blue\", 4]]]\n" +
                "}";
        Set<IRailConnection> owned = new HashSet<>();
        owned.add(new RailConnection(new UnorderedPair<>(boston, seattle), 4, RailColor.BLUE));
        owned.add(new RailConnection(new UnorderedPair<>(boston, seattle), 5, RailColor.WHITE));
        owned.add(new RailConnection(new UnorderedPair<>(boston, texas), 5, RailColor.GREEN));
        owned.add(new RailConnection(new UnorderedPair<>(seattle, texas), 4, RailColor.BLUE));

        Set<Destination> destinations = new HashSet<>();
        destinations.add(new Destination(boston, seattle));
        destinations.add(new Destination(seattle, texas));

        List<RailCard> cardsInHand = new ArrayList<>();
        cardsInHand.add(RailCard.RED);
        cardsInHand.add(RailCard.RED);
        cardsInHand.add(RailCard.RED);
        cardsInHand.add(RailCard.WHITE);

        this.playerStateJson = JsonParser.parseString(jsonState).getAsJsonObject();
        this.data = new PlayerData(new TrainsPlayerHand(cardsInHand), 3, destinations, owned);
        this.playerGameState = new PlayerGameState(data, new ArrayList<>());
    }

    @Test
    public void testPlayerStateFromJson() {
        IPlayerGameState actualState = PlayerStateJson.playerStateFromJson(playerStateJson);
        Assertions.assertEquals(playerGameState.getNumRails(), actualState.getNumRails());
        Assertions.assertEquals(playerGameState.getCardsInHand(), actualState.getCardsInHand());
        Assertions.assertEquals(playerGameState.getOpponentInfo(), actualState.getOpponentInfo());
        Assertions.assertEquals(playerGameState.getOwnedConnections(), actualState.getOwnedConnections());
    }
}
