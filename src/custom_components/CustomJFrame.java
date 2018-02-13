package custom_components;

import java.awt.*;

import javax.swing.JFrame;

import toolset.Tools;

/**
 * Class for the creation of a CustomJFrame
 * 
 * @author Olumide Igbiloba
 * @since Dec 7, 2017
 */
public class CustomJFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a CustomJFrame
	 * @throws HeadlessException Throws an HeadlessException
	 */
	public CustomJFrame() throws HeadlessException {
		
		setIconImage(Tools.getIconImage("/images/logo.png").getImage());
	}

	/**
	 * Creates a CustomJFrame
	 * @param arg0 the GraphicsConfiguration arg0 to set
	 */
	public CustomJFrame(GraphicsConfiguration arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a CustomJFrame 
	 * @param arg0 the String arg0 to set
	 * @throws HeadlessException Throws an HeadlessException
	 */
	public CustomJFrame(String arg0) throws HeadlessException {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Creates a CustomJFrame
	 * @param arg0 the String arg0 to set
	 * @param arg1 the GraphicsConfiguration arg1 to set
	 */
	public CustomJFrame(String arg0, GraphicsConfiguration arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}