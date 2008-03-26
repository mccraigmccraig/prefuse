package test.prefuse.data.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class All_PrefuseDataParser_Tests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for test.prefuse.data.parser");
        //$JUnit-BEGIN$
        suite.addTestSuite(ParserFactoryTest.class);
        suite.addTestSuite(JavaDateArrayParserTest.class);
        //$JUnit-END$
        return suite;
    }

}
