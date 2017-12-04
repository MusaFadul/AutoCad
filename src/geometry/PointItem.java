package geometry;
import core_classes.Feature;
import java.awt.Point;

/**
 * @author Isaac
 * Class for representing points in our system.  Inherits from Feature.
 */
public class PointItem extends Feature {
    
    Point geometry;

    /**
     * Constructor for PointItem feature.
     * @param id Id for this feature
     * @param point Geometry for this feature, in the form of a Point object.
     */
    public PointItem(int id, Point point) {

        super(id);
        this.geometry = point;

    }

    /**
     * Getter method for the PointItem's geometry object.
     * @return Point
     */
    public Point getGeometry() {
        return this.geometry;
    }

    /**
     * Setter method for the PointItem's geometry object.
     * @param point Point object to set as the PointItem's geomtetry.
     */
    public void setGeometry(Point point) {
        this.geometry = point;
    }

}