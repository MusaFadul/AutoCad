/**
 * 
 */
package geometry;

import core_classes.Feature;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * @author Isaac
 * Class for representing polylines in our system.  Inherits from Feature.
 */
public class PolylineItem extends Feature {

    ArrayList<Line2D.Double> geometry;

    /**
     * Constructor for PolylineItem feature.
     * @param id Id for this feature
     * @param polyline Geometry for this feature, in the form of a Line2D object.
     */
    public PolylineItem(int id, ArrayList<Line2D.Double> polyline) {

        super(id);
        this.geometry = polyline;

    }

    /**
     * Getter method for the PolylineItem's geometry object.
     * @return Line2D
     */
    public ArrayList<Line2D.Double> getGeometry() {
        return geometry;
    }

    /**
     * Setter method for the PolylineItem's geometry object.
     * @param polyline Line2D to set as the geometry.
     */
    public void setGeometry(ArrayList<Line2D.Double> polyline) {
        this.geometry = polyline;
    }

    /**
     * Getter method that returns the geometry in a standardized pair of arrays format for storage in the DB.
     * @return double[][] containting an x array and a y array, in that order.
     */
    @Override
    public double[][] getArray() {

        // Initialize the arrays to be the same length as the ArrayList + 1 (a polyline has n+1 vertices where n = number of line segments)
        double[] x = new double[this.geometry.size()+1];
        double[] y = new double[this.geometry.size()+1];

        // Iterate through the array list and get each coordinate pair.
        // Grab the first coordinate of the line only on the first pass.
        // Keep grabbing the second coordinate after that to get all the vertices.
        for (int i=0; i<this.geometry.size(); i++) {
            if (i==1) {
                x[i] = this.geometry.get(i).getX1();
                y[i] = this.geometry.get(i).getY1();
            }
            x[i+1] = this.geometry.get(i).getX2();
            y[i+1] = this.geometry.get(i).getY2();
        }

        // Pack the x and y arrays into one array and return it.
        return new double[][] {x, y};

    }

}
