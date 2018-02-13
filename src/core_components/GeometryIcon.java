package core_components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

import application_frames.MainFrame;
import application_frames.SettingsFrame;
import core_classes.Layer;

/**
 * This class is an extension of the swing JPanel component.
 * It is used in the TableOfContents to draw the geometry type of a layer.
 * The paint component super method draws the shape corresponding to the layer geometry type 
 * i.e point, polyline or polygon.
 * 
 * @author Olumide Igbiloba
 * @since Dec 7, 2017
 */
public class GeometryIcon extends JPanel {
	
	private static final long serialVersionUID = 1L;
	/**
	 * Color of the JPanel
	 */
	private Color color = SettingsFrame.DEFAULT_STATE_COLOR;
	/**
	 * Geometry type of the panel
	 */
	private String geometryType ="";
	
	/**
	 * Creates the GeometryTableIcon
	 */
	public GeometryIcon() {
		super();
		
	}
	
	/**
	 * Sets the Layer type
	 * @param type the type to set
	 */
	public void setLayerType(String type) {
		this.geometryType = type;
	}

	/**
	 * Sets the Layer
	 * @param layer the layer to set
	 */
	public void setLayer(Layer layer) {
		this.color = layer.getLayerColor();
		this.geometryType = layer.getLayerType();
		MainFrame.panel.repaint();
		repaint();
	}
	
	/**
	 * Sets the color 
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
		repaint();
	};
	
	/**
	 * Paints the Component using an Graphics Object
	 * @param g the graphics object to set
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2D = (Graphics2D) g.create();
		g2D.setColor(color);
		
		try {
			
			Rectangle bounds = new Rectangle (getWidth(), getHeight());
			
			if(geometryType.equals(SettingsFrame.POLYGON_GEOMETRY)) {
				g2D.fill(new Rectangle2D.Double(bounds.getCenterX() - (25/2), bounds.getCenterY() - (20/2), 25, 20));
			}
			
			if(geometryType.equals(SettingsFrame.POINT_GEOMETRY)) {
				g2D.fill(new Ellipse2D.Double(bounds.getCenterX() - (10/2), bounds.getCenterY() - (10/2), 10, 10));
			}
			
			if(geometryType.equals(SettingsFrame.POLYLINE_GEOMETRY)) {
				int padding = 5;
				g2D.draw(new Line2D.Double(padding, bounds.getCenterY(), getWidth() - padding , bounds.getCenterY()));
			}
			
		} finally {
			g2D.dispose();
		}	
	}
}
