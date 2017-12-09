package tester;

import geometry.PointItem;
import core_classes.Feature;
import core_classes.Layer;
import geometry.PolylineItem;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

/**
 * Created by isaac on 05/12/17.
 */
public class TestLayers {

    public static void main(String[] args) {

        Layer myLayer = new Layer(new PointItem(0, new Point(0,0)), "Layer0");

        PointItem myPoint0 = new PointItem(myLayer.getNextId(), new Point(0,0));
        myLayer.addFeature(myPoint0);
        PointItem myPoint1 = new PointItem(myLayer.getNextId(), new Point(0,0));
        myLayer.addFeature(myPoint1);
        PointItem myPoint2 = new PointItem(myLayer.getNextId(), new Point(0,0));
        myLayer.addFeature(myPoint2);
        PointItem myPoint3 = new PointItem(myLayer.getNextId(), new Point(0,0));
        myLayer.addFeature(myPoint3);
        PointItem myPoint4 = new PointItem(myLayer.getNextId(), new Point(0,0));
        myLayer.addFeature(myPoint4);


        ArrayList<Line2D.Double> myArrayList = new ArrayList<Line2D.Double>();

        Line2D.Double myLine2D = new Line2D.Double(0,0,1,1);
        myArrayList.add(myLine2D);
        Line2D.Double myLine2D_2 = new Line2D.Double(1,1,2,3);
        myArrayList.add(myLine2D_2);
        Line2D.Double myLine2D_3 = new Line2D.Double(2,3,5,5);
        myArrayList.add(myLine2D_3);
        Line2D.Double myLine2D_4 = new Line2D.Double(5,5,5,6);
        myArrayList.add(myLine2D_4);
        PolylineItem myPolyline = new PolylineItem(2, myArrayList);

        //myLayer.addFeature(myPolyline);

        ArrayList<Feature> myLayerFeatureList;

        myLayerFeatureList = myLayer.getFeatureList();
        for (int i=0; i<myLayerFeatureList.size(); i++) {
            System.out.println(myLayerFeatureList.get(i).getId());
        }

        System.out.println();
        myLayer.removeFeature(2);

        myLayerFeatureList = myLayer.getFeatureList();
        for (int i=0; i<myLayerFeatureList.size(); i++) {
            System.out.println(myLayerFeatureList.get(i).getId());
        }

    }

}
