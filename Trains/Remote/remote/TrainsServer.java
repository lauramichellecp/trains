package remote;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import player.IPlayer;
import player.ProxyPlayer;

/**
 * A class representing a server for Trains.
 */
public class TrainsServer 
{
    private static final int SIGNUP_TIMEOUT_DEFAULT = 20000;
    private static final int NAME_TIMEOUT_DEFAULT = 3000;
    private static final int FIRST_ROUND_MIN = 5;
    private static final int RELAXED_ROUND_MIN = 2;
    private static final int ROUND_MAX = 50;

    private ServerSocket server;
    private ArrayList<Socket> clients;

    private ArrayList<IPlayer> players;
    private Map<IPlayer, String> nameLookup;
    private List<Future<Optional<ProxyPlayer>>> waitingForNames;
    private ExecutorService executorNames; //Executor for waiting for names
    private ExecutorService executor; //Executor for allowing signups
    private Future acceptSocketConncetions;

    private int timeout;
    private int nameTimeout;

    /**
     * Constructs a TrainsServer object that listens for a client to connect to the specified port and accepts the connection
     * @param signUpTime The time to wait for people to signup in milliseconds
     * @param nameTimeout The time to wait for each player to supply a name in milliseconds
     */
    public TrainsServer(int port, int signUpTime, int nameTimeout) {
        try {
            server = new ServerSocket(port);
            server.setSoTimeout(10);   
            if(server.isClosed()) {
                throw new SocketException("Couldn't open server socket");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        clients = new ArrayList<Socket>();
        players = new ArrayList<IPlayer>();
        waitingForNames = new ArrayList<Future<Optional<ProxyPlayer>>>();
        executorNames = Executors.newCachedThreadPool();

        this.nameTimeout = nameTimeout;
        this.timeout = signUpTime;
        this.nameLookup = new HashMap<IPlayer, String>();
    };

    /**
     * Construct a TrainsServer object that listens for a client to connect to the specified port and accepts the connection.
     * Uses default signup timeout of 20s and name timeout of 3 seconds
     */
    public TrainsServer(int port) {
        this(port, SIGNUP_TIMEOUT_DEFAULT, NAME_TIMEOUT_DEFAULT);
    }

    /**
     * Listens for players signing up for a tournament and returns a ProxyPlayer with the given socket.
     * @param playersSocket a Socket for the interested player.
     * @return the ProxyPlayer if successful (the given name is valid and there is no timeout)
     */
    private Optional<ProxyPlayer> waitForName(Socket playersSocket) {
        try {
            playersSocket.setSoTimeout(nameTimeout);
            Scanner nameInputStream = new Scanner(new InputStreamReader(playersSocket.getInputStream())).useDelimiter("\n");
            String name = nameInputStream.next();
            if(Pattern.matches("\"[A-Za-z]{1,50}\"", name)) {
                
                ProxyPlayer player = new ProxyPlayer(playersSocket);
                nameLookup.put(player, name.substring(1, name.length() - 1));
                return Optional.of(player);
            } else {
                //INVALID NAME
                playersSocket.close();
                clients.remove(playersSocket);        
            }
        } catch (NoSuchElementException timeout) {
            try {
                playersSocket.close();
            } catch (IOException io) {
                //IF PLAYER SOCKET CLOSE FAILS CONTINUE
            }
            clients.remove(playersSocket);
        } catch (SocketException s) {
            //Server was closed by shutdown (or something else unrecoverable)
        } catch (IOException io) {
            io.printStackTrace();
            System.exit(1);
        }
        return Optional.empty();

    }

    /**
     * Accepts all waiting players and adds them to the list of clients (if their name is valid) until there's a timeout
     * or if the client size reaches the round max.
     * @return true if successful
     */
    private boolean acceptAllWaiting() {
        Callable<Optional<ProxyPlayer>>  waitNameTask = null;
        try {
            do {
                final Socket newPotentialPlayer = server.accept();
                if(newPotentialPlayer != null) {                    
                    clients.add(newPotentialPlayer);
                    waitNameTask = () -> { return waitForName(newPotentialPlayer);};
                    waitingForNames.add(executorNames.submit(waitNameTask));
                }
            } while(clients.size() < ROUND_MAX);
        } catch (SocketException e) {
            //Server was closed by shutdown (or something else unrecoverable)
            return false;
        } catch (SocketTimeoutException e) {
        } catch (IOException e) {
            //FATAL SOCKET EXCEPTION
            e.printStackTrace();
            System.exit(1);
        } 
        return true;
    }

    /**
     * Waits for players to sign up while the size is less than the maximum amount and the thread
     * isn't interrupted by a timeout.
     */
    private void allowSignUps() {
        while(players.size() < ROUND_MAX && !Thread.currentThread().isInterrupted()) {
            if(!acceptAllWaiting()) {
                return;
            }
            ArrayList<Future<Optional<ProxyPlayer>>> newWaitingForNames = new ArrayList<Future<Optional<ProxyPlayer>>>(waitingForNames);
            for(Future<Optional<ProxyPlayer>> waitingOnName : waitingForNames) {
                if(Thread.currentThread().isInterrupted()) {
                    break;
                }
                if(waitingOnName.isDone()) {
                    try {
                        Optional<ProxyPlayer> playerMaybe = waitingOnName.get();
                        if(playerMaybe.isPresent() && !Thread.currentThread().isInterrupted()) {
                            players.add(playerMaybe.get());
                        }
                    } catch(InterruptedException | ExecutionException fatalException) {
                        //WAITING FOR NAME CRASH
                        fatalException.printStackTrace();
                        System.exit(1);
                    }
                    newWaitingForNames.remove(waitingOnName);
                    if(players.size() == ROUND_MAX) {
                        break;
                    }
                }
            }
            waitingForNames = newWaitingForNames;
        }
    }
    
    /**
     * Start accepting signups, and if enough do signup, return a list of players.
     * If it fails it returns an empty list
     * @return The list of players who signed up
     */
    public ArrayList<IPlayer> startSignup() {
        int signUpRound = 0;
        executor = Executors.newSingleThreadExecutor();
        int minPlayers = FIRST_ROUND_MIN;

        do {
            signUpRound += 1;
            acceptSocketConncetions = executor.submit(this::allowSignUps);
            
            try {
                acceptSocketConncetions.get(timeout, TimeUnit.MILLISECONDS);

                //FULL ROUND
                shutdownExecutors();
                return new ArrayList<IPlayer>(players);
            } catch(TimeoutException timeout) {
                //TIMEOUT
                if(players.size() >= minPlayers) {
                    //ENOUGH PLAYERS TO RUN
                    shutdownExecutors();
                    return new ArrayList<IPlayer>(players);
                } else {
                    //NOT ENOUGH WAIT ONE MORE TIMEOUT
                    acceptSocketConncetions.cancel(true);
                    minPlayers = RELAXED_ROUND_MIN;
                }
            } catch(InterruptedException | ExecutionException fatalException) {
                //SIGNUP SERVER CRASH
                fatalException.printStackTrace();
                System.exit(1);
            }
        } while(signUpRound < 2);

        //DONE WITH SIGNUP, NOT ENOUGH PLAYERS, SHUT DOWN THREADS
        shutdownExecutors();

        //Should return default [ [], [] ] tourney result
        return new ArrayList<IPlayer>();
    }

    /**
     * Shutdowns the executors.
     */
    private void shutdownExecutors() {
        waitingForNames.forEach((f) -> {
            f.cancel(true);
        });
        acceptSocketConncetions.cancel(true);
        executor.shutdownNow();
        executorNames.shutdownNow();
    }

    /**
     * Return the name lookup table for converting from IPlayer references to their names
     * @return The lookup table
     */
    public Map<IPlayer, String> getLookupTable() {
        return nameLookup;
    }

    /**
     * Closes the server's socket
     */
    public void shutdown() {
        try {
            server.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
