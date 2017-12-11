/**
 * 
 */
package geometry;

import java.awt.Shape;
import java.awt.geom.Path2D;

import core_classes.Feature;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * @author Isaac
 * Class for representing polylines in our system.  Inherits from Feature.
 */
public class PolylineItem extends Feature {
	
	private Shape shape;
	
	public PolylineItem(int id, Path2D shape) {
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

    ArrayList<Line2D.Double> geometry;

    /**
     * Constructor for PolylineItem feature.
     * @param id Id for this feature
     * @param polyline Geometry for this feature, in the form of a Line2D object.
     */
    public PolylineItem(int id, ArrayList<Line2D.Double> polyline) {

        super(id);
        this.geometry = polyline;

    }

    /**
     * Getter method for the PolylineItem's geometry object.
     * @return Line2D
     */
    public ArrayList<Line2D.Double> getGeometry() {
        return geometry;
    }

    /**
     * Setter method for the PolylineItem's geometry object.
     * @param polyline Line2D to set as the geometry.
     */
    public void setGeometry(ArrayList<Line2D.Double> polyline) {
        this.geometry = polyline;
    }

	public double[][] getArray() {
		// TODO Auto-generated method stub
		return null;
	}

}
