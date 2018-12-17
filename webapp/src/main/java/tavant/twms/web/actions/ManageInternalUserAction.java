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

import com.opensymphony.xwork2.Preparable;
import org.acegisecurity.providers.encoding.PasswordEncoder;
import org.apache.commons.lang.StringUtils;
import org.hibernate.classic.Validatable;
import tavant.twms.dateutil.TWMSStringUtil;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.infra.i18n.ProductLocale;
import tavant.twms.infra.i18n.ProductLocaleService;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.security.authn.provider.TWMSPasswordEncoder;

import java.util.*;

@SuppressWarnings("serial")
public class ManageInternalUserAction extends I18nActionSupport implements
		Preparable, Validatable {

	private ServiceProvider serviceProvider;

	private User user;

	private String confirmPassword;

	private MSAService msaService;

	private final SortedHashMap<String, String> countryList = new SortedHashMap<String, String>();

	private List<String> countriesFromMSA = new ArrayList<String>();
	
	private List<Role> internalUserRoles = new ArrayList<Role>();

	private String countryCode;

	private String stateCode;

	private String cityCode;

	private String zipCode;

	private String defaultLocale;

	private List<Role> roleToBeAssigned = new ArrayList<Role>();

	 private boolean userCreated;

    private String loginName;

    private List<ProductLocale> listOfLocale;

    private ProductLocaleService productLocaleService;

    private String userPassword;
    
    private BusinessUnit selectedBU;

	public BusinessUnit getSelectedBU() {
		return selectedBU;
	}
	public void setSelectedBU(BusinessUnit selectedBU) {
		this.selectedBU = selectedBU;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.opensymphony.xwork2.Preparable#prepare()
	 */
	public void prepare() {
        List<Country> countries = msaService.getCountryList();
        for (Country country : countries) {
            countryList.put(country.getCode(), country.getName());
        }
        createRolesToBeDisplayed();
        countriesFromMSA = msaService.getCountriesFromMSA();
        defaultLocale = getLoggedInUser().getLocale().toString();
        user = new User();
        user.setAddress(new Address());
        user.getAddress().setCountry("en_US");
        listOfLocale = productLocaleService.findAll();
    }
    public String forwardToCreateInternalUser() {
    	for(BusinessUnit bu : getLoggedInUser().getBusinessUnits()) {
    		if(bu.getName().equalsIgnoreCase(SelectedBusinessUnitsHolder.getSelectedBusinessUnit()))
    			setSelectedBU(bu);
    	}
        return SUCCESS;
    }
    
	private void validatePassword() {

		if (StringUtils.isBlank(userPassword)) {

			addActionError("error.manageDealerUsers.password.required");
		}
		if (StringUtils.isNotBlank(userPassword)
				&& !userPassword.equals(confirmPassword)) {
			addActionError("error.manageDealerUsers.differentPasswords");
		}
		if(StringUtils.isNotBlank(userPassword) && userPassword.trim().length()<6){
            addActionError("error.manageDealerUsers.password.minimum");
        }
	}

    @Override
	public void validate() {
        if (user.getId() == null && orgService.findUserByName(this.user.getName()) != null) {
			addActionError("error.manageDealerUsers.login.exists");
		}
        
             
		if(StringUtils.isNotBlank(user.getPassword()) && user.getPassword().trim().length()<6){
            addActionError("error.manageDealerUsers.password.minimum");
        }
		
		if (roleToBeAssigned==null || roleToBeAssigned.isEmpty()) {
			addActionError("error.manageDealerUsers.assign.role");
		}
		if (this.user != null) {
			 	internalUserRoles.removeAll(roleToBeAssigned);
				Collections.sort(internalUserRoles);
				Collections.sort(roleToBeAssigned);	
		}
      }

    
    
    private void createRolesToBeDisplayed() {
        Role invFullViewRole=new Role();
        internalUserRoles.addAll(orgService.getRolesByType(RoleType.INTERNAL));  
        Collections.sort(internalUserRoles);    }



    public String search(){
        user = orgService.findInternalUser(loginName.trim(), AdminConstants.INTERNAL.toString());
        if (user == null) {
            addActionError("No user found for given Login");
            return INPUT;
        }
         if (!countriesFromMSA.contains( user.getAddress().getCountry())) {
			stateCode = user.getAddress().getState();
			cityCode = user.getAddress().getCity();
			zipCode = user.getAddress().getZipCode();
		}
		internalUserRoles.removeAll(user.getRoles());
		roleToBeAssigned.addAll(user.getRoles());
		Collections.sort(internalUserRoles);
		Collections.sort(roleToBeAssigned);
		defaultLocale = user.getLocale() != null ? user.getLocale().toString() : null;
		return SUCCESS;
    }
    


    public String create() {    	
    	validatePassword();
        if (hasActionErrors()) {
			return INPUT;
		}
        user.setLocale(new Locale(this.defaultLocale));
        TreeSet<BusinessUnit> businessUnitTreeSet=new TreeSet<BusinessUnit>();
        List<Organization> organizations = new ArrayList<Organization>();
        User loggedInUser = getLoggedInUser();
        if(getSelectedBU() != null)
        	businessUnitTreeSet.add(getSelectedBU());
        else
        	businessUnitTreeSet.addAll(loggedInUser.getBusinessUnits());
        user.setBusinessUnits(businessUnitTreeSet);
        organizations.add(orgService.findOrganizationByName(AdminConstants.OEM));
        user.setBelongsToOrganizations(organizations);
		Address address = user.getAddress();
        setNonMSAAddress();       
		address.setBelongsTo(orgService.findOrganizationByName(AdminConstants.OEM));        
        Set<Role> userRoles = new HashSet<Role>();
        for (Role role : roleToBeAssigned) {
            userRoles.add(role);
            if(Role.DEALER_WARRANTY_ADMIN.equals(role.getName())){
                 userRoles.add(orgService.findRoleByName(Role.DEALER));
                 userRoles.add(orgService.findRoleByName(Role.BASE_ROLE));
            }
            if(Role.DEALER_SALES_ADMIN.equals(role.getName())){
                userRoles.add(orgService.findRoleByName(Role.INVENTORY_SEARCH));
                userRoles.add(orgService.findRoleByName(Role.INVENTORY_LISTING));                
            }
        }
        user.setRoles(userRoles);
        setUserAvailability(user);
		this.user.setAddress(address);
		user.setUserType(AdminConstants.INTERNAL);
		
		PasswordEncoder passowrdEncoder = new TWMSPasswordEncoder();
		
		if (StringUtils.isNotBlank(userPassword)) {
 			user.setPassword(passowrdEncoder.encodePassword(userPassword,
 					user.getSalt())
 					+ "|" + TWMSStringUtil.bytesToHexString(user.getSalt()));
 		}
		
		this.orgService.createUser(user);

		if (hasActionErrors()) {
			return INPUT;
		}
        userCreated=true;
        addActionMessage(getText("label.dealerUser.success"));
        return SUCCESS;
	}
    
    private void setUserAvailability(User user) {
    	List<Role> userBUAvailabilityRolesList = new ArrayList<Role>();
    	List<Long> userBUAvailabilityOrgUserList = new ArrayList<Long>();
    	List<String> userBUAvailabilityBUList = new ArrayList<String>();
		if(user.getUserAvailablity().size() > 0 ){
			for(UserBUAvailability userBUAvail : user.getUserAvailablity()){
				userBUAvailabilityRolesList.add(userBUAvail.getRole());
				if(!userBUAvailabilityOrgUserList.contains(userBUAvail.getOrgUser().getId()))
				   userBUAvailabilityOrgUserList.add(userBUAvail.getOrgUser().getId());
				if(!userBUAvailabilityBUList.contains(userBUAvail.getBusinessUnitInfo().getName()))
				   userBUAvailabilityBUList.add(userBUAvail.getBusinessUnitInfo().getName());
			}
		}
    	
		Set<UserBUAvailability> userBUAvailabilityList = new HashSet<UserBUAvailability>();
		for(BusinessUnit businessUnit : user.getBusinessUnits()){
			for(Role role : user.getRoles()) {
					UserBUAvailability userBUAvailability = new UserBUAvailability();
					if(userBUAvailabilityRolesList.contains(role) && userBUAvailabilityOrgUserList.contains(user.getId()) 
							 && userBUAvailabilityBUList.contains(businessUnit.getName())){
					} else {
						userBUAvailability.setOrgUser(user);
						userBUAvailability.setRole(role);
						userBUAvailability.setAvailable(Boolean.TRUE);  //By default need to set TRUE for all new internal user 
						userBUAvailability.setDefaultToRole(Boolean.FALSE); //By default need to set False
						userBUAvailability.getBusinessUnitInfo().setName(businessUnit.getName());
						userBUAvailabilityList.add(userBUAvailability);
					}
			}
		}
		user.setUserAvailablity(userBUAvailabilityList);
	}
    
	public boolean isAssignedTechnicianRole() {
		for(Role role : roleToBeAssigned) {
			if(Role.TECHNICIAN.equalsIgnoreCase(role.getName()))
    			return true;
		}
    	return false;
    }
    
    public boolean isInternal() {
    	for(Role role : user.getRoles()) {
    		if(Role.DEALER_ADMIN.equalsIgnoreCase(role.getName()) || Role.DEALER_SALES_ADMIN.equalsIgnoreCase(role.getName()) 
    				|| Role.DEALER_WARRANTY_ADMIN.equalsIgnoreCase(role.getName()) )
    			return true;
    	}
    	return false;
    }

    private void validationForUpdate() {
    	
		if ((StringUtils.isNotBlank(userPassword) && !userPassword
				.equals(confirmPassword))
				|| (StringUtils.isBlank(userPassword) && StringUtils
						.isNotBlank(confirmPassword))) {
			addActionError("error.manageDealerUsers.differentPasswords");
		}
		
		if(StringUtils.isNotBlank(userPassword) && userPassword.trim().length()<6){
            addActionError("error.manageDealerUsers.password.minimum");
        }
    	
    	if(countriesFromMSA.contains(this.user.getAddress().getCountry()))
    	{
    		if(StringUtils.isBlank(user.getAddress().getState()))
    			addActionError("error.manageDealerUsers.requiredStateCode");
    		if(StringUtils.isBlank(user.getAddress().getCity()))
    			addActionError("error.manageDealerUsers.requiredCityCode");
    		if(StringUtils.isBlank(user.getAddress().getZipCode()))
    			addActionError("error.manageDealerUsers.requiredzipCode");
    		
    	}
    	internalUserRoles.removeAll(user.getRoles());
 		Collections.sort(internalUserRoles);
 		Collections.sort(roleToBeAssigned);
    }
 
    public String update() {
    	 validationForUpdate();
         if (hasActionErrors()) {
 			return INPUT;
 		}
        PasswordEncoder passowrdEncoder = new TWMSPasswordEncoder();
 		if (StringUtils.isNotBlank(userPassword)) {
 			user.setPassword(passowrdEncoder.encodePassword(userPassword,
 					user.getSalt())
 					+ "|" + TWMSStringUtil.bytesToHexString(user.getSalt()));
 		}
        Set<Role> rolesToBeAdded = new HashSet<Role>();
        for (Role role : roleToBeAssigned) {
            rolesToBeAdded.add(role);
            if (Role.DEALER_WARRANTY_ADMIN.equals(role.getName())) {
                rolesToBeAdded.add(orgService.findRoleByName(Role.DEALER));
                rolesToBeAdded.add(orgService.findRoleByName(Role.BASE_ROLE));
            }
            if (Role.DEALER_SALES_ADMIN.equals(role.getName())) {
                rolesToBeAdded.add(orgService.findRoleByName(Role.INVENTORY_SEARCH));
                rolesToBeAdded.add(orgService.findRoleByName(Role.INVENTORY_LISTING));
            }
        }
        this.user.setRoles(rolesToBeAdded);
        setUserAvailability(this.user);
        setNonMSAAddress();
        this.user.getAddress().setEmail(this.user.getEmail());
        this.orgService.updateUser(this.user);
        userCreated = false;
        addActionMessage(getText("label.dealerUser.update.success"));
        return SUCCESS;
    }

    private void setNonMSAAddress() {
        if (!countriesFromMSA.contains(user.getAddress().getCountry())) {
			user.getAddress().setState(stateCode);
            user.getAddress().setCity(cityCode);
            user.getAddress().setZipCode(zipCode);
        }
    }

    public boolean checkForValidatableCountry(String country) {
        return this.countriesFromMSA.contains(country);
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public MSAService getMsaService() {
		return msaService;
	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}

	public List<String> getCountriesFromMSA() {
		return countriesFromMSA;
	}

	public void setCountriesFromMSA(List<String> countriesFromMSA) {
		this.countriesFromMSA = countriesFromMSA;
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

	public SortedHashMap<String, String> getCountryList() {
		return countryList;
	}

	public ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	public void setServiceProvider(ServiceProvider serviceProvider) {
		this.serviceProvider = serviceProvider;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public List<Role> getRoleToBeAssigned() {
        return roleToBeAssigned;
    }

    public void setRoleToBeAssigned(List<Role> roleToBeAssigned) {
        this.roleToBeAssigned = roleToBeAssigned;
    }

   

    public boolean isUserCreated() {
        return userCreated;
    }

    public void setUserCreated(boolean userCreated) {
        this.userCreated = userCreated;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
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

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public List<Role> getInternalUserRoles() {
		return internalUserRoles;
	}

	public void setInternalUserRoles(List<Role> internalUserRoles) {
		this.internalUserRoles = internalUserRoles;
	}
  
}