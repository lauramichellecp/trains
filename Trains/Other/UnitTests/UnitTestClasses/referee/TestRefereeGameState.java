//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
//import game_state.IInitialPlayerData;
//import game_state.IPlayerData;
//import game_state.IRefereeGameState;
//import game_state.BasicInitialPlayerData;
//import game_state.IPlayerGameState;
//import game_state.PlayerData;
//import game_state.RailCard;
//import game_state.RefereeGameState;
//import game_state.TrainsPlayerHand;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import map.City;
//import map.ICity;
//import map.IRailConnection;
//import map.ITrainMap;
//import map.RailColor;
//import map.RailConnection;
//import map.TrainMap;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import utils.UnorderedPair;
//
//public class TestRefereeGameState {
//  IRefereeGameState simpleGameState = null;
//  ICity cityA;
//  ICity cityB;
//
//  List<String> playerID;
//  Map<String, IInitialPlayerData> playerData;
//  ITrainMap map;
//  List<RailCard> deck;
//  Map<RailCard, Integer> basicHand;
//
//  @BeforeEach
//  public void SetupSimpleGameState() {
//    Set<ICity> cities = new HashSet<>();
//    cityA = new City("A", 0.5, 0.5);
//    cityB = new City("B", 0.2, 0.7);
//    cities.add(cityA);
//    cities.add(cityB);
//
//    Set<IRailConnection> rails = new HashSet<>();
//    rails.add(new RailConnection(new UnorderedPair<>(cityA, cityB), 4, RailColor.BLUE));
//
//    this.map = new TrainMap(cities, rails);
//
//
//    List<String> pid = new ArrayList<>();
//    pid.add("player1");
//
//
//    List<RailCard> deck = new ArrayList<>();
//    deck.add(RailCard.GREEN);
//    deck.add(RailCard.WHITE);
//    deck.add(RailCard.BLUE);
//    deck.add(RailCard.GREEN);
//    deck.add(RailCard.GREEN);
//
//
//    Map<RailCard, Integer> cardsInHand = new HashMap<>();
//    cardsInHand.put(RailCard.BLUE, 1);
//    cardsInHand.put(RailCard.RED, 4);
//
//
//      List<IPlayerData> playerData = new ArrayList<>();
//      playerData.add(
//          new PlayerData("player1", new TrainsPlayerHand(cardsInHand), 6, new HashSet<>(),
//              new HashSet<>()));
//
//    simpleGameState = new RefereeGameState(playerData, deck, map);
//  }
//
//  @BeforeEach
//  public void Initialize() {
//    this.playerID = new ArrayList<>();
//    playerID.addAll(Arrays.asList("a", "b", "c"));
//
//    this.map = TestMapRenderer.readAndParseTestMap("bos-sea-tex.json");
//
//    this.basicHand = new HashMap<>();
//    basicHand.put(RailCard.BLUE, 5);
//    basicHand.put(RailCard.RED, 1);
//    this.playerData = new HashMap<>();
//
//    IInitialPlayerData aData =
//        new BasicInitialPlayerData(map.getAllPossibleDestinations(), basicHand, 5);
//    this.playerData.put("a", aData);
//    IInitialPlayerData bData =
//        new BasicInitialPlayerData(map.getAllPossibleDestinations(), basicHand, 2);
//    this.playerData.put("b", bData);
//    IInitialPlayerData cData =
//        new BasicInitialPlayerData(map.getAllPossibleDestinations(), basicHand, 0);
//    this.playerData.put("c", cData);
//
//    this.deck =
//        Arrays.asList(RailCard.BLUE, RailCard.BLUE, RailCard.GREEN, RailCard.RED, RailCard.WHITE);
//  }
//
//  // region Construction Tests
//
//  @Test
//  public void TestNullConstruction() {
//    ITrainMap map = new TrainMap(new HashSet<>(), new HashSet<>());
//    List<RailCard> deck = new ArrayList<>();
//    List<IPlayerData> playerData = new ArrayList<>();
//
//    try {
//      new RefereeGameState(null, deck, map);
//      fail();
//    } catch (NullPointerException ignored) {
//    }
//    try {
//      new RefereeGameState(playerData, null, map);
//      fail();
//    } catch (NullPointerException ignored) {
//    }
//    try {
//      new RefereeGameState(playerData, deck, null);
//      fail();
//    } catch (NullPointerException ignored) {
//    }
//  }
//
//  @Test
//  public void TestPlayerDataConstruction() {
//    // cannot have negative rails
//    this.playerData.put(
//        "a", new BasicInitialPlayerData(this.map.getAllPossibleDestinations(), this.basicHand, -1));
//    try {
//      new RefereeGameState(map, this.playerID, this.deck, this.playerData);
//      fail();
//    } catch (IllegalArgumentException ignored) {
//    }
//    this.Initialize();
//    // Player-chosen destinations must be in the map
//    ITrainMap isolated = TestMapRenderer.readAndParseTestMap("isolated-cities.json");
//    this.playerData = new HashMap<>();
//    this.playerID = new ArrayList<>();
//    playerID.add("isolated");
//    playerData.put(
//        "isolated",
//        new BasicInitialPlayerData(
//            new HashSet<>(
//                Collections.singletonList(
//                    new UnorderedPair<>(new City("Seattle", 0, 0), new City("Miami", 0, 0)))),
//            this.basicHand,
//            2));
//    try {
//      new RefereeGameState(isolated, this.playerID, this.deck, this.playerData);
//      fail();
//    } catch (IllegalArgumentException ignored) {
//    }
//    this.Initialize();
//    // Must have positive number of a given card (or else that key should not appear in the hand)
//    this.basicHand.put(RailCard.BLUE, 0);
//    this.playerData.put(
//        "a", new BasicInitialPlayerData(this.map.getAllPossibleDestinations(), this.basicHand, 1));
//    try {
//      new RefereeGameState(this.map, this.playerID, this.deck, this.playerData);
//      fail();
//    } catch (IllegalArgumentException ignored) {
//    }
//  }
//
//  // endregion
//
//  @Test
//  public void TestCreatePlayerGameState() {
//    IPlayerGameState playerGameState =
//        new RefereeGameState(this.map, this.playerID, this.deck, this.playerData)
//            .createPlayerGameState("b");
//    Assertions.assertEquals(playerGameState.getCardsInHand(), this.basicHand);
//    Assertions.assertEquals(playerGameState.getDeckSize(), 5);
//
//    Assertions.assertEquals(playerGameState.getGameMap().getAllPossibleDestinations(), this.map.getAllPossibleDestinations());
//    Assertions.assertEquals(playerGameState.getNumCardsInAllPlayersHand().get("a"), 6);
//    Assertions.assertEquals(playerGameState.getNumCardsInAllPlayersHand().get("b"), 6);
//    Assertions.assertEquals(playerGameState.getNumCardsInAllPlayersHand().get("c"), 6);
//
//    Assertions.assertEquals(playerGameState.getOccupiedConnections(), new HashMap<>());
//    Assertions.assertEquals(playerGameState.getPlayerID(), "b");
//    Assertions.assertEquals(playerGameState.getRailsInAllPlayersBank().get("a"), 5);
//    Assertions.assertEquals(playerGameState.getRailsInAllPlayersBank().get("b"), 2);
//    Assertions.assertEquals(playerGameState.getRailsInAllPlayersBank().get("c"), 0);
//
//    Assertions.assertEquals(playerGameState.getTurnOrder(), this.playerID);
//  }
//
//  @Test
//  public void TestRemoveCardsValid() {
//    assertEquals((int) simpleGameState.getCardsInHand("player1").getHand().get(RailCard.BLUE), 1);
//    simpleGameState.removeCards("player1", 1, RailCard.BLUE);
//    assertFalse(simpleGameState.getCardsInHand("player1").getHand().containsKey(RailCard.BLUE));
//  }
//
//  @Test
//  public void TestRemoveCardsNotEnoughInHand() {
//    assertEquals((int) simpleGameState.getCardsInHand("player1").getHand().get(RailCard.BLUE), 1);
//    Assertions.assertThrows(IllegalStateException.class, () -> {
//      simpleGameState.removeCards("player1", 2, RailCard.BLUE);
//    });
//  }
//
//  @Test
//  public void TestRemoveCardsNegativeArg() {
//    Assertions.assertThrows(IllegalArgumentException.class, () -> {
//      simpleGameState.removeCards("player1", -2, RailCard.BLUE);
//    });
//  }
//
//  @Test
//  public void TestRemoveCardsInvalidPlayerID() {
//    Assertions.assertThrows(IllegalArgumentException.class, () -> {
//      simpleGameState.removeCards("Doesn't Exist", 2, RailCard.BLUE);
//    });
//  }
//
//  @Test
//  public void TestAcquireConnectionValid() {
//    IRailConnection rail = new RailConnection(new UnorderedPair<>(cityA, cityB), 4, RailColor.BLUE);
//
//    assertFalse(simpleGameState.getOccupiedConnections().containsKey(rail));
//    simpleGameState.acquireConnection("player1", rail);
//    assertTrue(simpleGameState.getOccupiedConnections().containsKey(rail));
//  }
//
//  @Test
//  public void TestAcquireConnectionAlreadyOccupied() {
//    IRailConnection rail = new RailConnection(new UnorderedPair<>(cityA, cityB), 4, RailColor.BLUE);
//    simpleGameState.acquireConnection("player1", rail);
//
//    Assertions.assertThrows(IllegalStateException.class, () -> {
//      simpleGameState.acquireConnection("player1", rail);
//    });
//  }
//
//  @Test
//  public void TestAcquireConnectionInvalidPlayerID() {
//    IRailConnection rail = new RailConnection(new UnorderedPair<>(cityA, cityB), 4, RailColor.BLUE);
//
//    Assertions.assertThrows(IllegalArgumentException.class, () -> {
//      simpleGameState.acquireConnection("Doesn't Exist", rail);
//    });
//  }
//
//  @Test
//  public void TestAcquireConnectionRailDoesntExist() {
//    IRailConnection nonExistentRail = new RailConnection(new UnorderedPair<>(cityA, cityB), 4, RailColor.RED);
//
//    Assertions.assertThrows(IllegalArgumentException.class, () -> {
//      simpleGameState.acquireConnection("player1", nonExistentRail);
//    });
//  }
//
//  @Test
//  public void TestValidRemoveRailsFromBank() {
//    assertEquals(simpleGameState.getRailsInBank("player1"), 6);
//    simpleGameState.removeRailsFromBank("player1", 2);
//    assertEquals(simpleGameState.getRailsInBank("player1"), 4);
//  }
//
//  @Test
//  public void TestRemoveRailsFromBankNotEnoughRails() {
//    Assertions.assertThrows(IllegalStateException.class, () -> {
//      simpleGameState.removeRailsFromBank("player1", 7);
//    });
//  }
//
//  @Test
//  public void TestRemoveRailsFromBankNegativeArg() {
//    Assertions.assertThrows(IllegalArgumentException.class, () -> {
//      simpleGameState.removeRailsFromBank("player1", -2);
//    });
//  }
//
//  @Test
//  public void TestRemoveRailsFromBankInvalidPlayerID() {
//    Assertions.assertThrows(IllegalArgumentException.class, () -> {
//      simpleGameState.removeRailsFromBank("Doesn't Exist", 3);
//    });
//  }
//
//  @Test
//  public void TestValidDrawCards() {
//    assertFalse(simpleGameState.getCardsInHand("player1").getHand().containsKey(RailCard.GREEN));
//    assertFalse(simpleGameState.getCardsInHand("player1").getHand().containsKey(RailCard.WHITE));
//    simpleGameState.drawCards("player1", 2);
//    assertEquals((int) simpleGameState.getCardsInHand("player1").getHand().get(RailCard.GREEN), 1);
//    assertEquals((int) simpleGameState.getCardsInHand("player1").getHand().get(RailCard.WHITE), 1);
//  }
//
//  @Test
//  public void TestDrawCardsNotEnoughCards() {
//    Assertions.assertThrows(IllegalStateException.class, () -> {
//      simpleGameState.drawCards("player1", 900000);
//    });
//  }
//
//  @Test
//  public void TestDrawCardsNegativeArg() {
//    Assertions.assertThrows(IllegalArgumentException.class, () -> {
//      simpleGameState.drawCards("player1", -2);
//    });
//  }
//
//  @Test
//  public void TestDrawCardsInvalidPlayerID() {
//    Assertions.assertThrows(IllegalArgumentException.class, () -> {
//      simpleGameState.drawCards("Doesn't Exist", 3);
//    });
//  }
//}
