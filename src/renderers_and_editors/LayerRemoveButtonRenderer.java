package renderers_and_editors;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import application_frames.SettingsFrame;

/**
 * Class which renders the remove icon on the table cell
 * @author OlumideEnoch
 * @since
 * @version 1
 *
 */
public class LayerRemoveButtonRenderer extends JButton implements TableCellRenderer {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates the LayerRemoveButtonRenderer
	 */
	public LayerRemoveButtonRenderer() {
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		setIcon(SettingsFrame.LAYER_DELETE_ICON);
	}
	
	/**
	 * Returns the TableCellRendererComponent
	 * @param table the table to set
	 * @param object the object to set
	 * @param selected the selected to set 
	 * @param focused the focused to set
	 * @param row the row to set
	 * return the TableCellRendererComponent
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object object, boolean selected, boolean focused, int row,
			int col) {
		setText((object == null) ? "" : object.toString());
		return this;
	}
}

