package map_view;

import java.awt.Graphics2D;
import map.ITrainMap;
import map.MapDimensions;

/**
 * To represent a renderer capable of visually representing a train map via two-dimensional graphics.
 */
public interface IMapRenderer {

    /**
     * Draws a representation of the given train map on the given graphics.
     * @param g the graphics on which to draw the train map.
     * @param map the train map to visually represent.
     */
    void render(Graphics2D g, ITrainMap map);
}
