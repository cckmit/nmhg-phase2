package twms.clubcar.integration.is;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;

import twms.clubcar.integration.common.WSClient;
import twms.clubcar.integration.is.extwtypricecheck.$Proxy206;
import twms.clubcar.integration.is.extwtypricecheck.ExtWarrantyPriceCheckWS_Service;
import twms.clubcar.integration.is.extwtypricecheck.ExtWarrantyPriceCheckWS_ServiceLocator;

public class ExtWarrantyPriceCheckWSClient extends WSClient {

	public ExtWarrantyPriceCheckWSClient() {
		fileName = "Ext-Warranty-Price-Check-Request.xml";
		syncType = "server\\xml\\extwarrantypricecheck";
		urlFragment = "/services/ExtWarrantyPriceCheck-WS";
		hostname = "localhost";    port = "8086";
		//hostname = "192.168.132.190"; port = "8086";
	}

	public static void main(String[] args) {
		ExtWarrantyPriceCheckWSClient service = new ExtWarrantyPriceCheckWSClient();
		service.service();
	}

	public void service() {
		super.service();
		ExtWarrantyPriceCheckWS_Service service = new ExtWarrantyPriceCheckWS_ServiceLocator();
		try {
			$Proxy206 webServiceInterface = service.getExtWarrantyPriceCheckWS(new URL(url));
					
			Stub tempStub = (Stub) webServiceInterface;
			tempStub.setTimeout(999999999);
			tempStub.setUsername(username);
			tempStub.setPassword(password);
			String response = webServiceInterface.sync(xml);
			System.out.println("*********** WS call sucessfull..  The response is .. *****");
			System.out.println(response);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
