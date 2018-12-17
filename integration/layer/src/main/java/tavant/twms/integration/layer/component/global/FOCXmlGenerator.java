package tavant.twms.integration.layer.component.global;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;

import org.apache.xmlbeans.XmlOptions;

import tavant.globalsync.foc.ApplicationAreaType;
import tavant.globalsync.foc.FocDocument;
import tavant.globalsync.foc.HussmannPart;
import tavant.globalsync.foc.ReplacedPartConfiguration;
import tavant.globalsync.foc.SenderType;
import tavant.globalsync.foc.FocDocument.Foc;
import tavant.twms.claim.FocBean;

public class FOCXmlGenerator {

	public static void main(String[] args) throws IOException {
		FocDocument focDocument = FocDocument.Factory.newInstance();
		focDocument.addNewFoc();

		Foc foc = focDocument.getFoc(); 
		// Create application area
		ApplicationAreaType applicationAreaType = ApplicationAreaType.Factory
				.newInstance(); 
        SenderType senderTypeDto = SenderType.Factory.newInstance();
        senderTypeDto.setLogicalId("1");
        senderTypeDto.setTask("FOC");
        applicationAreaType.setSender(senderTypeDto);
        applicationAreaType.setCreationDateTime(Calendar.getInstance());
        foc.setApplicationArea(applicationAreaType);               
        foc.addNewDataArea();
        foc.getDataArea().setCompanyId("111111");
        foc.getDataArea().setCompanyName("Hussman Branch");
        
        foc.getDataArea().setSerialNumber("PT0675473890");
        foc.getDataArea().setRepairDate(Calendar.getInstance());
        
        foc.getDataArea().setWorkOrderNumber("21");
        foc.getDataArea().setFailureDate(Calendar.getInstance());
        
        foc.getDataArea().setCausalPartNumber("AM1004");
        foc.getDataArea().setFailureFound("Twisted");
        
        foc.getDataArea().setCausedBy("High Pressure");
        foc.getDataArea().setFaultCode("CC-A-1004");
        
        
/*        ReplacedPartConfiguration[] arr =  new ReplacedPartConfiguration[2];                
        ReplacedPartConfiguration replacedPartConfiguration1 = createReplacedConfig();                
        ReplacedPartConfiguration replacedPartConfiguration2 = createAnotherReplacedConfig();
        arr[0] = replacedPartConfiguration1;
        arr[1] = replacedPartConfiguration2;*/
        //foc.getDataArea().setReplacedPartConfigsArray(arr);
                
        FocBean  focBean = new FocBean();
        focBean.setSerialNumber("AD0103981961");
        focBean.setOrderNo("181");
        File file = new File("C:\\foc.xml");                
        String xml = focDocument.xmlText(createXMLOptions());
        
        FileWriter fos  = null;
        fos = new FileWriter(file);
        fos.append(xml);
        fos.flush();
        fos.close();
        
	}

	private static ReplacedPartConfiguration createReplacedConfig() {
		HussmannPart hussmanReplacedPart = HussmannPart.Factory.newInstance();
		HussmannPart[] hussmanReplacedParts = new HussmannPart[1];
		
        hussmanReplacedPart.setPartnumber("RPart1");        
        hussmanReplacedPart.setQuantity(BigInteger.valueOf(1l));  
        
        HussmannPart hussmanInstalledPart = HussmannPart.Factory.newInstance();        
        HussmannPart[] hussmanInstalledParts = new HussmannPart[1];
        hussmanInstalledParts[0] = hussmanInstalledPart;
        
        hussmanInstalledPart.setPartnumber("IPart1");        
        hussmanInstalledPart.setQuantity(BigInteger.valueOf(2l));                
                 

        

        

        ReplacedPartConfiguration replacedPartConfiguration = ReplacedPartConfiguration.Factory.newInstance();
        
        replacedPartConfiguration.setHussmannReplacedPartsArray(hussmanReplacedParts);
        replacedPartConfiguration.setHussmannInstalledPartsArray(hussmanInstalledParts);
		return replacedPartConfiguration;
	}
	
	private static ReplacedPartConfiguration createAnotherReplacedConfig() {
		HussmannPart hussmanReplacedPart = HussmannPart.Factory.newInstance();
        
		HussmannPart[] hussmanReplacedParts = new HussmannPart[1];
        hussmanReplacedPart.setPartnumber("RPart2");                
        hussmanReplacedPart.setQuantity(BigInteger.valueOf(2l));
        hussmanReplacedParts[0] = hussmanReplacedPart;
        
                
        HussmannPart[] hussmanInstalledParts = new HussmannPart[2];
        
        HussmannPart hussmanInstalledPart1 = HussmannPart.Factory.newInstance();
        hussmanInstalledParts[0] = hussmanInstalledPart1;        
        hussmanInstalledPart1.setPartnumber("IPart1");        
        hussmanInstalledPart1.setQuantity(BigInteger.valueOf(2l));

        HussmannPart hussmanInstalledPart2 = HussmannPart.Factory.newInstance();
        hussmanInstalledParts[1] = hussmanInstalledPart2;        
        hussmanInstalledPart2.setPartnumber("IPart2");        
        hussmanInstalledPart2.setQuantity(BigInteger.valueOf(2l));        
        
        
/*        NonHussmanPart[] nonHussParts = new NonHussmanPart[2];
        NonHussmanPart nonHussPart1 = NonHussmanPart.Factory.newInstance();                
        nonHussParts[0] = nonHussPart1;        
        nonHussPart1.setPartnumber("NPart1");
        nonHussPart1.setDescription("Non Business Part");        
        nonHussPart1.setPrice("10");
        nonHussPart1.setQuantity(BigInteger.valueOf(1l));
        
        
        NonHussmanPart nonHussPart2 = NonHussmanPart.Factory.newInstance();                
        nonHussParts[1] = nonHussPart2;        
        nonHussPart2.setPartnumber("NPart2");
        nonHussPart2.setDescription("Non Business Part");        
        nonHussPart2.setPrice("20");
        nonHussPart2.setQuantity(BigInteger.valueOf(1l));*/

        ReplacedPartConfiguration replacedPartConfiguration = ReplacedPartConfiguration.Factory.newInstance();
        
        replacedPartConfiguration.setHussmannReplacedPartsArray(hussmanReplacedParts);
        replacedPartConfiguration.setHussmannInstalledPartsArray(hussmanInstalledParts);
        /*replacedPartConfiguration.setNonHussmanPartsArray(nonHussParts);*/
		return replacedPartConfiguration;
	}
	
	private static XmlOptions createXMLOptions() {
		// Generate the XML document
		XmlOptions xmlOptions = new XmlOptions();
		xmlOptions.setSavePrettyPrint();
		xmlOptions.setSavePrettyPrintIndent(4);
		xmlOptions.setSaveAggressiveNamespaces();
		xmlOptions.setUseDefaultNamespace();
		return xmlOptions;
	}
}
