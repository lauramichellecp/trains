package json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import manager.TournamentResult;
import player.IPlayer;
import json.RankJson;
import json.PlayerInstanceJson;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/** Utility class for serializing and deserializing Json related to TournamentResult(s) and/or rankings of IPlayer. */
public class OutcomesJson {
    /**
     * Takes the winners and cheaters and returns a new JsonElement of the end outcome of a tournament
     * @param report the tournament result
     * @param playerNameLookup the lookup table to player names
     * @return a JsonArray of two arrays: first is the JsonArray of winners and the second is the JsonArray of cheaters
     */
    public static JsonElement tournamentOutcomeToJson(TournamentResult report, Map<IPlayer, String> playerNameLookup) {
        JsonArray winners = RankJson.playersToRankJson(report.first, playerNameLookup);
        JsonArray cheaters = RankJson.playersToRankJson(report.second, playerNameLookup);

        JsonArray endResult = new JsonArray();
        endResult.add(winners);
        endResult.add(cheaters);
        return endResult;
    }

    /**
     * Creates a new JsonElement representing the outcome of a game of Trains.
     *
     * @param outcome          a List of Set of IPlayer representing the rankings per score of all active players.
     * @param removedPlayers   the Set of IPlayer representing eliminated players
     * @param playerNameLookup a Map of IPlayer to String representing each IPlayer's name.
     * @return a JSON array with two values: a JSON array of rankings and a JSON array of removed players.
     */
    public static JsonElement gameEndOutcomeToJson(List<Set<IPlayer>> outcome, Set<IPlayer> removedPlayers,
                                                Map<IPlayer, String> playerNameLookup) {
        JsonArray ranking = new JsonArray();
        JsonArray removed = RankJson.playersToRankJson(new HashSet<>(removedPlayers), playerNameLookup);

        for (Set<IPlayer> players : outcome) {
            ranking.add(PlayerInstanceJson.playerInstancesToJson(players, playerNameLookup));
        }

        JsonArray endResult = new JsonArray();
        endResult.add(ranking);

        endResult.add(removed);
        return endResult;
    }
}
