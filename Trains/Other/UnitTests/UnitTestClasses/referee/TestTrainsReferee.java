package referee;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import game_state.RailCard;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import map.City;
import map.Destination;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.RailColor;
import map.RailConnection;
import map.TrainMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.*;
import strategy.InvalidAcquire;
import strategy.InvalidDestinationSelection;
import strategy.ThrowException;
import referee.GameEndReport.PlayerScore;
import referee.TrainsReferee.RefereeBuilder;
import strategy.BuyNow;
import strategy.Hold10;
import utils.UnorderedPair;

public class TestTrainsReferee {

    ITrainMap simpleMap;
    ITrainMap largeBlueMap;

    @BeforeEach
    public void setupSimpleMap() {

        ICity boston = new City("Boston", 0, 0);
        ICity nyc = new City("NYC", 0, 0);
        ICity chicago = new City("chicago", 0, 0);
        ICity washington = new City("washington", 0, 0);
        ICity texas = new City("texas", 0, 0);
        ICity lincoln = new City("lincoln", 0, 0);
        Set<ICity> cities = new HashSet<>();
        cities.add(boston);
        cities.add(nyc);
        cities.add(chicago);
        cities.add(washington);
        cities.add(texas);
        cities.add(lincoln);

        IRailConnection connection1 = new RailConnection(new UnorderedPair<>(boston, nyc), 3,
            RailColor.BLUE);
        IRailConnection connection2 = new RailConnection(new UnorderedPair<>(chicago, nyc), 3,
            RailColor.BLUE);
        IRailConnection connection3 = new RailConnection(new UnorderedPair<>(washington, nyc), 3,
            RailColor.BLUE);
        IRailConnection connection4 = new RailConnection(new UnorderedPair<>(texas, nyc), 3,
            RailColor.BLUE);
        IRailConnection connection5 = new RailConnection(new UnorderedPair<>(lincoln, nyc), 3,
            RailColor.BLUE);
        IRailConnection connection6 = new RailConnection(new UnorderedPair<>(boston, lincoln), 3,
            RailColor.BLUE);
        IRailConnection connection7 = new RailConnection(new UnorderedPair<>(lincoln, texas), 3,
            RailColor.BLUE);
        Set<IRailConnection> rails = new HashSet<>();
        rails.add(connection1);
        rails.add(connection2);
        rails.add(connection3);
        rails.add(connection4);
        rails.add(connection5);
        rails.add(connection6);
        rails.add(connection7);

        this.simpleMap = new TrainMap(cities, rails);
    }

    @BeforeEach
    public void setupLargeBlueMap() {

        Set<ICity> cities = new HashSet<>();
        for (int ii = 0; ii < 100; ii++) {
            cities.add(new City(String.valueOf(ii), 0, 0));
        }

        Set<IRailConnection> rails = new HashSet<>();
        List<ICity> cityList = new ArrayList<>(cities);
        for (int ii = 0; ii < 98; ii++) {
            for (int jj = ii + 1; jj < 99; jj++) {
                rails.add(
                    new RailConnection(new UnorderedPair<>(cityList.get(ii), cityList.get(jj)), 3,
                        RailColor.BLUE));
            }
        }

        this.largeBlueMap = new TrainMap(cities, rails);
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

    public static List<RailCard> ThousandBlueCardDeckSupplier() {
        List<RailCard> result = new ArrayList<>();
        for (int ii = 0; ii < 1000; ii++) {
            result.add(RailCard.BLUE);
        }

        return result;
    }

    @Test
    public void testConstruction() {
        // valid construction
        List<IPlayer> twoDummyPlayers = new ArrayList<>();
        twoDummyPlayers.add(new RefereePlayer(new ThrowException()));
        twoDummyPlayers.add(new RefereePlayer(new ThrowException()));

        new TrainsReferee.RefereeBuilder(this.simpleMap, twoDummyPlayers).build();
        new TrainsReferee.RefereeBuilder(this.largeBlueMap, twoDummyPlayers)
            .deckProvider(TestTrainsReferee::ThousandBlueCardDeckSupplier).build();
        new TrainsReferee.RefereeBuilder(this.simpleMap, twoDummyPlayers)
            .destinationProvider(TestTrainsReferee::destinationProvider).build();
        new TrainsReferee.RefereeBuilder(this.largeBlueMap, twoDummyPlayers)
            .destinationProvider(TestTrainsReferee::destinationProvider)
            .deckProvider(TestTrainsReferee::ThousandBlueCardDeckSupplier).build();

        try {
            new TrainsReferee.RefereeBuilder(null, twoDummyPlayers).build();
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            new TrainsReferee.RefereeBuilder(this.simpleMap, null).build();
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            new TrainsReferee.RefereeBuilder(this.simpleMap, twoDummyPlayers).deckProvider(null)
                .build();
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            new TrainsReferee.RefereeBuilder(this.simpleMap, twoDummyPlayers).destinationProvider(null)
                .build();
            fail();
        } catch (NullPointerException ignored) {
        }
        try {
            twoDummyPlayers.remove(0);
            new TrainsReferee.RefereeBuilder(this.simpleMap, twoDummyPlayers).destinationProvider(null)
                    .build();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            List<IPlayer> moreThan8Players = new ArrayList<>();
            for(int i = 0; i < 9; i++) {
                moreThan8Players.add(new RefereePlayer(new ThrowException()));
            }
            new TrainsReferee.RefereeBuilder(this.simpleMap, moreThan8Players).destinationProvider(null)
                    .build();
            fail();
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void TestAllDrawCards() {
        // Two BuyNow players with the given game setup will run out of cards before using all of
        // their rails
        List<IPlayer> playersInTurnOrder = new ArrayList<>();
        playersInTurnOrder.add(new RefereePlayer(new BuyNow()));
        playersInTurnOrder.add(new RefereePlayer(new BuyNow()));

        IReferee referee = new RefereeBuilder(this.simpleMap, playersInTurnOrder)
            .destinationProvider(TestTrainsReferee::destinationProvider)
            .deckProvider(TestTrainsReferee::TenCardDeckSupplier).build();

        // Construct expected game report
        List<PlayerScore> expectedPlayerScores = new ArrayList<>();
        expectedPlayerScores.add(new PlayerScore(playersInTurnOrder.get(0), 3));
        expectedPlayerScores.add(new PlayerScore(playersInTurnOrder.get(1), -20));
        GameEndReport expectedGameReport = new GameEndReport(expectedPlayerScores, new HashSet<>());

        GameEndReport actualGameReport = referee.playGame();

        assertGameReportEquals(expectedGameReport, actualGameReport);
    }

    @Test
    public void TestTimeOut() {
        // Two BuyNow players with the given game setup will run out of cards before using all of
        // their rails
        List<IPlayer> playersInTurnOrder = new ArrayList<>();
        playersInTurnOrder.add(new TimeOutRefereePlayer());
        playersInTurnOrder.add(new RefereePlayer(new Hold10()));

        IReferee referee = new RefereeBuilder(this.simpleMap, playersInTurnOrder)
            .destinationProvider(TestTrainsReferee::destinationProvider)
            .deckProvider(TestTrainsReferee::TenCardDeckSupplier).build();

        assertTrue(referee.playGame().removedPlayers.contains(playersInTurnOrder.get(0)));
    }

    @Test
    public void testRunOutOfRails() {
        // Two BuyNow players with the given game setup will run out of cards before using all of
        // their rails
        List<IPlayer> playersInTurnOrder = new ArrayList<>();
        playersInTurnOrder.add(new RefereePlayer(new BuyNow()));
        playersInTurnOrder.add(new RefereePlayer(new Hold10()));

        IReferee referee = new RefereeBuilder(this.largeBlueMap, playersInTurnOrder)
            .destinationProvider(TestTrainsReferee::destinationProvider)
            .deckProvider(TestTrainsReferee::ThousandBlueCardDeckSupplier).build();

        // Construct expected game report
        List<PlayerScore> expectedPlayerScores = new ArrayList<>();
        expectedPlayerScores.add(new PlayerScore(playersInTurnOrder.get(0), 65));
        expectedPlayerScores.add(new PlayerScore(playersInTurnOrder.get(1), 39));

        GameEndReport expectedGameReport = new GameEndReport(expectedPlayerScores, new HashSet<>());

        GameEndReport actualGameReport = referee.playGame();

        assertGameReportEquals(expectedGameReport, actualGameReport);
    }

    @Test
    public void testOneInvalidPlayer() {
        // The InvalidAcquire player will misbehave on their first turn, and be kicked out
        List<IPlayer> playersInTurnOrder = new ArrayList<>();
        playersInTurnOrder.add(new RefereePlayer(new InvalidAcquire()));
        playersInTurnOrder.add(new RefereePlayer(new Hold10()));

        // On the large blue map the Hold10 player will end the game by running out of rails
        IReferee referee = new RefereeBuilder(this.largeBlueMap, playersInTurnOrder)
            .destinationProvider(TestTrainsReferee::destinationProvider)
            .deckProvider(TestTrainsReferee::ThousandBlueCardDeckSupplier).build();

        // Construct expected game report
        List<PlayerScore> expectedPlayerScores = new ArrayList<>();
        expectedPlayerScores.add(new PlayerScore(playersInTurnOrder.get(1), 85));
        Set<IPlayer> expectedKickedOutPlayers = new HashSet<>();
        expectedKickedOutPlayers.add(playersInTurnOrder.get(0));
        GameEndReport expectedGameReport = new GameEndReport(expectedPlayerScores,
            expectedKickedOutPlayers);

        GameEndReport actualGameReport = referee.playGame();

        assertGameReportEquals(expectedGameReport, actualGameReport);
    }

    @Test
    public void testOneInvalidDestSelectionPlayer() {
        // The InvalidDestinationSelection player will misbehave during destination selection
        List<IPlayer> playersInTurnOrder = new ArrayList<>();
        playersInTurnOrder.add(new RefereePlayer(new InvalidDestinationSelection()));
        playersInTurnOrder.add(new RefereePlayer(new Hold10()));

        // On the large blue map the Hold10 player will end the game by running out of rails
        IReferee referee = new RefereeBuilder(this.largeBlueMap, playersInTurnOrder)
            .destinationProvider(TestTrainsReferee::destinationProvider)
            .deckProvider(TestTrainsReferee::ThousandBlueCardDeckSupplier).build();

        // Construct expected game report
        List<PlayerScore> expectedPlayerScores = new ArrayList<>();
        expectedPlayerScores.add(new PlayerScore(playersInTurnOrder.get(1), 85));
        Set<IPlayer> expectedKickedOutPlayers = new HashSet<>();
        expectedKickedOutPlayers.add(playersInTurnOrder.get(0));
        GameEndReport expectedGameReport = new GameEndReport(expectedPlayerScores,
            expectedKickedOutPlayers);

        GameEndReport actualGameReport = referee.playGame();

        assertGameReportEquals(expectedGameReport, actualGameReport);
    }

    @Test
    public void testOneInvalidThrowsExceptionPlayer() {
        // The InvalidDestinationSelection player will misbehave during destination selection
        List<IPlayer> playersInTurnOrder = new ArrayList<>();
        playersInTurnOrder.add(new RefereePlayer(new ThrowException()));
        playersInTurnOrder.add(new RefereePlayer(new Hold10()));

        // On the large blue map the Hold10 player will end the game by running out of rails
        IReferee referee = new RefereeBuilder(this.largeBlueMap, playersInTurnOrder)
            .destinationProvider(TestTrainsReferee::destinationProvider)
            .deckProvider(TestTrainsReferee::ThousandBlueCardDeckSupplier).build();

        // Construct expected game report
        List<PlayerScore> expectedPlayerScores = new ArrayList<>();
        expectedPlayerScores.add(new PlayerScore(playersInTurnOrder.get(1), 85));
        Set<IPlayer> expectedKickedOutPlayers = new HashSet<>();
        expectedKickedOutPlayers.add(playersInTurnOrder.get(0));
        GameEndReport expectedGameReport = new GameEndReport(expectedPlayerScores,
            expectedKickedOutPlayers);

        GameEndReport actualGameReport = referee.playGame();

        assertGameReportEquals(expectedGameReport, actualGameReport);
    }

    @Test
    public void testTwoInvalidPlayers() {
        // Both InvalidAcquire players will misbehave on their first turn, and be kicked out
        List<IPlayer> playersInTurnOrder = new ArrayList<>();
        playersInTurnOrder.add(new RefereePlayer(new InvalidAcquire()));
        playersInTurnOrder.add(new RefereePlayer(new InvalidAcquire()));

        IReferee referee = new RefereeBuilder(this.simpleMap, playersInTurnOrder)
            .destinationProvider(TestTrainsReferee::destinationProvider)
            .deckProvider(TestTrainsReferee::TenCardDeckSupplier).build();

        // Construct expected game report
        List<PlayerScore> expectedPlayerScores = new ArrayList<>();
        Set<IPlayer> expectedKickedOutPlayers = new HashSet<>();
        expectedKickedOutPlayers.add(playersInTurnOrder.get(0));
        expectedKickedOutPlayers.add(playersInTurnOrder.get(1));
        GameEndReport expectedGameReport = new GameEndReport(expectedPlayerScores,
            expectedKickedOutPlayers);

        GameEndReport actualGameReport = referee.playGame();

        assertGameReportEquals(expectedGameReport, actualGameReport);
    }

    @Test
    public void testCardsReceived() {
        List<List<RailCard>> expectedDrawnCards = new ArrayList<>();
        expectedDrawnCards.add(List.of(RailCard.WHITE, RailCard.BLUE));
        expectedDrawnCards.add(List.of(RailCard.GREEN, RailCard.GREEN));
        expectedDrawnCards.add(List.of(RailCard.RED, RailCard.RED));

        List<IPlayer> playersInTurnOrder = new ArrayList<>();
        playersInTurnOrder.add(new MockDrawCardsRefereePlayer(expectedDrawnCards));
        playersInTurnOrder.add(new RefereePlayer(new ThrowException()));

        IReferee referee = new RefereeBuilder(this.simpleMap, playersInTurnOrder)
            .destinationProvider(TestTrainsReferee::destinationProvider)
            .deckProvider(TestTrainsReferee::TenCardDeckSupplier).build();

        referee.playGame();
    }

    @Test
    public void testGameEndsWhenNoRailsRemaining() {
        // MockEndGameCorrectlyPlayer assert that they dont begin their turn when the game should
        // be over
        List<IPlayer> playersInTurnOrder = new ArrayList<>();
        playersInTurnOrder.add(new MockEndGameCorrectlyRefereePlayer());
        playersInTurnOrder.add(new MockEndGameCorrectlyRefereePlayer());

        IReferee referee = new RefereeBuilder(this.largeBlueMap, playersInTurnOrder)
            .destinationProvider(TestTrainsReferee::destinationProvider)
            .deckProvider(TestTrainsReferee::ThousandBlueCardDeckSupplier).build();

        referee.playGame();
    }

    private static void assertGameReportEquals(GameEndReport expected, GameEndReport actual) {
        for (int ii = 0; ii < expected.playerRanking.size(); ii++) {
            PlayerScore expectedPlayerScore = expected.playerRanking.get(ii);
            PlayerScore actualPlayerScore = actual.playerRanking.get(ii);

            assertEquals(expectedPlayerScore.score, actualPlayerScore.score);
            assertEquals(expectedPlayerScore.player,
                actualPlayerScore.player);
        }
        assertEquals(expected.removedPlayers,
            actual.removedPlayers);
    }

}
