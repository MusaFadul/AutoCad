package tester;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import core_classes.DatabaseConnection;
import core_classes.Layer;
import geometry.PolygonItem;

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

        PolygonItem poly0 = new PolygonItem(0, new Polygon(new int[] {0,1,1,0}, new int[] {0,0,1,0}, 4));
        PolygonItem poly1 = new PolygonItem(1, new Polygon(new int[] {0,2,2,0}, new int[] {0,0,2,0}, 4));
        PolygonItem poly2 = new PolygonItem(2, new Polygon(new int[] {0,3,3,0}, new int[] {0,0,3,0}, 4));

        Layer myLayer = new Layer(0,true,"Polygon","myLayer");

        myLayer.getListOfFeatures().add(poly0);
        myLayer.getListOfFeatures().add(poly1);
        myLayer.getListOfFeatures().add(poly2);

        for (int i=0; i<myLayer.getListOfFeatures().size(); i++) {
            double[] curx = myLayer.getListOfFeatures().get(i).getCoordinatesArrayXY()[0];
            double[] cury = myLayer.getListOfFeatures().get(i).getCoordinatesArrayXY()[1];
            for (int j=0; j<curx.length; i++) {
                System.out.print(curx[j] + ", ");
                System.out.println(cury[j]);
            }
        }

        // append the green features to the blue table
        databaseConnection.appendToTable("test_table", myLayer);

        // drop the green features altogether
/*        databaseConnection.dropTable("green_table");

        // get all the tables in the database and print them to console
        ArrayList<String> tables = databaseConnection.getTables();
        for (int i=0; i<tables.size(); i++) {

            System.out.println(tables.get(i));

            ArrayList<String[]> features = databaseConnection.readTable(tables.get(i));
            for (int j=0; j<features.size(); j++) {
                System.out.println("\t" + features.get(j)[0]);
                System.out.println("\t" + features.get(j)[1]);
                System.out.println("\t" + features.get(j)[2]);
            }

        }
*/
    }

}
