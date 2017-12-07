package features;

import java.awt.geom.Point2D;

public class TextItem {
	
	private Point2D basePosition;
	private String text;
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
	
	
	

}
