package application_frames;

import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

import java.awt.Color;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultCaret;

import core_classes.Feature;
import core_classes.Layer;
import core_components.DrawIconButton;
import core_components.DrawingJPanel;
import core_components.TableOfContents;
import core_components.ToolIconButton;
import custom_components.CustomJFrame;
import database.DatabaseConnection;
import features.PointItem;
import file_handling.FileHandler;
import file_handling.SessionManager;
import toolset.Tools;

import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
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

import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;
import javax.swing.border.LineBorder;

/**
 * 
 * Main frame of the application.<br>
 * At the initial start of the application, the settings frame is first displayed to set the
 * database connection and general drawing settings.<br>
 * <p>
 * It uses the Group layout primarily to arrange the components contained.<br>
 * The frame is divided into two, using the JSplit pane.<br>
 * <p>
 * The initial bounds of the mainframe is 15inch (resolution 1366 x 768), which can be resized.<br>
 * The left side contains the current drawing description and the list of layers
 * arranged in table of contents. The width can also be resized during runtime<br>
 * <p>
 * The other (right) part is composed of the drawing panel (at the middle), 
 * the tool bar (top) and the message/ log area at the bottom.<br>
 * This other part is managed by using the Group layout and stretches to the right when window is resized.<br>
 * <p>
 * Most of the activity (actions) happens at the DrawingJPanel class, the MainFrame handles common tasks such
 * as adding new layer to the table of contents, importing and exporting files and choosing the a shape to be drawn and 
 * general interface to other application frames in the program e.g. DatabaseCatalog, Settings Frame etc.
 * 
 * @author Musa Fadul,Olumide Igbiloba
 * @since Dec 7, 2017
 * @version
 * a. Dec 20, 2017 : Integrate database connection parameters from the settings frame.<br>
 * b. Dec 26, 2017 : Removed the overloaded constructor with a database connection and
 * changed it to a private method within the class.<br>
 * c. Dec 27, 2017 : Integrate drawing settings from the settings frame.<br>
 * d. Dec 28, 2017 : Created separate (popup) frame for saving and opening drawing sessions.<br>
 * e. Dec 28, 2017 : Created separate (popup) frame for importing and exporting CSV files.<br>
 * f. Dec 28, 2017 : Validate adding layer with same name on the table of contents<br>
 * g. Dec 29, 2017 : Implement look and feel<br>
 * i. Dec 31, 2017 : Integrate CSV loading and export to/from the drawing space;
 */
public class MainFrame extends CustomJFrame {

	private static final long serialVersionUID = -7752427807628614402L;

	/**
	 * Launch the application.
	 * @param args argument
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
					new MainFrame();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**Current project/ document name*/
	public static JTextField projectName;
	
	/**The drawing panel for drawing shapes*/
	public static DrawingJPanel panel;
	
	/**List showing the available layers at the table of contents*/
	public static JComboBox<String[]> layerListComboBox;
	
	/**Model of the layer list combo box*/
	public static DefaultComboBoxModel<String[]> model;
	
	/**Table of contents arranging the list of layers*/
	public static TableOfContents tableOfContents;
	
	/**Text field for showing messages on the frame*/
	private static JTextArea logText;
	
	/**Draw button group*/
	public static ButtonGroup drawButtonGroup = new ButtonGroup();
	
	/**Tools button group*/
	public static ButtonGroup toolsButtonGroup = new ButtonGroup();
	
	/**List of tools icon buttons*/
	public static List<ToolIconButton> buttonsList = new ArrayList<ToolIconButton>();
	
	/**Button that toggles edit session*/
	public static ToolIconButton btnDrawEdit;
	
	/**Button that toggles query session*/
	public static ToolIconButton queryButton;
	
	/**Button that toggles selection mode*/
	public static ToolIconButton selectionButton, btnVertices;
	
	/**Database connection object*/
	public static DatabaseConnection dbConnection;
	
	/**Database catalog frame*/
	public static DatabaseCatalog dbCatalog;
	
	/**Log button*/
	private static JButton logButton;
	
	/**Settings frame*/
	private SettingsFrame settingsFrame = new SettingsFrame(true, this);
	
	/**Import frame*/
	private ImportExportFrame importExportFrame;
	
	/**Session manager for saving and loading sessions*/
	private SessionManager sessionManager = new SessionManager(tableOfContents);
	
	/**Files frame*/
	private FilesFrame filesFrame;

	public static ToolIconButton btnSnap, btnGrid;


	/**
	 * Starts the application
	 */
	public MainFrame() {
		//setUp();
		initialize();
	}
	
	/**
	 * Sets up the application by displaying the settings frame.<br>
	 * This is needed to set up the database connection and other drawing preferences <br>
	 */
	private void setUp() {
	
		settingsFrame.getFrame().setVisible(true);
	
	}

	/**
	 * Starts the mainframe with a database connection
	 * @param dbConnection database connection for storing/ retrieving drawn shapes
	 */
	public void start(DatabaseConnection dbConnection) {
		
		MainFrame.dbConnection = dbConnection;
		initialize();

		log("Application started: GMCM3 Software Engineering (Group 1)"
				+ " https://github.com/enocholumide/GMCM3_Software_Eng.git "
				+ "\t Database connected");
		
	}

	/**
	 * Initializes the contents of the frame.
	 */
	private void initialize() {
		
		// Make visible
		setVisible(true);
		
		// Set title
		setTitle("GMCM3 Software Engineering (Group 1), Geomatics, Karlsruhe University of Applied Sciences (HsKA)");
		
		// Position at the middle of the screen
		setBounds(SettingsFrame.window.x  + (SettingsFrame.window.width - SettingsFrame.MAINFRAME_SIZE.width) / 2, 
				SettingsFrame.window.y + (SettingsFrame.window.height - SettingsFrame.MAINFRAME_SIZE.height) / 2,
				SettingsFrame.MAINFRAME_SIZE.width,
				SettingsFrame.MAINFRAME_SIZE.height);
		
		// Adding window listener
		addWindowListener(new WindowAdapter() {
			@Override 
			public void windowClosing(WindowEvent e) { 
				handleWindowClosingEvent(e);
			} 
		});
		
		// Side panel, containing the drawing description and the table of contents
		JPanel sidePanel = new JPanel();
		sidePanel.setBackground(Color.WHITE);
		
		// Right panel, containing the tool bar, drawing area and the message area
		JPanel rightPanel = new JPanel();
		rightPanel.setBackground(Color.WHITE);
		
		// Put the panels in a split pane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				sidePanel, rightPanel);
		
		// Application logo
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBackground(Color.LIGHT_GRAY);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setIcon(Tools.getIconImage("/images/logo.png", 60, 60));
		
		JLabel label = new JLabel("");
		
		// Project name textfield
		projectName = new JTextField();
		projectName.setHorizontalAlignment(SwingConstants.CENTER);
		projectName.setText(SettingsFrame.txtNewDoc.getText().toString());
		projectName.setFont(new Font("Tahoma", Font.BOLD, 18));
		projectName.setColumns(10);
		
		JLabel lblTableOfContents = new JLabel("Table of contents");
		lblTableOfContents.setHorizontalAlignment(SwingConstants.CENTER);
		lblTableOfContents.setFont(new Font("Tahoma", Font.PLAIN, 13));
		
		layerListComboBox = new JComboBox<String[]>();
		
		// Table of contents inside a J scroll pane
		tableOfContents = new TableOfContents();
		JScrollPane scrollPane2 = new JScrollPane(tableOfContents);
		GroupLayout gl_sidePanel = new GroupLayout(sidePanel);
		gl_sidePanel.setHorizontalGroup(
			gl_sidePanel.createParallelGroup(Alignment.LEADING)
				.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
				.addGroup(gl_sidePanel.createSequentialGroup()
					.addComponent(label)
					.addContainerGap())
				.addComponent(projectName, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
				.addComponent(lblTableOfContents, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
				.addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
		);
		gl_sidePanel.setVerticalGroup(
			gl_sidePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_sidePanel.createSequentialGroup()
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(label)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(projectName, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblTableOfContents, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE))
		);
		sidePanel.setLayout(gl_sidePanel);
		
		JPanel toolBar = new JPanel();
		toolBar.setBackground(Color.WHITE);
		
		panel = new DrawingJPanel();
		panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
		
		
		JPanel panel_9 = new JPanel();
		panel_9.setBackground(Color.BLACK);
		GroupLayout gl_rightPanel = new GroupLayout(rightPanel);
		gl_rightPanel.setHorizontalGroup(
			gl_rightPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(panel_9, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(toolBar, GroupLayout.DEFAULT_SIZE, 1154, Short.MAX_VALUE)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 1154, Short.MAX_VALUE)
		);
		gl_rightPanel.setVerticalGroup(
			gl_rightPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_rightPanel.createSequentialGroup()
					.addComponent(toolBar, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_9, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE))
		);
		
		logButton = new JButton("LOG");
		logButton.setBorderPainted(false);
		logButton.setFocusPainted(false);
		logButton.setForeground(Color.WHITE);
		logButton.setBackground(Color.BLACK);
		
		logText = new JTextArea();
		logText.setEditable(false);
		logText.setForeground(Color.WHITE);
		logText.setBackground(Color.DARK_GRAY);
		
		DefaultCaret caret = (DefaultCaret) logText.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		JScrollPane scrollPane = new JScrollPane(logText);
		scrollPane.setViewportView(logText);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		scrollPane.setBackground(Color.DARK_GRAY);
		
		GroupLayout gl_panel_9 = new GroupLayout(panel_9);
		gl_panel_9.setHorizontalGroup(
			gl_panel_9.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_9.createSequentialGroup()
					.addComponent(logButton, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 1045, Short.MAX_VALUE))
		);
		gl_panel_9.setVerticalGroup(
			gl_panel_9.createParallelGroup(Alignment.LEADING)
				.addComponent(logButton, GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 44, Short.MAX_VALUE)
		);
		panel_9.setLayout(gl_panel_9);
		
		JPanel fileRibbon = new JPanel();
		fileRibbon.setBackground(Color.WHITE);
		fileRibbon.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "File", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		fileRibbon.setLayout(new GridLayout(1, 2, 5, 5));
		
		ToolIconButton filesBtn = new ToolIconButton("Files", "/images/file.png", 60, 60);
		filesBtn.setToolTipText("Open previous projects");
		fileRibbon.add(filesBtn);
		
		ToolIconButton importBtn = new ToolIconButton("Import", "/images/import.png", 60, 60);
		importBtn.setToolTipText("Import projects from csv");
		fileRibbon.add(importBtn);
		
		ToolIconButton exportBtn = new ToolIconButton("Export", "/images/export.png", 60, 60);
		fileRibbon.add(exportBtn);
		
		JPanel selectorRibbon = new JPanel();
		selectorRibbon.setBackground(Color.WHITE);
		selectorRibbon.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Selector", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		selectorRibbon.setLayout(new GridLayout(1, 2, 5, 5));
		
		selectionButton = new ToolIconButton("Select", "/images/select.png", 60, 60);
		selectorRibbon.add(selectionButton);
		
		queryButton = new ToolIconButton("Query", "/images/query.png", 60,60);
		queryButton.setToolTipText("Select items with rectangle");
		selectorRibbon.add(queryButton);
		
		JPanel editorRibbon = new JPanel();
		editorRibbon.setBackground(Color.WHITE);
		editorRibbon.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Editor", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new EmptyBorder(0, 0, 5, 0));
		panel_5.setBackground(Color.WHITE);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new EmptyBorder(0, 0, 5, 0));
		panel_6.setBackground(Color.WHITE);
		panel_6.setLayout(new GridLayout(1, 2, 5, 5));
		
		ToolIconButton btnSaveEdit = new ToolIconButton("Save edit", "/images/save.png", 60,60);
		btnSaveEdit.setToolTipText("Save edited layers");
		panel_6.add(btnSaveEdit);
		
		ToolIconButton btnAddLayer = new ToolIconButton("Save edit", "/images/add_layer.png", 80,80);
		btnAddLayer.setToolTipText("Add more layers");
		panel_6.add(btnAddLayer);
		
		btnSnap = new ToolIconButton("Snap", "/images/snap.png", 35,35);
		btnSnap.setToolTipText("Turn of snap");
		
		btnGrid = new ToolIconButton("Grid", "/images/grid.png", 35, 35);
		btnGrid.setToolTipText("Turn on grid");
		
		btnVertices = new ToolIconButton("Grid", "/images/vertex.png", 35, 35);
		btnVertices.setToolTipText("Toggle vertices");
		btnVertices.doClick();
		btnVertices.setBackground(SettingsFrame.HIGHLIGHTED_STATE_COLOR);
		
		GroupLayout gl_editorRibbon = new GroupLayout(editorRibbon);
		gl_editorRibbon.setHorizontalGroup(
			gl_editorRibbon.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_editorRibbon.createSequentialGroup()
					.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_editorRibbon.createParallelGroup(Alignment.LEADING)
						.addComponent(layerListComboBox, 0, 104, Short.MAX_VALUE)
						.addGroup(gl_editorRibbon.createSequentialGroup()
							.addComponent(btnSnap, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnGrid, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnVertices, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_6, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE))
		);
		gl_editorRibbon.setVerticalGroup(
			gl_editorRibbon.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_editorRibbon.createSequentialGroup()
					.addGroup(gl_editorRibbon.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_editorRibbon.createSequentialGroup()
							.addComponent(layerListComboBox, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_editorRibbon.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_editorRibbon.createParallelGroup(Alignment.LEADING, false)
									.addComponent(btnSnap, 0, 0, Short.MAX_VALUE)
									.addComponent(btnGrid, GroupLayout.PREFERRED_SIZE, 30, Short.MAX_VALUE))
								.addComponent(btnVertices, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED, 5, Short.MAX_VALUE))
						.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 72, Short.MAX_VALUE)
						.addComponent(panel_6, GroupLayout.PREFERRED_SIZE, 72, Short.MAX_VALUE))
					.addContainerGap())
		);
		panel_5.setLayout(new GridLayout(1, 2, 5, 5));
		
		ToolIconButton btnDelete = new ToolIconButton("Delete", "/images/delete.png", 60,60);
		btnDelete.setToolTipText("Delete selected items");
		panel_5.add(btnDelete);
		
		btnDrawEdit = new ToolIconButton("Editing", "/images/edit.png", 60,60);
		panel_5.add(btnDrawEdit);
		btnDrawEdit.setToolTipText("Start edit session");
		editorRibbon.setLayout(gl_editorRibbon);
		
		JPanel drawRibbon = new JPanel();
		drawRibbon.setBackground(Color.WHITE);
		drawRibbon.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Draw", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		drawRibbon.setLayout(new GridLayout(2, 8, 5, 5));
		
		DrawIconButton geomRec = new DrawIconButton("Rectangle", SettingsFrame.POLYGON_GEOMETRY ,"/images/rectangle.png", 25, 25);
		geomRec.setToolTipText("Rectangle");
		drawRibbon.add(geomRec);
		
		DrawIconButton geomTriangle = new DrawIconButton("Triangle", SettingsFrame.POLYGON_GEOMETRY, "/images/triangle.png", 25, 25);
		geomTriangle.setToolTipText("Triangle");
		drawRibbon.add(geomTriangle);
		
		DrawIconButton geomCircle = new DrawIconButton("Circle", SettingsFrame.POLYGON_GEOMETRY, "/images/circle.png", 25, 25);
		geomCircle.setToolTipText("Circle");
		drawRibbon.add(geomCircle);
		
		DrawIconButton geomFreeformPolygon = new DrawIconButton("Freeform Polygon", SettingsFrame.POLYGON_GEOMETRY, "/images/polygon.png", 30, 30);
		geomFreeformPolygon.setToolTipText("Freeform Polygon");
		drawRibbon.add(geomFreeformPolygon);
		
		DrawIconButton geomPoint = new DrawIconButton("Point", SettingsFrame.POINT_GEOMETRY, "/images/point.png", 25, 25);
		geomPoint.setToolTipText("Point");
		drawRibbon.add(geomPoint);
		
		DrawIconButton geomSingleLine = new DrawIconButton("Line", SettingsFrame.POLYLINE_GEOMETRY, "/images/line.png", 25, 25);
		geomSingleLine.setToolTipText("Single line");
		drawRibbon.add(geomSingleLine);
		
		DrawIconButton geomMultiLine = new DrawIconButton("Multiline", SettingsFrame.POLYLINE_GEOMETRY ,"/images/polyline.png", 25, 25);
		geomMultiLine.setToolTipText("Multi line");
		drawRibbon.add(geomMultiLine);
		
		DrawIconButton geomEllipse = new DrawIconButton("Ellipse", SettingsFrame.POLYGON_GEOMETRY, "/images/ellipse.png", 30, 30);
		geomEllipse.setToolTipText("Ellipse");
		drawRibbon.add(geomEllipse);
		
		JPanel configureRibbon = new JPanel();
		configureRibbon.setBackground(Color.WHITE);
		configureRibbon.setBorder(new CompoundBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Configure", TitledBorder.LEFT, TitledBorder.TOP, null, new Color(0, 0, 0)), new EmptyBorder(5, 5, 5, 5)));
		configureRibbon.setLayout(new GridLayout(1, 2, 5, 5));
		
		ToolIconButton databaseButton = new ToolIconButton("Database", "/images/database.png", 60, 60);
		configureRibbon.add(databaseButton);
		
		ToolIconButton settingsButton = new ToolIconButton("Settings", "/images/settings.png", 60, 60);
		configureRibbon.add(settingsButton);
		GroupLayout gl_toolBar = new GroupLayout(toolBar);
		gl_toolBar.setHorizontalGroup(
			gl_toolBar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_toolBar.createSequentialGroup()
					.addComponent(fileRibbon, GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(selectorRibbon, GroupLayout.PREFERRED_SIZE, 157, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(editorRibbon, GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(drawRibbon, GroupLayout.PREFERRED_SIZE, 165, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(configureRibbon, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE))
		);
		gl_toolBar.setVerticalGroup(
			gl_toolBar.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_toolBar.createSequentialGroup()
					.addGroup(gl_toolBar.createParallelGroup(Alignment.TRAILING)
						.addComponent(configureRibbon, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
						.addComponent(drawRibbon, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 104, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_toolBar.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(editorRibbon, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
							.addComponent(selectorRibbon, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(fileRibbon, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addContainerGap())
		);
		toolBar.setLayout(gl_toolBar);
		rightPanel.setLayout(gl_rightPanel);
		splitPane.setDividerLocation((int)(getBounds().width / 8));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 1330, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(splitPane, GroupLayout.DEFAULT_SIZE, 707, Short.MAX_VALUE)
					.addContainerGap())
		);
		getContentPane().setLayout(groupLayout);
		
		addComponentListener(new ComponentListener() {
			
		    public void componentResized(ComponentEvent e) {
		       
		        panel.renderGrid(SettingsFrame.GRID_MM);
		        
		        if(getBounds().width > 1366) {
		        	splitPane.setDividerLocation((int)(getBounds().width / 4));
			        revalidate();
			        repaint();
		        }
		    }

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				panel.renderGrid(SettingsFrame.GRID_MM);
				panel.repaint();
			}
		});
		
		// ---------------------------------------------------------------------------
		// ACTION LISTENERS 
		// ---------------------------------------------------------------------------
		
		btnDelete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!panel.editModeIsOn) {
					log("Attempted to delete feature, but edit mode is off");
					panel.showAnimatedHint("Edit mode if off", SettingsFrame.DEFAULT_ERROR_COLOR);
				} else
					panel.deleteSelectedItem();
			}
		});
		
		
		importBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				panel.abandonEditSession();
				
				if(importExportFrame != null) {
					importExportFrame.dispose();
				}
				importExportFrame = new ImportExportFrame("Import from");
				importExportFrame.setVisible(true);
				
				importExportFrame.getCsvLoader().addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						importLayerFromCSV();
						
					}
				});
				
				importExportFrame.getGeoJsonLoader().addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						importLayerFromGeoJson();
					}
				});
			}
		});
		
		exportBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				panel.abandonEditSession();
				
				if(importExportFrame != null) {
					importExportFrame.dispose();
				}
				
				importExportFrame = new ImportExportFrame("Export to");
				importExportFrame.setVisible(true);
				
				importExportFrame.getCsvLoader().addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							FileHandler.writeToCSV(TableOfContents.layerList);
							panel.showAnimatedHint("Sucessfully exported to CSV", SettingsFrame.DEFAULT_SUCCESS_COLOR);
							log("Sucessfully exported to CSV");
						} catch (Exception e1) {
							log("An error occured, can not export to CSV: " + e1.getMessage());
						}
					}
				});
				
				importExportFrame.getGeoJsonLoader().addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						log("Musa, handle export to geoJson : work in progress");
					}
				});
			}
		});
		
		filesBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(filesFrame != null) {
					filesFrame.dispose();
				}
				filesFrame = new FilesFrame();
				filesFrame.setVisible(true);
				
				filesFrame.getSaveButton().addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						sessionManager.onSaveSessionIntent();
						filesFrame.dispose();
					}
				});
				
				filesFrame.getOpenButton().addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						sessionManager.onOpenSessionIntent();
						filesFrame.dispose();
					}
				}); 
			}
		});
		
		selectionButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {

				if(panel.editModeIsOn) {
					
					MainFrame.btnDrawEdit.setButtonReleased(true);
					MainFrame.btnDrawEdit.doClick();
					MainFrame.btnDrawEdit.setBackground(SettingsFrame.DEFAULT_STATE_COLOR);

				}
				
				if(panel.queryModeIsOn) {
					
					MainFrame.queryButton.setButtonReleased(true);
					MainFrame.queryButton.doClick();
					MainFrame.queryButton.setBackground(SettingsFrame.DEFAULT_STATE_COLOR);

				}
				
				panel.toggleSelectionMode();
				
			}
		});
		
		queryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				if(panel.editModeIsOn) {
					
					MainFrame.btnDrawEdit.setButtonReleased(true);
					MainFrame.btnDrawEdit.doClick();
					MainFrame.btnDrawEdit.setBackground(SettingsFrame.DEFAULT_STATE_COLOR);

				}
				
				if(panel.selectionModeIsOn) {
					
					MainFrame.selectionButton.setButtonReleased(true);
					MainFrame.selectionButton.doClick();
					MainFrame.selectionButton.setBackground(SettingsFrame.DEFAULT_STATE_COLOR);

				}
				
				panel.toggleQueryMode();
			}	
		});
		
		btnSaveEdit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(panel.editModeIsOn) {
					
					if(layerListComboBox.getItemCount() > 0) {
						
						Layer layer = DrawingJPanel.currentLayer;
	
				        if((layer.isNotSaved())) {
				        	
				        	saveLayerToDB(layer);
				        	
				        } else { log("Nothing to save"); }
				        
					} else
						log("No layer selected");
				}
				
			}
		});
		
		databaseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(dbCatalog != null) {
					dbCatalog.dispose();
				} 

				dbCatalog = new DatabaseCatalog(dbConnection);
				dbCatalog.setVisible(true);
				
				
			}
		});
		
		settingsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(settingsFrame != null) {
					if(settingsFrame.getFrame().isVisible()) {
						settingsFrame.getFrame().setVisible(false);
					} else if(!settingsFrame.getFrame().isVisible()) {
						settingsFrame.getFrame().setVisible(true);
					}
				} 	
			}
		});
		
		btnGrid.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				panel.toggleGrid();
			}
			
		});
		
		if(SettingsFrame.gridToggle.getState() != panel.gridIsOn) {
			btnGrid.doClick();
			btnGrid.setButtonReleased(panel.gridIsOn);
			if(panel.gridIsOn) {
				btnGrid.setBackground(SettingsFrame.HIGHLIGHTED_STATE_COLOR);
			}
		}
		
		btnAddLayer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				handleAddNewLayerIntent();
				updateDrawButtonGroup();
			}	
		});
		
		btnSnap.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.toggleSnap();
				
			}
		});
		
		if(SettingsFrame.snapToggle.getState() != panel.snappingModeIsOn) {
			btnSnap.doClick();
			btnSnap.setButtonReleased(panel.snappingModeIsOn);
			if(panel.snappingModeIsOn) {
				btnSnap.setBackground(SettingsFrame.HIGHLIGHTED_STATE_COLOR);
			}
		}
		
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
				handleEditingSession("new");
			}
			
		});
		
		btnVertices.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.toggleDisplayVertices();
				
			}
		});
		
		// ---------------------------------------------------------------------------
		
	}
	
	/**
	 * Imports/ creates a new layer from CSV
	 */
	private void importLayerFromCSV() {
		
		// 0. Create a new layer
		// ----------------------
		Layer layer = new Layer(TableOfContents.getNewLayerID(), true, "", "");
		
		try {
			
			// 1. Hide the import frame for the JOption pane
			// ----------------------------------------------
			importExportFrame.setVisible(false);
			
			// 2. Create contents for the JOption pane
			// ----------------------------------------
			JPanel panel = new JPanel();
			
			// 3. With just 3 rows, Set up the options and put action commands to know what item was selected during evaluation
			// ------------------------------------------------------------------------------------------------------------------
			panel.setLayout(new GridLayout(3, 1));
			
			JRadioButton point = new JRadioButton(SettingsFrame.POINT_GEOMETRY);
			point.setActionCommand(SettingsFrame.POINT_GEOMETRY);
			
			JRadioButton polyline = new JRadioButton(SettingsFrame.POLYLINE_GEOMETRY);
			polyline.setActionCommand(SettingsFrame.POLYLINE_GEOMETRY);
			
			JRadioButton polygon = new JRadioButton(SettingsFrame.POLYGON_GEOMETRY);
			polygon.setActionCommand(SettingsFrame.POLYGON_GEOMETRY);
			
			// 4. Make the first item selected
			// --------------------------------
			point.setSelected(true);
			
			// 5. Add the buttons to a button group
			// ------------------------------------
			ButtonGroup group = new ButtonGroup();
			group.add(point);
			group.add(polyline);
			group.add(polygon);
			
			// 6. Add the buttons to the panel
			// -------------------------------
			panel.add(point);
			panel.add(polyline);
			panel.add(polygon);
			
			// 7. Embed inside a JOption pane and get the response
			// ---------------------------------------------------
			int response = JOptionPane.showConfirmDialog( MainFrame.this, panel , "Choose geometry type", JOptionPane.OK_CANCEL_OPTION);
			
			// 8. If the response is an OK option
			// ----------------------------------
			if(response == JOptionPane.OK_OPTION) {
				
				// 8.1 Dispose the earlier created frame
				importExportFrame.dispose();
				
				// 8.2 Get the selected option from the action commands in (3)
				String geomSelected = group.getSelection().getActionCommand();
				
				// 8.3 Use the file handler to create features into the layer created at (0) using the geometry selected
				FileHandler.readFromCSV(layer, geomSelected);
				
				// 8.4 Prompt the user to enter a layer name
				String layerName = JOptionPane.showInputDialog(MainFrame.this, "Enter new layer name ");
				
				// 8.5 Finally create a new layer if there is a name input
				if(layerName != null) {
					layer.setLayerName(layerName);
					boolean added = tableOfContents.addRowLayer(layer);
					if(added) {
						layer.setNotSaved(false);
					}
				} else {
					log("Layer creation aborted");
				}
			
			// 9. If cancel option was selected from the JOption pane, make the initial frame visible until it is 
			//    properly disposed by the user
			// --------------------------------
			} else {
				importExportFrame.setVisible(true);
			}
		
		// 10. Catch errors during CSV file reading
		// ----------------------------------------
		} catch (IOException e1) {
			log(e1.getMessage());
			e1.printStackTrace();
		}
	}

	/**
	 * Imports layer from geoJson
	 * 
	 * WORK IN PROGRESS
	 */
	private void importLayerFromGeoJson() {
		
		importExportFrame.setVisible(false);
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(3, 1));
		ButtonGroup group = new ButtonGroup();
		JRadioButton wgs84 = new JRadioButton("WGS 84");
		wgs84.setActionCommand("WGS84");
		JRadioButton gausKru = new JRadioButton("Gauss�Kr�ger");
		gausKru.setActionCommand("GaussKrurger");
		JRadioButton lambert = new JRadioButton("Lambert 2005");
		lambert.setActionCommand("Lambert");
		
		wgs84.setSelected(true);
		group.add(wgs84);
		group.add(gausKru);
		group.add(lambert);
		
		panel.add(wgs84);
		panel.add(gausKru);
		panel.add(lambert);
		
		
		int response = JOptionPane.showConfirmDialog( MainFrame.this, panel , "Choose datum", JOptionPane.OK_CANCEL_OPTION);
		
		if(response == JOptionPane.OK_OPTION) {
			importExportFrame.dispose();
			
			String datumSelected = group.getSelection().getActionCommand();
			
			//System.out.println(datumSelected);
			Layer layer = new Layer(TableOfContents.getNewLayerID(), true, SettingsFrame.POINT_GEOMETRY, "Test");
			FileHandler.readFromGeoJson(datumSelected, layer);
			
			tableOfContents.addRowLayer(layer);
			layer.setNotSaved(false);
			
		} else {
			importExportFrame.setVisible(true);
		}
		
	}

	/**
	 * Handles when the user clicks the add new layer button
	 */
	private void handleAddNewLayerIntent() {
		
		// 0. Disable query mode
		panel.disableQueryMode();
		
		// 1. Create list of possible geometries
		// -------------------------------------
		String[] geom = {SettingsFrame.POLYGON_GEOMETRY, SettingsFrame.POLYLINE_GEOMETRY, SettingsFrame.POINT_GEOMETRY};
		
		// 2. Put list inside a combo box
		// ------------------------------
		JComboBox<String> geomList = new JComboBox<String>(geom);
		
	    // a. Create a J panel and set the layout
	    JPanel layerPanel = new JPanel();
	    layerPanel.setLayout(new GridLayout(4,1));
	  
	    // b. Create the text fields
	    String autoGeneratedLayerName = SettingsFrame.txtNewlayer.getText().toString() + TableOfContents.getNewLayerID();
	    JTextField layerNameTextField = new JTextField(autoGeneratedLayerName);
	   
	    // c. Add the components to panel
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
			if( geomList.getSelectedItem().toString().equals(SettingsFrame.POLYGON_GEOMETRY ) ||
			    geomList.getSelectedItem().toString().equals(SettingsFrame.POLYLINE_GEOMETRY) ||
			    geomList.getSelectedItem().toString().equals(SettingsFrame.POINT_GEOMETRY )
					) {

				// 4.2 Gets the layer name from the text field
				String layerName = layerNameTextField.getText().toString();
				// 4.3 If no name was inputed, use the auto-generated layer name
				if(layerName.length() < 1) { layerName = autoGeneratedLayerName; }
				
				// 4.3 Create a new layer
				createNewLayer(geomList.getSelectedItem().toString(), layerName);
				
			}
		}
	}
	
	
	/**
	 * Handles when the user wants to start editing session <br>
	 * Checks if there are no layer on the table of contents first then
	 * starts the editing session. <br>
	 * The background of the button is left active during the duration of the edit session
	 * @param signal directive to start new drawing session or continue drawing session
	 */
	private void handleEditingSession(String signal) {
		
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
	private void handleLayerSaving(ItemEvent e) {
		
		
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
        		   saveLayerToDB(layer);
   
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
	 * Saves a layer to the database<br>
	 * Checks for existing layers from the database<br>
	 * Prompts to overwrite the layer if the same name was found<br>
	 * After saving, the layer notSavedState will be turned to false.
	 * @param layer layer to be saved to the database
	 * @return true if the operation was successful
	 */
	public static boolean saveLayerToDB(Layer layer) {


		try {
			 
 		   // Checks for existing layers from the database
 		   boolean layerDoesNotExist = true;
 		   
 		   for(String existingTable[] : dbConnection.getTables()) {
 			   if(existingTable[0].equals(layer.getLayerName())) {
 				  layerDoesNotExist = false;
 				  break;
 			   }
 		   }
 		   
 		   // If layerDoesNotExist, just write to the db
 		   if(layerDoesNotExist) {
 			   
 			   	dbConnection.writeTable(layer.getLayerName(), layer);
				
				panel.showAnimatedHint("Saved!", SettingsFrame.FEATURE_CREATED_COLOR);
				log("Layer saved to DB");
				
				layer.setNotSaved(false);
				layer.setInDatabase(true);
				
				if(dbCatalog != null) {
					dbCatalog.addNewLayerToNode(layer.getLayerName());
				}

				return true;
				
 		   } else {
 			   
 			  panel.showAnimatedHint("Layer name exists!", SettingsFrame.DEFAULT_ERROR_COLOR);
 			  log("Layer with same name was found in the database, confirm to overwrite");
 			  
 			  int response = JOptionPane.showConfirmDialog(null, "Overwrite existing layer?", "Confirm", JOptionPane.YES_NO_OPTION );
 			  
 			  if(response == JOptionPane.YES_OPTION) {
 				  
 				 dbConnection.writeTable(layer.getLayerName(), layer);
 				
 				 panel.showAnimatedHint("Saved!", SettingsFrame.FEATURE_CREATED_COLOR);
 				 log("Layer saved to DB");
 				
 				 layer.setNotSaved(false);

 				 return true;
 			  }
 		   }
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
			panel.showAnimatedHint("Something went wrong /n Cannot save to DB", SettingsFrame.DEFAULT_ERROR_COLOR);
			log(e.getMessage());
			
			return false;
		}
		
		return false;
	}

	public static void createNewLayer(String layerType, String layerName) {
		
		
		// 1. Create a new layer
		Layer newLayer = new Layer(TableOfContents.getNewLayerID(), true, layerType, layerName );
		
		// 2. Add to the table of content
		boolean added = tableOfContents.addRowLayer(newLayer);
		
		if(added) {
			// 3. Log some message
			String message = "New " + newLayer.getLayerType() + " layer: "+ newLayer.getLayerName() + " was created";
			log(message);
			panel.showAnimatedHint(message, SettingsFrame.DEFAULT_STATE_COLOR);
		}	

	}
	
	/**
	 * Creates a new layer from a database result set
	 * @param resultSet result set from database query
	 * @param layerName the new layer name to store the features contained in the result set
	 * @return null
	 */
	public static Layer createLayerFromResultSet(ResultSet resultSet, String layerName) {
	
		try {
			
			String layerType = "";
			Layer newLayer = new Layer(TableOfContents.getNewLayerID(), true, "", layerName);

			while (resultSet.next()) {
				
				boolean isEllipse = resultSet.getBoolean(3);
				
				layerType = resultSet.getString(2);
				
				Double[] aX = (Double[]) resultSet.getArray(4).getArray();
				Double[] aY = (Double[]) resultSet.getArray(5).getArray();
				double rx = resultSet.getDouble(6);
				double ry = resultSet.getDouble(7);
				
				createFeatureFromResultSet(newLayer, layerType, isEllipse, aX, aY, rx, ry);
				
				
			}
			newLayer.setLayerName(layerName);
			newLayer.setNotSaved(false);
			tableOfContents.addRowLayer(newLayer);

			return newLayer;
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

		return null;

	}
	/**
	 * Creates a feature from a result set
	 * @param newLayer the layer to add the feature into
	 * @param layerType the layer geometry type e.g Polygon
	 * @param isEllipse if the feature is an ellipse
	 * @param aX array of X coordinates
	 * @param aY array of Y coordinates
	 * @param rx radius at X
	 * @param ry radius at Y
	 */
	public static void createFeatureFromResultSet(Layer newLayer, String layerType, boolean isEllipse, Double[] aX,
			Double[] aY, double rx, double ry) {
		if(isEllipse) {
			
			List<Rectangle2D> vertices = new ArrayList<Rectangle2D>();
			
			double x = aX[0];
			double y = aY[0];
			
			
			// Center vertix
			vertices.add(new Rectangle2D.Double(x - (SettingsFrame.SNAP_SIZE / 2), y - (SettingsFrame.SNAP_SIZE / 2),
					SettingsFrame.SNAP_SIZE, SettingsFrame.SNAP_SIZE));
			
			// AXIS_X
			vertices.add(new Rectangle2D.Double((x + rx) - (SettingsFrame.SNAP_SIZE / 2), y - (SettingsFrame.SNAP_SIZE / 2),
					SettingsFrame.SNAP_SIZE, SettingsFrame.SNAP_SIZE));
			
			// Ellipse
			Feature feature = new Feature(newLayer.getNextFeatureID());
			Shape circleShape = new Ellipse2D.Double(x - rx, y - ry , rx * 2, ry * 2);
			
			String featureType = "Ellipse";
			if(rx == ry) {
				featureType = "Circle";
			} else {
				// AXIS_Y
				vertices.add(new Rectangle2D.Double(x - (SettingsFrame.SNAP_SIZE / 2), (y - ry) - (SettingsFrame.SNAP_SIZE / 2),
						SettingsFrame.SNAP_SIZE, SettingsFrame.SNAP_SIZE));
			}
			
			feature.setEllipse(isEllipse, new Point2D.Double(x,y), rx, ry);
			feature.setShape(circleShape);
			feature.setFeatureType(featureType);
			feature.setVertices(vertices);
			feature.setVisibile(true);
			newLayer.setLayerType(layerType);
			newLayer.getListOfFeatures().add(feature);
			
		} 
		
		else if (layerType.equals(SettingsFrame.POINT_GEOMETRY)) {
			
			for(int i = 0; i < Math.min(aX.length, aY.length); i++) {
				
				Point2D pointCoord = new Point2D.Double(aX[0], aY[0]);
				
				Feature point  = new PointItem(newLayer.getNextFeatureID(), pointCoord);
				point.setFeatureType(SettingsFrame.POINT_GEOMETRY);
				point.setLayerID(newLayer.getId());
				point.setShape(new Ellipse2D.Double(pointCoord.getX() - SettingsFrame.POINT_SIZE/2,
						pointCoord.getY() - SettingsFrame.POINT_SIZE/2,
						SettingsFrame.POINT_SIZE, SettingsFrame.POINT_SIZE));
				point.getVertices().clear();
				point.getVertices().add(new Rectangle2D.Double(pointCoord.getX() - SettingsFrame.POINT_SIZE/2,
						pointCoord.getY() - SettingsFrame.POINT_SIZE/2,
						SettingsFrame.POINT_SIZE, SettingsFrame.POINT_SIZE));
				
				newLayer.getListOfFeatures().add(point);
				newLayer.setLayerType(SettingsFrame.POINT_GEOMETRY);
				newLayer.setNotSaved(true);
				
			}
		}
		
		else {
			
			// Normal path - polygon and polyline
			List<Rectangle2D> vertices = new ArrayList<Rectangle2D>();

			for(int i = 0; i < Math.min(aX.length, aY.length); i++) {
				vertices.add(new Rectangle2D.Double(aX[i] - (SettingsFrame.SNAP_SIZE / 2), aY[i] - (SettingsFrame.SNAP_SIZE / 2),
						SettingsFrame.SNAP_SIZE, SettingsFrame.SNAP_SIZE));
			}

			newLayer.setLayerType(layerType); // ! important
			
			panel.finishPath(vertices, newLayer);
			String currentFeatureType = "";
			if(layerType.equals(SettingsFrame.POLYGON_GEOMETRY)) {
				if(vertices.size() > 4) {
					currentFeatureType = "Freeform Polygon";
				} 
				else if (vertices.size() == 3) {
					currentFeatureType = "Triangle";
				}
				else if (vertices.size() == 4) {
					if(	(vertices.get(0).getCenterX() == vertices.get(2).getCenterX()) &&
						(vertices.get(0).getCenterY() == vertices.get(1).getCenterY()) &&
						
						(vertices.get(1).getCenterX() == vertices.get(3).getCenterX()) &&
						(vertices.get(2).getCenterY() == vertices.get(3).getCenterY())) {
						
						currentFeatureType = "Rectangle";
					}
				}
			} 
			
			else if (layerType.equals(SettingsFrame.POLYLINE_GEOMETRY)) {
				currentFeatureType = "Single line";
				if(vertices.size() > 2) {
					currentFeatureType = "Multi line";
				}
			}
			
			// Get last added feature and set the current feature type
			newLayer.getListOfFeatures().get(newLayer.getListOfFeatures().size()-1).setFeatureType(currentFeatureType);
			
		}
	}

	/**
	 * Gets the current feature type selected on the drawButtonGroup
	 * @return the selected feature type
	 */
	public static String getCurrentFeatureType() {
		
		if(drawButtonGroup.getSelection() != null) {
		
			for (Enumeration<AbstractButton> buttons = drawButtonGroup.getElements(); buttons.hasMoreElements();) {
				
				DrawIconButton button = (DrawIconButton) buttons.nextElement();
					
				if(button.isSelected()) {
					
					return button.getFeatureType();
				}
	        }
		} 
		
		return null;
		
	}

	/**
	 * Logs messages on the frame with simple animation
	 * @param string message to log
	 */
	public static void log(String string) {
		
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		logText.append("\n@" + dtf.format(now)+ " " + string );
		
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
	 * @param listOfLayersInString new layer names to be updated
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void updateLayerComboBoxModel(String[] listOfLayersInString) {
		
		model = new DefaultComboBoxModel( listOfLayersInString );
		MainFrame.layerListComboBox.setModel( model );
	}
	
	/**
	 * Updates the list of drawable geometries based on the current layer selected<br>
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
			btnDrawEdit.setBackground(SettingsFrame.DEFAULT_STATE_COLOR);
			
		}
			
	}
	
	/**
	 * Disables all draw buttons.
	 * 
	 */
	public static void disableAllDrawButtons() {
		
		for (Enumeration<AbstractButton> buttons = drawButtonGroup.getElements(); buttons.hasMoreElements();) {
			DrawIconButton button = (DrawIconButton) buttons.nextElement();
			button.setEnabled(false);
		}
	}

	/**
	 * Releases all draw buttons that cannot be drawn on a specific geometry type.<br>
	 * The buttons are identified from their corresponding action commands.
	 * @param geometry the geometry type to ignore
	 */
	public static void releaseAllOtherToolsButton(String geometry) {
		
		// 1. Loop through all the buttons in the draw button group
		// ----------------------------------------------------------
		for (Enumeration<AbstractButton> buttons = drawButtonGroup.getElements(); buttons.hasMoreElements();) {
            
			ToolIconButton button = (ToolIconButton) buttons.nextElement();
			
			// a. Get the layer type of the button
			// TODO validate constructing a draw button later!
			
			// b. Check if the draw button can be drawn on the current layer type
			if(!(geometry.equals(button.getActionCommand()))) {
				
				button.setButtonReleased(true);
				button.setSelected(false);
				button.setBackground(SettingsFrame.DEFAULT_STATE_COLOR);
				
			}
        }
	}

	/**
	 * Renames a layer in the database by dropping the old layer's table and writing a new one.<br>
	 * The layer combo list is also updated.
	 * @param oldName old layer name
	 * @param layer layer to be written
	 */
	public static void renameLayerInDatabase(String oldName, Layer layer) {
		
		try {
			
			// Drop the old table
			MainFrame.dbConnection.dropTable(oldName);
			
			// Write a new one
			MainFrame.dbConnection.writeTable(layer.getLayerName(), layer);
			
			// Update the combo box
			updateLayerComboBoxModel(TableOfContents.getListOfLayersInString());
			
			log(oldName + " renamed to " + layer.getLayerName());
			panel.showAnimatedHint("Success", SettingsFrame.FEATURE_CREATED_COLOR);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			
			log("Can not rename layer, an error occured : " + e.getMessage());
			panel.showAnimatedHint("Error", SettingsFrame.DEFAULT_ERROR_COLOR);
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
}