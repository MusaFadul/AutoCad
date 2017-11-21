/**
 * 
 */
package tester;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import custom_components.CustomJFrame;
import toolset.Settings;



/**
 * @author OlumideEnoch
 *
 */
public class MainFrame  {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private CustomJFrame frame;
	
	public MainFrame() {
		initialize();
	}

	private void initialize() {
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame = new CustomJFrame(Settings.TITLE);
		frame.setBounds(0,0,screenSize.width,screenSize.height);
		frame.getContentPane().setLayout(null);
	}

}
