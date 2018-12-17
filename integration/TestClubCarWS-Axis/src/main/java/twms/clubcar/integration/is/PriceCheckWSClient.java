package twms.clubcar.integration.is;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;

import twms.clubcar.integration.common.WSClient;
import twms.clubcar.integration.is.pricecheck.$Proxy207;
import twms.clubcar.integration.is.pricecheck.PriceCheckWS_Service;
import twms.clubcar.integration.is.pricecheck.PriceCheckWS_ServiceLocator;

public class PriceCheckWSClient extends WSClient {

	PriceCheckWSClient(){
		urlFragment = "/services/PriceCheck-WS";
		fileName = "testpricecheck.xml";
		syncType = "server\\xml\\pricecheck";

		//hostname = "localhost";		port = "8086";
		//hostname = "blrirap01.in.corp.tavant.com"; port = "8086"
		hostname = "/192.168.132.190"; port = "8086";

	}

	public static void main(String[] args) {
		PriceCheckWSClient testWSService = new PriceCheckWSClient();
		testWSService.service();
	}
	
	protected void service() {
		super.service();
		PriceCheckWS_Service service = new PriceCheckWS_ServiceLocator();
		try {
			$Proxy207 webServiceInterface = service.getPriceCheckWS(new URL(url));
					
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
