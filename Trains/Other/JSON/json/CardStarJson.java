package json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import game_state.RailCard;
import referee.IPlayerHand;
import utils.RailCardUtils;

/** Utility class for serializing and deserializing Json related to RailCard(s). */
public class CardStarJson {
    /**
     * Creates a list of cards given a Json array of colors for a game of trains.
     *
     * @param colors a JsonElement representing the color cards in the deck
     * @return a List of RailCard, representing a deck
     */
    public static List<RailCard> cardsFromJson(JsonElement colors) {
        List<RailCard> deck = new ArrayList<>();
        for (JsonElement color : colors.getAsJsonArray()) {
            deck.add(RailCardUtils.railCardFromLowercaseCard(color.getAsString()));
        }
        return deck;
    }

    /**
     * Returns the JsonArray object of the strings representing a list of colors (RailCard)
     * @param cards a List of RailCard representing cards
     * @return a new JsonArray containing each RailCard as a string
     */
    public static JsonArray cardsToJson(List<RailCard> cards) {
        JsonArray cardsStarJson = new JsonArray();
        for (RailCard card : cards) {
            cardsStarJson.add(card.toString());
        }
        return cardsStarJson;
    }

    /**
     * Returns a JsonObject representing the given "hand" of cards
     * @param hand a Map where the value is a natural number representing the count of the RailCard
     * @return a new JsonObject with the proper counts for each RailCard.
     */
    public static JsonObject handToJsonDictionary(Map<RailCard, Integer> hand) {
        JsonObject ret = new JsonObject();
        ret.addProperty("white", hand.get(RailCard.WHITE));
        ret.addProperty("red", hand.get(RailCard.RED));
        ret.addProperty("green", hand.get(RailCard.GREEN));
        ret.addProperty("blue", hand.get(RailCard.BLUE));

        return ret;
    }
}
