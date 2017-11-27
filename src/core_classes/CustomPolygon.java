package core_classes;

import java.awt.Color;
import java.awt.Polygon;

/**
 * Created by isaac on 27/11/17.
 */
public class CustomPolygon extends Feature {

    private Polygon geometry;

    /**
     * Constructor for a CustomPolygon object.  Geometry is stored as a java.awt native Polygon object alongside its
     * parent's properties (id, color, weight).
     * @return null
     */
    public CustomPolygon(Polygon polygon, int id, Color color, int weight) {
        super.Feature(id, color, weight);
        this.geometry = polygon;
    }

    /**
     * Getter method for the geometry.  Returns a Polygon object.
     * NOTE FOR ISAAC: to get the coordinates of the points, use myPolygon.getGeometry.xpoints[]
     * & myPolygon.getGeometry.ypoints[]
     * @return Polygon
     */
    public Polygon getGeometry() {
        return this.geometry;
    }

}
