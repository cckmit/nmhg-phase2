package twms.clubcar.integration.wm;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;

import twms.clubcar.integration.common.WSClient;
import twms.clubcar.integration.wm.extwtypricecheck.Tavant_ProcessPortType;
import twms.clubcar.integration.wm.extwtypricecheck.Tavant_ProcessService;
import twms.clubcar.integration.wm.extwtypricecheck.Tavant_ProcessServiceLocator;

public class ExtWarrantyPriceCheckWSClient extends WSClient {

	public ExtWarrantyPriceCheckWSClient() {
		fileName = "Ext-Warranty-Price-Check-Request.xml";
		syncType = "webmethods\\xml\\extwarrantypricecheck";
		urlFragment = "/soap/rpc";
		hostname = "localhost";    port = "9095";
		//hostname="10.34.131.70";port="5677";
	}

	public static void main(String[] args) {
		ExtWarrantyPriceCheckWSClient service = new ExtWarrantyPriceCheckWSClient();
		service.service();
	}

	public void service() {
		super.service();
		Tavant_ProcessService service = new Tavant_ProcessServiceLocator();
		try {
			Tavant_ProcessPortType webServiceInterface = service
					.getTavant_ProcessPort0(new URL(url));
			Stub tempStub = (Stub) webServiceInterface;
			tempStub.setTimeout(999999999);
			tempStub.setUsername(username);
			tempStub.setPassword(password);
			String response = webServiceInterface.processExtendedWarrantyPriceCheckRequest(xml);
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
