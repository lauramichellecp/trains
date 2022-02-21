package harnesses;

import json.MapJson;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonStreamParser;
import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import map.ITrainMap;
import map_view.MapPanel;

/**
 * Performs an integration test on the visualization of a map for a game of trains by consuming a
 * specification for a Trains game map then displaying that map in a new window.
 *
 * The specifications for the JSON input format can be found at:
 *      https://www.ccs.neu.edu/home/matthias/4500-f21/3.html
 *
 * Example input:
 *      {
 *          "width" : 800,
 *          "height": 800,
 *          "cities": [["Seattle", [0, 0]], ["Boston", [800, 50]], ["Texas", [500, 800]]],
 *          "connections": {"Boston": {"Seattle": {"red": 3},
 *                                      "Texas": {"green": 5}},
 *                          "Seattle": {"Texas": {"blue": 4}}}
 *      }
 */
public class XVisualize {

    private static final int SECONDS_OPEN = 10;
    private static final int MILLIS_OPEN = (int) TimeUnit.SECONDS.toMillis(SECONDS_OPEN);

    /**
     * Performs the integration test. Reading the JSON from stdin, and displaying it for
     * SECONDS_OPEN seconds.
     *
     * @param args Ignored.
     */
    public static void main(String[] args) {
        VisualizeJSONMap(new InputStreamReader(System.in));
    }

    /**
     * Performs the integration test consuming JSON input from the given reader, and displaying the
     * game map in a new window.
     *
     * @param input A stream of one JSON value representing the game map for a game of Trains
     */
    private static void VisualizeJSONMap(Reader input) {
        JsonStreamParser parser = new JsonStreamParser(input);
        try (input) {
            JsonElement mapSpecification = parser.next();
            ITrainMap map = MapJson.mapFromJson(mapSpecification);

            JFrame mapFrame = BuildMapFrame(map);
            displayFrame(mapFrame, MILLIS_OPEN);
        } catch (JsonIOException | IOException ignored) {
        }
    }

    /**
     * Builds a JFrame that contains a visualization of the map for a game of Trains, but does not
     * make it visible.
     *
     * @param map The representation of the map in a game of Trains
     */
    public static JFrame BuildMapFrame(ITrainMap map) {
        JPanel panel = new MapPanel(map);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        return frame;
    }

    /**
     * Displays a given JFrame in a new window, and closes it automatically after millisOpen
     * milliseconds. This will exit the program when the display closes.
     *
     * @param frame The frame to display on screen.
     * @param millisOpen The number of milliseconds to leave the window open.
     */
    public static void displayFrame(JFrame frame, int millisOpen) {
        frame.setVisible(true);

        Timer timer = new Timer(millisOpen,
            e -> {
                frame.setVisible(false);
                System.exit(0);
            });
        timer.start();
    }
}
