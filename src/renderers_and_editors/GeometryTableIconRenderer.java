package renderers_and_editors;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import core_classes.Layer;
import core_components.GeometryIcon;
import core_components.TableOfContents;

/**
 * Class to render the geometry icon on the table cell
 * 
 * @author Olumide Igbiloba
 * @since Dec 9, 2017
 * @version 1
 *
 */
public class GeometryTableIconRenderer extends GeometryIcon implements TableCellRenderer{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates the GeometryTableIconRenderer
	 */
	public GeometryTableIconRenderer () {
		super();
		super.setBackground(Color.WHITE);
		
	}
	
	/**
	 * Returns the TableCellRendererComponent
	 * @param table the table to set
	 * @param value the value to set
	 * @param arg2 the arg2 to set 
	 * @param arg3 the arg3 to set
	 * @param row the row to set
	 * return the TableCellRendererComponent
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean arg2, boolean arg3, int row,
			int col) {
		
		int id = (int) table.getModel().getValueAt(row, 4);
		Layer layer = TableOfContents.findLayerWithID(id);
		setLayer(layer);
		
		return this;
	}


}
