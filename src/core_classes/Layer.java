/**
 * 
 */
package core_classes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import toolset.Settings;

/**
 * 
 * @author OlumideEnoch
 *
 */
public class Layer  {
	
	private Object[] tableData = {};
	
	private List<Feature> listOfFeatures = new ArrayList<Feature>();
	
	private String layerName = "";
	private String layerType = "";
	private Color layerColor = Settings.DEFAULT_LAYER_COLOR;
	private int lineWeight = Settings.DEFAULT_LAYER_LINE_WEIGHT;
	
	private boolean isVisible = true;
	private boolean notSaved = true;
	
	private int id = 0;
	
	/**
	 * @param id
	 * @param layerName
	 * @param isActive
	 * @param layerColor
	 * @param layerType
	 */
	public Layer(int id, boolean isVisible, String layerType,  String layerName) {
		//super(id);
		this.id = id;
		this.layerName = layerName;
		this.isVisible = isVisible;
		this.layerType = layerType;
		this.tableData = new Object[] {isVisible, layerType, layerName, "", id};
		
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the tableData
	 */
	public Object[] getTableData() {
		return tableData;
	}

	/**
	 * @return the layerName
	 */
	public String getLayerName() {
		return layerName;
	}

	/**
	 * @param layerName the layerName to set
	 */
	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	/**
	 * @return the isActive
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * @return the layerColor
	 */
	public Color getLayerColor() {
		return layerColor;
	}

	/**
	 * @param layerColor the layerColor to set
	 */
	public void setLayerColor(Color layerColor) {
		this.layerColor = layerColor;
	}

	/**
	 * @return the layerType
	 */
	public String getLayerType() {
		return layerType;
	}

	/**
	 * @param layerType the layerType to set
	 */
	public void setLayerType(String layerType) {
		this.layerType = layerType;
	}

	/**
	 * @return the notSaved
	 */
	public boolean isNotSaved() {
		return notSaved;
	}

	/**
	 * @param notSaved the notSaved to set
	 */
	public void setNotSaved(boolean notSaved) {
		this.notSaved = notSaved;
	}

	/**
	 * @return the listOfFeatures
	 */
	public List<Feature> getListOfFeatures() {
		return listOfFeatures;
	}

	/**
	 * @param listOfFeatures the listOfFeatures to set
	 */
	public void setListOfFeatures(List<Feature> listOfFeatures) {
		this.listOfFeatures = listOfFeatures;
	}

	public int getNextID() {

		return this.listOfFeatures.size() + 1;
	}

	/**
	 * @return the lineWeight
	 */
	public int getLineWeight() {
		return lineWeight;
	}

	/**
	 * @param lineWeight the lineWeight to set
	 */
	public void setLineWeight(int lineWeight) {
		this.lineWeight = lineWeight;
	}
	
}
