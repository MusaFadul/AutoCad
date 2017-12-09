/**
 * 
 */
package geometry;

import java.awt.Shape;
import java.awt.geom.Path2D;

import core_classes.Feature;
import java.awt.Polygon;

/**
 * @author Isaac
 * Class for representing polygons in our system.  Inherits from Feature.
 */
public class PolygonItem extends Feature {
	
	private Shape shape;
	
	public PolygonItem(int id, Path2D shape) {
		super(id);
		this.shape = shape;
	}

	/**
	 * @return the shape
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * @param shape the shape to set
	 */
	public void setShape(Shape shape) {
		this.shape = shape;
	}

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

    /**
     * Getter method that returns the geometry in a standardized pair of arrays format for storage in the DB.
     * @return double[][] containting an x array and a y array, in that order.
     */
    @Override
    public double[][] getArray() {

        // Initialize two arrays with the length of the number of vertices.
        double[] x = new double[this.geometry.npoints];
        double[] y = new double[this.geometry.npoints];

        // Now map the vertices to them.
        for (int i=0; i<this.geometry.npoints; i++) {
            x[i] = this.geometry.xpoints[i];
            y[i] = this.geometry.ypoints[i];
        }

        // Now pack it into a single array and return it.
        return new double[][] {x, y};

    }

}
