package tester;

import java.util.ArrayList;

import core_classes.DatabaseConnection;

/**
 * Created by isaac on 22/11/17.
 */
public class TestDatabaseConnection {

//    public static void main(String[] args) {
//
//        /**
//         * Class for testing database functionality.
//         *   |                                                     |
//         *   |                                                     |
//         *   |   PUT YOUR DATABASE CREDENTIALS HERE TO RUN TEST    |
//         *   |                                                     |
//         *   |                                                     |
//         *   V                                                     v
//         */
//        // Initialize database connection
//        DatabaseConnection databaseConnection = new DatabaseConnection(
//            "localhost",
//            5432,
//            "softeng_db",
//            "postgres",
//            "postgres"
//        );
///*
//        // create some new features and add them to ArrayLists
//        ArrayList<String[]> redFeatures = new ArrayList<>();
//        redFeatures.add(new String[] {"#ff0000", "1.5", "LINESTRING (30 10, 10 30, 40 40)"});
//        redFeatures.add(new String[] {"#ff0000", "1.5", "LINESTRING (30 10, 10 30, 40 40)"});
//        redFeatures.add(new String[] {"#ff0000", "1.5", "LINESTRING (30 10, 10 30, 40 40)"});
//
//        ArrayList<String[]> blueFeatures = new ArrayList<>();
//        blueFeatures.add(new String[] {"#0000ff", "1.5", "LINESTRING (30 10, 10 30, 40 40)"});
//        blueFeatures.add(new String[] {"#0000ff", "1.5", "LINESTRING (30 10, 10 30, 40 40)"});
//        blueFeatures.add(new String[] {"#0000ff", "1.5", "LINESTRING (30 10, 10 30, 40 40)"});
//
//        ArrayList<String[]> greenFeatures = new ArrayList<>();
//        greenFeatures.add(new String[] {"#00ff00", "1.5", "LINESTRING (30 10, 10 30, 40 40)"});
//        greenFeatures.add(new String[] {"#00ff00", "1.5", "LINESTRING (30 10, 10 30, 40 40)"});
//        greenFeatures.add(new String[] {"#00ff00", "1.5", "LINESTRING (30 10, 10 30, 40 40)"});
//
//        // write all the red features to a table called "red_table", and do the same respecitvely for blue beatures & green features
//        databaseConnection.writeTable("red_table", redFeatures);
//        databaseConnection.writeTable("blue_table", blueFeatures);
//        databaseConnection.writeTable("green_table", greenFeatures);
//
//        // append the green features to the blue table
//        databaseConnection.appendToTable("blue_table", greenFeatures);
//
//        // drop the green features altogether
//        databaseConnection.dropTable("green_table");
//
//        // get all the tables in the database and print them to console
//        ArrayList<String> tables = databaseConnection.getTables();
//        for (int i=0; i<tables.size(); i++) {
//
//            System.out.println(tables.get(i));
//
//            ArrayList<String[]> features = databaseConnection.readTable(tables.get(i));
//            for (int j=0; j<features.size(); j++) {
//                System.out.println("\t" + features.get(j)[0]);
//                System.out.println("\t" + features.get(j)[1]);
//                System.out.println("\t" + features.get(j)[2]);
//            }
//
//        }
//*/
//    }

}
