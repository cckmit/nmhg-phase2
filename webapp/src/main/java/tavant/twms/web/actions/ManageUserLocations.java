package tavant.twms.web.actions;

import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.ValidationAware;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.infra.*;
import tavant.twms.web.admin.supplier.ManageContract;
import tavant.twms.web.inbox.DefaultPropertyResolver;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("serial")
public class ManageUserLocations extends SummaryTableAction implements Preparable, ValidationAware {

    private static Logger logger = LogManager.getLogger(ManageContract.class);

    private static final String LOCATION_TYPE_BUSINESS = "BUSINESS";

	public static final String USER_DEALER = "Dealer";

	public static final String USER_SUPPLIER = "Supplier";

	public static final String SUPPLIER_SUCCESS = "suppliersuccess";
	
	public static final String SUPPLIER_INPUT = "supplierinput";

	private AddressBookService addressBookService;
	
	private SupplierService supplierService;
	
	private User user;
	
	private OrganizationAddress userOrgAddress;
	
	private Supplier supplier;
	
	private Location supplierLocation;
	
	private String id;
	
	private List<String> countriesFromMSA = new ArrayList<String>();
	
	private String stateCode;
	private String cityCode;
	private String zipCode;
	
	private String context;
	
	private String locationCode;
	
	private final SortedHashMap <String, String> countryList = new SortedHashMap<String, String>();
	
	private MSAService msaService;

	public void prepare() {
		List<Country> countries = msaService.getCountryList();
		for (Country country : countries) {
			countryList.put(country.getCode(), country.getName());
		}
		countriesFromMSA = msaService.getCountriesFromMSA();
	}

	@Override
	protected PageResult<?> getBody() {
		if (USER_SUPPLIER.equalsIgnoreCase(getContext()))
			return supplierService.findLocationsForSupplier(getCriteria(), getLoggedInUserAsSupplier());
		return orgService.getAddressesForOrganization(getCriteria(),
				getLoggedInUser().getBelongsToOrganization());
	}

	protected ListCriteria getCriteria() {
		if (isDownloadMaxResults()) {
			pageSize = MAX_DOWNLOADABLE_ROWS;
		}
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
			boolean ascending = !sort[1].equals(SORT_DESCENDING);
			criteria.addSortCriteria(addAlias(sortOnColumn), ascending);
		}
	}

	private void addFilterCriteria(ListCriteria criteria) {
		for (String filterName : filters.keySet()) {
			String filterValue = filters.get(filterName);
			criteria.addFilterCriteria(addAlias(filterName), filterValue);
		}
	}

	private String addAlias(String sortOnColumn) {
		if (StringUtils.hasText(getAlias())) {
			return getAlias() + "." + sortOnColumn;
		}
		return sortOnColumn;
	}

	@Override
	protected List<SummaryTableColumn> getHeader() {
		this.tableHeadData = new ArrayList<SummaryTableColumn>();
		// REFER to SupplierRepositoryImpl's addFilterRestrictionOnQuery() and addSortRestrictionOnQuery()
		// API's before changing the id & expression attributes. Those are referred in the above mentioned
		// class and API's
		if (USER_DEALER.equalsIgnoreCase(getContext()))
		{
			this.tableHeadData.add(new SummaryTableColumn("",
					"id", 0, "number", "id", false, true, true, false));
			this.tableHeadData.add(new SummaryTableColumn("columnTitle.userMgmt.location",
					"location", 100, "string", "location", true, false, false, false));
		}
		else if (USER_SUPPLIER.equalsIgnoreCase(getContext()))
		{
			this.tableHeadData.add(new SummaryTableColumn("",
					"id", 0, "number", "id", false, true, true, false));
			this.tableHeadData.add(new SummaryTableColumn("columnTitle.userMgmt.locationCode",
					"code", 50, "string", "code", true, false, false, false));
			this.tableHeadData.add(new SummaryTableColumn("columnTitle.userMgmt.locationAddress",
					"supplierLocationAddress", 50, "string", "supplierLocationAddress", true, false, false, false));
		}
		return this.tableHeadData;
	}
	
	public BeanProvider getBeanProvider() {
		return new DefaultPropertyResolver() {
			@Override
			public Object getProperty(String propertyPath, Object root) {
				Object value = null;
				if ("supplierLocationAddress".equals(propertyPath)) {
					StringBuffer address = new StringBuffer((String)super.getProperty("address.addressLine1", root));
					if (StringUtils.hasText(((String)super.getProperty("address.addressLine2", root))))
						address.append(" "+(String)super.getProperty("address.addressLine2", root));
					if (StringUtils.hasText(((String)super.getProperty("address.city", root))))
						address.append("-"+(String)super.getProperty("address.city", root));
					if (StringUtils.hasText(((String)super.getProperty("address.state", root))))
						address.append("-"+(String)super.getProperty("address.state", root));
					if (StringUtils.hasText(((String)super.getProperty("address.country", root))))
						address.append("-"+(String)super.getProperty("address.country", root));
					if (StringUtils.hasText(((String)super.getProperty("address.zipCode", root))))
						address.append("-"+(String)super.getProperty("address.zipCode", root));
					value = address.toString();
					return value;
				} else {
					return super.getProperty(propertyPath, root);
				}
			}
		};
	}

	public String detail()
	{
		user = getLoggedInUser();
		String resultOfDetail = SUCCESS;
		if (USER_SUPPLIER.equalsIgnoreCase(getContext()))
		{
			// Supplier Detail
			resultOfDetail = supplierDetail();
		}
		else if (USER_DEALER.equalsIgnoreCase(getContext()))
		{
			// Dealer Detail
			resultOfDetail = dealerDetail();
		}
		return resultOfDetail;
	}

	private String supplierDetail() {
		Address address = user.getAddress();
		supplier = getLoggedInUserAsSupplier();
		List<Location> supplierLocations = supplierService.findLocationsForSupplier(supplier); 
		for (Iterator iter = supplierLocations.iterator(); iter.hasNext();) {
			Location suppLocation = (Location) iter.next();
			if (new Long(getId()).equals(suppLocation.getId()))
			{
				this.supplierLocation = suppLocation;
				locationCode = supplierLocation.getCode();
				break;
			}
		} 
		if (!countriesFromMSA.contains(supplierLocation.getAddress().getCountry())) {
			stateCode = supplierLocation.getAddress().getState();
			cityCode = supplierLocation.getAddress().getCity();
			zipCode = supplierLocation.getAddress().getZipCode();
		}
		return SUPPLIER_SUCCESS;
	}

	private String dealerDetail() {
		Address address = user.getAddress();
		List<OrganizationAddress> orgAddresses = orgService.getAddressesForOrganization(user.getBelongsToOrganization());
		for (Iterator iter = orgAddresses.iterator(); iter.hasNext();) {
			OrganizationAddress orgAddress = (OrganizationAddress) iter.next();
			if (new Long(getId()).equals(orgAddress.getId()))
			{
				this.userOrgAddress = orgAddress;
				break;
			}
		} 
		if (!countriesFromMSA.contains(userOrgAddress.getCountry())) {
			stateCode = userOrgAddress.getState();
			cityCode = userOrgAddress.getCity();
			zipCode = userOrgAddress.getZipCode();
		}
		return SUCCESS;
	}

	// Update or create a new User Location
	public String updateOrCreateUserLocation()
	{
		user = getLoggedInUser();
		String resultBasedOnValidation = validateAddress();
		if (!SUCCESS.equalsIgnoreCase(resultBasedOnValidation))
			return resultBasedOnValidation;
		if (USER_DEALER.equalsIgnoreCase(getContext()))
		{
			createOrUpdateDealershipLocation();
		}
		else if (USER_SUPPLIER.equalsIgnoreCase(getContext()))
		{
			try{
				createOrUpdateSupplierLocation();
			}
			catch (Exception exception)
			{
				logger.debug("Exception when creating/updating Supplier: " + exception);
				return SUPPLIER_INPUT;
			}
			return SUPPLIER_SUCCESS;
		}
		return SUCCESS;
	}

	public boolean checkForValidatableCountry(String country) {
		if (countriesFromMSA.contains(country)) {
			return true;
		}
		return false;
	}

	private void createOrUpdateSupplierLocation() {
		if (!countriesFromMSA.contains(supplierLocation.getAddress().getCountry())) {
			supplierLocation.getAddress().setState(stateCode);
			supplierLocation.getAddress().setCity(cityCode);
			supplierLocation.getAddress().setZipCode(zipCode);
		}
		if (supplierLocation.getId()==null)
		{
			// Creating a new location for the Supplier
			supplier = getLoggedInUserAsSupplier();
			supplierLocation.setCode(locationCode);
			supplierService.saveLocation(supplierLocation);
			supplierService.createLocationForSupplier(supplier.getId(), supplierLocation, LOCATION_TYPE_BUSINESS);
			addActionMessage("message.userLocation.supplier.createSuccess");
		}
		else
		{
			// Updating location for the Supplier
			//supplierLocation.setCode(locationCode);
			supplierService.updateSupplierLocation(supplierLocation);
			addActionMessage("message.userLocation.supplier.updateSuccess");
		}
	}

	private void createOrUpdateDealershipLocation() {
		if (!countriesFromMSA.contains(userOrgAddress.getCountry())) {
			userOrgAddress.setState(stateCode);
			userOrgAddress.setCity(cityCode);
			userOrgAddress.setZipCode(zipCode);
		}
		if (userOrgAddress.getId()==null)
		{
			// Creating a new location for the user
			userOrgAddress.setLocation(prepareLocation(userOrgAddress));
			userOrgAddress.setSiteNumber(getLoggedInUsersDealership().getDealerNumber()+"-");
			orgService.createOrgAddressForDealer(userOrgAddress, getLoggedInUsersDealership().getId());
			// Creating Address Book Mapping for the dealer
			createOrUpdateAddressBookMapping(userOrgAddress);
			addActionMessage("message.userLocation.dealer.createSuccess");
		}
		else
		{
			userOrgAddress.setLocation(prepareLocation(userOrgAddress));
			orgService.updateOrganizationAddress(userOrgAddress);
			addActionMessage("message.userLocation.dealer.updateSuccess");
		}
	}

	private void createOrUpdateAddressBookMapping(Address userAddress) {
		// Creating Address Book Mapping for the dealer
		AddressBookAddressMapping addressBookAddressMapping = new AddressBookAddressMapping();
		AddressBook addressBook = this.addressBookService
				.getAddressBookByOrganizationAndType(getLoggedInUser()
						.getBelongsToOrganization(), AddressBookType.SELF);
		addressBookAddressMapping.setAddressBook(addressBook);
		addressBookAddressMapping.setAddress(userAddress);
		addressBookAddressMapping.setPrimary(Boolean.FALSE);
		this.addressBookService.createAddressBookAddressMapping(addressBookAddressMapping);
	}
	
	private String prepareLocation(OrganizationAddress userOrgAddress) {
		StringBuffer orgLocation = new StringBuffer(userOrgAddress.getAddressLine1());
		if (StringUtils.hasText(userOrgAddress.getAddressLine2()))
			orgLocation.append("-"+userOrgAddress.getAddressLine2());
		
		if (StringUtils.hasText(userOrgAddress.getAddressLine3()))
			orgLocation.append("-"+userOrgAddress.getAddressLine3());
		
		if (StringUtils.hasText(userOrgAddress.getCity()))
			orgLocation.append("-"+userOrgAddress.getCity());
		
		if (StringUtils.hasText(userOrgAddress.getState()))
			orgLocation.append("-"+userOrgAddress.getState());

		if (StringUtils.hasText(userOrgAddress.getZipCode()))
			orgLocation.append("-"+userOrgAddress.getZipCode());

		if (StringUtils.hasText(userOrgAddress.getCountry()))
			orgLocation.append("-"+userOrgAddress.getCountry());
		
		return orgLocation.toString();
	}

	private String validateAddress() {
		boolean validAddressCombination = Boolean.TRUE;
		boolean validLocationCode = Boolean.TRUE;
		
		if (USER_DEALER.equalsIgnoreCase(getContext()))
		{
			validAddressCombination = validateDealerAddress(validAddressCombination);
			
			//Added validation for duplicate supplier locations TKTSA-745
			if(userOrgAddress.getId() == null)
			  validLocationCode = validateDuplicateServicingLocationForUser(validLocationCode);
			
			if ((!StringUtils.hasText(userOrgAddress.getAddressLine1())
					|| !StringUtils.hasText(userOrgAddress.getCountry()))
					|| !validAddressCombination || !validLocationCode)
				return INPUT;
		}
		else if (USER_SUPPLIER.equalsIgnoreCase(getContext()))
		{
			validAddressCombination = validateSupplierAddress(validAddressCombination);
			
			//Added validation for duplicate supplier locations TKTSA-745
			if(supplierLocation.getId() == null)
			  validLocationCode = validateDuplicateLocationCodeForSupplier(validLocationCode);
			
			if ((!StringUtils.hasText(supplierLocation.getAddress().getAddressLine1())
					|| !StringUtils.hasText(supplierLocation.getAddress().getCountry()))
					|| !validAddressCombination || !validLocationCode)
				return SUPPLIER_INPUT;
		}
		return SUCCESS;
	}		
			
	private boolean validateDuplicateLocationCodeForSupplier(boolean validLocationCode){
		List<Location> supplierLocations = supplierService.findLocationsForSupplier(getLoggedInUserAsSupplier());
		for(Location supplierLocation : supplierLocations){
			if(supplierLocation.getCode().equalsIgnoreCase(locationCode)){
				addActionError("error.supplierLocation.duplicateCode",new String[] { locationCode });
				validLocationCode = Boolean.FALSE;
				break;
			}
		}
		return validLocationCode;		
	}
	
	private boolean validateDuplicateServicingLocationForUser(boolean validLocationCode){
		if (!countriesFromMSA.contains(userOrgAddress.getCountry())) {
			userOrgAddress.setState(stateCode);
			userOrgAddress.setCity(cityCode);
			userOrgAddress.setZipCode(zipCode);
		}
		 List<OrganizationAddress> servicingLocations = orgService.getAddressesForOrganization(getLoggedInUser().getBelongsToOrganization());
		 for(OrganizationAddress servicingLocation : servicingLocations){
				if(servicingLocation.getLocation().equalsIgnoreCase(prepareLocation(userOrgAddress))){
					addActionError("error.userLocation.duplicateServicingLocation",new String[] { prepareLocation(userOrgAddress) });
					validLocationCode = Boolean.FALSE;
					break;
				}
			}
		 return validLocationCode;
		
	}	

	private boolean validateSupplierAddress(boolean validAddressCombination) {
		if (!StringUtils.hasText(supplierLocation.getAddress().getAddressLine1())){
			addActionError("error.manageProfile.requiredAddress");
			validAddressCombination = Boolean.FALSE;
		}
		if (!StringUtils.hasText(supplierLocation.getAddress().getCountry())){
			addActionError("error.manageProfile.requiredCountry");
			validAddressCombination = Boolean.FALSE;
		}
		if (supplierLocation.getId() == null && !StringUtils.hasText(locationCode)){
			addActionError("error.manageProfile.requiredLocationCode");
			validAddressCombination = Boolean.FALSE;
		}
		// To validate Country, State, City and Zip code combination
		if (!countriesFromMSA.contains(supplierLocation.getAddress().getCountry())) {			
			if (cityCode == null || "".equals(cityCode.trim())) {
				addActionError("error.manageProfile.requiredCity");
				validAddressCombination = validAddressCombination && Boolean.FALSE;
			}
			if (zipCode == null || "".equals(zipCode.trim())) {
				addActionError("error.manageProfile.requiredZipcode");
				validAddressCombination = validAddressCombination && Boolean.FALSE;
			}
		}
		else
		{
			if (!StringUtils.hasText(supplierLocation.getAddress().getCity()))
			{
				addActionError("error.manageProfile.requiredCity");
				validAddressCombination = validAddressCombination && Boolean.FALSE;
			}
			if (!StringUtils.hasText(supplierLocation.getAddress().getState()))
			{
				addActionError("error.manageProfile.requiredState");
				validAddressCombination = validAddressCombination && Boolean.FALSE;
			}
			if (!StringUtils.hasText(supplierLocation.getAddress().getZipCode()))
			{
				addActionError("error.manageProfile.requiredZipcode");
				validAddressCombination = validAddressCombination && Boolean.FALSE;
			}
		}
		return validAddressCombination;
	}

	private boolean validateDealerAddress(boolean validAddressCombination) {
		if (!StringUtils.hasText(userOrgAddress.getAddressLine1())){
			addActionError("error.manageProfile.requiredAddress");
			validAddressCombination = Boolean.FALSE;
		}
		if (!StringUtils.hasText(userOrgAddress.getCountry())){
			addActionError("error.manageProfile.requiredCountry");
			validAddressCombination = Boolean.FALSE;
		}
		// To validate Country, State, City and Zip code combination
		if (!countriesFromMSA.contains(userOrgAddress.getCountry())) {			
			if (cityCode == null || "".equals(cityCode.trim())) {
				addActionError("error.manageProfile.requiredCity");
				validAddressCombination = validAddressCombination && Boolean.FALSE;
			}
			if (zipCode == null || "".equals(zipCode.trim())) {
				addActionError("error.manageProfile.requiredZipcode");
				validAddressCombination = validAddressCombination && Boolean.FALSE;
			}
		}
		else
		{
			if (!StringUtils.hasText(userOrgAddress.getCity()))
			{
				addActionError("error.manageProfile.requiredCity");
				validAddressCombination = validAddressCombination && Boolean.FALSE;
			}
			if (!StringUtils.hasText(userOrgAddress.getState()))
			{
				addActionError("error.manageProfile.requiredState");
				validAddressCombination = validAddressCombination && Boolean.FALSE;
			}
			if (!StringUtils.hasText(userOrgAddress.getZipCode()))
			{
				addActionError("error.manageProfile.requiredZipcode");
				validAddressCombination = validAddressCombination && Boolean.FALSE;
			}
		}
		return validAddressCombination;
	}

    /**
     * If the currently logged in user is a Supplier, returns that Supplier; otherwise returns null.
     * @return Logged in User's supplier Company, if applicable.
     */
    public Supplier getLoggedInUserAsSupplier() {
        Organization organization = getLoggedInUser().getBelongsToOrganization();
        
        return (InstanceOfUtil.isInstanceOfClass( Supplier.class, organization)) ? (new HibernateCast<Supplier>().cast(organization)) : null;
    }

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getCountriesFromMSA() {
		return countriesFromMSA;
	}

	public void setCountriesFromMSA(List<String> countriesFromMSA) {
		this.countriesFromMSA = countriesFromMSA;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public MSAService getMsaService() {
		return msaService;
	}

	public void setMsaService(MSAService msaService) {
		this.msaService = msaService;
	}

	public SortedHashMap<String, String> getCountryList() {
		return countryList;
	}

	public OrganizationAddress getUserOrgAddress() {
		return userOrgAddress;
	}

	public void setUserOrgAddress(OrganizationAddress userOrgAddress) {
		this.userOrgAddress = userOrgAddress;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public AddressBookService getAddressBookService() {
		return addressBookService;
	}

	public void setAddressBookService(AddressBookService addressBookService) {
		this.addressBookService = addressBookService;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	public SupplierService getSupplierService() {
		return supplierService;
	}

	public void setSupplierService(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	public Location getSupplierLocation() {
		return supplierLocation;
	}

	public void setSupplierLocation(Location supplierLocation) {
		this.supplierLocation = supplierLocation;
	}
	
}