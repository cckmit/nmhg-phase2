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
package tavant.twms.integration.server.web.action;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletResponseAware;
import tavant.twms.integration.server.common.dataaccess.SyncType;

/**
 * @author kapil.pandit
 *
 */
public abstract class AbstractIntegrationServerAction extends ActionSupport implements Preparable, ServletResponseAware{
    protected List<SyncType> syncTypeOptions;
    protected String syncType;

    protected Date fromDate;
    protected Date toDate;
    private HttpServletResponse response;
    
    public String getSyncType() {
        return syncType;
    }

    public void setSyncType(String syncType) {
        this.syncType = syncType;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public List<SyncType> getSyncTypeOptions() {
        return syncTypeOptions;
    }

    public void setSyncTypeOptions(List<SyncType> syncTypeOptions) {
        this.syncTypeOptions = syncTypeOptions;
    }

    public void prepare() throws Exception {

    }


    public HttpServletResponse getResponse() {
        return response;
    }

    public void setServletResponse(HttpServletResponse hsr) {
        this.response = hsr;
    }

    protected String writeJSONResponse(String jsonString) {
         response.setHeader("Pragma", "no-cache");
	     response.addHeader("Cache-Control", "must-revalidate");
	     response.addHeader("Cache-Control", "no-cache");
	     response.addHeader("Cache-Control", "no-store");
	     response.setDateHeader("Expires", 0);     
		
		response.setContentType("text/json-comment-filtered");
		try {
			response.getWriter().write(jsonString);
			response.flushBuffer();

			return null;
		} catch (IOException e) {
			String errorMessage = "Exception while writing JSON string \""
					+ jsonString + "\" to response :";
			throw new RuntimeException(errorMessage, e);
		}
    }
    
}
