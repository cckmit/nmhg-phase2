package tavant.twms.integration.web.actions;


import java.util.Date;
import java.util.List;

import tavant.twms.integration.layer.interceptor.RemoteInteractionDao;
import tavant.twms.integration.layer.interceptor.dto.RemoteInteractionDTO;
import tavant.twms.integration.web.util.PropertiesUtil;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;

@SuppressWarnings("serial")
public class DetailsAction extends ActionSupport implements Preparable{

	RemoteInteractionDao remoteInteractionDao;
	
	Date startDate;
	
	Date endDate;
	
	String bodType;
	
	List<String> bodTypeList;
	
	List<RemoteInteractionDTO> remoteInteractionList;
	
	String errorMessage;
	
	String payLoad;
	
	Long remoteId;
	
	final String PAYLOAD= "payload";
	
	final String ERROR_MESSAGE = "errorMessage";
	
	String showMuleLogo = PropertiesUtil.getProperty("mule.displaylogo");
	
	public String display(){
		bodTypeList = remoteInteractionDao.getBodTypeList();
		return SUCCESS;
	}

	public String fetchDetails(){
		bodTypeList = remoteInteractionDao.getBodTypeList();
		remoteInteractionList= remoteInteractionDao.getDetails(startDate, endDate, bodType);
		return SUCCESS;
	}
	
	public String getErrorMessageById(){
		errorMessage = remoteInteractionDao.getErrorMessageById(remoteId);
		return ERROR_MESSAGE;
	}

	public String getPayloadById(){
		payLoad = remoteInteractionDao.getPayloadById(remoteId);
		return PAYLOAD;
	}
	
	
	
	public void prepare() throws Exception {
		bodTypeList = remoteInteractionDao.getBodTypeList();
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

	
	public String getBodType() {
		return bodType;
	}

	public void setBodType(String bodType) {
		this.bodType = bodType;
	}

	public void setRemoteInteractionDao(RemoteInteractionDao remoteInteractionDao) {
		this.remoteInteractionDao = remoteInteractionDao;
	}

	public List<String> getBodTypeList() {
		return bodTypeList;
	}

	public void setBodTypeList(List<String> bodTypeList) {
		this.bodTypeList = bodTypeList;
	}

	public List<RemoteInteractionDTO> getRemoteInteractionList() {
		return remoteInteractionList;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getPayLoad() {
		return payLoad;
	}

	public Long getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(Long remoteId) {
		this.remoteId = remoteId;
	}


	public String getShowMuleLogo() {
		return showMuleLogo;
	}


	
	
	
}
