package twms.clubcar.integration.wm;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;

import twms.clubcar.integration.common.WSClient;
import twms.clubcar.integration.wm.pricecheck.Tavant_ProcessPortType;
import twms.clubcar.integration.wm.pricecheck.Tavant_ProcessService;
import twms.clubcar.integration.wm.pricecheck.Tavant_ProcessServiceLocator;


public class PriceCheckWSClient extends WSClient {
	
	PriceCheckWSClient(){
		fileName = "OEMPriceCheck.xml";
		
		syncType = "webmethods\\xml\\pricecheck";
		urlFragment = "/soap/rpc";
		//urlFragment = "/invoke/ClubCar.Outbound.Tavant.PriceCheck.Services/processPriceCheck"; 

			//hostname = "localhost"; port = "9095";
			//		hostname = "10.34.131.72"; port = "5556";
		//hostname = "10.34.131.73"; port="5677";
		hostname = "10.34.131.70";port="5677";
	}

	
	public static void main(String[] args) {
		PriceCheckWSClient testWSService = new PriceCheckWSClient();
		testWSService.service();
	}
	
	
	protected void service() {
		super.service();
		Tavant_ProcessService service  = new Tavant_ProcessServiceLocator(); 
		try {
			Tavant_ProcessPortType webServiceInterface = service
					.getTavant_ProcessPort0(new URL(url));
			Stub tempStub = (Stub) webServiceInterface;
			tempStub.setTimeout(999999999);
			tempStub.setUsername(username);
			tempStub.setPassword(password);
			String response = webServiceInterface
					.processPriceCheck(xml);
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
