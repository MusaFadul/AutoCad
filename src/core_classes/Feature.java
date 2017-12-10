/**
 * 
 */
package core_classes;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author OlumideEnoch
 *
 */
public class Feature {

    private int id;
    private Shape shape;
    private List<Rectangle2D> vertices = new ArrayList<Rectangle2D> ();
    private String featureType;
    private boolean isEllipse = false;
    private double radiusX, radiusY;
	private double[][] coordinatesArrayXY;

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

	/**
	 * @return the isEllipse
	 */
	public boolean isEllipse() {
		return isEllipse;
	}

	/**
	 * @param isEllipse the isEllipse to set
	 */
	public void setEllipse(boolean isEllipse, double radiusX, double radiusY) {
		
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		
		this.isEllipse = isEllipse;
	}

	/**
	 * Returns the center coordinates of the feature vertices
	 * @return the coordinatesArrayXY
	 */
	public double[][] getCoordinatesArrayXY() {

		double[] x = new double[vertices.size()];
		double[] y = new double[vertices.size()];
		
		for (int i = 0 ; i < vertices.size(); i++) {
			x[i] = vertices.get(i).getCenterX();
			y[i] = vertices.get(i).getCenterY();
		}

		double[][] newCoords = {x, y};
		
		coordinatesArrayXY = newCoords;
		
		return coordinatesArrayXY;
	}

	/**
	 * @param coordinatesArrayXY the coordinatesArrayXY to set
	 */
	public void setCoordinatesArrayXY(double[][] coordinatesArrayXY) {
		this.coordinatesArrayXY = coordinatesArrayXY;
	}
	
	public double getRadiusX() {
		return radiusX;
	}

	/**
	 * @return the radiusY
	 */
	public double getRadiusY() {
		return radiusY;
	}

}
