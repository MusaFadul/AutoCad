/**
 * 
 */
package application_frames;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import core_classes.Feature;
import core_classes.Layer;
import core_components.DrawIconButton;
import core_components.DrawingJPanel;
import core_components.TableOfContents;
import core_components.ToolIconButton;
import custom_components.CustomJFrame;
import database.DatabaseConnection;
import features.PolygonItem;
import features.PolylineItem;
import toolset.Tools;


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
					new MainFrame();
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
	private static JTextArea logText;
	
	/**Draw button group*/
	public static ButtonGroup drawButtonGroup = new ButtonGroup();
	
	/**Tools button group*/
	public static ButtonGroup toolsButtonGroup = new ButtonGroup();
	
	/**Log button*/
	private static JButton logButton;
	
	/**Button that toggles edit session*/
	public static ToolIconButton btnDrawEdit;
	
	/**Button that toggles query session*/
	public static ToolIconButton queryButton;
	
	/**Button that toggles selection mode*/
	public static ToolIconButton selectionButton;
	
	/**Database connection object*/
	public static DatabaseConnection dbConnection;
	
	public static List<ToolIconButton> buttonsList = new ArrayList<ToolIconButton>();
	
	/**
	 * Constructs the main frame
	 */
	public MainFrame() {
		
		new Settings().setVisible(false);
		connectToDatabase();
		
	}
	
	/**
	 * Arranges and initializes the application frame and sets up listeners and necessary directives.
	 */
	private void initialize() {

		setVisible(true);
		
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
		
		tableOfContents = new TableOfContents();
		JScrollPane tableOfContentsScrollPane = new JScrollPane(tableOfContents);
		tableOfContentsScrollPane.setBounds(10, 279, 244, 721);
		getContentPane().add(tableOfContentsScrollPane);
		
		
		panel = new DrawingJPanel(new Rectangle(264, 99, 1640, 901));
		panel.setBounds(264, 130, 1640, 818);
		panel.setBorder(new LineBorder(new Color(0,171,220)));
		panel.setBackground(Color.WHITE);
		getContentPane().add(panel);
		panel.setLayout(null);
		
		JButton btnOrtho = new JButton("Ortho");
		btnOrtho.setBounds(825, 804, 75, 65);
		//panel.add(btnOrtho);
		
		JButton btnGrid = new JButton("Grid");
		btnGrid.setBounds(910, 804, 75, 65);
		
		btnGrid.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				panel.toggleGrid();
			}
			
		});
		
		
		JLabel tOCLabel = new JLabel("Table of contents");
		tOCLabel.setBounds(10, 243, 244, 33);
		tOCLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		getContentPane().add(tOCLabel);
		
		JPanel fileRibbon = new JPanel();
		fileRibbon.setBounds(264, 11, 309, 108);
		fileRibbon.setBackground(Color.WHITE);
		fileRibbon.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "File operations", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		getContentPane().add(fileRibbon);
		fileRibbon.setLayout(null);
		
		ToolIconButton filesBtn = new ToolIconButton("Files", "/images/file.png", 60, 60);
		filesBtn.setToolTipText("Open previous projects");
		filesBtn.setBounds(10, 22, 90, 75);
		fileRibbon.add(filesBtn);
		
		filesBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					dbConnection.getTables();
					System.out.println("Worked");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		buttonsList.add(filesBtn);
		
		ToolIconButton importBtn = new ToolIconButton("Import", "/images/import.png", 60, 60);
		importBtn.setToolTipText("Import projects from csv");
		importBtn.setText("");
		importBtn.setBounds(109, 22, 90, 75);
		fileRibbon.add(importBtn);
		
		//toolsButtonGroup.add(importBtn);
		buttonsList.add(importBtn);
		
		ToolIconButton exportBtn = new ToolIconButton("Export", "/images/export.png", 60, 60);
		exportBtn.setToolTipText("Export current project to csv");
		exportBtn.setText("");
		exportBtn.setBounds(209, 22, 90, 75);
		fileRibbon.add(exportBtn);
		
		//toolsButtonGroup.add(exportBtn);
		buttonsList.add(exportBtn);
		
		JPanel selectionRibbon = new JPanel();
		selectionRibbon.setBounds(583, 11, 213, 108);
		selectionRibbon.setLayout(null);
		selectionRibbon.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Selector", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		selectionRibbon.setBackground(Color.WHITE);
		getContentPane().add(selectionRibbon);
		
		selectionButton = new ToolIconButton("Select", "/images/select.png", 60, 60);
		selectionButton.setBounds(10, 22, 90, 75);
		selectionRibbon.add(selectionButton);
		selectionButton.setToolTipText("Select items on the drawing");
		
		
		selectionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				if(panel.editModeIsOn) {
					
					MainFrame.btnDrawEdit.setButtonReleased(true);
					MainFrame.btnDrawEdit.doClick();
					MainFrame.btnDrawEdit.setBackground(Settings.DEFAULT_STATE_COLOR);

				}
				
				if(panel.queryModeIsOn) {
					
					MainFrame.queryButton.setButtonReleased(true);
					MainFrame.queryButton.doClick();
					MainFrame.queryButton.setBackground(Settings.DEFAULT_STATE_COLOR);

				}
				
				panel.toggleSelectionMode();
				
			}
		});
		
		queryButton = new ToolIconButton("Query", "/images/query.png", 60,60);
		queryButton.setBounds(110, 22, 90, 75);
		selectionRibbon.add(queryButton);
		queryButton.setToolTipText("Select items with rectangle");
		
		queryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if(panel.editModeIsOn) {
					
					MainFrame.btnDrawEdit.setButtonReleased(true);
					MainFrame.btnDrawEdit.doClick();
					MainFrame.btnDrawEdit.setBackground(Settings.DEFAULT_STATE_COLOR);

				}
				
				if(panel.selectionModeIsOn) {
					
					MainFrame.selectionButton.setButtonReleased(true);
					MainFrame.selectionButton.doClick();
					MainFrame.selectionButton.setBackground(Settings.DEFAULT_STATE_COLOR);

				}
				
				panel.toggleQueryMode();
			}	
		});
		
		JPanel editorRibbon = new JPanel();
		editorRibbon.setBounds(806, 11, 621, 113);
		editorRibbon.setLayout(null);
		editorRibbon.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Editor", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		editorRibbon.setBackground(Color.WHITE);
		getContentPane().add(editorRibbon);
		
		ToolIconButton btnDelete = new ToolIconButton("Delete", "/images/delete.png", 60,60);
		btnDelete.setBounds(10, 22, 90, 75);
		editorRibbon.add(btnDelete);
		btnDelete.setToolTipText("Delete selected items");
		
		btnDrawEdit = new ToolIconButton("Editing", "/images/edit.png", 60,60);
		btnDrawEdit.setBounds(110, 22, 90, 75);
		editorRibbon.add(btnDrawEdit);
		btnDrawEdit.setToolTipText("Start edit session");
		
		
		layerListComboBox = new JComboBox<String[]>();
		layerListComboBox.setBounds(210, 22, 199, 33);
		editorRibbon.add(layerListComboBox);
		layerListComboBox.setBackground(Settings.DEFAULT_STATE_COLOR);
		layerListComboBox.setForeground(Color.WHITE);
		
		ToolIconButton btnSnap = new ToolIconButton("Save edit", "/images/snap.png", 35,35);
		btnSnap.setBounds(210, 62, 35, 35);
		editorRibbon.add(btnSnap);
		btnSnap.setToolTipText("Turn of snap");
		
		ToolIconButton btnOrthoMode = new ToolIconButton("Save edit", "/images/ortho.png", 35, 35);
		btnOrthoMode.setBounds(255, 62, 35, 35);
		editorRibbon.add(btnOrthoMode);
		btnOrthoMode.setToolTipText("Turn of ortho mode");
		
		ToolIconButton btnShowGrid = new ToolIconButton("Save edit", "/images/grid.png", 35, 35);
		btnShowGrid.setBounds(300, 62, 35, 35);
		editorRibbon.add(btnShowGrid);
		btnShowGrid.setToolTipText("Turn on grid");
		
		ToolIconButton btnSaveEdit = new ToolIconButton("Save edit", "/images/save.png", 60,60);
		btnSaveEdit.setBounds(419, 22, 90, 75);
		editorRibbon.add(btnSaveEdit);
		btnSaveEdit.setToolTipText("Save edited layers");
		
		ToolIconButton btnAddLayer = new ToolIconButton("Save edit", "/images/add_layer.png", 80,80);
		btnAddLayer.setBounds(519, 22, 90, 75);
		editorRibbon.add(btnAddLayer);
		btnAddLayer.setToolTipText("Add more layers");
		
		JPanel configureRibbon = new JPanel();
		configureRibbon.setBounds(1683, 11, 221, 108);
		configureRibbon.setLayout(null);
		configureRibbon.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Configure", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		configureRibbon.setBackground(Color.WHITE);
		getContentPane().add(configureRibbon);
		
		ToolIconButton databseButton = new ToolIconButton("Database", "/images/database.png", 60, 60);
		databseButton.setBounds(13, 22, 90, 75);
		configureRibbon.add(databseButton);
		
		databseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {


				new DatabaseCatalog().setVisible(true);
				
			}
		});
		
		ToolIconButton settingsButton = new ToolIconButton("Settings", "/images/settings.png", 60, 60);
		settingsButton.setBounds(113, 22, 90, 75);
		configureRibbon.add(settingsButton);
		
		settingsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				new Settings().setVisible(true);;
				
			}
		});
		
		JPanel drawRibbon = new JPanel();
		drawRibbon.setBounds(1437, 11, 236, 108);
		drawRibbon.setLayout(null);
		drawRibbon.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Draw", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)));
		drawRibbon.setBackground(Color.WHITE);
		getContentPane().add(drawRibbon);
		
		DrawIconButton geomRec = new DrawIconButton("Rectangle", Settings.POLYGON_GEOMETRY ,"/images/rectangle.png", 25, 25);
		geomRec.setToolTipText("Rectangle");
		geomRec.setBounds(10, 21, 35, 35);
		drawRibbon.add(geomRec);
		
		drawButtonGroup.add(geomRec);
		
		DrawIconButton geomTriangle = new DrawIconButton("Triangle", Settings.POLYGON_GEOMETRY, "/images/triangle.png", 25, 25);
		geomTriangle.setToolTipText("Triangle");
		geomTriangle.setBounds(55, 21, 35, 35);
		drawRibbon.add(geomTriangle);
		
		drawButtonGroup.add(geomTriangle);
		
		DrawIconButton geomCircle = new DrawIconButton("Circle", Settings.POLYGON_GEOMETRY, "/images/circle.png", 25, 25);
		geomCircle.setToolTipText("Circle");
		geomCircle.setBounds(100, 21, 35, 35);
		drawRibbon.add(geomCircle);
		
		drawButtonGroup.add(geomCircle);
		
		DrawIconButton geomHexagon = new DrawIconButton("Hexagon", Settings.POLYGON_GEOMETRY, "/images/polygon.png", 30, 30);
		geomHexagon.setToolTipText("Hexagon");
		geomHexagon.setBounds(145, 21, 35, 35);
		drawRibbon.add(geomHexagon);
		
		drawButtonGroup.add(geomHexagon);
		
		DrawIconButton geomPoint = new DrawIconButton("Point", Settings.POINT_GEOMETRY, "/images/point.png", 25, 25);
		geomPoint.setToolTipText("Point");
		geomPoint.setBounds(100, 62, 35, 35);
		drawRibbon.add(geomPoint);
		
		drawButtonGroup.add(geomPoint);
		
		DrawIconButton geomSingleLine = new DrawIconButton("Line", Settings.POLYLINE_GEOMETRY, "/images/line.png", 25, 25);
		geomSingleLine.setToolTipText("Single line");
		geomSingleLine.setBounds(10, 62, 35, 35);
		drawRibbon.add(geomSingleLine);
		
		drawButtonGroup.add(geomSingleLine);
		
		DrawIconButton geomMultiLine = new DrawIconButton("Multiline", Settings.POLYLINE_GEOMETRY ,"/images/polyline.png", 25, 25);
		geomMultiLine.setToolTipText("Multi line");
		geomMultiLine.setBounds(55, 62, 35, 35);
		drawRibbon.add(geomMultiLine);
		
		drawButtonGroup.add(geomMultiLine);
		
		DrawIconButton geomEllipse = new DrawIconButton("Ellipse", "Polygon", "/images/ellipse.png", 30, 30);
		geomEllipse.setToolTipText("Ellipse");
		geomEllipse.setBounds(190, 21, 35, 35);
		drawRibbon.add(geomEllipse);
		
		drawButtonGroup.add(geomEllipse);
		
		logButton = new JButton("Messages");
		logButton.setForeground(Color.WHITE);
		logButton.setBackground(Color.BLACK);
		logButton.setBounds(264, 959, 99, 41);
		getContentPane().add(logButton);
		
		logText = new JTextArea();
		logText.setEditable(false);
		logText.setForeground(Color.WHITE);
		logText.setBackground(Color.DARK_GRAY);
		//logText
		DefaultCaret caret = (DefaultCaret) logText.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(logText);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(365, 959, 1539, 41);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		scrollPane.setBackground(Color.DARK_GRAY);
		getContentPane().add(scrollPane);
	
		filesBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//importBtn.setButtonReleased(true);
				importBtn.setBackground(Settings.DEFAULT_STATE_COLOR);
			}
		});
		
		btnAddLayer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				handleAddNewLayerIntent();
				updateDrawButtonGroup();
			}	
		});
		
		btnShowGrid.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				panel.toggleGrid();
			}
		});
		
		btnSnap.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.toggleSnap();
				
			}
		});
		
		layerListComboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				updateDrawButtonGroup();
				handleLayerSaving(e);
				
			}
		});
		
		btnDrawEdit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				handleEditingSession(e, btnDrawEdit, "new");
			}
			
		});
		
		
	}
	
	
	/**
	 * Updates the list of drawable geometies based on the current layer selected
	 * This is done when:
	 * a. Before edit session is started
	 * b. When the layer in the combo box is changed
	 */
	public static void updateDrawButtonGroup() {
		
		drawButtonGroup.clearSelection();
		
		// 0. Only if there are layers
		//----------------------------------------
		if(layerListComboBox.getItemCount() > 0) {
			
			// 1. Get the current index at the layer list
			// ------------------------------------------
			int index = layerListComboBox.getSelectedIndex();
			
			// 2. Retreive the layer id at the table of currents ()
			// ----------------------------------------------------
			int layerID = (int) tableOfContents.getModel().getValueAt(index, TableOfContents.LAYER_ID_COL_INDEX );
			
			// 3. Retreive the layer itself
			// ----------------------------------------------------
			Layer layer = TableOfContents.findLayerWithID(layerID);
			
			// 4. Reconfirm, the layer cannot be null, just to keep track of things
			//    in case something breaks;
			// ---------------------------------------------------------------------
			if(layer != null) {
				
				// 4.1 Get the layer type
				// -----------------------
				String layerType = layer.getLayerType();
				
				// 4.2 Loop through all the buttons in the draw button group
				// ----------------------------------------------------------
				for (Enumeration<AbstractButton> buttons = drawButtonGroup.getElements(); buttons.hasMoreElements();) {
		            
					DrawIconButton button = (DrawIconButton) buttons.nextElement();
					
					// Get the layer type of the button
					// TODO validate constructing a draw button later!
					
					// Check if the draw button can be drawn on the current layer type
					if(!(layerType.equals(button.getGeometryFamily()))) {
						
						button.setEnabled(false);
						
					} else {
					
						button.setEnabled(true);
					}
		        }
			} 
			
			else {
				
				log("Error, layer not found with the current index!, something went wrong !");
			}	
		} 
		
		else {
			
			// If layer list is empty
			// Disable all draw buttons
			 disableAllDrawButtons();
			
			// Disable the edit start button
			btnDrawEdit.setButtonReleased(false);
			btnDrawEdit.setBackground(Settings.DEFAULT_STATE_COLOR);
			
		}
			
	}

	public static void disableAllDrawButtons() {
		
		for (Enumeration<AbstractButton> buttons = drawButtonGroup.getElements(); buttons.hasMoreElements();) {
			DrawIconButton button = (DrawIconButton) buttons.nextElement();
			button.setEnabled(false);
		}
	}

	/**
	 * 
	 */
	protected void handleAddNewLayerIntent() {
		
		// 0. Disable query mode
		panel.disableQueryMode();
		
		// 1. Create list of possible geometry
		// -----------------------------------
		String[] geom = {Settings.POLYGON_GEOMETRY, Settings.POLYLINE_GEOMETRY, Settings.POINT_GEOMETRY};
		
		// 2. Put list inside a combo box
		// ------------------------------
		JComboBox<String> geomList = new JComboBox<String>(geom);
		
	    // a. Create a Jpanel and set the layout
		
	    JPanel layerPanel = new JPanel();
	    layerPanel.setLayout(new GridLayout(4,1));
	  
	    // b. Create the text fields
	    String autoGeneratedLayerName = "New_Layer" + TableOfContents.getNewLayerID();
	    JTextField layerNameTextField = new JTextField(autoGeneratedLayerName);
	   
	    // c. Add componets to panel
	    layerPanel.add(new JLabel("Add a new layer"));
	    layerPanel.add(new JSeparator());
	    layerPanel.add(geomList);
	    layerPanel.add(layerNameTextField);
		
		// 3. Show a JOption pane to select a new geometry type
		// -----------------------------------------------------
		int response = JOptionPane.showConfirmDialog( null, layerPanel , "Choose geometry type", JOptionPane.OK_CANCEL_OPTION);
		
		// 4. On OK option
		//-------------------------------------
		if(response == JOptionPane.OK_OPTION) {
			
			// 4.1 Get the selected item on the combo box
			if( geomList.getSelectedItem().toString().equals(Settings.POLYGON_GEOMETRY ) ||
			    geomList.getSelectedItem().toString().equals(Settings.POLYLINE_GEOMETRY) ||
			    geomList.getSelectedItem().toString().equals(Settings.POINT_GEOMETRY )
					) {

				// 4.2 Create a new layer
				
				String layerName = layerNameTextField.getText().toString();
				if(layerName.length() < 1) { layerName = autoGeneratedLayerName; }
				
				createNewLayer(geomList.getSelectedItem().toString(), layerName);
				
			}
		}
	}
	
	
	/**
	 * Handles when the user wants to start editing session <br>
	 * Checks if there are no layer on the table of contents first then
	 * starts the editing session. <br>
	 * The background of the button is left active during the duration of the edit session
	 * @param e
	 * @param btnDrawEdit button where the action event comes from
	 */
	protected void handleEditingSession(ActionEvent e, ToolIconButton btnDrawEdit, String signal) {
		
		if(layerListComboBox.getModel().getSize() > 0) {
			
	
			String selectedFeatureType = getCurrentFeatureType();
			panel.toggleEditSession(layerListComboBox.getSelectedIndex(), selectedFeatureType,  signal);
			
			log("Edit session started on " + DrawingJPanel.currentLayer.getLayerName());
	
		} else {
			
			log("Drawing was attempted but no layer to edit on the list");
			Toolkit.getDefaultToolkit().beep();
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
		
		if (e.getStateChange() == ItemEvent.DESELECTED) {
	         
           int index = model.getIndexOf(e.getItem());
           int layerid = (int) tableOfContents.getModel().getValueAt(index, TableOfContents.LAYER_ID_COL_INDEX);
           Layer layer = TableOfContents.findLayerWithID(layerid);
           
           if(layer.isNotSaved() && layer.getListOfFeatures().size() > 0) {
        	   
        	   // protocol to save 
        	   Toolkit.getDefaultToolkit().beep();
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
        		   handleLayerSavingToDB(layer);
   
        		   

        	   } else if(response == JOptionPane.NO_OPTION) {
        		   
        		   // Reload previous saved item from DB
        		   
        		   // To test, will just clear away
        		   layer.getListOfFeatures().clear();
        		   
        		   panel.repaint();

        	   };
        	   
        	   layer.setNotSaved(false);
           }
           
           try {
        	
        	   // Change the current layer at the drawing panel
	           int toLayerIndex = layerListComboBox.getSelectedIndex();
	           
	           String currentFeature = getCurrentFeatureType();
	           panel.toggleEditSession(toLayerIndex, currentFeature, "continue");
			
			} catch (Exception e1) {

				log("Select a shape to draw in the layer");
			}
		}
	}
	
	/**
	 * 
	 * @param layer
	 * @return
	 */
	public static boolean handleLayerSavingToDB(Layer layer) {


		try {
			 
 		   // Check for name:
 		   boolean layerDoesNotExist = true;
 		   for(String existingTable : dbConnection.getTables()) {
 			   if(existingTable.equals(layer.getLayerName())) {
 				  layerDoesNotExist = false;
 				  break;
 			   }
 		   }
 		   
 		   if(layerDoesNotExist) {
 			   
 			   	dbConnection.writeTable(layer.getLayerName(), layer);
				
				panel.showAnimatedHint("Saved!", Settings.FEATURE_CREATED_COLOR);
				log("Layer saved to DB");

				return true;
				
 		   } else {
 			   
 			  panel.showAnimatedHint("Layer name exists!", Settings.DEFAULT_ERROR_COLOR);
 			  log("Layer name exists, overwrite?");
 			  
 			  int response = JOptionPane.showConfirmDialog(null, "Overwrite/ confirm", "Confirm", JOptionPane.YES_NO_OPTION );
 			  
 			  if(response == JOptionPane.YES_OPTION) {
 				  
 				 dbConnection.writeTable(layer.getLayerName(), layer);
 				
 				 panel.showAnimatedHint("Saved!", Settings.FEATURE_CREATED_COLOR);
 				 log("Layer saved to DB");

 				 return true;
 			  }
 		   }
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			panel.showAnimatedHint("Something went wrong /n Cannot save to DB", Settings.DEFAULT_ERROR_COLOR);
			log(e.getMessage());
			
			return false;
		}
		
		return false;
	}

	/**
	 * Closes the application appropriately
	 * @param e Window Event
	 */
	protected void handleWindowClosingEvent(WindowEvent e) {
		dispose();
		System.exit(0);
	}

	private void connectToDatabase() {
		
		String host = "localhost";
		int port = 5432;
		String database = "softeng_db";
		String user = "postgres";
		String password = "12345";
		
		try {
			
			dbConnection = new DatabaseConnection(host, port, database, user, password);
			
			initialize();
			log("Application started. GMCM3 Software Engineering HSKA Karlsruhe "
					+ "https://github.com/enocholumide/GMCM3_Software_Eng.git "
					+ "\t Database connected");
			
		} catch (ClassNotFoundException | SQLException e) {
			
			
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog( null, "Database connection cannot be established \n\n"
					+ e.getMessage() + "\n\n Remind me later?", "Database connection error", JOptionPane.ERROR_MESSAGE);
			
			initialize();
			log("Application started. GMCM3 Software Engineering HSKA Karlsruhe "
					+ "https://github.com/enocholumide/GMCM3_Software_Eng.git "
					+ "\t Database NOT CONNECTED!!!");		
		}
	}

	public static void createNewLayer(String layerType, String layerName) {
		
		// 1. Create a new layer
		Layer newLayer = new Layer(TableOfContents.getNewLayerID(), true, layerType, layerName );
		
		// 2. Add to the table of content
		tableOfContents.addRowLayer(newLayer);
		
		// 3. Log some message
		String message = "New " + newLayer.getLayerType() + " layer: "+ newLayer.getLayerName() + " was created";
		log(message);
		panel.showAnimatedHint(message, Settings.DEFAULT_STATE_COLOR);
	
	}

	public static void createLayerFromResultSet(ResultSet resultSet, String layerName) {
	
		try {
			
			String layerType = "";
			Layer newLayer = new Layer(TableOfContents.getNewLayerID(), true, "", layerName);

			while (resultSet.next()) {
				
				boolean isEllipse = resultSet.getBoolean(3);
				
				layerType = resultSet.getString(2);
				
				Double[] aX = (Double[]) resultSet.getArray(4).getArray();
				Double[] aY = (Double[]) resultSet.getArray(5).getArray();
				
				if(isEllipse) {
					
					double x = aX[0];
					double y = aY[0];
					double rx = resultSet.getDouble(6);
					double ry = resultSet.getDouble(7);
						
					// Ellipse
					Feature feature = new Feature(newLayer.getListOfFeatures().size());
					Shape circleShape = new Ellipse2D.Double(x - rx, y - ry , rx * 2, ry * 2);
					
					feature.setEllipse(isEllipse, new Point2D.Double(x,y), rx, ry);
					feature.setShape(circleShape);
					feature.setVisibile(true);
					newLayer.getListOfFeatures().add(feature);
					
				
				} else {
					
					// Normal path - polygon and polyline
					List<Rectangle2D> vertices = new ArrayList<Rectangle2D>();

					for(int i = 0; i < Math.min(aX.length, aY.length); i++) {
						vertices.add(new Rectangle2D.Double(aX[i] - (Settings.snappingTolerance / 2), aY[i] - (Settings.snappingTolerance / 2),
								Settings.snappingTolerance, Settings.snappingTolerance));
					}

					newLayer.setLayerType(layerType); // ! important
					
					panel.finishPath(vertices, newLayer);

				}		
			}
			
			newLayer.setNotSaved(false);
			tableOfContents.addRowLayer(newLayer);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String getCurrentFeatureType() {
		
		if(drawButtonGroup.getSelection() != null) {
			
			String selectedFeatureType = drawButtonGroup.getSelection().getActionCommand();
		
			return selectedFeatureType;
		} else
			
			return null;
	}

	/**
	 * Logs messages on the frame with simple animation
	 * @param string message to log
	 */
	public static void log(String string) {
		
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		logText.append("\n" + string + "        @ " + dtf.format(now));
		
		final long time = System.nanoTime() / 1000000000;

		final ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
		ex.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				if( ((System.nanoTime() / 1000000000) - time) % 2 == 0) {
					logButton.setBackground(Color.BLACK);
				} else {
					logButton.setBackground(Color.RED);
				}
					
				if( ((System.nanoTime() / 1000000000) - time) > 2) {
					logButton.setBackground(Color.BLACK);
					ex.shutdown();
				}
			}
			
		}, 0, 1, TimeUnit.SECONDS);
		
		logButton.setBackground(Color.BLACK);
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
	
	/**
	 * 
	 * @param name
	 */
	public static void releaseAllOtherToolsButton(String name) {
		
		// 1. Loop through all the buttons in the draw button group
		// ----------------------------------------------------------
		for (Enumeration<AbstractButton> buttons = toolsButtonGroup.getElements(); buttons.hasMoreElements();) {
            
			ToolIconButton button = (ToolIconButton) buttons.nextElement();
			
			// a. Get the layer type of the button
			// TODO validate constructing a draw button later!
			
			// b. Check if the draw button can be drawn on the current layer type
			if(!(name.equals(button.getActionCommand()))) {
				
				button.setButtonReleased(true);
				button.setSelected(false);
				button.setBackground(Settings.DEFAULT_STATE_COLOR);
				
			}
        }
	}
}
