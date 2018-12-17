package tavant.twms.filter;

import java.io.IOException;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;


import org.springframework.util.StringUtils;

import tavant.twms.security.SelectedBusinessUnitsHolder;

public class SelectedBusinessUnitsFilter implements Filter {
	FilterConfig filterConfig;

	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
//		HttpServletRequest req = (HttpServletRequest) servletRequest;
//		String[] selectedBUNames = req.getParameterValues("selectedBusinessUnits");
//		if (selectedBUNames != null && selectedBUNames.length > 0) {
//			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(selectedBUNames);
//		} else {
//			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(null);
//		}
		HttpServletRequest req = (HttpServletRequest) servletRequest;
		String selectedBUName = req.getParameter("selectedBusinessUnit");
		if (StringUtils.hasText(selectedBUName)) {
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(selectedBUName);
		} else {
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(null);
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig=filterConfig;
	}

	public void destroy() {
	}

}