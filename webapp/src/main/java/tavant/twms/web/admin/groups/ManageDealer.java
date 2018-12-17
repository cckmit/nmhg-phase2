/**
 *
 */
package tavant.twms.web.admin.groups;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.commons.lang.StringUtils;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Kiran.Kollipara
 *
 */
public class ManageDealer  extends I18nActionSupport implements Preparable, Validateable {


	private String id;

	private String dealerSchemeId;

	private DealerGroup dealerGroup;
	private DealerScheme dealerScheme;

	private String dealerName;
	private String dealerNumber;
	private List<ServiceProvider> dealers;
	private List<ServiceProvider> availableDealers = new ArrayList<ServiceProvider>();
	private Set<ServiceProvider> includedDealers;
	private Set<String> includedDealerNames = new HashSet<String>();
	private DealerGroupService dealerGroupService;
	private DealerSchemeService dealerSchemeService;
	private String buttonLabel;
	private String sectionTitle;

	public void prepare() throws Exception {
		buttonLabel = getText("button.manageGroup.createDealerGroup");
		sectionTitle = getText("label.manageGroup.createNewGroup");

		Long idToBeUsed = null;
		if (dealerSchemeId != null) {
			id = dealerSchemeId;
		}
		if (id != null) {
			idToBeUsed = Long.parseLong(id);
		} else if ((dealerScheme != null) && (dealerScheme.getId() != null)) {
			idToBeUsed = dealerScheme.getId();
		}
		if (idToBeUsed != null) {
			dealerScheme = dealerSchemeService.findById(idToBeUsed);
		}
		if (dealerGroup != null && dealerGroup.getId() != null) {
			buttonLabel = getText("button.manageGroup.updateDealerGroup");
			sectionTitle = getText("title.manageGroup.dealerGroup") + " - "
					+ dealerGroup.getName() + " - "
					+ dealerGroup.getDescription();
			for(ServiceProvider includeddealer : dealerGroup.getIncludedDealers()){
				if(includeddealer.getD().isActive()) {
					includedDealerNames.add(includeddealer.getName());
				}
			}
		}

       /* if(dealerGroupConfigParam == null)
                        dealerGroupConfigParam = this.configParamService.getBooleanValue(DEALER_GROUP_CODE);*/
	}

	@Override
	public void validate() {
		String actionName = ActionContext.getContext().getName();
		if ("search_dealers_for_dealergroup".equals(actionName)) {
			if (StringUtils.isBlank(dealerName)
					&& StringUtils.isBlank(dealerNumber)) {
				addActionError("error.manageGroup.dealerNameOrNumberRequired");

			}
		} else {
			validateOthers();
		}
		if (hasActionErrors()) {
			add();
		}
	}

	public String manage() {
		if (includedDealerNames != null) {
			includedDealers = new HashSet<ServiceProvider>();
			for (String aDealerName : includedDealerNames) {
				includedDealers.add(orgService.findServiceProviderByName(aDealerName));
			}
		}
		return SUCCESS;
	}

	public String search() {
		List<String> foundDealerNames = new ArrayList<String>();

		if (dealerName.length() > 0) {
			foundDealerNames = orgService.findServiceProviderNamesStartingWith(
					dealerName, 0, 10);
			dealers = new ArrayList<ServiceProvider>();
			for (String aName : foundDealerNames) {
				dealers.add(orgService.findServiceProviderByName(aName));
			}
			availableDealers.addAll(dealers);
			for (ServiceProvider dealer : dealers) {
				DealerGroup ownerGroup = dealerGroupService
						.findGroupContainingServiceProviders(dealer, dealerScheme);
				if (ownerGroup != null
						&& (dealerGroup.getId() == null || ownerGroup.getId()
								.compareTo(dealerGroup.getId()) != 0)) {
					availableDealers.remove(dealer);
				}
			}
		}

		if (dealerNumber.length() > 0) {
			foundDealerNames = orgService.findDealerNumbersStartingWith(
					dealerNumber, 0, 10);
			dealers = new ArrayList<ServiceProvider>();
			for (String aName : foundDealerNames) {
				dealers.add(orgService.findServiceProviderByName(aName));
			}
			availableDealers.addAll(dealers);
			for (ServiceProvider dealer : dealers) {
				DealerGroup ownerGroup = dealerGroupService
						.findGroupContainingServiceProviders(dealer, dealerScheme);
				if (ownerGroup != null
						&& (dealerGroup.getId() == null || ownerGroup.getId()
								.compareTo(dealerGroup.getId()) != 0)) {
					availableDealers.remove(dealer);
				}
			}

		}
		if (dealerGroup != null && dealerGroup.getId() != null)
			includedDealers = getAlreadyIncludedDealers(dealerGroup);

		return SUCCESS;
	}

	public String add() {
		includedDealers = new HashSet<ServiceProvider>();		
		if (dealerGroup != null && dealerGroup.getId() != null)
			includedDealers = getAlreadyIncludedDealers(dealerGroup);

		if (includedDealerNames != null) {
			for (String aDealerName : includedDealerNames) {
				if (includedDealers == null || (includedDealers != null && !getAlreadyIncludedDealerNames(includedDealers).contains(aDealerName)))
					includedDealers.add(orgService.findServiceProviderByName(aDealerName));
			}
		}
		return SUCCESS;
	}

	public String save() throws Exception {
		if (dealerGroup != null && dealerGroup.getId() != null) {
			return update();
		} else {
			String name = dealerGroup.getName();
			String description = dealerGroup.getDescription();
            String code = dealerGroup.getCode();
			dealerGroup = dealerScheme.createDealerGroup(name, description, code);
			includedDealers = new HashSet<ServiceProvider>();
			for (String aDealerName : includedDealerNames) {
				includedDealers.add(orgService.findServiceProviderByName(aDealerName));
			}
			dealerGroup.setIncludedDealers(includedDealers);
				dealerGroupService.save(dealerGroup);
				addActionMessage("message.manageGroup.dealerGroupCreateSuccess",
						dealerGroup.getName());
			return SUCCESS;
		}
	}

	private String update() throws Exception {
		String name = dealerGroup.getName();
		String description = dealerGroup.getDescription();
		dealerGroup = dealerGroupService.findById(dealerGroup.getId());

		dealerGroup.setName(name);
		dealerGroup.setDescription(description);
		
		includedDealers = getAlreadyIncludedDealers(dealerGroup);
		
		if (includedDealerNames != null && includedDealerNames.size() > includedDealers.size()) {
			for (String aDealerName : includedDealerNames) {
				if (!getAlreadyIncludedDealerNames(includedDealers).contains(aDealerName))
					includedDealers.add(orgService.findServiceProviderByName(aDealerName));				
			}			
		}else{
			for(String aDealerName : getAlreadyIncludedDealerNames(includedDealers)){
				if(!includedDealerNames.contains(aDealerName))
					includedDealers.remove(orgService.findServiceProviderByName(aDealerName));
			}			
		}		
			
		dealerGroup.setIncludedDealers(includedDealers);
		dealerGroupService.update(dealerGroup);
		addActionMessage("message.manageGroup.dealerGroupUpdateSuccess");
		return SUCCESS;
	}

	public String getButtonLabel() {
		return buttonLabel;
	}

	public void setButtonLabel(String buttonLabel) {
		this.buttonLabel = buttonLabel;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	// ************************ Private Methods **************************//
	private void validateOthers() {
		if (StringUtils.isBlank(dealerGroup.getName())) {
			addActionError("error.manageGroup.nonEmptyDealerGroupName");
		} else {
			DealerGroup group = dealerGroupService.findDealerGroupByName(
					dealerGroup.getName(), dealerScheme);
			if (group != null && !checkSame(group, dealerGroup)) {
				addActionError("error.manageGroup.duplicateGroupForScheme");
			}
		}
		if (StringUtils.isBlank(dealerGroup.getDescription())) {
			addActionError("error.manageGroup.nonEmptyDescriptionForItemGroup");
		}
		if ((includedDealerNames == null) || (includedDealerNames.isEmpty())) {
			addActionError("error.manageGroup.nonEmptyDealerSet");
		}
	}

	private boolean checkSame(DealerGroup source, DealerGroup target) {
		if (target.getId() != null
				&& source.getId().compareTo(target.getId()) == 0) {
			return true;
		}
		return false;
	}
	
	private Set<ServiceProvider> getAlreadyIncludedDealers(DealerGroup dealerGroup) {
		includedDealers = new HashSet<ServiceProvider>();
		for (ServiceProvider aDealer : dealerGroup.getIncludedDealers()) {
			if (aDealer.getD().isActive())
				includedDealers.add(aDealer);
		}
		return includedDealers;
		
	}
	
	private Set<String> getAlreadyIncludedDealerNames(
			Set<ServiceProvider> includedDealers) {
		Set<String> includedDealerNames = new HashSet<String>();
		for (ServiceProvider serviceProvider : includedDealers) {
			//There trim() is used here for triming existing data. As there are data like e.g. '"JENS LINDE A/S			"' which cannot be trimmed using trim() in DB
			includedDealerNames.add(serviceProvider.getName().trim());
		}
		return includedDealerNames;
	}

	public List<ServiceProvider> getAvailableDealers() {
		return availableDealers;
	}

	public void setAvailableDealers(List<ServiceProvider> availableDealers) {
		this.availableDealers = availableDealers;
	}

	public DealerGroup getDealerGroup() {
		return dealerGroup;
	}

	public void setDealerGroup(DealerGroup dealerGroup) {
		this.dealerGroup = dealerGroup;
	}

	public String getDealerName() {
		return dealerName;
	}

	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}

	public List<ServiceProvider> getDealers() {
		return dealers;
	}

	public void setDealers(List<ServiceProvider> dealers) {
		this.dealers = dealers;
	}

	public DealerScheme getDealerScheme() {
		return dealerScheme;
	}

	public void setDealerScheme(DealerScheme dealerScheme) {
		this.dealerScheme = dealerScheme;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<String> getIncludedDealerNames() {
		return includedDealerNames;
	}

	public void setIncludedDealerNames(Set<String> includedDealerNames) {
		this.includedDealerNames = includedDealerNames;
	}

	public Set<ServiceProvider> getIncludedDealers() {
		return includedDealers;
	}

	public void setIncludedDealers(Set<ServiceProvider> includedDealers) {
		this.includedDealers = includedDealers;
	}

	public void setDealerSchemeService(DealerSchemeService dealerSchemeService) {
		this.dealerSchemeService = dealerSchemeService;
	}

	public void setDealerGroupService(DealerGroupService dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}

	public String getDealerSchemeId() {
		return dealerSchemeId;
	}

	public void setDealerSchemeId(String dealerSchemeId) {
		this.dealerSchemeId = dealerSchemeId;
	}

	public String getDealerNumber() {
		return dealerNumber;
	}

	public void setDealerNumber(String dealerNumber) {
		this.dealerNumber = dealerNumber;
	}

	/*public Boolean getDealerGroupConfigParam() {
		return dealerGroupConfigParam;
	}

	public void setDealerGroupConfigParam(Boolean dealerGroupConfigParam) {
		this.dealerGroupConfigParam = dealerGroupConfigParam;
	}*/

}
