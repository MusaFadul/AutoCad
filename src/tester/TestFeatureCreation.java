package tester;
import geometry.PointItem;
import geometry.PolylineItem;
import geometry.PolygonItem;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.Polygon;
import java.util.ArrayList;

/**
 * Created by isaac on 04/12/17.
 */
public class TestFeatureCreation {

    public static void main(String[] args) {

        // ===== POINT TEST =====

        PointItem myPoint = new PointItem(1, new Point(0,0));
        System.out.println("point id: \n\t" + myPoint.getId());
        System.out.println("point geom: \n\t" + myPoint.getGeometry().toString());
        System.out.println("point geom (array format):");
        double[][] myPointArray = myPoint.getArray();
        for (int i=0; i<myPointArray[0].length; i++) {
            System.out.print("\tx array: [");
            System.out.print(myPointArray[0][i] + ", ");
        }
        System.out.println("]");
        for (int i=0; i<myPointArray[1].length; i++) {
            System.out.print("\ty array: [");
            System.out.print(myPointArray[1][i] + ", ");
        }
        System.out.println("]\n\n");

        // ===== POLYLINE TEST =====

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

        System.out.println("polyline id: \n\t" + myPolyline.getId());

        ArrayList<Line2D.Double> myPolylineGeometry = myPolyline.getGeometry();

        System.out.print("polyline geom: \n\t");
        for (int i=0; i<myPolylineGeometry.size(); i++) {
            System.out.print(myPolylineGeometry.get(i).toString() + ", ");
        }
        System.out.println();

        System.out.println("polyline geom (array format):");

        double[][] myPolylineArray = myPolyline.getArray();

        System.out.print("\tx array: [");
        for (int i=0; i<myPolylineArray[0].length; i++) {
            System.out.print(myPolylineArray[0][i] + ", ");
        }
        System.out.println("]");
        System.out.print("\ty array: [");
        for (int i=0; i<myPolylineArray[1].length; i++) {
            System.out.print(myPolylineArray[1][i] + ", ");
        }
        System.out.println("]\n\n");

        // ===== POLYGON TEST =====

        PolygonItem myPolygon = new PolygonItem(3, new Polygon(new int[]{0,1,1,0,0}, new int[]{0,0,1,1,0}, 5));
        System.out.println("polygon id: \n\t" + myPolygon.getId());
        System.out.println("polygon geom: \n\t" + myPolygon.getGeometry().toString());

        double[][] myPolygonArray = myPolygon.getArray();
        System.out.print("polygon geom (array format):\n\t[");
        for (int i=0; i<myPolygonArray[0].length; i++) {
            System.out.print(myPolygonArray[0][i] + ", ");
        }
        System.out.print("]\n\t[");
        for (int i=0; i<myPolygonArray[1].length; i++) {
            System.out.print(myPolygonArray[1][i] + ", ");
        }
        System.out.println("]\n\n");

        // ===== CHANGE GEOMETRY =====

        System.out.println("===== CHANGE GEOMETRY =====\n\n");

        myPoint.setGeometry(new Point(1,1));
        System.out.println("point id: \n\t" + myPoint.getId());
        System.out.println("point geom: \n\t" + myPoint.getGeometry().toString());
        System.out.println("point geom (array format):");
        double[][] myUpdatedPoint = myPoint.getArray();
        for (int i=0; i<myUpdatedPoint[0].length; i++) {
            System.out.print("\tx array: [");
            System.out.print(myUpdatedPoint[0][i] + ", ");
        }
        System.out.println("]");
        for (int i=0; i<myUpdatedPoint[1].length; i++) {
            System.out.print("\ty array: [");
            System.out.print(myUpdatedPoint[1][i] + ", ");
        }
        System.out.println("]\n\n");

        // ===== POLYLINE TEST =====

        ArrayList<Line2D.Double> myUpdatedArrayList = new ArrayList<Line2D.Double>();

        Line2D.Double myUpdatedLine2D = new Line2D.Double(1,1,2,2);
        myUpdatedArrayList.add(myUpdatedLine2D);

        Line2D.Double myUpdatedLine2D_2 = new Line2D.Double(2,2,4,4);
        myUpdatedArrayList.add(myUpdatedLine2D_2);

        Line2D.Double myUpdatedLine2D_3 = new Line2D.Double(4,4,8,6);
        myUpdatedArrayList.add(myUpdatedLine2D_3);

        Line2D.Double myUpdatedLine2D_4 = new Line2D.Double(8,6,10,10);
        myUpdatedArrayList.add(myUpdatedLine2D_4);

        myPolyline.setGeometry(myUpdatedArrayList);

        System.out.println("polyline id: \n\t" + myPolyline.getId());

        ArrayList<Line2D.Double> myUpdatedPolylineGeometry = myPolyline.getGeometry();

        System.out.print("polyline geom: \n\t");
        for (int i=0; i<myUpdatedPolylineGeometry.size(); i++) {
            System.out.print(myUpdatedPolylineGeometry.get(i).toString() + ", ");
        }
        System.out.println();

        System.out.println("polyline geom (array format):");

        double[][] myUpdatedPolylineArray = myPolyline.getArray();

        System.out.print("\tx array: [");
        for (int i=0; i<myUpdatedPolylineArray[0].length; i++) {
            System.out.print(myUpdatedPolylineArray[0][i] + ", ");
        }
        System.out.println("]");
        System.out.print("\ty array: [");
        for (int i=0; i<myUpdatedPolylineArray[1].length; i++) {
            System.out.print(myUpdatedPolylineArray[1][i] + ", ");
        }
        System.out.println("]\n\n");

        myPolygon.setGeometry(new Polygon(new int[]{0,3,3,0,0}, new int[]{0,0,3,3,0}, 5));
        System.out.println("polygon id: \n\t" + myPolygon.getId());
        System.out.println("polygon geom: \n\t" + myPolygon.getGeometry().toString());

        double[][] myUpdatedPolygonArray = myPolygon.getArray();
        System.out.print("polygon geom (array format):\n\t[");
        for (int i=0; i<myUpdatedPolygonArray[0].length; i++) {
            System.out.print(myUpdatedPolygonArray[0][i] + ", ");
        }
        System.out.print("]\n\t[");
        for (int i=0; i<myUpdatedPolygonArray[1].length; i++) {
            System.out.print(myUpdatedPolygonArray[1][i] + ", ");
        }
        System.out.println("]\n\n");



    }

}
