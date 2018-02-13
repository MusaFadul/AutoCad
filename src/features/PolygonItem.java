/**
 * 
 */
package features;

import java.awt.Shape;
import java.awt.geom.Path2D;

import application_frames.SettingsFrame;
import core_classes.Feature;
import java.awt.Polygon;

/**
 * Class for representing polygons in the system.  Inherits from Feature.
 * @author Isaac
 * @since Dec 7, 2017
 * @version 1
 */
public class PolygonItem extends Feature {
	
	private Shape shape;
	
	/**
	 * Creates the PolygonItem
	 * @param id the id to set
	 * @param shape the shape to set
	 */
	public PolygonItem(int id, Path2D shape) {
		super(id);
		super.setFeatureType(SettingsFrame.POLYGON_GEOMETRY);
		this.shape = shape;
	}

	/**
	 * Returns the shape of the PolygonItem
	 * @return the shape
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Sets the shape pof the PolygonItem
	 * @param shape the shape to set
	 */
	public void setShape(Shape shape) {
		this.shape = shape;
	}

    Polygon geometry;

    /**
     * Creates the PolygonItem
     * @param id the id to set
     * @param polygon the Geometry for this feature to set (in the form of a Polygon object)
     */
    public PolygonItem(int id, Polygon polygon) {

        super(id);
        this.geometry = polygon;
        super.setVerticesFromArray(polygon.xpoints, polygon.ypoints);
        super.setShape(polygon);
        super.setFeatureType("Polygon");

    }

    /**
     * Returns the PolylineItem's geometry object
     * @return geometry Returns the Polygon 
     */
    public Polygon getGeometry() {
        return geometry;
    }

    /**
     * Sets the PolylineItem's geometry object.
     * @param polygon the Polygon to set as the geometry.
     */
    public void setGeometry(Polygon polygon) {
        this.geometry = polygon;
    }

    /**
     * Returns the Array of the PolygonItem in the form of a double[][]
     * @return null Returns null
     */
	public double[][] getArray() {
		// TODO Auto-generated method stub
		return null;
	}

}
