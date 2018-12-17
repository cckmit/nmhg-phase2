package twms.clubcar.integration.is;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;

import twms.clubcar.integration.common.WSClient;
import twms.clubcar.integration.is.creditNotification.$Proxy205;
import twms.clubcar.integration.is.creditNotification.ProcessCreditNotificationWS_Service;
import twms.clubcar.integration.is.creditNotification.ProcessCreditNotificationWS_ServiceLocator;

public class CreditNotificationWSClient extends WSClient {

	
	CreditNotificationWSClient(){
		urlFragment = "/services/ProcessCreditNotification-WS";
		fileName = "Creditnotification.xml";
		syncType = "server\\xml\\creditnotification";

		hostname = "localhost";  port = "8086";
		//hostname = "blrirap01.in.corp.tavant.com"; port=8086
		//  UAT (Tavant IP)
		//hostname = "192.168.44.34" ; port="8086";
	}
	public static void main(String[] args) {
		CreditNotificationWSClient service = new CreditNotificationWSClient();
		service.service();
	}
	
	
	public void service(){
		super.service();
		ProcessCreditNotificationWS_Service service = new ProcessCreditNotificationWS_ServiceLocator();
		try {
			$Proxy205 webServiceInterface = service.getProcessCreditNotificationWS(new URL(url));
			Stub tempStub = (Stub) webServiceInterface;
			tempStub.setTimeout(999999999);
			tempStub.setUsername(username);
			tempStub.setPassword(password);
			String response = webServiceInterface.sync(xml);
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
