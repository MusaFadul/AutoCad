package effects;

import java.awt.geom.Line2D;

/**
 * Class for the creation of a GridLine used in the drawing panel
 * @author Olumide Igbiloba
 * @since Dec 7, 2017
 */
public class GridLine {
	private int weight = 0;
	private Line2D line;
	
	/**
	 * Creates the GridLine
	 * @param weight the weight to set
	 * @param line the line to set
	 */
	public GridLine(int weight, Line2D line) {
		this.weight = weight;
		this.line = line;
	}
	
	/**
	 * Creates the GridLine
	 * @param line the line to set
	 */
	public GridLine(Line2D line) {
		this.line = line;
	}
	
	/**
	 * Returns the weight of the GridLine
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * Sets the weight of the GridLine
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	/**
	 * Returns the line 
	 * @return the line
	 */
	public Line2D getLine() {
		return line;
	}
	
	/**
	 * Sets the line
	 * @param line the line to set
	 */
	public void setLine(Line2D line) {
		this.line = line;
	}
	
	
}
