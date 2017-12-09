/**
 * 
 */
package core_classes;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * @author OlumideEnoch
 * Abstract class for storing Features as the same type, so that they may be put into Layers.
 */
public class Feature {

    protected int id;
    protected Shape shape;
    protected List<Rectangle2D> vertices = new ArrayList<Rectangle2D> ();
    protected String featureType;

    /**
     * Constructor for making feature object
     * @param id
     */
    public Feature(int id) {
        this.id = id;
    }

    /**
     * Getter method for the id of this feature.
     * @return int
     */
    public int getId() {
        return this.id;
    }

    public double[][] getArray() {
        return new double[][] {};
    }

	/**
	 * @return the vertices
	 */
	public List<Rectangle2D> getVertices() {
		return vertices;
	}

	/**
	 * @param vertices the vertices to set
	 */
	public void setVertices(List<Rectangle2D> vertices) {
		this.vertices = vertices;
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

	/**
	 * @return the featureType
	 */
	public String getFeatureType() {
		return featureType;
	}

	/**
	 * @param featureType the featureType to set
	 */
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}
}