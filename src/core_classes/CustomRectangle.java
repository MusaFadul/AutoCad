package core_classes;

import java.awt.Color;
import java.awt.Rectangle;

/**
 * Created by isaac on 27/11/17.
 */
public class CustomRectangle extends Feature {

    private Rectangle geometry;

    /**
     * Constructor for a CustomRectangle object.  Geometry is stored as a java.awt native Rectangle object alongside its
     * parent's properties (id, color, weight)
     * @return null
     */
    public CustomRectangle(Rectangle rectangle, int id, Color color, int weight) {
        super.Feature(id, color, weight);
        this.geometry = rectangle;
    }

    /**
     * Getter method for the geometry.  Returns a Rectangle object.
     * @return Retangle
     */
    public Rectangle getGeometry() {
        return this.geometry;
    }

}
