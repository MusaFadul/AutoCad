package application_frames;

import java.awt.Color;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import core_classes.Feature;
import core_classes.Layer;
import custom_components.CustomJFrame;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Shows attribute table of all features in a layer
 * 
 * WORK IN PROGRESS
 * 
 * TODO: Support for showing more than one layer
 * 
 * @author OlumideEnoch
 *
 */
public class AttributeTableFrame extends CustomJFrame implements ActionListener {
	
	private static final long serialVersionUID = 2510826749504059745L;
	
	/**Feature ID column index on the attribute taable*/
	protected static final int FEATURE_ID_COL_INDEX = 1;

	/**Current table of the attribute table frame*/
	private JTable table = new JTable();
	
	/**Current table model of the attribute table frame*/
	private DefaultTableModel tableModel;
	
	/**Current layer of the attribute table*/
	private Layer layer;
	

	/**
	 * Create the frame.
	 * @param features 
	 */
	public AttributeTableFrame(Layer layer) {
		super("Atrribute table");
		this.layer = layer;
		addWindowListener(new WindowAdapter() {
			@Override 
			public void windowClosing(WindowEvent e) { 
				handleWindowClosingEvent(e);
			} 
		});
		
		if(layer!=null) {
			
			setTableModel();
			
		}
		

		JPanel contentPane;
		
		setAlwaysOnTop(true);
		setBounds(100, 100, 450, 783);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.inactiveCaption);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setBounds(0, 0, 434, 678);
		contentPane.add(scrollPane);
		
		JButton btnButton = new JButton("Close");
		btnButton.setBounds(312, 689, 112, 23);
		contentPane.add(btnButton);
		

		JMenu selectionMenu = new JMenu("Selection");
		selectionMenu.addSeparator();
		
		JMenuItem showAll = new JMenuItem("Show all features");
		JMenuItem showSel = new JMenuItem("Show only selected features");
		JMenuItem delete= new JMenuItem("Deleted selected features");
		JMenuItem clear = new JMenuItem("Clear selection");
		
		showAll.setActionCommand("showAll");
		showSel.setActionCommand("showSel");
		delete.setActionCommand("delete");
		clear.setActionCommand("clear");
		
		showAll.addActionListener(this);
		showSel.addActionListener(this);
		delete.addActionListener(this);
		clear.addActionListener(this);
		
		selectionMenu.add(showAll);
		selectionMenu.add(showSel);
		selectionMenu.add(clear);
		selectionMenu.add(delete);
		menuBar.add(selectionMenu);
		
		btnButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
	}
	
	/**
	 * Sets the table model with all the features of the layer
	 */
	private void setTableModel() {
		
		// Column names
		// FID* : current row number
		// ID :  Feature ID
		// Geometry : Geometry type of the features
		// Layer name of features
		String[] columnNames = { "FID*", "ID", "Geometry", "LayerName", "LayeID"};
		
		// Empty data for now
		Object[][] data = { };
		
		// Make the cell no editatble
		table = new JTable() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -4350617003914999563L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		// Set the default model with the empty data anc column names
		tableModel = new DefaultTableModel(data, columnNames);
		
		// Use the table model in the table
		table.setModel(tableModel);
		
		setColumnPreferedWidth();
		
		// Add features to the table
		addFeaturesToTable();
		
		// Add list selection on the table
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			  
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				handleListSelection(e);
			}
		});
	}
	
	/**
	 * Sets table column prefered widths
	 */
	private void setColumnPreferedWidth() {
		
		// FID
		table.getColumnModel().getColumn(0).setPreferredWidth(5);
		
		// FEATURE ID
		table.getColumnModel().getColumn(1).setPreferredWidth(5);
		
		// LAYER ID
		table.getColumnModel().getColumn(4).setPreferredWidth(5);
	}
	
	/**
	 * Add features to the table
	 */
	private void addFeaturesToTable() {
		
		int count = 0;
		List<Integer> alreadySeletedFeatures = new ArrayList<Integer>();
		for(Feature feature : layer.getListOfFeatures()) {
			
			Object[] fdata = {count, feature.getId(), layer.getLayerType(), layer.getLayerName(), layer.getId() } ;
			tableModel.addRow(fdata);
			
			if(feature.isHighlighted()) {
				alreadySeletedFeatures.add(count);
			}
			
			count++;
		}
		
		// Highlight the rows automatically
		
		if(!alreadySeletedFeatures.isEmpty()) {
			
			for(Integer row : alreadySeletedFeatures) {
				
				table.getSelectionModel().addSelectionInterval(row, row);
			}
		}
	}

	/**
	 * Event when rows are selected on the attibute table <br>
	 * It highlights the features on the drawing panel
	 * @param e
	 */
	protected void handleListSelection(ListSelectionEvent e) {
		
		layer.highlightAllFeatures(false);
		MainFrame.panel.repaint();
			
		int[] rows = table.getSelectedRows();
		
		for(Integer i : rows) {
			
			int fid = (int) (table.getModel().getValueAt(i, FEATURE_ID_COL_INDEX));
			
			layer.getFeatureWithID(fid).setHighlighted(true);
		}
		MainFrame.panel.repaint();
	}

	/**
	 * Used for the items in the menu bar 
	 * @param e
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		
		String command = e.getActionCommand();
		
		if(command.equals("showAll")) {
			
			for(Feature feature : layer.getListOfFeatures()) {
				feature.setVisibile(true);
			}
			
		}
		
		if(command.equals("showSel")) {
			
			for(Feature feature : layer.getListOfFeatures()) {
				
				if(!feature.isHighlighted()) {
					feature.setVisibile(false);
				}
			}
		}
		
		if(command.equals("delete")) {
			
			int rows[] = table.getSelectedRows();
			
			for(@SuppressWarnings("unused") Integer i : rows) {
				
				// Ignoring i because getSelectedRow() returns the index of the first selected row
				// Therefore i will not be found at next iteration
				
				int fid = (int) tableModel.getValueAt(table.getSelectedRow(), FEATURE_ID_COL_INDEX);
				Feature feature = layer.getFeatureWithID(fid);
				layer.getListOfFeatures().remove(feature);
				tableModel.removeRow(table.getSelectedRow());
			}
			
			tableModel.fireTableDataChanged();
			layer.setNotSaved(true);
		}

		if(command.equals("clear")) {
			
			// Clear the panel selection
			MainFrame.panel.cleanUpDrawing();
			
			// Clear table selection
			table.getSelectionModel().clearSelection();
		    table.getColumnModel().getSelectionModel().clearSelection();
			
			// Clear the feature selected
			for(Feature feature : layer.getListOfFeatures()) {
				feature.setVisibile(true);
				feature.setHighlighted(false);
			}
		}
		
		MainFrame.panel.repaint();
	}
	
	/**
	 * Closes the frame
	 * @param e
	 */
	protected void handleWindowClosingEvent(WindowEvent e) {
		
		dispose();
	}
}
