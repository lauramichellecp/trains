package map_view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.gson.JsonStreamParser;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import map.ITrainMap;
import org.junit.jupiter.api.Test;
import json.MapJson;

/**
 * Visualizations are tested by constructing the overall map image and testing various small
 * rectangles of it for expected items.
 *
 * <p>For a given small rectangle (subimage), use the {@link
 * TestMapRenderer#colorReport(BufferedImage)} and associated methods to generate a report that
 * shows the counts (in pixels) or relative frequency of colors in the sub-image.
 *
 * <p>Tests can then assert that this report for a particular subimage contains approximately the
 * expected amount of a given color.
 *
 * <p>For example, the subimage that surrounds the yellow circle of a city should contain
 * approximately 70-80% of pixels with the color of that city.
 *
 * <p>Colors in the report are exact, so these tests are not robust for slight variations in the
 * shades of colors.
 */
public class TestMapRenderer {
  private static final int EXPECTED_CITY_RADIUS = 5;
  private static final Color EXPECTED_BACKGROUND_COLOR = Color.BLACK;
  private static final Color EXPECTED_CITY_COLOR = Color.YELLOW;
  private static final Color EXPECTED_CITY_TEXT_COLOR = Color.MAGENTA;
  private static final Color EXPECTED_LENGTH_INDICATOR_COLOR = Color.BLACK;

  public static ITrainMap readAndParseTestMap(String jsonFileName) {
    try {
      return MapJson.mapFromJson(
          new JsonStreamParser(
                  new FileReader("Other/UnitTests/MapRenderedJsonInput/" + jsonFileName))
              .next());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void visualizeTrainMap(ITrainMap map) {
    BufferedImage mapView =
        new BufferedImage(
            map.getMapDimension().getWidth(),
            map.getMapDimension().getHeight(),
            BufferedImage.TYPE_INT_RGB);
    new MapRenderer().render(mapView.createGraphics(), map);
    renderImage(mapView);
  }

  @Test
  public void testMultipleConnections() {
    // Tests that the four connections are rendered in the approximately correct positions (tests
    // order with respect to perpendicular between two cities)
    ITrainMap map = readAndParseTestMap("bos-sea-tex-duplicate-connections.json");

    // render map (400 x 200) onto larger image to test bounds
    BufferedImage mapView = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = mapView.createGraphics();
    g.setColor(Color.RED);
    g.fillRect(0, 0, 800, 800);
    new MapRenderer().render(mapView.createGraphics(), map);

    // Render entire map:
    // renderImage(mapView);

    // This sub-image gets the four connections from Seattle to Boston about 2/3 way to Boston
    // It should contain all 4 connection colors to a small amount
    double expectedThreshold = .12;
    BufferedImage multipleConnections = mapView.getSubimage(250, 14, 60, 22);
    assertTrue(containsRelativeColorAmount(multipleConnections, Color.RED, expectedThreshold));
    assertTrue(containsRelativeColorAmount(multipleConnections, Color.BLUE, expectedThreshold));
    assertTrue(containsRelativeColorAmount(multipleConnections, Color.GREEN, expectedThreshold));
    assertTrue(containsRelativeColorAmount(multipleConnections, Color.WHITE, expectedThreshold));

    // Test the ordering (top to bottom) of connection colors
    // Each sub-image should contain much higher amounts of the particular color
    double singleColorExpectedThreshold = .65;
    BufferedImage redOnTop = multipleConnections.getSubimage(0, 0, 60, 4);
    assertTrue(containsRelativeColorAmount(redOnTop, Color.RED, singleColorExpectedThreshold));
    BufferedImage blueSecond = multipleConnections.getSubimage(0, 6, 60, 4);
    assertTrue(containsRelativeColorAmount(blueSecond, Color.BLUE, singleColorExpectedThreshold));
    BufferedImage greenThird = multipleConnections.getSubimage(0, 12, 60, 4);
    assertTrue(containsRelativeColorAmount(greenThird, Color.GREEN, singleColorExpectedThreshold));
    BufferedImage whiteBottom = multipleConnections.getSubimage(0, 18, 60, 4);
    assertTrue(containsRelativeColorAmount(whiteBottom, Color.WHITE, singleColorExpectedThreshold));
  }

  private void renderImage(BufferedImage img) {
    JFrame frame = new JFrame();
    frame.getContentPane().setLayout(new FlowLayout());
    frame.getContentPane().add(new JLabel(new ImageIcon(img)));
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(
        JFrame.EXIT_ON_CLOSE); // if you want the X button to close the app
    while (frame.isVisible()) {}
  }

  /** Test that an empty map produces a background of the proper size with the expected color */
  @Test
  public void testEmptyMap() {
    ITrainMap map = readAndParseTestMap("empty-map.json");

    // render map (400 x 200) onto larger image to test bounds
    BufferedImage mapView = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = mapView.createGraphics();
    g.setColor(Color.RED);
    g.fillRect(0, 0, 800, 800);
    new MapRenderer().render(mapView.createGraphics(), map);
    // renderImage(mapView);

    // within background should be black
    assertEquals(EXPECTED_BACKGROUND_COLOR, new Color(mapView.getRGB(0, 0)));
    assertEquals(EXPECTED_BACKGROUND_COLOR, new Color(mapView.getRGB(0, 199)));
    assertEquals(EXPECTED_BACKGROUND_COLOR, new Color(mapView.getRGB(200, 100)));
    assertEquals(EXPECTED_BACKGROUND_COLOR, new Color(mapView.getRGB(399, 0)));
    assertEquals(EXPECTED_BACKGROUND_COLOR, new Color(mapView.getRGB(399, 199)));

    // out of background should be red
    assertEquals(Color.RED, new Color(mapView.getRGB(0, 200)));
    assertEquals(Color.RED, new Color(mapView.getRGB(400, 0)));
  }

  @Test
  public void testIsolatedCities() {
    ITrainMap map = readAndParseTestMap("isolated-cities.json");

    // render map (400 x 200) onto larger image to test bounds
    BufferedImage mapView = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = mapView.createGraphics();
    g.setColor(Color.RED);
    g.fillRect(0, 0, 800, 800);
    new MapRenderer().render(mapView.createGraphics(), map);

    // Test of city in top-left corner, ensuring that it is a colored dot surrounded by the
    // background color
    assertTrue(
        containsRelativeColorAmount(mapView.getSubimage(0, 0, 5, 5), EXPECTED_CITY_COLOR, .75));
    assertFalse(
        containsRelativeColorAmount(mapView.getSubimage(5, 0, 10, 10), EXPECTED_CITY_COLOR, .01));
    assertFalse(
        containsRelativeColorAmount(mapView.getSubimage(0, 5, 5, 10), EXPECTED_CITY_COLOR, .1));

    // Basic test of city and city text in the middle of an image
    BufferedImage dodgeCityDot = mapView.getSubimage(196, 196, 8, 8);
    assertTrue(containsRelativeColorAmount(dodgeCityDot, EXPECTED_CITY_COLOR, .85));

    BufferedImage dodgeCityText = mapView.getSubimage(195, 185, 60, 10);
    assertTrue(containsRelativeColorAmount(dodgeCityText, EXPECTED_CITY_TEXT_COLOR, .2));

    // Behavior for a city that is in the corner - both the dot and the text appear off-screen for
    // the panel
    BufferedImage miamiDot = mapView.getSubimage(395, 395, 10, 10);
    assertTrue(containsRelativeColorAmount(miamiDot, EXPECTED_CITY_COLOR, .65));

    BufferedImage miamiDotOffScreen = mapView.getSubimage(401, 401, 5, 5);
    assertTrue(containsRelativeColorAmount(miamiDotOffScreen, EXPECTED_CITY_COLOR, .4));

    BufferedImage miamiText = mapView.getSubimage(395, 385, 30, 10);
    assertTrue(containsRelativeColorAmount(miamiText, EXPECTED_CITY_TEXT_COLOR, .2));

    BufferedImage miamiTextOffscreen = mapView.getSubimage(401, 385, 10, 10);
    assertTrue(containsRelativeColorAmount(miamiTextOffscreen, EXPECTED_CITY_TEXT_COLOR, .2));
  }

  @Test
  public void testSingleConnection() {
    ITrainMap map = readAndParseTestMap("single-connection.json");

    // render map (400 x 200) onto larger image to test bounds
    BufferedImage mapView = new BufferedImage(800, 800, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = mapView.createGraphics();
    g.setColor(Color.RED);
    g.fillRect(0, 0, 800, 800);
    new MapRenderer().render(mapView.createGraphics(), map);

    // The city dot should be rendered over the connection, so over the entire dot, very little
    // should be white (connection color)
    BufferedImage seattleDot = mapView.getSubimage(95, 95, 10, 10);
    assertFalse(containsRelativeColorAmount(seattleDot, Color.WHITE, .03));

    // Two parts of the connection, both near either end, and should contain no text or length
    // indicator
    BufferedImage connectionSegment = mapView.getSubimage(95, 115, 10, 10);
    assertTrue(containsRelativeColorAmount(connectionSegment, Color.WHITE, .25));
    assertFalse(containsRelativeColorAmount(connectionSegment, EXPECTED_CITY_COLOR, .001));
    assertFalse(containsRelativeColorAmount(connectionSegment, EXPECTED_CITY_TEXT_COLOR, .001));

    BufferedImage connectionSegment2 = mapView.getSubimage(125, 175, 10, 10);
    assertTrue(containsRelativeColorAmount(connectionSegment2, Color.WHITE, .25));
    assertFalse(containsRelativeColorAmount(connectionSegment2, EXPECTED_CITY_COLOR, .001));
    assertFalse(containsRelativeColorAmount(connectionSegment2, EXPECTED_CITY_TEXT_COLOR, .001));

    // For a 10 x 10 image over the length indicator, the amount of white decreases from >25% to
    // below <20% because of black dot length indicator
    BufferedImage lengthIndicator1 = mapView.getSubimage(103, 133, 10, 10);
    assertFalse(containsRelativeColorAmount(lengthIndicator1, Color.WHITE, .20));
    assertTrue(containsRelativeColorAmount(lengthIndicator1, Color.WHITE, .15));
    assertFalse(containsRelativeColorAmount(lengthIndicator1, EXPECTED_CITY_COLOR, .001));
    assertFalse(containsRelativeColorAmount(lengthIndicator1, EXPECTED_CITY_TEXT_COLOR, .001));

    // Tests the same for the second length indicator (2 indicators on a 3 length connection)
    BufferedImage lengthIndicator2 = mapView.getSubimage(120, 165, 10, 10);
    assertFalse(containsRelativeColorAmount(lengthIndicator2, Color.WHITE, .20));
    assertTrue(containsRelativeColorAmount(lengthIndicator2, Color.WHITE, .15));
    assertFalse(containsRelativeColorAmount(lengthIndicator2, EXPECTED_CITY_COLOR, .001));
    assertFalse(containsRelativeColorAmount(lengthIndicator2, EXPECTED_CITY_TEXT_COLOR, .001));
  }

  /**
   * Determines the number of pixels of the colors that appear in the entirety of the image.
   *
   * @param img
   * @return
   */
  private static Map<Color, Integer> colorReport(BufferedImage img) {
    Map<Color, Integer> result = new HashMap<>();
    for (int x = 0; x < img.getWidth(); x += 1) {
      for (int y = 0; y < img.getHeight(); y += 1) {
        Color pixelColor = new Color(img.getRGB(x, y));
        if (!result.containsKey(pixelColor)) {
          result.put(pixelColor, 0);
        } else {
          result.put(pixelColor, result.get(pixelColor) + 1);
        }
      }
    }
    return result;
  }

  /**
   * Like colorReport, but calculates relative frequency [0, 1] for each color
   *
   * @param colorReport
   * @return
   */
  private static Map<Color, Double> relativeColorReport(Map<Color, Integer> colorReport) {
    Map<Color, Double> result = new HashMap<>();
    double totalPixels = 0.0;
    for (Integer pixelsOfColor : colorReport.values()) {
      totalPixels += pixelsOfColor;
    }

    for (Entry<Color, Integer> oneColorReport : colorReport.entrySet()) {
      result.put(oneColorReport.getKey(), oneColorReport.getValue() / totalPixels);
    }
    return result;
  }

  /**
   * Relative color report for buffered image
   *
   * @param img
   * @return
   */
  private static Map<Color, Double> relativeColorReport(BufferedImage img) {
    return relativeColorReport(colorReport(img));
  }

  /**
   * Returns whether the image contains at least the given threshold of relative frequency for the
   * given color
   *
   * @param img
   * @param color
   * @param threshold
   * @return
   */
  private static boolean containsRelativeColorAmount(
      BufferedImage img, Color color, double threshold) {
    Map<Color, Double> report = relativeColorReport(img);
    return report.containsKey(color) && report.get(color) >= threshold;
  }
}
