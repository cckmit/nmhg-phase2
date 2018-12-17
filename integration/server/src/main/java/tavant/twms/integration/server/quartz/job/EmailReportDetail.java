/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.quartz.job;

/**
 * 
 * @author prasad.r
 */
public class EmailReportDetail {

	private String toUserEmail;
	private String fromUserEmail;
	private String template;
	private String subject;
	private String userName;
	private String password;
	private String creditSubmissionUrl;
	private String itemSyncURL;
	private String italyQaNotificationUrl;
	private String asyncResponseURL;
	private String bookingsSubmsiionWebServiceURL;
	private String dealerBatchClaimWebServiceURL;
	
	public String getFromUserEmail() {
		return fromUserEmail;
	}

	public void setFromUserEmail(String fromUserEmail) {
		this.fromUserEmail = fromUserEmail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getToUserEmail() {
		return toUserEmail;
	}

	public void setToUserEmail(String toUserEmail) {
		this.toUserEmail = toUserEmail;
	}
	public String getItemSyncURL() {
		return itemSyncURL;
	}

	public void setItemSyncURL(String itemSyncURL) {
		this.itemSyncURL = itemSyncURL;
	}
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCreditSubmissionUrl() {
		return creditSubmissionUrl;
	}

	public void setCreditSubmissionUrl(String creditSubmissionUrl) {
		this.creditSubmissionUrl = creditSubmissionUrl;
	}

	public String getItalyQaNotificationUrl() {
		return italyQaNotificationUrl;
	}

	public void setItalyQaNotificationUrl(String italyQaNotificationUrl) {
		this.italyQaNotificationUrl = italyQaNotificationUrl;
	}

	public String getAsyncResponseURL() {
		return asyncResponseURL;
	}

	public void setAsyncResponseURL(String asyncResponseURL) {
		this.asyncResponseURL = asyncResponseURL;
	}

	public String getBookingsSubmsiionWebServiceURL() {
		return bookingsSubmsiionWebServiceURL;
	}

	public void setBookingsSubmsiionWebServiceURL(
			String bookingsSubmsiionWebServiceURL) {
		this.bookingsSubmsiionWebServiceURL = bookingsSubmsiionWebServiceURL;
	}

	public String getDealerBatchClaimWebServiceURL() {
		return dealerBatchClaimWebServiceURL;
	}

	public void setDealerBatchClaimWebServiceURL(
			String dealerBatchClaimWebServiceURL) {
		this.dealerBatchClaimWebServiceURL = dealerBatchClaimWebServiceURL;
	}
	

}
