package core_components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import application_frames.AttributeTableFrame;
import application_frames.MainFrame;
import core_classes.Layer;
import renderers.LayerRemoveButtonRenderer;
import renderers.GeometryTableIconRenderer;

/**
 * Arranges the list of current layers <br>
 * It can: <br>
 * Turn off and on layer <br>
 * Remove layer and <br>
 * Change the color of a layer.
 * 
 * TODO Conclude the listener at the first column (visibility)
 * @author OlumideEnoch
 *
 */
public class TableOfContents extends JTable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**Unique layer ID in the table of contents*/
	private static int layerID = 0;
	
	/**The table model that organizes the layer arrangement */
	private static DefaultTableModel tableModel;
	
	/**The index of the layer id at the table model*/
	public static final int LAYER_VISIBILTY_COL_INDEX = 0;
	
	/**The index of the layer id at the table model*/
	public static final int LAYER_NAME_COL_INDEX = 2;
	
	/**The index of the layer id at the table model*/
	public static final int LAYER_ID_COL_INDEX = 4;
	


	/**The list of layers on the table*/
	public static List <Layer> layerList = new ArrayList<Layer>();
	
	public static JPopupMenu menu = new JPopupMenu();
	
	/**
	 * Constructs a new table of contents
	 */
	public TableOfContents() {
	
		super();
		setTableModel();
		setTablePreferredSizes();
		setTableRenderers();
		setPopUpMenu();
		
		getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				handleTableChangedListener(e);
			}
			
		});
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				JTable source = (JTable) e.getSource();
				int row = source.rowAtPoint(e.getPoint());
				int col = source.columnAtPoint(e.getPoint());
				
				if(! source.isRowSelected(row)) {
					source.changeSelection(row, col, false, false);
				}
			}
		});
		
		setComponentPopupMenu(menu);
	}
	
	protected void handleTableChangedListener(TableModelEvent e) {
		
		if(e.getColumn() == LAYER_VISIBILTY_COL_INDEX) {
			handleLayerVisibiltyFromClick(e);
		}
		if(e.getColumn() == LAYER_NAME_COL_INDEX) {
			//
			// Rename layer
		}
	}
	
	/**
	 * Attaches popup menu to the table
	 */
	public void setPopUpMenu() {
		
		
		menu = new JPopupMenu();
		
		JMenuItem attributeTable  = new JMenuItem ("Open attribute table");
		
		attributeTable.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Component c = (Component) e.getSource();
				JPopupMenu popup = (JPopupMenu) c.getParent();
				JTable table = (JTable) popup.getInvoker();
				int layerID = (int) (table.getValueAt(table.getSelectedRow(), LAYER_ID_COL_INDEX));
				
				Layer layer = findLayerWithID(layerID);
				
				System.out.println(layer.getLayerType() + " " + layer.getLayerName());
				
				new AttributeTableFrame(layer).setVisible(true);
			
			}
		});
		
		menu.add(attributeTable);
		
		menu.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent arg0) {
				menu.setVisible(false);
			}
			
			@Override
			public void focusGained(FocusEvent arg0) {;
			}
		});
	}
	
	/**
	 * 
	 * @param e
	 */
	protected void handleLayerVisibiltyFromClick(TableModelEvent e) {
		
		int layerID = (int) getModel().getValueAt(e.getFirstRow(), LAYER_ID_COL_INDEX);

		boolean layerVisibilityState = (boolean) getModel().getValueAt(e.getFirstRow(), 0);
		Layer layer = findLayerWithID(layerID);
		
		layer.setVisible(layerVisibilityState);
		MainFrame.panel.repaint();
	}

	/**
	 * Set general render styles for the table and for specific columns
	 */
	private void setTableRenderers() {
	      
        // General render styles
        setTableHeader(null);
        setShowVerticalLines(false);
        setShowHorizontalLines(false);
        
    	// Change the renderer of the second column to display shape type
		getColumnModel().getColumn(1).setCellRenderer(new GeometryTableIconRenderer());
        getColumnModel().getColumn(1).setCellEditor(new GeometryPanel(new JTextField()));
        
        // Attach a remove button to the 4th column
        getColumnModel().getColumn(3).setCellRenderer(new LayerRemoveButtonRenderer());
        getColumnModel().getColumn(3).setCellEditor(new LayerRemoveButton(new JTextField()));
        
        // Make the 5th column invisible
        // This contains the layer id of the layer initial row
        getColumnModel().getColumn(4).setMinWidth(0);
        getColumnModel().getColumn(4).setMaxWidth(0);
	}
	
	/**
	 * Change the size of the table columns
	 */
	private void setTablePreferredSizes() {
		
		// On/off column
		getColumnModel().getColumn(0).setPreferredWidth(40);
		// Name column
		getColumnModel().getColumn(2).setPreferredWidth(400);
		
		// General row height
        setRowHeight(50);
		
	}
	
	/**
	 * Initializes the model of the table <br>
	 * The column names are later turned off.
	 */
	private void setTableModel() {
		
		String[] columnNames = { "ON/OFF", "Type", "Name", "Remove Icon", "Layer Internal ID"};
		
		Object[][] data = { };
		
		tableModel = new DefaultTableModel(data, columnNames);
		
        setModel(tableModel);
	}
	
	/**
	 * Sets the class of the table columns
	 * 
	 * @param columnIndex 
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
	    Class<?> classType = String.class;
	    switch (columnIndex) {
	        
	    	// On/off button
	    	case 0:
	            classType = Boolean.class;
	            break;
	            
	        // Showing geometry of the layer
	        case 1: 
	        	classType = GeometryPanel.class;
	        	break;
	        	
	        // Layer name
	        case 2:
	        	classType = String.class;
	        
	        // Remove button
	        case 3:
	        	classType = LayerRemoveButton.class;
	        	break;
	        	
	       // Layer ID (hidden) : width set to 0
	        case 4:
	        	classType = Integer.class;
	        
	    }
	    return classType;
	}
	
	/**
	 * Add a new layer to table of contents
	 * @param layer layer to add
	 */
	public void addRowLayer(Layer layer) {
		
		// Add to the layer list
		layerList.add(layer);
		
		// Add to the table 
		tableModel.addRow(layer.getTableData());
		
		// Increase the layer id
		layerID++;
		
		// Update the combo box model
		MainFrame.updateLayerComboBoxModel( getListOfLayersInString() );
	}
	
	/**
	 * Returns an new layer id
	 * @return new layer id
	 */
	public static int getNewLayerID() {
		return layerID;
	}
	
	/**
	 * Retrieved a layer in the table of content with a particular id
	 * @param id id of the layer (should be retrieved at the 4th column of the table row)
	 * @return layer 
	 */
	public static Layer findLayerWithID(int id) {
		
		for(Layer layer : layerList) {
			if(layer.getId() == id) {
				return layer;
			}
		}
		return null;
	}
	
	/**
	 * Removes a layer from table of contents
	 * @param row specified row on the table
	 */
	public static void removeRowLayer(int row) {
		
		String message = "";
		
		// Get the ID of the layer with the layer id column index
		int id = (int) tableModel.getValueAt(row, LAYER_ID_COL_INDEX);
		
		// Find the layer with the ID
		Layer layer = findLayerWithID(id);
		
		// If a layer was found (it should be found!, unless the ID index is wrong!)
		if(layer != null) {
			
			// Remove layer from the list
			layerList.remove(layer);
			
			// Refresh the table row by removing the layer
			tableModel.removeRow(row);
			
			// Update the combo box model
			MainFrame.updateLayerComboBoxModel( getListOfLayersInString() );
			
			// Change the operation message to success
			message = layer.getLayerName() + " removed";
			
			// Disable edit mode
			if(tableModel.getRowCount() == 0) {
				
				MainFrame.panel.abandonEditSession();
				MainFrame.updateDrawButtonGroup();
			}
			
		} else {
			
			// If not found
			// Change the operation message to fail
			
			message = "System error, cannot find layer on list";
		}
		
		// Log the message at the mainframe
		MainFrame.log(message);
	}
	
	
	/**
	 * Gets the list of layer names in the table of contents as an array list of string.<p>
	 * The list is created as needed from the available list of layers
	 * @return string array of the avaliable layer names
	 */
	public static String[] getListOfLayersInString() {
		
		String[] layerNames = new String[layerList.size()];
		int index = 0;
		for(Layer layer : layerList) {
			layerNames[index] = layer.getLayerName();
			index++;
		}
		
		return layerNames;
	}
}
