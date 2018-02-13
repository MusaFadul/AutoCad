/**
 * 
 */
package core_components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
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

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import core_classes.Feature;
import core_classes.Layer;
import effects.GridLine;
import effects.TextItem;
import features.PointItem;
import features.PolygonItem;
import features.PolylineItem;
import toolset.Tools;
import application_frames.MainFrame;
import application_frames.SettingsFrame;

/**
 * Panel for drawing items.
 * 
 * Supports simple drawing guides, such as snap and grid.
 * 
 * @author Olumide Igbiloba
 * @since Dec 7, 2017
 * @version
 * a. Dec 29, 2017 : Added functionality for editing the vertices of a feature
 * b. Dec 31, 2017 : Added functionality for dragging/ moving a point feature
 */
public class DrawingJPanel extends JPanel implements MouseMotionListener, MouseListener {


	private static final long serialVersionUID = -6318145875906198958L;
	
	/**Size of snaps*/
	private int snapSize = SettingsFrame.SNAP_SIZE;
	
	/**Snapping mode*/
	public boolean snappingModeIsOn = false;
	
	public boolean displayVertices = true;
	
	/**Grid state*/
	public boolean gridIsOn = false;
	
	/**Editing mode*/
	public boolean editModeIsOn = false;
	
	/**Selection mode*/
	public boolean selectionModeIsOn = false;
	
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
	
	/**List of vertex when mouse is dragged, cleared during new selection session*/
	private List<Rectangle2D> draggedVertex = new ArrayList<Rectangle2D>(); 
	
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
	
	/**Cursor for selection*/
	public Rectangle2D selectionCursor = null;
	
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
	private String currentMouseTipText = SettingsFrame.DEFAULT_MOUSE_TIP;

	/** Last feature that its vertex was dragged during edit mode*/
	private Feature lastDraggedFeature;
	
	/** The vertex of the last dragged feature*/
	private Rectangle2D lastDraggedVertex;
	
	/** The index of the pressed feature vertex during edit mode*/
	private int pressedVertexIndex;
	
	
	/**
	 * Constructs a new drawing panel
	 */
	public DrawingJPanel() {
		
		super();
		
		addMouseMotionListener(this);
		addMouseListener(this);
		setBackground(SettingsFrame.DRAFTING_BACKGROUND.getBackground());
		
		renderGrid(SettingsFrame.GRID_MM);
		showAnimatedHint("Welcome", SettingsFrame.HIGHLIGHTED_STATE_COLOR);
		
	}
	
	/**
	 * Paints the drawn component
	 * @param g the graphics object
	 */
	@Override 
	public void paintComponent(Graphics g) {
		
		super.paintComponent(g);
		setBackground(SettingsFrame.DRAFTING_BACKGROUND.getBackground());
		
		// i. Clone the graphics object
		// -----------------------------
		g2d = (Graphics2D) g.create();
		
		// ii. General rendering, order matters !
		// --------------------------------------
		
		try {
			
			// 0. Set drawing pen to default each time
			// ----------------------------------------
			g2d.setStroke(new BasicStroke(1));
			g2d.setColor(SettingsFrame.DEFAULT_LAYER_COLOR);
			g2d.setFont(new Font("Tw Cen MT", Font.ITALIC, SettingsFrame.FONT_SIZE)); 
			g2dFontMetrics = g2d.getFontMetrics();
			
			// 1. Render the grid lines, should be below every item
			// -----------------------------------------------------
			for(GridLine item : this.gridLines) {
				Line2D line = item.getLine();
				g2d.setStroke(new BasicStroke(item.getWeight()));
				g2d.setColor(SettingsFrame.GRID_COLOR.getBackground());
				g2d.draw(line);
			}
			
			// 2. Render the layers in the table of contents
			// ----------------------------------------------
			for(Layer layer : TableOfContents.layerList) {
				
				if(layer.isVisible()) {
					
					Color c = layer.getLayerColor();
					
					for(Feature feature : layer.getListOfFeatures()) {
						
						if(feature.isVisibile()) {
						
							if(feature.isHighlighted()) {
								c = SettingsFrame.FEATURE_HIGHLIGHTED_STATE_COLOR.getBackground();
							} 
							
							else if (!feature.isHighlighted()) {
								c = layer.getLayerColor();
							}
							
							if (!layer.getLayerType().equals(SettingsFrame.POINT_GEOMETRY)) {
							
								if(layer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY)) {
									// Fill the shape
									g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
									g2d.fill(feature.getShape());
								}
								
								// Draw only outline for non-polygon shapes
								g2d.setColor(c);
								g2d.setStroke(new BasicStroke(layer.getLineWeight()));
								g2d.draw(feature.getShape());
								
								// Change the color of the pen if the current layer is currently edited
								// and edit mode is on
								if(layer.equals(currentLayer) && editModeIsOn) {
									c = SettingsFrame.HIGHLIGHTED_STATE_COLOR;
								}
								// Render the shape vertices on top
								if(displayVertices) {
									//if(!feature.isEllipse()) {
									for(Rectangle2D shape : feature.getVertices()) {
										g2d.setColor(c);
										g2d.fill(getCurrentVertixSize(shape));
									}
								}
								//}
							} else {
								
								if(layer.equals(currentLayer) && editModeIsOn) {
									c = SettingsFrame.HIGHLIGHTED_STATE_COLOR;
								}
								
								PointItem point = (PointItem) feature;
								g2d.setColor(c);
								g2d.fill(point.getShape());
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
			
			// 4. Render temporary shapes drawn on current layer
			// ---------------------------------------------------
			if(tempShape != null) {
				
				Color c = currentLayer.getLayerColor();
				Stroke stroke = new BasicStroke(currentLayer.getLineWeight());
				
				g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
				
				if(currentLayer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY)) {
					// Fill the shape
					g2d.fill(tempShape);
				}
				
				if(currentLayer.getLayerType().equals(SettingsFrame.POLYLINE_GEOMETRY)){
					stroke = new BasicStroke(currentLayer.getLineWeight(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
				}
				
				g2d.setStroke(stroke);
				g2d.setColor(c);
				g2d.draw(tempShape);
				
			}
			
			// 5. Render selection bounds if it is not null
			// ----------------------------------------------
			if(this.queryBounds != null ) {
				
				Color c = SettingsFrame.SELECTION_COLOR.getBackground();
				
				g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), SettingsFrame.TRANSPARENCY_LEVEL_2));
				g2d.fill(queryBounds);
				
				// Draw border
				g2d.setStroke(new BasicStroke(3));
				g2d.setColor(c);
				g2d.draw(queryBounds);
			}
			
			// 6. Render vertices coming from mouse clicked/ pressed
			// ------------------------------------------------------
			int count = 0;
			for(Rectangle2D item : this.vertexList) {
				
				g2d.setColor(currentLayer.getLayerColor());
				
				if(count == 0) {
					g2d.setColor(SettingsFrame.HIGHLIGHTED_STATE_COLOR);
				}
				// For poly lines - change color of the first and last vertex
				if(currentLayer.getLayerType().equals(SettingsFrame.POLYLINE_GEOMETRY)) {
					if(count == this.vertexList.size() - 1) {
						g2d.setColor(SettingsFrame.HIGHLIGHTED_STATE_COLOR);
					}
				}
				
				g2d.fill(getCurrentVertixSize(item));
				count++;
				
			}
			
			// 7. Render the snap 
			// -------------------
			if(!(snapPoint == null)) {
				g2d.setStroke(new BasicStroke(1));
				g2d.setColor(Color.PINK);
				g2d.draw(getCurrentVertixSize(snapPoint));
			}
			
			// 8. Render tool tips
			// ---------------------
			if(this.tooltip != null) {
				
				g2d.setFont(new Font("Tw Cen MT", Font.ITALIC, SettingsFrame.FONT_SIZE)); 
	            Rectangle2D rect = g2dFontMetrics.getStringBounds(tooltip.getText(), g2d); 	
	            Color c = SettingsFrame.DEFAULT_STATE_COLOR;
	            g2d.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), SettingsFrame.TRANSPARENCY_LEVEL_1));									
	            g2d.fillRect((int)tooltip.getBasePosition().getX(),
	            		(int)tooltip.getBasePosition().getY() - g2dFontMetrics.getAscent(),
	                       (int) rect.getWidth(),
	                       (int) rect.getHeight());
	            
	            
	            g2d.setColor(Color.WHITE);							
	            g2d.drawString(tooltip.getText(), (int)tooltip.getBasePosition().getX(), (int)tooltip.getBasePosition().getY());
			
			}
			
			// 9. Render Hints
			// -----------------
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
			
			// 10. Render selection rectangle
			// -------------------------------
			
			if(this.selectionCursor != null) {
				
				g2d.setColor(SettingsFrame.cursorColor);
				g2d.draw(selectionCursor);
			}
			
			// 11. Render draw guides
			// -----------------------
			if(this.drawGuide!=null) {
				
				setCurrentMouseGuide(drawGuide, SettingsFrame.DEFAULT_STATE_COLOR);
			}
			
			// 12. Render moving mouse tips
			// -----------------------------
			if(this.movingMouseTip!=null) {
				
				setCurrentMouseGuide(movingMouseTip, Color.BLACK);
			}
			
		} catch (Exception e) {
			
			MainFrame.log("Error occured while rendering (will ignore) : " + e.getMessage().toString());
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
			renderGrid(SettingsFrame.GRID_MM);
			
		}
		if(SettingsFrame.gridToggle.getState() != gridIsOn) {
			SettingsFrame.gridToggle.doClick();
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
		
		if(SettingsFrame.snapToggle.getState() != snappingModeIsOn) {
			SettingsFrame.snapToggle.doClick();
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
		
		// Disable selection and query mode
		disableQueryMode();
		disableSelectionMode();
		
		// Change the current editing layer and feature type
		changeCurrentLayerAndFeatureType(layerIndex, featureType);
		
		System.out.println(featureType);
		
		if(signal == null) {
			signal = "";
		}
		
		// Only turn off or on when the signal is a new edit session
		if(!signal.equals(SettingsFrame.DRAW_CONTINUE)) {
			
			cleanUpDrawing();
			
			if(this.editModeIsOn) {
				
				editModeIsOn = false;
				
				showAnimatedHint("Edit session turned off", SettingsFrame.DEFAULT_STATE_COLOR);
				
			} else {
				
				editModeIsOn = true;
				
				this.vertexList.clear();
				
				showAnimatedHint("Edit session turned on", SettingsFrame.HIGHLIGHTED_STATE_COLOR);
				
				// Update draw buttons
				MainFrame.updateDrawButtonGroup();
			}
		}
	}
	
	/**
	 * Toggles query mode
	 */
	public void toggleQueryMode() {
		
		abandonEditSession();
		
		if( queryModeIsOn ) {
			
			queryModeIsOn = false;
			MainFrame.log("Query mode turned off");
			
		} 
		else {

			queryModeIsOn = true;
			MainFrame.queryButton.setBackground(SettingsFrame.HIGHLIGHTED_STATE_COLOR);
			MainFrame.disableAllDrawButtons();
			MainFrame.log("Query mode started, all draw buttons disabled");
		}
	}

	/**
	 * Toggles selection mode
	 */
	public void toggleSelectionMode() {

		abandonEditSession();
		
		if( selectionModeIsOn ) {
			
			selectionModeIsOn = false;
			MainFrame.log("Selection mode turned off");
		} 
		
		else {

			selectionModeIsOn = true;
			MainFrame.selectionButton.setBackground(SettingsFrame.HIGHLIGHTED_STATE_COLOR);
			MainFrame.disableAllDrawButtons();
			
			MainFrame.log("Selection mode started, all draw buttons disabled");	
		}
	}

	/**
	 * Disables query mode
	 */
	public void disableQueryMode() {
		
		queryModeIsOn = false;
		
		MainFrame.queryButton.setButtonReleased(false);
		MainFrame.queryButton.setBackground(SettingsFrame.DEFAULT_STATE_COLOR);
		
		cleanUpDrawing();
	}
	
	/**
	 * Disables selection mode
	 */
	public void disableSelectionMode() {
		
		selectionModeIsOn = false;
		
		MainFrame.selectionButton.setButtonReleased(false);
		MainFrame.selectionButton.setBackground(SettingsFrame.DEFAULT_STATE_COLOR);
		
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
	 * Assists the paint component by creating vertexes based on the current settings snap size.
	 * @param vertix the vertex to set
	 * @return the vertex
	 */
	private Shape getCurrentVertixSize(Rectangle2D  vertix) {
		int size = SettingsFrame.SNAP_SIZE;
		vertix = new Rectangle2D.Double(
				vertix.getCenterX() - (size/2),
				vertix.getCenterY() - (size/2),
				size,
				size
				);
		
		return vertix;
	}

	/**
	 * Helps the paint component to compute rendering and conflict calculations for mouse guides <br>
	 * The repaint method should be called wherever this method is used.
	 * @param guideOrTip the guideOrTip to set
	 * @param color the color to set
	 */
	private void setCurrentMouseGuide(TextItem guideOrTip, Color color) {
		
		try {
			if(guideOrTip != null) {
				
				g2d.setFont(new Font("Tw Cen MT", Font.ITALIC, SettingsFrame.FONT_SIZE)); 
				FontMetrics fm = g2d.getFontMetrics();
				Rectangle2D rect = fm.getStringBounds(guideOrTip.getText(), g2d); 
				
				if(guideOrTip.getBasePosition().getX() < 0) {
					guideOrTip.setBasePosition(new Point2D.Double(getMousePosition().getX() + SettingsFrame.mouseOffset, getMousePosition().getY()));
				}
				
				if(guideOrTip.getBasePosition().getX() + rect.getWidth() > getWidth()) {
					guideOrTip.setBasePosition(new Point2D.Double(getMousePosition().getX() - SettingsFrame.mouseOffset - rect.getWidth(), getMousePosition().getY()));
				}
				
				int padding = SettingsFrame.TOOL_TIP_PADDING;
				g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), SettingsFrame.TRANSPARENCY_LEVEL_1));
				
				RoundRectangle2D roundedRect = getRoundedFrameRectForText(guideOrTip, fm, padding);
				guideOrTip.setBorderRectangleInPanel(roundedRect);
				
				g2d.fill(roundedRect);
				
				g2d.setColor(Color.WHITE);							
				g2d.drawString(guideOrTip.getText(), (int) (int)guideOrTip.getBasePosition().getX() + padding/2, (int)guideOrTip.getBasePosition().getY()- padding/2);
			}
		}
		catch (NullPointerException e) {
			MainFrame.log(e.getMessage());
		}
	}
	
	/**
	 * Supplies rounded edges rectangle for mouse guides and tips
	 * @param drawGuide the draw guide to set
	 * @param fm computed font metrics of the string to use
	 * @param padding the padding to use
	 * @return rounded edges rectangle for mouse guides and tips
	 */
	private RoundRectangle2D getRoundedFrameRectForText(TextItem drawGuide, FontMetrics fm, int padding) {
		
		Rectangle2D fittingRect = fm.getStringBounds(drawGuide.getText(), g2d); 
		
		return new RoundRectangle2D.Double(
				
				(int)drawGuide.getBasePosition().getX() - padding,
	    		(int)drawGuide.getBasePosition().getY() - fm.getAscent() - padding,
	            (int) fittingRect.getWidth() + padding*2,
	            (int) fittingRect.getHeight() + padding, padding + 2, padding + 2
				) ;		
	}
	
	/**
	 * Changes the current layer, needed at each new edit session.
	 * It finds the layer with same ID at the table of contents
	 * @param layerIndex layer index from the combo box and the mainframe to set
	 * @param the feature type selected e.g circle, rectangle to set
	 */
	private void changeCurrentLayerAndFeatureType(int layerIndex, String featureType) {
		
		int layerID = (int) MainFrame.tableOfContents.getModel().getValueAt(layerIndex, TableOfContents.LAYER_ID_COL_INDEX);
		currentLayer = TableOfContents.findLayerWithID(layerID);
		currentFeatureType = MainFrame.getCurrentFeatureType();
	}
	
	/**
	 * Handles temporary drawing on the panel before features are created with mouse clicked
	 * @param e the mouse moved event object
	 */
	private void handleMouseMovedDrawing(MouseEvent e) {
		
		
		if(editModeIsOn) {
			
			if( MainFrame.getCurrentFeatureType() != null ) {
				
				// Drawing guides
				
				if(SettingsFrame.DRAW_GUIDES_AND_TIPS == true) {
					
					if(this.vertexList.size() > 0) {
						getDrawDetailsToNewPoint(e.getPoint());
						getMouseToolTip(e.getPoint());
					}	
				}
	
				// Handling Polyline 
				if(currentLayer.getLayerType().equals(SettingsFrame.POLYLINE_GEOMETRY)) {
					this.handleDrawingPolyline(e);
				}
				
				// Handling Rectangle 
				if(currentLayer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY) && MainFrame.getCurrentFeatureType().equals("Ellipse")) {
					this.handleDrawingEllipse(e);
				}
					
				// Handling Rectangle 
				if(currentLayer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY) && MainFrame.getCurrentFeatureType().equals("Rectangle")) {
					this.handleDrawingRectangle(e);
				}
				
				// Handling Circle 
				if(currentLayer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY) && MainFrame.getCurrentFeatureType().equals("Circle")) {
					this.handleDrawingCircle(e);
				}
				
				// Handling Triangle 
				if(currentLayer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY) && MainFrame.getCurrentFeatureType().equals("Triangle")) {
					this.handleDrawingTriangle(e);
				}
				
				if(snappingModeIsOn) {
					
					findSnaps(e);
					
						
				} else {
					
					this.snapPoint = null;
					
					//if(this.editModeIsOn) {
						
						if(currentLayer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY) || currentLayer.getLayerType().equals(SettingsFrame.POLYLINE_GEOMETRY)) {
							this.handleDrawingClosingProtocol(e);
						}
					//}
					}
			}
		}	
	}

	/**
	 * Handles drawing rectangle
	 * @param e mouse moved event object to set
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
	 * Handles drawing circle during mouse moved drawing.
	 * @param e the mouse moved event object to set
	 */
	private void handleDrawingCircle(MouseEvent e) {

		if(this.vertexList.size() == 1) {
		
			// (a) The center point will be the first point in the vertex list
			Point2D centerPoint = new Point2D.Double(this.vertexList.get(0).getCenterX(), this.vertexList.get(0).getCenterY());
			
			// (b) The radius of the circle will be the distance from the center point to the currently moving mouse point
			double radius = (e.getPoint().distance(centerPoint));
			
			// (c) Construct circle with the parameters
			Shape circleShape = new Ellipse2D.Double(centerPoint.getX() - radius, centerPoint.getY() - radius , radius * 2, radius* 2);
			
			// (d) Display temporary circle
			tempShape = circleShape;
			repaint();
			
		}
	}
	
	/**
	 * Handles drawing triangle during mouse moved drawing.
	 * @param e the mouse moved event object to set
	 */
	private void handleDrawingTriangle(MouseEvent e) {
		
		// 0. Update current mouse tip
		if(this.vertexList.size() == 1) {
			
			this.currentMouseTipText = "Specify second point";
			
			repaint();
		}
				
		// 1. Create a path for the triangle
		Path2D path = new Path2D.Double();
		
		// 2. Create temporary triangle if the size point list is exactly 2
		if(this.vertexList.size() == 2) {
			
			// 2.1 Update current mouse tip
			this.currentMouseTipText = "Specify third point";
			
			// 2.2 Move to the first item clicked in the vertex list
			path.moveTo(vertexList.get(0).getCenterX(), vertexList.get(0).getCenterY());
			
			// 2.3 Line to the second item clicked in the vertex list
			path.lineTo(vertexList.get(1).getCenterX(), vertexList.get(1).getCenterY());
			
			// 2.4 Line to the current mouse position
			path.lineTo(e.getPoint().getX(), e.getPoint().getY());
			
			// 2.5 Close the path and render
			path.closePath();
			
			this.tempShape = path;
			
			repaint();
		}
	}

	/**
	 * Handles drawing polyline during mouse moved drawing.
	 * @param e the mouse moved event object
	 */
	private void handleDrawingPolyline(MouseEvent e) {
	
		if(vertexList.size() >= 1) {
			
			// Draw a moving line from the last point in the vertex list
			Point2D base = new Point2D.Double(vertexList.get(vertexList.size()-1).getCenterX(), vertexList.get(vertexList.size()-1).getCenterY());
			Shape movingLine = new Line2D.Double(base.getX(), base.getY(), e.getX(), e.getY());
			
			this.tempShape = movingLine;
			
			repaint();
		}
	}
	
	/**
	 * Handles drawing ellipse
	 * @param e the mouse moved event object
	 */
	private void handleDrawingEllipse(MouseEvent e) {
		
		// 0. If the center of the ellipse have been specified already
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
		
		// 1. If the center and the major axis of the ellipse have been specified already
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
	 * Closing protocol for drawing polygons
	 * For situation where the snap or grid is turned off and user needs to close a feature
	 * @param e the mouse moved event object
	 */
	private void handleDrawingClosingProtocol(MouseEvent e) {
		
		// 0. Only perform protocol if the edit mode is on
		// ------------------------------------------------
		if(this.editModeIsOn) {
			
			// 1. For Polygons
			// ------------------------------------------------
			if(MainFrame.getCurrentFeatureType() != null) {
					
				if(MainFrame.getCurrentFeatureType().equals("Freeform Polygon")) {
	
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
							this.currentMouseTipText = SettingsFrame.CLOSE_POLYGON_MESSAGE;;
							repaint();
						}
					}
				}
			}
			
			// 2. For Polylines
			/// ------------------------------------------------
			if(currentLayer.getLayerType() == SettingsFrame.POLYLINE_GEOMETRY) {
	
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
						this.currentMouseTipText  = SettingsFrame.CLOSE_POLYLINE_MESSAGE;
						
					} 
					else if ( lastVertex.contains(e.getPoint()) ) {
						
						this.currentMouseTipText  = SettingsFrame.FINISH_POLYLINE_MESSAGE;
					} 
					else {
						
						// 2.4 Erase (the snap and) the tool tip if mouse goes away
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
	 * Highlights features that are intersected by the query region.
	 * The highlight disappears when it intersects nothing
	 * @param queryBounds the bounds of the rectangle to set
	 */
	private void handleSelectionMode( Rectangle2D queryBounds ) {
		
		// i. If no feature found, the bounds should disappear
		boolean noFeatureFound = true;
		
		// 1. Loop through all layer at the table of contents
		for(Layer layer : TableOfContents.layerList) {
			
			// 2. Consider only layers that are visibile 
			if(layer.isVisible()) {
				
				// 3. Loop through all features in a layer
				for(Feature feature : layer.getListOfFeatures()) {
						
					// 4. Consider only features that are visible
					if(feature.isVisibile()) {
						
						if(feature.isHighlighted()) {
						
							feature.setHighlighted(false);
							repaint();
						} 
						
						else {
							
							// 4.0 Create an area object from the query bounds
							Area areaA = new Area(queryBounds);
							
							// 4.1 Test for polygon -> intersects
							if(layer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY)) {
								areaA.intersect(new Area(feature.getShape()));
								if(!areaA.isEmpty()) {
									feature.setHighlighted(true);
									noFeatureFound = false;
								}
							} 
							
							// 4.2. Test for polyline -> intersects
							else if (layer.getLayerType().equals(SettingsFrame.POLYLINE_GEOMETRY)) {
								
								PolylineItem polyline = (PolylineItem) feature;
								
								for(Line2D line : polyline.getListOfLines()) {
									if(queryBounds.intersectsLine(line)){
										feature.setHighlighted(true);
										noFeatureFound = false;
										break;
									}
								}
								
							}
							// 4.3 Then for point -> contains
							else {
								PointItem point = (PointItem) feature;
								if(areaA.contains( (Point2D) point.getGeometry())) {
									feature.setHighlighted(true);
									noFeatureFound = false;
								}
							}
						}
					}
					repaint();
				}
			}
		}
		
		// 5. Make the bound disappear if no feature found
		if(noFeatureFound) {
			
			cleanUpDrawing();
		
		} 
		
		// 6. Give hint on how to deselect feature(s)
		else {
			
			//showAnimatedHint("Click again to unhighlight and highlight feature", SettingsFrame.HIGHLIGHTED_STATE_COLOR);
			MainFrame.log("Click again to unhighlight and highlight feature");
		}
	}

	/**
	 * Handles editing mode on the drawing panel.<br>
	 * Cross hair cursor is set at the mouse moved event.<br>
	 * Originates from mouse clicked event.
	 * @param e the mouse clicked event object
	 */
	private void handleEditingMode(MouseEvent e) {
		
		if(MainFrame.getCurrentFeatureType() != null ) {
			
			// i. Set current feature type
			// ----------------------------------
			currentFeatureType = MainFrame.getCurrentFeatureType();
			
			// ii. Get current point of the mouse 
			// ----------------------------------
			Point2D clickedPoint = e.getPoint();
			
			// iii. Replace the point with the snapped point in case snapping is on
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
			
			else if(currentLayer.getLayerType().equals(SettingsFrame.POINT_GEOMETRY)) {
				
				Feature point  = new PointItem(currentLayer.getNextFeatureID(), clickedPoint);
				point.setFeatureType(SettingsFrame.POINT_GEOMETRY);
				point.setLayerID(currentLayer.getId());
				point.setShape(new Ellipse2D.Double(clickedPoint.getX() - SettingsFrame.POINT_SIZE/2,
						clickedPoint.getY() - SettingsFrame.POINT_SIZE/2,
						SettingsFrame.POINT_SIZE, SettingsFrame.POINT_SIZE));
				point.getVertices().add(vertex);
				currentLayer.getListOfFeatures().add(point);
				currentLayer.setNotSaved(true);
				repaint();
			}
			
			//------------------------------------------------------------------------
			
			//                   Protocol for drawing polygon
			
			//------------------------------------------------------------------------
			else if(currentLayer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY)) {
				
				
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
							ellipse.setEllipse(true, new Point2D.Double(centerX, centerY), diamX / 2, diamY / 2);
							ellipse.setShape(shape);
							ellipse.setFeatureType("Ellipse");
							ellipse.getVertices().addAll(this.vertexList);
							
							// Add to the current layer's feature list
							currentLayer.getListOfFeatures().add(ellipse);
							currentLayer.setNotSaved(true);
							
							onFeatureCreated("New ellipse created");
						}
					}
					
					else if(MainFrame.getCurrentFeatureType().equals("Freeform Polygon")) {
	
						
						// 2. Add the vertex to the current list of vertex and the global snap points
						// ---------------------------------------------------------------------------
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
							MainFrame.log("Tip: " + SettingsFrame.CLOSE_POLYGON_MESSAGE);
							this.currentMouseTipText = SettingsFrame.CLOSE_POLYGON_MESSAGE;
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
								finishPath(this.vertexList, currentLayer);
								currentLayer.getListOfFeatures().get(currentLayer.getListOfFeatures().size()-1).setFeatureType("Freeform Polygon");
	
								onFeatureCreated("Polygon feature created, click to save your edits");
								
							}
						}
					}
					
					//--------------------------------------------------------------------
					//                        RECTANGLE 
					//                        (2 POINT)
					//--------------------------------------------------------------------
					
					else if (MainFrame.getCurrentFeatureType().equals("Rectangle")) {
						
						// 2. Add the vertex to the current list of vertex and the global snap points
						// ---------------------------------------------------------------------------
						this.vertexList.add(vertex);
						this.globalDrawingSnapPoints.add(vertex);
						
						// 3. Render the vertex immediately
						// --------------------------------
						this.currentMouseTipText = "Click to finish rectangle";
						repaint();
						
						
						// Because the top left and bottom right vertix are needed to finish the rectangle
						// The list of vertex will be be only two
						// Guides will be handled by the mouse moved event
						// TODO: Comment properly
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
							this.vertexList.add(vertex);
							
							// (a) The center point will be the first point in the vertex list
							Point2D centerPoint = new Point2D.Double(this.vertexList.get(0).getCenterX(), this.vertexList.get(0).getCenterY());
							
							// (b) The radius of the circle will be the distance from the center point to the clicked point 
							double radius = (clickedPoint.distance(centerPoint));
							
							// 3.2 Construct circle with the parameters
							Shape circleShape = new Ellipse2D.Double(centerPoint.getX() - radius, centerPoint.getY() - radius , radius * 2, radius* 2);
							
							// 3.3 Create a new feature
							Feature circle = new Feature(currentLayer.getNextFeatureID());
							circle.setLayerID(currentLayer.getId());
							circle.setFeatureType("Circle");
							circle.getVertices().addAll(this.vertexList);
							circle.setEllipse(true, centerPoint, radius, radius);
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
							
							
							finishPath(this.vertexList, currentLayer);
							currentLayer.getListOfFeatures().get(currentLayer.getListOfFeatures().size()-1).setFeatureType("Triangle");
							
							// 3.5 Log some messages
							onFeatureCreated("Triangle created");
						}
						
					}
				}
			}
			
			//------------------------------------------------------------------------
			//                   Protocol for drawing polyline
			//-----------------------------------------------------------------------
			else if(currentLayer.getLayerType().equals(SettingsFrame.POLYLINE_GEOMETRY)) {
				
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
							
							if(currentFeatureType.equals("Line")) {
								
								//  Finish up the line and create a new feature
								finishPath(this.vertexList, currentLayer);
								
								//  Log some message and update
								onFeatureCreated("Line feature created");
							}
							
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
						finishPath(this.vertexList, currentLayer);
						
						// 4.3 Log some message and update
						onFeatureCreated("Polyline feature created");
						
					}
					
					if ( (lastVertex.contains(clickedPoint)) && e.getClickCount() > 1) {
						
						closed = true;
						
						// 4.3 Finish up the line and create a new feature
						finishPath(this.vertexList, currentLayer);
						
						// 4.3 Log some message and update
						onFeatureCreated("Multi line feature created");
						
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
		} 
		
		else {
			
			showAnimatedHint("No shape have been selected", SettingsFrame.HIGHLIGHTED_STATE_COLOR);
			MainFrame.log("No shape have been selected");
		}
	}

	/**
	 * Handles dragging of feature vertices during edit mode.
	 * 
	 * @param draggedPoint dragged point coming from the mouse dragged event
	 */
	private void handleVertixDragging(Point2D draggedPoint) {
		
		// 0. Update the current mouse tip
		if(this.movingMouseTip != null) {
			this.movingMouseTip.setBasePosition(draggedPoint);
			repaint();
		}
		
		// a. For non ellipse shapes
		if( !this.lastDraggedFeature.isEllipse() && !this.lastDraggedFeature.getFeatureType().equals(SettingsFrame.POINT_GEOMETRY) ) {
	
			// 0. Clear existing vertix lists
			this.vertexList.clear();
			
			// 1. Get the index of the vertix that was dragged from the feature list of vertices
			//
			int index = this.pressedVertexIndex;
			for(Rectangle2D vertix : this.lastDraggedFeature.getVertices()) {
				
				// 2. Add the other vertices to the list and ignore the vertix that was last dragged
				//    it will be replaced with the mouse dragged point later
			    //    therefore the index of the point is important
				if(index != this.lastDraggedFeature.getVertices().indexOf(vertix)) {
					this.vertexList.add(vertix);
				}
			}
			// 3. Create a new vertex from the mouse dragged point
			Rectangle2D vertex = new Rectangle2D.Double(draggedPoint.getX() - (snapSize/2), draggedPoint.getY() - (snapSize/2), snapSize, snapSize);
			
			// 4. Add the new dragged vertex to the list of vertex using its previous index 
			this.vertexList.add(index, vertex);
			
			// 5. Start a path from the list of vertex
			Path2D path = new Path2D.Double();
			path.moveTo(vertexList.get(0).getCenterX(), vertexList.get(0).getCenterY());
			
			// 6. Draw the path to the rest of the vertex in the list
			for(Rectangle2D v : this.vertexList) {
				if(vertexList.indexOf(v)!= 0 ) {
					path.lineTo(v.getCenterX(), v.getCenterY());
				}
			}
			
			// 7. If the current layer is a polygon, close the path
			if( currentLayer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY) ) {
				path.closePath();
			}
			
			// 8. Set the shape of the dragged feature to the path just created
			this.lastDraggedFeature.setShape(path);
			
			this.lastDraggedVertex = vertex;
			
			// 9. Clear the vertex list before repainting, so that the feature vertixes are 
			// only displayed for further dragging
			this.vertexList.clear();
			
			// 10. Update the drawing panel
			repaint();
		} 
		
		// b. For ellipse shapes
		else if (this.lastDraggedFeature.isEllipse()) {
			
			// 1. Retrieve the pressed vertex by using the index
			Rectangle2D vertex = lastDraggedFeature.getVertices().get(pressedVertexIndex);
			
			// 2. Numbers represent the order of the vertices
			//    This have been ordered when the ellipse was created !important
			//    For circles, the index of "2" will be absent, because there is no
			//    vertex for the semi-minor axis
			
			int CENTER = 0; // <-- Center point
			int AXIS_X = 1; // <-- Axis x
			int AXIS_Y = 2; // <-- Axis y
			
			// 3. Retrieve the radiusX and radiusY of the ellipse shape and the initial size of the vertixes
			double radiusX = this.lastDraggedFeature.getRadiusX();
			double radiusY = this.lastDraggedFeature.getRadiusY();
			int previousSize = this.lastDraggedFeature.getVertices().size();
			
			// 4. If the vertex dragged is the center point, the shape will be moved/ translated
			if( vertex.equals(lastDraggedFeature.getVertices().get(CENTER)) ) {
				// 4.1 Create a new ellipse shape using the dragged point as the center
				//     preserving the radiusX and the radiusY
				Shape shape = new Ellipse2D.Double(draggedPoint.getX() - radiusX, draggedPoint.getY() - radiusY , radiusX * 2, radiusY * 2);
				// 4.2 Replace the shape of the feature
				lastDraggedFeature.setShape(shape);
				// 4.3 Change the center of the feature with the current mouse dragged point !importabt
				lastDraggedFeature.setCenter(draggedPoint);
				// 4.4 Make a new center rectangle vertix for the shape for rendering purposes and further selection/ dragging
				Rectangle2D newCenterVertix = new Rectangle2D.Double(draggedPoint.getX() - (snapSize/2), draggedPoint.getY() - (snapSize/2), snapSize, snapSize);
				// 4.4 Replace rectangle of the center using the indexes specified at 2.
				lastDraggedFeature.getVertices().clear();
				lastDraggedFeature.getVertices().add(newCenterVertix);
				// 4.5 For circle: Replace the rectangle of the X axis by using the new center of the feature 
				//     the initial radius X will still be used
				Rectangle2D axisX = new Rectangle2D.Double((lastDraggedFeature.getCenter().getX() + radiusX) - (snapSize/2),
						(lastDraggedFeature.getCenter().getY() - (snapSize/2)), (snapSize), (snapSize));
				lastDraggedFeature.getVertices().add(axisX);
				// 4.6 For real ellipse : Replace the rectangle of the Y axis by using the new center of the feature 
				//     the initial radius Y will still be used.
				//	   The previous size of the vertix list will be more than 2 if it is not a cirlce
				if(previousSize > 2) {
					Rectangle2D axisY = new Rectangle2D.Double((lastDraggedFeature.getCenter().getX()) - (snapSize/2),
							(lastDraggedFeature.getCenter().getY() - radiusY) - (snapSize/2), (snapSize), (snapSize));
					lastDraggedFeature.getVertices().add(axisY);
					
				}
				// 4.7 Update the panel as the feature is moved
				repaint();
			} 
			
			// 5. If the vertex dragged vertex at the X Axis, that means the center will be 
			//     preserved and radius at X will be changed
			else if( vertex.equals(lastDraggedFeature.getVertices().get(AXIS_X)) ) {
				// 5.1 Get the absolute X difference from the ellipse center X to the mouse dragged point
				radiusX = Math.abs(lastDraggedFeature.getCenter().getX() - draggedPoint.getX());
				// 5.2 If the feature is a circle, increase the radius Y with the just computed index
				// again, for circle, the list of vertix will be less than 3 or excatly 2! see 4.6 and 2.
				if(lastDraggedFeature.getVertices().size() < 3) {
					radiusY = radiusX;
				}
				// 5.3 Create a new shape with the new computed radius X and radius Y, but preseving the center
				// for circles, the radius will increase uniformly see 5.2 above
				Shape shape = new Ellipse2D.Double(lastDraggedFeature.getCenter().getX() - radiusX, lastDraggedFeature.getCenter().getY() - radiusY , radiusX * 2, radiusY * 2);
				// 4.2 Replace the shape of the feature
				lastDraggedFeature.setShape(shape);
				// 4.3 Update the parameters of the feature ! important for saving to DB
				lastDraggedFeature.setRadiusX(radiusX); // <-- radiusX
				lastDraggedFeature.setRadiusY(radiusY); // <-- radiusY
				// and then the rectangle vertix of the axis X
				lastDraggedFeature.getVertices().get(AXIS_X).setRect(
						(lastDraggedFeature.getCenter().getX() + radiusX) - (snapSize/2),
						(lastDraggedFeature.getCenter().getY() - (snapSize/2)), (snapSize), (snapSize));
				
				// 4.4 Update the panel as the feature is moved
				repaint();
			}
			// 6. If the vertex dragged vertex at the Y Axis, that means the center will be 
			//     preserved and radius at Y will be changed
			else if( vertex.equals(lastDraggedFeature.getVertices().get(AXIS_Y)) ) {
				// 6.1 Get the absolute Y difference from the ellipse center Y to the mouse dragged point
				radiusY = Math.abs(lastDraggedFeature.getCenter().getY() - draggedPoint.getY());
				// 6.2 Create a new shape with the new computed and radius Y, but preseving the center
				// for circles, it will never get to this step because there is no Axis_Y see 2, 4.6
				Shape shape = new Ellipse2D.Double(lastDraggedFeature.getCenter().getX() - radiusX, lastDraggedFeature.getCenter().getY() - radiusY , radiusX * 2, radiusY * 2);
				// 6.3 Replace the shape
				lastDraggedFeature.setShape(shape);
				// 6.4 Update the parameters of the feature ! important for saving to DB
				lastDraggedFeature.setRadiusX(radiusX); // <-- radiusX
				lastDraggedFeature.setRadiusY(radiusY); // <-- radiusY
				// and then the rectangle vertix of the axis Y
				lastDraggedFeature.getVertices().get(AXIS_Y).setRect(
						lastDraggedFeature.getCenter().getX() - (snapSize/2),
						(lastDraggedFeature.getCenter().getY() - radiusY) - (snapSize/2), (snapSize), (snapSize));
				
				// 6.5 Update the panel as the feature is moved
				repaint();
			}
		}
		
		else if ( this.lastDraggedFeature.getFeatureType().equals(SettingsFrame.POINT_GEOMETRY) ) {
			lastDraggedFeature.setShape(new Ellipse2D.Double(draggedPoint.getX() - SettingsFrame.POINT_SIZE/2,
					draggedPoint.getY() - SettingsFrame.POINT_SIZE/2,
					SettingsFrame.POINT_SIZE, SettingsFrame.POINT_SIZE));
			lastDraggedFeature.getVertices().clear();
			lastDraggedFeature.getVertices().add(new Rectangle2D.Double(draggedPoint.getX() - SettingsFrame.POINT_SIZE/2,
					draggedPoint.getY() - SettingsFrame.POINT_SIZE/2,
					SettingsFrame.POINT_SIZE, SettingsFrame.POINT_SIZE));
			repaint();
		}
		// Prompt for save 
		currentLayer.setNotSaved(true);
	}

	/**
	 * Handles right click 
	 */
	private void handleDrawUndoIntent() {
		
		if(this.vertexList.size() > 1) {
			
			vertexList.remove(vertexList.size() - 1);
			
			if(currentLayer.getLayerType().equals(SettingsFrame.POLYLINE_GEOMETRY)) {
				
				tempLine.remove(tempLine.size() - 1);
			}
			
			if(currentLayer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY)) {
				
				showTempPolygon(vertexList);
			}
			
			repaint();
		}
		
		if(this.vertexList.size() == 1) {
			
			this.vertexList.clear();
			
			cleanUpDrawing();
			
			repaint();
		}
		
		if(currentLayer.getLayerType().equals(SettingsFrame.POINT_GEOMETRY)) {
			
			currentLayer.removeLastItem();
		}
		
	}

	private void findSnaps(MouseEvent e) {
		
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
			
			// Retrieve all vertices
			List<Rectangle2D> otherVertices = new ArrayList<Rectangle2D>();
			for(Layer layer : TableOfContents.layerList) {
				if(layer.isVisible()) {
					for(Feature feature : layer.getListOfFeatures()) {
						if(feature.isVisibile()) {
							otherVertices.addAll(feature.getVertices());
						}
					}
				}
			}
			
			for(Rectangle2D item : otherVertices) {
				if(item.contains(e.getPoint())) {
					this.snapPoint = item;
					this.repaint();
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
		}
		
	}

	/**
	 * Shows temporary polygon on the panel while drawing
	 * @param pointList the current list of points (should be more than 3) to set
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
	 * Computes a rectangle shape from two points, it gets necessary when drawing rectangles
	 * with mouse dragged, as the last point may not be at the right side of the first point
	 * @param b the base point to use
	 * @param m the mouse (new) point to use
	 * @return a rectangle shape
	 */
	private Shape getRectangleShape(Point2D b, Point2D m) {
		
		// (a) Get the coordinates of the mouse point
		double x = m.getX();
		double y = m.getY();
		
		// (b) Compute the width and height from the mouse point to the base point
		double width = Math.abs(m.getX() - b.getX());
		double height =  Math.abs(m.getY() - b.getY());
		
		// (c) Switch the mouse points at (a) to base points, if the X and Y of the mouse points
		//     are greater than the base point
		if(b.getX() < m.getX()) {
			x = b.getX();
		}
		
		if(b.getY() < m.getY()) {
			y = b.getY();
		}
		
		// (d) Create and return a rectangle with the parameters
		return new Rectangle2D.Double(x, y, width, height);
	}

	/**
	 * General handler for updating the message on the mouse tool tip.
	 * Can be disabled at the settings. <br>
	 * This is done at every mouse move event. <br>
	 * The message can be changed or controlled by merely updating the global currentMouseTip variable
	 * @param mousePoint the current mouse event point to set
	 */
	private void getMouseToolTip(Point mousePoint) {
		
		// Determine the text from the global currentMouseTip variable
		String mouseTip = this.currentMouseTipText;
		
		// Get the font metrics of the text
		Rectangle2D rect = g2dFontMetrics.getStringBounds(mouseTip, g2d);
		
		// Determine the last vertex
		Rectangle2D lastVertex = this.vertexList.get(vertexList.size()-1);
		
		// Determine the base position of the mouse point by adding a mouse offset
		Point2D basePosition = new Point2D.Double(mousePoint.getX() + SettingsFrame.mouseOffset, mousePoint.getY());
		
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
	 * @param mousePoint the current mouse point to set
	 */

	private void getDrawDetailsToNewPoint(Point mousePoint) {
		
		if(this.vertexList.size() > 0) {
		
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
	
	}

	/**
	 * Sets the drawing cursor based on the current session
	 * @param e the MouseEvent originating from the mouse moved to set
	 */
	private void setDrawingCursor(MouseEvent e) {
		
		Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		Cursor crossHaircursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR); 
		
		if(queryModeIsOn) {
			
		    setCursor(handCursor);
		     
		} 
		
		if(selectionModeIsOn) {
			
		    setCursor(handCursor);
		     
		     this.selectionCursor = new Rectangle2D.Double(e.getPoint().getX() - (SettingsFrame.cursorSize)/2,
		    		 e.getPoint().getY() - (SettingsFrame.cursorSize)/2,
		    		 SettingsFrame.cursorSize, SettingsFrame.cursorSize);
		        
		} 
		
		else {
			
			this.selectionCursor = null;
		}
		
		if(editModeIsOn) {
			
		     setCursor(crossHaircursor);
		     
		     for(Feature feature : currentLayer.getListOfFeatures()) {
					for(Rectangle2D vertix : feature.getVertices()) {
						if(!vertix.contains(e.getPoint())) {
							movingMouseTip = null;
							repaint();
							break;
						} 
					}	
				}
		     
		     if(displayVertices) {
			     for(Feature feature : currentLayer.getListOfFeatures()) {
					for(Rectangle2D vertix : feature.getVertices()) {
						if(vertix.contains(e.getPoint())) {
							setCursor(handCursor);
							movingMouseTip = new TextItem(e.getPoint(), "Drag to edit vertix");
							setCurrentMouseGuide(movingMouseTip, SettingsFrame.DEFAULT_LAYER_COLOR);
							repaint();
							break;
						} 
					}	
				}
		    }
		}
		
		repaint();
		
	}
	
	/**
	 * When a feature has been created, log some message, repaint and and clean up the drawing
	 * @param message the message to be logged
	 */
	private void onFeatureCreated(String message) {
	
		// Log some message
		MainFrame.log(message);
		showAnimatedHint(message, SettingsFrame.FEATURE_CREATED_COLOR);
		
		// Clean up the panel
		cleanUpDrawing();
	
		// Update the panel
		repaint();
	}

	/**
	 * Renders the grid on the drawing panel
	 * @param gridSizeMM the grid size specified in MM to set
	 */
	public void renderGrid(int gridSizeMM) {
		
		this.gridLines.clear();
		
		if(gridIsOn) {
			
		// 1. Find conversion factor from mm to DPI
		// ------------------------------------------
		int grid = (int) ((gridSizeMM * SettingsFrame.DEFAULT_DPI)/25.4);
		
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
					if(count % SettingsFrame.GRID_MAJOR_INTERVAL == 0) {
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
	 * Finish drawing of a path, if the current layer is a polygon, the path will be closed and filled<br>
	 * Creates a new feature in the current layer<br>
	 * @param pointList the current list of points (should be more than 3) to set
	 * @param currentLayer the current Layer to set
	 * @return returns the path 
	 */
	public Path2D finishPath(List<Rectangle2D> pointList, Layer currentLayer) {
		
		// Construct a new (open) path
		Path2D path = new Path2D.Double();
		
		// Start at the first item on the list
		path.moveTo(pointList.get(0).getCenterX(), pointList.get(0).getCenterY());
		
		// Create a new feature with an ID
		
		
		// Connect the path and add all the vertices to the feature
		for(Rectangle2D vertex : pointList) {
			path.lineTo(vertex.getCenterX(), vertex.getCenterY());
		}
		
		// Close path , if the current layer is a polygon
		if(currentLayer.getLayerType().equals(SettingsFrame.POLYGON_GEOMETRY)){
			
			path.closePath();
			
			PolygonItem featurePolygon = new PolygonItem(currentLayer.getNextFeatureID(), path);
			featurePolygon.getVertices().addAll(pointList);
			// Set the path as the shape of the feature
			featurePolygon.setShape(path);
			featurePolygon.setFeatureType(currentFeatureType);
			
			// Add to the current list of features in the layer
			currentLayer.getListOfFeatures().add(featurePolygon);
			
			// Change the status of the layer to be unsaved
			currentLayer.setNotSaved(true);
		}
		
		else if (currentLayer.getLayerType().equals(SettingsFrame.POLYLINE_GEOMETRY)) {
			
			PolylineItem featurePolyline = new PolylineItem(currentLayer.getNextFeatureID(), path);
			featurePolyline.getVertices().addAll(pointList);
			// Set the path as the shape of the feature
			featurePolyline.setShape(path);
			featurePolyline.setFeatureType(currentFeatureType);
			
			// Add to the current list of features in the layer
			currentLayer.getListOfFeatures().add(featurePolyline);
			
			// Change the status of the layer to be unsaved
			currentLayer.setNotSaved(true);
			
		}
		
		repaint();
		
		return path;
	}

	/**
	 * Cleans up the drawing. 
	 * Panel repainted automatically.
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
		
		//
		this.lastDraggedFeature = null;
		
		//
		this.draggedPoints.clear();
		this.draggedVertex.clear();
		
		for(Layer layer : TableOfContents.layerList) {
			layer.highlightAllFeatures(false);
		}
		
		MainFrame.updateDrawButtonGroup();
		
		repaint();
	}

	/**
	 * Shows animated hint on the drawing panel.<br>
	 * This can be turned on/off at the settings frame.
	 * @param message the Message to display
	 * @param stateColor the state color e.g Settings.LAYER_CREATED_COLOR
	 */
	public void showAnimatedHint(String message, Color stateColor) {
		
		if(SettingsFrame.HINT.getState() == true) {
			
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
	}

	/**
	 * The mouseClicked method
	 * @param e the MouseEvent to set
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		
		if (selectionModeIsOn) {
			
			handleSelectionMode(this.selectionCursor);
		}
		
		else if ( editModeIsOn ) {
			
			handleEditingMode(e);
				
		} else if (!editModeIsOn && (!selectionModeIsOn || !queryModeIsOn)) {
			
			MainFrame.log("Drawing attempted but edit session is off");	
		}
	}

	/**
	 * the mouseMoved method
	 * @param e the MouseEvent to set
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		
		setDrawingCursor(e);
		
		if( queryModeIsOn ) {
			
			movingMouseTip = new TextItem(e.getPoint(), "Draw bounds to select features");
			
			setCurrentMouseGuide(movingMouseTip, SettingsFrame.DEFAULT_LAYER_COLOR);
			
			repaint();
		} 
		
		else
			
			handleMouseMovedDrawing(e);
	}

	/**
	 * the mouseEntered method
	 * @param arg0 the MouseEvent to set
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * the mouseExited method
	 * @param e the MouseEvent to set
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		
		this.movingMouseTip = null;
		repaint();
	}

	/**
	 * the MouseEvent to set
	 * @param e the MouseEvent to set
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		
		// For selection with rectangle
		if( queryModeIsOn ) {
			
			for(Layer layer : TableOfContents.layerList) {
				layer.highlightAllFeatures(false);
			}
			
			if(this.draggedPoints.size() > 1) {
				cleanUpDrawing();
			}
			
			this.draggedPoints.add(e.getPoint());
			
		} 
		
		// For dragging vertices
		// Conditions: 
		//a. Edit mode is one
		//b. No drawing tool selected
		//c. The mouse pressed is not a right click
		else if ( editModeIsOn && MainFrame.getCurrentFeatureType() == null) {
			
			if(!SwingUtilities.isRightMouseButton(e)) {
				// Get the pressed vertix and the feature itself 
				for(Feature feature : currentLayer.getListOfFeatures()) {
					for(int i = 0; i < feature.getVertices().size(); i++) {
						Rectangle2D vertix = feature.getVertices().get(i);
						if(vertix.contains(e.getPoint())) {
							this.pressedVertexIndex = i;
							this.lastDraggedFeature = feature;
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * The mouseReleased method
	 * @param e the MouseEvent to set
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		
		if( queryModeIsOn ) {
			
			if( this.queryBounds != null ) {
				
				handleSelectionMode(this.queryBounds);
			}
		}
		
		if( editModeIsOn ) {
		
			if(!SwingUtilities.isRightMouseButton(e)) {
				
				if(this.lastDraggedVertex != null) {
					
					int index = this.pressedVertexIndex;

					this.lastDraggedFeature.getVertices().remove(index);
					this.lastDraggedFeature.getVertices().add(index, lastDraggedVertex);
					currentLayer.setNotSaved(true);
					
					repaint();
					
					this.lastDraggedVertex = null;
					this.lastDraggedFeature = null;
				}
			}
		}

	}
	
	/**
	 * The mouseDragged method
	 * @param e the MouseEvent to set
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		
		findSnaps(e);
	
		Point2D draggedPoint = e.getPoint();
		if(snapPoint != null ) {
			draggedPoint = new Point2D.Double(snapPoint.getCenterX(), snapPoint.getCenterY());
		}
		
		// Mouse drag during query mode
		if( queryModeIsOn ) {
			
			// The dragged points will be cleared at the mouse released
			// So, for showing the rectangle as user drags, only one point is needed 
			// on the list, for a new selection
			
			if(this.draggedPoints.size() == 1) {
				
				// Compute a rectangle with the point and the current  mouse position
				this.queryBounds = (Rectangle2D) getRectangleShape(this.draggedPoints.get(0), draggedPoint);
				
				// Create a moving mouse tip
				movingMouseTip = new TextItem(e.getPoint(), "Draw bounds to select features");
				
				// Set the mouse tip
				setCurrentMouseGuide(movingMouseTip, SettingsFrame.DEFAULT_LAYER_COLOR);
				
				// Repaint
				repaint();
			}
		}
		
		// Mouse drag during editing mode
		if( editModeIsOn ) {
			if(displayVertices) {
				// For non-right clicks
				if(!SwingUtilities.isRightMouseButton(e)) {
					// Dragging/ editing vertices
					if(this.lastDraggedFeature != null ) {
						handleVertixDragging(draggedPoint);
					}
				}
			}
		}
	}
	
	/**
	 * Deletes currently selected item
	 */
	public void deleteSelectedItem() {
		
		if(DrawingJPanel.currentLayer != null) {
			for(int i = 0; i < DrawingJPanel.currentLayer.getListOfFeatures().size(); i++) {
				Feature feature = DrawingJPanel.currentLayer.getListOfFeatures().get(i);
				if(feature.isHighlighted()) {
					DrawingJPanel.currentLayer.getListOfFeatures().remove(feature);
					DrawingJPanel.currentLayer.setNotSaved(true);
					repaint();
				}
			}
		}
	}
	
	/**
	 * Display vertices or not
	 */
	public void toggleDisplayVertices() {
	
		if(displayVertices) {
			displayVertices = false;
		} else {
			displayVertices = true;
		}
		
		repaint();
		
	}
}
