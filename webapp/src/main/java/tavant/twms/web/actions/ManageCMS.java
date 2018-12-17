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
package tavant.twms.web.actions;

import static tavant.twms.domain.common.AdminConstants.INTEGRATION_ERRORS;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import tavant.twms.common.TWMSException;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.integration.SyncTracker;
import tavant.twms.domain.integration.SyncTrackerService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.SecurityHelper;
import tavant.twms.web.security.authn.decrypter.UsernameDecrypter;

@SuppressWarnings("serial")
public class ManageCMS extends TwmsActionSupport {

	private static Logger logger = Logger.getLogger(ManageCMS.class);

	private String actionUrl;

	private SecurityHelper securityHelper;

	private ServiceProvider serviceProvider;

	private InventoryService inventoryService;

	private UsernameDecrypter aesBasedDecrypterForCMS;

	private User user;

	private InventoryItem item;

	List<String> CMSErrorsList = new ArrayList<String>();

	private SyncTrackerService syncTrackerService;

	private String loginName;

	private String cmsInfo;

	private String queryString;

	private String serialNo;

	private HttpSession session;

	public String viewEquipmentHistory() throws Exception {
		try {
			securityHelper.populateSystemUser();
			session = request.getSession();
			decryptCMSInfo();
			validateUserFields();
			validateCMSinfoWithDb();

		} catch (TWMSException e) {
			createSyncTracker();
			session.setAttribute(INTEGRATION_ERRORS, CMSErrorsList);
			return INPUT;
		}
		session.setAttribute(AdminConstants.AESENCRYPTION, AdminConstants.NO);
		session.setAttribute(AdminConstants.CMS, "inventoryDetail.action?id="
				+ item.getId());
		actionUrl = "/authenticateUser?user=" + loginName;
		return SUCCESS;
	}

	private void decryptCMSInfo() {
		queryString = request.getParameter("info");
		if(StringUtils.isBlank(queryString)){
			CMSErrorsList.add("error.manageDealerUsers.authToken.notMatch");
			throw new TWMSException("Throwing Exception because of query string null");
		}
		cmsInfo = aesBasedDecrypterForCMS.decrypt(request
				.getParameter("info"));
		if (cmsInfo.equalsIgnoreCase("error.manageDealerUsers.authToken.notMatch")) {
			CMSErrorsList.add("error.manageDealerUsers.authToken.notMatch");
			throw new TWMSException("Exception while decrypting userid");
		} else {
			if(isUserIDOrSerialNoBlank(cmsInfo)){
				CMSErrorsList.add("error.manageCMS.serialNo/user.required");
				throw new TWMSException("Throwing Exception because of mandatory fields are missing");
			}
			loginName = cmsInfo.split("&")[0].split("=")[1];
			serialNo = cmsInfo.split("&")[1].split("=")[1];
		}

	}

	private void createSyncTracker() {
		if (queryString == null) {
			SyncTracker syncTracker = new SyncTracker(AdminConstants.CMS,
					"Empty Query String for CMSINFO");
			return;
		}
		SyncTracker syncTracker = new SyncTracker(AdminConstants.CMS,
				queryString);
		syncTracker.setErrorMessage(CMSErrorsList.toString());
		syncTrackerService.save(syncTracker);

	}

	private void validateUserFields() {
		if (StringUtils.isBlank(loginName)) {
			CMSErrorsList.add("error.manageDealerUsers.login.required");
		}
		if (StringUtils.isBlank(serialNo)) {
			CMSErrorsList.add("error.manageCMS.serialNo.required");
			return;
		}
		if (CMSErrorsList.size() > 0) {

			throw new TWMSException("Mandatory Fields Missing or Not Proper");
		}
	}

	private void validateCMSinfoWithDb() {
		user = orgService.findUserByName(loginName);
		if (user == null) {
			CMSErrorsList.add("error.loginFailed");
			throw new TWMSException(
					"user and inventory not assosiated each other");
		}
		try {
			item = inventoryService.findInventoryItemBySerialNumber(serialNo);
		} catch (ItemNotFoundException e) {
			CMSErrorsList.add("error.manageCMS.serialNumber.notExists");
			throw new TWMSException(
					"user and inventory not assosiated each other");
		}
		
	}


	private boolean isUserIDOrSerialNoBlank(String cmsinfo) {
		if (cmsInfo.split("&").length >= 2) {
			if (cmsInfo.split("&")[0].split("=").length >= 2
					&& cmsInfo.split("&")[1].split("=").length >= 2) {
				return false;
			}
			return true;
		}
		return true;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}


	public String getActionUrl() {
		return actionUrl;
	}

	public SyncTrackerService getSyncTrackerService() {
		return syncTrackerService;
	}

	public void setSyncTrackerService(SyncTrackerService syncTrackerService) {
		this.syncTrackerService = syncTrackerService;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public UsernameDecrypter getAesBasedDecrypterForCMS() {
		return aesBasedDecrypterForCMS;
	}

	public void setAesBasedDecrypterForCMS(UsernameDecrypter aesBasedDecrypterForCMS) {
		this.aesBasedDecrypterForCMS = aesBasedDecrypterForCMS;
	}

}
