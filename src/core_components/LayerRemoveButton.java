package core_components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import tester.MainFrame;
import application_frames.Settings;

/**
 * Button for removing layer at the table of contents
 * Embedded inside the table cell.
 * @author OlumideEnoch
 *
 */
public class LayerRemoveButton extends DefaultCellEditor {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JButton removeButton;
	private Object currentObject;
	private int currentRow;
	
	public LayerRemoveButton(JTextField text) {
		super(text);
		removeButton = new JButton();
		removeButton.setOpaque(false);
		removeButton.setContentAreaFilled(false);
		removeButton.setBorderPainted(false);
		removeButton.setIcon(Settings.LAYER_DELETE_ICON);
		
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
				TableOfContents.removeRowLayer(currentRow);
				MainFrame.panel.repaint();
			}
		});
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object obj, boolean selected, int row, int col ) {
		currentObject = obj;
		currentRow = row;
		return removeButton;
		
	}
	
	@Override
	public Object getCellEditorValue() {
		return currentObject;
	}
	
	@Override
	public boolean stopCellEditing() {
		return super.stopCellEditing();
	}
	
	@Override
	protected void fireEditingStopped() {
		// TODO Auto-generated method stub
		super.fireEditingStopped();
	}
}

