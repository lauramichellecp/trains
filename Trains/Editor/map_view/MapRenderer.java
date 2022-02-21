package map_view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import map.ICity;
import map.IRailConnection;
import map.ITrainMap;
import map.MapDimensions;
import map.RailColor;
import utils.OrderedPair;
import utils.PairUtils;
import utils.UnorderedPair;
import org.apache.commons.math3.geometry.euclidean.twod.*;

//TODO: Whoever is drawing trains onto the map is gonna need access to private methods in this class
// to draw in the same place
/**
 * A basic map visualizer that renders cities as circles with their name in text, and connections as
 * straight, colored lines with different number of segments depending on length.
 */
public class MapRenderer implements IMapRenderer {
  private static final int CITY_RADIUS = 5;
  private static final Color BACKGROUND_COLOR = Color.BLACK;
  private static final Color CITY_COLOR = Color.YELLOW;
  private static final Color CITY_TEXT_COLOR = Color.MAGENTA;
  private static final Color LENGTH_INDICATOR_COLOR = Color.BLACK;
  private static final int RAIL_CONNECTION_WIDTH = 3;
  private static final int LENGTH_INDICATOR_RADIUS = 3;

  /**
   * Renders cities and rail connections of the map on top of a black rectangle background of the
   * map's dimensions. The stored color and stroke of the graphics are unchanged.
   *
   * @param g the graphics on which to draw the train map.
   * @param map the train map to visually represent.
   */
  public void render(Graphics2D g, ITrainMap map) {
    Color storedColor = g.getColor();
    Stroke storedStroke = g.getStroke();
    this.drawBackground(g, map.getMapDimension());
    this.drawRailConnections(g, map);
    this.drawCities(g, map);
    g.setColor(storedColor);
    g.setStroke(storedStroke);
  }

  /**
   * Draws a black background rectangle onto the graphics bounded by (0,0) and (WIDTH, HEIGHT)
   * corresponding to the given dimensions.
   *
   * @param g the graphics on which to render the background.
   * @param dimensions the dimensions of the map.
   */
  private void drawBackground(Graphics2D g, MapDimensions dimensions) {
    g.setColor(BACKGROUND_COLOR);
    g.fillRect(0, 0, dimensions.getWidth(), dimensions.getHeight());
  }

  /**
   * Draws all of the cities of the given map onto the graphics. Cities are rendered as yellow
   * circles with their name as magenta text to the north-east of the circle.
   *
   * @param g the graphics on which to draw.
   * @param map the map containing information for all of the cities to draw.
   */
  private void drawCities(Graphics2D g, ITrainMap map) {
    MapDimensions dimensions = map.getMapDimension();
    for (ICity city : map.getCities()) {
      OrderedPair<Integer> location =
          PairUtils.scalePair(dimensions, city.getRelativePosition());
      // Draw city circle
      g.setColor(CITY_COLOR);
      drawCircle(g, location, CITY_RADIUS);

      // Draw city name text
      g.setColor(CITY_TEXT_COLOR);
      g.drawString(city.getName(), location.first - CITY_RADIUS, location.second - CITY_RADIUS);
    }
  }

  /**
   * Renders a filled circle onto the graphics with the given center and radius. All other
   * parameters (such as color) are according to the given graphics object.
   *
   * @param g the graphics on which to draw the circle.
   * @param center the coordinates of the circle's center.
   * @param radius the circle's radius.
   */
  private static void drawCircle(Graphics2D g, OrderedPair<Integer> center, int radius) {
    g.fillOval(center.first - radius, center.second - radius, 2 * radius, 2 * radius);
  }

  /**
   * Renders all of the rail connections in the map onto the given graphics. Rail connections are
   * rendered as a 3-pixel wide line of the specified color between the two cities, though the lines
   * do not start and end directly at the city circle.
   *
   * <p>Length (L) is indicated by drawing black circles onto the line to give it the appearance of
   * L distinct segments. Multiple connections between two cities are rendered by rendering multiple
   * parallel lines (with length indication). The order and position of these lines are
   * predetermined by the color of the line.
   *
   * @param g the graphics on which to render connections.
   * @param map providing the data for rail connections to render.
   */
  private void drawRailConnections(Graphics2D g, ITrainMap map) {
    MapDimensions dimensions = map.getMapDimension();
    // For each connection, calculate connection endpoints, draw rail segment, and add length
    // indicators.
    for (IRailConnection railConnection : map.getRailConnections()) {
      OrderedPair<Vector2D> lineEndpoints =
          ConnectionRenderer.calculateLineEndpoints(railConnection, dimensions);
      Vector2D lineStart = lineEndpoints.first;
      Vector2D lineEnd = lineEndpoints.second;

      ConnectionRenderer.drawRailConnectionLine(g, lineStart, lineEnd, railConnection.getColor());
      ConnectionRenderer.drawLengthIndicators(g, lineStart, lineEnd, railConnection.getLength());
    }
  }

  /**
   * A helper class containing all the functionality for rendering rail connections. Used for
   * organizational purposes.
   */
  private static class ConnectionRenderer {

    /**
     * Draws a line connecting the given start and end points of the given color. Note: Vector
     * coordinates are floored to the int value for drawing the line.
     *
     * @param g the graphics to draw on.
     * @param lineStart vector coordinates of one endpoint.
     * @param lineEnd vector coordinates of the other endpoint.
     * @param railColor the rail color of the connection to draw.
     */
    private static void drawRailConnectionLine(
        Graphics2D g, Vector2D lineStart, Vector2D lineEnd, RailColor railColor) {
      g.setColor(ConnectionRenderer.convertRailColor(railColor));
      g.setStroke(new BasicStroke(RAIL_CONNECTION_WIDTH));
      g.drawLine(
          (int) lineStart.getX(),
          (int) lineStart.getY(),
          (int) lineEnd.getX(),
          (int) lineEnd.getY());
    }

    /**
     * Returns the java.awt.Color corresponding to the given RailColor.
     *
     * @param rc the RailColor.
     * @return the directly corresponding java.awt.Color corresponding, or Color.CYAN for default.
     */
    private static Color convertRailColor(RailColor rc) {
      switch (rc) {
        case RED:
          return Color.RED;
        case BLUE:
          return Color.BLUE;
        case GREEN:
          return Color.GREEN;
        case WHITE:
          return Color.WHITE;
        default:
          return Color.CYAN;
      }
    }

    /**
     * Renders indicators of RailConnection length onto a line with given start and end points.
     *
     * @param g the graphics on which to draw indicators.
     * @param lineStart the coordinates of the start of the line.
     * @param lineEnd the coordinates of the end of the line.
     * @param length the length from IRailConnection, representing the number of segments the
     *     original line will appear to be broken into.
     */
    private static void drawLengthIndicators(
        Graphics2D g, Vector2D lineStart, Vector2D lineEnd, int length) {
      // Determine positions of each length indicator and draw a black circle at each point
      List<Vector2D> lengthIndicators = lengthIndicatorPositions(lineStart, lineEnd, length);
      g.setColor(LENGTH_INDICATOR_COLOR);
      for (Vector2D lengthIndicatorPoint : lengthIndicators) {
        drawCircle(
            g, PairUtils.vectToOrderedPair(lengthIndicatorPoint), LENGTH_INDICATOR_RADIUS);
      }
    }

    /**
     * Calculates the coordinates on the line segment specified by start and end such that the line
     * is split into railConnectionLength number of equal-sized segments. These are the positions of
     * black circles to be drawn to indicate the RailConnection length of a RailConnection.
     *
     * @param start the coordinates of the start of the line.
     * @param end the coordinates of the end of the line.
     * @param railConnectionLength the RailConnection length, equal to the number of equal-sized
     *     segments to partition the line into.
     * @return a list of Vector2D coordinates of the points separating the equal-length
     *     sub-segments, from start to end.
     */
    private static List<Vector2D> lengthIndicatorPositions(
        Vector2D start, Vector2D end, int railConnectionLength) {
      // Calculate the displacement between each length indicator
      Vector2D displacement = end.subtract(start);
      Vector2D scaledDisplacement =
          displacement.scalarMultiply(1.0 / (double) railConnectionLength);

      // Construct points along line for each length indicator. Start at '1' and end before given
      // length to avoid returning length indicators at line endpoints, only between.
      List<Vector2D> result = new ArrayList<>();
      for (int segmentNumber = 1; segmentNumber < railConnectionLength; segmentNumber += 1) {
        result.add(start.add(segmentNumber, scaledDisplacement));
      }
      return result;
    }

    /**
     * Calculated the coordinates of the line endpoints for the given rail connection. Adds a
     * color-based transverse displacement to ensure that multiple connections between cities can be
     * clearly rendered.
     *
     * @param railConnection the rail connection to calculate endpoints for.
     * @param dimensions the dimensions of the map, to scale rail connection relative coordinates.
     * @return the two coordinates of the rail connection start and end points.
     */
    private static OrderedPair<Vector2D> calculateLineEndpoints(
        IRailConnection railConnection, MapDimensions dimensions) {
      // Get the absolute positions of end cities
      UnorderedPair<ICity> cities = railConnection.getCities();
      OrderedPair<Integer> startLocation =
          PairUtils.scalePair(dimensions, cities.first.getRelativePosition());
      OrderedPair<Integer> endLocation =
          PairUtils.scalePair(dimensions, cities.second.getRelativePosition());

      return lineEndpointsForColor(
          PairUtils.orderedPairToVect(startLocation),
          PairUtils.orderedPairToVect(endLocation),
          railConnection.getColor());
    }

    /**
     * Given the coordinates of the start and end cities, calculates the rail connection endpoints.
     * Uses the color to add a transverse displacement to make rendering multiple connections
     * between the same city clear. Note: Another term for "transverse displacement" is
     * "perpendicular offset", perpendicular with respect to displacement between two cities.
     *
     * <p>For a start city in the west and an end in the east, the four connections would be ordered
     * as follows (where top = North, left = West, right = East, bottom = South):
     *
     * <p>RED rail connection
     *
     * <p>BLUE rail connection
     *
     * <p>Imaginary line connecting two cities
     *
     * <p>GREEN rail connection
     *
     * <p>WHITE rail connection
     *
     * @param startLoc the coordinates of the start city.
     * @param endLoc the coordinates of the end city.
     * @param color the color of the rail connection to calculate transverse displacement.
     * @return the coordinates of the actual rail line endpoints accounting for transverse
     *     displacement.
     */
    private static OrderedPair<Vector2D> lineEndpointsForColor(
        Vector2D startLoc, Vector2D endLoc, RailColor color) {

      Vector2D displacement = endLoc.subtract(startLoc);
      // Perpendicular calculated as 90 degree rotation anti-clockwise
      Vector2D perpendicular = new Vector2D(-1 * displacement.getY(), displacement.getX());
      // Use rail width as base units for transverse displacement
      Vector2D scaledPerpendicular =
          perpendicular.normalize().scalarMultiply(RAIL_CONNECTION_WIDTH);
      // Total transverse displacement based on color
      Vector2D perpendicularOffset =
          scaledPerpendicular.scalarMultiply(railColorToOffsetScaling(color));
      // Displace initial start and end coordinates
      Vector2D lineStart = startLoc.add(perpendicularOffset);
      Vector2D lineEnd = endLoc.add(perpendicularOffset);
      return new OrderedPair<>(lineStart, lineEnd);
    }

    /**
     * Calculates the number of unit perpendicular offsets to offset a rail of the given color by
     * from the standard line connecting two cities. The number returned indicates the number of
     * rail connection widths the rail should be displaced transversely.
     *
     * @param rc the rail color.
     * @return an integer representing the whole number of rail connection widths to displace
     *     transversely. Negative indicates displacement in the opposite direction.
     */
    private static int railColorToOffsetScaling(RailColor rc) {
      switch (rc) {
        case RED:
          return 3;
        case BLUE:
          return 1;
        case GREEN:
          return -1;
        case WHITE:
          return -3;
        default:
          return 0;
      }
    }
  }
}
