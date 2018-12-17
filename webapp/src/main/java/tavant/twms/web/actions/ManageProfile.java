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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.acegisecurity.providers.encoding.ShaPasswordEncoder;
import org.apache.commons.lang.StringUtils;

import tavant.twms.dateutil.TWMSStringUtil;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Country;
import tavant.twms.domain.orgmodel.MSAService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.security.SecurityHelper;
import tavant.twms.web.common.SessionUtil;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.security.authn.provider.TWMSPasswordEncoder;

import com.opensymphony.xwork2.Preparable;

/**
 * @author kiran.sg
 */
@SuppressWarnings("serial")
public class ManageProfile extends I18nActionSupport implements Preparable{

	private User user;

	private Map<String, String> addressTypes = new HashMap<String, String>();

	private String existingPassword;

	private String newPassword;

	private String confirmPassword;

	private MSAService msaService;
	private final SortedHashMap <String, String> countryList = new SortedHashMap<String, String>();
	private HttpServletResponse response;
	private String countryCode;
	private String stateCode;
	private String cityCode;
	private String zipCode;
	List<String> countriesFromMSA = new ArrayList<String>();

	private List<ProductLocale> listOfLocale;
	
	private ProductLocaleService productLocaleService;
	private List<String> certified = new ArrayList<String>();
	private List<String> organization = new ArrayList<String>();
	private String certificationFlag;

	
	public String getCertificationFlag() {
		return certificationFlag;
	}

	public void setCertificationFlag(String certificationFlag) {
		this.certificationFlag = certificationFlag;
	}
	
	public List<String> getCertified() {
		return certified;
	}

	public void setCertified(List<String> certified) {
		this.certified = certified;
	}
	public List<String> getOrganization() {
		return organization;
	}

	public void setOrganization(List<String> organization) {
		this.organization = organization;
	}
	public List<ProductLocale> getListOfLocale() {
		return listOfLocale;
	}

	public void setListOfLocale(List<ProductLocale> listOfLocale) {
		this.listOfLocale = listOfLocale;
	}

	public ProductLocaleService getProductLocaleService() {
		return productLocaleService;
	}

	public void setProductLocaleService(ProductLocaleService productLocaleService) {
		this.productLocaleService = productLocaleService;
	}

	public ManageProfile() {
		super();
		addressTypes.put("HOME", getText("label.manageProfile.home"));
		addressTypes.put("WORK", getText("label.manageProfile.work"));
		addressTypes.put("BILLING", getText("label.manageProfile.billing"));
		addressTypes.put("SHIPPING", getText("label.manageProfile.shipping"));
	}

	public String showProfile() {
		user = orgService.findUserById(getLoggedInUser().getId());
		Address address = user.getAddress();
		if (address!=null &&!countriesFromMSA.contains(address.getCountry())) {
			stateCode = address.getState();
			cityCode = address.getCity();
			zipCode = address.getZipCode();
		}
		if (!user.getUserType().equals(AdminConstants.SUPPLIER_USER) && orgService.isDealer(user)){
			if (user.getBelongsToOrganizations().size() > 1) {
				for (Organization org : user.getBelongsToOrganizations()) {
					certified.add((orgService.findDealerById(org.getId())
							.getCertified()) ? "Certified" : "Non-Certified");
					organization.add(org.getName());
				}
			} else {

				certified.add((orgService.findDealerById(user
						.getBelongsToOrganizations().get(0).getId())
						.getCertified()) ? "Certified" : "Non-Certified");
				organization.add(user.getBelongsToOrganizations().get(0)
						.getName());
			}
			this.certificationFlag = "Yes";
		  }
		return SUCCESS;
	}

	public String updateProfile() {
		prepareAddress();
		
		// Set the new password
		PasswordEncoder passowrdEncoder = new TWMSPasswordEncoder();
		 if (existingPassword != null && existingPassword.length() != 0) {
	            user.setPassword(passowrdEncoder.encodePassword(newPassword, user.getSalt())
	            		+ "|" + TWMSStringUtil.bytesToHexString(user.getSalt()));
	        }
		orgService.updateUser(user);

		// Update the locale
		SessionUtil.setLocale(session, user.getLocale());
		new SecurityHelper().getLoggedInUser().setLocale(user.getLocale());
		
		
		addActionMessage("message.manageProfile.profileUpdated");
		return SUCCESS;
	}

	public Map<String, String> getAddressTypes() {
		return addressTypes;
	}

	public void setAddressTypes(Map<String, String> addressTypes) {
		this.addressTypes = addressTypes;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getExistingPassword() {
		return existingPassword;
	}

	public void setExistingPassword(String existingPassword) {
		this.existingPassword = existingPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public Map getSession() {
		return session;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	private void prepareAddress() {
		Address address = user.getAddress();
		if (!countriesFromMSA.contains(address.getCountry())) {
			address.setState(stateCode);
			address.setCity(cityCode);
			address.setZipCode(zipCode);
		}
	}

	public void prepare() {
		List<Country> countries = msaService.getCountryList();
		for (Country country : countries) {
			countryList.put(country.getCode(), country.getName());
		}
		countriesFromMSA = msaService.getCountriesFromMSA();
		listOfLocale=productLocaleService.findAll();
	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}
	
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public SortedHashMap<String, String> getCountryList() {
		return countryList;
	}

	public boolean checkForValidatableCountry(String country) {
		if (countriesFromMSA.contains(country)) {
			return true;
		}
		return false;
	}

	@Override
	public void validate() {

		boolean validStateList = true;
		boolean validCityList = true;
		boolean validZipList = true;
		boolean validAddressCombination = true;
		Address address = user.getAddress();
		if (!countriesFromMSA.contains(address.getCountry())) {			
			if (cityCode == null || "".equals(cityCode.trim())) {
				validCityList = false;
			}
			if (zipCode == null || "".equals(zipCode.trim())) {
				validZipList = false;
			}
		} else {
			if (address.getState() == null
					|| "".equals(address.getState().trim())) {
				validStateList = false;
			}
			if (address.getCity() == null
					|| "".equals(address.getCity().trim())) {
				validCityList = false;
			}
			if (address.getZipCode() == null
					|| "".equals(address.getZipCode().trim())) {
				validZipList = false;
			}
			if (!validateAddressCombination(address)) {
				validAddressCombination = false;
			}
		}

		if (!validStateList) {
			addActionError("error.manageProfile.requiredState");
		}
		if (!validCityList) {
			addActionError("error.manageProfile.requiredCity");
		}
		if (!validZipList) {
			addActionError("error.manageProfile.requiredZipcode");
		}
		if (validStateList && validCityList && validZipList
				&& !validAddressCombination) {
			addActionError("error.manageCustomer.invalidAddressCombination");
		}
	}

	private boolean validateAddressCombination(Address address) {
		return msaService.isValidAddressCombination(address.getCountry(),
				address.getState(), address.getCity(), address.getZipCode());
	}

	public boolean isDisabledPasswordUpdate() {
		return false;
	}


}