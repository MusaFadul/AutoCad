/**
 * 
 */
package features;

import java.awt.Shape;
import java.awt.geom.Path2D;

import core_classes.Feature;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.util.ArrayList;

/**
 * Class for representing Polylines in the system.  Inherits from Feature.
 * @author Isaac
 * @since Dec 7, 2017
 * @version 1
 */
public class PolylineItem extends Feature {
	
	private Path2D shape;
	private ArrayList<Line2D.Double> polyline = new ArrayList<Line2D.Double>();
	
	/**
	 * Creates the PolylineItem
	 * @param id the id to set
	 * @param shape the shape to set
	 */
	public PolylineItem(int id, Path2D shape) {
		super(id);
		this.shape = shape;
		super.setShape(shape);
	}

	/**
	 * Returns the shape of the PolylineItem
	 * @return the shape
	 */
	public Path2D getShape() {
		return shape;
	}

	/**
	 * Sets the shape of the PolylineItem
	 * @param shape the shape to set
	 */
	public void setShape(Shape shape) {
		this.shape = (Path2D) shape;
		super.setShape(shape);
	}
	
	/**
	 * Returns the polyline as an ArrayList of the type double
	 * @return polyline Returns the polyline 
	 */
	public ArrayList<Double> getListOfLines() {
		
		polyline.clear();
		
		for(int i = 0; i < super.getVertices().size(); i++) {
			
			if(i < super.getVertices().size() - 1) {
				polyline.add(new Line2D.Double(
						super.getVertices().get(i).getCenterX(), 
						super.getVertices().get(i).getCenterY(), 
						super.getVertices().get(i+1).getCenterX(), 
						super.getVertices().get(i+1).getCenterY()));
			}
		}
		
		return polyline;
	}

    ArrayList<Line2D.Double> geometry;

    /**
     * Creates the PolylineItem
     * @param id the id for this feature to set
     * @param polyline the Geometry for this feature to set (in the form of a Line2D object)
     */
    public PolylineItem(int id, ArrayList<Line2D.Double> polyline) {

        super(id);
        this.geometry = polyline;

    }

    /**
     * Returns the PolylineItem's geometry object
     * @return Line2D
     */
    public ArrayList<Line2D.Double> getGeometry() {
        return geometry;
    }

    /**
     * Sets the PolylineItem's geometry object
     * @param polyline the Line2D to set as the geometry
     */
    public void setGeometry(ArrayList<Line2D.Double> polyline) {
        this.geometry = polyline;
    }

    /**
     * Returns the Array of the PolylineItem in the form of a double[][]
     * @return null Returns null
     */
	public double[][] getArray() {
		// TODO Auto-generated method stub
		return null;
	}

}
