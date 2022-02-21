package referee;

import game_state.RailCard;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import referee.GameEndReport.PlayerScore;
import strategy.TurnAction;
import utils.CommunicationUtils;
import utils.InitializationUtils;

/**
 * This Referee runs games of Trains on a given map and list of players, constructed through the
 * {@link RefereeBuilder}.
 *
 * <p>One referee object is intended to run one game of Trains.
 *
 * <p>This referee will remove players from the game for the following abnormal interactions"
 *
 * <ul>
 *   <li>Any exception thrown by the player when a method is called on it.
 *   <li>Any response from the player that is well-formed but invalid (e.g., incorrect number of
 *       destinations, attempting to acquire a connection that is already occupied)
 * </ul>
 *
 * <p>Other abnormal interactions are not directly handled by the referee and will instead be
 * handled during the project phase that adds remote communication. That being said, these
 * interactions will be handled by the referee by catching an exception thrown by the communication
 * component for the abnormal interaction so as to unilaterally avoid bringing down the system for
 * any reason.
 *
 * <p>For example:
 *
 * <ul>
 *   <li>A TimeOutException thrown by the component communicating with the player
 *   <li>An exception thrown for receiving a non-well-formed message as response from the player
 * </ul>
 */
public class TrainsReferee implements IReferee {
  public static final int MAX_PLAYERS_PER_GAME = 8;
  public static final int MIN_PLAYERS_PER_GAME = 2;
  public static final int PLAYER_NUM_RAILS_START = 45;
  public static final int PLAYER_NUM_CARDS_START = 4;
  public static final int PLAYER_NUM_DEST_OPTIONS = 5;
  public static final int PLAYER_NUM_DEST_TO_REJECT = 3;

  public static final int TIME_OUT_IN_SECONDS = 2;

  // All fields are things needed to be saved from construction to playGame()
  private final ITrainMap map;
  private final List<IPlayer> initialPlayersInOrder;
  private final Function<ITrainMap, List<Destination>> destinationProvider;
  private final Supplier<List<RailCard>> deckSupplier;

  // region Construction

  /** Constructor defensively copies list of IPlayer. */
  private TrainsReferee(
      ITrainMap map,
      List<IPlayer> initialPlayersInOrder,
      Function<ITrainMap, List<Destination>> destinationProvider,
      Supplier<List<RailCard>> deckSupplier) {
    this.map = map;
    this.initialPlayersInOrder = new ArrayList<>(initialPlayersInOrder);
    this.destinationProvider = destinationProvider;
    this.deckSupplier = deckSupplier;
  }

  /**
   * To construct instances of this referee, requiring a map and initial number of players, and
   * optionally a means of ordering potential destinations and creating a deck.
   */
  public static class RefereeBuilder {

    private static final int NUM_CARDS_IN_DECK = 250;

    private final ITrainMap map;
    private final List<IPlayer> playersInOrder;
    private Function<ITrainMap, List<Destination>> destinationProvider;
    private Supplier<List<RailCard>> deckSupplier;

    /**
     * Constructs this builder from the required map and players.
     *
     * @param map the map for the game.
     * @param playersInOrder the players in their turn order.
     * @throws IllegalArgumentException if the number of players is not between 2 and 8, inclusive.
     */
    public RefereeBuilder(ITrainMap map, List<IPlayer> playersInOrder)
        throws IllegalArgumentException {
      Objects.requireNonNull(map);
      Objects.requireNonNull(playersInOrder);
      if (playersInOrder.size() < MIN_PLAYERS_PER_GAME
          || playersInOrder.size() > MAX_PLAYERS_PER_GAME) {
        throw new IllegalArgumentException(
            "Number of players should be between "
                + MIN_PLAYERS_PER_GAME
                + " and "
                + MAX_PLAYERS_PER_GAME);
      }
      if (InitializationUtils.notEnoughDestinations(map.getAllPossibleDestinations().size(),
              playersInOrder.size(),
              PLAYER_NUM_DEST_OPTIONS,
              PLAYER_NUM_DEST_OPTIONS - PLAYER_NUM_DEST_TO_REJECT)) {
        throw new IllegalArgumentException(
            "Not enough unique destinations available in the map for given number of players.");
      }
      for (IPlayer player : playersInOrder) {
        Objects.requireNonNull(player);
      }

      this.map = map;
      this.playersInOrder = playersInOrder;
      this.destinationProvider = InitializationUtils::defaultDestinationProvider;
      this.deckSupplier = () -> InitializationUtils.defaultDeckSupplier(NUM_CARDS_IN_DECK);
    }

    /**
     * Updates the destination provider.
     *
     * @param destinationProvider new destination provider.
     * @return the updated builder for chaining.
     */
    public RefereeBuilder destinationProvider(
        Function<ITrainMap, List<Destination>> destinationProvider) {
      this.destinationProvider = destinationProvider;
      return this;
    }

    /**
     * Updates teh deck provider.
     *
     * @param deckProvider new deck provider.
     * @return the updated builder for chaining.
     */
    public RefereeBuilder deckProvider(Supplier<List<RailCard>> deckProvider) {
      this.deckSupplier = deckProvider;
      return this;
    }

    /**
     * Builds the referee, throwing exceptions if any inputs are null.
     *
     * @return the constructed referee, ready to play a game.
     */
    public TrainsReferee build() {
      Objects.requireNonNull(this.deckSupplier);
      Objects.requireNonNull(this.destinationProvider);

      return new TrainsReferee(
          this.map, this.playersInOrder, this.destinationProvider, this.deckSupplier);
    }
  }
  // endregion

  @Override
  public GameEndReport playGame() {
    RefereeGameState refereeGameState = this.initializeGame(this.initialPlayersInOrder);
    return this.runGame(refereeGameState);
  }

  // region Running the Game

  /**
   * Runs the game starting from the given, newly-created RefereeGameState until completion,
   * returning the scores/ranking.
   *
   * @param refereeGameState the newly created RefereeGameState, ready for the first player to take
   *     the first turn.
   * @return a GameEndReport detailing which players were removed and the ranking/scores of
   *     remaining players.
   */
  private GameEndReport runGame(RefereeGameState refereeGameState) {
    while (!refereeGameState.isGameOver()) {
      // this performs communication and actual action of turn-taking
      // (mutating game state to draw cards, etc.)
      TurnResult turnResult = this.takePlayerTurn(refereeGameState);

      // this deals with transitioning to the next turn
      this.processTurnResult(turnResult, refereeGameState);
    }
    GameEndReport results = this.calculateGameEndReport(refereeGameState);
    reportResults(results);
    return results;
  }

  /**
   * Report whether the player was winner of the game to this player
   * @param report The ending game report
   */
  private void reportResults(GameEndReport report) {
    if(report.playerRanking.size() != 0) {
      int winningScore = report.playerRanking.get(0).score;
      for(PlayerScore rank : report.playerRanking) {
        if(rank.score == winningScore) {
          rank.player.winNotification(true);
        } else {
          rank.player.winNotification(false);
        }
      }
    }
  }

  /**
   * A turn can be "significant and valid", "insignificant but still valid", or "invalid".
   *
   * <p>A valid turn is significant iff it results in a change to the game state.
   *
   * <p>For example: SIGNIFICANT - legally occupying a connection INSIGNIFICANT - drawing cards when
   * there are none left (nothing changes) INVALID - attempting to occupy an already occupied
   * connection, or throwing an exception
   */
  private enum TurnResult {
    SIGNIFICANT,
    INSIGNIFICANT,
    INVALID
  }

  /**
   * Takes a single turn for the active player and returns the result of that turn.
   *
   * @param gameState the state of the game just before the player takes their turn.
   * @return TurnResult indicating whether turn was significant, insignificant, or invalid.
   */
  private TurnResult takePlayerTurn(RefereeGameState gameState) {
    IPlayer activePlayer = gameState.getActivePlayer().playerCommunication;

    Optional<TurnAction> playerTurnRequest =
            CommunicationUtils.tryPlayerInteraction((p) -> p.takeTurn(gameState.getActivePlayerState()),
                    activePlayer, TIME_OUT_IN_SECONDS);

    if (playerTurnRequest.isPresent()) {
      return applyActionToActivePlayer(playerTurnRequest.get(), gameState);
    } else {
      return TurnResult.INVALID;
    }
  }

  /**
   * Applies the specified turn action to the active player.
   *
   * @param action the action requested by the active player for their turn.
   * @param gameState the state of the game just before the player's turn action has been applied.
   * @return a TurnResult indicating whether the requested action was invalid, or resulted in a
   *     significant/insignificant change to the game state.
   */
  private TurnResult applyActionToActivePlayer(TurnAction action, RefereeGameState gameState) {
    switch (action.getActionType()) {
      case DRAW_CARDS:
        List<RailCard> drawnCards = gameState.drawCardsForActivePlayer();
        CommunicationUtils.tryPlayerInteraction((p) -> {
              p.receiveCards(new ArrayList<>(drawnCards));
              return true;
            }, gameState.getActivePlayer().playerCommunication, TIME_OUT_IN_SECONDS);
        return drawnCards.isEmpty() ? TurnResult.INSIGNIFICANT : TurnResult.SIGNIFICANT;
      case ACQUIRE_CONNECTION:
        boolean connectionAcquired =
            gameState.acquireConnectionForActivePlayer(action.getRailConnection());
        return connectionAcquired ? TurnResult.SIGNIFICANT : TurnResult.INVALID;
      default:
        return TurnResult.INVALID;
    }
  }

  /**
   * Processes the turn result for a preceding turn in order to transition to the next turn.
   *
   * <p>This will result either in advancing the turn or removal of active player if turnResult is
   * INVALID.
   *
   * @param turnResult the result of the previous turn (still corresponding to the active player in
   *     the game state)
   * @param gameState the game state just after a turn was taken, before advanced to the next turn.
   */
  private void processTurnResult(TurnResult turnResult, RefereeGameState gameState) {
    switch (turnResult) {
      case SIGNIFICANT:
        gameState.advanceTurn(true);
        break;
      case INSIGNIFICANT:
        gameState.advanceTurn(false);
        break;
      case INVALID:
        gameState.removeActivePlayer();
        break;
    }
  }

  /**
   * Computes the GameEndReport for the given RefereeGameState for which the game is over.
   *
   * @param gameState game state for a finished game.
   * @return the GameEndReport for the finished game.
   */
  private GameEndReport calculateGameEndReport(RefereeGameState gameState) {
    Map<IPlayer, Integer> nonCheaterScores = gameState.calculatePlayerScores();

    List<PlayerScore> gameReportScores = new ArrayList<>();
    for (PlayerInfoPair player : gameState.getPlayersInTurnOrder()) {
      gameReportScores.add(new PlayerScore(player.playerCommunication, nonCheaterScores.get(player.playerCommunication)));
    }
    gameReportScores.sort(Comparator.comparingInt(s -> -s.score));

    return new GameEndReport(gameReportScores, gameState.getRemovedPlayers());
  }

  // endregion

  // region Initialization

  /**
   * Creates a RefereeGameState for the initial game from the given players, using destination and
   * deck providers for initial setup, and communicating with players for setup and destination
   * selection. Players removed during setup will be reflected in the constructed RefereeGameState.
   *
   * @param playersInOrder the players to communicate with in turn order.
   * @return a RefereeGameState for just before the first player takes their first turn.
   */
  private RefereeGameState initializeGame(List<IPlayer> playersInOrder) {
    // accumulators tracking remaining destinations and deck
    List<Destination> activeDestinationList =
        new ArrayList<>(this.destinationProvider.apply(this.map));
    List<RailCard> deck = this.deckSupplier.get();
    // accumulators for results of player setup
    List<PlayerInfoPair> playerInfoPairInOrder = new ArrayList<>();
    Set<IPlayer> playersRemovedInSetup = new HashSet<>();

    // process each player, either resulting in successful IPlayerData or removal of player
    for (IPlayer player : playersInOrder) {
      // Communication for setup and choosing destinations occurs here
      Optional<IPlayerData> startingPlayerData =
          this.createSinglePlayerData(player, activeDestinationList, deck);

      // Interpret result of communication
      if (startingPlayerData.isPresent()) {
        playerInfoPairInOrder.add(new PlayerInfoPair(player, startingPlayerData.get()));
        // remove chosen destinations and given cards
        activeDestinationList.removeAll(startingPlayerData.get().getDestinations());
        deck =
            deck.stream()
                .skip(startingPlayerData.get().getPlayerHand().getTotalNumCards())
                .collect(Collectors.toList());
      } else {
        playersRemovedInSetup.add(player);
      }
    }

    return new RefereeGameState(playerInfoPairInOrder, playersRemovedInSetup, deck, this.map);
  }

  /**
   * Communicates with the player to set up and choose destinations in order to generate the
   * IPlayerData to start the game.
   *
   * @param player the player to communicate with.
   * @param activeDestinationList the list of destinations handed to players in order, from which
   *     destination options will be picked from the top.
   * @param deck the deck of cards, from which the top cards will be drawn and given to the player.
   * @return an optional containing IPlayerData if it was successfully created or empty if something
   *     went wrong and the player needs to be removed.
   */
  private Optional<IPlayerData> createSinglePlayerData(
          IPlayer player, List<Destination> activeDestinationList, List<RailCard> deck) {
    // Given to player
    List<RailCard> nextCards = new ArrayList<>(deck.subList(0, PLAYER_NUM_CARDS_START));
    List<Destination> nextDestinationsOptions =
        new ArrayList<>(activeDestinationList.subList(0, PLAYER_NUM_DEST_OPTIONS));

    // Try setup, then try destination selection
    boolean setupResult = this.setupPlayer(player, nextCards);
    if (setupResult) {
      Optional<Set<Destination>> chosenDestinations =
          this.playerChooseDestinations(player, nextDestinationsOptions);
      if (chosenDestinations.isPresent()) {
        IPlayerData startingPlayerData =
            new PlayerData(
                new TrainsPlayerHand(nextCards),
                PLAYER_NUM_RAILS_START,
                chosenDestinations.get(),
                new HashSet<>());
        return Optional.of(startingPlayerData);
      }
    }
    return Optional.empty();
  }

  /**
   * Communicates with the player to set up, giving information about map, starting hand, and num
   * rails.
   *
   * @param player the player to set up.
   * @param cardsForPlayer the hand the player will start with.
   * @return true if the setup occurred without error, false if some error occurred during setup
   *     resulting in player's removal.
   */
  private boolean setupPlayer(IPlayer player, List<RailCard> cardsForPlayer) {
    return CommunicationUtils.tryPlayerInteraction(
            (p) -> {
              p.setup(this.map, PLAYER_NUM_RAILS_START, new ArrayList<>(cardsForPlayer));
              return true;
            },
            player, TIME_OUT_IN_SECONDS)
        .isPresent();
  }

  /**
   * Calls the player to select their destinations from the given options.
   *
   * @param player the player choosing destinations.
   * @param playerDestinationOptions the options for the destinations (defensively copied).
   * @return An optional containing the chosen destinations or empty if player chose improperly.
   */
  private Optional<Set<Destination>> playerChooseDestinations(
          IPlayer player, List<Destination> playerDestinationOptions) {

    Optional<Set<Destination>> rejectedDestinations =
            CommunicationUtils.tryPlayerInteraction(
            (p) -> p.chooseDestinations(new HashSet<>(playerDestinationOptions)),
            player, TIME_OUT_IN_SECONDS);

    if (rejectedDestinations.isPresent()
        && validDestinationChoice(
            new HashSet<>(playerDestinationOptions), rejectedDestinations.get())) {
      // Set difference to obtain kept destinations
      Set<Destination> chosenDestinations = new HashSet<>(playerDestinationOptions);
      chosenDestinations.removeAll(rejectedDestinations.get());
      return Optional.of(chosenDestinations);
    }
    return Optional.empty();
  }

  /**
   * Determines if the set of provided destinations are a valid set for the player to reject.
   *
   * @param options the destination options given to the player.
   * @param rejected the destination options the player chose to reject.
   * @return true if the rejected destinations are all valid options and have the correct amount,
   *     false otherwise.
   */
  private boolean validDestinationChoice(Set<Destination> options, Set<Destination> rejected) {
    return options.containsAll(rejected) && rejected.size() == PLAYER_NUM_DEST_TO_REJECT;
  }

  // endregion
}
