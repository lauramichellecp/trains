package json;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import map.IRailConnection;
import map.RailConnection;
import map.ICity;
import map.City;
import map.RailColor;
import map.Destination;
import game_state.RailCard;
import game_state.PlayerGameState;
import game_state.IOpponentInfo;
import game_state.IPlayerGameState;
import game_state.OpponentInfo;
import referee.IPlayerData;
import referee.PlayerData;
import referee.TrainsPlayerHand;

import utils.RailCardUtils;
import utils.UnorderedPair;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;

import json.DestinationJson;
import json.CardStarJson;

/** Utility class for serializing and deserializing Json related to IPlayerGameState. */
public class PlayerStateJson {

    /**
     * Constructs a JsonObject from the given IPlayerGameState
     */
    public static JsonObject playerStateToJson(IPlayerGameState gamestate) {
        JsonObject playerStateJson = new JsonObject();

        JsonObject thisPlayerJson = new JsonObject();

        //Add Destinations
        Set<Destination> destinations = gamestate.getDestinations();
        Iterator<Destination> destinationIterator = destinations.iterator();

        thisPlayerJson.add("destination1", DestinationJson.destinationToJson(destinationIterator.next()));
        thisPlayerJson.add("destination2", DestinationJson.destinationToJson(destinationIterator.next()));

        //Add rails
        thisPlayerJson.addProperty("rails", gamestate.getNumRails());
        
        //Add cardstar
        thisPlayerJson.add("cards", CardStarJson.handToJsonDictionary(gamestate.getCardsInHand()));

        //add acquired
        List<JsonArray> allConnections = gamestate.getOwnedConnections().stream().map(ConnectionJson::connectionToAcquiredJson).collect(Collectors.toList());
        JsonArray connections = new JsonArray();
        allConnections.forEach(connections::add);
        thisPlayerJson.add("acquired", connections);

        playerStateJson.add("this", thisPlayerJson);

        JsonArray opponentAcquired = new JsonArray();
        for(IOpponentInfo opponent : gamestate.getOpponentInfo()) {
          List<JsonArray> allOpponentConnections = opponent.getOwnedConnections().stream().map(ConnectionJson::connectionToAcquiredJson).collect(Collectors.toList());
          JsonArray opponentConnections = new JsonArray();
          allOpponentConnections.forEach(opponentConnections::add);
          opponentAcquired.add(opponentConnections);
        }
        playerStateJson.add("acquired", opponentAcquired);

        return playerStateJson;
    }

    /**
   * Constructs a single IPlayerGameState from the given JSON
   */
  public static IPlayerGameState playerStateFromJson(JsonElement playerStateJson) {
    JsonObject playerObject = playerStateJson.getAsJsonObject();
    JsonObject thisPlayersData = playerObject.getAsJsonObject("this");
    Map<RailCard, Integer> cardsInHand =
        cardsInHandFromJson(thisPlayersData.getAsJsonObject("cards"));

    Set<IRailConnection> occupiedConnections = occupiedConnectionsFromJson(playerObject);

    return new PlayerGameState(
        new PlayerData(
            new TrainsPlayerHand(cardsInHand),
            thisPlayersData.get("rails").getAsInt(),
            new HashSet<>(),
            occupiedConnections),
        new ArrayList<>());
  }

  /**
   * Returns the hand of cards for the given JSON specification of a hand of cards.
   */
  public static Map<RailCard, Integer> cardsInHandFromJson(JsonObject cardsJson) {
    Map<RailCard, Integer> cardsInHand = new HashMap<>();
    for (String cardString : cardsJson.keySet()) {
      cardsInHand.put(
          RailCardUtils.railCardFromLowercaseCard(cardString),
          cardsJson.get(cardString).getAsInt());
    }
    return cardsInHand;
  }

  /**
   * Returns set of IRailConnection occupied by all players from the given player game state json.
   */
  public static Set<IRailConnection> occupiedConnectionsFromJson(
      JsonObject playerStateJson) {
    Set<IRailConnection> result = new HashSet<>();
    for (JsonElement player : playerStateJson.get("acquired").getAsJsonArray()) {
      result.addAll(occupiedConnectionForPlayer(player));
    }
    result.addAll(
        occupiedConnectionForPlayer(
            playerStateJson.getAsJsonObject("this").get("acquired").getAsJsonArray()));
    return result;
  }

  /**
   * Returns set of IRailConnection occupied by a player.
   */
  public static Set<IRailConnection> occupiedConnectionForPlayer(JsonElement player) {
    Set<IRailConnection> result = new HashSet<>();
    for (JsonElement connection : player.getAsJsonArray()) {
      result.add(acquiredConnectionFromJson(connection));
    }
    return result;
  }

  /**
   * Constructs one IRailConnection from JSON specification.
   */
  public static IRailConnection acquiredConnectionFromJson(JsonElement connectionJson) {
    JsonArray jsonArray = connectionJson.getAsJsonArray();
    ICity city1 = new City(jsonArray.get(0).getAsString(), 0, 0);
    ICity city2 = new City(jsonArray.get(1).getAsString(), 0, 0);
    RailColor color = RailCardUtils.railColorFromLowercaseColor(jsonArray.get(2).getAsString());
    int length = jsonArray.get(3).getAsInt();
    return new RailConnection(new UnorderedPair<>(city1, city2), length, color);
  }
}
