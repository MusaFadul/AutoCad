/**
 * 
 */
package tester;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import custom_components.CustomJFrame;
import toolset.RoundedBorder;
import toolset.Settings;
import toolset.Tools;

import javax.swing.JPanel;

import core_classes.Layer;
import core_components.DrawingJPanel;
import core_components.TableOfContents;
import core_components.ToolIconButton;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import application_frames.SettingsFrame;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;


/**
 * Main application frame.<br>
 * 
 * Contains a drawing panel, table of contents and general drawing tools.<br>
 * Needs connection to the database for storing and retrieving drawn shapes.<br>
 * 
 * @author OlumideEnoch
 * 
 */
public class MainFrame extends CustomJFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**Current project/ document name*/
	private JTextField projectName;
	
	/**The drawing panel for drawing shapes*/
	public static DrawingJPanel panel;
	
	/**List showing the available layers at the table of contents*/
	public static JComboBox<String[]> layerListComboBox;
	
	/**Model of the layer list combo box*/
	private static DefaultComboBoxModel<String[]> model;
	
	/**Table of contents arranging the list of layers*/
	public static TableOfContents tableOfContents;
	
	/**Text field for showing messages on the frame*/
	public static JTextField logTextField;
	
	
	
	/**
	 * Constructs the main frame
	 */
	public MainFrame() {
		
		initialize();
	}
	
	/**
	 * Arranges and initializes the application frame and sets up listeners and necessary directives.
	 */
	private void initialize() {
		
		addWindowListener(new WindowAdapter() {
			@Override 
			public void windowClosing(WindowEvent e) { 
				handleWindowClosingEvent(e);
			} 
		});
		
		getContentPane().setBackground(Color.WHITE);
		getContentPane().setLayout(null);
		
		// Panel for project description
		// Project name ....
		JPanel projectPanel = new JPanel();
		projectPanel.setBounds(10, 11, 244, 221);
		getContentPane().add(projectPanel);
		projectPanel.setLayout(null);
		
		projectName = new JTextField();
		projectName.setFont(new Font("Tahoma", Font.PLAIN, 30));
		projectName.setHorizontalAlignment(SwingConstants.CENTER);
		projectName.setText("Untitled");
		projectName.setBounds(0, 152, 244, 69);
		projectPanel.add(projectName);
		projectName.setColumns(10);
		
		JLabel applicationLogo = new JLabel("");
		applicationLogo.setIcon(Tools.getIconImage("/images/Images-icon.png", 100,100));
		applicationLogo.setHorizontalAlignment(SwingConstants.CENTER);
		applicationLogo.setBounds(0, 0, 244, 139);
		projectPanel.add(applicationLogo);
		
		// Panel containing all the tools
		// ...
		JPanel toolBarPanel = new JPanel();
		toolBarPanel.setBackground(Color.WHITE);
		toolBarPanel.setBounds(264, 11, 1640, 77);
		getContentPane().add(toolBarPanel);
		toolBarPanel.setLayout(null);
		
		ToolIconButton btnFiles = new ToolIconButton("Files", "/images/file.png", 60,60);
		btnFiles.setToolTipText("Open previous projects");
		btnFiles.setBounds(0, 0, 90, 75);
		toolBarPanel.add(btnFiles);
		
		ToolIconButton btnImport = new ToolIconButton("Import", "/images/import.png", 60,60);
		btnImport.setText("");
		btnImport.setToolTipText("Import projects from csv");
		btnImport.setBounds(99, 0, 90, 75);
		toolBarPanel.add(btnImport);
		
		ToolIconButton btnExport = new ToolIconButton("Export", "/images/export.png", 60,60);
		btnExport.setText("");
		btnExport.setToolTipText("Export current project to csv");
		btnExport.setBounds(199, 0, 90, 75);
		toolBarPanel.add(btnExport);
		
		ToolIconButton btnSelecr = new ToolIconButton("Select items", "/images/select.png", 60,60);
		btnSelecr.setToolTipText("Select items on the drawing panel");
		btnSelecr.setBounds(299, 0, 90, 75);
		toolBarPanel.add(btnSelecr);
		
		ToolIconButton btnQuery = new ToolIconButton("Clear sel", "/images/query.png", 60,60);
		btnQuery.setToolTipText("Clear selection");
		btnQuery.setBounds(399, 0, 90, 75);
		toolBarPanel.add(btnQuery);
		
		ToolIconButton btnDelete = new ToolIconButton("Delete", "/images/delete.png", 60,60);
		btnDelete.setToolTipText("Delete selected items");
		btnDelete.setBounds(499, 0, 90, 75);
		toolBarPanel.add(btnDelete);
		
		ToolIconButton btnDrawEdit = new ToolIconButton("Editing", "/images/edit.png", 60,60);
		btnDrawEdit.setToolTipText("Start edit session");
		btnDrawEdit.setBounds(599, 0, 90, 75);
		toolBarPanel.add(btnDrawEdit);
		
		btnDrawEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleEditingSession(e, btnDrawEdit);
			}
			
		});
		
		ToolIconButton btnSaveEdit = new ToolIconButton("Save edit", "/images/save.png", 60,60);
		btnSaveEdit.setToolTipText("Save edited layers");
		btnSaveEdit.setBounds(908, 0, 90, 75);
		toolBarPanel.add(btnSaveEdit);
		
				
		ToolIconButton btnDatabase = new ToolIconButton("Database", "/images/database.png", 60,60);
		btnDatabase.setBounds(1450, 0, 90, 75);
		toolBarPanel.add(btnDatabase);
		
		ToolIconButton btnSettings = new ToolIconButton("Settings", "/images/settings.png", 60,60);
		btnSettings.setBorder(new RoundedBorder(10));
		btnSettings.setBounds(1550, 0, 90, 75);
		toolBarPanel.add(btnSettings);
		
		btnSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				SettingsFrame settingsFrame = new SettingsFrame();
				settingsFrame.setVisible(true);
			}
			
		});
		
		
		layerListComboBox = new JComboBox<String[]>();
		layerListComboBox.setBackground(Settings.DEFAULT_BUTTON_COLOR);
		layerListComboBox.setForeground(Color.WHITE);
		layerListComboBox.setBounds(699, 0, 199, 33);
		toolBarPanel.add(layerListComboBox);
		
		layerListComboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				handleLayerSaving(e);
			}
		});
		
		ToolIconButton btnAddLayer = new ToolIconButton("Save edit", "/images/add.png", 60,60);
		btnAddLayer.setToolTipText("Add more layers");
		btnAddLayer.setBounds(1008, 0, 90, 75);
		toolBarPanel.add(btnAddLayer);
		
		ToolIconButton btnSnap = new ToolIconButton("Save edit", "/images/snap.png", 35,35);
		btnSnap.setToolTipText("Turn of snap");
		btnSnap.setBounds(699, 40, 35, 35);
		toolBarPanel.add(btnSnap);
		
		btnSnap.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.toggleSnap();
				
			}
		});
		
		ToolIconButton btnOrthoMode = new ToolIconButton("Save edit", "/images/ortho.png", 35, 35);
		btnOrthoMode.setToolTipText("Turn of ortho mode");
		btnOrthoMode.setBounds(744, 40, 35, 35);
		toolBarPanel.add(btnOrthoMode);
		
		ToolIconButton btnShowGrid = new ToolIconButton("Save edit", "/images/grid.png", 35, 35);
		btnShowGrid.setToolTipText("Turn on grid");
		btnShowGrid.setBounds(789, 40, 35, 35);
		toolBarPanel.add(btnShowGrid);
		
		btnShowGrid.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				panel.toggleGrid();
			}
		});
		
		
		
		btnAddLayer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				String[] list = {"Polygon", "Polyline", "Point"};
				JComboBox<String> jcb = new JComboBox<String>(list);
				
				int response = JOptionPane.showConfirmDialog( null, jcb, "Choose geometry type", JOptionPane.OK_CANCEL_OPTION);
				
				if(response == JOptionPane.OK_OPTION) {
					
					if(jcb.getSelectedItem().toString().equals("Polygon")) {
						Layer newLayer = new Layer(tableOfContents.getNewLayerID(), true, jcb.getSelectedItem().toString(), "Layer_" +  tableOfContents.getNewLayerID() );
						tableOfContents.addRowLayer(newLayer);
						log("New " + newLayer.getLayerType() + " layer: "+ newLayer.getLayerName() + " was created");
					} else
						JOptionPane.showOptionDialog(null, "Only Polygon supported for now", "WORK IN PROGRESS", JOptionPane.PLAIN_MESSAGE, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				}
			}	
		});
		
		tableOfContents = new TableOfContents();
		JScrollPane tableOfContentsScrollPane = new JScrollPane(tableOfContents);
		tableOfContentsScrollPane.setBounds(10, 279, 244, 721);
		getContentPane().add(tableOfContentsScrollPane);
		
		
		panel = new DrawingJPanel(new Rectangle(264, 99, 1640, 901));
		panel.setBorder(new LineBorder(new Color(0,171,220)));
		panel.setBackground(Color.WHITE);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btnOrtho = new JButton("Ortho");
		btnOrtho.setBounds(825, 804, 75, 65);
		//panel.add(btnOrtho);
		
		JButton btnGrid = new JButton("Grid");
		btnGrid.setBounds(910, 804, 75, 65);
		//panel.add(btnGrid);
		
		logTextField = new JTextField();
		logTextField.setOpaque(false);
		logTextField.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		logTextField.setBounds(10, 870, 349, 20);
		panel.add(logTextField);
		logTextField.setColumns(10);
		
		btnGrid.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				panel.toggleGrid();
			}
			
		});
		
		JLabel tOCLabel = new JLabel("Table of contents");
		tOCLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		tOCLabel.setBounds(10, 243, 244, 33);
		getContentPane().add(tOCLabel);
		
	}
	
	/**
	 * Handles when the user wants to start editing session <br>
	 * Checks if there are no layer on the table of contents first then
	 * starts the editing session. <br>
	 * The background of the button is left active during the duration of the edit session
	 * @param e
	 * @param btnDrawEdit button where the action event comes from
	 */
	protected void handleEditingSession(ActionEvent e, ToolIconButton btnDrawEdit) {
		
		if(layerListComboBox.getModel().getSize() > 0) {
			
			panel.toggleEditStart(layerListComboBox.getSelectedIndex(), "");
			log("Edit session started on " + DrawingJPanel.currentLayer.getLayerName());
		
		} else {
			
			log("Drawing was attempted but no layer to edit on the list");
			JOptionPane.showMessageDialog(null, "No layer is selected, add a layer", "Choose a layer", JOptionPane.ERROR_MESSAGE);
			
			// Release the button from pressed state
			btnDrawEdit.setButtonReleased(true);
		}
	}

	/**
	 * Protocol to save and load layers from the database <br>
	 * Event is invoked when a layer is not saved and another layer was selected to be edited
	 * @param e item event coming from the combo box model
	 */
	protected void handleLayerSaving(ItemEvent e) {
		
		if (e.getStateChange() == ItemEvent.DESELECTED){
	         
	           int index = model.getIndexOf(e.getItem());
	           int layerid = (int) tableOfContents.getModel().getValueAt(index, TableOfContents.LAYER_ID_COL_INDEX);
	           Layer layer = TableOfContents.findLayerWithID(layerid);
	           
	           if(layer.isNotSaved() && layer.getListOfFeatures().size() > 0) {
	        	   
	        	   // protocol to save 
	        	   int response =    JOptionPane.showOptionDialog(
	        			   				null,
	                                   "Do you want to save changes to " + layer.getLayerName(),
	                                   "Save changes",
	                                    JOptionPane.YES_NO_OPTION,
	                                    JOptionPane.QUESTION_MESSAGE,
	                                    null,
	                                    null,
	                                    null);
	        	   
	        	   if(response == JOptionPane.YES_OPTION) {
	        		   
	        		   // Save the items to the database!

	        	   } else if(response == JOptionPane.NO_OPTION) {
	        		   
	        		   // Reload previous saved item from DB
	        		   
	        		   // To test, will just clear away
	        		   layer.getListOfFeatures().clear();
	        		   
	        		   panel.repaint();

	        	   };
	        	   
	        	   layer.setNotSaved(false);
	     
	           }
	           
	           // Change the current layer at the drawing panel
	           int toLayerIndex = layerListComboBox.getSelectedIndex();
	           panel.toggleEditStart(toLayerIndex, "continue");
	            
	       }
	}

	/**
	 * Closes the application appropriately
	 * @param e Window Event
	 */
	protected void handleWindowClosingEvent(WindowEvent e) {
		dispose();
		System.exit(0);
	}
	
	/**
	 * Logs messages on the frame
	 * @param string message to log
	 */
	public static void log(String string) {
		
		logTextField.setOpaque(true);
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		
		logTextField.setText(string + "  @" + dtf.format(now));
	}
	
	/**
	 * Updates the list of layer names on the combo box list. <br>
	 * 
	 * The index of an item on the list is equivalent to the index on the table of contents
	 * @param listOfLayersInString
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void updateLayerComboBoxModel(String[] listOfLayersInString) {
		
		model = new DefaultComboBoxModel( listOfLayersInString );
		MainFrame.layerListComboBox.setModel( model );
	}

}
