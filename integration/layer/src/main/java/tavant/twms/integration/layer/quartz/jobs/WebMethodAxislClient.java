package tavant.twms.integration.layer.quartz.jobs;

import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import tavant.twms.integration.layer.IntegrationPropertiesBean;
import tavant.twms.jbpm.infra.BeanLocator;

public class WebMethodAxislClient {

	private IntegrationPropertiesBean integrationPropertiesBean;
	
	private final BeanLocator beanLocator = new BeanLocator();
	
	public String makeCall(String url, String bodXML, String methodName,
			String inParam, String outParam) throws ServiceException,
			MalformedURLException, RemoteException {
		String wmNamespace = "http://www.webmethods.com/Tavant.Process";
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.setTargetEndpointAddress(new java.net.URL(url));
		call.setOperationName(new QName(wmNamespace, methodName));
		call.addParameter(new QName(wmNamespace, inParam), new QName(
				wmNamespace, inParam), ParameterMode.IN);
		call.setReturnType(new QName(wmNamespace, outParam));
		Object responseObj = call.invoke(new Object[] { bodXML });
		if (responseObj == null) {
			return null;
		}
		String response = responseObj.toString();
		int index = response.indexOf("SUCCESS");
		if (index > 0) {
			return null;
		} else {
			return response;
		}
	}
	
	public String makeCallWithNameSpace(String url, String bodXML, String methodName,
			String inParam, String outParam,String wmNamespace) throws ServiceException,
			MalformedURLException, RemoteException {
		integrationPropertiesBean = (IntegrationPropertiesBean) this.beanLocator.lookupBean("integrationPropertiesBean");
		Service service = new Service();
		Call call = (Call) service.createCall();
		call.setTargetEndpointAddress(new java.net.URL(url));
		call.setOperationName(new QName(wmNamespace, methodName));
		call.addParameter(new QName(wmNamespace, inParam), new QName(
				wmNamespace, inParam), ParameterMode.IN);
		call.setReturnType(new QName(wmNamespace, outParam));
		call.setUsername(integrationPropertiesBean.getWebmethodsUserName());
		call.setPassword(integrationPropertiesBean.getWebmethodsPassword());
		call.setTimeout(60000);
		Object responseObj = call.invoke(new Object[] { bodXML });
		if (responseObj == null) {
			return null;
		}
		String response = responseObj.toString();
		int index = response.indexOf("SUCCESS");
		if (index > 0) {
			return null;
		} else {
			return response;
		}
	}


	public void setIntegrationPropertiesBean(
			IntegrationPropertiesBean integrationPropertiesBean) {
		this.integrationPropertiesBean = integrationPropertiesBean;
	}
}
