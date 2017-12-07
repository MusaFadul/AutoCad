/**
 * 
 */
package core_components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import core_classes.Feature;
import core_classes.Layer;
import custom_components.CustomJPanel;
import features.GridLine;
import features.TextItem;
import tester.MainFrame;
import toolset.Settings;

/**
 * Panel for drawing items
 * 
 * Supports simple drawing guides, such as snap, grid and ortho 
 * 
 * WORK IN PROGRESS
 * 
 * @author OlumideEnoch
 *
 */
public class DrawingJPanel extends CustomJPanel implements MouseMotionListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6318145875906198958L;
	
	/**Size of snaps*/
	private int snapSize = Settings.snappingTolerance;
	
	/**Snapping mode*/
	private boolean snappingModeIsOn = false;
	
	/**Grid state*/
	private boolean gridIsOn = false;
	
	/**Editing mode*/
	private boolean editModeIsOn = false;
	
	/**Drawing grid lines*/
	private List<GridLine> gridLines = new ArrayList<GridLine>();;
	
	/**Snap points of the grid*/
	private List<Rectangle2D> gridSnapPoints = new ArrayList<Rectangle2D>();
	
	/**List of points when mouse is clicked, cleared during new edit session*/
	private List<Rectangle2D> vertexList = new ArrayList<Rectangle2D>(); 
	
	/**Snapped point when mouse is clicked*/
	private Rectangle2D snapPoint = null;
	
	/**Current layer to save drawn items*/
	public static Layer currentLayer;
	
	/**Feature ID, must be increased where used e.g. featureID++*/
	private static int featureID = 0;
	
	/**Temporary polygon*/
	private Path2D tempPolygon = null;
	
	/**Collected drawing snap points*/
	private List<Rectangle2D> drawingSnapPoints = new ArrayList<Rectangle2D>();
	
	/**Moving tool tips*/
	private TextItem tooltip = null;
	

	/**
	 * Constructs a new drawing panel
	 * @param rectangle Bounds of the panel
	 */
	public DrawingJPanel(Rectangle rectangle ) {
		super();
		setBounds(rectangle);
		renderGrid(Settings.gridSizeMM);
		addMouseMotionListener(this);
		addMouseListener(this);
	}

	@Override 
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Clone the graphics object
		Graphics2D g2d = (Graphics2D) g.create();
		
		// General rendering, order matters !
		
		try {
			
			// 0. Set drawing pen settings to default
			// --------------------------------------
			g2d.setStroke(new BasicStroke(1));
			g2d.setColor(Settings.DEFAULT_LAYER_COLOR);
			
			// 1. Render the grid lines
			// ------------------------------------------
			for(GridLine item : this.gridLines) {
				Line2D line = item.getLine();
				g2d.setStroke(new BasicStroke(item.getWeight()));
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.draw(line);
			}
			
			// 2. Render the layers in the table of contents
			// ------------------------------------------
			for(Layer layer : TableOfContents.layerList) {
				
				if(layer.isVisible()) {
					
					Color c = layer.getLayerColor();
					
					for(Feature feature : layer.getListOfFeatures()) {
						
						// Fill the shape
						g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
						g2d.fill(feature.getShape());
						
						// Draw outline
						g2d.setStroke(new BasicStroke(3));
						g2d.setColor(c);
						g2d.draw(feature.getShape());
						
						// Render the shape vertices
						for(Shape shape : feature.getVertices()) {
							g2d.setColor(c);
							g2d.fill(shape);
						}	
					}
				}
			}
			
			// 3. Render temporary polygon
			// ------------------------------------------
			if(tempPolygon != null) {
				Color c = currentLayer.getLayerColor();
				g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
				g2d.fill(tempPolygon);
				
				g2d.setStroke(new BasicStroke(3));
				g2d.setColor(c);
				g2d.draw(tempPolygon);
			}
			
			// 4. Render vertices of points drawn
			// ------------------------------------------
			int count = 0;
			for(Rectangle2D item : this.vertexList) {
				g2d.setColor(currentLayer.getLayerColor());
				if(count == 0) {
					g2d.setColor(Settings.DEFAULT_BUTTON_COLOR);
				} 
				g2d.fill(item);
				count++;
			}
			
			// 5. Render the snap 
			// ------------------------------------------
			if(!(snapPoint == null)) {
				g2d.setColor(Color.PINK);
				g2d.draw(snapPoint);
			}
			
			// 6. Render tool tips
			// ------------------------------------------
			if(this.tooltip != null) {
				FontMetrics fm = g2d.getFontMetrics();
	            Rectangle2D rect = fm.getStringBounds(tooltip.getText(), g2d); 	// -- Wrap rectangle around tool tip
	
	            g2d.setColor(Settings.DEFAULT_BUTTON_COLOR);									// -- Set background color of rectangle to be black
	            g2d.fillRect((int)tooltip.getBasePosition().getX(),
	            		(int)tooltip.getBasePosition().getY() - fm.getAscent(),
	                       (int) rect.getWidth(),
	                       (int) rect.getHeight());
	            
	            //g2d.setFont(item.getFont());
	            g2d.setColor(Color.WHITE);									// -- Set text color to white
	            g2d.drawString(tooltip.getText(), (int)tooltip.getBasePosition().getX(), (int)tooltip.getBasePosition().getY());
			}
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
		}
		
		finally {
			g2d.dispose();
		}
	}
	
	/**
	 * Renders grid on the drawing panel
	 * @param gridSizeMM grid size specified in MM
	 */
	private void renderGrid(int gridSizeMM) {
		
		if(gridIsOn) {
			
		// 1. Find conversion factor from mm to DPI
		// ------------------------------------------
		int grid = (int) ((gridSizeMM * Settings.DEFAULT_DPI)/25.4);
		
		// 2. Draw vertical lines
		// ------------------------------------------
		int value = 0;
		int count = 0;
		
		while(true) {
		
			if((getBounds().width >= value)) {
				
				Point2D start = new Point2D.Double(value, 0);
				Point2D end = new Point2D.Double(value, getBounds().height);
				
				Line2D line = new Line2D.Double(start, end);
				
				GridLine gridline = new GridLine(line);
				if(count != 0) {
					if(count % Settings.gridMajorInterval == 0) {
						gridline.setWeight(2);
					}
				}
				
				// 2.1 Set up snap points for each vertical line using the grid size along the Y
				// ------------------------------------------------------------------------------
				for(int i = 0; i < getBounds().height; i = i + grid) {
				
					Point2D item = new Point2D.Double(value, i);
					Rectangle2D snapGrid = new Rectangle2D.Double(item.getX() - (snapSize/2), item.getY() - (snapSize/2), snapSize, snapSize);
					gridSnapPoints.add(snapGrid);
						
				}
				
				// 2.2 Increase the grid value
				// ------------------------------------------
				value = value + grid;
				
				// 2.3 Add the line to the gridLines list
				// ------------------------------------------
				gridLines.add(gridline);
				
				count++;
				
			} else {
				break;
			}	
		};
		
		// 3. Draw horizontal lines
		// ------------------------------------------
		value = 0;
		count = 0;
		while(true) {
			
			if((getBounds().height >= value)) {
				
				Point2D start = new Point2D.Double(0, value);
				Point2D end = new Point2D.Double(getBounds().width, value);
				
				Line2D line = new Line2D.Double(start, end);
				
				GridLine gridline = new GridLine(line);
				if(count != 0) {
					if(count % 5 ==0) {
						gridline.setWeight(2);
					}
				}
				value = value + grid;
				
				// 3.1 Add the line to the gridLines list
				// ------------------------------------------
				gridLines.add(gridline);
				count++;
				
			} else {
				break;
			}	
		};
		
		repaint();
		}
	}
	
	/**
	 * Turns the grid on and off
	 */
	public void toggleGrid() {
		
		if(!(this.gridLines.isEmpty())) {
			this.gridLines.clear();
			this.gridSnapPoints.clear();
			this.snapPoint = null;
			this.gridIsOn  = false;
			repaint();
		} else if (this.gridLines.isEmpty()) {
			this.gridIsOn = true;
			renderGrid(Settings.gridSizeMM);
			repaint();
		}
		
	}
	
	/**
	 * Turns snappig on and off
	 */
	public void toggleSnap() {
		
		if(this.snappingModeIsOn) {
			snappingModeIsOn = false;
		} else {
			snappingModeIsOn = true;
		}
		
	}
	
	/**
	 * Toggles the edit session 
	 * @param layerIndex the index of the layer on the table of contents or from the combo box
	 * @param signal specify not to toggle edit session but to current editing layer by using "continue"
	 */
	public void toggleEditStart(int layerIndex, String signal) {
		
		// Change the current editing layer
		int layerID = (int) MainFrame.tableOfContents.getModel().getValueAt(layerIndex, TableOfContents.LAYER_ID_COL_INDEX);
		currentLayer = TableOfContents.findLayerWithID(layerID);
		
		if(signal == null) {
			signal = "";
		}
		
		// Turn off or on when the signal is to continue editing
		if(!signal.equals(Settings.DRAW_CONTINUE)) {
			
			if(this.editModeIsOn) {
				editModeIsOn = false;
			} else {
				editModeIsOn = true;
				this.vertexList.clear();
			}
		}
		
	}
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		

		
		if(snappingModeIsOn) {
			
			for(Rectangle2D item : this.gridSnapPoints) {
				if(!(item.contains(e.getPoint()))) {
					this.snapPoint = null;
					this.repaint();
					break;
				}
			}
			
			for(Rectangle2D item : this.gridSnapPoints) {
				if(item.contains(e.getPoint())) {
					this.snapPoint = item;
					this.repaint();
					//this.handlePolygonClosingProtocol(e);
					break;
				}
			}
			
			if(!gridIsOn) {
				this.handlePolygonClosingProtocol(e);
			}
				
		} else {
			
			this.snapPoint = null;
			this.handlePolygonClosingProtocol(e);
		}	
	}
	
	/**
	 * Closing protocol for drawing polygons
	 * For situation where the snap or grid is turned off and user needs to close a polygon
	 * @param e
	 */
	private void handlePolygonClosingProtocol(MouseEvent e) {
		
		// 0. Only perform protocol if the edit mode is on
		// ------------------------------------------------
		if(this.editModeIsOn) {
			
			// 1. For Polygons
			// ------------------------------------------------
			if(currentLayer.getLayerType() == "Polygon") {
				
				// 2. Ensure that at least one point have been drawn
				// ------------------------------------------------
				if(this.vertexList.size() > 0) {
					
					// 3. Then test for the first point
					// ------------------------------------------------
					if(this.vertexList.get(0).contains(e.getPoint())) {
						
						// 4. If mouse is on the first point, then snap the mouse to the first point
						// ------------------------------------------------
						this.snapPoint = this.vertexList.get(0);
						
						// 5. Show some tool tip
						// ------------------------------------------------
						this.tooltip = new TextItem(e.getPoint(), Settings.CLOSE_POLYGON_MESSAGE);
						repaint();
						
					} else {
						
						// 6. Erase the snap and the tool tip if mouse goes away
						// ------------------------------------------------
						this.snapPoint = null;
						this.tooltip = null;
						repaint();
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		
		if(editModeIsOn) {
			
			// Protocol for drawing polygon
			if(currentLayer.getLayerType() == "Polygon") {
			
				Point2D point = e.getPoint();
				
				if(this.snapPoint != null) {
					
					point = new Point2D.Double(this.snapPoint.getCenterX(), this.snapPoint.getCenterY());
				}
				
				Rectangle2D vertex = new Rectangle2D.Double(point.getX() - (snapSize/2), point.getY() - (snapSize/2), snapSize, snapSize);
				
				this.vertexList.add(vertex);
				this.drawingSnapPoints.add(vertex);
				
				repaint();
				
				if(this.vertexList.size() > 1) {
					
					MainFrame.log("Tip: Click the first point to finish shape");
					
					// Create temporary polygon as an hint
					if(this.vertexList.size() > 2) {
						showTempPolygon(this.vertexList);
					}
					
					if(this.vertexList.get(0).getCenterX() == point.getX() && this.vertexList.get(0).getCenterY() == point.getY()) {
						
						finishPolygon(this.vertexList);
						MainFrame.log("Polygon feature created, click to save your edits");
						this.vertexList.clear();
						this.tempPolygon = new Path2D.Double();
						this.tooltip = null;
					}
				}
			}
				
		} else {
			
			MainFrame.log("Drawing attempted but edit session is off");
			
		}
		
	}
	
	private void showTempPolygon (List<Rectangle2D> pointList) {
		
		tempPolygon = new Path2D.Double();
		tempPolygon.moveTo(pointList.get(0).getCenterX(), pointList.get(0).getCenterY());
		
		for(Rectangle2D vertex : pointList) {
			tempPolygon.lineTo(vertex.getCenterX(), vertex.getCenterY());
		}
		
		tempPolygon.closePath();
		repaint();
		
	}

	private void finishPolygon(List<Rectangle2D> pointList) {
		
		Path2D path = new Path2D.Double();
		
		path.moveTo(pointList.get(0).getCenterX(), pointList.get(0).getCenterY());
		
		Feature feature = new Feature(featureID++);
		for(Rectangle2D vertex : pointList) {
			path.lineTo(vertex.getCenterX(), vertex.getCenterY());
			feature.getVertices().add(vertex);
		}
		
		path.closePath();
		feature.setShape(path);
		
		currentLayer.getListOfFeatures().add(feature);
		currentLayer.setNotSaved(true);
		
		repaint();
		
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
