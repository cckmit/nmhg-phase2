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

package tavant.twms.domain.orgmodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterJoinTable;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import tavant.twms.common.Views;
import tavant.twms.dateutil.TWMSStringUtil;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitComparator;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.AuditableColumns;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author kamal.govindraj
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "org_user")
@SuppressWarnings("serial")
@FilterDef(name="bu_name", parameters=@ParamDef( name="name", type="string" ) )
@Filter(name = "bu_name", condition = "id in (select mapping.org_user from bu_user_mapping mapping where mapping.bu in (:name)) ")
public class User implements Comparable<User>, Serializable, AuditableColumns {

	@Id
	@GeneratedValue(generator = "User")
	@GenericGenerator(name = "User", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ORG_USER_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@Version
    @JsonIgnore
	private int version;

	@Column (name="login")
	private String name;

    @JsonIgnore
	private String userId;

    @JsonIgnore
	private String password;

	private String email;

    @JsonIgnore
    private Locale locale;

	private String firstName;

	private String lastName;

    @JsonIgnore
	private String remoteSystemName;

    @JsonIgnore
	private String remoteSystemUserId;

    @JsonIgnore
	private String jobTitle;

    @JsonIgnore
    private String preferredBu;

	@JsonView(value=Views.Internal.class)
	@OneToOne(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	private Address address;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = { @JoinColumn(name = "org_user") }, inverseJoinColumns = { @JoinColumn(name = "roles") })
	private Set<Role> roles = new HashSet<Role>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_event_states", joinColumns = { @JoinColumn(name = "org_user") }, inverseJoinColumns = { @JoinColumn(name = "event_states") })
	private Set<EventState> eventState = new HashSet<EventState>();

	@ManyToMany(fetch = FetchType.LAZY)
	private Set<UserGroup> userGroups;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "bu_user_mapping", joinColumns = { @JoinColumn(name = "org_user") }, inverseJoinColumns = { @JoinColumn(name = "bu") })
	@Sort(type = SortType.COMPARATOR, comparator = BusinessUnitComparator.class)
	@FilterJoinTable(name = "bu_name", condition = "bu in (:name) ")
    @JsonIgnore
    private SortedSet<BusinessUnit> businessUnits = new TreeSet<BusinessUnit>();

	@OneToOne(fetch = FetchType.LAZY)
	private User supervisor;

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "org_user_belongs_to_orgs", joinColumns = { @JoinColumn(name = "org_user") }, inverseJoinColumns = { @JoinColumn(name = "belongs_to_organizations") })
    @JsonIgnore
    private List<Organization> belongsToOrganizations;

	@ManyToMany
	@JoinTable(name = "user_attr_vals", joinColumns = { @JoinColumn(name = "user_id") }, inverseJoinColumns = { @JoinColumn(name = "user_attr_val_id") })
	private Set<UserAttributeValue> userAttrVals;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	@OneToMany(mappedBy = "orgUser", fetch = FetchType.LAZY)
	@Cascade( { CascadeType.SAVE_UPDATE })
	@Filter(name = "bu_name", condition = "business_unit_info in (:name) ")
	private Set<UserBUAvailability> userAvailablity = new TreeSet<UserBUAvailability>();

    @Transient
    @JsonIgnore
    private List<BusinessUnit> businessUnitAdded = new ArrayList<BusinessUnit>();

    @JsonIgnore
    @Column(name = "VIEW_FOC_CLAIMS")
	private Boolean viewFocClaims = Boolean.FALSE;
    
    private String userType;

	public static Comparator<User> SORT_BY_COMPLETE_NAME = new Comparator<User>() {
		public int compare(User arg0, User arg1) {
			return arg0.getCompleteNameAndLogin().compareToIgnoreCase(
					arg1.getCompleteNameAndLogin());
		}
	};
    
    public List<BusinessUnit> getBusinessUnitAdded() {
        return businessUnitAdded;
    }

    public void setBusinessUnitAdded(List<BusinessUnit> businessUnitAdded) {
        this.businessUnitAdded = businessUnitAdded;
    }
    @JsonIgnore
    @Transient
    private Organization currentlyActiveOrganization;

    public Organization getCurrentlyActiveOrganization() {
        return currentlyActiveOrganization;
    }

    public void setCurrentlyActiveOrganization(Organization currentlyActiveOrganization) {
        this.currentlyActiveOrganization = currentlyActiveOrganization;
    }

    public Set<UserBUAvailability> getUserAvailablity() {
		return userAvailablity;
	}

	public void setUserAvailablity(Set<UserBUAvailability> userAvailablity) {
		this.userAvailablity = userAvailablity;
	}

	/**
	 * @return the userAttrVals
	 */

	public Set<UserAttributeValue> getUserAttrVals() {
		return userAttrVals;
	}

	/**
	 * @param userAttrVals
	 *            the userAttrVals to set
	 */
	public void setUserAttrVals(Set<UserAttributeValue> userAttrVals) {
		this.userAttrVals = userAttrVals;
	}

	/**
	 * @return the supervisor
	 */
	public User getSupervisor() {
		return supervisor;
	}

	/**
	 * @param supervisor
	 *            the supervisor to set
	 */
	public void setSupervisor(User supervisor) {
		this.supervisor = supervisor;
	}

	/**
	 * @return the roles
	 */
	public Set<Role> getRoles() {
		return roles;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	public boolean hasAttribute(String attributeName, String attributeValue) {
        for (UserAttributeValue userAttributeValue : userAttrVals) {
            if (userAttributeValue.getAttribute().getName().equals(
                    attributeName)
                    && (userAttributeValue.getValue() != null && userAttributeValue
                    .getValue().equals(attributeValue))) {
                return true;
            }
        }
		return false;
	}


    //TEMP!!!
    @JsonIgnore
    public Organization getBelongsToOrganization() {
    	if(this.currentlyActiveOrganization!=null)
    	{
        	return this.currentlyActiveOrganization;
	
    	}
    	else
    	{
		return belongsToOrganizations.isEmpty() ? null : belongsToOrganizations.get(0);
    	}
	}
    
	@JsonIgnore
	public List<Long> getOrgIds() {
		List<Long> orgIds = new ArrayList<Long>();
		for (Organization org : this.getBelongsToOrganizations()) {
			orgIds.add(org.getId());
		}
		return orgIds;
	}

    public List<Organization> getBelongsToOrganizations() {
		return belongsToOrganizations;
	}

	public void setBelongsToOrganizations(List<Organization> belongsTo) {
		belongsToOrganizations = belongsTo;
	}

	/**
	 * @return the groups
	 */
	public Set<UserGroup> getUserGroups() {
		return userGroups;
	}

	/**
	 * @param userGroups
	 *            the groups to set
	 */
	public void setUserGroups(Set<UserGroup> userGroups) {
		this.userGroups = userGroups;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		if(password != null && password.indexOf("|")  > 0 ) {
			String hexpass = password.substring(0, password.indexOf("|"));			
			return hexpass;
		}
		else
			return password;
	}

    @JsonIgnore
    public byte[] getSalt() {
		
		/**
		 * Urgent production fix to handle R3 users encrypted password. Proper fix will be done later.
		 */
		if(password != null && password.indexOf("|")  > 0 ){
			/**
			 * password is stored in DB in following format
			 * HEX(SHA-HASHEDPASSWORD)|HEX(SALTSOURCE). This method need to return the salt to authenticate
			 */
			StringTokenizer tokenizer = new StringTokenizer(password, "|");
			tokenizer.nextToken();
			return TWMSStringUtil.hexStringToBytes(tokenizer.nextToken()) ;
		}
		return TWMSStringUtil.hexStringToBytes("6be84af9") ; // For newly created users, defaulting the salt value
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public boolean hasRole(String role) {
		Assert.hasText(role);
		for (Role r : roles) {
			if (role.equals(r.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Verifies that user is only in the specified role.There are some use cases
	 * where we need to differentiate b/w the users that are just the dealers or
	 * dealer plus some other role (like processor). For e.g. some claims are
	 * hidden from a dealer but not from processor,therefore we need to identify
	 * if logged in user is just a dealer or dealer plus some other role (like
	 * processor etc.).
	 */
	public boolean hasOnlyRole(String role) {
		Assert.hasText(role);
        return roles.size() == 1 && role.equals(roles.iterator().next().getName());
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int compareTo(User otherUser) {
		String otherUserName = (otherUser).getName();
		return this.getName().compareTo(otherUserName);
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", id).append("name", name)
				.append("user id", userId).append(
						"password",
						(StringUtils.hasText(password)) ? "[HIDDEN]"
								: "[NOT SET]").append("email", email).append(
						"locale", locale).toString();
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}


	public String getRemoteSystemName() {
		return remoteSystemName;
	}

	public void setRemoteSystemName(String remoteSystemName) {
		this.remoteSystemName = remoteSystemName;
	}

	public String getRemoteSystemUserId() {
		return remoteSystemUserId;
	}

	public void setRemoteSystemUserId(String remoteSystemUserId) {
		this.remoteSystemUserId = remoteSystemUserId;
	}

	public SortedSet<BusinessUnit> getBusinessUnits() {
	        if(CollectionUtils.isEmpty(businessUnits)){
	        	businessUnits=new TreeSet<BusinessUnit>();
	        if(!isInternalUser()){
	        	for (Organization organization : getBelongsToOrganizations()) {
                    businessUnits.addAll(organization.getBusinessUnits());
                }
	        }
             return businessUnits;
	        }
	        return businessUnits;
	}

	public void setBusinessUnits(TreeSet<BusinessUnit> businessUnits) {
			this.businessUnits = businessUnits;
	}

	public Set<EventState> getEventState()
	{
		return eventState;
	}

	public void setEventState(Set<EventState> eventState)
	{
		this.eventState = eventState;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null || !(obj instanceof User))
			return false;
		return this.getId().equals(((User)obj).getId());
	}

	@Override
	public int hashCode() {
		return this.getId().intValue()*13;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

    @JsonIgnore
    public String getAllBUNames() {
		Set<BusinessUnit> businessUnits = this.getBusinessUnits();
		StringBuffer buName = new StringBuffer("");
		for (Iterator<BusinessUnit> itr = businessUnits.iterator();itr.hasNext();) {
			BusinessUnit businessUnit = itr.next();
			if(itr.hasNext())
			{
				buName = buName.append(businessUnit.getName() + "/");
			}
			else
			{
				buName = buName.append(businessUnit.getName());
			}
		}
		return buName.toString();
	}

	public Boolean isAvailableForBU(String businessUnitName, String role) 	{
		if (!StringUtils.hasText(businessUnitName) || getUserAvailablity()==null ||
				getUserAvailablity().isEmpty())
			return Boolean.FALSE;
		boolean isAvailableForBU = Boolean.FALSE;
		for (Iterator<UserBUAvailability> iterator = getUserAvailablity().iterator(); iterator.hasNext();) {
			UserBUAvailability userBUAvailability = (UserBUAvailability) iterator.next();
			if ( userBUAvailability!=null && userBUAvailability.getBusinessUnitInfo()!=null &&
					businessUnitName.equalsIgnoreCase(userBUAvailability.getBusinessUnitInfo().getName()) &&
					userBUAvailability.getRole()!=null &&
					userBUAvailability.getRole().getName().equalsIgnoreCase(role))
			{
				isAvailableForBU = userBUAvailability.isAvailable();
				break;
			}
		}
		return isAvailableForBU;
	}

	public Boolean isDefaultUserForBURole(String businessUnitName, String role) {
		if (!StringUtils.hasText(businessUnitName) || !StringUtils.hasText(role) || getUserAvailablity() == null
				|| getUserAvailablity().isEmpty())
			return Boolean.FALSE;
		for (Iterator<UserBUAvailability> iterator = getUserAvailablity().iterator(); iterator.hasNext();) {
			UserBUAvailability userBUAvailability = (UserBUAvailability) iterator.next();
			if (userBUAvailability != null && userBUAvailability.getBusinessUnitInfo() != null
					&& businessUnitName.equalsIgnoreCase(userBUAvailability.getBusinessUnitInfo().getName())
					&& userBUAvailability.getRole() != null
					&& userBUAvailability.getRole().getName().equalsIgnoreCase(role)
					&& userBUAvailability.isDefaultToRole()) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public UserBUAvailability findDefaultUserBUAvailability(String businessUnitName, String role)
	{
		if (!StringUtils.hasText(businessUnitName) || !StringUtils.hasText(role)||
				getUserAvailablity()==null || getUserAvailablity().isEmpty())
			return null;

		for (Iterator<UserBUAvailability> iterator = getUserAvailablity().iterator(); iterator.hasNext();) {
			UserBUAvailability userBUAvailability = (UserBUAvailability) iterator.next();
			if ( userBUAvailability!=null && userBUAvailability.getBusinessUnitInfo()!=null &&
					businessUnitName.equalsIgnoreCase(userBUAvailability.getBusinessUnitInfo().getName())
					&& userBUAvailability.getRole() != null
					&& role.equalsIgnoreCase(userBUAvailability.getRole().getName()))
			{
				if (userBUAvailability.isDefaultToRole())
					return userBUAvailability;
			}
		}
		return null;
	}

	public UserBUAvailability findUserBUAvailability(String businessUnitName, String role) {
		if (!StringUtils.hasText(businessUnitName) ||
				getUserAvailablity()==null || getUserAvailablity().isEmpty())
			return null;

		for (Iterator<UserBUAvailability> iterator = getUserAvailablity().iterator(); iterator.hasNext();) {
			UserBUAvailability userBUAvailability = (UserBUAvailability) iterator.next();
			if ( userBUAvailability!=null && userBUAvailability.getBusinessUnitInfo()!=null &&
					businessUnitName.equalsIgnoreCase(userBUAvailability.getBusinessUnitInfo().getName()) &&
					userBUAvailability.getRole()!=null && userBUAvailability.getRole().getName().equalsIgnoreCase(role))
			{
				return userBUAvailability;
			}
		}
		return null;
	}

    @JsonIgnore
    public boolean isInternalUser() {
        if (this.userType.equalsIgnoreCase(UserType.INTERNAL.getType())) {
            return true;
        }
        return false;
    }

	public Boolean getViewFocClaims() {
			return viewFocClaims;
		}

	public void setViewFocClaims(Boolean viewFocClaims) {
			this.viewFocClaims = viewFocClaims;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	public String getCompleteNameAndLogin()
	{
		return (((this.getLastName()!=null)?this.getLastName().concat(", "):" ").concat((this.getFirstName()!= null)?this.getFirstName():" ")).concat(" (").concat((this.getName()!=null)?this.getName():" ").concat(")"); 
	}
	
	public String getCompleteName()
	{
		return (((this.getFirstName()!=null)?this.getFirstName():" ").concat(" ").concat((this.getLastName()!= null)?this.getLastName():" ")); 
	}
	
	public boolean belongsToActiveOrganization() {
		if(belongsToOrganizations == null || belongsToOrganizations.size()==0)
			return false;
		for(Organization org : belongsToOrganizations)
			if(org.getD().isActive())
				return true;
		return false;
	}

	public void setPreferredBu(String preferredBu) {
		this.preferredBu = preferredBu;
	}

	public String getPreferredBu() {
		return preferredBu;
	}

    @JsonIgnore
    public boolean isDealer() {
		for (Organization org : belongsToOrganizations) {
			return (org != null && InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, org));
		}
		return false;

	}

    @JsonIgnore
    public String getAssignToUserName()
	{
		StringBuffer assignToUserName = new StringBuffer();
		String firstName = this.getFirstName();
		String lastName = this.getLastName();
		String login = this.getName();
		assignToUserName.append(firstName == null ? "" : firstName);
		assignToUserName.append(" ");
		assignToUserName.append(lastName == null ? "" : lastName);
		assignToUserName.append(" (");
		assignToUserName.append(login);
		assignToUserName.append(")");
		return assignToUserName.toString();
		
	}

    @JsonIgnore
    public boolean isWarrantyAdminOrDealerOwnedAdmin() {
		if (this.userType.equalsIgnoreCase(UserType.INTERNAL.getType())) {
			return true;
		} else {
			for (Role role : this.roles) {
				if (role.getRoleType().equals(RoleType.DEALER_OWNED))
					return true;
			}
			return false;
		}

	}

    @JsonIgnore
    public String getLoginAndCompleteName() {
		return (this.getName().concat(" (").concat(((this.getLastName()!=null)?this.getLastName().concat(", "):" ").concat((this.getFirstName()!= null)?this.getFirstName():" ")).concat(")"));
	}

    @JsonIgnore
	public boolean isDealerOwned() {  //temp fix
		for (Role role : this.roles) {
			if (role.getRoleType() != null && role.getRoleType().getType().equalsIgnoreCase(RoleType.DEALER_OWNED.getType())) {
				return true;
			}
		}
		return false;
	}

    @JsonIgnore
	public boolean isAServicingDealer() {
		for (Role role : this.roles) {
			if (role.getRoleType() != null && role.getRoleType().getType().equalsIgnoreCase(RoleType.DEALER.getType())) {
				return true;
			}
		}
		return false;
	}

}
