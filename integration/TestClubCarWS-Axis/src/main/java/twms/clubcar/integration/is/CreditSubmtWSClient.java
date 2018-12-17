package twms.clubcar.integration.is;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;

import twms.clubcar.integration.common.WSClient;
import twms.clubcar.integration.is.creditsubmit.$Proxy207;
import twms.clubcar.integration.is.creditsubmit.ProcessCreditSubmitWS_Service;
import twms.clubcar.integration.is.creditsubmit.ProcessCreditSubmitWS_ServiceLocator;


public class CreditSubmtWSClient extends WSClient{
	
	CreditSubmtWSClient(){
    	fileName = "creditsubmit.xml";
    	syncType = "server\\xml\\creditsubmit";
    	urlFragment = "/services/ProcessCreditSubmit-WS";

    	hostname = "localhost";  	port = "8086";
//    	hostname = "blrirap01.in.corp.tavant.com"; port = "8086"; 
    }
	
	public static void main(String[] args) {
		CreditSubmtWSClient service = new CreditSubmtWSClient();
		service.service();
	}
	
	
	public void service(){
		super.service();
		ProcessCreditSubmitWS_Service service = new ProcessCreditSubmitWS_ServiceLocator();
		try {
			$Proxy207 webServiceInterface = service
					.getProcessCreditSubmitWS(new URL(url));
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