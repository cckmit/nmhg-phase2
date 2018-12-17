package tavant.twms.integration.web.actions;

import java.util.Collection;
import java.util.Date;

import tavant.twms.integration.layer.interceptor.RemoteInteractionDao;
import tavant.twms.integration.layer.interceptor.dto.SummaryDTO;
import tavant.twms.integration.web.util.PropertiesUtil;

import com.opensymphony.xwork2.ActionSupport;


@SuppressWarnings("serial")
public class SummaryAction extends ActionSupport {


	RemoteInteractionDao remoteInteractionDao;
	
	Date startDate;
	
	Date endDate;
	
	Collection<SummaryDTO> summaryDTOList ;
	
	String showMuleLogo = PropertiesUtil.getProperty("mule.displaylogo");
	
	public String display(){
		return SUCCESS;
	}

	public String fetchSummary(){
		summaryDTOList = remoteInteractionDao.getSummary(startDate, endDate);
		return SUCCESS;
	}
	

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setRemoteInteractionDao(RemoteInteractionDao remoteInteractionDao) {
		this.remoteInteractionDao = remoteInteractionDao;
	}

	public Collection<SummaryDTO> getSummaryDTOList() {
		return summaryDTOList;
	}

	
	public String getShowMuleLogo() {
		return showMuleLogo;
	}	
	
	

}
