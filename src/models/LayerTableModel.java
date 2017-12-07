package models;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import core_classes.Layer;

public class LayerTableModel extends AbstractTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Layer> layers;
	
	public LayerTableModel(List<Layer> layers) {
		this.layers = layers;
	}
	
	public void addRow(Layer layer) {
		layers.add(layer);
		super.fireTableRowsInserted(layers.size()-1, layers.size()-1);
	}
	
	@Override
	public void fireTableRowsDeleted(int firstRow, int lastRow) {
		// TODO Auto-generated method stub
		super.fireTableRowsDeleted(firstRow, lastRow);
	}
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 3;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return layers.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Layer getLayerAt(int row) {
		return layers.get(row);
	}

}
