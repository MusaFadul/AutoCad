package models;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import core_classes.Layer;

/**
 * Class for the creation of a LayerTableModel
 * @author 
 * @since
 * @version 1
 */
public class LayerTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private List<Layer> layers;
	
	/**
	 * Creates the LayerTableModel
	 * @param layers the Layers to set as a List of the type Layer
	 */
	public LayerTableModel(List<Layer> layers) {
		this.layers = layers;
	}
	
	/**
	 * Adds a row to the TableModel of a Layer
	 * @param layer the Layer to set
	 */
	public void addRow(Layer layer) {
		layers.add(layer);
		super.fireTableRowsInserted(layers.size()-1, layers.size()-1);
	}
	
	/**
	 * Fires deleted table rows
	 * @param firstRow the firstRow to set
	 * @param lastRow the lastRow to set
	 */
	@Override
	public void fireTableRowsDeleted(int firstRow, int lastRow) {
		// TODO Auto-generated method stub
		super.fireTableRowsDeleted(firstRow, lastRow);
	}
	
	/**
	 * Returns the number of columns of a LayerTableModel
	 * @return 3 Returns the column number
	 */
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	/**
	 * Returns the number of rows of a LayerTableModel
	 * @return size Returns the size of the Layer
	 */
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return layers.size();
	}

	/**
	 * Returns the value of a Field in a LayerTableModel
	 * @return null Returns the Field value
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Returns the Layer at a specific row of the LayerTableModel
	 * @param row the row to set
	 * @return row the row of the LayerTableModel
	 */
	public Layer getLayerAt(int row) {
		return layers.get(row);
	}

}
