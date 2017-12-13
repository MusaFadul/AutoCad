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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import core_classes.Feature;
import core_classes.Layer;
import custom_components.CustomJPanel;
import features.GridLine;
import features.TextItem;
import geometry.PointItem;
import geometry.PolygonItem;
import tester.MainFrame;
import toolset.Tools;
import application_frames.Settings;

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
	public boolean editModeIsOn = false;
	
	/**Selection mode*/
	public boolean queryModeIsOn = false;
	
	/**Drawing grid lines*/
	private List<GridLine> gridLines = new ArrayList<GridLine>();;
	
	/**Snap points of the grid*/
	private List<Rectangle2D> gridSnapPoints = new ArrayList<Rectangle2D>();
	
	/**List of points when mouse is clicked, cleared during new edit session*/
	private List<Rectangle2D> vertexList = new ArrayList<Rectangle2D>(); 
	
	/**List of points when mouse is dragged, cleared during new selection session*/
	private List<Point2D> draggedPoints = new ArrayList<Point2D>(); 
	
	/**Snapped point when mouse is clicked*/
	private Rectangle2D snapPoint = null;
	
	/**Current layer to save drawn items*/
	public static Layer currentLayer;
	
	/**Feature type, circle, rectangle etc*/
	public static String currentFeatureType = "";

	/**Temporary Shapes*/
	private Shape tempShape = null;
	
	/**Temporary Shapes*/
	public Rectangle2D queryBounds = null;
	
	/**Temporary line*/
	private List<Line2D> tempLine = new ArrayList<Line2D>();;
	
	/**Global drawing snap points*/
	private List<Rectangle2D> globalDrawingSnapPoints = new ArrayList<Rectangle2D>();
	
	/**Moving tool tips*/
	private TextItem tooltip = null;
	
	/**Draw guide such as angle, distance*/
	private TextItem drawGuide;
	
	/**Moving hint*/
	private static TextItem movingHint = null;
		
	/**Animator timers*/
	private static Timer timer, fadetimer;
	
	/**Animator counter*/
	private static int animatorTime;
	
	/**Graphics object used to paint, disposed off each time*/
	private Graphics2D g2d;

	/**Graphics font metrics for getting graphic details of strings to*/
	private FontMetrics g2dFontMetrics;
	
	/** Tip following the mouse poistion*/
	private TextItem movingMouseTip;
	
	/** Text of the mouse tip*/
	private String currentMouseTipText = Settings.DEFAULT_MOUSE_TIP;
	

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
		g2d = (Graphics2D) g.create();
		
		// General rendering, order matters !
		
		try {
			
			// 0. Set drawing pen settings to default
			// --------------------------------------
			g2d.setStroke(new BasicStroke(1));
			g2d.setColor(Settings.DEFAULT_LAYER_COLOR);
			g2d.setFont(new Font("Tw Cen MT", Font.ITALIC, 18)); 
			g2dFontMetrics = g2d.getFontMetrics();
			
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
						
						if(feature.isVisibile()) {
						
							if(feature.isHighlighted()) {
								c = Settings.FEATURE_HIGHLIGHTED_STATE_COLOR;
							} 
							
							else if (!feature.isHighlighted()) {
								c = layer.getLayerColor();
							}
							
							if(layer.getLayerType().equals(Settings.POINT_GEOMETRY)){
								
								PointItem point = (PointItem) feature;
								g2d.setColor(c);
								g2d.fill(point.getShape());
								
							}
							
							else {
							
								if(layer.getLayerType().equals(Settings.POLYGON_GEOMETRY)) {
									// Fill the shape
									g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
									g2d.fill(feature.getShape());
								}
								
								// Draw outline
								g2d.setColor(c);
								g2d.setStroke(new BasicStroke(layer.getLineWeight()));
								g2d.draw(feature.getShape());
								
								
								
								// Render the shape vertices
								for(Shape shape : feature.getVertices()) {
									g2d.setColor(c);
									g2d.fill(shape);
								}
							}
						}
					}
				}
			}
			
			// 3. Render temporary poly lines
			// -------------------------------
			for(Line2D line : this.tempLine) {
				g2d.setStroke(new BasicStroke(currentLayer.getLineWeight()));
				g2d.setColor(currentLayer.getLayerColor());
				g2d.draw(line);
			}
			
			// 3. Render temporary shapes drawn on current layer
			if(tempShape != null) {
				
				Color c = currentLayer.getLayerColor();
				Stroke stroke = new BasicStroke(currentLayer.getLineWeight());
				
				g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
				
				if(currentLayer.getLayerType().equals(Settings.POLYGON_GEOMETRY)) {
					// Fill the shape
					g2d.fill(tempShape);
				}
				
				if(currentLayer.getLayerType().equals(Settings.POLYLINE_GEOMETRY)){
					stroke = new BasicStroke(currentLayer.getLineWeight(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
				}
				
				g2d.setStroke(stroke);
				g2d.setColor(c);
				g2d.draw(tempShape);
				
			}
			
			// 4. Render selection bounds
			if(this.queryBounds != null ) {
				
				Color c = Settings.DEFAULT_SELECTION_COLOR;
				
				g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Settings.TRANSPARENCY_LEVEL_2));
				g2d.fill(queryBounds);
				
				// Draw border
				g2d.setStroke(new BasicStroke(3));
				g2d.setColor(c);
				g2d.draw(queryBounds);
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
	            Rectangle2D rect = g2dFontMetrics.getStringBounds(tooltip.getText(), g2d); 	
	            Color c = Settings.DEFAULT_STATE_COLOR;
	            g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Settings.TRANSPARENCY_LEVEL_1));									
	            g2d.fillRect((int)tooltip.getBasePosition().getX(),
	            		(int)tooltip.getBasePosition().getY() - g2dFontMetrics.getAscent(),
	                       (int) rect.getWidth(),
	                       (int) rect.getHeight());
	            
	            
	            g2d.setColor(Color.WHITE);							
	            g2d.drawString(tooltip.getText(), (int)tooltip.getBasePosition().getX(), (int)tooltip.getBasePosition().getY());
			
			}
			
			// 7. Render Hint
			// ------------------------------------------
			if(movingHint != null ) {
				
	            Rectangle2D rect = g2dFontMetrics.getStringBounds(movingHint.getText(), g2d); 	
	            //Color c = Settings.DEFAULT_STATE_COLOR;
	            g2d.setColor(movingHint.getColor());									
	            g2d.fillRect((int) (getWidth() - rect.getWidth() - 60),
	            		(int)movingHint.getBasePosition().getY() - 10 - g2dFontMetrics.getAscent(),
	                       (int) rect.getWidth() + 20,
	                       (int) rect.getHeight() + 20);
	            
	            
	            g2d.setColor(Color.WHITE);							
	            g2d.drawString(movingHint.getText(), (int) (getWidth() - rect.getWidth() - 50) , (int)movingHint.getBasePosition().getY());
			
			}
			
			// 8. Render rectangle bounds
			
			
			// 8. Render draw guides
			// ---------------------
			if(this.drawGuide!=null) {
				
				setCurrentMouseGuide(drawGuide, Settings.DEFAULT_STATE_COLOR);
			}
			if(this.movingMouseTip!=null) {
				
				setCurrentMouseGuide(movingMouseTip, Color.BLACK);
			}
			
		} catch (Exception e) {
			
			System.out.println(e.getMessage());
			
			e.printStackTrace();
	
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
			
		} else if (this.gridLines.isEmpty()) {
			
			this.gridIsOn = true;
			renderGrid(Settings.gridSizeMM);
			
		}
		
		repaint();
	}
	
	/**
	 * Turns snapping on and off
	 */
	public void toggleSnap() {
		
		if(snappingModeIsOn) {
			
			snappingModeIsOn = false;
			this.snapPoint = null;
		} 
		
		else {
			
			snappingModeIsOn = true;
		}
		
		repaint();
	}
	
	/**
	 * Toggles the edit session 
	 * @param layerIndex the index of the layer on the table of contents or from the combo box
	 * @param featureType the current feature type to draw
	 * @param signal specify not to toggle edit session but to change current editing layer by using "continue"
	 */
	public void toggleEditSession(int layerIndex, String featureType, String signal) {
		
		// Change the current editing layer and feature type
		disableQueryMode();
		changeCurrentLayerAndFeatureType(layerIndex, featureType);
		
		if(signal == null) {
			signal = "";
		}
		
		// Only turn off or on when the signal is a new edit session
		if(!signal.equals(Settings.DRAW_CONTINUE)) {
			
			cleanUpDrawing();
			
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
	 * Toogles query mode
	 */
	public void toggleSelectionMode() {
		
		abandonEditSession();
		
		if( queryModeIsOn ) {
			
			queryModeIsOn = false;

			//MainFrame.btnQuery.setBackground(Settings.DEFAULT_STATE_COLOR);
			//MainFrame.disableAllDrawButtons();
			
		} 
		else {

			queryModeIsOn = true;
			MainFrame.btnQuery.setBackground(Settings.HIGHLIGHTED_STATE_COLOR);
			MainFrame.disableAllDrawButtons();
		}
	}

	/**
	 * Disable selection mode
	 */
	public void disableQueryMode() {
		
		queryModeIsOn = false;
		
		MainFrame.btnQuery.setButtonReleased(false);
		MainFrame.btnQuery.setBackground(Settings.DEFAULT_STATE_COLOR);
		
		cleanUpDrawing();
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
	/**
	 * Helps the paint component to compute rendering and conflict calculations for mouse guides <br>
	 * The repaint method should be called wherever this method is used
	 * @param guideOrTip
	 * @param color
	 */
	private void setCurrentMouseGuide(TextItem guideOrTip, Color color) {
		
		if(guideOrTip != null) {
			
			g2d.setFont(new Font("Tw Cen MT", Font.ITALIC, 15)); 
			FontMetrics fm = g2d.getFontMetrics();
			Rectangle2D rect = fm.getStringBounds(guideOrTip.getText(), g2d); 
			
			if(guideOrTip.getBasePosition().getX() < 0) {
				guideOrTip.setBasePosition(new Point2D.Double(getMousePosition().getX() + Settings.mouseOffset, getMousePosition().getY()));
			}
			
			if(guideOrTip.getBasePosition().getX() + rect.getWidth() > getWidth()) {
				guideOrTip.setBasePosition(new Point2D.Double(getMousePosition().getX() - Settings.mouseOffset - rect.getWidth(), getMousePosition().getY()));
			}
			
			int padding = Settings.TOOL_TIP_PADDING;
			g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), Settings.TRANSPARENCY_LEVEL_1));
			
			RoundRectangle2D roundedRect = getRoundedFrameRectForText(guideOrTip, fm, padding);
			guideOrTip.setBorderRectangleInPanel(roundedRect);
			
			g2d.fill(roundedRect);
			
			g2d.setColor(Color.WHITE);							
			g2d.drawString(guideOrTip.getText(), (int) (int)guideOrTip.getBasePosition().getX() + padding/2, (int)guideOrTip.getBasePosition().getY()- padding/2);
		}
	}
	
	/**
	 * Supplies rounded edges rectangle for mouse guides and tips
	 * @param drawGuide
	 * @param fm computed font metrics of the string
	 * @param padding padding
	 * @return rounded edges rectangle for mouse guides and tips
	 */
	private RoundRectangle2D getRoundedFrameRectForText(TextItem drawGuide, FontMetrics fm, int padding) {
		
		Rectangle2D rect = fm.getStringBounds(drawGuide.getText(), g2d); 
		
		return new RoundRectangle2D.Double(
				
				(int)drawGuide.getBasePosition().getX() - padding,
	    		(int)drawGuide.getBasePosition().getY() - fm.getAscent() - padding,
	               (int) rect.getWidth() + padding*2,
	               (int) rect.getHeight() + padding, padding + 2, padding + 2
				) ;
				
	}
	
	/**
	 * Changes the current layer, needed at each new edit session.
	 * It finds the layer with same ID at the table of contents
	 * @param layerIndex layer index from the combom box the mainframe
	 * @param featureType the feature type selected e.g circle, rectangle
	 */
	private void changeCurrentLayerAndFeatureType(int layerIndex, String featureType) {
		
		int layerID = (int) MainFrame.tableOfContents.getModel().getValueAt(layerIndex, TableOfContents.LAYER_ID_COL_INDEX);
		currentLayer = TableOfContents.findLayerWithID(layerID);
		currentFeatureType = featureType;
	}
	
	/**
	 * Handles drawing rectangle
	 * @param e mouse moved event object
	 */
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
			
			this.tempShape = getRectangleShape(b, m);
			
			repaint();
		
		} 
		
	}
	
	/**
	 * Computes a rectangle shaoe from two points, it gets neccesary when drawing rectangles
	 * with mouse dragged, as the last point may not be at the right side of the first point
	 * @param b base point
	 * @param m mouse (new) point
	 * @return rectangle shape
	 */
	private Shape getRectangleShape(Point2D b, Point2D m) {
		
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
		
		Shape rec = new Rectangle2D.Double(x, y, width, height);

		return rec;
	}
	
	/**
	 * Handles drawing cirlce
	 * @param e mouse moved event object
	 */
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
	 * Handles drawing triangle
	 * @param e mouse moved event object
	 */
	private void handleDrawingTriangle(MouseEvent e) {

		Path2D path = new Path2D.Double();
		
		if(this.vertexList.size() == 2) {
			
			path.moveTo(vertexList.get(0).getCenterX(), vertexList.get(0).getCenterY());
			
			path.lineTo(vertexList.get(1).getCenterX(), vertexList.get(1).getCenterY());
			
			path.lineTo(e.getPoint().getX(), e.getPoint().getY());
			
			path.closePath();
			
			this.tempShape = path;
			
			this.currentMouseTipText = "Specify third point";
			
			repaint();
		}
	}

	/**
	 * Closing protocol for drawing polygons
	 * For situation where the snap or grid is turned off and user needs to close a feature
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
							this.currentMouseTipText = "Close now!";
							repaint();
							
						} else {
							
							// 1.5 Erase the snap and the tool tip if mouse goes away
							// ------------------------------------------------
							this.snapPoint = null;
							this.currentMouseTipText = Settings.CLOSE_POLYGON_MESSAGE;;
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
					if( firstVertex.contains(e.getPoint())) {
						
						// 2.3 Show some tips
						// ------------------------------------------------
						this.currentMouseTipText  = Settings.CLOSE_POLYLINE_MESSAGE;
						
					} 
					else if ( lastVertex.contains(e.getPoint()) ) {
						
						this.currentMouseTipText  = Settings.FINISH_POLYLINE_MESSAGE;
					} 
					else {
						
						// 2.4 Erase the snap and the tool tip if mouse goes away
						// ------------------------------------------------------
						//this.snapPoint = null; <- Not neccessary to erase the snap point!
						this.tooltip = null;	
					}
					
					repaint();
				}
			}
		}
	}

	/**
	 * Handles drawing polyline
	 * @param e mouse moved event object
	 */
	private void handleDrawingPolyline(MouseEvent e) {
	
		if(vertexList.size() >= 1) {
			
			// Draw a moving line from the base point
			Point2D base = new Point2D.Double(vertexList.get(vertexList.size()-1).getCenterX(), vertexList.get(vertexList.size()-1).getCenterY());
			Shape movingLine = new Line2D.Double(base.getX(), base.getY(), e.getX(), e.getY());
			
			this.tempShape = movingLine;
			
			repaint();
		}
	}
	
	/**
	 * Handles drawing ellipse
	 * @param e mouse moved event object
	 */
	private void handleDrawingEllipse(MouseEvent e) {
	
		if(vertexList.size() == 1) {
			
			this.currentMouseTipText = "Specify major axis ";
			repaint();
			
			// Ellipse parameters
			double centerX = vertexList.get(0).getCenterX();
			double centerY = vertexList.get(0).getCenterY();
			double diamX = Math.abs(centerX - e.getX()) * 2;
			double diamY = Math.abs(centerY - e.getY()) * 2;
			
			tempShape = new Ellipse2D.Double(centerX - diamX / 2, centerY - diamY / 2, diamX, diamY);
			repaint();
				
		}
		
		if(vertexList.size() == 2) {
			
			this.currentMouseTipText = "Specify minor axis ";
			repaint();
			
			// Ellipse parameters
			double centerX = vertexList.get(0).getCenterX();
			double centerY = vertexList.get(0).getCenterY();
			double diamX = Math.abs(centerX - vertexList.get(1).getCenterX()) * 2;
			double diamY = Math.abs(centerY - e.getY()) * 2;
			
			tempShape = new Ellipse2D.Double(centerX - diamX / 2, centerY - diamY / 2, diamX, diamY);
			repaint();
		}	
	}
	
	/**
	 * Handles temporary drawwing on the panel before features are created
	 * @param e mouse moved event object
	 */
	private void handleMouseMovedDrawing(MouseEvent e) {
		
		
		if(editModeIsOn) {
			
			if( MainFrame.getCurrentFeatureType() != null ) {
				
				// Drawing guides
				
				if(Settings.DRAW_GUIDES_AND_TIPS == true) {
					
					if(this.vertexList.size() > 0) {
						getDrawDetailsToNewPoint(e.getPoint());
						getMouseToolTip(e.getPoint());
					}	
				}
	
				// Handling Polyline 
				if(currentLayer.getLayerType().equals(Settings.POLYLINE_GEOMETRY)) {
					this.handleDrawingPolyline(e);
				}
				
				// Handling Rectangle 
				if(currentLayer.getLayerType().equals(Settings.POLYGON_GEOMETRY) && MainFrame.getCurrentFeatureType().equals("Ellipse")) {
					this.handleDrawingEllipse(e);
				}
					
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
	 * Handles right click 
	 */
	private void handleDrawUndoIntent() {
		
		if(this.vertexList.size() > 1) {
			
			vertexList.remove(vertexList.size() - 1);
			
			if(currentLayer.getLayerType().equals(Settings.POLYLINE_GEOMETRY)) {
				
				tempLine.remove(tempLine.size() - 1);
			}
			
			if(currentLayer.getLayerType().equals(Settings.POLYGON_GEOMETRY)) {
				
				showTempPolygon(vertexList);
			}
			
			repaint();
		}
		
		if(this.vertexList.size() == 1) {
			
			this.vertexList.clear();
			
			cleanUpDrawing();
			
			repaint();
		}
		
		if(currentLayer.getLayerType().equals(Settings.POINT_GEOMETRY)) {
			
			currentLayer.removeLastItem();
		}
		
	}

	/**
	 * Higlights features that are interseced by the query region
	 * @param e Mouse event at mouse realeased
	 * @param queryBounds bounds of the rectangle
	 */
	private void highlightIntersectedFeatures( Rectangle2D queryBounds ) {
		
		// 1. Loop through all layer at the table of contents
		for(Layer layer : TableOfContents.layerList) {
			// 2. Consider only layers that are visibile 
			if(layer.isVisible()) {
				// 3. Loop through all features in a layer
				for(Feature feature : layer.getListOfFeatures()) {
					// 4. Consider only features that are visible
					if(feature.isVisibile()) {
						// 4.1 Make an area of the query bounds
						Area areaA = new Area(queryBounds);
						// 4.2 Get the shape of the feature
						//     and intersects it with the feature area object
						//     just created
						areaA.intersect(new Area(feature.getShape()));
						
						// 4.3 If the area is not empty,
						//     That means there is an intersect
						if(!areaA.isEmpty()) {
							// Therefore highlight the feature
							feature.setHighlighted(true);
							// However, there will be a problem for point and line objects
							// Even if there are intersection, it (may) not find it
						} 
						// 4.4 Let us test for polyline and points
						else {
							boolean stillDoNotContain = true;
							// 4.4.1 For polyline
							if(layer.getLayerType().equals(Settings.POLYLINE_GEOMETRY)) {
								// Loop through all the vertices
								for(Rectangle2D rec : feature.getVertices()) {
									// Create a point
									Point2D point = new Point2D.Double(rec.getCenterX(), rec.getCenterY());
									// Test for the intersection of one point
									if(queryBounds.contains(point)) {
										// If it contains, that means there is an 
										// intersection , then highlight the feature
										feature.setHighlighted(true);
										stillDoNotContain = false;
										// stop searching!, we got what we wanted already
										break;
									}
								}
							} 
							// 4.4.2 For point
							if (layer.getLayerType().equals(Settings.POINT_GEOMETRY)) {
								
								// Since we know the item is a point already :)
								// We can cast it to a point item , 
								// we need the getGeom method in the class
								PointItem point = (PointItem) feature;
								// Test for intersection
								if(queryBounds.contains(point.getGeometry())) {
									feature.setHighlighted(true);
									// turn of the clause
									stillDoNotContain = false;
								}
							}
							// If after all the test, and the feature is still not 
							// within the query box, then no need to highlight
							if(stillDoNotContain) {
								feature.setHighlighted(false);
							}
						}
						
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
		} else {
			tempShape = null;
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
		PolygonItem featurePolygon = new PolygonItem(currentLayer.getNextFeatureID(), path);
		
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
	
	/**
	 * General handler for updating the message on the mouse tool tip.
	 * Can be disabled at the settings. <br>
	 * This is done at every mouse move event. <br>
	 * The message can be changed or controlled by merely updating the global currentMouseTip variable
	 * @param mousePoint Current mouse event point
	 */
	private void getMouseToolTip(Point mousePoint) {
		
		// Determine the text from the global currentMouseTip variable
		String mouseTip = this.currentMouseTipText;
		
		// Get the font metrics of the text
		Rectangle2D rect = g2dFontMetrics.getStringBounds(mouseTip, g2d);
		
		// Determine the last vertex
		Rectangle2D lastVertex = this.vertexList.get(vertexList.size()-1);
		
		// Determine the base position of the mouse point by adding a mouse offset
		Point2D basePosition = new Point2D.Double(mousePoint.getX() + Settings.mouseOffset, mousePoint.getY());
		
		// Get the current mouse position
		// Tool tip postion will be placed at the right hand side except if
		// the current mouse postion is at the left of the lastVertex
		// Therefore the tip base postion will be shifted based on the text's graphics width
		if(mousePoint.getX() < lastVertex.getCenterX()) {
			basePosition.setLocation(new Point2D.Double(basePosition.getX() - rect.getWidth(), basePosition.getY()));
		}
		
		// Create the text item
		this.movingMouseTip = new TextItem(basePosition, mouseTip);
		
		repaint();
		
	}

	/**
	 * Shows tips such as length, angle from a point to another point <br>
	 * Detects conflict between the mouse tool tip as well <br>
	 * Refuses to draw if such conflict exists <br>
	 * @param point
	 */
	private void getDrawDetailsToNewPoint(Point mousePoint) {
		
		// 1. Determine the base point
		// This is the last point on the vertex list
		Point2D base = new Point2D.Double(
				this.vertexList.get(vertexList.size()-1).getCenterX(),
				this.vertexList.get(vertexList.size()-1).getCenterY());
		
		// Create a line from the base point to the moouse point
		Line2D line = new Line2D.Double(base.getX(), base.getY(), mousePoint.getX(), mousePoint.getY());
		
		// Calculate length of the line
		double lineDPI =  base.distance(mousePoint);
		
		// Position the tool tip at the center of the line just created
		Point2D.Double toolTipPosition = Tools.interpolationByDistance(line, (int) (lineDPI/2));
		
		// Compute line length with the current DPI settings
		String lineLength = String.valueOf((int) (lineDPI * 25.4f / 72)) + " mm";
		
		// Get the angle
		int angle = (int) Tools.getAngle(base, mousePoint);
		
		// Check for conflict with the mouse tool tip
		
		boolean conflict = false;
		
		if(this.drawGuide != null && this.movingMouseTip != null) {
			
			try {
				conflict = (movingMouseTip.borderIntersectsAnotherRectangle(drawGuide.getBorderRectangleInPanel().getBounds2D()));
			}
			catch(NullPointerException e) {
				MainFrame.log("The guides were not found, ignoring finding conflict");
			}
			
		}
		
		if(!conflict) {
			
			// Create a draw guide showing the length and the angle 
			this.drawGuide = new TextItem(toolTipPosition, lineLength + " " + angle + "\u00b0");
			
			repaint();
			
		} else {
			
			this.drawGuide = null;
			repaint();
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
	 * Cleans up the drawing. 
	 * Panel repainted authomatically.
	 */
	public void cleanUpDrawing() {
		
		// Clear the vertex list for new polygon
		this.vertexList.clear();
		
		// Remove any tool tip on the drawing
		this.tooltip = null;
		
		this.movingMouseTip = null;
		
		// Remove the current snap
		this.snapPoint = null;
		
		// Remove temp lines
		this.tempLine = new ArrayList<Line2D>();
		
		// Remove temp shape
		this.tempShape = null;
		
		// Remove selection
		this.queryBounds = null;
		
		// Draw guides
		this.drawGuide = null;
		
		for(Layer layer : TableOfContents.layerList) {
			layer.highlightAllFeatures(false);
		}
		
		MainFrame.updateDrawButtonGroup();
		
		repaint();
	}

	/**
	 * When a feature has been created, log some message, repaint and and clean up the drawing
	 * @param message
	 */
	private void onFeatureCreated(String message) {
	
		// Log some message
		MainFrame.log(message);
		showAnimatedHint(message, Settings.FEATURE_CREATED_COLOR);
		
		// Clean up the panel
		cleanUpDrawing();
	
		// Update the panel
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
				
				Rectangle2D vertex = new Rectangle2D.Double(clickedPoint.getX() - (snapSize/2), clickedPoint.getY() - (snapSize/2), snapSize, snapSize);
				
				
				if(SwingUtilities.isRightMouseButton(e)) {
					
					handleDrawUndoIntent();
				}
				
				//------------------------------------------------------------------------
				
				//                   Protocol for drawing point
				
				//------------------------------------------------------------------------
				
				else if(currentLayer.getLayerType().equals(Settings.POINT_GEOMETRY)) {
					
					Feature point  = new PointItem(currentLayer.getNextFeatureID(), clickedPoint);
					point.setFeatureType(Settings.POINT_GEOMETRY);
					point.setLayerID(currentLayer.getId());
					point.setShape(new Ellipse2D.Double(clickedPoint.getX() - Settings.POINT_SIZE/2,
							clickedPoint.getY() - Settings.POINT_SIZE/2,
							Settings.POINT_SIZE, Settings.POINT_SIZE));
					currentLayer.getListOfFeatures().add(point);
					repaint();
				}
				
				//------------------------------------------------------------------------
				
				//                   Protocol for drawing polygon
				
				//------------------------------------------------------------------------
				else if(currentLayer.getLayerType().equals(Settings.POLYGON_GEOMETRY)) {
					
					
					if(!SwingUtilities.isRightMouseButton(e)) {
						
						//--------------------------------------------------------------------
						//                        HEXAGONS 
						//                   (FREE FORM POLYGON)
						//--------------------------------------------------------------------
						if(MainFrame.getCurrentFeatureType().equals("Ellipse")) {
							
							if(this.vertexList.size() < 3) {
								
								// For the major axis
								// Snap the vertex to the Y of the center point keep the X of the mouse point
								if(this.vertexList.size() == 1) {
									vertex = new Rectangle2D.Double(clickedPoint.getX() - (snapSize/2), vertexList.get(0).getCenterY() - (snapSize/2), snapSize, snapSize);
									vertexList.add(vertex);
								}
								// For the minor axis
								// Snap the vertex to the X of the center point keep the Y of the mouse point
								else if (this.vertexList.size() == 2) {
									vertex = new Rectangle2D.Double(vertexList.get(0).getCenterX() - (snapSize/2), clickedPoint.getY() - (snapSize/2), snapSize, snapSize);
									vertexList.add(vertex);
								} else 
									// For the center point
									vertexList.add(vertex);
							}
							
							// If the total size of the vertex is equals to 3
							// Time to finish up the ellipse
							if(this.vertexList.size() == 3) {
	
								// Get the ellipse parameters
								// -------------------------------
								
								// Center will be the first point in the list, at the index of 0
								double centerX = vertexList.get(0).getCenterX();
								double centerY = vertexList.get(0).getCenterY();
								
								// The diameter at X will be the absolute (linear) distance from the centerX to the first point X
								double diamX = Math.abs(centerX - vertexList.get(1).getCenterX()) * 2;
								
								// The diameter at Y will be the absolute (linear) distance from the centerY to the last point Y
								double diamY = Math.abs(centerY - vertexList.get(2).getCenterY()) * 2;
								
								// Create a shape with the parameters
								Shape shape = new Ellipse2D.Double(centerX - diamX / 2, centerY - diamY / 2, diamX, diamY);
								
								// Create new feature in current layer and set all attributes
								Feature ellipse = new Feature(currentLayer.getNextFeatureID());
								ellipse.setLayerID(currentLayer.getId());
								ellipse.setEllipse(true, diamX / 2, diamY / 2);
								ellipse.setShape(shape);
								ellipse.setFeatureType("Ellipse");
								ellipse.setVertices(vertexList);
								
								// Add to the current layer's feature list
								currentLayer.getListOfFeatures().add(ellipse);
								currentLayer.setNotSaved(true);
								
								onFeatureCreated("New ellipse created");
							}
						}
						
						else if(MainFrame.getCurrentFeatureType().equals("Hexagon")) {
		
							// 1. Create a vertex for the new point using the current snap size ( see Settings )
							// ---------------------------------------------------------------------------------
							
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
								MainFrame.log("Tip: " + Settings.CLOSE_POLYGON_MESSAGE);
								this.currentMouseTipText = Settings.CLOSE_POLYGON_MESSAGE;
								repaint();
								
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
									
									onFeatureCreated("Polygon feature created, click to save your edits");
									
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
							
							// 2. Add the vertex the the current list of vertex and the global snap points
							// ---------------------------------------------------------------------------------
							this.vertexList.add(vertex);
							this.globalDrawingSnapPoints.add(vertex);
							
							// 3. Render the vertex immediately
							// --------------------------------
							this.currentMouseTipText = "Click to finish rectangle";
							repaint();
							
							
							// Because the top left and bottom right vertix are needed to finish the rectangle
							// The list of vertex will be be only two
							// Guides will be handles by the mouse moved event
							
							if (this.vertexList.size() == 2) {
								
								// Save the layer
								Feature rectangle = new Feature(currentLayer.getNextFeatureID());
								
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
								rectangle.setLayerID(currentLayer.getId());
								currentLayer.getListOfFeatures().add(rectangle);
								currentLayer.setNotSaved(true);
								
								// log messages ();
								onFeatureCreated("New rectangle created");
							}
						} 
						
						//--------------------------------------------------------------------
						//                              CIRCLE 
						//                        (CENTER - RADIUS)
						//--------------------------------------------------------------------
						
						else if (MainFrame.getCurrentFeatureType().equals("Circle")) {
							
							// 1. Create a vertex for the new point using the current snap size ( see Settings )
							// ---------------------------------------------------------------------------------
							
							// 2. Add the center point
							// ------------------------
							if(this.vertexList.isEmpty()) {
								vertexList.add(vertex);
								this.currentMouseTipText = "Specify radius";
								repaint();
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
								Feature circle = new Feature(currentLayer.getNextFeatureID());
								circle.setLayerID(currentLayer.getId());
								circle.setVertices(vertexList);
								circle.setFeatureType("Circle");
								circle.setEllipse(true, radius, radius);
								circle.setShape(circleShape);
								
								// 3.4 Add the to current layer list of features
								currentLayer.getListOfFeatures().add(circle);
								currentLayer.setNotSaved(true);
								
								// 3.5 Log some messages
								onFeatureCreated("New Circle created");
								
							}	
						}
						
						//--------------------------------------------------------------------
						//                              TRIANGLE 
						//                         (3 POINT TRIANGLE)
						//--------------------------------------------------------------------
						
						else if (MainFrame.getCurrentFeatureType().equals("Triangle")) {
							
							// 1. Create a vertex for the new point using the current snap size ( see Settings )
							// ---------------------------------------------------------------------------------
							
							if(this.vertexList.size() <= 1) {
								
								this.vertexList.add(vertex);
								this.currentMouseTipText = "Specify second point";
								repaint();
								
							}
							
							else if (this.vertexList.size() == 2) {
								
								this.vertexList.add(vertex);
								
								
								finishPath(this.vertexList);
								
								// 3.5 Log some messages
								onFeatureCreated("Triangle created");
							}
							
						}
					}
				}
				
				//------------------------------------------------------------------------
				//                   Protocol for drawing polyline
				//-----------------------------------------------------------------------
				else if(currentLayer.getLayerType().equals(Settings.POLYLINE_GEOMETRY)) {
					
					// 0. Create a vertex for the new point using the current snap size ( see Settings )
					// ---------------------------------------------------------------------------------
					
					// 1. Current size of the drawn vertex
					// ------------------------------------
					int size = vertexList.size();
					
					// 2. For first two points drawn ...
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
						if( (firstVertex.contains(clickedPoint) && e.getClickCount() > 1) ) {
							closed = true;
							
							// 4.3 Finish up the line and create a new feature
							finishPath(this.vertexList);
							
							// 4.3 Log some message and update
							onFeatureCreated("Polyline feature created");
							
						}
						
						if( (lastVertex.contains(clickedPoint)) && e.getClickCount() > 1){
							
							closed = true;
							
							vertexList.add(vertex);
							this.globalDrawingSnapPoints.add(vertex);
							
							// 4.3 Finish up the line and create a new feature
							finishPath(this.vertexList);
							
							// 4.3 Log some message and update
							onFeatureCreated("Polyline feature created");
							
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
	
		handleMouseMovedDrawing(e);
		
		if( queryModeIsOn ) {
			
			movingMouseTip = new TextItem(e.getPoint(), "Draw bounds to select features");
			
			setCurrentMouseGuide(movingMouseTip, Settings.DEFAULT_LAYER_COLOR);
			
			repaint();
		}
	
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
		this.movingMouseTip = null;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
		
		
		if( queryModeIsOn ) {
			
			/*for(Layer layer : TableOfContents.layerList) {
				layer.highlightAllFeatures(false);
			}*/
			
			if(this.draggedPoints.size() > 1) {
				cleanUpDrawing();
			}
			
			this.draggedPoints.add(e.getPoint());
			
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
		if( queryModeIsOn ) {
			
			if( this.queryBounds != null ) {
				
				highlightIntersectedFeatures(this.queryBounds);
			}
		}
		
		this.draggedPoints.clear();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {

		if( queryModeIsOn ) {
			
			// The dragged points will be clear at the mouse released
			// So, for showing the rectangle as user drags, only one point is needed 
			// on the list, for a new selection
			
			if(this.draggedPoints.size() == 1) {
				
				// Compute a rectangle with the point and the current  mouse position
				this.queryBounds = (Rectangle2D) getRectangleShape(this.draggedPoints.get(0), e.getPoint());
				
				// Create a moving mouse tip
				movingMouseTip = new TextItem(e.getPoint(), "Draw bounds to select features");
				
				// Set the mouse tip
				setCurrentMouseGuide(movingMouseTip, Settings.DEFAULT_LAYER_COLOR);
				
				// Repaint
				repaint();
			}
		}
	}
}
