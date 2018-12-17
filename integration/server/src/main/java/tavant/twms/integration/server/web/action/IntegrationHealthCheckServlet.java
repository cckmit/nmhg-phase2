package tavant.twms.integration.server.web.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

@SuppressWarnings("serial")
public class IntegrationHealthCheckServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {

			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(
					"http://localhost:8086/services/HealthCheck?"));
			call.setOperationName(new QName("http://twms/integration", "echo"));
			call.invoke(new Object[] { "Health Check Test" });

		} catch (ServiceException e) {
			e.printStackTrace();
		}

	}
}
