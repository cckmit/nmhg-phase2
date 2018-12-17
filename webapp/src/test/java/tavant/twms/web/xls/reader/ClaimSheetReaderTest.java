package tavant.twms.web.xls.reader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork2.ObjectFactory;

import tavant.twms.infra.WebappRepositoryTestCase;

public class ClaimSheetReaderTest extends WebappRepositoryTestCase {

    public void testReadingClaimSheet() throws Exception {
        ApplicationContextHolder.getInstance().setContext(applicationContext);
        ObjectFactory.setObjectFactory(new ObjectFactory());
        InputStream claims = this.getClass().getResourceAsStream("claim.xls");
        InputStream mapping = this.getClass().getResourceAsStream("claim-mapping.xml");
        Reader claimsSheetReader = Utility.getReaderFromXML(mapping);
        Map<String, List<ConversionResult>> result = claimsSheetReader.read(claims);
        List<ConversionResult> results = result.get("Machine");
        assertNotNull(results);
        assertEquals(3, results.size());
//        assertFalse(results.get(0).hasConversionErrors());
//        assertFalse(results.get(1).hasConversionErrors());
//        assertTrue(results.get(2).hasConversionErrors());
    }

}
