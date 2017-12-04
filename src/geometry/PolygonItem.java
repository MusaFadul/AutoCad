/**
 * 
 */
package geometry;

import core_classes.Feature;
import java.awt.Polygon;

/**
 * @author Isaac
 * Class for representing polygons in our system.  Inherits from Feature.
 */
public class PolygonItem extends Feature {

    Polygon geometry;

    /**
     * Constructor for PolygonItem feature.
     * @param id Id for this feature
     * @param polygon Geometry for this feature, in the form of a Polygon object.
     */
    public PolygonItem(int id, Polygon polygon) {

        super(id);
        this.geometry = polygon;

    }

    /**
     * Getter method for the PolylineItem's geometry object.
     * @return Polygon
     */
    public Polygon getGeometry() {
        return geometry;
    }

    /**
     * Setter method for the PolylineItem's geometry object.
     * @param polygon Polygon to set as the geometry.
     */
    public void setGeometry(Polygon polygon) {
        this.geometry = polygon;
    }

}
