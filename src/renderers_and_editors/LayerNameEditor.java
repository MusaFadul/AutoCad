package renderers_and_editors;

import java.awt.Component;
import java.sql.SQLException;

import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;


import application_frames.MainFrame;
import core_classes.Layer;
import core_components.TableOfContents;

/**
 * Class handles the changing of layer names from the table of contents.
 * @author Olumide Igbiloba
 * @since Dec 28, 2017
 * @version 1
 *
 */
public class LayerNameEditor extends DefaultCellEditor implements CellEditorListener  {
	
	/**Current table that is edited*/
	private TableOfContents table;
	
	/**Current row that is edited*/
	private int row;
	
	/**Current column that is edited*/
	private int column;
	
	/**Old value of the cell just edited*/
	private String oldValue = "";

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates the LayerNameEditor
	 * @param textField the textField to set
	 */
	public LayerNameEditor(JTextField textField) {
		super(textField);
		addCellEditorListener(this);
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		// Update parameters
		this.table = (TableOfContents) table;
		this.row = row;
		this.column = column;
		this.oldValue = (String) this.table.getModel().getValueAt(row, column);
		return super.getTableCellEditorComponent(table, value, isSelected, row, column);
	}
	
	@Override
	public void editingCanceled(ChangeEvent e) { }

	@Override
	public void editingStopped(ChangeEvent e) {
		onChangeLayerNameIntent();
	}

	/**
	 * Changes the layer name at the table of contents.<br>
	 * <br>
	 * Process the intent if there is a difference between the old name and the new name and 
	 * if the layer have been stored in the database earlier <br>
	 * The operation is completed at the MainFrame.
	 */
	private void onChangeLayerNameIntent() {
		
		// 0. Get the new layer name 
		String newValue = (String) this.table.getModel().getValueAt(row, TableOfContents.LAYER_NAME_COL_INDEX);
		
		// 1. Check if there is a difference between the old name and the new name, if there us a difference:
		if(!this.oldValue.equals(newValue)) {
			
			boolean changedNameDoesNotExistsInTable = true;
			int count = 0;
			for(String name : TableOfContents.getListOfLayersInString()) {
				if(count != row) {
					if(name.equals(newValue)) {
						changedNameDoesNotExistsInTable = false;
						break;
					}
				}
				count++;
			}
			
			if(changedNameDoesNotExistsInTable) {
			
				// 1.1. Find the layer with ID
				int id = (int) this.table.getModel().getValueAt(row, TableOfContents.LAYER_ID_COL_INDEX);
				Layer layer = TableOfContents.findLayerWithID(id);
				
				// 1.2 Change the layer name
				layer.setLayerName(newValue);
				
				// 1.3 Check if the layer have been stored in the database earlier
				if(layer.isInDatabase() //&& !layer.getListOfFeatures().isEmpty()
						) {
	
						try {
							// 1.4 Check if the changed name does not exist in the db
							boolean changedNameDoesNotExistsInDB = true;
							for(String[] names : MainFrame.dbConnection.getTables()) {
								if(names[0].equals(newValue)) {
									changedNameDoesNotExistsInDB = false;
									break;
								}
							}
							
							// 1.5 If it doesnt exist, rename the layer
							if(changedNameDoesNotExistsInDB) {
								
								MainFrame.renameLayerInDatabase(oldValue, layer);
								
							} 
							// 1.6 If it exist ..
							else {
								// 1.7 Prompt to overwrite
								int response = JOptionPane.showConfirmDialog(null, "Layer with "
										+ "the same name exists in the database"
										+ "\nWould you like to overwrite?", "Name conflict", JOptionPane.ERROR_MESSAGE);
								// 1.8 On Yes option
								if(response == JOptionPane.OK_OPTION) {
									// Overwrite
									MainFrame.renameLayerInDatabase(oldValue, layer);
									
								} 
								// 1.9 On no or cancel option, set name to the old value
								else {
									// On the TOC
									this.table.getModel().setValueAt(oldValue, row, column);
									// And the layer itself
									layer.setLayerName(oldValue);
								}
							}
							
						} catch (SQLException e1) {
							e1.printStackTrace();
							MainFrame.log("An error has occured : " + e1.getMessage());
						}
				}
				// 2.0 If layer is not in the DB
				else {
					MainFrame.log("Layer renamed on the table of contents, not found in the database. Click save to save to the database");
				}
			
			}else {
				// Revert the action
				MainFrame.log("Layer cannot be renamed, name already exist in the table of contents");
				MainFrame.tableOfContents.getModel().setValueAt(oldValue, row, TableOfContents.LAYER_NAME_COL_INDEX);
			}
		}
	}
}
