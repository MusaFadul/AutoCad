package core_components;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import core_classes.Layer;
import renderers.LayerRemoveButtonRenderer;
import renderers.GeometryTableIconRenderer;
import tester.MainFrame;

/**
 * 
 * @author OlumideEnoch
 *
 */
public class TableOfContents extends JTable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**Unique layer ID in the table of contents*/
	private static int layerID = 0;
	
	/**The table model that organizes the layer arrangement */
	private static DefaultTableModel tableModel;
	
	/**The index of the layer id at the table model*/
	public static final int LAYER_ID_COL_INDEX = 4;

	/**The list of layers on the table*/
	public static List <Layer> layerList = new ArrayList<Layer>();
	
	/**
	 * Constructs a new table of contents
	 */
	public TableOfContents() {
	
		super();
		setTableModel();
		setTablePreferredSizes();
		setTableRenderers();
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
	public int getNewLayerID() {
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
		
		String message ="";
		
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
			
		} else {
			
			// If not found
			// Change the operation message to fail
			
			message = "System error, cannot find layer on list";
		}
		
		// Log the message at the mainframe
		MainFrame.log(message);
	}
	
	/**
	 * Expose the table model for modification outside the class
	 * @return the table of contents table model 
	 */
	public DefaultTableModel getTableModel() {
		
		return tableModel;
	}
	
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


