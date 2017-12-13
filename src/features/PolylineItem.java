/**
 * 
 */
package features;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import core_classes.Feature;
import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.util.ArrayList;

/**
 * @author Isaac
 * Class for representing polylines in our system.  Inherits from Feature.
 */
public class PolylineItem extends Feature {
	
	private Path2D shape;
	private ArrayList<Line2D.Double> polyline = new ArrayList<Line2D.Double>();
	
	public PolylineItem(int id, Path2D shape) {
		super(id);
		this.shape = shape;
	}

	/**
	 * @return the shape
	 */
	public Path2D getShape() {
		return shape;
	}

	/**
	 * @param shape the shape to set
	 */
	public void setShape(Shape shape) {
		this.shape = (Path2D) shape;
	}
	
	/**
	 * @return 
	 * 
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
