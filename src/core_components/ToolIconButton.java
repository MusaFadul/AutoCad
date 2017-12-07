package core_components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import toolset.Settings;
import toolset.Tools;

/**
 * Blue print for the tool buttons
 * Uses background image and can change background dynamically
 * @author OlumideEnoch
 *
 */
public class ToolIconButton extends JButton {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1387944835690709531L;
	
	private boolean buttonReleased = false;

	public ToolIconButton(String text, String iconPath, int x, int y) {
		
		super(text);
		setBackground(Settings.DEFAULT_BUTTON_COLOR);
		setBorderPainted(false);
		setFocusPainted(false);
		setIcon(Tools.getIconImage(iconPath, x,y));
		setText(null);
		
		
		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(buttonReleased) {
					buttonReleased = false;
				} else
					buttonReleased = true;
			}
			
		});
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseEntered(e);
				setBackground(Settings.DEFAULT_STATE_COLOR);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseExited(e);
				if(!buttonReleased) {
					setBackground(Settings.DEFAULT_BUTTON_COLOR);
				}
			}
		});
		
	}

	/**
	 * @return the buttonReleased
	 */
	public boolean isButtonReleased() {
		return buttonReleased;
	}

	/**
	 * @param buttonReleased the buttonReleased to set
	 */
	public void setButtonReleased(boolean buttonReleased) {
		this.buttonReleased = buttonReleased;
	}
}
