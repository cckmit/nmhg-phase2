package twms.clubcar.integration.wm;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Stub;

import twms.clubcar.integration.common.WSClient;
import twms.clubcar.integration.wm.extwtydebitsubmit.Tavant_ProcessPortType;
import twms.clubcar.integration.wm.extwtydebitsubmit.Tavant_ProcessService;
import twms.clubcar.integration.wm.extwtydebitsubmit.Tavant_ProcessServiceLocator;

public class ExtWarrantyDebitSubmtWSClient extends WSClient {

	public ExtWarrantyDebitSubmtWSClient() {
		fileName = "creditsubmit.xml";
		//fileName="CreditSubmission_Updated.xml";
		syncType = "server\\xml\\creditsubmit";
		//urlFragment = "/invoke/Tavant.Process/processCreditSubmission";
		urlFragment = "/soap/rpc";

					hostname = "localhost";    port = "9095";
					//	hostname = "10.34.131.72"; port = "5556"; 
	}

	public static void main(String[] args) {
		ExtWarrantyDebitSubmtWSClient service = new ExtWarrantyDebitSubmtWSClient();
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
			String response = webServiceInterface.processExtendedWarrantyDebitSubmission(xml);
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
