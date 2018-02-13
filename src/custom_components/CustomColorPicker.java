/**
 * 
 */
package custom_components;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import application_frames.MainFrame;

/**
 * Class for the creation of a CustomJPanel
 * 
 * @author Olumide Igbiloba
 * @since Dec 21, 2017
 *
 */
public class CustomColorPicker extends JPanel  {

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a CustomJPanel s
	 */
	public CustomColorPicker() {
		super();
		setBorder(new LineBorder(Color.GRAY));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				Color color = JColorChooser.showDialog(null, "Change color", getBackground());
				if(color != null) {
					setBackground(color);
				}
				
				if(MainFrame.panel != null) {
					MainFrame.panel.repaint();
				}
			}
		});
	}
}
