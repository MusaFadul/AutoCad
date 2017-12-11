package toolset;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
	
	public static Point2D.Double interpolationByDistance(Line2D l, int d) {
		
		Point2D.Double start = new Point2D.Double(l.getX1(), l.getY1());
		Point2D.Double end = new Point2D.Double(l.getX2(), l.getY2());
		
		double length = start.distance(end);
		double ratio = d/ length;
		double x = ratio * start.getX() + (1.0 - ratio) * end.getX();
		double y = ratio * start.getY() + (1.0 - ratio) * end.getY();


		Point2D.Double answerPoint = new Point2D.Double(x,y);
		return answerPoint;

	}
	
	public static float getAngle(Point2D from, Point2D target) {
		
	    float angle = (float) Math.toDegrees(Math.atan2(target.getY() - from.getY(), target.getX() - from.getX()));

	    if(angle < 0){
	        angle += 360;
	    }
	    
	    angle += 90;
	    
	    if(angle > 360) {
	    	angle = angle - 360;
	    }
	    
	    if(angle == 360) {
	    	angle = 0;
	    }
	    
	    return angle ;
	}

	public static boolean isOverlap(Rectangle2D rectTooltip, Rectangle2D rectGuide) {
	
		Point2D topRight = new Point2D.Double(rectGuide.getMaxX(), rectGuide.getMaxX());
		
		if(rectTooltip.contains(topRight)) {
			return false;
		}
		
		Point2D bottomRight = new Point2D.Double(rectGuide.getMaxX(), rectGuide.getMaxY());
		
		if(rectTooltip.contains(bottomRight)) {
			return false;
		}
		
		Point2D bottomLeft = new Point2D.Double(rectGuide.getMinX(), rectGuide.getMaxY());
		
		if(rectTooltip.contains(bottomLeft)) {
			return false;
		}
		
		Point2D topLeft = new Point2D.Double(rectGuide.getMinX(), rectGuide.getMinY());
		
		if(rectTooltip.contains(topLeft)) {
			return false;
		}
	
		return true;
	}

	public static double[] copyFromIntArray(int[] source) {
		double[] doubleArray = new double[source.length];
		for(int i = 0; i < source.length; i++) {
			doubleArray[i] = source[i];
		}
		return doubleArray;
	}
}
