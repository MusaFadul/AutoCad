package toolset;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;

public class Settings {
	
	// Software information
	public static final String TITLE = "GMCM3_Software_Eng";
	
	// System configurations 
	public static final int DEFAULT_DPI = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
	public static final Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	// Database connection parameters
	public static String HOST = "loalhost";
	// ...
	
	// Drawing settings
	public static int gridSizeMM = 5;
	public static int snappingTolerance = 20;
	public static int gridMajorInterval = 5;
	public static int cursorSize = 25;
	public static Color cursorColor = new Color(244, 98, 66);
	public static Color defaultColor = Color.BLACK;
	public static Color DEFAULT_STATE_COLOR = new Color(31, 105, 224);
	public static final Color DEFAULT_STATE_TRANSPARENT_COLOR = new Color(31, 105, 224, 180);
	public static Color HIGHLIGHTED_STATE_COLOR = new Color(239, 66, 14);
	
	// GUI parameters
	public static final ImageIcon LAYER_DELETE_ICON = Tools.getIconImage("/images/bin.png", 15, 15);
	public static final Color DEFAULT_LAYER_COLOR = Color.BLACK;
	public static final Color DEFAULT_VERTIX_COLOR = new Color(31, 105, 224);
	public static final String DRAW_CONTINUE = "continue";
	public static final String CLOSE_POLYGON_MESSAGE = "Click point to close the polygon. Turn on the snap for precise drawing";
	public static final String CLOSE_POLYLINE_MESSAGE = "Click first or last point to close polyline";

	public static final String POLYLINE_GEOMETRY = "Polyline";
	public static final String POINT_GEOMETRY = "Point";
	public static final String POLYGON_GEOMETRY = "Polygon";

	public final Color MUTE_STATE_COLOR = Color.LIGHT_GRAY;

	public final static Color FEATURE_CREATED_COLOR = new Color (16, 91, 26);

	public static final int DEFAULT_LAYER_LINE_WEIGHT = 3;

	public static boolean DRAW_GUIDES_AND_TIPS = true;

}
