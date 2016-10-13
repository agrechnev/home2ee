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
        DBTester.INSTANCE.testSelectFullAuto(
            "SELECT ORDER_NUM,AMOUNT,DESCRIPTION FROM ORDERS,PRODUCTS WHERE MFR=MFR_ID AND PRODUCT=PRODUCT_ID;");
    }

    @Test
    public void test163_3() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT ORDER_NUM,AMOUNT,DESCRIPTION FROM ORDERS JOIN PRODUCTS ON MFR=MFR_ID AND" +
                        " PRODUCT=PRODUCT_ID;");
    }


    @Test
    public void test165() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT ORDER_NUM,AMOUNT,COMPANY,NAME FROM ORDERS,CUSTOMERS,SALESREPS WHERE CUST=CUST_NUM" +
                        " AND REP=EMPL_NUM AND AMOUNT>25000.00;");
    }

    @Test
    public void test165_2() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT ORDER_NUM,AMOUNT,COMPANY,NAME FROM ORDERS JOIN CUSTOMERS ON  CUST=CUST_NUM JOIN" +
                        " SALESREPS ON REP=EMPL_NUM WHERE AMOUNT>25000.00;");
    }


    @Test
    public void test166() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT ORDER_NUM,AMOUNT,COMPANY,NAME FROM ORDERS JOIN CUSTOMERS ON  CUST=CUST_NUM JOIN" +
                        " SALESREPS ON REP=EMPL_NUM WHERE AMOUNT>25000.00;");
    }

    @Test
    public void test167() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT ORDER_NUM,AMOUNT,COMPANY,NAME,CITY FROM ORDERS,CUSTOMERS,SALESREPS,OFFICES" +
                        " WHERE CUST=CUST_NUM AND CUST_REP=EMPL_NUM AND REP_OFFICE=OFFICE AND AMOUNT>25000.00;");
    }


    @Test
    public void test169() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT ORDER_NUM,AMOUNT,ORDER_DATE,NAME FROM ORDERS, SALESREPS WHERE ORDER_DATE=HIRE_DATE;");
    }


    @Test
    public void test170() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT NAME,QUOTA,CITY,TARGET FROM SALESREPS,OFFICES WHERE QUOTA>TARGET;");
    }


    @Test
    public void test171() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT CITY,SALES FROM OFFICES WHERE SALES>TARGET;");
    }


    @Test
    public void test172() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT NAME,SALES FROM SALESREPS WHERE SALES > 350000.00;");
    }

    @Test
    public void test172_2() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT NAME,SALESREPS.SALES,CITY FROM SALESREPS, OFFICES WHERE REP_OFFICE=OFFICE;");
    }


    @Test
    public void test173() {
        // Here we really really need this false: 2 columns with the same name !
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT * FROM SALESREPS, OFFICES WHERE REP_OFFICE=OFFICE;",false);
    }

    @Test
    public void test173_2() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT SALESREPS.*,CITY,REGION FROM SALESREPS, OFFICES WHERE REP_OFFICE=OFFICE;");
    }


    @Test
    public void test173_3() {
        // Note: The result set is empty for this test
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT * FROM SALESREPS WHERE MANAGER=EMPL_NUM;");
    }

    @Test
    public void test175() {
        // Two "NAME" columns: we need false here
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT EMPS.NAME, MGRS.NAME FROM SALESREPS EMPS, SALESREPS MGRS WHERE EMPS.MANAGER=MGRS.EMPL_NUM;",
                false);
    }


    @Test
    public void test175_2() {
        // Two "NAME" columns: we need false here
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT SALESREPS.NAME, MGRS.NAME FROM SALESREPS, SALESREPS MGRS" +
                        " WHERE SALESREPS.MANAGER=MGRS.EMPL_NUM;",
                false);
    }

    @Test
    public void test175_3() {
        // Two "QUOTA" columns: we need false here
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT SALESREPS.NAME, SALESREPS.QUOTA, MGRS.QUOTA FROM SALESREPS, SALESREPS MGRS" +
                        " WHERE SALESREPS.MANAGER=MGRS.EMPL_NUM AND SALESREPS.QUOTA>MGRS.QUOTA;",
                false);
    }


    @Test
    public void test176() {
        // Repeated column names: we need false here
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT EMPS.NAME,EMP_OFFICE.CITY,MGRS.NAME,MGR_OFFICE.CITY " +
                        "FROM SALESREPS EMPS, SALESREPS MGRS, OFFICES EMP_OFFICE, OFFICES MGR_OFFICE " +
                        "WHERE EMPS.REP_OFFICE = EMP_OFFICE.OFFICE AND MGRS.REP_OFFICE=MGR_OFFICE.OFFICE " +
                        "AND EMPS.MANAGER=MGRS.EMPL_NUM AND EMPS.REP_OFFICE<>MGRS.REP_OFFICE;",
                false);
    }


    @Test
    public void test179() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT NAME,CITY FROM SALESREPS,OFFICES;");
    }

    @Test
    public void test180() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT NAME,CITY FROM SALESREPS,OFFICES WHERE REP_OFFICE=OFFICE;");
    }

    @Test
    public void test180_2() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT COMPANY,ORDER_NUM,AMOUNT FROM CUSTOMERS JOIN ORDERS ON CUST_NUM=CUST " +
                        "WHERE CUST_NUM=2103 ORDER BY ORDER_NUM;");
    }


    @Test
    public void test182() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT NAME,REP_OFFICE FROM SALESREPS;");
    }

    @Test
    public void test182_2() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT NAME,REP_OFFICE FROM SALESREPS JOIN OFFICES ON REP_OFFICE=OFFICE;");
    }


    @Test
    public void test183() {
        DBTester.INSTANCE.testSelectFullAuto(
                "SELECT NAME,REP_OFFICE FROM SALESREPS LEFT OUTER JOIN OFFICES ON REP_OFFICE=OFFICE;");
    }


}
