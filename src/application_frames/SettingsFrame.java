package application_frames;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import core_components.DrawIconButton;
import core_components.ToolIconButton;
import custom_components.CustomColorPicker;
import custom_components.CustomJFrame;
import custom_components.CustomJToggle;
import database.DatabaseConnection;
import file_handling.DatabaseCredentialsManager;
import toolset.Tools;
import javax.swing.JComboBox;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Class contains the general settings for the application<br>
 * In this class the general settings of the application are defined.
 * @author Olumide Igbiloba
 * @since Dec 7, 2017
 * @version
 * a. Dec 29, 2017 - Implement look and feel<br>
 * b. Dec 30, 2017 - Added support for changing application theme<br>
 */
public class SettingsFrame {
	
	private JPanel contentPane;
	private CustomJFrame frame;
	
	/**
	 * Returns the Frame
	 * @return the frame
	 */
	public CustomJFrame getFrame() {
		return frame;
	}

	// Database params
	public static JTextField dbHost;
	public static JTextField dbPort;
	public static JTextField dbName;
	public static JPasswordField dbPassword;
	public static JTextField dbUsername;
	
	// General settings
	public static JTextField userDefaultDirectory;
	public static JTextField userProfile;
	public static JTextField userSoftwareUse;
	public static JTextField userOccupation;
	
	public static JLabel settingsMessage;
	public static final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	public static final GraphicsDevice[] gs = ge.getScreenDevices();
	public static JSpinner monitorSpinner = new JSpinner(new SpinnerNumberModel(1, 1, gs.length, 1));
	public static JSpinner gridSizeSpinner, snapSizeSpinner;
	public int[] windowSize = getDefaultWindowSize();
	
	public static Rectangle window = getWindow();
	
	private static boolean initialized = false;
	private static String PASSWORD = "";
	public static CustomJToggle snapToggle, gridToggle;
	public static int THEME = 1;
	
	private MainFrame mainFrame;
	public static JComboBox<String[]> themeCmbBox;
	public static DefaultComboBoxModel<String[]> model;
	

	/**
	 * Creates the frame.
	 * @param openMainFrame the openMainFrame to be set
	 * @param mainFrame the application mainframe
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public SettingsFrame(boolean openMainFrame, MainFrame mainFrame) {
		
		this.mainFrame = mainFrame;
		
		frame = new CustomJFrame();
		frame.setResizable(false);
		frame.setTitle("Settings");

		frame.addWindowListener(new WindowAdapter() {
			@Override 
			public void windowClosing(WindowEvent e) { 
				handleWindowClosingEvent(e);
			} 
		});
		
		frame.setBounds(SettingsFrame.window.x  + (SettingsFrame.window.width - 1210) / 2, 
				SettingsFrame.window.y + (SettingsFrame.window.height - 735) / 2,
				1210, 
				735);
		
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.window);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 80, 711);
		panel.setBackground(Color.DARK_GRAY);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBounds(3, 5, 75, 75);
		panel.add(label);
		label.setIcon(Tools.getIconImage("/images/settings.png", 75, 75));
		
		JLabel label_1 = new JLabel();
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setIcon(Tools.getIconImage("/images/help.png", 40, 40));
		label_1.setBounds(20, 658, 40, 40);
		panel.add(label_1);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(79, 0, 1127, 143);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblSettings = new JLabel("Settings");
		lblSettings.setFont(new Font("Tahoma", Font.BOLD, 23));
		lblSettings.setBounds(21, 27, 402, 32);
		panel_1.add(lblSettings);
		
		JLabel lblGeneralSettings = new JLabel("General settings");
		lblGeneralSettings.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblGeneralSettings.setBounds(57, 81, 310, 20);
		panel_1.add(lblGeneralSettings);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(SystemColor.controlShadow);
		separator.setBounds(79, 176, 1127, 2);
		contentPane.add(separator);
		
		settingsMessage = new JLabel("Click to the green button to test connection to your database");
		settingsMessage.setForeground(Color.BLACK);
		settingsMessage.setBackground(Color.BLACK);
		settingsMessage.setFont(new Font("Tahoma", Font.BOLD, 12));
		settingsMessage.setBounds(90, 151, 1106, 14);
		contentPane.add(settingsMessage);
		
		if(gs.length > 1) {
			settingsMessage.setText(settingsMessage.getText() + ".\t  Multiple screens detected, please choose desired screen ");
		}
		
		JPanel databaseConnectionPanel = new JPanel();
		databaseConnectionPanel.setBounds(90, 189, 536, 215);
		contentPane.add(databaseConnectionPanel);
		databaseConnectionPanel.setLayout(null);
		
		JPanel databaseConnectionSubPanel = new JPanel();
		databaseConnectionSubPanel.setBackground(Color.WHITE);
		databaseConnectionSubPanel.setBounds(10, 50, 516, 154);
		databaseConnectionPanel.add(databaseConnectionSubPanel);
		databaseConnectionSubPanel.setLayout(null);
		
		dbHost = new JTextField();
		dbHost.setBounds(95, 11, 411, 38);
		databaseConnectionSubPanel.add(dbHost);
		dbHost.setColumns(10);
		
		JButton btnNewButton = new JButton("Host");
		btnNewButton.setEnabled(false);
		btnNewButton.setMargin(new Insets(1,5,1,1));
		btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton.setBackground(SystemColor.controlHighlight);
		btnNewButton.setBounds(10, 11, 86, 38);
		databaseConnectionSubPanel.add(btnNewButton);
		
		dbPort = new JTextField("");
		dbPort.setColumns(10);
		dbPort.setBounds(95, 60, 159, 38);
		databaseConnectionSubPanel.add(dbPort);
		
		JButton btnPort = new JButton("Port");
		btnPort.setMargin(new Insets(1,5,1,1));
		btnPort.setHorizontalAlignment(SwingConstants.LEFT);
		btnPort.setEnabled(false);
		btnPort.setBackground(SystemColor.controlHighlight);
		btnPort.setBounds(10, 60, 86, 38);
		databaseConnectionSubPanel.add(btnPort);
		
		JButton btnDatabas = new JButton("Database");
		btnDatabas.setMargin(new Insets(1,5,1,1));
		btnDatabas.setHorizontalAlignment(SwingConstants.LEFT);
		btnDatabas.setEnabled(false);
		btnDatabas.setBackground(SystemColor.controlHighlight);
		btnDatabas.setBounds(264, 60, 86, 38);
		databaseConnectionSubPanel.add(btnDatabas);
		
		dbName = new JTextField();
		dbName.setColumns(10);
		dbName.setBounds(347, 60, 159, 38);
		databaseConnectionSubPanel.add(dbName);
		
		JButton btnPassword = new JButton("Password");
		btnPassword.setMargin(new Insets(1,5,1,1));
		btnPassword.setHorizontalAlignment(SwingConstants.LEFT);
		btnPassword.setEnabled(false);
		btnPassword.setBackground(SystemColor.controlHighlight);
		btnPassword.setBounds(264, 109, 86, 38);
		databaseConnectionSubPanel.add(btnPassword);
		
		dbPassword = new JPasswordField(PASSWORD);
		dbPassword.setColumns(10);
		dbPassword.setBounds(347, 109, 159, 38);
		databaseConnectionSubPanel.add(dbPassword);
		
		dbUsername = new JTextField(DatabaseConnection.dbUser);
		dbUsername.setColumns(10);
		dbUsername.setBounds(95, 109, 159, 38);
		databaseConnectionSubPanel.add(dbUsername);
		
		JButton btnUsername = new JButton("Username");
		btnUsername.setMargin(new Insets(1,5,1,1));
		btnUsername.setHorizontalAlignment(SwingConstants.LEFT);
		btnUsername.setEnabled(false);
		btnUsername.setBackground(SystemColor.controlHighlight);
		btnUsername.setBounds(10, 109, 86, 38);
		databaseConnectionSubPanel.add(btnUsername);
		
		JLabel lblDbConn = new JLabel("Database connection");
		lblDbConn.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblDbConn.setBounds(10, 11, 280, 28);
		databaseConnectionPanel.add(lblDbConn);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setToolTipText("Test database connection");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setIcon(Tools.getIconImage("/images/testdb.png", 25, 25));
		lblNewLabel.setBounds(496, 11, 30, 30);
		databaseConnectionPanel.add(lblNewLabel);
		
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
				try {
					
					String host = (SettingsFrame.dbHost.getText());
					int port = Integer.parseInt((SettingsFrame.dbPort.getText()));
					String database = (SettingsFrame.dbName.getText());
					String user = SettingsFrame.dbUsername.getText();
					String password = String.valueOf(SettingsFrame.dbPassword.getPassword());
					
					PASSWORD = password;
					
					new DatabaseConnection(host, port, database, user, password);
					
					settingsMessage.setForeground(Color.BLACK);
					settingsMessage.setText("Database connection successfull");
					
				} catch (ClassNotFoundException | SQLException | NumberFormatException e1) {
					
					settingsMessage.setForeground(Color.RED);
					settingsMessage.setText("CANNOT CONNECT TO DATABASE \t\t " + e1.getMessage());
				}
			}
		});
		
		JPanel generalSettingsPanel = new JPanel();
		generalSettingsPanel.setLayout(null);
		generalSettingsPanel.setBounds(660, 189, 536, 215);
		contentPane.add(generalSettingsPanel);
		
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(null);
		panel_5.setBackground(Color.WHITE);
		panel_5.setBounds(10, 50, 516, 154);
		generalSettingsPanel.add(panel_5);
		
		userProfile = new JTextField(System.getProperty("user.name"));
		userProfile.setColumns(10);
		userProfile.setBounds(95, 11, 159, 38);
		panel_5.add(userProfile);
		
		JButton btnGridSize = new JButton("Profile");
		btnGridSize.setMargin(new Insets(1, 5, 1, 1));
		btnGridSize.setHorizontalAlignment(SwingConstants.LEFT);
		btnGridSize.setEnabled(false);
		btnGridSize.setBackground(SystemColor.controlHighlight);
		btnGridSize.setBounds(10, 11, 86, 38);
		panel_5.add(btnGridSize);
		
		userSoftwareUse = new JTextField("Educational purpose");
		userSoftwareUse.setColumns(10);
		userSoftwareUse.setBounds(95, 60, 411, 38);
		panel_5.add(userSoftwareUse);
		
		JButton btnUse = new JButton("Use");
		btnUse.setMargin(new Insets(1, 5, 1, 1));
		btnUse.setHorizontalAlignment(SwingConstants.LEFT);
		btnUse.setEnabled(false);
		btnUse.setBackground(SystemColor.controlHighlight);
		btnUse.setBounds(10, 60, 86, 38);
		panel_5.add(btnUse);
		
		JButton btnDirectory = new JButton("Directory");
		btnDirectory.setBounds(10, 109, 86, 38);
		panel_5.add(btnDirectory);
		btnDirectory.setMargin(new Insets(1, 5, 1, 1));
		btnDirectory.setHorizontalAlignment(SwingConstants.LEFT);
		btnDirectory.setEnabled(false);
		btnDirectory.setBackground(SystemColor.controlHighlight);
		
		userDefaultDirectory = new JTextField(System.getProperty("user.dir"));
		userDefaultDirectory.setBounds(95, 109, 411, 38);
		panel_5.add(userDefaultDirectory);
		userDefaultDirectory.setColumns(10);
		
		JButton btnOccupation = new JButton("Occupation");
		btnOccupation.setMargin(new Insets(1, 5, 1, 1));
		btnOccupation.setHorizontalAlignment(SwingConstants.LEFT);
		btnOccupation.setEnabled(false);
		btnOccupation.setBackground(SystemColor.controlHighlight);
		btnOccupation.setBounds(264, 11, 86, 38);
		panel_5.add(btnOccupation);
		
		userOccupation = new JTextField("Student");
		userOccupation.setColumns(10);
		userOccupation.setBounds(349, 11, 159, 38);
		panel_5.add(userOccupation);
		
		JLabel lblFile = new JLabel("General");
		lblFile.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblFile.setBounds(10, 11, 280, 28);
		generalSettingsPanel.add(lblFile);
		
		JPanel drawingSettingsPanel = new JPanel();
		drawingSettingsPanel.setLayout(null);
		drawingSettingsPanel.setBounds(90, 415, 1106, 223);
		contentPane.add(drawingSettingsPanel);
		
		JPanel drawingSettingsSubPanel = new JPanel();
		drawingSettingsSubPanel.setLayout(null);
		drawingSettingsSubPanel.setBackground(Color.WHITE);
		drawingSettingsSubPanel.setBounds(10, 50, 1086, 162);
		drawingSettingsPanel.add(drawingSettingsSubPanel);
		
		JLabel lblDraftSettings = new JLabel("Drafting and colors");
		lblDraftSettings.setForeground(Color.GRAY);
		lblDraftSettings.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblDraftSettings.setBounds(10, 11, 272, 28);
		drawingSettingsSubPanel.add(lblDraftSettings);
		
		JLabel lblGridSize = new JLabel("Grid size (mm)");
		lblGridSize.setForeground(Color.BLACK);
		lblGridSize.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblGridSize.setBounds(10, 50, 85, 28);
		drawingSettingsSubPanel.add(lblGridSize);
		
		JLabel lblDraftingBackgrouns = new JLabel("Drafting background");
		lblDraftingBackgrouns.setForeground(Color.BLACK);
		lblDraftingBackgrouns.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDraftingBackgrouns.setBounds(202, 50, 131, 28);
		drawingSettingsSubPanel.add(lblDraftingBackgrouns);
		
		JLabel lblHighlightColor = new JLabel("Highlight color");
		lblHighlightColor.setForeground(Color.BLACK);
		lblHighlightColor.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblHighlightColor.setBounds(202, 89, 131, 28);
		drawingSettingsSubPanel.add(lblHighlightColor);
		
		DRAFTING_BACKGROUND = new CustomColorPicker();
		DRAFTING_BACKGROUND.setBounds(332, 50, 36, 28);
		DRAFTING_BACKGROUND.setBackground(Color.WHITE);
		drawingSettingsSubPanel.add(DRAFTING_BACKGROUND);
		
		FEATURE_HIGHLIGHTED_STATE_COLOR = new CustomColorPicker();
		FEATURE_HIGHLIGHTED_STATE_COLOR.setBackground(Color.CYAN);
		FEATURE_HIGHLIGHTED_STATE_COLOR.setBounds(332, 89, 36, 28);
		drawingSettingsSubPanel.add(FEATURE_HIGHLIGHTED_STATE_COLOR);
		
		JLabel lblGridColor = new JLabel("Grid color");
		lblGridColor.setForeground(Color.BLACK);
		lblGridColor.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblGridColor.setBounds(10, 123, 131, 28);
		drawingSettingsSubPanel.add(lblGridColor);
		
		GRID_COLOR = new CustomColorPicker();
		GRID_COLOR.setBackground(Color.LIGHT_GRAY);
		GRID_COLOR.setBounds(140, 123, 36, 28);
		drawingSettingsSubPanel.add(GRID_COLOR);
		
		JLabel lblSelectionColor = new JLabel("Selection color");
		lblSelectionColor.setForeground(Color.BLACK);
		lblSelectionColor.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblSelectionColor.setBounds(202, 123, 131, 28);
		drawingSettingsSubPanel.add(lblSelectionColor);
		
		SELECTION_COLOR = new CustomColorPicker();
		SELECTION_COLOR.setBackground(new Color (135, 234, 105));
		SELECTION_COLOR.setBounds(332, 128, 36, 28);
		drawingSettingsSubPanel.add(SELECTION_COLOR);
		
		JLabel lblTextAndDefault = new JLabel("Text and default names");
		lblTextAndDefault.setForeground(Color.GRAY);
		lblTextAndDefault.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblTextAndDefault.setBounds(598, 10, 272, 28);
		drawingSettingsSubPanel.add(lblTextAndDefault);
		
		JLabel lblNewLayerName = new JLabel("New layer name");
		lblNewLayerName.setForeground(Color.BLACK);
		lblNewLayerName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLayerName.setBounds(598, 49, 131, 28);
		drawingSettingsSubPanel.add(lblNewLayerName);
		
		txtNewlayer = new JTextField();
		txtNewlayer.setText("New_layer");
		txtNewlayer.setColumns(10);
		txtNewlayer.setBounds(732, 50, 106, 28);
		drawingSettingsSubPanel.add(txtNewlayer);
		
		JLabel lblNewDocName = new JLabel("New document name");
		lblNewDocName.setForeground(Color.BLACK);
		lblNewDocName.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewDocName.setBounds(598, 88, 131, 28);
		drawingSettingsSubPanel.add(lblNewDocName);
		
		txtNewDoc = new JTextField();
		txtNewDoc.setText("Untitled");
		txtNewDoc.setColumns(10);
		txtNewDoc.setBounds(732, 89, 106, 28);
		drawingSettingsSubPanel.add(txtNewDoc);
		
		JLabel lblDisplay = new JLabel("Others");
		lblDisplay.setForeground(Color.GRAY);
		lblDisplay.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblDisplay.setBounds(880, 11, 183, 28);
		drawingSettingsSubPanel.add(lblDisplay);
		
		JLabel lblMonitor = new JLabel("Monitor");
		lblMonitor.setForeground(Color.BLACK);
		lblMonitor.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblMonitor.setBounds(880, 50, 131, 28);
		drawingSettingsSubPanel.add(lblMonitor);
		
		JLabel lblAutoSave = new JLabel("Autosave on close");
		lblAutoSave.setForeground(Color.BLACK);
		lblAutoSave.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblAutoSave.setBounds(881, 128, 131, 28);
		drawingSettingsSubPanel.add(lblAutoSave);
		
		JLabel lblShowHint = new JLabel("Show hint");
		lblShowHint.setForeground(Color.BLACK);
		lblShowHint.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblShowHint.setBounds(881, 89, 131, 28);
		drawingSettingsSubPanel.add(lblShowHint);
		
		JLabel lblSnap = new JLabel("Snap");
		lblSnap.setForeground(Color.BLACK);
		lblSnap.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblSnap.setBounds(391, 50, 107, 28);
		drawingSettingsSubPanel.add(lblSnap);
		
		snapToggle = new CustomJToggle(false);
		snapToggle.setBounds(497, 51, 60, 28);
		drawingSettingsSubPanel.add(snapToggle);
		
		JLabel lblGrid = new JLabel("Grid");
		lblGrid.setForeground(Color.BLACK);
		lblGrid.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblGrid.setBounds(391, 88, 107, 28);
		drawingSettingsSubPanel.add(lblGrid);
		
		gridToggle = new CustomJToggle(false);
		gridToggle.setBounds(497, 89, 60, 28);
		drawingSettingsSubPanel.add(gridToggle);
		
		HINT = new CustomJToggle(false);
		HINT.setBounds(1016, 89, 60, 28);
		drawingSettingsSubPanel.add(HINT);
		
		AUTOSAVE_TOGGLE = new CustomJToggle(true);
		AUTOSAVE_TOGGLE.setBounds(1016, 128, 60, 28);
		drawingSettingsSubPanel.add(AUTOSAVE_TOGGLE);
	
		monitorSpinner.setBounds(1021, 50, 55, 28);
		drawingSettingsSubPanel.add(monitorSpinner);
		
		gridSizeSpinner = new JSpinner(new SpinnerNumberModel(5, 5, 15, 1));
		gridSizeSpinner.setBounds(140, 50, 36, 28);
		drawingSettingsSubPanel.add(gridSizeSpinner);
		
		JLabel lblGI = new JLabel("Snap size (mm)");
		lblGI.setForeground(Color.BLACK);
		lblGI.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblGI.setBounds(10, 89, 131, 28);
		drawingSettingsSubPanel.add(lblGI);
		
		snapSizeSpinner = new JSpinner(new SpinnerNumberModel(10, 10, 20, 1));
		snapSizeSpinner.setBounds(140, 89, 36, 28);
		drawingSettingsSubPanel.add(snapSizeSpinner);
		
		snapSizeSpinner.getModel().addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {

				SettingsFrame.SNAP_SIZE = (int) snapSizeSpinner.getValue();
				if (MainFrame.panel != null) {
					MainFrame.panel.repaint();
				}
			}
		});
		
		JLabel lblTheme = new JLabel("Theme");
		lblTheme.setForeground(Color.BLACK);
		lblTheme.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblTheme.setBounds(598, 128, 131, 28);
		drawingSettingsSubPanel.add(lblTheme);
		
		String[] themes = {"Light","Dark"};
		model = new DefaultComboBoxModel( themes );
		
		themeCmbBox = new JComboBox<String[]>();
		
		themeCmbBox.setModel(model);
		themeCmbBox.setBounds(732, 128, 106, 28);
		drawingSettingsSubPanel.add(themeCmbBox);
		
		
		
		themeCmbBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {


				String lnfName = "com.jtattoo.plaf.smart.SmartLookAndFeel";
				
				if(themeCmbBox.getSelectedIndex() == 1) {
					lnfName = "com.jtattoo.plaf.hifi.HiFiLookAndFeel";
				}
				
				updateTheme(lnfName);

			}
		});
		
		JLabel lblDrawingSettings = new JLabel("Drawing settings");
		lblDrawingSettings.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblDrawingSettings.setBounds(10, 11, 280, 28);
		drawingSettingsPanel.add(lblDrawingSettings);
		
		JButton btnFinish = new JButton("Finish");
		btnFinish.setBorderPainted(false);
		btnFinish.setFocusPainted(false);
		btnFinish.setForeground(SystemColor.text);
		btnFinish.setBackground(SettingsFrame.HIGHLIGHTED_STATE_COLOR);
		btnFinish.setBounds(1057, 662, 139, 38);
		contentPane.add(btnFinish);
		
		btnFinish.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(openMainFrame) {
					
					try {
						
						String host = (SettingsFrame.dbHost.getText());
						int port = Integer.parseInt((SettingsFrame.dbPort.getText()));
						String database = (SettingsFrame.dbName.getText());
						String user = SettingsFrame.dbUsername.getText();
						String password = String.valueOf(SettingsFrame.dbPassword.getPassword());
						
						SettingsFrame.window = getWindow();
						if(!initialized){
							mainFrame.start(new DatabaseConnection(host, port, database, user, password));
							initialized = true;
						}
						
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					
					
					
				}
				//dispose();
				frame.setVisible(false);
			}
			
		});
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(SystemColor.controlShadow);
		separator_1.setBounds(79, 649, 1127, 2);
		contentPane.add(separator_1);
		
		JLabel lblcLicense = new JLabel("Licensed under the Apache License, Version 2.0");
		lblcLicense.setFont(new Font("Tahoma", Font.ITALIC, 13));
		lblcLicense.setBounds(90, 674, 310, 20);
		contentPane.add(lblcLicense);
		
		JButton btnSave = new JButton("Save");
		btnSave.setForeground(Color.WHITE);
		btnSave.setFocusPainted(false);
		btnSave.setBorderPainted(false);
		btnSave.setBackground(Color.DARK_GRAY);
		btnSave.setBounds(908, 662, 139, 38);
		contentPane.add(btnSave);
		

		// Get DB params from credential manager
		try {
			DatabaseCredentialsManager databaseCredentialsManager = new DatabaseCredentialsManager();
			dbHost.setText(databaseCredentialsManager.host);
			dbPort.setText(String.valueOf(databaseCredentialsManager.port));
			dbName.setText(databaseCredentialsManager.database);
			dbUsername.setText(databaseCredentialsManager.user);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveAllChanges();
			}
		});
	}

	/**
	 * Changes the theme from light to dark or vice versa.
	 * @param lnfName Name of the theme to be applied.
	 */
	public void updateTheme(String lnfName) {
		try {
			UIManager.setLookAndFeel(lnfName);


			SettingsFrame.DEFAULT_STATE_COLOR = new JButton().getBackground();
			if(themeCmbBox.getSelectedItem().toString().toUpperCase().equals("LIGHT")) {
				DEFAULT_STATE_COLOR = new Color(31, 105, 224);
				SettingsFrame.DRAFTING_BACKGROUND.setBackground(Color.WHITE);
				SettingsFrame.GRID_COLOR.setBackground(Color.LIGHT_GRAY);
			} else {
				SettingsFrame.DRAFTING_BACKGROUND.setBackground(Color.BLACK);
				SettingsFrame.GRID_COLOR.setBackground(Color.DARK_GRAY);
			}
			updateCustomButtonsLookAndFeel(SettingsFrame.DEFAULT_STATE_COLOR);

			SwingUtilities.updateComponentTreeUI(frame);
			SwingUtilities.updateComponentTreeUI(mainFrame);

			frame.repaint();
			frame.revalidate();

			mainFrame.repaint();
			mainFrame.revalidate();
			
			if(MainFrame.tableOfContents != null) {
				MainFrame.tableOfContents.setTablePreferredSizes();
			}
				
			

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
	};
	
	/**
	 * Updates the customs Buttons Look and Feel color
	 * @param newColor the Color to set
	 */
	private void updateCustomButtonsLookAndFeel(Color newColor) {
		for(ToolIconButton button : MainFrame.buttonsList) {
			if(!button.getBackground().equals(SettingsFrame.HIGHLIGHTED_STATE_COLOR)) {
				button.setBackground(newColor);
			}
		}
		for (Enumeration<AbstractButton> buttons = MainFrame.drawButtonGroup.getElements(); buttons.hasMoreElements();) {
			DrawIconButton button = (DrawIconButton) buttons.nextElement();
			if(!button.isSelected()) {
				button.setBackground(newColor);
			}
		}
	}

	/**
	 * Returns the Window
	 * @return the window
	 */
	private static Rectangle getWindow() {
		
		int screen = 0;
		if(monitorSpinner.getValue().toString() != null) {
			screen = Integer.parseInt(monitorSpinner.getValue().toString()) - 1;
		}
		
		return gs[screen].getDefaultConfiguration().getBounds();
		
	}

	/**
	 * Computes the width and height of the application.  If there are multiple displays attached, for some reason it
	 * thinks that the first display's width is the sum of all display widths.  So we have to subtract the widths of
	 * all other displays from the first if there are multiple displays attached.
	 * @return int[] in which the first element is the width and the second is the height.
	 */
	public static int[] getDefaultWindowSize() {
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		// If there is more than one device (gs.length > 1), subtract the widths of all subsequent displays from its width.
		int width;
		if (gs.length > 1) {
			width = gs[0].getDisplayMode().getWidth();
			int otherWidths = 0;
			for (int i=1; i<gs.length; i++) {
				otherWidths += gs[i].getDisplayMode().getWidth();
			}
			width = width - otherWidths;
		// Otherwise, just take the first (and only) display's width.
		} else {
			width = gs[0].getDisplayMode().getWidth();
		}

		// Height should stay the same.
		int height = gs[0].getDisplayMode().getHeight();

		return new int[] {width, height};

	}


	protected void saveAllChanges() {
		
		
		try {
			
			// DB Params
			
			DatabaseConnection.dbName = dbName.getText();
			DatabaseConnection.dbUser = dbUsername.getText();
			DatabaseConnection.dbHost = dbHost.getText();
			DatabaseConnection.dbPort = Integer.parseInt(dbPort.getText());
			
			MainFrame.dbConnection = new DatabaseConnection(DatabaseConnection.dbHost, DatabaseConnection.dbPort,
					DatabaseConnection.dbName, DatabaseConnection.dbUser, String.valueOf(dbPassword.getPassword()) );
			
			PASSWORD = String.valueOf(dbPassword.getPassword());
			
			// Save DB Params

			DatabaseCredentialsManager databaseCredentialsManager = new DatabaseCredentialsManager();
			databaseCredentialsManager.setDatabaseCredentials(dbHost.getText(), Integer.parseInt(dbPort.getText()),
					dbName.getText(), dbUsername.getText());

			// Drawing settings
			
			SettingsFrame.GRID_MM = (int) SettingsFrame.gridSizeSpinner.getValue();
			SettingsFrame.SNAP_SIZE = (int) SettingsFrame.snapSizeSpinner.getValue();
			
			if(MainFrame.panel != null) {
				MainFrame.panel.renderGrid(SettingsFrame.GRID_MM);
			}
			
			
			// Log some messages
			
			settingsMessage.setText("Settings saved");
			settingsMessage.setForeground(SettingsFrame.defaultColor);
			
		} catch (Exception e) {
			
			settingsMessage.setText("Something went wrong:  " + e.getMessage());
			settingsMessage.setForeground(SettingsFrame.DEFAULT_ERROR_COLOR);
			
			e.printStackTrace();
		}
		
	}

	/**
	 * Handles the Window Closing Event
	 * @param e the WindowEvent to set
	 */
	protected void handleWindowClosingEvent(WindowEvent e) {
		frame.setVisible(false);
	}

	// Software information
	public static final String TITLE = "GMCM3_Software_Eng";

	public static final int DEFAULT_DPI = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
	
	// Drawing settings
	public static int GRID_MM = 5;
	public static int SNAP_SIZE = 20;
	public static int GRID_MAJOR_INTERVAL = GRID_MM;
	public static int cursorSize = 25;

	public static double mouseOffset = 20;
	public static boolean DRAW_GUIDES_AND_TIPS = true;
	
	public static int POINT_SIZE = SettingsFrame.SNAP_SIZE;

	// GUI parameters
	public static final ImageIcon LAYER_DELETE_ICON = Tools.getIconImage("/images/bin.png", 15, 15);
	
	public static final String DRAW_CONTINUE = "continue";
	public static final String CLOSE_POLYGON_MESSAGE = "Click the first point to finish polygon";
	public static final String CLOSE_POLYLINE_MESSAGE = "Double click the first point to close the polyline";
	public static final String FINISH_POLYLINE_MESSAGE = "Double click the last point to finish drawing the polyline";
	public static final String DEFAULT_MOUSE_TIP = "Click the last point to finish shape";

	public static final int DEFAULT_LAYER_LINE_WEIGHT = 3;
	public static final int TRANSPARENCY_LEVEL_1 = 180;
	public static final int TRANSPARENCY_LEVEL_2 = 100;
	public static final int TOOL_TIP_PADDING = 5;
	
	
	// Geometry Identifiers
	// TODO: Change to int to reduce memory
	public static final String POLYLINE_GEOMETRY = "Polyline";
	public static final String POINT_GEOMETRY = "Point";
	public static final String POLYGON_GEOMETRY = "Polygon";
	
	public static Color cursorColor = new Color(244, 98, 66);
	public static Color defaultColor = Color.BLACK;
	public static Color DEFAULT_ERROR_COLOR = Color.RED;
	public static Color DEFAULT_SUCCESS_COLOR = new Color(31, 105, 224);
	public static Color DEFAULT_STATE_COLOR = new Color(31, 105, 224);
	
	public static CustomColorPicker DRAFTING_BACKGROUND;
	public static CustomColorPicker GRID_COLOR;
	public static CustomColorPicker FEATURE_HIGHLIGHTED_STATE_COLOR;
	public static CustomColorPicker SELECTION_COLOR;
	public static CustomJToggle HINT, AUTOSAVE_TOGGLE;
	
	public static final Color ICON_COLOR = new Color(31, 105, 224);
	public static final Color DEFAULT_LAYER_COLOR = Color.LIGHT_GRAY;
	public static final Color MUTE_STATE_COLOR = Color.LIGHT_GRAY;
	
	public static final Color HIGHLIGHTED_STATE_COLOR = new Color(239, 66, 14);
	public static final Color FEATURE_CREATED_COLOR = new Color (16, 91, 26);
	public static final int MONITOR_SCREEN = 1;
	public static final Dimension MAINFRAME_SIZE = new Dimension(1366, 768);
	public static final int FONT_SIZE = 15;

	
	public static JTextField txtNewlayer;
	public static JTextField txtNewDoc;
}
