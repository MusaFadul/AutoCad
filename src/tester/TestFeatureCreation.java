package tester;
import geometry.PointItem;
import geometry.PolylineItem;
import geometry.PolygonItem;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.Polygon;

/**
 * Created by isaac on 04/12/17.
 */
public class TestFeatureCreation {

    public static void main(String[] args) {

        PointItem myPoint = new PointItem(1, new Point(0,0));
        System.out.println("point id: " + myPoint.getId());
        System.out.println("point geom: " + myPoint.getGeometry().toString());

        PolylineItem myPolyline = new PolylineItem(2, new Line2D.Double(0,0,1,1));
        System.out.println("polyline id: " + myPolyline.getId());
        System.out.println("polyline geom: " + myPolyline.getGeometry().toString());

        PolygonItem myPolygon = new PolygonItem(3, new Polygon(new int[]{0,1,1,0,0}, new int[]{0,0,1,1,0}, 5));
        System.out.println("polygon id: " + myPolygon.getId());
        System.out.println("polygon geom: " + myPolygon.getGeometry().toString());

        System.out.println("===== CHANGE GEOMETRY =====");

        myPoint.setGeometry(new Point(1,1));
        System.out.println("point id: " + myPoint.getId());
        System.out.println("point geom: " + myPoint.getGeometry().toString());

        myPolyline.setGeometry(new Line2D.Double(2,2,3,3));
        System.out.println("polyline id: " + myPolyline.getId());
        System.out.println("polyline geom: " + myPolyline.getGeometry().toString());

        myPolygon.setGeometry(new Polygon(new int[]{1,2,2,1,1}, new int[]{1,1,2,2,1}, 5));
        System.out.println("polygon id: " + myPolygon.getId());
        System.out.println("polygon geom: " + myPolygon.getGeometry().toString());

    }

}
