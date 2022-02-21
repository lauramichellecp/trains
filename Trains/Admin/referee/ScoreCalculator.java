package referee;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import map.IRailConnection;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import utils.ScoreGraphUtils;

/**
 * To calculate scores of players in Trains.
 */
public class ScoreCalculator {

    private static final int POINTS_PER_DESTINATION = 10;
    private static final int POINTS_PER_FAILED_DESTINATION = -10;
    private static final int POINTS_PER_SEGMENT = 1;
    private static final int POINTS_FOR_LONGEST_PATH = 20;

    /**
     * Calculates the scores of each player, accounting for each segment, destinations
     * completed/failed, and longest path ownership.
     *
     * @param playerDataInOrder the player data in turn order.
     * @return a List of Integer of the scores of the players in the same order as the given list.
     */
    public static List<Integer> scorePlayers(List<IPlayerData> playerDataInOrder) {
        List<Integer> scoresInTurnOrder = new ArrayList<>();

        // Construct graph of player's occupied connections
        List<Graph<String, DefaultWeightedEdge>> playerConnectionGraphs =
            playerDataInOrder.stream()
                .map(
                    (player) ->
                        ScoreGraphUtils.occupiedConnectionsToGraph(player.getOwnedConnections()))
                .collect(Collectors.toList());

        // Calculate players with the longest path
        Set<Integer> playersWithLongestPath =
            ScoreGraphUtils.calculatePlayersWithLongestPath(playerConnectionGraphs);

        // For each player, calculate individual score using above info
        for (int index = 0; index < playerConnectionGraphs.size(); index += 1) {
            scoresInTurnOrder.add(
                calculatePlayerScore(
                    playerDataInOrder.get(index),
                    playerConnectionGraphs.get(index),
                    playersWithLongestPath.contains(index)));
        }

        return scoresInTurnOrder;
    }

    /**
     * Computers the score for the given player accounting for whether they own a longest path.
     *
     * @param playerData            the data for the player.
     * @param playerConnectionGraph the graph representing the player's best occupied connections
     *                              (to avoid recomputing it).
     * @param hasLongestPath        whether the player owns a longest path.
     * @return the score of the player as an integer.
     */
    private static int calculatePlayerScore(
        IPlayerData playerData,
        Graph<String, DefaultWeightedEdge> playerConnectionGraph,
        boolean hasLongestPath) {
        int totalNumSegments = calculateTotalNumSegments(playerData);

        int numDestinationsConnected =
            ScoreGraphUtils.calculateNumDestinationsConnected(
                playerConnectionGraph, playerData.getDestinations());
        return assignPoints(
            totalNumSegments,
            numDestinationsConnected,
            playerData.getDestinations().size(),
            hasLongestPath);
    }

    /**
     * Determines the total number of segments among all owned connections.
     *
     * @param playerData the data for the player containing the owned connections.
     * @return the integer total of the lengths of all owned connections.
     */
    private static int calculateTotalNumSegments(IPlayerData playerData) {
        return playerData.getOwnedConnections().stream().mapToInt(IRailConnection::getLength).sum();
    }

    /**
     * Assigns points to each piece of information related to scoring and returns the total score.
     *
     * @param totalNumSegments         the total number of rails placed by the player.
     * @param numDestinationsCompleted the total number of destinations completed by the player.
     * @param numDestinationsTotal     the total number of destinations chosen by the player.
     * @param hasLongestPath           whether the player has a longest path.
     * @return the player's total score as an integer.
     */
    private static int assignPoints(
        int totalNumSegments,
        int numDestinationsCompleted,
        int numDestinationsTotal,
        boolean hasLongestPath) {

        int ptsFromSegments = totalNumSegments * POINTS_PER_SEGMENT;
        // Gain points for successful destinations, lose points for failed destinations
        int ptsFromDestinations =
            (POINTS_PER_DESTINATION * numDestinationsCompleted)
                + (POINTS_PER_FAILED_DESTINATION * (numDestinationsTotal
                - numDestinationsCompleted));
        int ptsFromLongest = hasLongestPath ? POINTS_FOR_LONGEST_PATH : 0;
        return ptsFromSegments + ptsFromDestinations + ptsFromLongest;
    }
}
