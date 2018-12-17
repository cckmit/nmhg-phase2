package tavant.twms.web;

public class DynamicDataSourceContextHolder {
	private static final ThreadLocal<String> contextHolder =
        new ThreadLocal<String>();
   
	public static void setQuerySearchContext(String querySearchContext) {
	  contextHolder.set(querySearchContext);
	}
	
	public static Boolean isQuerySearchContext() {
		if (contextHolder.get() != null && contextHolder.get().contains("SEARCH_QUERY"))
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}
	
	public static Boolean isReportTaskingContext() {
		if (contextHolder.get() != null && contextHolder.get().contains("REPORT_TASK"))
			return Boolean.TRUE;
		else
			return Boolean.FALSE;
	}
	
	public static void clearQuerySearchContext() {
	  contextHolder.remove();
	}
}
