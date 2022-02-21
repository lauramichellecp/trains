package manager;

import game_state.RailCard;
import map.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.*;
//import player.MockTournamentPlayer;
import strategy.BuyNow;
import strategy.Cheat;
import strategy.Hold10;
import utils.UnorderedPair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A Set of unit tests for an ITournamentManager class.
 */
public class TestTournament {
    KnockOutTournamentManager manager;

    // map stuff
    ICity cityA;
    ICity cityB;
    ICity cityC;
    ICity cityD;
    ICity cityE;
    ICity cityF;
    ICity cityG;
    ICity cityH;
    ICity cityI;

    ITrainMap map;
    ITrainMap biggerMap;


    // player stuff
    List<IPlayer> initialPlayers = new ArrayList<>();

    @BeforeEach
    public void SetupTournamentState() {
        Set<ICity> cities = new HashSet<>();
        cityA = new City("A", 0.5, 0.5);
        cityB = new City("B", 0.2, 0.5);
        cityC = new City("C", 0.5, 0.8);
        cityD = new City("D", 0.2, 0.7);
        cityE = new City("E", 0.3, 0.7);
        cityF = new City("F", 0.2, 0.31);
        cityG = new City("G", 0.3, 0.72);
        cityH = new City("H", 0.1, 0.74);
        cityI = new City("I", 0.3, 0.87);

        cities.add(cityA);
        cities.add(cityB);
        cities.add(cityC);
        cities.add(cityD);
        cities.add(cityE);
        cities.add(cityF);
        cities.add(cityG);
        cities.add(cityH);
        cities.add(cityI);


        Set<IRailConnection> rails = new HashSet<>();
        rails.add(new RailConnection(new UnorderedPair<>(cityA, cityB), 4, RailColor.BLUE));
        rails.add(new RailConnection(new UnorderedPair<>(cityA, cityB), 3, RailColor.RED));
        rails.add(new RailConnection(new UnorderedPair<>(cityA, cityB), 5, RailColor.WHITE));
        rails.add(new RailConnection(new UnorderedPair<>(cityB, cityC), 3, RailColor.GREEN));
        rails.add(new RailConnection(new UnorderedPair<>(cityB, cityC), 4, RailColor.BLUE));
        rails.add(new RailConnection(new UnorderedPair<>(cityA, cityD), 4, RailColor.WHITE));
        rails.add(new RailConnection(new UnorderedPair<>(cityB, cityD), 3, RailColor.RED));
        rails.add(new RailConnection(new UnorderedPair<>(cityB, cityD), 5, RailColor.BLUE));
        rails.add(new RailConnection(new UnorderedPair<>(cityD, cityC), 5, RailColor.BLUE));
        rails.add(new RailConnection(new UnorderedPair<>(cityD, cityE), 4, RailColor.RED));
        Set<IRailConnection> moreRails = new HashSet<>(rails);
        rails.add(new RailConnection(new UnorderedPair<>(cityF, cityE), 4, RailColor.WHITE));
        rails.add(new RailConnection(new UnorderedPair<>(cityF, cityE), 3, RailColor.RED));
        rails.add(new RailConnection(new UnorderedPair<>(cityG, cityE), 3, RailColor.BLUE));
        rails.add(new RailConnection(new UnorderedPair<>(cityG, cityE), 4, RailColor.GREEN));
        rails.add(new RailConnection(new UnorderedPair<>(cityG, cityH), 5, RailColor.RED));
        rails.add(new RailConnection(new UnorderedPair<>(cityG, cityI), 5, RailColor.BLUE));
        rails.add(new RailConnection(new UnorderedPair<>(cityG, cityE), 4, RailColor.WHITE));
        rails.add(new RailConnection(new UnorderedPair<>(cityH, cityI), 3, RailColor.RED));
        rails.add(new RailConnection(new UnorderedPair<>(cityH, cityI), 4, RailColor.GREEN));
        rails.add(new RailConnection(new UnorderedPair<>(cityA, cityI), 5, RailColor.GREEN));
        rails.add(new RailConnection(new UnorderedPair<>(cityD, cityI), 4, RailColor.BLUE)); // 21 (min for 9 players)


        this.map = new TrainMap(cities, rails);
        this.biggerMap = new TrainMap(cities, moreRails);


        initialPlayers = new ArrayList<>();

        this.initialPlayers.add(new Player(new Hold10(), map));
        this.initialPlayers.add(new Player(new BuyNow(), map));

        this.manager = new KnockOutTournamentManager.ManagerBuilder(initialPlayers).
                deckProvider(TestTournament::TenCardDeckSupplier).destinationProvider(TestTournament::destinationProvider).build();
    }

    private static List<Destination> destinationProvider(ITrainMap map) {
        return map.getAllPossibleDestinations().stream()
                .map((pair) -> new Destination(pair)).sorted()
                .collect(Collectors.toList());
    }

    public static List<RailCard> TenCardDeckSupplier() {
        List<RailCard> result = new ArrayList<>();
        result.add(RailCard.BLUE);
        result.add(RailCard.BLUE);
        result.add(RailCard.BLUE);
        result.add(RailCard.BLUE);
        result.add(RailCard.WHITE);
        result.add(RailCard.BLUE);
        result.add(RailCard.GREEN);
        result.add(RailCard.GREEN);
        result.add(RailCard.RED);
        result.add(RailCard.RED);

        return result;
    }

    @Test
    public void testSimpleTournament() {
        // This is a normal tournament between 2 non-cheating players and 1 game
        TournamentResult actualResult = manager.runTournament();
        TournamentResult expectedResult = new TournamentResult(
                new HashSet<>(initialPlayers), new HashSet<>());
        Assertions.assertEquals(expectedResult.second, actualResult.second);
        Assertions.assertEquals(expectedResult.first, actualResult.first);
    }

    @Test
    public void testTournamentEndsWithWinnersAndCheaters() {
        // This should be a normal tournament of 3 players (including a cheater) and 1 game
        List<IPlayer> players = new ArrayList<>(initialPlayers);
        players.add(new Player(new Cheat(), map));

        TournamentResult actualResult = new KnockOutTournamentManager.ManagerBuilder(players).build().runTournament();

        List<IPlayer> cheaters = players.subList(players.size() - 1, players.size()); // get the cheating player (the last player added)

        TournamentResult result = new TournamentResult(new HashSet<>(initialPlayers), new HashSet<>(cheaters));

        Assertions.assertEquals(result.second, actualResult.second);
        Assertions.assertTrue(result.first.containsAll(actualResult.first)); // Check actual winners appear in the
                                                                             // initial (non cheating) players list
    }

    @Test
    public void testTournamentBiggerTournament() {
        // This should be a normal tournament of 9 players
        this.initialPlayers.add(new Player(new Hold10(), biggerMap));
        this.initialPlayers.add(new Player(new BuyNow(), biggerMap));
        this.initialPlayers.add(new Player(new BuyNow(), biggerMap));
        this.initialPlayers.add(new Player(new Hold10(), biggerMap));
        this.initialPlayers.add(new Player(new Hold10(), biggerMap));
        this.initialPlayers.add(new Player(new BuyNow(), biggerMap));
        this.initialPlayers.add(new Player(new Hold10(), biggerMap));

        TournamentResult result = new TournamentResult(new HashSet<>(initialPlayers), new HashSet<>());
        TournamentResult actualResult = manager.runTournament();

        Assertions.assertTrue(result.first.containsAll(actualResult.first)); // Check actual winners appear
                                                                             // in original players
        Assertions.assertTrue(result.first.size() > actualResult.first.size());
        Assertions.assertEquals(result.second, actualResult.second);
    }

    @Test
    public void testTournamentEndsWithNoWinners() {
        List<IPlayer> cheaters = new ArrayList<>();
        cheaters.add(new Player(new Cheat(), map));
        cheaters.add(new Player(new Cheat(), map));
        cheaters.add(new Player(new Cheat(), map));

        TournamentResult result = new TournamentResult(new HashSet<>(), new HashSet<>(cheaters));
        Assertions.assertEquals(result, new KnockOutTournamentManager.ManagerBuilder(cheaters).build().runTournament());
    }

    // TODO: more testing: timeouts, bigger tournaments, etc.
}
