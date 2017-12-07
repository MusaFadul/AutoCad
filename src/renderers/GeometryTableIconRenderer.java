package renderers;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import core_classes.Layer;
import core_components.GeometryTableIcon;
import core_components.TableOfContents;

/**
 * Renders the geometry icon on the table cell
 * @author OlumideEnoch
 *
 */
public class GeometryTableIconRenderer extends GeometryTableIcon implements TableCellRenderer{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public GeometryTableIconRenderer () {
		super();
		super.setBackground(Color.WHITE);
		
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int row,
			int col) {
		
		int id = (int) table.getModel().getValueAt(row, 4);
		Layer layer = TableOfContents.findLayerWithID(id);
		setLayer(layer);
		
		return this;
	}


}
