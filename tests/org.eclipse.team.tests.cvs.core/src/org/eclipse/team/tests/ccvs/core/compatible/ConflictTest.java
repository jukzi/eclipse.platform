package org.eclipse.team.tests.ccvs.core.compatible;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */
import junit.awtui.TestRunner;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.team.internal.ccvs.core.CVSException;
import org.eclipse.team.tests.ccvs.core.JUnitTestCase;

public class ConflictTest extends JUnitTestCase {

	SameResultEnv env1;
	SameResultEnv env2;
	
	public ConflictTest(String arg) {
		super(arg);
		env1 = new SameResultEnv(arg, getFile("checkout1"));
		env2 = new SameResultEnv(arg, getFile("checkout2"));
	}

	public static void main(String[] args) {	
		run(ConflictTest.class);
	}

	public void setUp() throws Exception {
		env1.setUp();
		env2.setUp();

		// Set the project to the content we need ...
		env1.magicSetUpRepo("proj2",new String[]{"a.txt","f1/b.txt","f1/c.txt"});
		env2.deleteFile("proj2");
	}
	
	public void tearDown() throws CVSException {
		env1.tearDown();
		env2.tearDown();
	}
	
	public static Test suite() {
		TestSuite suite = new TestSuite(ConflictTest.class);
		return new CompatibleTestSetup(suite);
	}
	
	public void testSimpleConflict() throws Exception {
		// Download content in two locations
		env1.execute("co",EMPTY_ARGS,new String[]{"proj2"},"");
		env2.execute("co",EMPTY_ARGS,new String[]{"proj2"},"");
		
		// change the file in both directories in a different way
		env1.appendToFile("proj2/f1/c.txt","AppendIt This");
		env2.appendToFile("proj2/f1/c.txt","AppendIt That");
		
		// commit changes of the first
		env1.execute("ci",new String[]{"-m","TestMessage"},new String[]{"proj2"},"");
		
		// load the changes into the changed file
		// and submit the merge
		env2.execute("update",EMPTY_ARGS,new String[]{"proj2"},"");
		
		// Make a change to the file in order to let the cvs-client know
		// that we solved the confilict
		env2.appendToFile("proj2/f1/c.txt","That's allright");		
		
		env2.execute("ci",new String[]{"-m","TestMessage"},new String[]{"proj2"},"");
	}
}

