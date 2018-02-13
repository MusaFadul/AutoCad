package core_components;

import javax.swing.JToggleButton;
import application_frames.MainFrame;
import application_frames.SettingsFrame;
import toolset.Tools;

/**
 * Draw Icon button for drawing different shapes on the drawing panel.<br>
 * The created buttons are further added to the MainFrame's drawing button group which handles the 
 * behaviors related to the drawing of shapes on the panel.<br>
 * @author Olumide Igbiloba
 * @since Dec 7, 2017
 */
public class DrawIconButton extends JToggleButton {

	private static final long serialVersionUID = 1L;
	
	private String featureType ;
	private String geometryFamily;
	
	/**
	 * Creates the DrawIconButton
	 * @param name the name of the DrawIconButton to set
	 * @param geometryFamily the geometryFamily of the DrawIconButton to set
	 * @param iconPath the iconPath of the DrawIconButton to set
	 * @param x the x position of the DrawIconButton to set
	 * @param y the y position of the DrawIconButton to set
	 */
	
	public DrawIconButton(String name, String geometryFamily, String iconPath, int x, int y) {
		super();
		this.featureType = name;
		this.geometryFamily = geometryFamily;
		setIcon(Tools.getIconImage(iconPath, x, y));
		setBorderPainted(false);
		setFocusPainted(false);
		setActionCommand(name);
		setBackground(SettingsFrame.DEFAULT_STATE_COLOR);
		
		MainFrame.drawButtonGroup.add(this);
	}

	/**
	 * Returns the featureType
	 * @return the featureType
	 */
	public String getFeatureType() {
		return featureType;
	}

	/**
	 * Returns the geometryFamily
	 * @return the geometryFamily
	 */
	public String getGeometryFamily() {
		return geometryFamily;
	}
}
