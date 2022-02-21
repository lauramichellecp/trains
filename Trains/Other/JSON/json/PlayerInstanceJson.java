package json;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.List;
import java.util.function.Function;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import map.ITrainMap;
import player.IPlayer;
import player.RefereePlayer;

/** Utility class for serializing and deserializing Json related to players with names. */
public class PlayerInstanceJson {

    /**
     * Represents an IPlayer with a pairwise distinct name.
     */
    public static class PlayerWithName {
        final public String name;
        final public IPlayer player;

        PlayerWithName(String name, IPlayer player) {
            this.name = name;
            this.player = player;
        }
    }

    /**
     * Creates a List of players given a Json array of PlayerInstance(s) which contains two values:
     * a string representing the PlayerName, and a string a Strategy.
     *
     * @param allPlayerInstances the JsonElement representing the PlayerInstance of all players
     * @return a List of PlayerWithName, which represents an IPlayer with a pairwise distinct name.
     */
    public static List<PlayerWithName> playerInstancesFromJson(JsonElement allPlayerInstances, Function<String, String> stratToFilePath, ITrainMap map) {
        List<PlayerWithName> players = new ArrayList<>();

        for (JsonElement instance : allPlayerInstances.getAsJsonArray()) {
            String name = instance.getAsJsonArray().get(0).getAsString();
            String strategy = instance.getAsJsonArray().get(1).getAsString();
            players.add(new PlayerWithName(name, new RefereePlayer(stratToFilePath.apply(strategy), map)));
        }
        return players;
    }

    /**
     * Creates a List of players given a Json array of PlayerInstance(s) which contains two values:
     * a string representing the PlayerName, and a string a Strategy.
     *
     * @param allPlayerInstances the JsonElement representing the PlayerInstance of all players
     * @return a List of PlayerWithName, which represents an IPlayer with a pairwise distinct name.
     */
    public static List<PlayerWithName> playerInstancesFromJson(JsonElement allPlayerInstances, Function<String, String> stratToFilePath) {
        List<PlayerWithName> players = new ArrayList<>();

        for (JsonElement instance : allPlayerInstances.getAsJsonArray()) {
            String name = instance.getAsJsonArray().get(0).getAsString();
            String strategy = instance.getAsJsonArray().get(1).getAsString();
            players.add(new PlayerWithName(name, new RefereePlayer(stratToFilePath.apply(strategy))));
        }
        return players;
    }

    /**
     * Creates a JsonArray of PlayerInstance given some players and a lookuptable of IPlayer(s) and their name
     * @param players the Set of player
     * @param playerNameLookup the loop of IPlayer and their name
     * @return a new JsonArray representing PlayerInstance
     */
    public static JsonArray playerInstancesToJson(Set<IPlayer> players, Map<IPlayer, String> playerNameLookup) {
        JsonArray playersJson = new JsonArray();

        for(IPlayer player : players) {
            JsonArray playerInst = new JsonArray();
            playerInst.add(playerNameLookup.get(player));
        }
        return playersJson;
    }

    public static JsonArray playerInstancesToJsonWithStrat(List<PlayerWithName> players, ITrainMap map) {
        JsonArray playersJson = new JsonArray();

        for(PlayerWithName player : players) {
            JsonArray playerInst = new JsonArray();
            playerInst.add(player.name);
            //TODO GET STRAT SOMEHOW
        }
        return playersJson;
    } 
}
