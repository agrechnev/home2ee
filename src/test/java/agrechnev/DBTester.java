package agrechnev;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

/**
 * Created by Oleksiy Grechnyev on 10/11/2016.
 * A toy framework for SQL unit testing with Junit and JDBC
 * Part of Java EE homework assignment 2
 * <p>
 * An enum singleton. In real life I would used a multiple-instance object
 * (each instance=server+database), but for the homework 2 a singleton will do
 * <p>
 * The connection is configured by the file scripts/dbtester.config
 * with a trivial syntax
 */

public enum DBTester {
    INSTANCE; // The only instance in existence

    /**
     * A Functional interface much like Consumer<ResultSet>
     * but throws SQLException
     */
    interface Asserter {
        /**
         * Perform the operation
         *
         * @param resultSet the input argument
         * @throws SQLException
         */
        void accept(ResultSet resultSet) throws SQLException;
    }

    // The SQL connection parameters
    // Loaded from file scripts/dbtester.config by the constructor
    private String DB_DRIVER;
    private String DB_URL;
    private String DB_USER;
    private String DB_PASSWORD;

    // True if runs on windows, otherwise assume Unix/Linux
    // It's ugly to run .bat/.sh files without this check
    private boolean runsOnWindows;


    // Constructor: runs once for a single instance
    DBTester() {
        System.out.println("Reading DBTester configuration ...");

        // Read the configuration file scripts/dbtester.config
        Properties config = new Properties();
        try (BufferedReader in = Files.newBufferedReader(Paths.get("scripts", "dbtester.config"))) {
            // Load configuration from file
            config.load(in);
            // Set the SQL parameters
            DB_DRIVER = config.getProperty("DB_DRIVER");
            DB_URL = config.getProperty("DB_URL");
            DB_USER = config.getProperty("DB_USER");
            DB_PASSWORD = config.getProperty("DB_PASSWORD");

        } catch (IOException e) {
            System.err.println("Error: Cannot find file scripts/dbtester.config ");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("DB_DRIVER="+DB_DRIVER);
        System.out.println("DB_URL="+DB_URL);
        System.out.println("DB_USER="+DB_USER);
        System.out.println("DB_PASSWORD="+DB_PASSWORD);

        // Check for Windows vs Unix
        String os = System.getProperty("os.name").toLowerCase();
        runsOnWindows = os.contains("win");

        // Ensure the exectutable permission of the Unix script scripts/init.sh
        // Do not quit on exception, only print the error message
        try {
            if (!runsOnWindows) Runtime.getRuntime().exec("chmod a+x scripts/init.sh").waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test a single SQL SELECT statement:
     * <p>
     * First runs the init script to set up the database
     * Then open the connection, executet he quesry and check the results with asserter
     *
     *                 Junit test example:
     *  @Test
     *  public void testExample(){
     *    DBTester.INSTANCE.testSelect("SELECT user();",rs -> {
     *     Assert.assertTrue(rs.next());
     *     Assert.assertEquals(rs.getString("user()"),"imbecile@localhost");
     *     Assert.assertFalse(rs.next());
     *    });
     *  }
     *
     * @param sqlQuery The select statement to test
     * @param asserter A function to check the result set using JUnit asserts, use lambdas
     *
     */
    public void testSelect(String sqlQuery, Asserter asserter) {
        // Running script before each test and opening a new connection
        // is extremely inefficient
        // I do it to ensure a clean test

        // First, run the init script: different for Win and Unix
        /*try {

            String command = (runsOnWindows ? "scripts\\init.bat " : "scripts/init.sh ") +
                    DB_USER + " " + DB_PASSWORD;

            Runtime.getRuntime().exec(command).waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            // Exception means failed test
            Assert.fail();
        }*/

        // Load the DB driver
        // Not needed nowadays, but wouldn't hurt
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Assert.fail();
        }

        // Then, run the query with auto-close
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery);
        ) {
            // Check the results with provided asserter
            asserter.accept(resultSet);

        } catch (SQLException e) {
            e.printStackTrace();
            // SQL exception means failed test
            Assert.fail();
        }
    }

    /**
     * Test whether the result of the sqlQuery coincides with a ResultTable object
     * @param sqlQuery An SQL SELECT statement
     * @param table The desired result as a ResultTable
     */
    public void testSelectWithTable(String sqlQuery,ResultsTable table) {
        // We use testSelect() with an elaborate Asserter
        testSelect(sqlQuery,(rs)->{
            String[] colNames=table.getColumnNames(); // Local cache column names
            int numCol=colNames.length; // Number of columns

            for (String[] row: table.getQueryResults()) {
                // Check that next row is available in the results set
                Assert.assertTrue(rs.next());

                // Assert the number of cells in the row first
                Assert.assertEquals(row.length,numCol);

                // Loop over all columns colInd=column index
                for (int colInd = 0; colInd < numCol; colInd++) {
                    // Check every cell of the row
                    // I use trim() just in case
                    Assert.assertEquals(row[colInd].trim(),rs.getString(colNames[colInd]).trim());
                }

            }

            // Finally check that there is no more results in the results set
            Assert.assertFalse(rs.next());

        });

    }

}
