package tavant.twms.integration.server.external;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import tavant.twms.integration.server.util.ReadWriteTextFile;

public class PriceFetchRequestTest {

	public static void main(String[] args) {

		String env = "QA2";
		// Web Methods URL 
		String endPoint = getURL(env);
		// Web Methods Request XML
		String requestXML = getRequestXML();
		// Web Method NameSpace
		String wmNamespace = "http://www.webmethods.com/TavantWMS.Inbound";
		// Web Method WSDL Method
		String methodName = "processPriceFetch";
		//String methodName = "sync";
		// In Param
		String inParam = "inData";
		// Out Param
		String outParam = "outData";
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new URL(endPoint));
			call.setUsername("Tavantuser");
			call.setPassword("Tavant$123");
			call.setOperationName(new QName(wmNamespace, methodName));
			call.addParameter(new QName(wmNamespace, inParam), new QName(wmNamespace, inParam), ParameterMode.IN);
			call.setReturnType(new QName(wmNamespace, outParam));
//			call.setOperationName(methodName);
			call.setTimeout(12000);
			String responose = (String) call.invoke(new Object[] { requestXML });
			
			System.out.println("Response Received ::::"+responose);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static String getURL(String env) {
		// Default URL is DEV
		String url = "http://10.80.237.36:5677/soap/rpc";
		if (env.equalsIgnoreCase("DEV")) {
			url = "http://10.80.237.36:5677/soap/rpc";
		} else if (env.equalsIgnoreCase("QA1")) {
			url = "http://10.80.237.82:5677/soap/rpc";		
		} else if (env.equalsIgnoreCase("QA2")) {
			url = "http://10.34.131.70:5677/soap/rpc";
		}
		return url;
	}
	
	private static String getRequestXML(){
		// XML Location
		String sampleXML = "D:/svn-root/TWMS_IRI_HUS_DEV/integration/server/src/test/resources/server/xml/pricecheck/PriceFetch_IRI_CostCheck.xml";
		// Convert XML to String
		sampleXML = (String) ReadWriteTextFile.getContents(sampleXML);
		return sampleXML;
	}
	
}
