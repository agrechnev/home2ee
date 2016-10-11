package agrechnev;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * My SQL tests, part of Java EE Homework assignment 2
 * Uses DBTester
 * Created by Oleksiy Grechnyev
 * */

public class RunDBTests {
    @Test
    public void test_user(){
        DBTester.INSTANCE.testSelect("SELECT user();",rs -> {
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getString("user()"),"agrechnev@localhost");
            Assert.assertFalse(rs.next());
        });
    }


    @Test
    public void test158() throws IOException {
        ResultsTable table=ResultsTable.readFromFile(
             new String[]{"order_num","amount","company","credit_limit"},
                "tables/ttt.dat","\\|");

        DBTester.INSTANCE.testSelectWithTable(
                "SELECT order_num,amount,company,credit_limit FROM orders,customers WHERE cust=cust_num;",
                table);
    }
}
