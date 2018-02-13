package file_handling;

import java.io.*;

/**
 * A class for managing the database credentials in an external file so that they may be retrieved easily.  Stores
 * credentials in dbconfig.cfg in separate lines in the following order:
 *     <ul>
 *     <li>host</li>
 *     <li>port</li>
 *     <li>database</li>
 *     <li>user</li>
 *     </ul>
 *     @author Isaac
 *     @since 16/12/17
 *     @version 1
 */
public class DatabaseCredentialsManager {

    public String host = "";
    public int port = 0;
    public String database = "";
    public String user = "";

    /**
     * Constructor for DatabaseCredentialsManager.  Will initially try to read the current database credentials from
     * dbconfig.cfg.  If this file doesn't exist, it will be automatically created.
     * @throws IOException Throws an IOException
     */
    public DatabaseCredentialsManager() throws IOException {

        try {

            // Try to read the dbconfig.cfg file and read the individual credentials
            BufferedReader databaseCredentialsFileReader = new BufferedReader(new FileReader("dbconfig.cfg"));
            host = databaseCredentialsFileReader.readLine();
            port = Integer.parseInt(databaseCredentialsFileReader.readLine());
            database = databaseCredentialsFileReader.readLine();
            user = databaseCredentialsFileReader.readLine();

        } catch (FileNotFoundException e0) {

            try {

                // If the file doesn't exist, just make a new empty one.
                File databaseCredentialFile = new File("dbconfig.cfg");
                databaseCredentialFile.createNewFile();
                setDatabaseCredentials(host, port, database, user);

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }

    /**
     * Sets the database credentials. Will update the DatabaseCredentialsManager object and dbconfig.cfg.
     * @param host the host name of the database to set
     * @param port the port number of the database to set
     * @param database the name of the database to set
     * @param user the user role with which to connect to the database to set
     * @throws IOException Throws an IOException
     */
    public void setDatabaseCredentials(String host, int port, String database, String user) throws IOException {

        // Make a BufferedWriter and overwrite the current credentials to dbconfig.cfg
        BufferedWriter databaseCredentialsFileWriter = new BufferedWriter(new FileWriter("dbconfig.cfg", false));
        databaseCredentialsFileWriter.write(host + "\n");
        databaseCredentialsFileWriter.write(port + "\n");
        databaseCredentialsFileWriter.write(database +"\n");
        databaseCredentialsFileWriter.write(user);
        databaseCredentialsFileWriter.close();

    }

}
