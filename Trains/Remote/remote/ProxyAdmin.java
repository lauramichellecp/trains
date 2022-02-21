package remote;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonStreamParser;

import game_state.IPlayerGameState;
import game_state.RailCard;
import player.IPlayer;
import strategy.TurnAction;
import json.*;
import map.ITrainMap;
import map.Destination;

/**
 *  Represents a proxy for the Admin component and contains a single socket.
 */
public class ProxyAdmin {
    private Socket socket;
    private String name;
    private IPlayer player; 
    private OutputStreamWriter proxyOut;
    private JsonStreamParser proxyIn;
    private ITrainMap map;

    private Gson gson;

    private final static String domain = "127.0.0.1";
    
    public ProxyAdmin(int port, String name, IPlayer player) {
        this(domain, port, name, player);
    }

    public ProxyAdmin(String domain, int port, String name, IPlayer player) {
        try {
            socket = new Socket(domain, port);
            proxyOut = new OutputStreamWriter(socket.getOutputStream());
            proxyIn = new JsonStreamParser(new InputStreamReader(socket.getInputStream()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.name = name;
        this.player = player;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            proxyOut.write("\"" + name + "\"");
            proxyOut.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle a single operation from a remote player
     */
    public void handleOp() {
        JsonArray nextOp = proxyIn.next().getAsJsonArray();
        String response;
        switch(nextOp.get(0).getAsString()) {
            case "setup":
                response = handleSetup(nextOp.get(1).getAsJsonArray());
                break;
            case "pick":
                response = handleChooseDestinations(nextOp.get(1).getAsJsonArray());
                break;
            case "play":
                response = handleTakeTurn(nextOp.get(1).getAsJsonArray());
                break;
            case "more":
                response = handleReceiveCards(nextOp.get(1).getAsJsonArray());
                break;
            case "win":
                response = handleWinNotification(nextOp.get(1).getAsJsonArray());
                break;
            case "start":
                response = handleTournamentStart(nextOp.get(1).getAsJsonArray());
                break;
            case "end":
                response = handleTournamentResult(nextOp.get(1).getAsJsonArray());
                break;
            default:
                return;
        }
        try {
            proxyOut.write(response);
            proxyOut.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        while(!socket.isInputShutdown() && !Thread.currentThread().isInterrupted()) {
            handleOp();
        }
    }

    /**
     * Parses a JsonArray with a map, rails and cards, and calls setup on the player
     * @param args the JsonObject containing the arguments to call setup.
     */
    public String handleSetup(JsonArray args) {
        ITrainMap map = MapJson.mapFromJson(args.get(0));
        this.map = map;
        List<RailCard> cards = CardStarJson.cardsFromJson(args.get(2));
        int rails = args.get(1).getAsInt();

        player.setup(map, rails, cards);
        return "\"void\"";
    }

    /**
     * Parses the given Json, and returns the result of calling chooseDestinations on the player to output stream.
     * @param args the JsonArray containing the arguments to chooseDestinations.
     */
    public String handleChooseDestinations(JsonArray args) {
        Set<Destination> destinations = new HashSet<>();
        args.get(0).getAsJsonArray().forEach((element) -> {
            JsonArray destinationJson = element.getAsJsonArray();
            destinations.add(DestinationJson.destinationFromJson(destinationJson, map));
        });

        Set<Destination> unchosen = player.chooseDestinations(destinations);

        JsonArray unchosenJson = new JsonArray();
        unchosen.forEach((dest) -> { unchosenJson.add(DestinationJson.destinationToJson(dest)); });
        return gson.toJson(unchosenJson);
    }

    /**
     * Parses the given Json, and returns the result of calling takeTurn on the player to output stream.
     * @param args the JsonArray containing the arguments to call takeTurn.
     */
    public String handleTakeTurn(JsonArray args) {
        IPlayerGameState state = PlayerStateJson.playerStateFromJson(args.get(0));
        TurnAction action = player.takeTurn(state);
        JsonElement actionJson = TurnActionJson.turnActionToJSON(action);
        return gson.toJson(actionJson);
    }

    /**
     * Parses the given Json, and calls receiveCards on the player
     * @param args the JsonArray containing the arguments to call receiveCards.
     */
    public String handleReceiveCards(JsonArray args) {
        List<RailCard> cards = CardStarJson.cardsFromJson(args.get(0));
        player.receiveCards(cards);
        return "\"void\"";
    }

    /**
     * Parses the given Json, and calls winNotification on the player
     * @param args the JsonArray containing the arguments to call winNotification.
     */
    public String handleWinNotification(JsonArray args) {
        player.winNotification(args.get(0).getAsBoolean());
        return "\"void\"";
    }

    /**
     * Calls winNotification on the player and returns the resulting ITrainMap as Json
     * @param args the JsonArray containing the arguments to call tournamentStart.
     */
    public String handleTournamentStart(JsonArray args) {
        ITrainMap map = player.tournamentStart();
        JsonObject mapJson = MapJson.mapToJson(map);
        return gson.toJson(mapJson);
    }
    /**
     * Parses the given Json, and calls tournamentResult on the player
     * @param args the JsonArray containing the arguments to call tournamentResult.
     */
    public String handleTournamentResult(JsonArray args) {
        player.tournamentResult(args.get(0).getAsBoolean());
        return "\"void\"";
    }
}
