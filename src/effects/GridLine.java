package effects;

import java.awt.geom.Line2D;

public class GridLine {
	private int weight = 0;
	private Line2D line;
	/**
	 * @param weight
	 * @param line
	 */
	public GridLine(int weight, Line2D line) {
		this.weight = weight;
		this.line = line;
	}
	
	public GridLine(Line2D line) {
		this.line = line;
	}
	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	/**
	 * @return the line
	 */
	public Line2D getLine() {
		return line;
	}
	/**
	 * @param line the line to set
	 */
	public void setLine(Line2D line) {
		this.line = line;
	}
	
	
}
