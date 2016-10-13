package agrechnev;

import org.junit.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
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
 * Execpt for DB_INITBEFOREEACH :
 * DB_INITBEFOREEACH =true : run init scripts before each unit test (very slow)
 * DB_INITBEFOREEACH = false : run init scripts only once
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
    private String DB_DATABASE;
    private String DB_USER;
    private String DB_PASSWORD;
    private boolean DB_INITBEFOREEACH;

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
            DB_DATABASE = config.getProperty("DB_DATABASE");
            DB_USER = config.getProperty("DB_USER");
            DB_PASSWORD = config.getProperty("DB_PASSWORD");
            DB_INITBEFOREEACH = Boolean.parseBoolean(config.getProperty("DB_INITBEFOREEACH"));

        } catch (IOException e) {
            System.err.println("Error: Cannot find file scripts/dbtester.config ");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("DB_DRIVER="+DB_DRIVER);
        System.out.println("DB_URL="+DB_URL);
        System.out.println("DB_DATABASE="+DB_DATABASE);
        System.out.println("DB_USER="+DB_USER);
        System.out.println("DB_PASSWORD="+DB_PASSWORD);
        System.out.println("DB_INITBEFOREEACH="+DB_INITBEFOREEACH);

        // Check for Windows vs Unix
        String os = System.getProperty("os.name").toLowerCase();
        runsOnWindows = os.contains("win");

        // Ensure the exectutable permission of the Unix scripts scripts/init.sh, scripts/run.sh
        // Do not quit on exception, only print the error message
        try {
            if (!runsOnWindows) {
                Runtime.getRuntime().exec("chmod a+x scripts/init.sh").waitFor();
                Runtime.getRuntime().exec("chmod a+x scripts/run.sh").waitFor();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // First, run the init script if DB_INITBEFOREEACH=false
        // Exit if unsuccessful
        if (!DB_INITBEFOREEACH) {
            /*if (!runInitScript()) {
                System.exit(1);
            }*/
        }
    }

    /**
     * Test a single SQL SELECT statement with a supplied function asserter:
     *
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

        // First, run the init script if DB_INITBEFOREEACH=true
        // Fail test if unsuccessful
        if (DB_INITBEFOREEACH) Assert.assertTrue(runInitScript());

        System.out.println(sqlQuery); // Print the query

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
     * @param useColumnNames   true=check column names, false=read columns by number
     *
     * Note: I had to introduce the latter option because of multiple columns with same name
     *  from different tables in some searches, must use false then
     *
     * I also add an overloaded version without it below
     */
    public void testSelectWithTable(String sqlQuery,ResultsTable table,boolean useColumnNames) {
        // We use testSelect() with an elaborate Asserter
        testSelect(sqlQuery,(rs)->{

            // Note:  if the result set is empty
            //  table.getColumnNames() is allowed to be NULL
            // That is why I put column count check into the row: foreach loop
            // If empty table.getColumnNames() we skip the foreach loop and do one check
            // Assert.assertFalse(rs.next());

            String[] colNames=table.getColumnNames(); // Local cache column names


            for (String[] row: table.getQueryResults()) {

                int numCol=colNames.length; // Number of columns in colNames

                // Assert that numCol matches the column count from the result set
                Assert.assertEquals(rs.getMetaData().getColumnCount(),numCol);

                // Check that next row is available in the results set
                Assert.assertTrue(rs.next());

                // Assert the number of cells in the row first
                Assert.assertEquals(row.length,numCol);


                // Loop over all columns colInd=column index
                for (int colInd = 0; colInd < numCol; colInd++) {
                    // Check every cell of the row
                    // I use trim() just in case

                    // Compare two strings from table and rs
                    String stringTab = row[colInd].trim();
                    String stringRS;

                    // Get the RS cell by either name or number
                    if (useColumnNames) {
                        stringRS = rs.getString(colNames[colInd].trim());
                    } else {
                        stringRS = rs.getString(colInd+1);
                    }

                    if (stringRS != null) stringRS=stringRS.trim();

                    // Note: NULL is a special case,
                    // stringRS=NULL, stringTab="NULL"
                    Assert.assertEquals(stringTab, stringRS==null ? "NULL" : stringRS);
                }

            }

            // Finally check that there is no more results in the results set
            Assert.assertFalse(rs.next());

        });

    }

    /**
     * Test whether the result of the sqlQuery coincides with a ResultTable object
     * @param sqlQuery An SQL SELECT statement
     * @param table The desired result as a ResultTable
     */
    public void testSelectWithTable(String sqlQuery,ResultsTable table) {
        testSelectWithTable(sqlQuery,table,true);
    }

    /**
     * Fully qutomatic test of an SQL select query
     * Runs the same query through external sql client first (via run.bat or run.sh)
     * and then through JDBC and compares both result sets
     *
     * Fails on any exception (e.g. on SQL syntax error)
     *
     * Uses database nade DB_DATABASE from the external query
     *
     * @param sqlQuery The query to be tested
     * @param useColumnNames   true=check column names, false=read columns by number
     *
     * Note: I had to introduce the latter option because of multiple columns with same name
     *  from different tables in some searches, must use false then
     *
     * I also add an overloaded version without it below
     */
    public void testSelectFullAuto(String sqlQuery,boolean useColumnNames){
        try {

            // Run the query with external SQL
            // I use the external script file for that
            // One could have used mysql -e "SELECT ...;"
            // But external file is more robust if the query is long or contains special chars

            try (PrintWriter out=new PrintWriter(Files.newBufferedWriter(Paths.get("temp.sql")))){
                if (DB_DATABASE != null && DB_DATABASE!="") {
                    out.println("USE "+DB_DATABASE+";"); //Add the SQL use statement
                }
                out.println(sqlQuery); // Add the query
            };

            // Run MySql client with the query file via run.bat or run.sh script

            String command = (runsOnWindows ? "scripts\\run.bat " : "scripts/run.sh ") +
                    DB_USER + " " + DB_PASSWORD;
            Runtime.getRuntime().exec(command).waitFor();

            // Read the table from file
            ResultsTable table=ResultsTable.readFromTableFile("temp.dat");

            // Test with the table
            testSelectWithTable(sqlQuery,table,useColumnNames);

            // Remove the temp files
            Files.delete(Paths.get("temp.sql"));
            Files.delete(Paths.get("temp.dat"));


        } catch (InterruptedException|IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

    }

    /**
     * Fully qutomatic test of an SQL select query
     * Runs the same query through external sql client first (via run.bat or run.sh)
     * and then through JDBC and compares both result sets
     *
     * Fails on any exception (e.g. on SQL syntax error)
     *
     * Uses database nade DB_DATABASE from the external query
     *
     * @param sqlQuery The query to be tested
     */
    public void testSelectFullAuto(String sqlQuery){
        testSelectFullAuto(sqlQuery,true);
    }


    /**
     * Run the init script for the SQL database
     * @return true is successful, false if exception
     */
    private boolean runInitScript(){
        try {
            System.out.println("Running init scripts ...");

            // different for Win and Unix
            String command = (runsOnWindows ? "scripts\\init.bat " : "scripts/init.sh ") +
                    DB_USER + " " + DB_PASSWORD;

            Runtime.getRuntime().exec(command).waitFor();
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

}
