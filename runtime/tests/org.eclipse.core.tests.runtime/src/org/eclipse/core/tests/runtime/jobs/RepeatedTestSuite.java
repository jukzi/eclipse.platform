package org.eclipse.core.tests.runtime.jobs;
import junit.framework.*;

/** can be manual started for testing random fails **/
public class RepeatedTestSuite extends TestCase {
    public static Test suite() {
        TestSuite suite = new TestSuite(RepeatedTestSuite.class.getName());

		for (int i = 0; i < 1000; i++) {
			suite.addTestSuite(OrderedLockTest.class); // the test to repeat
        }
        return suite;
    }
}