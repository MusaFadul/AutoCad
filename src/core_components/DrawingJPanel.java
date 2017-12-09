/**
 * 
 */
package core_components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import core_classes.Feature;
import core_classes.Layer;
import custom_components.CustomJPanel;
import features.GridLine;
import features.TextItem;
import geometry.PolygonItem;
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
	
	/**Feature type, circle, rectangle etc*/
	public static String currentFeatureType = "";

	/**Temporary Shapes*/
	private Shape tempShape = null;
	
	/**Temporary polygon*/
	private List<Line2D> tempLine = new ArrayList<Line2D>();;
	
	/**Global drawing snap points*/
	private List<Rectangle2D> globalDrawingSnapPoints = new ArrayList<Rectangle2D>();
	
	/**Moving tool tips*/
	private TextItem tooltip = null;
	
	/**Moving hint*/
	private static TextItem movingHint = null;
	
	private static int animatorTime;
	private static Timer timer, fadetimer;
	

	/**
	 * Constructs a new drawing panel
	 * @param rectangle Bounds of the panel
	 */
	public DrawingJPanel(Rectangle rectangle ) {
		
		super();
		
		setBounds(rectangle);
		addMouseMotionListener(this);
		addMouseListener(this);
		
		renderGrid(Settings.gridSizeMM);
		showAnimatedHint("Welcome", Settings.DEFAULT_STATE_COLOR);
		
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
						
						if(layer.getLayerType().equals(Settings.POLYGON_GEOMETRY)) {
							// Fill the shape
							g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
							g2d.fill(feature.getShape());
						}
						
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
			
			// 3. Render temporary shapes
			if(tempShape != null) {
				
				Color c = currentLayer.getLayerColor();
				g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
				
				if(currentLayer.getLayerType().equals(Settings.POLYGON_GEOMETRY)) {
					// Fill the shape
					g2d.fill(tempShape);
				}
				
				g2d.setStroke(new BasicStroke(3));
				g2d.setColor(c);
				g2d.draw(tempShape);
				
			}
			
			// 3. Render temporary poly lines
			// ------------------------------------------
			for(Line2D line : this.tempLine) {
				g2d.setColor(currentLayer.getLayerColor());
				g2d.draw(line);
			}
			
			// 4. Render vertices of points drawn
			// ------------------------------------------
			int count = 0;
			for(Rectangle2D item : this.vertexList) {
				g2d.setColor(currentLayer.getLayerColor());
				
				if(count == 0) {
					g2d.setColor(Settings.DEFAULT_STATE_COLOR);
				}
				// For poly lines - change color of the first and last vertex
				if(currentLayer.getLayerType().equals(Settings.POLYLINE_GEOMETRY)) {
					if(count == this.vertexList.size() - 1) {
						g2d.setColor(Settings.HIGHLIGHTED_STATE_COLOR);
					}
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
			// TODO Find conflict of tool tip
			if(this.tooltip != null) {
				g2d.setFont(new Font("Tw Cen MT", Font.ITALIC, 18)); 
				FontMetrics fm = g2d.getFontMetrics();
	            Rectangle2D rect = fm.getStringBounds(tooltip.getText(), g2d); 	
	            Color c = Settings.DEFAULT_STATE_COLOR;
	            g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 180));									
	            g2d.fillRect((int)tooltip.getBasePosition().getX(),
	            		(int)tooltip.getBasePosition().getY() - fm.getAscent(),
	                       (int) rect.getWidth(),
	                       (int) rect.getHeight());
	            
	            
	            g2d.setColor(Color.WHITE);							
	            g2d.drawString(tooltip.getText(), (int)tooltip.getBasePosition().getX(), (int)tooltip.getBasePosition().getY());
			
			}
			
			// 7. Render Hint
			// ------------------------------------------
			if(movingHint != null ) {
				
				g2d.setFont(new Font("Tw Cen MT", Font.ITALIC, 18)); 
				FontMetrics fm = g2d.getFontMetrics();
	            Rectangle2D rect = fm.getStringBounds(movingHint.getText(), g2d); 	
	            //Color c = Settings.DEFAULT_STATE_COLOR;
	            g2d.setColor(movingHint.getColor());									
	            g2d.fillRect((int) (getWidth() - rect.getWidth() - 60),
	            		(int)movingHint.getBasePosition().getY() - 10 - fm.getAscent(),
	                       (int) rect.getWidth() + 20,
	                       (int) rect.getHeight() + 20);
	            
	            
	            g2d.setColor(Color.WHITE);							
	            g2d.drawString(movingHint.getText(), (int) (getWidth() - rect.getWidth() - 50) , (int)movingHint.getBasePosition().getY());
			
			}
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
	
		}
		
		finally {
			
			g2d.dispose();
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
	 * Turns snapping on and off
	 */
	public void toggleSnap() {
		
		if(snappingModeIsOn) {
			snappingModeIsOn = false;
		} else {
			snappingModeIsOn = true;
		}
		
	}
	
	/**
	 * Toggles the edit session 
	 * @param layerIndex the index of the layer on the table of contents or from the combo box
	 * @param featureType the current feature type to draw
	 * @param signal specify not to toggle edit session but to current editing layer by using "continue"
	 */
	public void toggleEditStart(int layerIndex, String featureType, String signal) {
		
		// Change the current editing layer and feature type
		int layerID = (int) MainFrame.tableOfContents.getModel().getValueAt(layerIndex, TableOfContents.LAYER_ID_COL_INDEX);
		currentLayer = TableOfContents.findLayerWithID(layerID);
		currentFeatureType = featureType;
		
		if(signal == null) {
			signal = "";
		}
		
		// Turn off or on when the signal is to continue editing
		if(!signal.equals(Settings.DRAW_CONTINUE)) {
			
			if(this.editModeIsOn) {
				editModeIsOn = false;
				
				showAnimatedHint("Edit session turned off", Settings.HIGHLIGHTED_STATE_COLOR);
			} else {
				editModeIsOn = true;
				
				this.vertexList.clear();
				
				showAnimatedHint("Edit session turned on", Settings.DEFAULT_STATE_COLOR);
				
				// Update draw buttons
				MainFrame.updateDrawButtonGroup();
			}
		}
	}
	
	/**
	 * Abandons current drawing session
	 */
	public void abandonEditSession() {
		
		cleanUpDrawing();
		
		if(editModeIsOn) {
			editModeIsOn = false;
		}
	}
	
	private void handleDrawingRectangle(MouseEvent e) {
		
		if(this.vertexList.size() == 1) {
			
			for(Rectangle2D item : this.gridSnapPoints) {
				if(item.contains(e.getPoint())) {
					snapPoint = item;
					repaint();
					break;
				}
			}
			
			Point2D b = new Point2D.Double(this.vertexList.get(0).getCenterX(), this.vertexList.get(0).getCenterY());
			Point2D m = e.getPoint();
			
			double x = m.getX();
			double y = m.getY();
			
			double width = Math.abs(m.getX() - b.getX());
			double height =  Math.abs(m.getY() - b.getY());
			
			if(b.getX() < m.getX()) {
				x = b.getX();
			}
			
			if(b.getY() < m.getY()) {
				y = b.getY();
			}
			
			this.tempShape = new Rectangle2D.Double(x, y, width, height);

			repaint();
		} 
		
	}

	private void handleDrawingCircle(MouseEvent e) {

		if(this.vertexList.size() == 1) {
		
			// (a) The center point will be the first point in the vertex list
			Point2D centerPoint = new Point2D.Double(this.vertexList.get(0).getCenterX(), this.vertexList.get(0).getCenterY());
			
			// (b) The radius of the circle will be the distance from the center point to the currently moving mouse point
			double radius = (e.getPoint().distance(centerPoint));
			
			// (c) Construct circle with the parameters
			Shape circleShape = new Ellipse2D.Double(centerPoint.getX() - radius, centerPoint.getY() - radius , radius * 2, radius* 2);
			
			// (d) Display temporary cirlce
			tempShape = circleShape;
			repaint();
			
		}
	}
	
	/**
	 * 
	 * @param e Mouse event from mouse moved
	 */
	private void handleDrawingTriangle(MouseEvent e) {

		Path2D path = new Path2D.Double();
		
		if(this.vertexList.size() == 2) {
			
			path.moveTo(vertexList.get(0).getCenterX(), vertexList.get(0).getCenterY());
			
			path.lineTo(vertexList.get(1).getCenterX(), vertexList.get(1).getCenterY());
			
			path.lineTo(e.getPoint().getX(), e.getPoint().getY());
			
			path.closePath();
			
			this.tempShape = path;
			
			repaint();
		}
	}

	/**
	 * Closing protocol for drawing polygons
	 * For situation where the snap or grid is turned off and user needs to close a polygon
	 * @param e
	 */
	private void handleDrawingClosingProtocol(MouseEvent e) {
		
		// 0. Only perform protocol if the edit mode is on
		// ------------------------------------------------
		if(this.editModeIsOn) {
			
			// 1. For Polygons
			// ------------------------------------------------
			if(MainFrame.getCurrentFeatureType() != null) {
					
				if(MainFrame.getCurrentFeatureType().equals("Hexagon")) {

				// 1.1 Ensure that at least one point have been drawn
				// ------------------------------------------------
				if(this.vertexList.size() > 0) {
					
					// 1.2 Then test for the first point
					// ------------------------------------------------
					if(this.vertexList.get(0).contains(e.getPoint())) {
						
						// 1.3 If mouse is on the first point, then snap the mouse to the first point
						// ------------------------------------------------
						this.snapPoint = this.vertexList.get(0);
						
						// 1.4 Show some tool tip
						// ------------------------------------------------
						this.tooltip = new TextItem(e.getPoint(), Settings.CLOSE_POLYGON_MESSAGE);
						repaint();
						
					} else {
						
						// 1.5 Erase the snap and the tool tip if mouse goes away
						// ------------------------------------------------
						this.snapPoint = null;
						this.tooltip = null;
						repaint();
					}
				}
			}
			}
			
			// 2. For polylines
			/// ------------------------------------------------
			if(currentLayer.getLayerType() == Settings.POLYLINE_GEOMETRY) {

				// 2.1 Ensure that at least one point have been drawn
				// ------------------------------------------------
				if(this.vertexList.size() > 0) {
					
					// 2.2 Get first and last vertex of the polyline
					// ------------------------------------------------
					Rectangle2D firstVertex = vertexList.get(0);
					Rectangle2D lastVertex = vertexList.get( vertexList.size() - 1 );
					
					// 2.2 Test if the mouse is on it
					// ------------------------------------------------
					if( firstVertex.contains(e.getPoint()) || lastVertex.contains(e.getPoint()) ) {
						
						// 2.3 Show some tips
						// ------------------------------------------------
						this.tooltip = new TextItem(e.getPoint(), Settings.CLOSE_POLYLINE_MESSAGE);
						repaint();
						
					} 
					else {
						// 2.4 Erase the snap and the tool tip if mouse goes away
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
	 * Shows temporary polygon on the panel while drawing
	 * 
	 * @param pointList current list of points, should be more than 3!
	 */
	private void showTempPolygon (List<Rectangle2D> pointList) {
		
		if(pointList.size() > 2) {
			
			// Construct a new (open) path
			Path2D path = new Path2D.Double();
			
			// Start at the first item on the list
			path.moveTo(pointList.get(0).getCenterX(), pointList.get(0).getCenterY());
			
			// Connect other points
			for(Rectangle2D vertex : pointList) {
				path.lineTo(vertex.getCenterX(), vertex.getCenterY());
			}
			// Close the path
			path.closePath();
			
			tempShape = path;
			
			// Render
			repaint();
		}	
	}
	
	/**
	 * Finish drawing of a path, if the current layer is a polygon, the path will be closed and filled<br>
	 * Creates a new feature in the current layer<br>
	 * @param pointList pointList current list of points, should be more than 3!
	 */
	private void finishPath(List<Rectangle2D> pointList) {
		
		// Construct a new (open) path
		Path2D path = new Path2D.Double();
		
		// Start at the first item on the list
		path.moveTo(pointList.get(0).getCenterX(), pointList.get(0).getCenterY());
		
		// Create a new feature with an ID
		PolygonItem featurePolygon = new PolygonItem(currentLayer.getNextID(), path);
		
		// Connect the path and add all the vertices to the feature
		for(Rectangle2D vertex : pointList) {
			path.lineTo(vertex.getCenterX(), vertex.getCenterY());
			featurePolygon.getVertices().add(vertex);
		}
		
		// Close path , if the current layer is a polygon
		if(currentLayer.getLayerType().equals(Settings.POLYGON_GEOMETRY)){
			path.closePath();
		}
		
		// Set the path as the shape of the feature
		featurePolygon.setShape(path);
		featurePolygon.setFeatureType(currentFeatureType);
		
		// Add to the current list of features in the layer
		currentLayer.getListOfFeatures().add(featurePolygon);
		
		// Change the status of the layer to be unsaved
		currentLayer.setNotSaved(true);
		
		repaint();
	}
	
	private void findSnaps(MouseEvent e) {
		
		
		if(editModeIsOn) {
			
			if( MainFrame.getCurrentFeatureType() != null ) {
				
			// Handling Rectangle 
			if(currentLayer.getLayerType().equals(Settings.POLYGON_GEOMETRY) && MainFrame.getCurrentFeatureType().equals("Rectangle")) {
				this.handleDrawingRectangle(e);
			}
			
			// Handling Circle 
			if(currentLayer.getLayerType().equals(Settings.POLYGON_GEOMETRY) && MainFrame.getCurrentFeatureType().equals("Circle")) {
				this.handleDrawingCircle(e);
			}
			
			// Handling Triangle 
			if(currentLayer.getLayerType().equals(Settings.POLYGON_GEOMETRY) && MainFrame.getCurrentFeatureType().equals("Triangle")) {
				this.handleDrawingTriangle(e);
			}
			
			if(snappingModeIsOn) {
				
				// Check first current mouse is found on the grid snap points
				for(Rectangle2D item : this.gridSnapPoints) {
					if(!(item.contains(e.getPoint()))) {
						this.snapPoint = null;
						this.repaint();
						break;
					}
				}
				
				// Check for if found
				for(Rectangle2D item : this.gridSnapPoints) {
					if(item.contains(e.getPoint())) {
						this.snapPoint = item;
						this.repaint();
						//this.handlePolygonClosingProtocol(e);
						break;
					}
				}
	
				/*// Check in global snap points
				for(Rectangle2D item : this.globalDrawingSnapPoints) {
					if(item.contains(e.getPoint())) {
						this.snapPoint = item;
						this.repaint();
						break;
					}
				}
				
				// Check in global snap points
				for(Rectangle2D item : this.globalDrawingSnapPoints) {
					if(item.contains(e.getPoint())) {
						this.snapPoint = item;
						this.repaint();
						break;
					} else {
						this.snapPoint = null;
						repaint();
					}
				}*/
				
				//if(!gridIsOn) {
					this.handleDrawingClosingProtocol(e);
				//}
					
			} else {
				
				this.snapPoint = null;
				
				//if(this.editModeIsOn) {
					
					if(currentLayer.getLayerType().equals(Settings.POLYGON_GEOMETRY) || currentLayer.getLayerType().equals(Settings.POLYLINE_GEOMETRY)) {
						this.handleDrawingClosingProtocol(e);
					}
				//}
				}
			}
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

	private void cleanUpDrawing() {
		
		// Clear the vertex list for new polygon
		this.vertexList.clear();
		
		// Remove any tool tip on the drawing
		this.tooltip = null;
		
		// Remove the current snap
		this.snapPoint = null;
		
		// Remove temp lines
		this.tempLine = new ArrayList<Line2D>();
		
		// Remove temp shape
		this.tempShape = null;
		
		MainFrame.updateDrawButtonGroup();
		
		repaint();
	}

	/**
	 * Shows animated hint on the drawing panel
	 * @param message Message to display
	 * @param stateColor State color e.g Settings.LAYER_CREATED_COLOR
	 */
	public void showAnimatedHint(String message, Color stateColor) {
		
		animatorTime = 0;
		movingHint = null;
		repaint();
		
		int x = getWidth();

		movingHint = new TextItem(new Point2D.Double(x, 0), message);
		Color c = stateColor;
		movingHint.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 200));
		
		timer = new Timer(5, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				animatorTime++;
				if(movingHint != null ) {
					if(animatorTime != 75) {
						double ya = movingHint.getBasePosition().getY();
						movingHint.setBasePosition(new Point2D.Double(0, ya + 0.7));
						repaint();
					}
					
					else  {
						
						timer.stop();
						animatorTime = 0;
						
						// Stay for some time and go off
						
						fadetimer = new Timer(10, new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								// TODO Auto-generated method stub
								animatorTime++;
								
								if(movingHint != null) {
									if(animatorTime > 150) {
										if(!(movingHint.getColor().getAlpha() < 10)) {
											Color c = movingHint.getColor();
											movingHint.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()-2));
											repaint();
										} else {
											movingHint = null;
											repaint();
											fadetimer.stop();
											timer.stop();
										}
									}
								}
								if(animatorTime == 300) {
									
									movingHint = null;
									repaint();
									fadetimer.stop();
									timer.stop();
								}
							}
						});
						fadetimer.start();
					}
				}
			}
		});
		timer.start();
	}

	/**
	 * 
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		
		if(editModeIsOn) {
			
			if(MainFrame.getCurrentFeatureType() != null ) {
				
				// i. Get current point of the mouse 
				// ----------------------------------
				Point2D clickedPoint = e.getPoint();
				
				// ii. Replace the point with the snapped point in case snapping is on
				// -------------------------------------------------------------------
				if(this.snapPoint != null) {
					
					clickedPoint = new Point2D.Double(this.snapPoint.getCenterX(), this.snapPoint.getCenterY());
				}
				
				//------------------------------------------------------------------------
				
				//                   Protocol for drawing polygon
				
				//------------------------------------------------------------------------
				if(currentLayer.getLayerType().equals(Settings.POLYGON_GEOMETRY)) {
					
					//--------------------------------------------------------------------
					//                        HEXAGONS 
					//                   (FREE FORM POLYGON)
					//--------------------------------------------------------------------
					
					if(MainFrame.getCurrentFeatureType().equals("Hexagon")) {
	
						// 1. Create a vertex for the new point using the current snap size ( see Settings )
						// ---------------------------------------------------------------------------------
						Rectangle2D vertex = new Rectangle2D.Double(clickedPoint.getX() - (snapSize/2), clickedPoint.getY() - (snapSize/2), snapSize, snapSize);
						
						// 2. Add the vertex the the current list of vertex and the global snap points
						// ---------------------------------------------------------------------------------
						this.vertexList.add(vertex);
						this.globalDrawingSnapPoints.add(vertex);
						
						// 3. Render the vertex immediately
						// --------------------------------
						repaint();
						
						// 4. Procedure to show temporary polygon while drawing and close
						// --------------------------------------------------------------
						if(this.vertexList.size() > 1) {
							
							// 4.1 Show some message to the user
							// ----------------------------------
							MainFrame.log("Tip: Click the first point to finish shape");
							
							// 4.2 If at least 3 points have been drawn, show a temporary polygon
							// ------------------------------------------------------------------
							if(this.vertexList.size() > 2) {
								showTempPolygon(this.vertexList);
							}
							
							// 4.3 If the first point of the polygon was clicked
							// --------------------------------------------------------------------
							if(this.vertexList.get(0).getCenterX() == clickedPoint.getX() && this.vertexList.get(0).getCenterY() == clickedPoint.getY()) {
								
								// 4.3.1 Close the polygon and create a new feature
								finishPath(this.vertexList);
								
								// 4.3.2 Log some message
								String message = "Polygon feature created, click to save your edits";
								MainFrame.log(message);
								showAnimatedHint(message, Settings.DEFAULT_STATE_COLOR);
								
								// 4.3.3 Clean up the panel
								cleanUpDrawing();
							
								// 4.3.7 Update the panel
								repaint();
							}
						}
					}
					
					//--------------------------------------------------------------------
					//                        RECTANGLE 
					//                        (2 POINT)
					//--------------------------------------------------------------------
					
					else if (MainFrame.getCurrentFeatureType().equals("Rectangle")) {
						
						
						// 1. Create a vertex for the new point using the current snap size ( see Settings )
						// ---------------------------------------------------------------------------------
						Rectangle2D vertex = new Rectangle2D.Double(clickedPoint.getX() - (snapSize/2), clickedPoint.getY() - (snapSize/2), snapSize, snapSize);
						
						// 2. Add the vertex the the current list of vertex and the global snap points
						// ---------------------------------------------------------------------------------
						this.vertexList.add(vertex);
						this.globalDrawingSnapPoints.add(vertex);
						
						// 3. Render the vertex immediately
						// --------------------------------
						repaint();
						
						
						// Because the top left and bottom right vertix are needed to finish the rectangle
						// The list of vertex will be be only two
						// Guides will be handles by the mouse moved event
						
						if (this.vertexList.size() == 2) {
							
							// Save the layer
							Feature rectangle = new Feature(currentLayer.getNextID());
							
							Point2D b = new Point2D.Double(this.vertexList.get(0).getCenterX(), this.vertexList.get(0).getCenterY());
							Point2D m = clickedPoint;
							
							double x = m.getX();
							double y = m.getY();
							
							double width = Math.abs(m.getX() - b.getX());
							double height =  Math.abs(m.getY() - b.getY());
							
							if(b.getX() < m.getX()) {
								x = b.getX();
							}
							
							if(b.getY() < m.getY()) {
								y = b.getY();
							}
							
							rectangle.setShape(new Rectangle2D.Double(x, y, width, height));
							
							// 
							Rectangle2D topRight = new Rectangle2D.Double(	vertexList.get(1).getCenterX() - (snapSize / 2),
																			vertexList.get(0).getCenterY() - (snapSize / 2),
																			snapSize, snapSize);
							
							Rectangle2D bottomLeft = new Rectangle2D.Double(vertexList.get(0).getCenterX() - (snapSize / 2),
																			vertexList.get(1).getCenterY() - (snapSize / 2),
																			snapSize, snapSize);
							vertexList.add(1, topRight);
							vertexList.add(bottomLeft);
							
							rectangle.getVertices().addAll(vertexList);
							rectangle.setFeatureType("Rectangle");
							currentLayer.getListOfFeatures().add(rectangle);
							currentLayer.setNotSaved(true);
							
							// log messages ();
							String message = "New rectangle created";
							MainFrame.log(message);
							showAnimatedHint(message, Settings.FEATURE_CREATED_COLOR);
							
							cleanUpDrawing();
							repaint ();
						}
					} 
					
					//--------------------------------------------------------------------
					//                              CIRCLE 
					//                        (CENTER - RADIUS)
					//--------------------------------------------------------------------
					
					else if (MainFrame.getCurrentFeatureType().equals("Circle")) {
						
						// 1. Create a vertex for the new point using the current snap size ( see Settings )
						// ---------------------------------------------------------------------------------
						Rectangle2D vertex = new Rectangle2D.Double(clickedPoint.getX() - (snapSize/2), clickedPoint.getY() - (snapSize/2), snapSize, snapSize);
						
						// 2. Add the center point
						// ------------------------
						if(this.vertexList.isEmpty()) {
							vertexList.add(vertex);
						}
						
						// 3. If a center point have been added
						// -------------------------------------
						else if (this.vertexList.size() == 1) {
							
							// 3.1 Get circle parameters
							
							// (a) The center point will be the first point in the vertex list
							Point2D centerPoint = new Point2D.Double(this.vertexList.get(0).getCenterX(), this.vertexList.get(0).getCenterY());
							
							// (b) The radius of the circle will be the distance from the center point to the clicked point 
							double radius = (clickedPoint.distance(centerPoint));
							
							// 3.2 Construct circle with the parameters
							Shape circleShape = new Ellipse2D.Double(centerPoint.getX() - radius, centerPoint.getY() - radius , radius * 2, radius* 2);
							
							// 3.3 Create a new feature
							Feature circle = new Feature(currentLayer.getNextID());
							circle.setVertices(vertexList);
							circle.setFeatureType("Circle");
							circle.setEllipse(true, radius, radius);
							circle.setShape(circleShape);
							
							// 3.4 Add the to current layer list of features
							currentLayer.getListOfFeatures().add(circle);
							currentLayer.setNotSaved(true);
							
							// 3.5 Log some messages
							String message = "New Circle created";
							MainFrame.log(message);
							showAnimatedHint(message, Settings.FEATURE_CREATED_COLOR);
							
							// 3.6 Clean up the drawing
							cleanUpDrawing();
							repaint ();
							
						}	
					}
					
					//--------------------------------------------------------------------
					//                              TRIANGLE 
					//                         (3 POINT TRIANGLE)
					//--------------------------------------------------------------------
					
					else if (MainFrame.getCurrentFeatureType().equals("Triangle")) {
						
						System.out.println("Testing");
						
						// 1. Create a vertex for the new point using the current snap size ( see Settings )
						// ---------------------------------------------------------------------------------
						Rectangle2D vertex = new Rectangle2D.Double(clickedPoint.getX() - (snapSize/2), clickedPoint.getY() - (snapSize/2), snapSize, snapSize);
						
						
						if(this.vertexList.size() <= 1) {
							
							this.vertexList.add(vertex);
							repaint();
							
						}
						
						else if (this.vertexList.size() == 2) {
							
							this.vertexList.add(vertex);
							finishPath(this.vertexList);
							
							// 3.5 Log some messages
							String message = "Triangle created";
							MainFrame.log(message);
							showAnimatedHint(message, Settings.FEATURE_CREATED_COLOR);
							
							// 3.6 Clean up the drawing
							cleanUpDrawing();
							repaint ();
						}
						
					}
				}
				
				//------------------------------------------------------------------------
				//                   Protocol for drawing polyline
				//-----------------------------------------------------------------------
				else if(currentLayer.getLayerType().equals(Settings.POLYLINE_GEOMETRY)) {
					
					// 1. Create a vertex for the new point using the current snap size ( see Settings )
					// ---------------------------------------------------------------------------------
					Rectangle2D vertex = new Rectangle2D.Double(clickedPoint.getX() - (snapSize/2), clickedPoint.getY() - (snapSize/2), snapSize, snapSize);
					
					// 2. Current size of the drawn vertex
					// ------------------------------------
					int size = vertexList.size();
					
					// 3. For first two points drawn ...
					// ----------------------------------
					if(this.vertexList.size() < 2) {
						
						if(!SwingUtilities.isRightMouseButton(e)) {
							
							this.vertexList.add(vertex);
							this.globalDrawingSnapPoints.add(vertex);
						
						
							// 3. Draw a line 
							// ---------------
							if(vertexList.size() > 1) {
								
								Point2D start = new Point2D.Double(vertexList.get(size - 1).getCenterX(), vertexList.get(size - 1).getCenterY());
								Point2D end = new Point2D.Double(vertex.getCenterX(), vertex.getCenterY());
								
								this.tempLine.add(new Line2D.Double(start, end));
							}
							
							repaint();
							
						}
							
					} 
					
					// 4. When the vertex is of the line is more than two, need to start wacthing out for closing the line
					// ----------------------------------------------------------------------------------------------------
					else {
						
						// 4.1 Get the first and last point of the polyline
						// -------------------------------------------------
						Rectangle2D firstVertex = vertexList.get(0);
						Rectangle2D lastVertex = vertexList.get( this.vertexList.size() - 1 );
						boolean closed = false;
						
						// 4.2 Check if there is a click or double click on the first and last vertex then close the line
						// -----------------------------------------------------------------------------------------------
						if( (firstVertex.contains(clickedPoint) || lastVertex.contains(clickedPoint)) || e.getClickCount() > 1) {
							closed = true;
							
							// 4.3 Finish up the line and create a new feature
							finishPath(this.vertexList);
							
							// 4.3 Log some message
							String message = "Polyline feature created, click to save your edits";
							MainFrame.log(message);
							showAnimatedHint(message, Settings.FEATURE_CREATED_COLOR);
							
							// 4.4 Clean up the drawing panel
							cleanUpDrawing();
							
							// 4.3.7 Update the panel
							repaint();
							
						}
						
						// 4.3 If no click and the line is not closed, continue drawing line
						// -----------------------------------------------------------------
						if(!closed) {
							
							if(!SwingUtilities.isRightMouseButton(e)) {
								
								vertexList.add(vertex);
								this.globalDrawingSnapPoints.add(vertex);
								
								Point2D start = new Point2D.Double(vertexList.get(size - 1).getCenterX(), vertexList.get(size - 1).getCenterY());
								Point2D end = new Point2D.Double(vertex.getCenterX(), vertex.getCenterY());
								
								this.tempLine.add(new Line2D.Double(start, end));
								
								repaint();
							}
						}	
					}
				}
				
			} else {
				
				showAnimatedHint("No shape have been selected", Settings.HIGHLIGHTED_STATE_COLOR);
				MainFrame.log("No shape have been selected");
			}
				
		} else {
			MainFrame.log("Drawing attempted but edit session is off");	
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	
		findSnaps(e);
	
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

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
