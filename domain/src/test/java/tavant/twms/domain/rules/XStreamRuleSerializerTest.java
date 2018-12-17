package tavant.twms.domain.rules;

import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.springframework.core.io.ClassPathResource;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;

public class XStreamRuleSerializerTest extends TestCase {

    public void testXStreamSerialization() throws Exception {
        RuleSerializer xmlSerializer = new XStreamRuleSerializer();
        DomainSpecificVariable travelLocation = new DomainSpecificVariable(Claim.class,
                        "claim.serviceInformation.serviceDetail.travelDetails.location",
                        BusinessObjectModelFactory.CLAIM_RULES);
        Constant MADRID = new Constant("madrid", Type.STRING);
        Equals checkIfTravelLocationIsMadrid = new Equals(travelLocation, MADRID);
        String domainPredicateName = "Travel Location is Madrid";

        DomainPredicate ifTravelLocationIsMadrid = new DomainPredicate(domainPredicateName,
                checkIfTravelLocationIsMadrid);

        OGNLExpressionGenerator generator = new OGNLExpressionGenerator();
        ifTravelLocationIsMadrid.accept(generator);

        String expected = "claim.serviceInformation.serviceDetail.travelDetails.location.toLowerCase()" +
                ".equals(\"madrid\".toLowerCase())";
        assertEquals(expected, generator.getExpressionString());

        String toXML = xmlSerializer.toXML(ifTravelLocationIsMadrid);
        assertNotNull(toXML);
        
        ClassPathResource classPathResource = new ClassPathResource("XStreamRuleSerializerTest.xml",getClass());
        InputStreamReader expectation = new InputStreamReader( classPathResource.getInputStream());
        Diff xmlDiff = XMLUnit.compareXML(expectation,toXML);
        assertTrue(xmlDiff.identical());
        assertNotNull(xmlSerializer.fromXML(toXML));
    }
}
