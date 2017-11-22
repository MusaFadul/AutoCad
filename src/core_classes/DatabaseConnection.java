package core_classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by isaac on 22/11/17.
 */
public class DatabaseConnection {

    /**
     * A connection to a PostgreSQL database foe the purposes of storing basic geometric features.  It is not a direct
     * connection to the database server itself, meaning the user is not permitted to create or drop any tables, and
     * the only table permitted to be modified is one called "geo_data", which will be initialized automatically if it
     * does not already exist in the public schema.
     *
     * The individual "tables" with which the user works are actually just groups of features in the "geo_data" table,
     * whose membership in a table is indicated by the string stored in the "table_name" field.  This is to protect
     * against SQL injection by never allowing the user to specify table identifiers.
     *
     * The "geo_data" table stores all the data managed by Group 1's GIS application and has the following fields:
     * id: primary key for the table itself, automatically serialized.
     * table_name: varchar(100) representing individual "tables" being managed by the application.
     * colour: varchar(7) hexidecimal string (with hash symbol) representing the colour with which to draw the feature.
     * weight: real number representing the thickness with which to draw the feature.
     * wkt_geom: varchar representing the geometry of the feature, expressed in Well-Known-Text format.
     */

    private Connection conn;

    public DatabaseConnection(String host, int port, String database, String user, String password) {

        /**
         * Constructor method for a PostgreSQL database connection object.
         * @param host String representing the host address where the database is hosted.
         * @param port Integer representing the port number on which the database listens.
         * @param database String representing the name of the database to which to connect.
         * @param user String representing the username with which to connect to the database.
         * @param password String representing the password with which to connect to the database.
         */

        String connectionString = "jdbc:postgresql://" + host + ":" + Integer.toString(port) + "/" + database;
        try {
            // Establish database connection given database connection parameters.
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(connectionString, user, password);

            // Create geo_data table if it doesn't already exist.
            PreparedStatement initializationStatement = conn.prepareStatement("CREATE TABLE IF NOT EXISTS geo_data (id SERIAL PRIMARY KEY, table_name VARCHAR(100), colour VARCHAR(7), weight REAL, wkt_geom VARCHAR);");
            initializationStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getTables() {

        /**
         * Retrieves the names of all tables stored in the database.
         * @return A string ArrayList containing all the unique table names.
         */

        ArrayList<String> returnArrayList = new ArrayList<>();
        try {

            PreparedStatement selectStatement = conn.prepareStatement("SELECT DISTINCT table_name FROM geo_data;");
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                returnArrayList.add(resultSet.getString("table_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnArrayList;

    }

    public ArrayList<String[]> readTable(String tableName) {

        /**
         * Retrieves the features in a specified table.
         * @param tableName A string representing the name of the table from which to retrieve the features.
         * @return A string array ArrayList containing all the features in the specified table.
         */

        ArrayList<String[]> returnArrayList = new ArrayList<>();
        try {

            PreparedStatement selectStatement = conn.prepareStatement("SELECT colour, weight, wkt_geom FROM geo_data WHERE table_name = ?;");
            selectStatement.setString(1, tableName); // table name
            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {
                returnArrayList.add(
                        new String[] {
                                resultSet.getString("colour"),
                                resultSet.getString("weight").toString(),
                                resultSet.getString("wkt_geom"),
                        }
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnArrayList;

    }

    public void writeTable(String tableName, ArrayList<String[]> features) {

        /**
         * Creates a new table with specified features.  Overwrites the table if it already exists.
         * @param tableName String representing the name of the table to which to save the features.
         * @param features String Array ArrayList containing all the properties of the features.
         */

        try {

            PreparedStatement dropStatement = conn.prepareStatement("DELETE FROM geo_data WHERE table_name = ?;");
            dropStatement.setString(1, tableName);
            dropStatement.executeUpdate();

            this.appendToTable(tableName, features);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void appendToTable(String tableName, ArrayList<String[]> features) {

        /**
         * Adds new features to an existing table.
         * @param tableName String representing the name of the table to which to save the features.
         * @param features String Array ArrayList containing all the properties of the features.
         */

        try {

            for (int i=0; i<features.size(); i++) {

                PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO geo_data (table_name, colour, weight, wkt_geom) VALUES (?, ?, ?, ?);");
                String[] currentFeature = features.get(i);
                insertStatement.setString(1, tableName); // table name
                insertStatement.setString(2, currentFeature[0]); // colour
                insertStatement.setFloat(3, Float.parseFloat(currentFeature[1])); // weight
                insertStatement.setString(4, currentFeature[2]); // geometry
                insertStatement.executeUpdate();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void dropTable(String tableName) {

        /** Drops a table from the database
         * @param tableName String representing the name of the table to be deleted.
         */

        try {

            PreparedStatement dropStatement = conn.prepareStatement("DELETE FROM geo_data WHERE table_name = ?;");
            dropStatement.setString(1, tableName);
            dropStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
