package tester;

import java.awt.*;
import java.awt.geom.Line2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core_classes.Layer;
import database.DatabaseConnection;
import features.PolygonItem;
import features.PolylineItem;

/**
 * Created by isaac on 22/11/17.
 */
public class TestDatabaseConnection {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        /**
         * Class for testing database functionality.
         *   |                                                     |
         *   |                                                     |
         *   |   PUT YOUR DATABASE CREDENTIALS HERE TO RUN TEST    |
         *   |                                                     |
         *   |                                                     |
         *   V                                                     v
         */
        // Initialize database connection
        DatabaseConnection databaseConnection = new DatabaseConnection(
            "localhost",
            5432,
            "softeng_db",
            "postgres",
            "postgres"
        );

        // =============================================================================================================
        // TEST POLYGONS
        // =============================================================================================================

        PolygonItem poly0 = new PolygonItem(0, new Polygon(new int[] {0,1,1,0}, new int[] {0,0,1,0}, 4));
        PolygonItem poly1 = new PolygonItem(1, new Polygon(new int[] {0,2,2,0}, new int[] {0,0,2,0}, 4));
        PolygonItem poly2 = new PolygonItem(2, new Polygon(new int[] {0,3,3,0}, new int[] {0,0,3,0}, 4));

        Layer myPolygonLayer = new Layer(0,true,"Polygon","myPolygonLayer");
        myPolygonLayer.getListOfFeatures().add(poly0);
        myPolygonLayer.getListOfFeatures().add(poly1);
        myPolygonLayer.getListOfFeatures().add(poly2);

        databaseConnection.writeTable("polygon_table", myPolygonLayer);

        // =============================================================================================================
        // TEST POLYLINES
        // =============================================================================================================

        Line2D.Double lineSeg0_0 = new Line2D.Double(0,0,1,1);
        Line2D.Double lineSeg0_1 = new Line2D.Double(1,1,2,2);
        Line2D.Double lineSeg0_2 = new Line2D.Double(2,2,3,4);
        PolylineItem line0 = new PolylineItem(0, new ArrayList<Line2D.Double>(Arrays.asList(lineSeg0_0, lineSeg0_1, lineSeg0_2)));

        Line2D.Double lineSeg1_0 = new Line2D.Double(5,6,7,8);
        Line2D.Double lineSeg1_1 = new Line2D.Double(7,8,2,2);
        Line2D.Double lineSeg1_2 = new Line2D.Double(2,2,9,9);
        PolylineItem line1 = new PolylineItem(1, new ArrayList<Line2D.Double>(Arrays.asList(lineSeg1_0, lineSeg1_1, lineSeg1_2)));

        Line2D.Double lineSeg2_0 = new Line2D.Double(3,2,1,0);
        Line2D.Double lineSeg2_1 = new Line2D.Double(1,0,10,10);
        Line2D.Double lineSeg2_2 = new Line2D.Double(10,10,100,100);
        PolylineItem line2 = new PolylineItem(2, new ArrayList<Line2D.Double>(Arrays.asList(lineSeg2_0, lineSeg2_1, lineSeg2_2)));

        Layer myPolylineLayer = new Layer(0,true,"Polyline", "myPolyLineLayer");
        myPolylineLayer.getListOfFeatures().add(line0);
        myPolylineLayer.getListOfFeatures().add(line1);
        myPolylineLayer.getListOfFeatures().add(line2);

        databaseConnection.writeTable("polyline_table", myPolylineLayer);

        // =============================================================================================================
        // TEST APPENDING TABLES
        // =============================================================================================================

        PolygonItem polyNew = new PolygonItem(3, new Polygon(new int[] {10,15,15,10}, new int[] {10,10,15,10}, 4));
        Layer myNewPolygonLayer = new Layer(0,true,"Polygon","new_polygon_layer");
        myNewPolygonLayer.getListOfFeatures().add(polyNew);

        databaseConnection.appendToTable("polygon_table", myNewPolygonLayer);

        Line2D.Double lineSegNew_0 = new Line2D.Double(0,0,10,10);
        Line2D.Double lineSegNew_1 = new Line2D.Double(10,10,20,20);
        Line2D.Double lineSegNew_2 = new Line2D.Double(20,20,30,40);
        PolylineItem lineNew = new PolylineItem(3, new ArrayList<Line2D.Double>(Arrays.asList(lineSegNew_0, lineSegNew_1, lineSegNew_2)));

        Layer myNewPolylineLayer = new Layer(0,true,"Polyline", "new_polyline_layer");
        myNewPolylineLayer.getListOfFeatures().add(lineNew);

        databaseConnection.appendToTable("polyline_table", myNewPolylineLayer);

        // =============================================================================================================
        // TEST DROPPING TABLES (uncomment to test dropping)
        // =============================================================================================================

        //databaseConnection.dropTable("polygon_table");
        //databaseConnection.dropTable("polyline_table");

        // =============================================================================================================
        // TEST GET TABLES
        // =============================================================================================================

        ArrayList<String[]> tables = databaseConnection.getTables();
        for (int i=0; i<tables.size(); i++) {
            System.out.println(tables.get(i)[0]);
        }

    }

}
