package json;

import java.lang.IllegalArgumentException;

import com.google.gson.JsonArray;

import java.util.Optional;

import map.Destination;
import map.ITrainMap;
import map.ICity;

/** Utility class for serializing and deserializing Json related to Destination. */
public class DestinationJson {

    /**
     * Creates a JsonArray representing a given destination
     * @param destination the Destination to be converted
     * @return a new JsonArray
     */
    public static JsonArray destinationToJson(Destination destination) {
        JsonArray dest = new JsonArray();
        dest.add(destination.first.getName());
        dest.add(destination.second.getName());
        return dest;
    }

    /**
     * Creates a Destination given a JsonArray representing a destination and a map
     * @param destinationJson a JsonArray for a destination
     * @param map an ITrainMap
     * @return a new Destination that exists in the given ITrainMap
     */
    public static Destination destinationFromJson(JsonArray destinationJson, ITrainMap map) {
        if(destinationJson.size() != 2) {
            throw new IllegalArgumentException("Json not a legal destination");
        }

        Optional<ICity> city1Opt = map.getCityFromName(destinationJson.get(0).getAsString());
        Optional<ICity> city2Opt = map.getCityFromName(destinationJson.get(1).getAsString());
        if(city1Opt.isPresent() && city2Opt.isPresent()) {
            return new Destination(city1Opt.get(), city2Opt.get());
        } 
        throw new IllegalArgumentException("Invalid destination json");
    }
}
