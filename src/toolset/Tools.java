package toolset;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.ImageIcon;

import tester.MainFrame;

public class Tools {
	
	
	public static boolean isPointOnLine(Line2D line, Point point) {
		
		Point2D start = line.getP1();
		Point2D end = line.getP2();
		
		double d0 = start.distance(end);
		double d1 = start.distance(point);
		double d2 = point.distance(end);
		
		if(d1+d2 == d0) {
			return true;
		}
		return false;
	}

	public static ImageIcon getIconImage(String path) {
		ImageIcon icon = new ImageIcon(MainFrame.class.getResource(path));
		Image img = icon.getImage() ;  
		Image newimg = img.getScaledInstance( 60, 60,  java.awt.Image.SCALE_SMOOTH ) ;  
		icon = new ImageIcon( newimg );
		return icon;
	}
	
	public static ImageIcon getIconImage(String path, int x, int y) {
		ImageIcon icon = new ImageIcon(MainFrame.class.getResource(path));
		Image img = icon.getImage() ;  
		Image newimg = img.getScaledInstance( x, y,  java.awt.Image.SCALE_SMOOTH ) ;  
		icon = new ImageIcon( newimg );
		return icon;
	}

}
