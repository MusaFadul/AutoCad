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
 * Frame for opening and saving drawing sessions.<br>
 * <br>
 * Has two major buttons, open and save which can be accessed and an action listener can be 
 * attached to it where it is needed.<br>
 * The frame is not resizable and it is never closed when the close button is pressed, but hidden,
 * it is available throughout the drawing session once it has been initialized.<br>
 * 
 * @author Olumide Igbiloba
 * @since Dec 28, 2017
 *
 */
public class FilesFrame extends CustomJFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/*Save file button*/
	private ToolIconButton saveFileButton;
	/*Open file button*/
	private ToolIconButton openFileButton;
	
	/**
	 * Class constructor
	 */
	public FilesFrame() {

		super.setTitle("Files");
	    Container contentPane = getContentPane();
	    JPanel panel = new JPanel();
	    saveFileButton = new ToolIconButton("SaveQ", "/images/save_file.png", 50, 50);
	    openFileButton = new ToolIconButton("OpenQ", "/images/open_file.png", 50, 50);
	    saveFileButton.setText("Save current project");
	    openFileButton.setText("Open existing project");
	    panel.setLayout(new GridLayout(2, 1, 10, 10));
	    panel.setBorder(new EmptyBorder(10, 10, 10, 10));
	    panel.add(saveFileButton);
	    panel.add(openFileButton);
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
	 * Returns the saveButton
	 * @return the saveButton
	 */
	public ToolIconButton getSaveButton() {
		return saveFileButton;
	}


	/**
	 * Returns the opebButton
	 * @return the openButton
	 */
	public ToolIconButton getOpenButton() {
		return openFileButton;
	}
}
