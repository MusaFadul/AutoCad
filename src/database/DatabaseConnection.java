package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Array;
import core_classes.Feature;
import core_classes.Layer;

/**
 * An object representing a connection to a PostgreSQL database.  All data will be stored in a table called "geo_data".
 * This is to prevent SQL injection.  Individual "tables" are actually just groups of features identified by the value
 * in the "table_name_ attribute.  If the "geo_data" table does not exist, it will be created upon initialization of
 * this object.
 * @author isaac
 * @since 22/11/17
 */
public class DatabaseConnection {


    private Connection conn;
    /**
     * String representing the host address where the database is hosted.
     */
    public static String dbHost;
    /**
     * Integer representing the port number on which the database listens.
     */
    public static int dbPort;
    /**
     * String representing the name of the database to which to connect.
     */
    public static String dbName;
    /**
     * String representing the username with which to connect to the database.
     */
    public static String dbUser;


    /**
     * Constructor method for a PostgreSQL database connection object.
     * @param host String representing the host address where the database is hosted.
     * @param port Integer representing the port number on which the database listens.
     * @param database String representing the name of the database to which to connect.
     * @param user String representing the user name with which to connect to the database.
     * @param password String representing the password with which to connect to the database.
     * @throws SQLException Throws an SQLException
     * @throws ClassNotFoundException Throws a ClassNotFoundException
     */
    public DatabaseConnection(String host, int port, String database, String user, String password) throws SQLException, ClassNotFoundException {

        String connectionString = "jdbc:postgresql://" + host + ":" + Integer.toString(port) + "/" + database;

            // Establish database connection given database connection parameters.
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(connectionString, user, password);

            // Create geo_data table if it doesn't already exist.
            PreparedStatement initializationStatement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS geo_data (gid SERIAL PRIMARY KEY, table_name TEXT, id INTEGER, type TEXT, is_ellipse BOOLEAN, x DOUBLE PRECISION[], y DOUBLE PRECISION[], rx DOUBLE PRECISION, ry DOUBLE PRECISION);");
            initializationStatement.executeUpdate();

            dbHost = host;
            dbPort = port;
            dbName = database;
            dbUser = user;

    }

    /**
     * Retrieves the names of all tables stored in the database.
     * @return A string[2] ArrayList containing all the unique table names, along with their feature type.
     * @throws SQLException Throws an SQLEXception
     */
    public ArrayList<String[]>getTables() throws SQLException {

        ArrayList<String[]> returnArrayList = new ArrayList<>();

            PreparedStatement selectStatement = conn.prepareStatement("SELECT DISTINCT table_name, type FROM geo_data;");
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                returnArrayList.add(new String[] {resultSet.getString("table_name"), resultSet.getString("type")});
            }

        return returnArrayList;

    }

    /**
     * Retrieves the features in a specified table.
     * @param tableName A string representing the name of the table from which to retrieve the features.
     * @return A string array ArrayList containing all the features in the specified table.
     * @throws SQLException Throws a SQLEXception
     */
    public ResultSet readTable(String tableName) throws SQLException {

            PreparedStatement selectStatement = conn.prepareStatement("SELECT id, type, is_ellipse, x, y, rx, ry FROM geo_data WHERE table_name = ?;");
            selectStatement.setString(1, tableName); // table name
            ResultSet resultSet = selectStatement.executeQuery();

        return resultSet;

    }

    /**
     * Creates a new table with specified features.  Overwrites the table if it already exists.
     * @param tableName String representing the name of the table to which to save the features.
     * @param layer Layer object to be added to the database as a table.
     * @throws SQLException Throws a SQLEXception
     */
    public void writeTable(String tableName, Layer layer) throws SQLException {

            PreparedStatement dropStatement = conn.prepareStatement("DELETE FROM geo_data WHERE table_name = ?;");
            dropStatement.setString(1, tableName);
            dropStatement.executeUpdate();

            this.appendToTable(tableName, layer);

    }

    /**
     * Adds new features to an existing table.
     * @param tableName String representing the name of the table to which to save the features.
     * @param layer Layer object to be appended to the existing table in the database
     * @throws SQLException Throws a SQLEXception
     */
    public void appendToTable(String tableName, Layer layer) throws SQLException {

        List<Feature> featureList = layer.getListOfFeatures();
        Feature feature;
        int featureId;
        String layerType = layer.getLayerType();
        boolean isEllipse;
        double xCoords[], yCoords[];
        double xRadius, yRadius;

        // Iterate through feature list & write each to the database
        for (int i=0; i<featureList.size(); i++) {
        	
            feature = featureList.get(i);

            featureId = feature.getId();
            isEllipse = feature.isEllipse();

            xCoords = feature.getCoordinatesArrayXY()[0];
            yCoords = feature.getCoordinatesArrayXY()[1];
            Object[] xCoordsObject = new Object[xCoords.length];
            Object[] yCoordsObject = new Object[yCoords.length];
            for (int j=0; j<xCoords.length; j++) {
                xCoordsObject[j] = Double.toString(xCoords[j]);
                yCoordsObject[j] = Double.toString(yCoords[j]);
            }

            xRadius = 0;
            yRadius = 0;
            if (isEllipse) {
            	
                xRadius = feature.getRadiusX();
                yRadius = feature.getRadiusY();
                
                xCoordsObject = new Object[1];
                xCoordsObject[0] = feature.getCenter().getX();
                
                yCoordsObject = new Object[1];
                yCoordsObject[0] = feature.getCenter().getY();
                
            }

            Array myX = conn.createArrayOf("float4", xCoordsObject);
            Array myY = conn.createArrayOf("float4", yCoordsObject);

            PreparedStatement insertFeatureStatement = conn.prepareStatement("INSERT INTO geo_data (table_name, id, type, is_ellipse, x, y, rx, ry) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            
            insertFeatureStatement.setString(1, tableName);
            insertFeatureStatement.setInt(2, featureId);
            insertFeatureStatement.setString(3, layerType);
            insertFeatureStatement.setBoolean(4, isEllipse);
            insertFeatureStatement.setArray(5, myX);
            insertFeatureStatement.setArray(6, myY);
            insertFeatureStatement.setDouble(7, xRadius);
            insertFeatureStatement.setDouble(8, yRadius);

            insertFeatureStatement.executeUpdate();

        }

    }

    /** 
     * Drops a table from the database
     * @param tableName String representing the name of the table to be deleted.
     * @throws SQLException Throws a SQLEXception
     */
    public void dropTable(String tableName) throws SQLException {

            PreparedStatement dropStatement = conn.prepareStatement("DELETE FROM geo_data WHERE table_name = ?;");
            dropStatement.setString(1, tableName);
            dropStatement.executeUpdate();

    }

}
