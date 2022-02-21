package manager;

import game_state.RailCard;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import referee.GameEndReport;
import referee.IReferee;
import referee.TrainsReferee;
import utils.CommunicationUtils;
import utils.InitializationUtils;

/**
 * This KnockOutTournamentManager runs a tournament of Trains on a list of players, constructed through the
 * {@link ManagerBuilder}.
 *
 *  <p>One tournament manager object is intended to run a single tournament of games of Trains.
 *
 *  A KnockOutTournamentManager runs a tournament as a knock-out elimination system. The first-placed finisher(s)
 *  of every game of round n move on to round n+1, and so on.
 */
public class KnockOutTournamentManager implements ITournamentManager {
  private static final int TIME_OUT_IN_SECONDS = 2;
  public static final int MAX_NUMBER_OF_USELESS_ROUNDS = 1;

  private final List<IPlayer> initialPlayersInOrder;
  private final Function<ITrainMap, List<Destination>> destinationProvider;
  private final Supplier<List<RailCard>> deckSupplier;

  /** Constructor defensively copies list of IPlayer. */
  private KnockOutTournamentManager(
      List<IPlayer> initialPlayersInOrder,
      Function<ITrainMap, List<Destination>> destinationProvider,
      Supplier<List<RailCard>> deckSupplier) {
    this.initialPlayersInOrder = new ArrayList<>(initialPlayersInOrder);
    this.destinationProvider = destinationProvider;
    this.deckSupplier = deckSupplier;
  }

  /**
   * To construct instances of this referee, requiring a map and initial number of players, and
   * optionally a means of ordering potential destinations and creating a deck.
   */
  public static class ManagerBuilder {

    private static final int NUM_CARDS_IN_DECK = 250;

    private final List<IPlayer> playersInOrder;
    private Function<ITrainMap, List<Destination>> destinationProvider;
    private Supplier<List<RailCard>> deckSupplier;

    /**
     * Constructs this builder from the required map and players.
     *
     * @param playersInOrder the players in their turn order.
     * @throws IllegalArgumentException if the number of players is not between 2 and 8, inclusive.
     */
    public ManagerBuilder(List<IPlayer> playersInOrder) throws IllegalArgumentException {
      Objects.requireNonNull(playersInOrder);
      for (IPlayer player : playersInOrder) {
        Objects.requireNonNull(player);
      }

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
    public ManagerBuilder destinationProvider(
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
    public ManagerBuilder deckProvider(Supplier<List<RailCard>> deckProvider) {
      this.deckSupplier = deckProvider;
      return this;
    }

    /**
     * Builds the referee, throwing exceptions if any inputs are null.
     *
     * @return the constructed referee, ready to play a game.
     */
    public KnockOutTournamentManager build() {
      Objects.requireNonNull(this.deckSupplier);
      Objects.requireNonNull(this.destinationProvider);
      return new KnockOutTournamentManager(
          this.playersInOrder, this.destinationProvider, this.deckSupplier);
    }
  }

  @Override
  public TournamentResult runTournament() {
    TournamentState tournamentState = this.initializeTournament();
    while (tournamentState.tournamentStillActive(MAX_NUMBER_OF_USELESS_ROUNDS, TrainsReferee.MIN_PLAYERS_PER_GAME)) {
      if(tournamentState.oneMoreGame(TrainsReferee.MIN_PLAYERS_PER_GAME, TrainsReferee.MAX_PLAYERS_PER_GAME)) {
        runOneGame(tournamentState);
        break;
      } else {
        runTournamentRound(tournamentState);
      }
    }

    notifyPlayersEnd(tournamentState);

    return new TournamentResult(
            new HashSet<>(tournamentState.stillAlive), tournamentState.cheaters);
  }

  /**
   * Notifies the players of the result of the tournament.
   * @param tournamentState the current state of the tournament.
   */
  private void notifyPlayersEnd(TournamentState tournamentState) {
    for(IPlayer player : tournamentState.stillAlive) {
      CommunicationUtils.tryPlayerInteraction(
        (p) -> {
          p.tournamentResult(true);
          return true;
        }, player, TIME_OUT_IN_SECONDS);
    }

    List<IPlayer> onlyLosers = new ArrayList<IPlayer>(this.initialPlayersInOrder){{
      removeAll(tournamentState.stillAlive);
      removeAll(tournamentState.cheaters);
    }};
    
    for(IPlayer player : onlyLosers) {
      CommunicationUtils.tryPlayerInteraction(
        (p) -> {
          p.tournamentResult(false);
          return true;
        }, player, TIME_OUT_IN_SECONDS);
    }
  }

  //region Initialization

  /**
   * Initializes a tournament by asking all signed up players to submit a map, and eliminating ones who do not.
   * @return a new TournamentState, representing the
   */
  public TournamentState initializeTournament() {
    List<IPlayer> survivedSetup = new ArrayList<>();
    List<IPlayer> misbehavedInSetup = new ArrayList<>();
    List<ITrainMap> submittedMaps = new ArrayList<>();

    for (IPlayer player : this.initialPlayersInOrder) {
      Optional<ITrainMap> submittedMap =
          CommunicationUtils.tryPlayerInteraction(
              IPlayer::tournamentStart, player, TIME_OUT_IN_SECONDS);
      if (submittedMap.isPresent()) {
        survivedSetup.add(player);
        if(!InitializationUtils.notEnoughDestinations(submittedMap.get().getAllPossibleDestinations().size(), initialPlayersInOrder.size(), 5, 2)) {
          submittedMaps.add(submittedMap.get());
        }
      } else {
        misbehavedInSetup.add(player);
      }
    }
    return new TournamentState(
        chooseTournamentMap(submittedMaps), survivedSetup, new HashSet<>(misbehavedInSetup));
  }

  /**
   * Chooses a Map for the entire tournament given a list of TrainsMap to choose from.
   * @param mapSubmissions a List of TrainsMap submissions from all players that have signed up
   * @return the TrainsMap chosen for the tournament.
   */
  private ITrainMap chooseTournamentMap(List<ITrainMap> mapSubmissions) {
    int minDestPerMap =
        InitializationUtils.minDestinationsToPlay(
            Math.min(TrainsReferee.MAX_PLAYERS_PER_GAME, mapSubmissions.size()),
            TrainsReferee.PLAYER_NUM_DEST_OPTIONS,
            TrainsReferee.PLAYER_NUM_DEST_OPTIONS - TrainsReferee.PLAYER_NUM_DEST_TO_REJECT);

    List<ITrainMap> feasibleMaps =
        mapSubmissions.stream()
            .filter((map) -> map.getAllPossibleDestinations().size() >= minDestPerMap)
            .collect(Collectors.toList());

    if (feasibleMaps.isEmpty()) {
      throw new RuntimeException("No valid maps submitted.");
    } else {
      Collections.shuffle(feasibleMaps);
      return feasibleMaps.get(0);
    }
  }
  //endregion


  /**
   * Runs a round of games in a tournament.
   * @param tournamentState the current state of the tournament.
   */
  private void runTournamentRound(TournamentState tournamentState) {
    List<List<IPlayer>> initPlayers = InitializationUtils.orderedMaximumGrouping(
            new ArrayList<>(tournamentState.stillAlive),
            TrainsReferee.MIN_PLAYERS_PER_GAME, TrainsReferee.MAX_PLAYERS_PER_GAME);

    List<IPlayer> survivedThisRound = new ArrayList<>();
    List<IPlayer> cheatedThisRound = new ArrayList<>();

    for (List<IPlayer> singleGamePlayers : initPlayers) {
      runAGame(tournamentState.tournamentMap, singleGamePlayers, survivedThisRound, cheatedThisRound);
    }
    tournamentState.updateOnRoundEnd(survivedThisRound, new HashSet<>(cheatedThisRound),
            survivedThisRound.equals(tournamentState.stillAlive));
  }

  /**
   * Runs a game of Trains for the tournament.
   * @param tournamentState the current state of the tournament.
   */
  private void runOneGame(TournamentState tournamentState) {
    List<IPlayer> survivedThisRound = new ArrayList<>();
    List<IPlayer> cheatedThisRound = new ArrayList<>();

    runAGame(tournamentState.tournamentMap, tournamentState.stillAlive, survivedThisRound, cheatedThisRound);
    
    tournamentState.updateOnRoundEnd(survivedThisRound, new HashSet<>(cheatedThisRound),
            survivedThisRound.equals(tournamentState.stillAlive));
  }


  /**
   * Runs a single game of Trains and updates the surviving players and the cheating players.
   * @param tournamentMap an ITrainMap for the tournament.
   * @param singleGamePlayers the List of ITournamentPlayer to run a single game of Trains.
   * @param survivedThisRound the List of ITournamentPlayer that got the highest score in the game.
   * @param cheatedThisRound the List of ITournamentPlayer that cheated during the game.
   */
  private void runAGame(ITrainMap tournamentMap, List<IPlayer> singleGamePlayers,
                        List<IPlayer> survivedThisRound,
                        List<IPlayer> cheatedThisRound) {
    // Run a game
    IReferee ref = new TrainsReferee.RefereeBuilder(tournamentMap, new ArrayList<>(singleGamePlayers)).
            deckProvider(this.deckSupplier).destinationProvider(this.destinationProvider).build();
    GameEndReport gameResult = ref.playGame();
          
    if (gameResult.playerRanking.size() > 0) {
      List<GameEndReport.PlayerScore> maxScorePlayers = gameResult.playerRanking.stream().filter(p -> p.score == gameResult.playerRanking.get(0).score).collect(Collectors.toList());
      survivedThisRound.addAll(maxScorePlayers.stream().map(p -> singleGamePlayers.
              get(singleGamePlayers.indexOf(p.player))).collect(Collectors.toList()));
    }

    cheatedThisRound.addAll(gameResult.removedPlayers.stream().map(p -> singleGamePlayers.
            get(singleGamePlayers.indexOf(p))).collect(Collectors.toList()));
  }
}
