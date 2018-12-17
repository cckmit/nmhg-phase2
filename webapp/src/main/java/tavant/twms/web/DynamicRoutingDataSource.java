package tavant.twms.web;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource{	

	@Override
	protected Object determineCurrentLookupKey() {
		if (DynamicDataSourceContextHolder.isQuerySearchContext())
			return "SEARCH_QUERY";
		else if (DynamicDataSourceContextHolder.isReportTaskingContext())
			return "REPORT_TASK";
		else
			return "DEFAULT";
	}
}
