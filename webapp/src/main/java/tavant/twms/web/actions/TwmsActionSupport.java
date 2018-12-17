package tavant.twms.web.actions;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import ognl.Ognl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.util.StringUtils;
import tavant.twms.action.ThemeProvider;
import static tavant.twms.config.UiConfReader.getPropertyValue;
import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParam;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.infra.ApplicationSettingsHolder;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.SecurityHelper;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import tavant.twms.infra.googlemaps.GoogleMapsSettings;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.worklist.common.BUSetting;
import tavant.twms.worklist.common.BuSettingsService;
import static tavant.twms.taglib.nlist.NList.INDEX_VARIABLE;
import static tavant.twms.taglib.nlist.NList.FQ_NAME_VARIABLE;

/**
 * User: janmejay.singh Date: Aug 8, 2007 Time: 4:17:38 PM
 */

@SuppressWarnings("serial")
public class TwmsActionSupport extends ActionSupport implements SessionAware,
		ServletResponseAware, ServletRequestAware, ThemeProvider, TWMSWebConstants {

	private SecurityHelper securityHelper;
	private BusinessUnitService businessUnitService;
	protected Map session;
	protected OrgService orgService;
	protected Collection<String> actionWarnings = new ArrayList<String>(5);

	private static final Logger logger = Logger
			.getLogger(TwmsActionSupport.class);
	protected HttpServletResponse response;
	protected HttpServletRequest request;
	protected String searchPrefix;
	protected GoogleMapsSettings googleMapsSettings;
	protected ApplicationSettingsHolder applicationSettings;
	protected ConfigParamService configParamService;
	private int defaultHeaderSize = 0; // Variable to hold the default columns
	private Pattern NLIST_FQ_ID_ESCAPER_PATTERN = Pattern.compile("(\\[([0-9]+)\\])*(\\.|$){1}");
	private final String BUSINESS_UNIT_AMER="AMER";
	private final String BUSINESS_UNIT_EMEA="EMEA";
	private final String CANADA_COUNTRY_CODE="CA";
	
	protected SortedSet<BusinessUnit> businessUnits = new TreeSet<BusinessUnit>();

	public Collection<String> getActionWarnings() {
		return this.actionWarnings;
	}

	public void setActionWarnings(Collection<String> actionWarnings) {
		this.actionWarnings = actionWarnings;
	}

	public String getCssTheme() {
		return this.session.containsKey(CSS_THEME_SESSION_KEY) ? this.session
				.get(CSS_THEME_SESSION_KEY).toString()
				: getPropertyValue(DEFAULT_CSS_THEME_KEY);
	}

	@SuppressWarnings("unchecked")
	public void setCssTheme(String theme) {
		session.put(CSS_THEME_SESSION_KEY, theme);
	}

	public void setSession(Map session) {
		this.session = session;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}

	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public User getLoggedInUser() {
		return this.securityHelper.getLoggedInUser();
	}

	public BusinessUnit getCurrentBusinessUnit() {
		return this.securityHelper.getDefaultBusinessUnit();
	}

	public boolean isLoggedInUserADealer() {
		if(AdminConstants.EXTERNAL.equals(getLoggedInUser().getUserType()) ||
		   AdminConstants.DEALER_USER.equals(getLoggedInUser().getUserType()))
			return true;
		else
			return false;
		
	}
	
	public boolean isLoggedInUserAParentDealer() {
		if(AdminConstants.EXTERNAL.equals(getLoggedInUser().getUserType()) ||
		   AdminConstants.DEALER_USER.equals(getLoggedInUser().getUserType())){
			if(!orgService.getChildOrganizationsIds(getLoggedInUser().getBelongsToOrganization().getId()).isEmpty()){
				return true;
			}
			return false;
		}
		else
			return false;
		
	}
	
	 public boolean isLoggedInUserAnInternalUser(){
		 return getLoggedInUser().isInternalUser();
	  }

	/**
	 * This method will return true or false based on the fact whether the
	 * current logged in user belongs to a Third Party organization or not.
	 * 
	 * @return boolean value
	 */
	public boolean isLoggedInUserThirdParty() {
		return this.orgService.isThirdParty(getLoggedInUser());
	}
	
	public boolean isLoggedInUserAnInvAdmin() {
		return this.orgService.doesUserHaveRole(getLoggedInUser(),"inventoryAdmin");
	}

	public boolean isLoggedInUserAnAdmin() {
		return this.orgService.doesUserHaveRole(getLoggedInUser(), "admin");
	}
	
	public boolean isLoggedInUserContractAdmin() {
		return this.orgService.doesUserHaveRole(getLoggedInUser(), "sra");
	}
	
	public boolean isLoggedInUserNcrProcessor() {
		return this.orgService.doesUserHaveRole(getLoggedInUser(), "ncrProcessor");
	}
	
	public boolean showDealerJobNumber() {
		return  getConfigParamService().getBooleanValue(
				ConfigName.SHOW_DEALER_JOB_NUMBER.getName());
	}
	
	public boolean showAuthorizationReceived() {
		return getConfigParamService().getBooleanValue(
				ConfigName.SHOW_AUTHORIZATION_RECEIVED_ON_CLAIM.getName());
	}

	public boolean showAuthorizationNumber() {
		return getConfigParamService().getBooleanValue(
				ConfigName.SHOW_AUTHORIZATION_NUMBER_ON_CLAIM.getName());
	}

	/**
	 * If the currently logged in user is associated with a Dealership, returns
	 * that Dealership; otherwise returns null.
	 * 
	 * @return Logged in User's Dealership, if applicable.
	 */
	public ServiceProvider getLoggedInUsersDealership() {
        Organization organization = getLoggedInUsersOrganization();
        return ( organization != null && InstanceOfUtil.isInstanceOfClass(ServiceProvider.class, organization)) ?
        								new HibernateCast<ServiceProvider>().cast(organization) : null;
        		
	}

    public Organization getLoggedInUsersOrganization() {
        return getLoggedInUser().getBelongsToOrganization();
    }

    public void addActionWarning(String warningMessage) {
		if (StringUtils.hasText(warningMessage)) {
			internalGetWarningMessages().add(warningMessage);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Not adding invalid warning message :"
						+ warningMessage);
			}
		}
	}

	public boolean hasActionWarnings() {
		return this.actionWarnings.size() > 0;
	}

	private Collection<String> internalGetWarningMessages() {
		if (this.actionWarnings == null) {
			this.actionWarnings = new ArrayList<String>();
		}

		return this.actionWarnings;
	}

	/**
	 * Generate a JSON string in the correct format expected by a Dijit Select
	 * widget. This particular version of the API does a toString() of each
	 * collection element, and uses that value as both the key and label. You
	 * would normally use this API if your list contains plain strings instead
	 * of proper objects.
	 * 
	 * @param matchingObjects
	 *            List of objects whose toString() wuld be used to generate the
	 *            key and value pair.
	 * @return Returns the generated JSON string.
	 */
	protected String generateComboboxJson(Collection matchingObjects) {
		return generateComboboxJson(matchingObjects, "");
	}

	public boolean isScrapped(InventoryItem inventoryItem) {
		if (inventoryItem != null
				&& "SCRAP".equals(inventoryItem.getConditionType()
						.getItemCondition())) {
			addActionError("message.scrap.machineScrapped");
			return true;
		}
		return false;
	}

	/**
	 * Calls {@link #generateComboboxJson(java.util.List)} to generate the
	 * corresponding JSON string and, in addition, also writes it out to the
	 * response.
	 * 
	 * @param matchingObjects
	 *            List of objects whose toString() wuld be used to generate the
	 *            key and value pair.
	 * @return Always returns null, since the json is directly written out in to
	 *         the response stream, right away.
	 */
	protected String generateAndWriteComboboxJson(Collection matchingObjects) {
		return writeJsonResponse(generateComboboxJson(matchingObjects));
	}

	/**
	 * Generate a JSON string in the correct format expected by a Dijit Select
	 * widget. This particular version of the API accepts an additional "key"
	 * argument. The "key" should be the name of a property that the collection
	 * elements possess. It uses OGNL to evaluate the "key" property for each
	 * collection element, and uses that value as both the key and label. e.g.:
	 * generateAndWriteComboboxJson(LIST_OF_ITEMS, "name"); // The select wud
	 * use item names as key and value. e.g.:
	 * generateAndWriteComboboxJson(LIST_OF_ITEMS, "productType.groupCode"); //
	 * The select wud use group codes as key and value.
	 * 
	 * @param matchingObjects
	 *            List of objects whose toString() would be used to generate the
	 *            key and value pair.
	 * @param keyProperty
	 *            a property that is present in the collection element. Can be a
	 *            nested property, such as "item.name"
	 * @return Returns the generated JSON string.
	 */
	protected String generateComboboxJson(Collection matchingObjects,
			String keyProperty) {
		return generateComboboxJson(matchingObjects, keyProperty, keyProperty);
	}

	/**
	 * Calls {@link #generateComboboxJson(java.util.List, java.lang.String key)}
	 * to generate the corresponding JSON string and, in addition, also writes
	 * it out to the response.
	 * 
	 * @param matchingObjects
	 *            List of objects whose toString() would be used to generate the
	 *            key and value pair.
	 * @param keyProperty
	 *            a property that is present in the collection element. Can be a
	 *            nested property, such as "item.name"
	 * @return Always returns null, since the json is directly written out in to
	 *         the response stream, right away.
	 */
	protected String generateAndWriteComboboxJson(Collection matchingObjects,
			String keyProperty) {
		return writeJsonResponse(generateComboboxJson(matchingObjects,
				keyProperty, keyProperty));
	}

	/**
	 * Generates a JSON string in the correct format expected by a Dijit Select
	 * widget. This particular version of the API accepts an additional "key"
	 * and "label" argument. Both "key" and "label" should be names of
	 * properties that the collection elements possess. It uses OGNL to evaluate
	 * the "key" and "label" properties for each collection element, and uses
	 * them as the key and label respectively. e.g.:
	 * generateAndWriteComboboxJson(LIST_OF_ITEMS, "id", "name"); // The select
	 * wud use item ids as key and item names as label. e.g.:
	 * generateAndWriteComboboxJson(LIST_OF_ITEMS, "id",
	 * "productType.groupCode"); // The select wud use item ids as key and group
	 * codes as label.
	 * 
	 * @param matchingObjects
	 *            List of objects whose toString() would be used to generate the
	 *            key and value pair.
	 * @param keyProperty
	 *            a property that is present in the collection element. Can be a
	 *            nested property, such as "item.name"
	 * @param labelProperty
	 *            a property that is present in the collection element. Can be a
	 *            nested property, such as "item.name"
	 * @return Returns the generated JSON string.
	 */
	protected String generateComboboxJson(Collection matchingObjects,
			String keyProperty, String labelProperty) {
		boolean useKeyAsLabel = keyProperty.equals(labelProperty);
		boolean useToString = "".equals(keyProperty);

		JSONArray itemsArray = new JSONArray();

		try {
			for (Object matchingObject : matchingObjects) {
				Object keyValue = (useToString) ? matchingObject.toString()
						: Ognl.getValue(keyProperty, matchingObject);
				Object labelValue = (useKeyAsLabel) ? keyValue : Ognl.getValue(
						labelProperty, matchingObject);
				itemsArray.put(new JSONObject().put("key", keyValue).put(
						"label", labelValue));
			}

			JSONObject comboJson = new JSONObject();
			comboJson.put("identifier", "key");
			comboJson.put("items", itemsArray);

			return comboJson.toString();
		} catch (Exception e) {
			String errorMessage = "Exception while json'ifying combobox data :";// TOD
																				// CHECK
																				// THIS
			logger.error(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	protected String generateComboboxJsonForSerialNumber(List<InventoryItem> matchingObjects,
			String keyProperty, String labelProperty) {
		boolean useKeyAsLabel = keyProperty.equals(labelProperty);
		boolean useToString = "".equals(keyProperty);
		JSONArray itemsArray = new JSONArray();
		List<String> serialNumberList = new ArrayList<String>();
		for ( InventoryItem matchingObject : matchingObjects ) {
			serialNumberList.add(matchingObject.getSerialNumber());
		}
		try {
			for (InventoryItem matchingObject : matchingObjects) {
				Object keyValue = (useToString) ? matchingObject.toString()
						: Ognl.getValue(keyProperty, matchingObject);
				Object labelValue = (useKeyAsLabel) ? keyValue : Ognl.getValue(
						labelProperty, matchingObject);
				Object objectValue = Ognl.getValue("ofType.number", matchingObject);
				int j = CollectionUtils.cardinality( matchingObject.getSerialNumber() , serialNumberList );
				if( j > 1 ) {
					labelValue = labelValue.toString().concat("["+objectValue+"]");
				}
				itemsArray.put(new JSONObject().put("key", keyValue).put(
						"label", labelValue));
			}
	
			JSONObject comboJson = new JSONObject();
			comboJson.put("identifier", "key");
			comboJson.put("items", itemsArray);
	
			return comboJson.toString();
		} catch (Exception e) {
			String errorMessage = "Exception while json'ifying combobox data :";// TOD
																				// CHECK
																				// THIS
			logger.error(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		}
	}

	/**
	 * Calls {@link #generateComboboxJson(java.util.List, java.lang.String key,
	 * java.lang.String label)} to generate the corresponding JSON string and,
	 * in addition, also writes it out to the response.
	 * 
	 * @param matchingObjects
	 *            List of objects whose toString() would be used to generate the
	 *            key and value pair.
	 * @param keyProperty
	 *            a property that is present in the collection element. Can be a
	 *            nested property, such as "item.name"
	 * @param labelProperty
	 *            a property that is present in the collection element. Can be a
	 *            nested property, such as "item.name"
	 * @return Always returns null, since the json is directly written out in to
	 *         the response stream, right away.
	 */
	public String generateAndWriteComboboxJson(Collection matchingObjects,
			String keyProperty, String labelProperty) {
		writeJsonResponse(generateComboboxJson(matchingObjects, keyProperty,
				labelProperty));

		return null;
	}
	
	public String generateAndWriteComboboxJsonForSerialNumber( List<InventoryItem> matchingObjects,
			String keyProperty, String labelProperty) {
		writeJsonResponse(generateComboboxJsonForSerialNumber(matchingObjects, keyProperty,
				labelProperty));
		return null;
	}
	
	/**
	 * An API to append more than one property of an entity in UI for display.
	 * For example, if we need to show supplier number appended with name on UI we can use this.
	 * @param matchingObjects
	 * @param keyProperty
	 * @param labelProperties
	 * @return
	 */
	
	public String generateAndWriteComboboxJson(List matchingObjects,
			String keyProperty, List<String> labelProperties) {
		writeJsonResponse(generateComboboxJsonWithLabelProperties(matchingObjects, keyProperty,
				labelProperties));

		return null;
	}

	/**
	 * Generate a JSON string in the correct format expected by a Dijit Select
	 * widget. This particular version of the API sends an empty JSON string,
	 * i.e one for which the items array is empty.
	 * 
	 * @return Returns the generated JSON string.
	 */
	public String generateEmptyComboboxJson() {
		return generateComboboxJson(Collections.EMPTY_LIST, "", "");
	}

	/**
	 * Calls {@link #generateEmptyComboboxJson()} to generate the corresponding
	 * JSON string and, in addition, also writes it out to the response.
	 * 
	 * @return Always returns null, since the json is directly written out in to
	 *         the response stream, right away.
	 */
	public String generateAndWriteEmptyComboboxJson() {
		writeJsonResponse(generateEmptyComboboxJson());

		return null;
	}

	/**
	 * Writes out a JSONObject to the response.
	 * 
	 * @param jsonObject
	 *            the JSONObject to convert to string and write out
	 * @return null, since actions which use this API can then directly say
	 *         "return writeJsonResponse(...)" and the returned null would
	 *         indicate to Struts2 that there is no "view" configured for the
	 *         action.
	 */
	public String writeJsonResponse(JSONObject jsonObject) {
		return writeJsonResponse(jsonObject.toString());
	}

	/**
	 * Writes out a JSONArray to the response.
	 * 
	 * @param jsonArray
	 *            the JSONArray to convert to string and write out
	 * @return null, since actions which use this API can then directly say
	 *         "return writeJsonResponse(...)" and the returned null would
	 *         indicate to Struts2 that there is no "view" configured for the
	 *         action.
	 */
	public String writeJsonResponse(JSONArray jsonArray) {
		return writeJsonResponse(jsonArray.toString());
	}

	/**
	 * Writes out a JSON string directly to the response.
	 * 
	 * @param jsonString
	 *            the JSONString to write out directly
	 * @return null, since actions which use this API can then directly say
	 *         "return writeJsonResponse(...)" and the returned null would
	 *         indicate to Struts2 that there is no "view" configured for the
	 *         action.
	 */
	public String writeJsonResponse(String jsonString) {
		 response.setHeader("Pragma", "no-cache");
	     response.addHeader("Cache-Control", "must-revalidate");
	     response.addHeader("Cache-Control", "no-cache");
	     response.addHeader("Cache-Control", "no-store");
	     response.setDateHeader("Expires", 0);     
		
		response.setContentType("text/json-comment-filtered");
		try {
			response.getWriter().write(jsonString);
			response.flushBuffer();

			return null;
		} catch (IOException e) {
			String errorMessage = "Exception while writing JSON string \""
					+ jsonString + "\" to response :";
			logger.error(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		}
	}

	public void setServletResponse(HttpServletResponse httpServletResponse) {
		response = httpServletResponse;
	}

	public String getSearchPrefix() {
		return searchPrefix;
	}

	public void setSearchPrefix(String searchPrefix) {
		this.searchPrefix = searchPrefix;
	}

	public GoogleMapsSettings getGoogleMapsSettings() {
		return googleMapsSettings;
	}

	public void setGoogleMapsSettings(GoogleMapsSettings googleMapsSettings) {
		this.googleMapsSettings = googleMapsSettings;
	}

	public void setServletRequest(HttpServletRequest httpServletRequest) {
		this.request = httpServletRequest;
	}

	public ApplicationSettingsHolder getApplicationSettings() {
		return applicationSettings;
	}

	public void setApplicationSettings(
			ApplicationSettingsHolder applicationSettings) {
		this.applicationSettings = applicationSettings;
	}

	public BusinessUnitService getBusinessUnitService() {
		return businessUnitService;
	}

	public void setBusinessUnitService(BusinessUnitService businessUnitService) {
		this.businessUnitService = businessUnitService;
	}

	public SortedSet<BusinessUnit> getBusinessUnits() {
		if (businessUnits == null || businessUnits.isEmpty()) {
			businessUnits = getSecurityHelper().getLoggedInUser()
					.getBusinessUnits();
		}
		return businessUnits;
	}

	public void setBusinessUnits(TreeSet<BusinessUnit> businessUnits) {
		this.businessUnits = businessUnits;
	}

	public boolean isUserTKDealer() {
		boolean isDealer = false;
		Set<Role> roles = getLoggedInUser().getRoles();
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(Role.TK_USER)) {
				isDealer = true;
				break;
			}
		}
		return isDealer;
	}

	public String getDateFormatForLoggedInUser() {
		{
			return TWMSDateFormatUtil.getDateFormatForLoggedInUser();
		}
	}
	
	
	protected String generateAndWriteComboboxJsonForItem(List<Item> matchingObjects,
			String keyProperty) {
		return writeJsonResponse(generateComboboxJsonForItem(matchingObjects,
				keyProperty, keyProperty));
	}
	
	
	protected String generateComboboxJsonForItem(List<Item> matchingObjects,
			String keyProperty, String labelProperty) {	
		

		JSONArray itemsArray = new JSONArray();
		List<Item> matchingObjectsComp = matchingObjects;

		try {
			for (Item matchingObject : matchingObjects) {
				Object keyValue = matchingObject.getAlternateNumber();				
					if(matchingObject.getDuplicateAlternateNumber())
					{
						//Alternate MFR Number is  duplicated. We need to show the segmented number in the UI
						keyValue = matchingObject.getNumber();
					}
				
				itemsArray.put(new JSONObject().put("key", keyValue).put(
						"label", keyValue));
			}

			JSONObject comboJson = new JSONObject();
			comboJson.put("identifier", "key");
			comboJson.put("items", itemsArray);

			return comboJson.toString();
		} catch (Exception e) {
			String errorMessage = "Exception while json'ifying combobox data :";// TOD
																				// CHECK
																				// THIS
			logger.error(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		}
	}
	
	
	/**
	 * Generates a JSON string in the correct format expected by a Dijit Select
	 * widget. This particular version of the API accepts an additional "key"
	 * and "label" argument. Both "key" and "label" should be names of
	 * properties that the collection elements possess. It uses OGNL to evaluate
	 * the "key" and "label" properties for each collection element, and uses
	 * them as the key and label respectively. e.g.:
	 * generateAndWriteComboboxJson(LIST_OF_ITEMS, "id", "name"); // The select
	 * wud use item ids as key and item names as label. e.g.:
	 * generateAndWriteComboboxJson(LIST_OF_ITEMS, "id",
	 * "productType.groupCode"); // The select wud use item ids as key and group
	 * codes as label.
	 * 
	 * @param matchingObjects
	 *            List of objects whose toString() would be used to generate the
	 *            key and value pair.
	 * @param keyProperty
	 *            a property that is present in the collection element. Can be a
	 *            nested property, such as "item.name"
	 * @param labelProperties
	 *            a list of properties whose value on the object will be appended as the value 
	 *            against keyProperty
	 * @return Returns the generated JSON string.
	 */
	protected String generateComboboxJsonWithLabelProperties(List matchingObjects,
			String keyProperty, List<String> labelProperties) {
		
		JSONArray itemsArray = new JSONArray();

		try {
			for (Object matchingObject : matchingObjects) {
				Object keyValue = Ognl.getValue(keyProperty, matchingObject);
				Object labelValueToReturn = "";
				for(String labelProperty : labelProperties)
				{
					Object labelValue = Ognl.getValue(labelProperty, matchingObject);
					if(labelValue != null && StringUtils.hasText(labelValue.toString()))
					{
						if(labelValueToReturn.equals(""))
							labelValueToReturn = labelValue.toString();
						else
							labelValueToReturn = labelValueToReturn.toString() + "-" + labelValue.toString();
					}					
				}
				itemsArray.put(new JSONObject().put("key", keyValue).put(
						"label", labelValueToReturn));
			}

			JSONObject comboJson = new JSONObject();
			comboJson.put("identifier", "key");
			comboJson.put("items", itemsArray);

			return comboJson.toString();
		} catch (Exception e) {
			String errorMessage = "Exception while json'ifying combobox data :";// TOD
																				// CHECK
																				// THIS
			logger.error(errorMessage, e);
			throw new RuntimeException(errorMessage, e);
		}
	}
	
    /**
     * Child actions should override this method and put actual logic in its place.
     *
     * @return
     */
	public boolean isPageReadOnly() {
		boolean isReadOnlyDealer = false;
		Set<Role> roles = getLoggedInUser().getRoles();
		for (Role role : roles) {
			if (role.getName().equalsIgnoreCase(Role.READ_ONLY_DEALER)) {
				isReadOnlyDealer = true;
				break;
			}
		}
		return isReadOnlyDealer;

	}
    
    public String getJSCalendarDateFormatForLoggedInUser() {
    	return TWMSDateFormatUtil.getJSCalendarDateFormatForLoggedInUser();
    }
    

	public int getDefaultHeaderSize() {
		return defaultHeaderSize;
	}

	public void setDefaultHeaderSize(int defaultHeaderSize) {
		this.defaultHeaderSize = defaultHeaderSize;
	}
	
	public void incrementDefaultHeaderSize(){
		this.defaultHeaderSize++;
	}

	public void setNListIndex(int nListIndex) {
		putIntoActionContext(INDEX_VARIABLE, nListIndex);
	}

	public void setNListName(String nListName) {
		putIntoActionContext(FQ_NAME_VARIABLE, nListName);
	}

	protected void putIntoActionContext(String key, Object value) {
		ActionContext.getContext().put(key, value);
	}

	public String qualifyId(String idFragment) {
		String parentIdentifier = nullSafeGetFromContext(FQ_NAME_VARIABLE);

		StringBuffer fqId = new StringBuffer(100);

		if (StringUtils.hasText(parentIdentifier)) {
			fqId.append(NLIST_FQ_ID_ESCAPER_PATTERN.matcher(parentIdentifier).replaceAll("$2_"));
		}

		return fqId.append(idFragment).toString();
	}

	protected String nullSafeGetFromContext(String key) {
		final Object value = ActionContext.getContext().get(key);
		return (value == null) ? "" : value.toString();
	}
	
	protected boolean isSupplierRecoveryUser(){
		if(getLoggedInUser().hasRole(Role.SUPPLIER) ||
		   getLoggedInUser().hasRole(Role.RECOVERYPROCESSOR) || 
		   getLoggedInUser().hasRole(Role.SUPPLIER_REC_INITIATOR) || 
		   getLoggedInUser().hasRole(Role.SRA) ||
		   getLoggedInUser().hasRole(Role.PART_SHIPPER_LIMITED_VIEW) ||
		   getLoggedInUser().hasRole(Role.PARTSHIPPER)||
		   getLoggedInUser().hasRole(Role.PROCESSOR)
		)
			return true;
		else
			return false;
	}
	
	public boolean isLoggedInUserAnEnterpriseDealer() {
		ServiceProvider dealership = getLoggedInUsersDealership();
		return (dealership != null) && dealership.isEnterpriseDealer();
    }

    public boolean isLoggedInUserInternalOrEnterpriseDealer(){
        return  isLoggedInUserAnEnterpriseDealer() || isLoggedInUserAnInternalUser();
    }

    public ConfigParamService getConfigParamService() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

    public boolean hasOnlyCevaRole(){
        return getLoggedInUser().hasOnlyRole(Role.CEVA_PROCESSOR);
    }

    public boolean enableWarrantyOrderClaims(){
        return  getConfigParamService().getBooleanValue(
                ConfigName.ENABLE_WARRANTY_ORDER_CLAIM.getName());
    }
    
    public boolean displayNCRandBT30DayNCROnClaimPage(){
    	return getConfigParamService().getBooleanValue(ConfigName.DISPLAY_NCR_AND_BT_30DAY_NCR_ON_CLAIM_PAGE.getName());
    }
    
	public boolean displayEmissionOnClaimPage() {
		return getConfigParamService().getBooleanValue(
				ConfigName.ENABLE_EMISSION.getName());
	}
	
	public boolean isBuConfigAMER() {
		return getCurrentBusinessUnit().getName().equals(BUSINESS_UNIT_AMER);
	}
	
	public boolean isBuConfigEMEA() {
		return getCurrentBusinessUnit().getName().equals(BUSINESS_UNIT_EMEA);
	}
	
	public boolean isLoggedInUserDualBrandDealer(){
		if(!isLoggedInUserAnInternalUser() && null !=new HibernateCast<Dealership>().cast(getLoggedInUsersDealership()).getDualDealer())
			return true;
		else 
			return false;
	
	}
	
	public boolean isLoggedInUserACanadianDealer() {
        //Internal user is not a dealer
        if(getLoggedInUser().isInternalUser()){
            return false;
        }

        return orgService.findOrgById(getLoggedInUsersOrganization().getId()).getAddress().getCountry().equalsIgnoreCase(CANADA_COUNTRY_CODE);
    }
	
	public String getCentralLogisticLocation(){
		String centralLogisticName  = getConfigParamService().getStringValueByBU(ConfigName.CENTRAL_LOGISTIC_LOCATION.getName(), getCurrentBusinessUnit().getName());
		return centralLogisticName;		
	}
	public String getDefaultPartReturnLocation(){
		String centralLogisticName  = getConfigParamService().getStringValueByBU(ConfigName.DEFAULT_RETURN_LOCATION_CODE.getName(), getCurrentBusinessUnit().getName());
		return centralLogisticName;		
	}
	
	public boolean isLoggedInUserASupplier() {
		if(AdminConstants.SUPPLIER_USER.equals(getLoggedInUser().getUserType()))
			return true;
		else
			return false;
		
	}
    
}