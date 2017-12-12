package application_frames;

import java.awt.Color;
import javax.swing.JFrame;
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
import tester.MainFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
public class AttributeTable extends JFrame implements ActionListener {
	
	private static final long serialVersionUID = 2510826749504059745L;

	protected static final int FEATURE_ID_COL_INDEX = 1;
	
	private JPanel contentPane;
	
	private JTable table = new JTable();
	
	private DefaultTableModel tableModel;
	
	private Layer layer;

	/**
	 * Create the frame.
	 * @param features 
	 */
	public AttributeTable(Layer layer) {
		
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
		
		String[] columnNames = { "FID*", "ID", "Geometry", "LayerName", "LayeID"};
		
		Object[][] data = { };
		
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
		
		tableModel = new DefaultTableModel(data, columnNames);
		table.setModel(tableModel);
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			  
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				layer.highlightAllFeatures(false);
				MainFrame.panel.repaint();
					
				int[] rows = table.getSelectedRows();
				
				for(Integer i : rows) {
					
					int fid = (int) (table.getModel().getValueAt(i, FEATURE_ID_COL_INDEX));
					
					layer.getFeatureWithID(fid).setHighlighted(true);
				}
				MainFrame.panel.repaint();
			}
		});
		
		// FID
		table.getColumnModel().getColumn(0).setPreferredWidth(5);
		
		// ID
		table.getColumnModel().getColumn(1).setPreferredWidth(5);
		
		int count = 0;
		for(Feature feature : layer.getListOfFeatures()) {
			
			Object[] fdata = {count, feature.getId(), layer.getLayerType(), layer.getLayerName(), layer.getId() } ;
			tableModel.addRow(fdata);
			
			count++;
		}
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
