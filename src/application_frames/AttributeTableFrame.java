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
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

/**
 * Shows attribute table of all Features in a Layer<br>
 * <br>
 * It is realized with a JTable. <br>
 * Each row in the table represents a feature in the layer<br>
 * <br>
 * "FID*", "ID", "Geometry", "LayerName", "LayeID"<br>
 * <br>
 * Features can also be selected and it can be highlighted directly on the drawing panel
 * by setting the highlight status to true.<br>
 * These selected features are retrieved from the layer using the feature ID<br>
 * If a feature was selected at the drawing panel before the attribute table of the layer is opened
 * the rows corresponding to the feature are automatically highlighted.<br>
 * <br>
 * Also have support for deleting the selected feature.<br>
 * 
 * @author Olumide Igbiloba
 * @since Dec 10, 2017
 * @version 1
 * 
 *
 */
public class AttributeTableFrame extends CustomJFrame implements ActionListener {
	
	private static final long serialVersionUID = 2510826749504059745L;
	
	/**Feature ID column index on the attribute table*/
	protected static final int FEATURE_ID_COL_INDEX = 1;

	/**Current table of the attribute table frame*/
	private JTable table = new JTable();
	
	/**Current table model of the attribute table frame*/
	private DefaultTableModel tableModel;
	
	/**Current layer of the attribute table*/
	private Layer layer;
	
	private JMenu selectionMenu;
	

	/**
	 * Creates the Attribute Table Frame
	 * @param layer The Layer for which an Attribute Table should be created
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
			
			setTableModel(layer);
			
		}
		
		JPanel contentPane;
		
		setAlwaysOnTop(true);
		setBounds(SettingsFrame.window.getBounds().x + MainFrame.panel.getBounds().x, 100, 300, 783);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.inactiveCaption);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBackground(Color.WHITE);
		
		JButton btnButton = new JButton("Close");
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnButton, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE))
				.addComponent(scrollPane, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 679, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnButton))
		);
		contentPane.setLayout(gl_contentPane);
		

		selectionMenu = new JMenu("Selection");
		selectionMenu.addSeparator();
		
		JMenuItem showAll = new JMenuItem("Show all features");
		JMenuItem showSel = new JMenuItem("Show only selected features");
		JMenuItem delete= new JMenuItem("Delete selected features");
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
	 * @param layer The Layer the table model should be set for
	 */
	private void setTableModel(Layer layer) {
		
		// Column names
		// FID* : current row number
		// ID :  Feature ID
		// Geometry : Geometry type of the features
		// Layer name of features
		String[] columnNames = { "FID*", "ID", "Geometry", "LayerName", "LayeID"};
		
		// Empty data for now
		Object[][] data = { };
		
		// Make the cell no editable
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
		addFeaturesToTable(layer);
		
		// Add list selection on the table
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			  
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				handleListSelection(e);
			}
		});
	}
	
	/**
	 * Sets table column preferred widths
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
	 * Add features to the table<br>
	 * Highlights the corresponding row of already selected feature in the layer
	 * @param layer the layer to show its features on the attribute table
	 */
	private void addFeaturesToTable(Layer layer) {
		
		// 0. Use count for to populate the FID column
		int count = 0;
		// 1. Compile the list of already selected features
		List<Integer> alreadySeletedFeatures = new ArrayList<Integer>();
		
		// 2. Loop through the feature in the layer
		for(Feature feature : layer.getListOfFeatures()) {
			
			// 2.1 Constructs the table data
			Object[] fdata = {count, feature.getId(), layer.getLayerType(), layer.getLayerName(), layer.getId() } ;
			tableModel.addRow(fdata);
			
			// 2.2 Compile the list of already selected features
			if(feature.isHighlighted()) {
				alreadySeletedFeatures.add(count);
			}
			
			// 2.3 Increase the row number
			count++;
		}
		
		// 2.4 Highlight the rows automatically
		
		if(!alreadySeletedFeatures.isEmpty()) {
			
			for(Integer row : alreadySeletedFeatures) {
				
				table.getSelectionModel().addSelectionInterval(row, row);
			}
		}
	}

	/**
	 * Event when rows are selected on the attribute table <br>
	 * Highlights the Features on the drawing panel
	 * @param e the ListSelectionEvent to set
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
	 * @param e the ActionEvent to set
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
			
			deleteSelectedFeatures();
		}

		if(command.equals("clear")) {
			
			clearSelectedFeatures();
		}
		
		MainFrame.panel.repaint();
	}
	
	/**
	 * Deletes selected features 
	 */
	private void deleteSelectedFeatures() {
		
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
		
		MainFrame.panel.repaint();
		
	}

	/**
	 * Clears selected features 
	 */
	private void clearSelectedFeatures() {
		
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
		
		MainFrame.panel.repaint();
	}

	/**
	 * Closes the frame
	 * @param e the WindowEvent to set
	 */
	protected void handleWindowClosingEvent(WindowEvent e) {
		
		dispose();
	}
}
