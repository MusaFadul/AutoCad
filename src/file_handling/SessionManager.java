package file_handling;

import application_frames.SettingsFrame;
import core_components.TableOfContents;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import application_frames.MainFrame;
import core_classes.Layer;

/**
 * An object used to save and load sessions. Sessions are groups of active layers. A session file should end in '.gmcm'
 * and store a layer name on each line.
 * @author Isaac
 * @since 19/12/17
 * @version 1
 */
public class SessionManager {

    /**
     * String array representing the names of currently active layers in the table of contents.
     */
    String[] currentActiveLayers;
    FileNameExtensionFilter sessionFileFilter = new FileNameExtensionFilter("GMCM Application Session Files", "gmcm");

    /**
     * Constructor for SessionManager. Builds the currentActiveLayers array.
     * @param tableOfContents the table of contents from the main frame.
     */
    public SessionManager(TableOfContents tableOfContents) {

        currentActiveLayers = tableOfContents.getListOfLayersInString();

    }

    /**
     * Saves the current session to a file.  The first 14 lines are application/drawing parameters, the remaining lines are layers.
     * @param sessionPath String representing path to file to which to save the session.
     */
    public void saveCurrentSession(String sessionPath) {

        try {

            // Initialize a BufferedWriter and write each layer name to a new line therein, then save & close.
        	BufferedWriter sessionWriter = new BufferedWriter(new FileWriter(sessionPath));

        	// Write the project name
        	sessionWriter.write(MainFrame.projectName.getText() + "\n");

        	// Write the grid size
        	sessionWriter.write(SettingsFrame.GRID_MM + "\n");

        	// Write the snap size
        	sessionWriter.write(SettingsFrame.SNAP_SIZE + "\n");

        	// Write the grid color
        	sessionWriter.write(SettingsFrame.GRID_COLOR.getBackground().getRed() + " " +
					SettingsFrame.GRID_COLOR.getBackground().getGreen() + " " +
					SettingsFrame.GRID_COLOR.getBackground().getBlue() + " " + "\n");

        	// Write the background color
        	sessionWriter.write(SettingsFrame.DRAFTING_BACKGROUND.getBackground().getRed() + " " +
					SettingsFrame.DRAFTING_BACKGROUND.getBackground().getGreen() + " " +
					SettingsFrame.DRAFTING_BACKGROUND.getBackground().getBlue() + " " + "\n");

        	// Write the feature highlight color
			sessionWriter.write(SettingsFrame.FEATURE_HIGHLIGHTED_STATE_COLOR.getBackground().getRed() + " " +
					SettingsFrame.FEATURE_HIGHLIGHTED_STATE_COLOR.getBackground().getGreen() + " " +
					SettingsFrame.FEATURE_HIGHLIGHTED_STATE_COLOR.getBackground().getBlue() + " " + "\n");

			// Write the selection box color
			sessionWriter.write(SettingsFrame.SELECTION_COLOR.getBackground().getRed() + " " +
					SettingsFrame.SELECTION_COLOR.getBackground().getGreen() + " " +
					SettingsFrame.SELECTION_COLOR.getBackground().getBlue() + " " + "\n");

			// Write the snap toggle value
        	sessionWriter.write(String.valueOf(SettingsFrame.snapToggle.getState()) + "\n");

        	// Write the grid toggle value
        	sessionWriter.write(String.valueOf(SettingsFrame.gridToggle.getState()) + "\n");

			// Write the new text layer name
        	sessionWriter.write(SettingsFrame.txtNewlayer.getText() + "\n");

        	// Write the new document name
        	sessionWriter.write(SettingsFrame.txtNewDoc.getText() + "\n");

        	// Write the theme selection
        	sessionWriter.write(SettingsFrame.themeCmbBox.getSelectedIndex() + "\n");

			// Write the hint status selection
        	sessionWriter.write(String.valueOf(SettingsFrame.HINT.getState()) + "\n");

        	// Write the autosave selection
			sessionWriter.write(String.valueOf(SettingsFrame.AUTOSAVE_TOGGLE.getState()) + "\n");

			// Write the relevant properties of each layer
        	for(Layer layer : TableOfContents.layerList) {
        		MainFrame.saveLayerToDB(layer);
        		sessionWriter.write(layer.getLayerName() + " "  + layer.getLayerColor().getRed() + " "
						+ layer.getLayerColor().getGreen() + " "  + layer.getLayerColor().getBlue() + " "
						+ layer.isVisible() +  "\n");
        	}
        	sessionWriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Opens a session from a saved file and returns its contents, each line converted to an entry in a String ArrayList, which is then returned.
     * @param sessionPath String representing path to file where the session is stored.
     * @return ArrayList of layer name Strings.
     */
    public ArrayList<String> openSession(String sessionPath) {

        // Initialize our list of layer names.
        ArrayList<String> layerList = new ArrayList<>(0);

        try {
            // Read all the names from the file and add them to the returned layer list.
            BufferedReader sessionReader = new BufferedReader(new FileReader(sessionPath));
            String line;

			// get title & layers
            while ((line = sessionReader.readLine()) != null) {
                layerList.add(line);
            }
            sessionReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return layerList;

    }

	/**
	 * Handler function for saving a session. Launches the file chooser and calls the saveSession method. Saves files with
	 * a '.gmcm' extension, which contain information about the drawing settings and the layers currently active.
	 */
	public void onSaveSessionIntent() {
		
		JFileChooser saveSessionFileChooser = new JFileChooser();
		saveSessionFileChooser.setFileFilter(sessionFileFilter);

		// Once the user selects a file name, write the session to it.
		int saveSessionReturnVal = saveSessionFileChooser.showSaveDialog(null);
		if (saveSessionReturnVal == JFileChooser.APPROVE_OPTION) {
			File saveSession = saveSessionFileChooser.getSelectedFile();
			String saveSessionPath = saveSession.getPath();
			if (!saveSessionPath.substring(saveSessionPath.length()-5).equals(".gmcm")) {
				saveSessionPath += ".gmcm";
			}
			saveCurrentSession(saveSessionPath);
		}
	}

	/**
	 * Handler function for opening a session. Launches the file chooser, and parses the selected file, creating a
	 * new session from its contents by calling the openSession method.<br>
	 *     <ul>
	 *         <li>Gets the selected file from the user.</li>
	 *         <li>Reads the first few lines of the file, which contain things like title, drawing settings, etc. and applies them.</li>
	 *         <li>Totally removes the current layers and drawing settings.</li>
	 *         <li>Reads the remaining lines of the tile, which contain the necessary parameters to construct layers from the database.</li>
	 *     </ul>
	 */
	public void onOpenSessionIntent() {
		
		JFileChooser openSessionFileChooser = new JFileChooser();
		openSessionFileChooser.setFileFilter(sessionFileFilter);

		// When the user selects a file, read it with the SessionManager and try to add the layers from the database, if they exist.
		int openSessionReturnVal = openSessionFileChooser.showOpenDialog(null);
		if (openSessionReturnVal == JFileChooser.APPROVE_OPTION) {
			File openSession = openSessionFileChooser.getSelectedFile();
			ArrayList<String> sessionContents = openSession(openSession.getPath());

			// Abandon the current edit session if there is one.
			MainFrame.panel.abandonEditSession();

			// Clear the rows of the TableModel representing the Table of Contents.
			if (MainFrame.tableOfContents.getModel().getRowCount() > 0) {
				//System.out.println(TableOfContents.tableModel.getRowCount());
				for (int i = MainFrame.tableOfContents.getModel().getRowCount() - 1; i > -1; i--) {
					TableOfContents.removeRowLayer(i);
				}
			}

			// Set project name with the first line in the contents.
			MainFrame.projectName.setText(sessionContents.get(0));

			// Set grid size
			SettingsFrame.GRID_MM = Integer.parseInt(sessionContents.get(1));
			SettingsFrame.gridSizeSpinner.setValue(Integer.parseInt(sessionContents.get(1)));
			MainFrame.panel.renderGrid(SettingsFrame.GRID_MM);

			// Set snap size
			SettingsFrame.SNAP_SIZE = Integer.parseInt(sessionContents.get(2));
			SettingsFrame.snapSizeSpinner.setValue(Integer.parseInt(sessionContents.get(2)));

			// Set grid color
			String[] gridColorRGB = sessionContents.get(3).split(" ");
			SettingsFrame.GRID_COLOR.setBackground(new Color(Integer.parseInt(gridColorRGB[0]),
					Integer.parseInt(gridColorRGB[1]),
					Integer.parseInt(gridColorRGB[2])));

			// Set background color
			String[] backgroundColorRGB = sessionContents.get(4).split(" ");
			SettingsFrame.DRAFTING_BACKGROUND.setBackground(new Color(Integer.parseInt(backgroundColorRGB[0]),
					Integer.parseInt(backgroundColorRGB[1]),
					Integer.parseInt(backgroundColorRGB[2])));
			
			// Set highlight color
			String[] highlightColorRGB = sessionContents.get(5).split(" ");
			SettingsFrame.FEATURE_HIGHLIGHTED_STATE_COLOR.setBackground(new Color(Integer.parseInt(highlightColorRGB[0]),
					Integer.parseInt(highlightColorRGB[1]),
					Integer.parseInt(highlightColorRGB[2])));
			
			// Set selection color
			String[] selectionColorRGB = sessionContents.get(6).split(" ");
			SettingsFrame.SELECTION_COLOR.setBackground(new Color(Integer.parseInt(selectionColorRGB[0]),
					Integer.parseInt(selectionColorRGB[1]),
					Integer.parseInt(selectionColorRGB[2])));

			// Set snap toggle
			SettingsFrame.snapToggle.setState(Boolean.valueOf(sessionContents.get(7)));
			if(MainFrame.panel.snappingModeIsOn != Boolean.valueOf(sessionContents.get(7))) {
				MainFrame.btnSnap.doClick();
				if(Boolean.valueOf(sessionContents.get(5)) == false) {
					MainFrame.btnSnap.setBackground(SettingsFrame.DEFAULT_STATE_COLOR);
				}

			}

			// Set grid toggle
			SettingsFrame.gridToggle.setState(Boolean.valueOf(sessionContents.get(8)));
			if(MainFrame.panel.gridIsOn != Boolean.valueOf(sessionContents.get(8))) {
				MainFrame.btnGrid.doClick();
				if(Boolean.valueOf(sessionContents.get(7)) == false) {
					MainFrame.btnGrid.setBackground(SettingsFrame.DEFAULT_STATE_COLOR);
				}
			}

			// Set new layer text
			SettingsFrame.txtNewlayer.setText(sessionContents.get(9));

			// Set new document text
			SettingsFrame.txtNewDoc.setText(sessionContents.get(10));

			// Set theme
			SettingsFrame.themeCmbBox.setSelectedIndex(Integer.parseInt(sessionContents.get(11)));

			// Set showing hints
			if(SettingsFrame.HINT.getState() != Boolean.valueOf(sessionContents.get(12))) {
				SettingsFrame.HINT.doClick();
			}

			// Set autosave on close
			if(SettingsFrame.AUTOSAVE_TOGGLE.getState() != Boolean.valueOf(sessionContents.get(13))) {
				SettingsFrame.AUTOSAVE_TOGGLE.doClick();
			}

			// Add layers to current session.
			boolean openSessionSuccess = true;
			for (int i=0; i<sessionContents.size(); i++) {

				// first 8 rows are not layers
				if (i>13) {

					// Get a line
					String sessionContentsLine = sessionContents.get(i);

					String[] newLayerBlueprints = sessionContentsLine.split(" ");
					String layerName = newLayerBlueprints[0];
					Color layerColor = new Color(Integer.parseInt(newLayerBlueprints[1]), Integer.parseInt(newLayerBlueprints[2]),
							Integer.parseInt(newLayerBlueprints[3]));
					boolean layerVisible = Boolean.valueOf(newLayerBlueprints[4]);

					try {
						ResultSet layerContents = MainFrame.dbConnection.readTable(layerName);
						Layer newLayer = MainFrame.createLayerFromResultSet(layerContents, layerName);

						// set layer properties from blueprints (name is already set from createLayerFromResultSet
						newLayer.setLayerColor(layerColor);
						newLayer.setVisible(layerVisible);

						// Check or uncheck the checkbox indicating layer visibility
						TableOfContents.tableModel.setValueAt(layerVisible, TableOfContents.tableModel.getRowCount()-1, 0);

					} catch (Exception ex) {
						MainFrame.log("Table '" + sessionContentsLine + "' does not exist in database.");
						openSessionSuccess = false;
					}
				}
			}
			if (openSessionSuccess) {
				MainFrame.log("Restored session '" + openSession.getName() + "' successfully");
			} else {
				MainFrame.log("Failure when restoring session '" + openSession.getName() + "'.");
			}
		}
	}
}
