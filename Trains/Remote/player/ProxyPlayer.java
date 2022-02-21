package player;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;
import com.google.gson.JsonElement;

import game_state.IPlayerGameState;
import game_state.RailCard;
import map.Destination;
import map.ITrainMap;
import player.IPlayer;
import strategy.TurnAction;
import utils.CommunicationUtils;
import remote.TrainsServer;
import json.CardStarJson;
import json.MapJson;
import json.PlayerStateJson;
import json.DestinationJson;
import json.TurnActionJson;

/**
 * Represents a player proxy that contains a socket and implements the IPlayer interface.
 */
public class ProxyPlayer implements IPlayer {

    private static final int MAX_PLAYER_NAME_LENGTH = 50;
    private static final int SOCKET_TIMEOUT = 2000;
    private Socket socket;
    private OutputStreamWriter proxyOut;
    private JsonStreamParser proxyIn;
    private Gson gson;
    private ITrainMap map;
    
    public ProxyPlayer(Socket socket) {
        this.socket = socket;

        try {
            socket.setSoTimeout(SOCKET_TIMEOUT);
            proxyOut = new OutputStreamWriter(socket.getOutputStream());
            proxyIn = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        gson = (new GsonBuilder()).setPrettyPrinting().create();
        map = null;
    }

    @Override
    public void setup(ITrainMap map, int numRails, List<RailCard> cards) {
        this.map = map;
        JsonArray wrapper = new JsonArray();
        JsonArray args = new JsonArray();

        args.add(MapJson.mapToJson(map));
        args.add(numRails);
        args.add(CardStarJson.cardsToJson(cards));
        wrapper.add("setup");
        wrapper.add(args);
        try {
            proxyOut.write(gson.toJson(wrapper));
            proxyOut.flush();
            JsonElement response = proxyIn.next();
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("setup failed");
        }
    }

    @Override
    public Set<Destination> chooseDestinations(Set<Destination> options) {
        List<JsonArray> destinations = options.stream().map(DestinationJson::destinationToJson).collect(Collectors.toList());

        JsonArray wrapper = new JsonArray();
        JsonArray args = new JsonArray();
        JsonArray destinationsPlus = new JsonArray();
        destinations.forEach(destinationsPlus::add);
        args.add(destinationsPlus);
        wrapper.add("pick");
        wrapper.add(args);

        try {
            proxyOut.write(gson.toJson(wrapper));
            proxyOut.flush();
            JsonElement response = proxyIn.next();
            JsonArray allDestinations = response.getAsJsonArray();
            Set<Destination> returnedDestinations = new HashSet<Destination>();
            for(JsonElement destination : allDestinations) {
                returnedDestinations.add(DestinationJson.destinationFromJson(destination.getAsJsonArray(), map));
            }
            return returnedDestinations;
        } catch (NoSuchElementException e) {
            //TIMEOUT
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("choose destination failed");
    }

    @Override
    public TurnAction takeTurn(IPlayerGameState playerGameState) {
        
        JsonObject message = PlayerStateJson.playerStateToJson(playerGameState);
        JsonArray wrapper = new JsonArray();
        JsonArray args = new JsonArray();
        args.add(message);
        wrapper.add("play");
        wrapper.add(args);
        try {
            proxyOut.write(gson.toJson(wrapper));
            proxyOut.flush();
            JsonElement response = proxyIn.next();
            return TurnActionJson.turnActionFromJson(response, map);
        } catch (NoSuchElementException e) {
            //TIMEOUT
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("take turn failed"); 
    }

    @Override
    public void receiveCards(List<RailCard> drawnCards) {
        JsonArray message = CardStarJson.cardsToJson(drawnCards);
        JsonArray wrapper = new JsonArray();
        JsonArray args = new JsonArray();
        args.add(message);
        wrapper.add("more");
        wrapper.add(args);

        try {
            proxyOut.write(gson.toJson(wrapper));
            proxyOut.flush();
            JsonElement response = proxyIn.next();
        } catch (NoSuchElementException e) {
            //TIMEOUT
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("receive cards failed");
        }
    }

    @Override
    public void winNotification(boolean thisPlayerWon) {
        JsonArray wrapper = new JsonArray();
        JsonArray args = new JsonArray();
        args.add(thisPlayerWon);
        wrapper.add("win");
        wrapper.add(args);

        try {
            proxyOut.write(gson.toJson(wrapper));
            proxyOut.flush();
            JsonElement response = proxyIn.next();
        } catch (NoSuchElementException e) {
            //TIMEOUT            
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("win notification failed");
        }
    }

    @Override
    public ITrainMap tournamentStart() {
        JsonArray wrapper = new JsonArray();
        JsonArray args = new JsonArray();
        wrapper.add("start");
        //Satisfy message spec, earlier in course boolean arg of start was declared optional
        args.add(true);
        wrapper.add(args);

        try {
            proxyOut.write(gson.toJson(wrapper));
            proxyOut.flush();
            JsonElement response = proxyIn.next();
            return MapJson.mapFromJson(response);
        } catch (NoSuchElementException e) {
            //TIMEOUT
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("tournament start failed"); 
    }

    @Override
    public void tournamentResult(boolean winner) {
        JsonArray wrapper = new JsonArray();
        JsonArray args = new JsonArray();
        args.add(winner);
        wrapper.add("end");
        wrapper.add(args);

        try {
            proxyOut.write(gson.toJson(wrapper));
            proxyOut.flush();
            JsonElement response = proxyIn.next();
        } catch (NoSuchElementException e) {
            //TIMEOUT
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("tournament result failed"); 
    }
}
