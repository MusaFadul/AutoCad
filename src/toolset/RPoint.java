package toolset;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * 
 * @author OlumideEnoch
 * @since
 * @version 1 
 */
public class RPoint  {
	
	private Color color = Color.BLACK;
	private Point2D realPoint;
	private Point2D imagePoint;
	private Ellipse2D imagePointShape;
	
	private int r = 10;
	
	
	/**
	 * Creates the RPoint
	 */
	public RPoint() {
		super();
	}
	
	/**
	 * Creates the RPoint
	 * @param point the Point to set
	 */
	public RPoint(Point2D point) {
		super();
		this.realPoint = point;
		this.setImagePointShape(new Ellipse2D.Double(point.getX() - (r/2), point.getY() - (r/2), r , r));
	}
	
	/**
	 * Creates the RPoint
	 * @param point the Point to set
	 * @param color the Color to set
	 */
	public RPoint(Point2D point, Color color) {
		super();
		this.realPoint = point;
		this.color = color;
		this.setImagePointShape(new Ellipse2D.Double(point.getX() - (r/2), point.getY() - (r/2), r , r));
	}

	/**
	 * Returns the color
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the color
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Returns the RealPoint
	 * @return the realPoint to set
	 */
	public Point2D getRealPoint() {
		return realPoint;
	}

	/**
	 * Sets the realPoint
	 * @param realPoint the RealPoint to set
	 */
	public void setRealPoint(Point2D realPoint) {
		this.realPoint = realPoint;
	}

	/**
	 * Returns the ImagePoint
	 * @return the ImagePoint
	 */
	public Point2D getImagePoint() {
		return imagePoint;
	}

	/**
	 * Returns the ImagePointShape
	 * @return the imagePointShape
	 */
	public Ellipse2D getImagePointShape() {
		return imagePointShape;
	}

	/**
	 * Sets the ImagePointShape
	 * @param imagePointShape the imagePointShape to set
	 */
	public void setImagePointShape(Ellipse2D imagePointShape) {
		this.imagePointShape = imagePointShape;
	}

	/**
	 * Sets the ImagePoint
	 * @param imagePoint the ImagePoint to set
	 */
	public void setImagePoint(Point2D imagePoint) {
		this.imagePoint = imagePoint;
		this.setImagePointShape(new Ellipse2D.Double(imagePoint.getX() - (r/2), imagePoint.getY() - (r/2), r , r));
	}

}
