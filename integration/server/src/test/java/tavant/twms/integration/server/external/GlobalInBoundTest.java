package tavant.twms.integration.server.external;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import tavant.twms.integration.server.util.ReadWriteTextFile;

public class GlobalInBoundTest {

	public static void main(String[] args) {

		String endPoint = "http://192.168.135.145:8086/services/ProcessGlobalExchangeRate-WS?WSDL&method=";
		String requestXML = getRequestXML("ExchangeRate","ExchangeRate.xml");
			
			boolean success = true;
			if (success) {
				String namespace = "http://www.tavant.com/oagis";
				String methodName = "sync";
				try {
					Service service = new Service();
					Call call = (Call) service.createCall();
					call.setTargetEndpointAddress(new URL(endPoint));
					call.setOperationName(new QName(namespace, methodName));
					String response = (String) call
							.invoke(new Object[] { requestXML });

					if (null != response) {
						System.out.println("response" + response);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}

		private static String getRequestXML(String syncType,String xmlName) {
			// XML Location
			String sampleXML = "D:/TWMS_IRI_HUSS_DEV/integration/server/src/test/resources/server/xml/"+syncType+"/"+xmlName;
			// Convert XML to String
			sampleXML = (String) ReadWriteTextFile.getContents(sampleXML);
			return sampleXML;
		}
}
