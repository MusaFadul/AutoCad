package application_frames;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import core_classes.DatabaseConnection;
import toolset.Tools;

import java.awt.Color;
import javax.swing.JSeparator;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.SwingConstants;

public class Settings extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Dimension appSize = new Dimension(1250,750);
	public static JTextField dbHost;
	public static JTextField dbPort;
	public static JTextField dbName;
	public static JPasswordField dbPassword;
	public static JTextField dbUsername;
	public static JTextField userDefaultDirectory;
	public static JTextField userProfile;
	public static JTextField userSoftwareUse;
	public static JTextField userOccupation;

	/**
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Settings frame = new Settings();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	 * Create the frame.
	 */
	public Settings() {
		
		addWindowListener(new WindowAdapter() {
			@Override 
			public void windowClosing(WindowEvent e) { 
				handleWindowClosingEvent(e);
			} 
		});
		
		setBounds((windowSize.width - appSize.width) / 2, (windowSize.height - appSize.height) / 2, 1222, 750);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
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
		
		JLabel settingsMessage = new JLabel("CLick to the green button to test connection to your database");
		settingsMessage.setFont(new Font("Tahoma", Font.BOLD, 12));
		settingsMessage.setBounds(90, 151, 1106, 14);
		contentPane.add(settingsMessage);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(SystemColor.inactiveCaption);
		panel_2.setBounds(90, 189, 536, 215);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		panel_3.setBounds(10, 50, 516, 154);
		panel_2.add(panel_3);
		panel_3.setLayout(null);
		
		dbHost = new JTextField();
		dbHost.setText("localhost");
		dbHost.setBounds(95, 11, 411, 38);
		panel_3.add(dbHost);
		dbHost.setColumns(10);
		
		JButton btnNewButton = new JButton("Host");
		btnNewButton.setEnabled(false);
		btnNewButton.setMargin(new Insets(1,5,1,1));
		btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
		btnNewButton.setBackground(SystemColor.controlHighlight);
		btnNewButton.setBounds(10, 11, 86, 38);
		panel_3.add(btnNewButton);
		
		dbPort = new JTextField();
		dbPort.setText("5432");
		dbPort.setColumns(10);
		dbPort.setBounds(95, 60, 159, 38);
		panel_3.add(dbPort);
		
		JButton btnPort = new JButton("Port");
		btnPort.setMargin(new Insets(1,5,1,1));
		btnPort.setHorizontalAlignment(SwingConstants.LEFT);
		btnPort.setEnabled(false);
		btnPort.setBackground(SystemColor.controlHighlight);
		btnPort.setBounds(10, 60, 86, 38);
		panel_3.add(btnPort);
		
		JButton btnDatabas = new JButton("Database");
		btnDatabas.setMargin(new Insets(1,5,1,1));
		btnDatabas.setHorizontalAlignment(SwingConstants.LEFT);
		btnDatabas.setEnabled(false);
		btnDatabas.setBackground(SystemColor.controlHighlight);
		btnDatabas.setBounds(264, 60, 86, 38);
		panel_3.add(btnDatabas);
		
		dbName = new JTextField();
		dbName.setText("softeng_db");
		dbName.setColumns(10);
		dbName.setBounds(347, 60, 159, 38);
		panel_3.add(dbName);
		
		JButton btnPassword = new JButton("Password");
		btnPassword.setMargin(new Insets(1,5,1,1));
		btnPassword.setHorizontalAlignment(SwingConstants.LEFT);
		btnPassword.setEnabled(false);
		btnPassword.setBackground(SystemColor.controlHighlight);
		btnPassword.setBounds(264, 109, 86, 38);
		panel_3.add(btnPassword);
		
		dbPassword = new JPasswordField("12345");
		dbPassword.setColumns(10);
		dbPassword.setBounds(347, 109, 159, 38);
		panel_3.add(dbPassword);
		
		dbUsername = new JTextField();
		dbUsername.setText("postgres");
		dbUsername.setColumns(10);
		dbUsername.setBounds(95, 109, 159, 38);
		panel_3.add(dbUsername);
		
		JButton btnUsername = new JButton("Username");
		btnUsername.setMargin(new Insets(1,5,1,1));
		btnUsername.setHorizontalAlignment(SwingConstants.LEFT);
		btnUsername.setEnabled(false);
		btnUsername.setBackground(SystemColor.controlHighlight);
		btnUsername.setBounds(10, 109, 86, 38);
		panel_3.add(btnUsername);
		
		JLabel lblDatabaseConnection = new JLabel("Database connection");
		lblDatabaseConnection.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDatabaseConnection.setBounds(10, 11, 280, 28);
		panel_2.add(lblDatabaseConnection);
		
		JButton btnTestConnection = new JButton("");
		btnTestConnection.setBackground(SystemColor.inactiveCaption);
		btnTestConnection.setToolTipText("Test database connection");
		btnTestConnection.setBounds(503, 11, 23, 23);
		panel_2.add(btnTestConnection);
		btnTestConnection.setBorderPainted(false);
		btnTestConnection.setFocusPainted(false);
		btnTestConnection.setIcon(Tools.getIconImage("/images/testdb.png", 20, 20));
		
		btnTestConnection.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					
					String host = (Settings.dbHost.getText());
					int port = Integer.parseInt((Settings.dbPort.getText()));
					String database = (Settings.dbName.getText());
					String user = Settings.dbUsername.getText();
					String password = String.valueOf(Settings.dbPassword.getPassword());
					
					
				
					
					new DatabaseConnection(host, port, database, user, password);
					
					settingsMessage.setForeground(Color.BLACK);
					settingsMessage.setText("Database connection successfull");
					
				} catch (ClassNotFoundException | SQLException | NumberFormatException e1) {
					
					settingsMessage.setForeground(Color.RED);
					settingsMessage.setText("CANNOT CONNECT TO DATABASE \t\t " + e1.getMessage());
				}
			}
		});
		
		JPanel panel_4 = new JPanel();
		panel_4.setLayout(null);
		panel_4.setBackground(SystemColor.inactiveCaption);
		panel_4.setBounds(660, 189, 536, 215);
		contentPane.add(panel_4);
		
		JPanel panel_5 = new JPanel();
		panel_5.setLayout(null);
		panel_5.setBackground(Color.WHITE);
		panel_5.setBounds(10, 50, 516, 154);
		panel_4.add(panel_5);
		
		userProfile = new JTextField("John Doe");
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
		lblFile.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblFile.setBounds(10, 11, 280, 28);
		panel_4.add(lblFile);
		
		JPanel panel_6 = new JPanel();
		panel_6.setLayout(null);
		panel_6.setBackground(SystemColor.inactiveCaption);
		panel_6.setBounds(90, 415, 1106, 223);
		contentPane.add(panel_6);
		
		JPanel panel_7 = new JPanel();
		panel_7.setLayout(null);
		panel_7.setBackground(Color.WHITE);
		panel_7.setBounds(10, 50, 1086, 162);
		panel_6.add(panel_7);
		
		JLabel lblDrawingSettings = new JLabel("Drawing settings");
		lblDrawingSettings.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblDrawingSettings.setBounds(10, 11, 280, 28);
		panel_6.add(lblDrawingSettings);
		
		JButton btnFinish = new JButton("Close");
		btnFinish.setBorderPainted(false);
		btnFinish.setFocusPainted(false);
		btnFinish.setForeground(SystemColor.text);
		btnFinish.setBackground(Settings.HIGHLIGHTED_STATE_COLOR);
		btnFinish.setBounds(1057, 662, 139, 38);
		contentPane.add(btnFinish);
		
		btnFinish.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		});
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setForeground(SystemColor.controlShadow);
		separator_1.setBounds(79, 649, 1127, 2);
		contentPane.add(separator_1);
		
		JLabel lblcLicense = new JLabel("(c) 2017 License : -----");
		lblcLicense.setFont(new Font("Tahoma", Font.ITALIC, 13));
		lblcLicense.setBounds(90, 674, 310, 20);
		contentPane.add(lblcLicense);
	}
	
	protected void handleWindowClosingEvent(WindowEvent e) {
		dispose();
	}

	// Software information
	public static final String TITLE = "GMCM3_Software_Eng";
	
	// System configurations 
	public static final int DEFAULT_DPI = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();
	public static final Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	// Drawing settings
	public static int gridSizeMM = 5;
	public static int snappingTolerance = 20;
	public static int gridMajorInterval = 5;
	public static int cursorSize = 25;
	public static Color cursorColor = new Color(244, 98, 66);
	public static Color defaultColor = Color.BLACK;
	public static double mouseOffset = 20;
	public static boolean DRAW_GUIDES_AND_TIPS = true;
	public static Color DEFAULT_SELECTION_COLOR = new Color (135, 234, 105);

	// GUI parameters
	public static final ImageIcon LAYER_DELETE_ICON = Tools.getIconImage("/images/bin.png", 15, 15);
	public static final Color DEFAULT_LAYER_COLOR = Color.BLACK;
	public static final Color DEFAULT_VERTIX_COLOR = new Color(31, 105, 224);
	public static final String DRAW_CONTINUE = "continue";
	public static final String CLOSE_POLYGON_MESSAGE = "Click the first point to finish polygon";
	public static final String CLOSE_POLYLINE_MESSAGE = "Click first or last point to close polyline";
	public static final Color DEFAULT_STATE_COLOR = new Color(31, 105, 224);
	public static final Color HIGHLIGHTED_STATE_COLOR = new Color(239, 66, 14);
	public static final Color MUTE_STATE_COLOR = Color.LIGHT_GRAY;
	
	// Geometry Identifiers
	public static final String POLYLINE_GEOMETRY = "Polyline";
	public static final String POINT_GEOMETRY = "Point";
	public static final String POLYGON_GEOMETRY = "Polygon";

	
	public final static Color FEATURE_CREATED_COLOR = new Color (16, 91, 26);
	public static final String DEFAULT_MOUSE_TIP = "Click the last point to finish shape";
	public static final int DEFAULT_LAYER_LINE_WEIGHT = 3;
	public static final int TRANSPARENCY_LEVEL = 180;
	public static final int TOOL_TIP_PADDING = 5;
	public static final Color FEATURE_HIGHLIGHTED_STATE_COLOR = Color.CYAN;


}
