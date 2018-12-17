package tavant.twms.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import tavant.twms.web.DynamicDataSourceContextHolder;

public class DynamicContextFilter implements Filter{
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) 
		throws IOException, ServletException {
		String requestURI = ((HttpServletRequest)servletRequest).getRequestURI();
		if (requestURI.contains("claimSearchSummaryBody") ||
			requestURI.contains("inventorySearchBody") ||
			requestURI.contains("get_partReturn_search_body") ||
			requestURI.contains("itemSearchBody") ||
			requestURI.contains("recoveryClaimSearchSummaryBody") )
			DynamicDataSourceContextHolder.setQuerySearchContext("SEARCH_QUERY");
		else if (requestURI.contains("downloadClaimData") || 
				requestURI.contains("downloadClaimReportData") || 
				requestURI.contains("downloadClaimFinancialReportData") || 
				requestURI.contains("downloadInventoryData") || 
				requestURI.contains("uploadTemplateData"))
			DynamicDataSourceContextHolder.setQuerySearchContext("REPORT_TASK");
		else
			DynamicDataSourceContextHolder.setQuerySearchContext("DEFAULT");
		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void init(FilterConfig arg0) throws ServletException {}
	public void destroy() {}
}
