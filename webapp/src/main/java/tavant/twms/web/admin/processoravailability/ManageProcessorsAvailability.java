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

package tavant.twms.web.admin.processoravailability;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserBUAvailability;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.inbox.DefaultPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author jhulfikar.ali
 * @Created on Nov 18, 2008
 * 
 */
@SuppressWarnings("serial")
public class ManageProcessorsAvailability extends SummaryTableAction {

	public static final String EXPRESSION_DEFAULTED_USER = "defaultedUser";

	public static final String EXPRESSION_AVAILABLE_USER = "availableUser";

	public static final Logger logger = Logger.getLogger(ManageProcessorsAvailability.class);
	
	private List<User> allProcessors = new ArrayList<User>();

	private User userForAvailability = new User();

	private String role;

	protected String id;// the dataId of the row...
	
	private String selectedBusinessUnit;

	private Boolean userAvailable;
	
	private Boolean isDefaultUser;
	
	private Boolean userDefaultRole;

	public String listProcessors() {
		allProcessors = orgService.findAllAvailableProcessors();
		return SUCCESS;
	}

	public List<User> getAllProcessors() {
		return allProcessors;
	}

	public void setAllProcessors(List<User> allProcessors) {
		this.allProcessors = allProcessors;
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		this.tableHeadData = new ArrayList<SummaryTableColumn>();
		this.tableHeadData.add(new SummaryTableColumn("", "id", 0, "number",
				"id", false, true, true, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.processorAvail.processor", "name", 33, "string",
				"CompleteNameAndLogin", true, false, false, false));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.processorAvail.availability", EXPRESSION_AVAILABLE_USER, 33,
				"string", SummaryTableColumnOptions.NO_FILTER));
		this.tableHeadData.add(new SummaryTableColumn(
				"columnTitle.processorAvail.defaultUser", EXPRESSION_DEFAULTED_USER, 33,
				"string", EXPRESSION_DEFAULTED_USER, SummaryTableColumnOptions.NO_SORT | SummaryTableColumnOptions.NO_FILTER));
		return this.tableHeadData;
	}

	public BeanProvider getBeanProvider() {
		return new DefaultPropertyResolver() {
			@SuppressWarnings("unchecked")
			@Override
			public Object getProperty(String propertyPath, Object root) {
				Object value = null;
				if (EXPRESSION_AVAILABLE_USER.equals(propertyPath)
						|| EXPRESSION_DEFAULTED_USER.equals(propertyPath)) {
					selectedBusinessUnit = SelectedBusinessUnitsHolder
							.getSelectedBusinessUnit();
					Set<UserBUAvailability> userBUAvailabilities = (Set<UserBUAvailability>) super
							.getProperty("userAvailablity", root);

					if (selectedBusinessUnit == null
							|| userBUAvailabilities == null
							|| userBUAvailabilities.isEmpty())
						return getText("label.common.emptyValue");
					UserBUAvailability tempUserBUAvailability, userBUAvailability = null;

					for (Iterator<UserBUAvailability> iterator = userBUAvailabilities.iterator(); iterator.hasNext();) {
						tempUserBUAvailability = (UserBUAvailability) iterator.next();
						if (tempUserBUAvailability != null && 
								selectedBusinessUnit.equalsIgnoreCase(
										tempUserBUAvailability.getBusinessUnitInfo().getName()) && 
										tempUserBUAvailability.getRole()!=null && 
										tempUserBUAvailability.getRole().getName().equalsIgnoreCase(getRole()))
						{
							userBUAvailability = tempUserBUAvailability;
							break;
						}
					}

					// Logic to display the User is available or not
					if (EXPRESSION_AVAILABLE_USER.equalsIgnoreCase(propertyPath)) {
						if (userBUAvailability!=null && userBUAvailability.isAvailable())
							value = getText("label.common.yes");
						else 
							value = getText("label.common.no");
					}
					// Logic to display the Default User or not
					else if (EXPRESSION_DEFAULTED_USER.equalsIgnoreCase(propertyPath)) {
						
						if (userBUAvailability!=null && userBUAvailability.isDefaultToRole())
							value = getText("label.common.yes");
						else 
							value = getText("label.common.no");
					}
					return value;
				} else {
					return super.getProperty(propertyPath, root);
				}
			}
		};
	}

	@Override
	protected PageResult<?> getBody() {
		return orgService.findProcessors(getCriteria(), role);
	}

	protected ListCriteria getCriteria() {
		PageSpecification pageSpecification = new PageSpecification();
		pageSpecification.setPageSize(pageSize);
		pageSpecification.setPageNumber(page - 1);

		ListCriteria listCriteria = new ListCriteria();
		addFilterCriteria(listCriteria);
		addSortCriteria(listCriteria);
		listCriteria.setPageSpecification(pageSpecification);
		return listCriteria;
	}

	private void addSortCriteria(ListCriteria criteria) {
		for (String[] sort : sorts) {
			String sortOnColumn = sort[0];			
			// by-passing the dynamic values to object navigation path
			if (EXPRESSION_AVAILABLE_USER.equalsIgnoreCase(sortOnColumn))
				sortOnColumn = "userAvailablity.available";
			boolean ascending = !sort[1].equals(SORT_DESCENDING);
			criteria.addSortCriteria(addAlias(sortOnColumn), ascending);
		}
	}

	private void addFilterCriteria(ListCriteria criteria) {
		for (String filterName : filters.keySet()) {
			String filterValue = filters.get(filterName);
			// by-passing the dynamic values to object navigation path
			if (EXPRESSION_AVAILABLE_USER.equalsIgnoreCase(filterName))
				filterName = "userAvailablity.available";
			criteria.addFilterCriteria(addAlias(filterName), filterValue);
		}
	}

	private String addAlias(String sortOnColumn) {
		if (StringUtils.hasText(getAlias())) {
			return getAlias() + "." + sortOnColumn;
		}
		return sortOnColumn;
	}

	public String preview() {
		populateUserAvailability(null);
		return SUCCESS;
	}

	public String detail() {
		return preview();
	}

	public String updateProcessor() {
		// Validations goes here...
		
		if (!validUserAvailability())
			return INPUT;
		try {
			selectedBusinessUnit = SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
			orgService.updateUserAvailability(getUserForAvailability().getId(), selectedBusinessUnit, getRole(), 
					getUserAvailable(), getUserDefaultRole());
			populateUserAvailability(getUserForAvailability().getId());
			addActionMessage("label.processorAvail.success");
			return SUCCESS;
		}
		catch (Exception exception)
		{
			logger.debug("Exception while saving user Availability information: " + exception.getMessage());
			populateUserAvailability(getUserForAvailability().getId());
			addActionError("error.processor.availability.general");
			return INPUT;
		}
	}

	private Boolean validUserAvailability() {
		// Validations
		boolean isValid = Boolean.TRUE;
		if (isDefaultUser && !getUserAvailable())
		{
			// When user is the default to the role and admin tries to change his availability to no. 
			addActionError("error.processor.availability.defaultuser");
			isValid = isValid && Boolean.FALSE;
		}
		else if (!isDefaultUser && !getUserAvailable() && getUserDefaultRole())
		{
			// When the user is not a default to the role and trying to update as Default without
			// making him as Available
			addActionError("error.processor.availability.avalailableUser");
			isValid = isValid && Boolean.FALSE;
		}
		if(!getUserDefaultRole()){
			User DefaultUser=orgService.findDefaultUserBelongingToRoleForSelectedBU(SelectedBusinessUnitsHolder.getSelectedBusinessUnit(), getRole());
			if(DefaultUser.equals(getUserForAvailability())){
				addActionError("error.processor.availability.cannotChangeDefaultUser");
				isValid = isValid && Boolean.FALSE;
			}
		}
		if (!isValid)
			populateUserAvailability(getUserForAvailability().getId());
		return isValid;
	}

	private void populateUserAvailability(Long id) {
		Long userId = id != null ? id : new Long(getId());
		userForAvailability = orgService.findUserById(userId);
		selectedBusinessUnit = SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
		userAvailable = userForAvailability.isAvailableForBU(selectedBusinessUnit, role);
		userDefaultRole = userForAvailability.isDefaultUserForBURole(selectedBusinessUnit, getRole());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getUserForAvailability() {
		return userForAvailability;
	}

	public void setUserForAvailability(User userForAvailability) {
		this.userForAvailability = userForAvailability;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getSelectedBusinessUnit() {
		return selectedBusinessUnit;
	}

	public void setSelectedBusinessUnit(String selectedBusinessUnit) {
		this.selectedBusinessUnit = selectedBusinessUnit;
	}

	public Boolean getUserAvailable() {
		return userAvailable;
	}

	public void setUserAvailable(Boolean userAvailable) {
		this.userAvailable = userAvailable;
	}

	public Boolean getUserDefaultRole() {
		return userDefaultRole;
	}

	public void setUserDefaultRole(Boolean userDefaultRole) {
		this.userDefaultRole = userDefaultRole;
	}

	public Boolean getIsDefaultUser() {
		return isDefaultUser;
	}

	public void setIsDefaultUser(Boolean isDefaultUser) {
		this.isDefaultUser = isDefaultUser;
	}
	
}
