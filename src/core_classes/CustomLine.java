package core_classes;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Created by isaac on 27/11/17.
 */
public class CustomLine extends Feature {

    private ArrayList<Point> geometry;

    /**
     * Constructor for a CustomLine object.  Geometry is stored as an ArrayList of native Point objects alongside its
     * parent's properties (id, color, weight).
     * @return null
     */
    public CustomLine(ArrayList<Point> line, int id, Color color, int weight) {
        super.Feature(id, color, weight);
        this.geometry = line;
    }

    /**
     * Getter method for the Line's geometry.  Returns an ArrayList of Points.
     * @return
     */
    public ArrayList<Point> getGeometry() {
        return this.geometry;
    }

}
