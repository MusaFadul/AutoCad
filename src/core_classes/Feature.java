/**
 * 
 */
package core_classes;
import java.awt.Color;

/**
 * @author OlumideEnoch
 *
 */
public abstract class Feature {

    private int id;
    private Color color;
    private int weight;

    /**
     * Abstract constructor for Feature object.  Requires an id, a color and a weight.
     * @return
     */
    public void Feature(int id, Color color, int weight) {
        this.id = id;
        this.color = color;
        this.weight = weight;
    }

    /**
     * Getter method for the id of this feature.
     * @return int
     */
    public int getId() {
        return this.id;
    }

    /**
     * Getter method for the color of this feature.
     * @return Color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Getter method for the weight of this feature.
     * @return int
     */
    public int getWeight() {
        return this.weight;
    }

}
