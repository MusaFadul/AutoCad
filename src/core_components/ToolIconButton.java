package core_components;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

import application_frames.MainFrame;
import application_frames.SettingsFrame;
import toolset.Tools;

/**
 * Blue print for the tool buttons<br>
 * Uses background image and can change background dynamically<br>
 * Responds to mouse over and mouse exited by changing the background of the button
 * 
 * @author Olumide Igbiloba
 * @since Dec 7, 2017
 * 
 */
public class ToolIconButton extends JButton {
	
	private static final long serialVersionUID = 1387944835690709531L;
	
	private boolean buttonReleased = false;

	/**
	 * Creates the ToolIconButton
	 * @param text the text to set
	 * @param iconPath the iconPath to set
	 * @param x the x coordinate to set
	 * @param y the y coordinate to set
	 */
	public ToolIconButton(String text, String iconPath, int x, int y) {
		
		super();
		setBackground(SettingsFrame.DEFAULT_STATE_COLOR);
		setForeground(Color.WHITE);
		setBorderPainted(false);
		setFocusPainted(false);
		setActionCommand(text);
		setIcon(Tools.getIconImage(iconPath, x,y));
		setText(null);
		
		MainFrame.buttonsList.add(this);
		
		addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (getActionCommand().equals("Editing")|| 
					getActionCommand().equals("Query")  || 
					getActionCommand().equals("Select") ||
					getActionCommand().equals("Snap") ||
					getActionCommand().equals("Grid") ||
					getActionCommand().equals("Ortho")){
					
					if (buttonReleased) {
						buttonReleased = false;
					} else {
						buttonReleased = true;
					} 
				}
			}
		});
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				setBackground(SettingsFrame.HIGHLIGHTED_STATE_COLOR);
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mouseExited(e);
				if(!buttonReleased) {
					setBackground(SettingsFrame.DEFAULT_STATE_COLOR);
				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(!buttonReleased) {
					setBackground(SettingsFrame.DEFAULT_STATE_COLOR);
				} else
					setBackground(SettingsFrame.HIGHLIGHTED_STATE_COLOR);
			}
		});
		
	}
	
	/**
	 * Returns whether the ToolIconButton is released or not
	 * @return the buttonReleased
	 */
	public boolean isButtonReleased() {
		return buttonReleased;
	}

	/**
	 * Sets the buttonReleased status of the ToolIconButton
	 * @param buttonReleased the buttonReleased to set
	 */
	public void setButtonReleased(boolean buttonReleased) {
		this.buttonReleased = buttonReleased;
	}
}