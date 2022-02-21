package manager;

import map.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import player.IPlayer;
import player.MockPlayer;
import utils.UnorderedPair;

import java.util.*;

/**
 * A Set of unit tests for the manager.TournamentState class.
 */
public class TestTournamentState {
    TournamentState tournamentState;

    // map stuff
    ICity cityA;
    ICity cityB;
    ITrainMap map;

    // player stuff
    List<IPlayer> initialPlayers = new ArrayList<>();

    @BeforeEach
    public void SetupTournamentState() {
        Set<ICity> cities = new HashSet<>();
        cityA = new City("A", 0.5, 0.5);
        cityB = new City("B", 0.2, 0.7);
        cities.add(cityA);
        cities.add(cityB);

        Set<IRailConnection> rails = new HashSet<>();
        rails.add(new RailConnection(new UnorderedPair<>(cityA, cityB), 4, RailColor.BLUE));

        this.map = new TrainMap(cities, rails);

        initialPlayers = new ArrayList<>();
        this.initialPlayers.add(new MockPlayer(null));
        this.initialPlayers.add(new MockPlayer(null));
        this.initialPlayers.add(new MockPlayer(null));

        this.tournamentState = new TournamentState(map, initialPlayers, new HashSet<>());
    }

    @Test
    public void testUpdateOnRoundEndUselessRounds() {
        Assertions.assertEquals(0, tournamentState.numUselessRounds);
        Set<IPlayer> cheaters = new HashSet<>();
        cheaters.add(initialPlayers.get(2));
        initialPlayers.remove(initialPlayers.get(2));

        tournamentState.updateOnRoundEnd(initialPlayers, cheaters, false);
        Assertions.assertEquals(0, tournamentState.numUselessRounds);

        tournamentState.updateOnRoundEnd(initialPlayers.subList(0, 2), new HashSet<>(), true);
        Assertions.assertEquals(1, tournamentState.numUselessRounds);
    }

    @Test
    public void testUpdateOnRoundEndStillActive() {
        List<IPlayer> expectedActive = new ArrayList<>(initialPlayers);
        Assertions.assertEquals(expectedActive, tournamentState.stillAlive);

        Set<IPlayer> cheaters = new HashSet<>();

        cheaters.add(initialPlayers.get(2));
        expectedActive.remove(initialPlayers.get(2));

        tournamentState.updateOnRoundEnd(expectedActive, cheaters, false);
        Assertions.assertEquals(expectedActive, tournamentState.stillAlive);

        // can't update on round end without removing cheaters from your active players
        cheaters.add(initialPlayers.get(1));

        //Assertions.assertThrows(RuntimeException.class, () -> tournamentState.updateOnRoundEnd(expectedActive, cheaters, false));

        // removing cheaters from active players
        expectedActive.remove(initialPlayers.get(1));

        tournamentState.updateOnRoundEnd(expectedActive, cheaters, false);
        Assertions.assertEquals(expectedActive, tournamentState.stillAlive);
    }

    @Test
    public void testTournamentStillActiveNotEnoughPlayers() {
        Assertions.assertTrue(tournamentState.tournamentStillActive(2, 2));
        tournamentState.stillAlive.remove(tournamentState.stillAlive.get(0));
        tournamentState.stillAlive.remove(tournamentState.stillAlive.get(0));
        Assertions.assertFalse(tournamentState.tournamentStillActive(2, 2));
    }

    @Test
    public void testTournamentStillActive() {
        // for 3 total active players in the tournament
        Assertions.assertTrue(tournamentState.tournamentStillActive(2, 2));
        tournamentState.stillAlive.remove(tournamentState.stillAlive.get(0));
        tournamentState.stillAlive.remove(tournamentState.stillAlive.get(0));
        Assertions.assertFalse(tournamentState.tournamentStillActive(2, 2));
    }

    @Test
    public void testTournamentEndTwoUselessRounds() {
        // Tests the end condition where two tournament rounds of games in a row produce the exact same winners
        Assertions.assertEquals(0, tournamentState.numUselessRounds);
        tournamentState.updateOnRoundEnd(initialPlayers, new HashSet<>(), true);
        Assertions.assertTrue(tournamentState.tournamentStillActive(2, 2));
        Assertions.assertEquals(1, tournamentState.numUselessRounds);
        tournamentState.updateOnRoundEnd(initialPlayers, new HashSet<>(), true);
        Assertions.assertTrue(tournamentState.tournamentStillActive(2, 2));
        Assertions.assertEquals(2, tournamentState.numUselessRounds);
        tournamentState.updateOnRoundEnd(initialPlayers, new HashSet<>(), true);
        // tournament becomes inactive after the number of useless rounds has surpassed the max.
        Assertions.assertFalse(tournamentState.tournamentStillActive(2, 2));
    }

    @Test
    public void testTournamentEndTooFewPlayers() {
        // for 3 total active players in the tournament
        Assertions.assertFalse(tournamentState.tournamentStillActive(2, 4));
    }

    @Test
    public void testTournamentEndEnoughForOneGame() {
        // Tests the end condition when the number of participants has become small enough for single game
        Assertions.assertFalse(tournamentState.oneMoreGame(1, 2));
        Assertions.assertFalse(tournamentState.oneMoreGame(4, 8));
        Assertions.assertTrue(tournamentState.oneMoreGame(3, 3));
    }
}