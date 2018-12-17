package tavant.twms.integration.server.inbound;

import javax.xml.namespace.QName;
import java.net.URL;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import tavant.twms.integration.server.util.ReadWriteTextFile;

public class GlobalSyncTest {

	public static void main(String[] args) {

        //Itemsync
        //String endPoint = getURL("DEV", "ItemSync");
        //String requestXML = getRequestXML("ItemSync","Item_Sync_Part.xml");
        //String requestXML = getRequestXML("ItemSync","ItemSync_Machine.xml");
        //String namespace = "http://www.tavant.com/globalsync/itemsync/definition";


        String endPoint = getURL("DEV","InstallBase");
        String requestXML = getRequestXML("InstallBase","InstallBase_Machine.xml");
        /*String requestXML = getRequestXML("InstallBase","InstallBaseSync_DropShipment.xml");
        String requestXML = getRequestXML("InstallBase","InstallBase_NA_Machine.xml");*/
        String namespace = "http://www.tavant.com/globalsync/installbasesync/definition";

        //CurrencyExchangeRate
        //String endPoint = getURL("DEV", "ExchangeRate");
        //String requestXML = getRequestXML("ExchangeRate", "ExchangeRateSync.xml");
        //String namespace = "http://www.tavant.com/globalsync/exchangeratesync/definition";

        //ExtendedWarrantyPurchaseNotification
        //String endPoint = getURL("DEV", "ExtendedWarrantyPurchaseNotification");
        //String requestXML = getRequestXML("ExtendedWarrantyPurchaseNotification","ExtendedWarrantyPurchaseNotification.xml");
        //String namespace = "http://www.tavant.com/globalsync/extwarrantynotification/definition";

        //WarrantyClaimCreditNotification
        //String endPoint = getURL("DEV", "creditnotification");
        //String requestXML = getRequestXML("creditnotification","WarrantyClaimCreditNotification.xml");
        //String namespace = "http://www.tavant.com/globalsync/warrantyclaimcreditnotification/definition";

        //Customer Sync
        /*String endPoint = getURL("DEV", "CustomerSync");
        String requestXML = getRequestXML("CustomerSync", "DealerSync.xml");
        //String requestXML = getRequestXML("CustomerSync", "SupplierSync.xml");
        String namespace = "http://www.tavant.com/globalsync/customersync/definition";
*/
        System.out.println(endPoint);
        // Tavant WSDL Method
        String methodName = "sync";
        try {
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(endPoint));
            call.setOperationName(new QName(namespace, methodName));
            String response = (String) call
                    .invoke(new Object[]{requestXML});

            if (null != response) {
                System.out.println("response: " + response);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

	private static String getURL(String env, String syncType) {
		// Default URL is DEV
		String webService = "";
		if(syncType.equalsIgnoreCase("creditnotification")){
			webService = "ProcessGlobalCreditNotification-WS";
		}
		else if(syncType.equalsIgnoreCase("ItemSync")){
			webService = "ProcessGlobalItemSync-WS";
		}
		else if(syncType.equalsIgnoreCase("CustomerSync")){
			webService = "ProcessGlobalCustomerSync-WS";
		}
		else if(syncType.equalsIgnoreCase("InstallBase")){
			webService = "ProcessGlobalInstallBaseSync-WS";
		}
		else if(syncType.equalsIgnoreCase("ExtWarrantyPurchaseNotification")){
			webService = "processGlobalExtWarrantyPurchaseNotification-WS";
		}
		else if(syncType.equalsIgnoreCase("ExchangeRate")){
			webService = "ProcessGlobalExchangeRate-WS";
		}
		
		String url = null;
		if (env.equalsIgnoreCase("DEV")) {
			url = "http://localhost:9090/services/integration/"+webService+"?method=";
		} else if (env.equalsIgnoreCase("QA")) {
			url = "http://192.168.146.96:8080/services/integration/"+webService+"?method=";
		}
		return url;
	}

	private static String getRequestXML(String syncType,String xmlName) {
		// XML Location
		String sampleXMLFileName = "D:/Codebase/NMHG_DEV/integration/server/src/test/resources/server/xml/"+syncType+"/"+xmlName;
		System.out.println(sampleXMLFileName);
		// Convert XML to String
		String sampleXML = ReadWriteTextFile.getContents(sampleXMLFileName);
		return sampleXML;
	}
}