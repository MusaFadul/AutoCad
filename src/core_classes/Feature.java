/**
 * 
 */
package core_classes;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import application_frames.SettingsFrame;
import toolset.Tools;

/**
 * Defines the structure of a feature in the system.<br>
 * A feature will be a subset of a layer and will be of particular geometry type
 * the same with all the features in the layer<br>
 * <p>
 * A feature is unique with its ID, this ID is stored in the database. 
 * The layer class manages this ID and assigns ID to the feature, based on the time of creation.<br>
 * <p>
 * A feature is represented by its shape on the drawing panel.<br>
 * This shape contains vertices (for non ellipse shapes), the vertices are rectangle and 
 * its size is determined based on the current size of the snap @see Settings class.<br>
 * So, to save these vertices in the database, the center of the vertices is used.<br>
 * However, for features that are ellipse (or circle), only the center of the ellipse is stored as a vertix
 * and the radius of the X and Y is stored in the database as well<br>
 * <p>
 * The drawing panel displays a feature based on its visibility status.<br>
 * A feature can be directly selected on the drawing panel, when a feature is selected, the highlited status 
 * is set to true automatically<br>
 * 
 * @author Olumide Igbiloba
 * @since Dec 7, 2017
 * @version
 * a. Dec 8, 2017 - Added a parameter to return the coordinates array in double for the database <br>
 * b. Dec 12, 2017 - Added the parameter to store the center of the feature if it is an ellipse <br>
 */
public class Feature {
	
	/** The unique id a feature. This ID is stored in the database */
    private int id;
    /** The layer id where the feature belongs to */
	private int layerID;
    /** Shape contianed by the feature eg. Rectangle, Path  */
    private Shape shape;
    /** Feature type such as rectangle, circle */
    private String featureType;
	/** Highlight status of the layer, this changes the color at the drawing panel*/
	private boolean isHighlighted = false;
	/** The Visibility of a feature, when feature is not visible, it will be not painted, nor selectable */
	private boolean isVisibile = true;
    /** The vertices of the shape, this is used for rendering purposes by the drawing panel */
    private List<Rectangle2D> vertices = new ArrayList<Rectangle2D> ();
    /** Coordinates array (XY) of the feature vertices in double, this is needed for the database,
     * this coords comes from the center of all the vertices */
	private double[][] coordinatesArrayXY;
    /** Variable to know if a feature is an ellipse or not */
    private boolean isEllipse = false;
    /** The center point of a feature, this is particular for an ellipse or a circle */
    private Point2D center;
    /** The radius X and Y of the feature if it is an ellipse */
    private double radiusX, radiusY;
    
    /**
     * Creates an Object of the class Layer
     * @param id the ID of a Feature
     */
    public Feature(int id) {
        this.id = id;
    }

    /**
     * Returns the ID of a Feature
     * @return id 
     */
    public int getId() {
        return this.id;
    }
    
    /**
     * Sets the ID of a Feature
     * @param id unique id of the feature
     */
    public void setId(int id) {
       this.id = id;
    }

	/**
	 * Returns a list of vertices. The vertices are used for rendering. They are all Rectangle 2D <br>
	 * @return The vertices as a list of Rectangle 2D
	 */
	public List<Rectangle2D> getVertices() {
		return vertices;
	}
	
	/**
	 * Sets the vertices (Rectangle 2D used for rendering.
	 * @param vertices the vertices to set
	 */
	public void setVertices(List<Rectangle2D> vertices) {
		if(!isEllipse) {
			this.vertices = vertices;
		}
	}
	
	/**
	 * Sets the vertices of a Feature from an input integer array
	 * @param xp Array of x coordinates of the vertices
	 * @param yp Array of y coordinates of the vertices
	 */
	
	public void setVerticesFromArray(int[] xp, int[] yp) {
		
		if(!isEllipse) {
			double[] x = Tools.copyFromIntArray(xp);
			double[] y = Tools.copyFromIntArray(xp);
			
			int snapSize = SettingsFrame.SNAP_SIZE;
			
			for(int i = 0; i < x.length; i++) {
				this.vertices.add(new Rectangle2D.Double(x[i] - (snapSize/2), y[i] - (snapSize/2), snapSize, snapSize));
			}
		}
	}
	
	/**
	 * Sets the vertices of a Feature from an input double array
	 * @param x Array of x coordinates of the vertices
	 * @param y Array of y coordinates of the vertices
	 */
	public void setVerticesFromDoubleArray(double[] x, double[] y) {
		
		if(!isEllipse) {
			int snapSize = SettingsFrame.SNAP_SIZE;
			
			for(int i = 0; i < x.length; i++) {
				this.vertices.add(new Rectangle2D.Double(x[i] - (snapSize/2), y[i] - (snapSize/2), snapSize, snapSize));
			}
		}
	}
	
	/**
	 * Returns the Shape of a Feature
	 * @return the Shape
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Sets the shape of a Feature
	 * @param shape the shape to set
	 */
	public void setShape(Shape shape) {
		this.shape = shape;
	}

	/**
	 * Returns the type of a Feature
	 * @return the featureType
	 */
	public String getFeatureType() {
		return featureType;
	}

	/**
	 * Sets the type of a Feature
	 * @param featureType the featureType to set
	 */
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}

	/**
	 * Returns true, if a Feature is an Ellipse
	 * @return the isEllipse
	 */
	public boolean isEllipse() {
		return isEllipse;
	}

	/**
	 * Sets the parameters of an Ellipse
	 * @param isEllipse the isEllipse to set
	 * @param center the center of an Ellipse to set
	 * @param radiusX the x radius of an Ellipse to set
	 * @param radiusY the y radius of an Ellipse to set
	 */
	public void setEllipse(boolean isEllipse, Point2D center, double radiusX, double radiusY) {
		
		this.radiusX = radiusX;
		this.radiusY = radiusY;
		this.setCenter(center);
		this.isEllipse = isEllipse;
		
		if(isEllipse) {
			this.vertices.clear();
			// Center
			vertices.add(new Rectangle2D.Double(center.getX() - (SettingsFrame.SNAP_SIZE / 2), center.getY() - (SettingsFrame.SNAP_SIZE / 2),
					SettingsFrame.SNAP_SIZE, SettingsFrame.SNAP_SIZE));
			// X
			vertices.add(new Rectangle2D.Double((center.getX() + radiusX) - (SettingsFrame.SNAP_SIZE / 2), center.getY()  - (SettingsFrame.SNAP_SIZE / 2),
					SettingsFrame.SNAP_SIZE, SettingsFrame.SNAP_SIZE));
			
			// Y
			if(radiusX != radiusY) {
				vertices.add(new Rectangle2D.Double(center.getX() - (SettingsFrame.SNAP_SIZE / 2), (center.getY() - radiusY)  - (SettingsFrame.SNAP_SIZE / 2),
						SettingsFrame.SNAP_SIZE, SettingsFrame.SNAP_SIZE));
			}
		}
	}

	/**
	 * Returns the center coordinates of a Feature's vertices
	 * @return the coordinatesArrayXY
	 */
	public double[][] getCoordinatesArrayXY() {

		double[] x = new double[vertices.size()];
		double[] y = new double[vertices.size()];
		
		for (int i = 0 ; i < vertices.size(); i++) {
			x[i] = vertices.get(i).getCenterX();
			y[i] = vertices.get(i).getCenterY();
		}

		double[][] newCoords = {x, y};
		
		coordinatesArrayXY = newCoords;
		
		return coordinatesArrayXY;
	}

	/**
	 * Sets the coordinates array of a Feature for x and y
	 * @param coordinatesArrayXY the coordinatesArrayXY to set
	 */
	public void setCoordinatesArrayXY(double[][] coordinatesArrayXY) {
		this.coordinatesArrayXY = coordinatesArrayXY;
	}
	
	/**
	 * Returns the x radius of an Ellipse
	 * @return the radiusX
	 */
	public double getRadiusX() {
		return radiusX;
	}

	/**
	 * Returns the y radius of an Ellipse
	 * @return the radiusY
	 */
	public double getRadiusY() {
		return radiusY;
	}

	/**
	 * Sets the RadiusX of an Ellipse
	 * @param radiusX the radiusX to set
	 */
	public void setRadiusX(double radiusX) {
		this.radiusX = radiusX;
	}

	/**
	 * Sets the RadiusX of an Ellipse
	 * @param radiusY the radiusY to set
	 */
	public void setRadiusY(double radiusY) {
		this.radiusY = radiusY;
	}

	/**
	 * Returns the Layer ID of which a Feature belongs to
	 * @return the layerID
	 */
	public int getLayerID() {
		return layerID;
	}

	/**Sets the ID of the Layer a Feature belongs to
	 * @param layerID the layerID to set
	 */
	public void setLayerID(int layerID) {
		this.layerID = layerID;
	}

	/**
	 * Returns the highlighting status of a Feature
	 * @return the isHighlighted
	 */
	public boolean isHighlighted() {
		return isHighlighted;
	}

	/**
	 * Sets the highlighting status of a Feature
	 * @param isHighlighted the isHighlighted to set
	 */
	public void setHighlighted(boolean isHighlighted) {
		this.isHighlighted = isHighlighted;
	}

	/**
	 * Returns the visibility status of a Feature
	 * @return the isVisibile
	 */
	public boolean isVisibile() {
		return isVisibile;
	}

	/**
	 * Sets the visibility status of a Feature
	 * @param isVisibile the isVisibile to set
	 */
	public void setVisibile(boolean isVisibile) {
		this.isVisibile = isVisibile;
	}

	/**
	 * Returns the center of a Feature
	 * @return the center
	 */
	public Point2D getCenter() {
		this.center = new Point2D.Double(getVertices().get(0).getCenterX(), getVertices().get(0).getCenterY());
		return center;
	}

	/**
	 * Sets the center of a Feature
	 * @param center the center to set
	 */
	public void setCenter(Point2D center) {
		
		this.center = center;
	}
}
