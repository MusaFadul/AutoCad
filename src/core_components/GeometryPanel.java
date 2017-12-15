package core_components;

import java.awt.Color;
import java.awt.Component;
import java.util.Random;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import core_classes.Layer;

public class GeometryPanel extends DefaultCellEditor {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JButton btn;
	protected GeometryTableIcon panel;
	private Layer layer;
	
	final Random r= new Random();
	
	public GeometryPanel(JTextField text) {
		super(text);
		panel = new GeometryTableIcon();
	}
	
	// Override some default methods
	@Override
	public Component getTableCellEditorComponent(JTable table, Object obj, boolean isSelected, int row, int column ) {


		int id = (int) table.getModel().getValueAt(row, 4);
		layer = TableOfContents.findLayerWithID(id);

		layer.setLayerColor(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
		panel.setLayer(layer);
		
		return panel;
		
	}
	
	@Override
	public Object getCellEditorValue() {
	// TODO Auto-generated method stub
		return super.getCellEditorValue();
	}
	
	@Override
	public boolean stopCellEditing() {
		return super.stopCellEditing();
	}
	
	@Override
	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}

	}