package agrechnev;

import org.junit.Assert;
import org.junit.Test;

/**
 * My SQL tests, part of Java EE Homework assignment 2
 * Uses DBTester
 * Created by Oleksiy Grechnyev
 * */

public class RunDBTests {
    @Test
    public void test1(){
        DBTester.INSTANCE.testSelect("SELECT user();",rs -> {
            Assert.assertTrue(rs.next());
            Assert.assertEquals(rs.getString("user()"),"agrechnev@localhost");
            Assert.assertFalse(rs.next());
        });
    }
}
