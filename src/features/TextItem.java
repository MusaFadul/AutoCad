package features;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class TextItem {
	
	private Point2D basePosition;
	private String text;
	private Color color;
	private RoundRectangle2D borderRectangleInPanel;

	/**
	 * @param basePosition
	 * @param text
	 */
	public TextItem(Point2D basePosition, String text) {
		super();
		this.basePosition = basePosition;
		this.text = text;
	}
	/**
	 * @return the basePosition
	 */
	public Point2D getBasePosition() {
		return basePosition;
	}
	/**
	 * @param basePosition the basePosition to set
	 */
	public void setBasePosition(Point2D basePosition) {
		this.basePosition = basePosition;
	}
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}
	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}
	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}
	
	/**
	 * @return the borderRectangleInPanel
	 */
	public RoundRectangle2D getBorderRectangleInPanel() {
		return borderRectangleInPanel;
	}
	/**
	 * @param borderRectangleInPanel the borderRectangleInPanel to set
	 */
	public void setBorderRectangleInPanel(RoundRectangle2D borderRectangleInPanel) {
		this.borderRectangleInPanel = borderRectangleInPanel;
	}
	
	/**
	 * Checks if there is an intersection on another rectangle (from a text item)
	 * @param rectangle
	 * @return true if there is an intersection on another rectangle
	 */
	public boolean borderIntersectsAnotherRectangle(Rectangle2D rectangle) {
		
		if(getBorderRectangleInPanel().getBounds2D() != null) {
		
			Rectangle2D self = getBorderRectangleInPanel().getBounds2D();
			
			Point2D topRight = new Point2D.Double(self.getMaxX(), self.getMinY());
			Point2D bottomRight = new Point2D.Double(self.getMaxX(), self.getMaxY());
			Point2D bottomLeft = new Point2D.Double(self.getMinX(), self.getMaxY());
			Point2D topLeft = new Point2D.Double(self.getMinX(), self.getMinY());
			
			if(rectangle.contains(topRight)) {
				return true;
			}
			
			
			else if(rectangle.contains(bottomRight)) {
				return true;
			}
			
			
			else if(rectangle.contains(bottomLeft)) {
				return true;
			}
			
			
			else if(rectangle.contains(topLeft)) {
				return true;
			}
		} 
		
		
		return false;
	}

}
