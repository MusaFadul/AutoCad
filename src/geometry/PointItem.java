package geometry;
import core_classes.Feature;
import java.awt.geom.Point2D;

/**
 * @author Isaac
 * Class for representing points in our system.  Inherits from Feature.
 */
public class PointItem extends Feature {
    
    private Point2D geometry;

    /**
     * Constructor for PointItem feature.
     * @param id Id for this feature
     * @param point Geometry for this feature, in the form of a Point object.
     */
    public PointItem(int id, Point2D point) {

        super(id);
        this.geometry = point;

    }

    /**
     * Getter method for the PointItem's geometry object.
     * @return Point
     */
    public Point2D getGeometry() {
        return this.geometry;
    }

    /**
     * Setter method for the PointItem's geometry object.
     * @param point Point object to set as the PointItem's geomtetry.
     */
    public void setGeometry(Point2D point) {
        this.geometry = point;
    }
    

    /**
     * Getter method that returns the geometry in a standardized pair of arrays format for storage in the DB.
     * @return double[][] containting an x array and a y array, in that order.
     */
    public double[][] getArray() {
        double[] x = new double[] {this.geometry.getX()};
        double[] y = new double[] {this.geometry.getY()};
        return new double[][] {x, y};
    }
}