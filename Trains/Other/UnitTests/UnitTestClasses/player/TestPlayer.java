package player;

import game_state.IPlayerGameState;
import game_state.RailCard;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import map.Destination;
import map.ITrainMap;
import map_view.TestMapRenderer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import referee.IPlayerHand;
import referee.TrainsPlayerHand;
import strategy.BuyNow;
import strategy.Hold10;
import strategy.IStrategy;
import strategy.MockStrategy;
import strategy.TestStrategy;

public class TestPlayer {
  public static final String BUY_NOW_PATH = "target/classes/strategy/BuyNow.class";
  public static final String HOLD_10_PATH = "target/classes/strategy/Hold10.class";
  public static final String CHEAT_PATH = "target/classes/strategy/Cheat.class";

  IPlayer hold10FromStrategy;
  IPlayer buyNowFromStrategy;
  IPlayer hold10FromFile;
  IPlayer buyNowFromFile;

  @BeforeEach
  public void init() {
    this.hold10FromStrategy = new RefereePlayer(new Hold10());
    this.buyNowFromStrategy = new RefereePlayer(new BuyNow());
    this.buyNowFromFile = new RefereePlayer(BUY_NOW_PATH);
    this.hold10FromFile = new RefereePlayer(HOLD_10_PATH);
  }

  @Test
  public void TestPlayers() {
    // None of these boring methods should throw exceptions, etc
    TestBoringPlayerMethods(hold10FromStrategy);
    TestBoringPlayerMethods(hold10FromFile);
    TestBoringPlayerMethods(buyNowFromStrategy);
    TestBoringPlayerMethods(buyNowFromFile);
  }

  public void TestBoringPlayerMethods(IPlayer player) {
    player.winNotification(true);
    player.winNotification(false);
    player.receiveCards(null);
    player.receiveCards(Arrays.asList(RailCard.BLUE, RailCard.WHITE));
  }

  @Test
  public void TestSetUp() {
    // Test what is passed to strategy in case a player is not set up
    IPlayer p = new RefereePlayer(new MockStrategy(new HashSet<>(), 2, null, 0, null));
    p.chooseDestinations(new HashSet<>());

    // Test that set-up properly stores and passes information to the strategy
    IPlayerHand<RailCard> sampleHand = new TrainsPlayerHand(new ArrayList<>());
    sampleHand.addCardsToHand(RailCard.BLUE, 2);
    sampleHand.addCardsToHand(RailCard.RED, 1);
    p =
        new RefereePlayer(
            new MockStrategy(
                new HashSet<>(),
                2,
                TestMapRenderer.readAndParseTestMap("bos-sea-tex.json"),
                7,
                sampleHand.getHand()));
    p.setup(
        TestMapRenderer.readAndParseTestMap("bos-sea-tex.json"),
        7,
        Arrays.asList(RailCard.BLUE, RailCard.RED, RailCard.BLUE));
    p.chooseDestinations(new HashSet<>());
    p.takeTurn(null);
  }


  @Test
  public void TestPlayerIntegrations() {
    TestIntegration(this.buyNowFromFile, new BuyNow());
    TestIntegration(this.buyNowFromStrategy, new BuyNow());

    TestIntegration(this.hold10FromFile, new Hold10());
    TestIntegration(this.hold10FromStrategy, new Hold10());
  }

  private void TestIntegration(IPlayer player, IStrategy strategy) {
    ITrainMap map = TestStrategy.readAndParseTestMap("bos-sea-red-white.json").getFirst();
    IPlayerGameState gameState =
        TestStrategy.readAndParseTestMap("bos-sea-red-white.json").getSecond();
    TestStrategy s = new TestStrategy();
    s.init();

    player.setup(map, 0, new ArrayList<>());
    Set<Destination> chosenDestinations = player.chooseDestinations(s.destinations);
    s.destinations.removeAll(chosenDestinations);
    Assertions.assertEquals(
        strategy.chooseDestinations(s.destinations, 2, map, 0, null), s.destinations);

    Assertions.assertEquals(
        strategy.takeTurn(gameState, map, chosenDestinations).getActionType(),
        player.takeTurn(gameState).getActionType());
  }
}
