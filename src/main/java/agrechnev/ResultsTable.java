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
 * Immutable class (except when it is being created within readFromFile)
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
     * Static factory: create a new ResultsTable name from a text file
     * Cells must be separated by some separator
     * The routine does not check the number of columns
     * It will give the test failure later
     *
     * @param columnNames Names of all columns
     * @param fileName  File name
     * @param separator  Separator regex, e.g. "\\|" for the '|' char
     * @return A new ResultsTable instance
     * @throws IOException
     */
    public static ResultsTable readFromFile(String[] columnNames,String fileName,String separator) throws IOException {
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

}
