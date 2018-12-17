/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.web;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ParameterAware;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.json.JSONArray;
import tavant.twms.infra.ListCriteria;
import tavant.twms.web.actions.TwmsActionSupport;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author <a href="radhakrishnan.j@tavant.com">radhakrishnan.j</a>
 * @date Sep 19, 2006
 */
public abstract class AbstractListAction extends TwmsActionSupport implements ServletRequestAware,
        ServletResponseAware, ParameterAware {
    protected static final String SORT_DESCENDING = "dsc";
    protected static Logger logger = LogManager.getLogger(AbstractListAction.class);
    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected ServletContext servletContext;
    protected List<ColumnDefinition> columns = new ArrayList<ColumnDefinition>();
    protected String jsonString;
    protected Map<String, String> filters = new HashMap<String, String>(7);
    protected List<String[]> sorts = new ArrayList<String[]>(7);
    protected int page = 0;

    public int getPage() {
        return page;
    }

    public void setPage(int currentPage) {
        this.page = currentPage;
    }

    @SuppressWarnings("unchecked")
    public void setParameters(Map params) {
        Map<String,String[]>map = (Map<String,String[]>)params;
        if (logger.isInfoEnabled()) {
            logger.info("Parameter values bound to request :");
            for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
                String key =iter.next();
                String[] values = map.get(key);
                for (int i = 0; i < values.length; i++) {
                    logger.info(key + "[" + i + "]=" + values[i]);
                }
            }
        }

        String[] page = map.get("page");
        if (page != null) {
            this.page = Integer.parseInt(page[0]); // TODO: Errors!
            if( logger.isInfoEnabled() ) {
                logger.info("current page is ["+page+"]");
            }
        }

        for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
            String key =iter.next();
            if (key.startsWith("column")) {
                if (key.length() > "column".length()) {
                    String index = key.substring("column".length());
                    if (map.containsKey("filter" + index)) {
                        filters.put(map.get(key)[0], map.get("filter" + index)[0]);
                    }
                }
            } else if (key.startsWith("sort")) {
                if (key.length() > "sort".length()) {
                    String index = key.substring("sort".length());
                    if (map.containsKey("as" + index)) {
                        sorts.add(new String[] {map.get(key)[0], map.get("as" + index)[0]});
                    }
                }
            }
        }
    }

    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
    
    public void setColumns(List<ColumnDefinition> columns) {
        this.columns = columns;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }

    public List<String[]> getSorts() {
        return sorts;
    }

    public void setSorts(List<String[]> sorts) {
        this.sorts = sorts;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonColumns) {
        this.jsonString = jsonColumns;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public Map getSession() {
        return session;
    }

    protected void addSortCriteria(ListCriteria criteria) {
        for (Iterator<String[]> iter = sorts.iterator(); iter.hasNext();) {
            String[] sort = iter.next();
            String sortOnColumn = sort[0];
            boolean ascending = sort[1].equals(SORT_DESCENDING) ? false : true;
            if (logger.isInfoEnabled()) {
                logger.info("Adding sort criteria " + sortOnColumn + " "
                        + (ascending ? "ascending" : "descending"));
            }
            criteria.addSortCriteria(sortOnColumn, ascending);
        }
    }

    protected void addFilterCriteria(ListCriteria criteria) {
        for (Iterator<String> iter = filters.keySet().iterator(); iter.hasNext();) {
            String filterName = iter.next();
            String filterValue = filters.get(filterName);
            if (logger.isInfoEnabled()) {
                logger.info("Adding filter criteria " + filterName + " : " + filterValue);
            }
            criteria.addFilterCriteria(filterName, filterValue);
        }
    }

    protected Object getProperty(Object item, String propertyName) {
        try {
            return PropertyUtils.getProperty(item, propertyName);
        } catch (IllegalAccessException e) {
            logger.error("Error encountered while fetching value of property"
                    + propertyName + " of object" + item, e);
        } catch (InvocationTargetException e) {
            logger.error("Error encountered while fetching value of property"
                    + propertyName + " of object" + item, e);
        } catch (NoSuchMethodException e) {
            logger.error("Error encountered while fetching value of property"
                    + propertyName + " of object" + item, e);
        }
        return null;
    }
    

    /**
     * @param response
     */
    @SuppressWarnings("unchecked")    
    protected void addColumnHeaders(JSONArray response) {
        JSONArray data = new JSONArray();
        
        for(ColumnDefinition aColumnDefn : columns ) {
            Map newColumn = new HashMap();
            newColumn.put("name", aColumnDefn.getDisplayName());
            newColumn.put("field", aColumnDefn.getName());
            newColumn.put("dataType", aColumnDefn.getType());
            data.put(newColumn);
        }
        
        response.put(data);
    }
}
