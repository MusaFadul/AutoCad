package features;
import core_classes.Feature;

import java.awt.geom.Point2D;

/**
 * Class for representing points in the system.  Inherits from Feature.
 * @author Isaac
 * @since Dec 7, 2017
 * @version 1
 */
public class PointItem extends Feature {
    
    private Point2D geometry;

    /**
     * Creates a PointItem feature.
     * @param id the Id for this feature to set
     * @param point the point Geometry for this feature to set (in the form of a Point object)
     */
    public PointItem(int id, Point2D point) {

        super(id);
        this.geometry = point;

    }

    /**
     * Returns the PointItem's geometry object
     * @return Point
     */
    public Point2D getGeometry() {
        return this.geometry;
    }

    /**
     * Sets the PointItem's geometry object
     * @param point Point object to set as the PointItem's geometry.
     */
    public void setGeometry(Point2D point) {
        this.geometry = point;
    }

    /**
     * Returns the geometry in a standardized pair of arrays format for storage in the DB
     * @return double[][] containing an x array and a y array, in that order.
     */
    public double[][] getArray() {
        double[] x = new double[] {this.geometry.getX()};
        double[] y = new double[] {this.geometry.getY()};
        return new double[][] {x, y};
    }
}