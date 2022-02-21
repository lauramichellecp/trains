package json;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import manager.TournamentResult;
import map.ITrainMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import player.IPlayer;
import player.Player;
import strategy.BuyNow;
import strategy.Cheat;
import strategy.Hold10;

import java.util.*;
import java.util.stream.Collectors;

public class TestRankOutcomesJson {
    ITrainMap map;

    IPlayer player1 = new Player(new Hold10(), map);
    IPlayer player2 = new Player(new BuyNow(), map);
    IPlayer player3 = new Player(new Hold10(), map);

    Set<IPlayer> players;
    Map<IPlayer, String> playerNameLookup;

    TournamentResult report1;
    TournamentResult report2;

    public TestRankOutcomesJson() {
        players = new HashSet<>();
        playerNameLookup = new HashMap<>();

        players.add(player1);
        players.add(player2);
        players.add(player3);

        playerNameLookup.put(player1, "first");
        playerNameLookup.put(player2, "second");
        playerNameLookup.put(player3, "third");

        Set<IPlayer> removed = new HashSet<>();
        Set<IPlayer> winners = new HashSet<>();
        winners.add(player1);
        removed.add(player3);

        report1 = new TournamentResult(players, new HashSet<>());
        report2 = new TournamentResult(winners, removed);
    }


    @Test
    public void testTournamentOutcomeToJson() {
        JsonArray expectedResult = new JsonArray();
        JsonArray expectedResult2 = new JsonArray();

        JsonArray winners1 = new JsonArray();
        JsonArray winners2 = new JsonArray();
        JsonArray cheaters = new JsonArray();

        List<String> sortedWinners = players.stream().map(playerNameLookup::get).sorted().collect(Collectors.toList());
        sortedWinners.forEach(winners1::add);

        winners2.add(playerNameLookup.get(player1));
        cheaters.add(playerNameLookup.get(player3));

        expectedResult.add(winners1);
        expectedResult.add(new JsonArray());

        expectedResult2.add(winners2);
        expectedResult2.add(cheaters);

        Assertions.assertEquals(expectedResult, OutcomesJson.tournamentOutcomeToJson(report1, playerNameLookup));
        Assertions.assertEquals(expectedResult2, OutcomesJson.tournamentOutcomeToJson(report2, playerNameLookup));
    }

    @Test
    public void testGameEndOutcomeToJson() {
        JsonArray expectedOutcome = new JsonArray();
        JsonArray removed = new JsonArray();

        List<String> sortedCheaters = players.stream().map(playerNameLookup::get).sorted().collect(Collectors.toList());
        sortedCheaters.forEach(removed::add);

        expectedOutcome.add(new JsonArray());
        expectedOutcome.add(removed);

        Assertions.assertEquals(expectedOutcome, OutcomesJson.gameEndOutcomeToJson(new ArrayList<>(), players, playerNameLookup));
    }

    @Test
    public void testPlayersToRankJson() {
        // JsonArray playersToRankJson(Set<IPlayer> players, Map<IPlayer, String> playerNameLookup)
        JsonArray expectedRank = new JsonArray();

        Set<IPlayer> players = new HashSet<>();
        Map<IPlayer, String> playerNameLookup = new HashMap<>();

        IPlayer one = new Player(new Hold10(), map);
        IPlayer two = new Player(new BuyNow(), map);
        IPlayer three = new Player(new BuyNow(), map);
        IPlayer four = new Player(new Hold10(), map);

        players.add(one);
        players.add(two);
        players.add(three);
        players.add(four);

        playerNameLookup.put(one, "one");
        playerNameLookup.put(two, "two");
        playerNameLookup.put(three, "three");
        playerNameLookup.put(four, "four");

        // added alphabetically
        expectedRank.add(new JsonPrimitive("four"));
        expectedRank.add(new JsonPrimitive("one"));
        expectedRank.add(new JsonPrimitive("three"));
        expectedRank.add(new JsonPrimitive("two"));

        Assertions.assertEquals(expectedRank, RankJson.playersToRankJson(players, playerNameLookup));
    }
}
