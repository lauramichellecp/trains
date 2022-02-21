package json;

import com.google.gson.JsonArray;

import player.IPlayer;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

public class RankJson {
    /**
    * Creates a new JsonArray representing a set of players.
    *
    * @param players          a Set of IPlayer to convert into a JSON array
    * @param playerNameLookup a Map of IPlayer to String representing each IPlayer's name.
    * @return a JSONArray of PlayerInstance(s) representing each IPlayer with their name.
    */
   public static JsonArray playersToRankJson(Set<IPlayer> players, Map<IPlayer, String> playerNameLookup) {
       List<String> sortedNames = players.stream().map(playerNameLookup::get).sorted().collect(Collectors.toList());

       JsonArray rank = new JsonArray();
       for (String name : sortedNames) {
           rank.add(name);

       }
       return rank;
   }
}
