package agrechnev;

import org.junit.Test;

import java.io.IOException;

/**
 * My SQL tests, part of Java EE Homework assignment 2
 * Uses DBTester
 * Created by Oleksiy Grechnyev
 *
 * Note: I used testSelectWithTable for the first few tests (158-163)
 * And then switch to the more efficient testSelectFullAuto
 * */

public class RunDBTests {
    public static final String SEPARATOR = "\\s*\\|\\s*"; // separator regex for testSelectWithTable
    /*
    // A sample test with testSelect
    // Other wise I only use testSelectWithTable
    @Test
    public void test_user(){
        DBTester.INSTANCE.testSelect("SELECT user();",rs -> {
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getString("user()"),"agrechnev@localhost");
            Assert.assertFalse(rs.next());
        });
    }*/

    // Tests158-163 demonstrate testing with testSelectWithTable()



    @Test
    public void test158() throws IOException {
        ResultsTable table=ResultsTable.readFromDataFile(
             new String[]{"ORDER_NUM","AMOUNT","COMPANY","CREDIT_LIMIT"},
                "tables/t158.dat", SEPARATOR);

        DBTester.INSTANCE.testSelectWithTable(
                "SELECT ORDER_NUM,AMOUNT,COMPANY,CREDIT_LIMIT FROM ORDERS,CUSTOMERS WHERE CUST=CUST_NUM;",
                table);
    }

    @Test
    public void test159() throws IOException {
        ResultsTable table=ResultsTable.readFromDataFile(
             new String[]{"NAME" , "CITY" , "REGION"},
                "tables/t159.dat", SEPARATOR);

        DBTester.INSTANCE.testSelectWithTable(
                "SELECT NAME , CITY , REGION FROM SALESREPS, OFFICES WHERE REP_OFFICE=OFFICE;",
                table);
    }


    @Test
    public void test161() throws IOException {
        ResultsTable table=ResultsTable.readFromDataFile(
                new String[]{"CITY", "NAME" ,"TITLE"},
                "tables/t161.dat", SEPARATOR);

        DBTester.INSTANCE.testSelectWithTable(
                "SELECT CITY,NAME,TITLE FROM OFFICES,SALESREPS WHERE MGR=EMPL_NUM;",
                table);
    }

    @Test
    public void test161_2() throws IOException {
        ResultsTable table=ResultsTable.readFromDataFile(
                new String[]{"NAME", "CITY", "REGION"},
                "tables/t161_2.dat", SEPARATOR);

        DBTester.INSTANCE.testSelectWithTable(
                "SELECT NAME,CITY,REGION FROM SALESREPS JOIN OFFICES ON REP_OFFICE=OFFICE;",
                table);
    }


    @Test
    public void test162() throws IOException {
        ResultsTable table=ResultsTable.readFromDataFile(
                new String[]{"CITY", "NAME" ,"TITLE"},
                "tables/t162.dat", SEPARATOR);

        DBTester.INSTANCE.testSelectWithTable(
                "SELECT CITY, NAME, TITLE FROM OFFICES JOIN SALESREPS ON MGR=EMPL_NUM",
                table);
    }


    @Test
    public void test162_2() throws IOException {
        ResultsTable table=ResultsTable.readFromDataFile(
                new String[]{"CITY", "NAME" ,"TITLE"},
                "tables/t162_2.dat", SEPARATOR);

        DBTester.INSTANCE.testSelectWithTable(
                "SELECT CITY, NAME, TITLE FROM OFFICES, SALESREPS WHERE MGR=EMPL_NUM AND TARGET>600000.00;",
                table);
    }


    @Test
    public void test163() throws IOException {
        ResultsTable table=ResultsTable.readFromDataFile(
                new String[]{"CITY", "NAME" ,"TITLE"},
                "tables/t163.dat", SEPARATOR);

        DBTester.INSTANCE.testSelectWithTable(
                "SELECT CITY, NAME, TITLE FROM OFFICES JOIN SALESREPS ON MGR=EMPL_NUM WHERE TARGET>600000.00;",
                table);
    }


    //-----------------------------------
    // And now the Full Auto tests
    @Test
    public void test163_2() {
        DBTester.INSTANCE.testSelectFullAuto("home2ee",
            "SELECT ORDER_NUM,AMOUNT,DESCRIPTION FROM ORDERS,PRODUCTS WHERE MFR=MFR_ID AND PRODUCT=PRODUCT_ID;");
    }

}
