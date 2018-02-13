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
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import application_frames.AttributeTableFrame;
import application_frames.MainFrame;
import core_classes.Layer;
import renderers_and_editors.GeometryPanelEditor;
import renderers_and_editors.GeometryTableIconRenderer;
import renderers_and_editors.LayerNameEditor;
import renderers_and_editors.LayerRemoveButtonEditor;
import renderers_and_editors.LayerRemoveButtonRenderer;

/**
 * Arranges the list of current layers <br>
 * It can: <br>
 * Turn off and on layer <br>
 * Remove layer and <br>
 * Change the color of a layer.
 * 
 * @author Olumide Igbiloba
 * @since Dec 7, 2017
 * @version
 * a. Dec 28, 2017 : Added functionality for changing layer name
 * b. Dec 28, 2017 : Validate adding layer with same name on the table of contents
 *
 */
public class TableOfContents extends JTable {
	
	private static final long serialVersionUID = 1L;
	
	/**Unique layer ID in the table of contents*/
	private static int layerID = 0;
	
	/**The table model that organizes the layer arrangement */
	public static DefaultTableModel tableModel;
	
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
	 * Handles the Layer Visibility from a Click
	 * @param e the TableModelEvent to set
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
        getColumnModel().getColumn(1).setCellEditor(new GeometryPanelEditor(new JTextField()));
        
        getColumnModel().getColumn(2).setCellEditor(new LayerNameEditor(new JTextField()));
        
        // Attach a remove button to the 4th column
        getColumnModel().getColumn(3).setCellRenderer(new LayerRemoveButtonRenderer());
        getColumnModel().getColumn(3).setCellEditor(new LayerRemoveButtonEditor(new JTextField()));
        
        
	}
	
	/**
	 * Change the size of the table columns
	 */
	public void setTablePreferredSizes() {
		
		// On/off column
		getColumnModel().getColumn(0).setPreferredWidth(40);
		// Name column
		getColumnModel().getColumn(2).setPreferredWidth(400);
		
		// General row height
        setRowHeight(50);
        
     // Make the 5th column invisible
        // This contains the layer id of the layer initial row
        getColumnModel().getColumn(4).setMinWidth(0);
        getColumnModel().getColumn(4).setMaxWidth(0);
		
	}
	
	/**
	 * Initializes the model of the table. <br>
	 * The column names are later turned off.
	 */
	private void setTableModel() {
		
		String[] columnNames = { "ON/OFF", "Type", "Name", "Remove Icon", "Layer Internal ID"};
		
		Object[][] data = { };
		
		tableModel = new DefaultTableModel(data, columnNames);
		
        setModel(tableModel);
	}
	
	/**
	 * Sets the class of the table columns.
	 * @param columnIndex the columnIndex to set
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
	        	classType = GeometryPanelEditor.class;
	        	break;
	        	
	       // Skip cell 2!, use default class
	        
	        // Remove button
	        case 3:
	        	classType = LayerRemoveButtonEditor.class;
	        	break;
	        	
	       // Layer ID (hidden) : width set to 0
	        case 4:
	        	classType = Integer.class;
	        
	    }
	    return classType;
	}
	
	/**
	 * Add a new layer to table of contents
	 * @param layer the layer to set
	 * @return true if operation successful
	 */
	public boolean addRowLayer(Layer layer) {
		
		// Validate layer
		
		if(validateLayer(layer)) {
		
			// Add to the layer list
			layerList.add(layer);
			
			// Add to the table 
			tableModel.addRow(layer.getTableData());
			
			// Increase the layer id
			layerID++;
			
			// Update the combo box model
			MainFrame.updateLayerComboBoxModel( getListOfLayersInString() );
			
			return true;
			
		} else {
			MainFrame.log("Cannot add layer, layer with same name exists");
			JOptionPane.showMessageDialog(null, "Cannot add layer, layer with same name exists", "Error adding new layer", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	/**
	 * Validates adding a new layer
	 * @param layer the layer to set
	 * @return false is layer name already exist
	 */
	public boolean validateLayer(Layer layer) {

		for(String existingLayerNames : getListOfLayersInString()) {
			if(existingLayerNames.equals(layer.getLayerName())) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Returns a new layer ID
	 * @return new layer id
	 */
	public static int getNewLayerID() {
		return layerID;
	}
	
	/**
	 * Retrieves a layer in the table of content with a particular ID.
	 * @param id ID of the layer (should be retrieved at the 4th column of the table row)
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
	 * Removes a layer from table of contents.
	 * @param row specified row on the table
	 */
	public static void removeRowLayer(int row) {
		
		// 0. Message to display at the end of the operation
		String message = "";
		
		// 1. Get the ID of the layer with the layer id column index
		int id = (int) tableModel.getValueAt(row, LAYER_ID_COL_INDEX);
		
		// 2. Find the layer with the ID
		Layer layer = findLayerWithID(id);
		
		// 3. If a layer was found (it should be found!, unless the ID index is wrong!)
		if(layer != null) {
			
			// 3.1 Remove layer from the list
			layerList.remove(layer);
			
			// 3.2 Refresh the table row by removing the layer
			tableModel.removeRow(row);
			
			// 3.3 Update the combo box model
			MainFrame.updateLayerComboBoxModel( getListOfLayersInString() );
			
			// 3.4 Change the operation message to success
			message = layer.getLayerName() + " removed";
			
			// 3.5 Disable edit mode if there is no more items on the table of contents
			if(tableModel.getRowCount() == 0) {
				
				MainFrame.panel.abandonEditSession();
				MainFrame.updateDrawButtonGroup();
			}
		
		// 4. If not found
		} else {
			
			// 4.1 Change the operation message to fail
			
			message = "System error, cannot find layer on list";
		}
		
		// 5. Log the message at the mainframe
		MainFrame.log(message);
	}
	
	
	/**
	 * Gets the list of layer names in the table of contents as an array list of string.<p>
	 * The list is created as needed from the available list of layers.
	 * @return string array of the available layer names
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
