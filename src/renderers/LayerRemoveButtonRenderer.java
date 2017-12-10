package renderers;

import java.awt.Component;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import application_frames.Settings;

/**
 * Renders the remove icon on the table cell
 * @author OlumideEnoch
 *
 */
public class LayerRemoveButtonRenderer extends JButton implements TableCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LayerRemoveButtonRenderer() {
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		setIcon(Settings.LAYER_DELETE_ICON);
	}
	@Override
	public Component getTableCellRendererComponent(JTable table, Object object, boolean selected, boolean focused, int row,
			int col) {
		setText((object == null) ? "" : object.toString());
		return this;
	}
}

