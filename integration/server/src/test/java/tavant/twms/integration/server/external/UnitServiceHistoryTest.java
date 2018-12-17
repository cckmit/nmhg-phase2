package tavant.twms.integration.server.external;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import tavant.twms.integration.server.util.ReadWriteTextFile;

public class UnitServiceHistoryTest {
    
    public static void main(String[] args) {

        final String endPoint = "http://192.168.135.197:8086/services/GetUnitServiceHistory-WS?WSDL&method=";
        String requestXML = getRequestXML("SoapInput.xml");
                
                boolean success = true;
                if (success) {
                        String namespace = "http://www.tavant.com/dealerinterfaces/unitservicehistory/unitservicehistoryrequest";
                        String methodName = "execute";
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

        private static String getRequestXML(String xmlName) {
                // XML Location
                String sampleXML = "C:/WebServiceInput/"+xmlName;
                // Convert XML to Strin
                sampleXML = (String) ReadWriteTextFile.getContents(sampleXML);
                System.out.println("sampleXML = " + sampleXML);
                return sampleXML;
        }
        
        

}
