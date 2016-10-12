package agrechnev;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleksiy Grechnyev on 10/11/2016.
 *
 * A results table structure for testing an SQL SELECT query
 * Includes both column names and results as arrays
 *
 * All fields are represented as Strings
 * Immutable class after creation (not while it is being created within static factory methods)
 * The array indices start with 0 as usual (and unlike the JDBC column numbers which start with 1)
 *
 * Note on columns names: while I could have avoided them, they give some extra testing
 * Wrong name will give SQL exception -> fail the test
 */
public final class ResultsTable {
    // SQL column names
    private String[] columnNames;
    // SELECT QUERY results :
    private List<String[]> queryResults;

    // Public constructor
    public ResultsTable(String[] columnNames, List<String[]> queryResults) {
        this.columnNames = columnNames;
        this.queryResults = queryResults;
    }

    // Getters
    public String[] getColumnNames() {
        return columnNames;
    }

    public List<String[]> getQueryResults() {
        return queryResults;
    }

    /**
     * Static factory: create a new ResultsTable object from a text file
     * Cells must be separated by some separator
     * The routine does not check the number of columns
     * It will give the test failure later
     *
     *
     * Note: this version reads the file containing only data, e.g. generated by
     * SELECT INTO OUTFILE <file_name> on server
     * This requires a proper separator
     *
     * @param columnNames Names of all columns
     * @param fileName  Input file name
     * @param separator  Separator regex, e.g. "\\|" for the '|' char
     * @return A new ResultsTable instance
     * @throws IOException
     */
    public static ResultsTable readFromDataFile(String[] columnNames, String fileName, String separator) throws IOException {
        // Create with a private constructor, with empty ArrayList
        ResultsTable newInstance=new ResultsTable(columnNames,new ArrayList<String[]>());

        // Read data from file
        try(BufferedReader in= Files.newBufferedReader(Paths.get(fileName))) {
            String line;

            while ((line = in.readLine())!=null) {
                newInstance.queryResults.add(line.split(separator));
            }
        }

        return newInstance;
    }

    /**
     * Static factory: create a new ResultsTable object from a text file
     * The routine does not check the number of columns
     *
     * NOTE: this version reads a fully formatted table with '+','-','|' markup
     * and columns names, as generated by the mysql client with -t flag, e.g.
     * mysql -t <temp.sql > temp.dat
     *   or
     * mysql -e "SELECT ... ;" > temp.dat
     *
     * @param fileName Input file name
     * @return A new ResultsTable instance
     * @throws IOException
     */
    public static ResultsTable readFromTableFile(String fileName) throws IOException {
        final String SEPARATOR = "\\s*\\|\\s*"; // separator regex for columns

        // Create with a private constructor, with null columns and empty ArrayList
        ResultsTable newInstance=new ResultsTable(null,new ArrayList<String[]>());

        // Read data from file
        try(BufferedReader in= Files.newBufferedReader(Paths.get(fileName))) {
            String line;

            in.readLine(); // Skip the 1st line

            line=in.readLine(); // Read the column names (2nd) line
            // Remove the leading and trailing '|' chars before splitting
            line=line.substring(line.indexOf('|')+1,line.lastIndexOf('|')).trim();


            newInstance.columnNames=line.split(SEPARATOR); // Set the column names

            in.readLine(); // Skip the 3rd line

            // The regex "[\\+\\-]+" matches the terminating line
            while ((line = in.readLine())!=null && !line.matches("[\\+\\-]+")) {

                // Remove the leading and trailing '|' chars before splitting
                line=line.substring(line.indexOf('|')+1,line.lastIndexOf('|')).trim();
                newInstance.queryResults.add(line.split(SEPARATOR));
            }
        }

        return newInstance;
    }

}
