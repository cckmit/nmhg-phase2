package tavant.twms.web.xls.reader;

import java.io.InputStream;

import junit.framework.TestCase;

public class InvUploadTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testCreate() throws Exception {
        InputStream input = this.getClass().getResourceAsStream("excel-impl-test.xls");
        InputStream config = this.getClass().getResourceAsStream("mapping.xml");
        Reader reader = Utility.getReaderFromXML(config);
        Object result = reader.read(input);

    }

}
