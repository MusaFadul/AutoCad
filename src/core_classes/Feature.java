/**
 * 
 */
package core_classes;

/**
 * @author Isaac
 * Abstract class for storing Features as the same type, so that they may be put into Layers.
 */
public abstract class Feature {

    protected int id;

    /**
     * Constructor for making feature object
     * @param id
     */
    public Feature(int id) {
        this.id = id;
    }

    /**
     * Getter method for the id of this feature.
     * @return int
     */
    public int getId() {
        return this.id;
    }

}
