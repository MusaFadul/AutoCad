package renderers_and_editors;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JColorChooser;
import javax.swing.JTable;
import javax.swing.JTextField;

import core_classes.Layer;
import core_components.GeometryIcon;
import core_components.TableOfContents;

/**
 * Table cell editor for the geometry panel inside the table of contents.<br>
 * Opens color chooser for changing the color of the layer
 * 
 * @author Olumide Igbiloba
 * @since Dec 9, 2017
 * @version 1
 */
public class GeometryPanelEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Panel rendering the geometry type of the current table row
	 */
	private GeometryIcon panel;
	
	
	/**
	 * Creates the GeometryPanel
	 * @param text the text to set
	 */
	public GeometryPanelEditor(JTextField text) {
		super(text);
		panel = new GeometryIcon();
	}
	
	/**
	 * Returns the TableCellEditorComponent
	 * @param table the table to set
	 * @param obj the obj to set
	 * @param isSelected the isSelected to set
	 * @param row the row to set
	 * @param column the column to set
	 * @return the panel
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object obj, boolean isSelected, int row, int column ) {


		int id = (int) table.getModel().getValueAt(row, 4);
		Layer layer = TableOfContents.findLayerWithID(id);
		
		Color color = JColorChooser.showDialog(null, "Set Layer Color",layer.getLayerColor());
		if(color != null) {
			layer.setLayerColor(color);
		}
		panel.setLayer(layer);
		
		return panel;
		
	}
	
	/**
	 * Returns the CellEditorValue
	 * @return the Object getCellEditorValue
	 */
	@Override
	public Object getCellEditorValue() {
	// TODO Auto-generated method stub
		return super.getCellEditorValue();
	}
	
	/**
	 * Returns the stopCellEditing
	 * @return the stopCellEditing
	 */
	@Override
	public boolean stopCellEditing() {
		return super.stopCellEditing();
	}
	
	/**
	 * Returns the fireEditingStopped
	 * @return the fireEditingStopped
	 */
	@Override
	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}

	}