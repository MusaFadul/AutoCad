package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.JComboBox;

//test change
/**
 * @author Carina:
 *
 * Things to be added:
 * 2017/11/27
 * Environment Toolbar (Snapping Checkbox)
 * Icons
 * Layer Chooser (Table of Contents)
 * Size / Proportions of Menu Items
 * variable size of Environment Toolbar
 */


public class MainFrame extends JFrame {
	
	//~~~~~~~~~~~~~~~~~~~ DEFINING VARIABLES ~~~~~~~~~~~~~~~~~~~~~~~
	private static final long serialVersionUID = 1L;
	JFrame MainFrame;
	private String[] Shapes = { "Point" , "Polyline" , "Polygon",  "Rectangle",  "Triangle" };
    private String [] Status = {"Start","Finish"};
	
	
	//~~~~~~~~~~~~~~~~~~~ CONSTRUCTOR ~~~~~~~~~~~~~~~~~~~~~~~
	public MainFrame() {
		initialize();
		
	}

	
	//~~~~~~~~~~~~~~~~~~~ METHOD FOR DEFINING THE ELEMENTS OF THE GUI ~~~~~~~~~~~~~~~~~~~~~~~
	private void initialize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); // this is to get the screen size of the used device
		
		// creating MainFrame
		MainFrame = new JFrame();
		MainFrame.setBounds(0, 0, screenSize.width, screenSize.height);
		MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = MainFrame.getContentPane();
		//MainFrame.add(contentPane);
		
		// set the contentPane on which other things will be build up
		contentPane.setBackground(Color.RED);
		contentPane.setLayout(null);
		contentPane.setSize(screenSize.width, screenSize.height);
		
		
		//~~~~~~~~~~~~~~~~~~~ JPANELS FOR DIFFERENT PARTS OF GUI ~~~~~~~~~~~~~~~~~~~~~~~
		
		// MenuBar 
		JPanel menu = new JPanel();
 		menu.setBackground(Color.YELLOW);
		menu.setBounds(0, 0, screenSize.width, screenSize.height*1/10); // (x, y, width, height)
		contentPane.add(menu);
		
		// Environments
		JPanel env = new JPanel();
		env.setBackground(Color.CYAN);
		env.setBounds(0, screenSize.height*9/10, screenSize.width, screenSize.height*1/10); // (x, y, width, height)
		contentPane.add(env);
		
		//DrawingPanel
		JPanel DrawingPanel = new JPanel();
		DrawingPanel.setBackground(Color.GREEN);
		DrawingPanel.setBounds(screenSize.width*1/5, screenSize.height*1/10, screenSize.width*4/5, screenSize.height*8/10); // (x, y, width, height)
		contentPane.add(DrawingPanel);
		
		//Table of Contents
		JPanel TOC = new JPanel();
		TOC.setBackground(Color.BLUE);
		TOC.setBounds(0, screenSize.height*1/10, screenSize.width*1/5, screenSize.height*8/10); // (x, y, width, height)
		contentPane.add(TOC);
	

		// ~~~~~~~~~~~~~~~~~~~ ADDING ITEMS TO THE DIFFERENT PARTS OF THE GUI ~~~~~~~~~~~~~~~~~~~~~~~
		// 1. Menu
		
		JMenuBar jmenubar = new JMenuBar();
		jmenubar.setSize(screenSize.width, screenSize.height*1/10);
		menu.add(jmenubar);
		
		// setting JComboBox Status
		JComboBox combobox      = new JComboBox (Shapes);
		JComboBox EditingStatus = new JComboBox (Status);
		combobox.setRenderer(new MyComboBoxRenderer("Layers"));
		combobox.setSelectedIndex(-1 );
		EditingStatus.setRenderer(new MyComboBoxRenderer("Editor"));
		EditingStatus.setSelectedIndex(-1 );
		
		JMenuItem Import = new JMenuItem ("Import");
		JMenuItem export = new JMenuItem ("Export");
		JMenuItem query  = new JMenuItem ("Query");
		JMenuItem select= new JMenuItem ("Select");
		JMenuItem save = new JMenuItem ("Save");
		JMenuItem delete = new JMenuItem ("Delete");
		JMenuItem database = new JMenuItem ("Database");
		
		jmenubar.add(Import);
		jmenubar.add(export);
		jmenubar.add(query);
		jmenubar.add(select);
		jmenubar.add(EditingStatus);
		jmenubar.add(combobox);
		jmenubar.add(save);
		jmenubar.add(delete);
		jmenubar.add(database);
		jmenubar.setVisible(true);
		
		// 2. Environments
		JLabel xpos = new JLabel("x");
		JLabel ypos = new JLabel("y");
		env.add(xpos);
		env.add(ypos);
	
	} //initialize
	
	
	// ~~~~~~~~~~~~~~~~~~~ MAIN METHOD ~~~~~~~~~~~~~~~~~~~~~~~
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.MainFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
        
	
	// ~~~~~~~~~~~~~~~~~~~ CREATING A SPECIFIC CLASS FOR ADDING TITLES TO A JCOMBOBOX ~~~~~~~~~~~~~~~~~~~~~~~
	  class MyComboBoxRenderer extends JLabel implements ListCellRenderer
	    {
		  
		private static final long serialVersionUID = 1L;
			private String _title;
	        public MyComboBoxRenderer(String title)
	        {
	            _title = title;
	        }
	        @Override
	        public Component getListCellRendererComponent(JList list, Object value,
	                int index, boolean isSelected, boolean hasFocus)
	        {
	            if (index == -1 && value == null) setText(_title);
	            else setText(value.toString());
	            return this;
	        }
	    }
    
	

}
