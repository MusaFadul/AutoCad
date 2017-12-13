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
import core_classes.Layer;

public class GeometryTableIcon extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color color = Color.PINK;
	private String type ="";
	
	public GeometryTableIcon() {
		super();
		
	}
	
	public void setLayerType(String type) {
		this.type = type;
	}
	
	public void setLayerColor(Layer layer) {
		this.color = layer.getLayerColor();
		this.type = layer.getLayerType();
		repaint();
	};
	
	public void setLayer(Layer layer) {
		this.color = layer.getLayerColor();
		this.type = layer.getLayerType();
		MainFrame.panel.repaint();
		repaint();
	}
	
	public void setColor(Color color) {
		this.color = color;
		repaint();
	};
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		
		Graphics2D g2D = (Graphics2D) g.create();
		g2D.setColor(color);
		
		try {
			
			Rectangle bounds = new Rectangle (getWidth(), getHeight());
			
			if(type.equals("Polygon")) {
				g2D.fill(new Rectangle2D.Double(bounds.getCenterX() - (25/2), bounds.getCenterY() - (20/2), 25, 20));
			}
			
			if(type.equals("Point")) {
				g2D.fill(new Ellipse2D.Double(bounds.getCenterX() - (10/2), bounds.getCenterY() - (10/2), 10, 10));
			}
			
			if(type.equals("Polyline")) {
				int padding = 5;
				g2D.draw(new Line2D.Double(padding, bounds.getCenterY(), getWidth() - padding , bounds.getCenterY()));
			}
			
		} finally {
			g2D.dispose();
		}	
	}
}
