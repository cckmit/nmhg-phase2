package tavant.twms.web.common;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import tavant.twms.common.HealthCheckService;

@SuppressWarnings("serial")
public class HealthCheckServlet extends HttpServlet {
	private static final Logger LOGGER = Logger
			.getLogger(HealthCheckServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		WebApplicationContext webApplicationContext = WebApplicationContextUtils
				.getWebApplicationContext(this.getServletContext());
		HealthCheckService healthCheckService = (HealthCheckService) webApplicationContext
				.getBean("healthCheckService");
		try {
			healthCheckService.checkConnectivity();
			resp.setStatus(HttpServletResponse.SC_OK);

			resp.setContentType("text/html");

			PrintWriter out = resp.getWriter();

			out.println("<HEAD><TITLE>HEALTH CHECK</TITLE></HEAD><BODY>");
	        out.println("<h1>The App server and DB sever are Healthy</h1>");
	        out.println("</BODY>");
	        out.close();

		} catch (Exception e) {
			LOGGER.error("un healthy :" + e.getMessage(), e);
			resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, e
					.getMessage());
		}
	}

}
