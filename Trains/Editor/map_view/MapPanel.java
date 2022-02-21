package map_view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import map.ITrainMap;
import map.MapDimensions;

/**
 * Represents the java Swing panel that will contain the visualization of a game map.
 */
public class MapPanel extends JPanel {
    private final ITrainMap map;
    private final IMapRenderer mapRenderer;

    /**
     * Creates a panel that will draw a representation of the given game map.
     *
     * @param map The map that should be drawn in this panel.
     */
    public MapPanel(ITrainMap map) {
        this.map = map;
        this.mapRenderer = new MapRenderer();

        MapDimensions dimensions = map.getMapDimension();
        this.setPreferredSize(new Dimension(dimensions.getWidth(), dimensions.getHeight()));
    }

    /**
     * Renders a visualization of the game map in this panel.
     *
     * @param g The graphics object used to draw the game map.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.mapRenderer.render((Graphics2D) g, map);
    }

    /**
     * Overrides getPreferredSize so that the panel prefers to be large enough to show the whole map.
     *
     * @return The minimum dimensions that this panel prefers
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.map.getMapDimension().getWidth(), this.map.getMapDimension().getHeight());
    }
}
