/**
 * 
 */
package geometry;

import core_classes.Feature;
import java.awt.geom.Line2D;

/**
 * @author Isaac
 * Class for representing polylines in our system.  Inherits from Feature.
 */
public class PolylineItem extends Feature {

    Line2D geometry;

    /**
     * Constructor for PolylineItem feature.
     * @param id Id for this feature
     * @param polyline Geometry for this feature, in the form of a Line2D object.
     */
    public PolylineItem(int id, Line2D polyline) {

        super(id);
        this.geometry = polyline;

    }

    /**
     * Getter method for the PolylineItem's geometry object.
     * @return Line2D
     */
    public Line2D getGeometry() {
        return geometry;
    }

    /**
     * Setter method for the PolylineItem's geometry object.
     * @param polyline Line2D to set as the geometry.
     */
    public void setGeometry(Line2D polyline) {
        this.geometry = polyline;
    }

}
