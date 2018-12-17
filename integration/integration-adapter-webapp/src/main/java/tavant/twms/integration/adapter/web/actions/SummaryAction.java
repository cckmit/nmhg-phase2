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

package tavant.twms.integration.adapter.web.actions;

import tavant.twms.integration.adapter.util.PropertiesUtil;
import com.opensymphony.xwork2.ActionSupport;
import tavant.twms.integration.adapter.SyncTrackerDAO;
import tavant.twms.integration.adapter.SyncTrackerSearchCriteria;
import tavant.twms.integration.adapter.SummaryDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author vikas.a
 * 
 */
@SuppressWarnings("serial")
public class SummaryAction extends ActionSupport {

	SyncTrackerDAO syncTrackerDAO ;
	
	SyncTrackerSearchCriteria syncTrackerSearchCriteria = new SyncTrackerSearchCriteria();

	List<SummaryDTO> summaryList = new ArrayList<SummaryDTO>();
	
	String showMuleLogo = PropertiesUtil.getProperty("mule.displaylogo");

	public String display() throws Exception {
		return SUCCESS;
	}

	public String fetchSummary() throws Exception {
		
		setSummaryList(syncTrackerDAO.getSummary(
				syncTrackerSearchCriteria.getStartDate(), syncTrackerSearchCriteria
                .getEndDate()));

		return SUCCESS;
	}


	public void setStartDate(Date startDate) {
		syncTrackerSearchCriteria.setStartDate(startDate);
	}

	public void setEndDate(Date endDate) {
		syncTrackerSearchCriteria.setEndDate(endDate);
	}

	public Date getStartDate() {
		return syncTrackerSearchCriteria.getStartDate();
	}

	public Date getEndDate() {
		return syncTrackerSearchCriteria.getEndDate();
	}

	public List<SummaryDTO> getSummaryList() {
		return summaryList;
	}

	public void setSummaryList(List<SummaryDTO> summaryList) {
		this.summaryList = summaryList;
	}

	public SyncTrackerSearchCriteria getSyncSummaryCriteria() {
		return syncTrackerSearchCriteria;
	}

	public void setSyncSummaryCriteria(SyncTrackerSearchCriteria syncTrackerSearchCriteria) {
		this.syncTrackerSearchCriteria = syncTrackerSearchCriteria;
	}

	public void setSyncTrackerDAO(SyncTrackerDAO syncTrackerDAO) {
		this.syncTrackerDAO = syncTrackerDAO;
	}

	public String getShowMuleLogo() {
		return showMuleLogo;
	}

	public SyncTrackerSearchCriteria getSyncTrackerSearchCriteria() {
		return syncTrackerSearchCriteria;
	}

	public void setSyncTrackerSearchCriteria(
			SyncTrackerSearchCriteria syncTrackerSearchCriteria) {
		this.syncTrackerSearchCriteria = syncTrackerSearchCriteria;
	}
	

	
}
