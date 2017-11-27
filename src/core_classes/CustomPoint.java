package core_classes;

import java.awt.Color;
import java.awt.Point;

/**
 * Created by isaac on 27/11/17.
 */
public class CustomPoint extends Feature {

    private Point geometry;

    /**
     * Constructor for a CustomPoint object.  Geometry is stored as a java.awt native Point object alongside its
     * parent's properties (id, color, weight).
     * @return null
     */
    public CustomPoint(Point point, int id, Color color, int weight) {
        super.Feature(id, color, weight);
        this.geometry = point;
    }

    /**
     * Getter method for the geometry.  Returns a Point object.
     * @return Point
     */
    public Point getGeometry() {
        return this.geometry;
    }

}
