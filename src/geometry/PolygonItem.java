/**
 * 
 */
package geometry;

import java.awt.Shape;
import java.awt.geom.Path2D;

import core_classes.Feature;

/**
 * @author OlumideEnoch
 *
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

}
