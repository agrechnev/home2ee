DBTester

SQL SELECT unit testing mini-framework by Oleksiy Grechnyev

It has 3 levels of testing:

--
1. testSelect

Runs an SQL SELECT query with JDBC, after that you have to write your own Asserter (basically Consumer) to check the results
--
2. testSelectTable, testSelectUTable

Test if the SQL query results coincide with a ResultTable object (a pre-recorded table of data).

 testSelectTable requires rows to go in the same order. It is faster and it asserts each individual field  in a row, which is more informative. It often fails if the SQL server product or version is different from the one used to generate the table (I had a few such failures even in the MySQL vs MariaDB case)

 testSelectUTable allows rows to go in any order. It is slower and makes less informative asserts.

 Class ResultsTable is used to represent a table, and to read a table from a formatted disk file.  The latter option works for MySQL and MariaDB, but could fail on other SQL implementations.  Also treating null fields as "NULL" strings in testSelect(U)Table might be not portable.

--
3. testSelectFullAuto

Run an SQL SELECT query with both JDBC and an external SQL client and compare results
--

Level 2 and 3 tests have an optional parameter:

useColumnNames:   true=read columns by names (default), false=read columns by numbers

Using names is better, but fails if two columns have the same name

Usage: Run any of the 3 test methods from within a JUnit test routine (marked by @Test). See RunDBTests.java for examples. To run RunDBTest, run a Maven command 'mvn clean test' or 'mvn -Dtest=agrechnev.RunDBTest clean test' from the project root directory (.../home2ee). Or, if you're using Intellij Idea, simply run the class RunDBTest.

Important: You will need to edit up the configuration file scripts/dbtester.config first. At the very least, you must set up DB_USER, DB_PASSWORD of your MySQL server before running this program!

The config file contains JDBC connection parameters:
DB_DRIVER, DB_URL, DB_DATABASE, DB_USER, DB_PASSWORD
Note DB_DATABASE is required for the full auto tests,
plus one extra parameter DB_INITBEFOREEACH :
  DB_INITBEFOREEACH = true : run init scripts before each unit test (very slow)
  DB_INITBEFOREEACH = false : run init scripts only once

The scripts in the scripts/ directory are for MySQL/MariaDB, my code is tested for these two DBMS's only. Small modifications might be required for other SQL databases. The executable "mysql" must be in your path, and your OS must be either Windows (.bat scripts) or Unix/Linux (.sh scripts).

Note on the tests from the SQL book (Weinberg, Groff, ..., 3rd edition): I did the first few ones with testSelectUTable, and the rest with testSelectFullAuto. The pages correspond to the russian translation of the book.