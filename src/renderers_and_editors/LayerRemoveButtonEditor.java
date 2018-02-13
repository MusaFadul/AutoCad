package renderers_and_editors;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import application_frames.MainFrame;
import application_frames.SettingsFrame;
import core_components.TableOfContents;

/**
 * Button for removing layer at the table of contents.
 * Embedded inside the table cell.
 * @author OlumideEnoch
 * @since
 * @version 1
 *
 */
public class LayerRemoveButtonEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;
	protected JButton removeButton;
	private Object currentObject;
	private int currentRow;
	
	/**
	 * Creates the LayerRemoveButton
	 * @param text the text to set
	 */
	public LayerRemoveButtonEditor(JTextField text) {
		super(text);
		removeButton = new JButton();
		removeButton.setOpaque(false);
		removeButton.setContentAreaFilled(false);
		removeButton.setBorderPainted(false);
		removeButton.setIcon(SettingsFrame.LAYER_DELETE_ICON);
		
		removeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fireEditingStopped();
				TableOfContents.removeRowLayer(currentRow);
				MainFrame.panel.repaint();
			}
		});
	}
	
	/**
	 * Returns the TableCellEditorComponent
	 * @param table the table to set
	 * @param obj the object to set
	 * @param selected the selected to set
	 * @param row the row to set
	 * @param col the column to set
	 * @return the removeButton
	 */
	@Override
	public Component getTableCellEditorComponent(JTable table, Object obj, boolean selected, int row, int col ) {
		currentObject = obj;
		currentRow = row;
		return removeButton;
		
	}
	
	/**
	 * Returns the CellEditorValue
	 * @return the currentObject
	 */
	@Override
	public Object getCellEditorValue() {
		return currentObject;
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
		// TODO Auto-generated method stub
		super.fireEditingStopped();
	}
}

