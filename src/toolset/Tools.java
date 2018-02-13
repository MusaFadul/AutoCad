package toolset;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;

import application_frames.MainFrame;
import core_components.DrawingJPanel;

/**
 * Class which contains miscellaneous utilities used throughout the application.
 * @author Olumide Igbiloba
 * @since Dec 7, 2017
 * @version 1
 */
public class Tools {
	
	public static double maxX, maxY, minX, minY;
	private static Random rand = new Random();
	
	/**
	 * Returns true if the a Point lies on a Line
	 * @param line the line to set
	 * @param point the point to set
	 * @return true if the Point lies on the Line and false if not
	 */
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
	
	public static Color getRandomColor() {
		
		// Java 'Color' class takes 3 floats, from 0 to 1.
		float r = rand.nextFloat();
		float g = rand.nextFloat();
		float b = rand.nextFloat();

		Color randomColor = new Color(r, g, b);
		
		return randomColor;
	}

	/**
	 * Returns the ImageIcon of a particular path
	 * @param path the path to set
	 * @return The ImageIcon is returned
	 */
	public static ImageIcon getIconImage(String path) {
		ImageIcon icon = new ImageIcon(MainFrame.class.getResource(path));
		Image img = icon.getImage() ;  
		Image newimg = img.getScaledInstance( 60, 60,  java.awt.Image.SCALE_SMOOTH ) ;  
		icon = new ImageIcon( newimg );
		return icon;
	}
	
	/**
	 * Returns the ImageIcon of a particular path and scales it
	 * @param path the path to set
	 * @param x the x for scaling to set
	 * @param y the y for scaling to set
	 * @return The ImageIcon is returned
	 */
	public static ImageIcon getIconImage(String path, int x, int y) {
		ImageIcon icon = new ImageIcon(MainFrame.class.getResource(path));
		Image img = icon.getImage() ;  
		Image newimg = img.getScaledInstance( x, y,  java.awt.Image.SCALE_SMOOTH ) ;  
		icon = new ImageIcon( newimg );
		return icon;
	}
	
	/**
	 * Interpolates a new Point of a Line2D using a distance
	 * @param l the line to calculate the new point from to set
	 * @param d the distance to set
	 * @return the new interpolated Point2D
	 */
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
	
	/**
	 * Returns the calculated angle between two Point2D objects
	 * @param from the first (from) point to set
	 * @param target the second (target) point to set
	 * @return the angle between the two specified points
	 */
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

	/**
	 * Returns information whether two Rectangles are spatially overlapping
	 * @param rectTooltip the tooltip rectangle to set
	 * @param rectGuide the guide rectangle to set
	 * @return true if the two rectangles are overlapping
	 */
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

	/**
	 * Returns a doubleArray copied from an integerArray
	 * @param source the integerArray to set
	 * @return the doubleArray
	 */
	public static double[] copyFromIntArray(int[] source) {
		double[] doubleArray = new double[source.length];
		for(int i = 0; i < source.length; i++) {
			doubleArray[i] = source[i];
		}
		return doubleArray;
	}
	
	/**
	 * Removes duplicates in an integerArray
	 * @param s the integerArray to set
	 * @return the resulting Array
	 */
	public static int[] removeDuplicates(int []s){
        int result[] = new int[s.length], j=0;
        for (int i : s) {
            if(!isExists(result, i))
                result[j++] = i;
        }
        return result;
    }
	
	/**
	 * Checks whether a specific value exists in an integerArray
	 * @param array the integerArray to set
	 * @param value the value to set
	 * @return true, if the specified value exists in the integerArray, false if not
	 */
    private static boolean isExists(int[] array, int value){
        for (int i : array) {
            if(i==value)
                return true;
        }
        return false;
    }

	public static Double[] copyFromStringArray(String[] source) {
		Double[] doubleArray = new Double[source.length];
		for(int i = 0; i < source.length; i++) {
			doubleArray[i] = Double.parseDouble(source[i]);
		}
		return doubleArray;
	}
	
	/**
	 * Transforms the World Coordinates to Image Coordinate System
	 * @param list the RPointList to set
	 * @param panel the DrawinPanel to set
	 */
	public static void wcsToImageCoords(List<RPoint> list, DrawingJPanel panel){
		
		Random rand = new Random();
		
		Point2D centroid = findCentroid(list);
		Point2D panelCenter = new Point2D.Double(panel.getWidth()/2, panel.getHeight()/2);
		
		
		for(int i = 0; i < list.size(); i++) {
			
			Point2D wcsPoint = list.get(i).getRealPoint();
			
			double pdev = 0.0;
			double pdevy = 0.0;
			if(centroid.getX() < wcsPoint.getX()) {
				
				// right hand X
				
				// deviation
				double devx = Math.abs(Math.abs(wcsPoint.getX()) - Math.abs(centroid.getX()));
				double maxRelC = maxX - centroid.getX();
				pdev = devx / maxRelC;
				pdev = Math.floor(pdev * 10000) / 10000;
			}
			
			if(centroid.getX() > wcsPoint.getX()) {
				// left hand X
				
				double devx = Math.abs(Math.abs(wcsPoint.getX()) - Math.abs(centroid.getX()));
				double maxRelC = minX - centroid.getX();
				pdev = (devx / maxRelC);
				
				
				pdev = Math.floor(pdev * 10000) / 10000;
	
			}
			
			if(centroid.getY() < wcsPoint.getY()) {
				// right hand X
				
				// deviation
				double devy = Math.abs(Math.abs(wcsPoint.getY()) - Math.abs(centroid.getY()));
				double maxRelC = maxY - centroid.getY();
				pdevy = devy / maxRelC;
				pdevy = Math.floor(pdevy * 10000) / 10000;
			}
			
			if(centroid.getY() > wcsPoint.getY()) {
				// left hand X
				
				double devY = Math.abs(Math.abs(wcsPoint.getY()) - Math.abs(centroid.getY()));
				double maxRelC = minY - centroid.getY();
				pdevy = (devY / maxRelC);
				
				
				pdevy = Math.floor(pdevy * 10000) / 10000;
	
			}
			
			//System.out.println(pdev);
			
			Point2D imageCoord = new Point2D.Double(panelCenter.getX() + (panelCenter.getX() * pdev), panelCenter.getY() + (panelCenter.getY() * pdevy));
			list.get(i).setImagePoint(imageCoord);
			
			//System.out.println(pdev);
			//list.get(i).setImagePoint(imagePoint);
		}
		
		
		
	}
	
	/**
	 * Searches the centroid of a RPoint list
	 * @param list the RPoint list to set
	 * @return the centroid
	 */
	public static Point2D findCentroid(List<RPoint> list) {
		
		double xSum = 0.0;
		double ySum = 0.0;
		
		maxX = list.get(0).getRealPoint().getX();
		maxY = list.get(0).getRealPoint().getY();
		
		minX = list.get(0).getRealPoint().getX();
		minY = list.get(0).getRealPoint().getY();
		
		for(RPoint item : list) {
			
			xSum += item.getRealPoint().getX();
			ySum += item.getRealPoint().getY();
			
			// Max values
			if(maxX < item.getRealPoint().getX()) {
				maxX = item.getRealPoint().getX();
			}
			
			if(maxY < item.getRealPoint().getY()) {
				maxY = item.getRealPoint().getY();
			}
			
			// Min values
			if(minX > item.getRealPoint().getX()) {
				minX = item.getRealPoint().getX();
			}
			
			if(minY > item.getRealPoint().getY()) {
				minY = item.getRealPoint().getY();
			}		
		}
		
		Point2D centroid = new Point2D.Double(xSum/list.size(), ySum/list.size());
		System.out.print(centroid.getX() + " " + centroid.getY());
		return centroid;
	}
	
	/**
	 * Searches for the minimum of a List of the type double
	 * @param list the list to set
	 * @return the minimum
	 */
	public static double findMinOfArray(double[] list) {
		
		double min = 0;
		
		if(list.length > 0) {
			
			for(Double item : list) {
				if(min > item) {
					min = item;
				}
			}
			
			return min;
			
		}
		
		return min;
		
	}
	
	/**
	 * Searches for the maximum of a List of the type double
	 * @param list the list to set
	 * @return the maximum
	 */
	public static double findMaxOfArray(double[] list) {
		
		double max = 0;
		
		if(list.length > 0) {
			
			for(Double item : list) {
				if(max < item) {
					max = item;
				}
			}
			
			return max;
			
		}
		return max;
	}
}
