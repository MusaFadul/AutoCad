package tester;

import java.io.IOException;

import core_classes.Layer;
import file_handling.FileHandler;

public class CSVTester {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Layer layer = new Layer(0, false, "", "");
		FileHandler.readFromGeoJson("WGS84", layer);
	}

}
