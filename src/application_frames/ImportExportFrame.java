package application_frames;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import core_components.ToolIconButton;
import custom_components.CustomJFrame;

/**
 * Frame for importing and exporting files such as csv files and geojson<br>
 * <p>
 * It served both the export and the import commands from the Mainframe and the command
 * be set when it is initialised.
 * This command simply changes the display text and the title of the frame,
 * the command can be such as, "Import from " or "Export to ".<br>
 * Has two major buttons, csv and geojson which can be accessed and an action listener can be 
 * attached to it where it is needed.<br>
 * The frame is not resizeable and it can be disposed.<br>
 * 
 * @author Olumide Igbiloba
 * @since Dec 28, 2017
 *
 */

public class ImportExportFrame extends CustomJFrame {

	private static final long serialVersionUID = 1L;
	
	private ToolIconButton csvLoader;
	private ToolIconButton geoJsonLoader;
	
	private String operation = "";
	
	public ImportExportFrame(String operation) {
		this.operation = operation;
		
		super.setTitle(operation);
	    Container contentPane = getContentPane();
	    JPanel panel = new JPanel();
	    csvLoader = new ToolIconButton("CSV", "/images/csv.png", 50, 50);
	    geoJsonLoader = new ToolIconButton("GEOJSON", "/images/geojson.png", 50, 50);
	    csvLoader.setText(operation + " Csv");
	    geoJsonLoader.setText(operation + " GeoJson");
	    panel.setLayout(new GridLayout(2, 1, 10, 10));
	    panel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    panel.add(csvLoader);
	    panel.add(geoJsonLoader);
	    contentPane.add(panel);
	    Dimension dim = new Dimension(300, 200);
	    setBounds(SettingsFrame.window.x  + (SettingsFrame.window.width - dim.width) / 2, 
				SettingsFrame.window.y + (SettingsFrame.window.height - dim.height) / 2,
				dim.width,
				dim.height);
	    panel.setBackground(Color.WHITE);
	    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    setResizable(false);
	}
	
	/**
	 * Sets the Operation
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * Returns the operation
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * Returns the csvLoader
	 * @return the csvLoader
	 */
	public ToolIconButton getCsvLoader() {
		return csvLoader;
	}

	/**
	 * Returns the geoJsonLoader
	 * @return the geoJsonLoader
	 */
	public ToolIconButton getGeoJsonLoader() {
		return geoJsonLoader;
	}

}
