package core_classes;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Array;
import core_classes.Feature;
import geometry.PointItem;
import geometry.PolylineItem;
import geometry.PolygonItem;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.Polygon;

/**
 * Created by isaac on 22/11/17.
 */
public class DatabaseConnection {


    /**
     *
     */

    private Connection conn;

    public DatabaseConnection(String host, int port, String database, String user, String password) throws SQLException, ClassNotFoundException {

        /**
         * Constructor method for a PostgreSQL database connection object.
         * @param host String representing the host address where the database is hosted.
         * @param port Integer representing the port number on which the database listens.
         * @param database String representing the name of the database to which to connect.
         * @param user String representing the username with which to connect to the database.
         * @param password String representing the password with which to connect to the database.
         */

        String connectionString = "jdbc:postgresql://" + host + ":" + Integer.toString(port) + "/" + database;

            // Establish database connection given database connection parameters.
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(connectionString, user, password);

            // Create geo_data table if it doesn't already exist.
            PreparedStatement initializationStatement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS geo_data (gid SERIAL PRIMARY KEY, table_name TEXT, id INTEGER, type TEXT, is_ellipse BOOLEAN, x DOUBLE PRECISION[], y DOUBLE PRECISION[], rx DOUBLE PRECISION, ry DOUBLE PRECISION);");
            initializationStatement.executeUpdate();

    }

    public ArrayList<String> getTables() throws SQLException {

        /**
         * Retrieves the names of all tables stored in the database.
         * @return A string ArrayList containing all the unique table names.
         */

        ArrayList<String> returnArrayList = new ArrayList<>();

            PreparedStatement selectStatement = conn.prepareStatement("SELECT DISTINCT table_name FROM geo_data;");
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                returnArrayList.add(resultSet.getString("table_name"));
            }

        return returnArrayList;

    }

    public List<Feature> readTable(String tableName) throws SQLException {

        /**
         * Retrieves the features in a specified table.
         * @param tableName A string representing the name of the table from which to retrieve the features.
         * @return A string array ArrayList containing all the features in the specified table.
         */

        List<Feature> returnList = new ArrayList<Feature>();

            PreparedStatement selectStatement = conn.prepareStatement("SELECT id, type, is_ellipse, x, y, rx, ry FROM geo_data WHERE table_name = ?;");
            selectStatement.setString(1, tableName); // table name
            ResultSet resultSet = selectStatement.executeQuery();

            int featureId;
            String featureType;
            double[] xArray, yArray;
            double xRadius, yRadius;

            while (resultSet.next()) {

                // Initialize feature with id
                featureId = resultSet.getInt("id");
                Feature feature = new Feature(featureId);

                // Set feature type
                // TODO: type isn't being written properly in the first place so i can't tell if this works or not
                featureType = resultSet.getString("type");
                feature.setFeatureType(featureType);

                // Set feature XY coordinate arrays
                xArray = (double[]) resultSet.getArray("x").getArray();
                yArray = (double[]) resultSet.getArray("y").getArray();
                feature.setCoordinatesArrayXY(new double[][] {xArray, yArray});

                // Set the radii if it is an ellipse
                if (resultSet.getBoolean("is_ellipse")) {
                    xRadius = resultSet.getDouble("rx");
                    yRadius = resultSet.getDouble("ry");
                    //feature.setRadius
                    //TODO: Feature has no setRadius method
                }

            }

        return returnList;

    }
//
//    public void writeTable(String tableName, ArrayList<Feature[]> features) {
//
//        /**
//         * Creates a new table with specified features.  Overwrites the table if it already exists.
//         * @param tableName String representing the name of the table to which to save the features.
//         * @param features String Array ArrayList containing all the properties of the features.
//         */
//
//        try {
//
//            PreparedStatement dropStatement = conn.prepareStatement("DELETE FROM geo_data WHERE table_name = ?;");
//            dropStatement.setString(1, tableName);
//            dropStatement.executeUpdate();
//
//            this.appendToTable(tableName, features);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
    public void appendToTable(String tableName, Layer layer) throws SQLException {

        /**
         * Adds new features to an existing table.
         * @param tableName String representing the name of the table to which to save the features.
         * @param features String Array ArrayList containing all the properties of the features.
         */

        List<Feature> featureList = layer.getListOfFeatures();
        Feature feature;
        int featureId;
        String featureType;
        boolean isEllipse;
        double xCoords[], yCoords[];
        double xRadius = 0 , yRadius = 0;

        // Iterate through feature list & write each to the database
        for (int i=0; i<featureList.size(); i++) {

            feature = featureList.get(i);

            featureId = feature.getId();
            featureType = feature.getFeatureType();
            isEllipse = feature.isEllipse();

            xCoords = feature.getCoordinatesArrayXY()[0];
            yCoords = feature.getCoordinatesArrayXY()[1];
            Object[] xCoordsObject = new Object[xCoords.length];
            Object[] yCoordsObject = new Object[yCoords.length];
            for (int j=0; j<xCoords.length; i++) {
                xCoordsObject[j] = Double.toString(xCoords[j]);
                yCoordsObject[j] = Double.toString(yCoords[j]);
                System.out.println(xCoords[j] + yCoords[j]);
                System.out.println(Double.toString(xCoords[j]) + Double.toString(yCoords[j]));
            }

            Array myX = conn.createArrayOf("float4", xCoordsObject);
            Array myY = conn.createArrayOf("float4", yCoordsObject);

            if (isEllipse) {
                xRadius = feature.getRadiusX();
                yRadius = feature.getRadiusY();
            }

            PreparedStatement insertFeatureStatement = conn.prepareStatement("INSERT INTO geo_data (table_name, id, type, is_ellipse, x, y, rx, ry) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            insertFeatureStatement.setString(1, tableName);
            insertFeatureStatement.setInt(2, featureId);
            insertFeatureStatement.setString(3, featureType);
            insertFeatureStatement.setBoolean(4, isEllipse);
            insertFeatureStatement.setArray(5, myX);
            insertFeatureStatement.setArray(6, myY);
            insertFeatureStatement.setDouble(7, xRadius);
            insertFeatureStatement.setDouble(8, yRadius);

            insertFeatureStatement.executeUpdate();

        }

    }
//
//    public void dropTable(String tableName) {
//
//        /** Drops a table from the database
//         * @param tableName String representing the name of the table to be deleted.
//         */
//
//        try {
//
//            PreparedStatement dropStatement = conn.prepareStatement("DELETE FROM geo_data WHERE table_name = ?;");
//            dropStatement.setString(1, tableName);
//            dropStatement.executeUpdate();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

}
