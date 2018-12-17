package tavant.twms.web.admin.groups.user;

import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.springframework.util.StringUtils;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.infra.PageResult;
import tavant.twms.web.actions.SortedHashMap;
import tavant.twms.web.actions.TwmsActionSupport;
import tavant.twms.web.admin.supplier.ManageContract;
import tavant.twms.web.i18n.I18nActionSupport;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 16/9/13
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class ManageVendorAddresses  extends SummaryTableAction {

    private static final String LOCATION_TYPE_BUSINESS = "BUSINESS";
    SupplierService supplierService;
    private Supplier supplier;
    private Address address;
    private OrgService orgService;

    private Location supplierLocation;

    private final SortedHashMap<String, String> countryList = new SortedHashMap<String, String>();

    private List<String> countriesFromMSA = new ArrayList<String>();

    private MSAService msaService;

    private String stateCode;
    private String cityCode;
    private String zipCode;

    private String locationCode;

    private boolean newAddressCreation = false;

    private boolean editAddress = false;
    private ConfigParamService configParamService;

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

    @Override
    protected PageResult<?> getBody() {
        return supplierService.findAllSuppliers(getCriteria());
    }

    @Override
    protected List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("id", "id", 0, "String", "id", true, true, true, false));
        tableHeadData.add(new SummaryTableColumn("Supplier Number", "supplierNumber", 15, "String", "supplierNumber",
                true, false, false, false));
        tableHeadData.add(new SummaryTableColumn("columnTitle.listContracts.supplier_name", "name", 20, "String"));
        /**
         * Commenting below line as contact person name has been removed
         */
        /*tableHeadData.add(new SummaryTableColumn("Contact Person Name", "supplierAddress.contactPersonName", 20,
               "String"));*/
        //tableHeadData.add(new SummaryTableColumn("Address", "displayAddress", 45, "String"));
        return tableHeadData;
    }

    public void setSupplierService(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String detail() {
        if (id == null || "".equals(id)) {
            supplier = supplierService.findById(supplier.getId());
        } else {
            supplier = supplierService.findById(new Long(id));
        }
        populateLocationorSiteNumber();
        return SUCCESS;
    }

    /**
     * to populate location/site number on address UI while creating supplier
     */
    private void populateLocationorSiteNumber() {
        if(null != supplier && (null != supplier.getLocations() && supplier.getLocations().size() > 0)){
            List<Location> locations = supplier.getLocations();
            supplier.getAddress().setLocation(locations.get(supplier.getLocations().size() - 1).getCode());
        }
    }

    public String addressDetail(){
        List<Country> countries = msaService.getCountryList();
        for (Country country : countries) {
            countryList.put(country.getCode(), country.getName());
        }
        return SUCCESS;
    }

    public void prepare() {
        List<Country> countries = msaService.getCountryList();
        for (Country country : countries) {
            countryList.put(country.getCode(), country.getName());
        }
        countriesFromMSA = msaService.getCountriesFromMSA();
    }

    public String populateForm(){
        List<Country> countries = msaService.getCountryList();
        for (Country country : countries) {
            countryList.put(country.getCode(), country.getName());
        }
        return SUCCESS;
    }

    public String updateAddress(){
        if(this.address == null){
            return INPUT;
        }
        if(validateUpdateAddress().equals(INPUT)){
            List<Country> countries = msaService.getCountryList();
            for (Country country : countries) {
                countryList.put(country.getCode(), country.getName());
            }
            return INPUT;
        }
        orgService.updateAddress(this.address);
        addActionMessage("message.itemStatus.updated");
        return SUCCESS;
    }

    private String validateUpdateAddress(){
       if(this.address.getAddressLine1() == null || !StringUtils.hasText(this.address.getAddressLine1())) {
           addActionError("error.manageProfile.requiredAddress");
       }
        if(this.address.getCountry() == null || !StringUtils.hasText(this.address.getCountry())) {
            addActionError("error.manageProfile.requiredCountry");
        }

        //SLMSPROD-652 state and zip is optional for emea -- configurable
        if(!configParamService.getBooleanValue(ConfigName.OPTIONAL_POSTAL_AND_ZIP_CODE_FOR_VENDOR_SITE.getName())){
            if(this.address.getState() == null || !StringUtils.hasText(this.address.getState())) {
                addActionError("error.manageProfile.requiredState");
            }
            if(this.address.getZipCode() == null || !StringUtils.hasText(this.address.getZipCode())) {
                addActionError("error.manageProfile.requiredZipcode");
            }
        }

        if(this.address.getCity() == null || !StringUtils.hasText(this.address.getCity())) {
            addActionError("error.manageProfile.requiredCity");
        }

        if(StringUtils.hasText(this.address.getEmail()) && !this.address.getEmail().contains("@")) {
            addActionError("error.manageCustomer.invalidEmail");
        }
        if(hasActionErrors()){
            setAddress(this.address);
            editAddress = true;
            return INPUT;
        }
        return SUCCESS;
    }

    public String createNewSupplierAddress(){
        String resultBasedOnValidation = validateAddress();
        if (!SUCCESS.equalsIgnoreCase(resultBasedOnValidation)) {
            newAddressCreation = true;
            List<Country> countries = msaService.getCountryList();
            for (Country country : countries) {
                countryList.put(country.getCode(), country.getName());
            }
           /* if (!configParamService.getBooleanValue(ConfigName.OPTIONAL_POSTAL_AND_ZIP_CODE_FOR_VENDOR_SITE.getName()) && !StringUtils.hasText(getStateCode()))
            {
                addActionError("error.manageProfile.requiredState");
                resultBasedOnValidation = INPUT;
            }*/
            return resultBasedOnValidation;
        }
        createOrUpdateSupplierLocation();
        return SUCCESS;
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

    private String validateAddress() {
        boolean validAddressCombination = Boolean.TRUE;
        boolean validLocationCode = Boolean.TRUE;

            validAddressCombination = validateSupplierAddress(validAddressCombination);

            //Added validation for duplicate supplier locations TKTSA-745
            if(supplierLocation.getId() == null)
                validLocationCode = validateDuplicateLocationCodeForSupplier(validLocationCode);

            if(StringUtils.hasText(supplierLocation.getAddress().getEmail()) && !supplierLocation.getAddress().getEmail().contains("@")) {
                addActionError("error.manageCustomer.invalidEmail");
                validAddressCombination = Boolean.FALSE;
            }

            if ((!StringUtils.hasText(supplierLocation.getAddress().getAddressLine1())
                    || !StringUtils.hasText(supplierLocation.getAddress().getCountry()))
                    || !validAddressCombination || !validLocationCode)
                return INPUT;
        return SUCCESS;
    }

    private boolean validateDuplicateLocationCodeForSupplier(boolean validLocationCode){
        List<Location> supplierLocations = this.supplier.getLocations();
        for(Location supplierLocation : supplierLocations){
            if(supplierLocation.getCode().equalsIgnoreCase(locationCode)){
                addActionError("error.supplierLocation.duplicateCode",new String[] { locationCode });
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
            //SLMSPROD-652 state and zip is optional for emea -- configurable
            if(!configParamService.getBooleanValue(ConfigName.OPTIONAL_POSTAL_AND_ZIP_CODE_FOR_VENDOR_SITE.getName())){
                if (zipCode == null || "".equals(zipCode.trim())) {
                    addActionError("error.manageProfile.requiredZipcode");
                    validAddressCombination = validAddressCombination && Boolean.FALSE;
                }
                if (!StringUtils.hasText(stateCode) )
                {
                    addActionError("error.manageProfile.requiredState");
                    validAddressCombination = validAddressCombination && Boolean.FALSE;
                }
            }
        }
        else
        {
            if (!StringUtils.hasText(supplierLocation.getAddress().getCity()))
            {
                addActionError("error.manageProfile.requiredCity");
                validAddressCombination = validAddressCombination && Boolean.FALSE;
            }
            //SLMSPROD-652 state and zip is optional for emea -- configurable
            if(!configParamService.getBooleanValue(ConfigName.OPTIONAL_POSTAL_AND_ZIP_CODE_FOR_VENDOR_SITE.getName())){
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

        }
        return validAddressCombination;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public OrgService getOrgService() {
        return orgService;
    }

    public void setOrgService(OrgService orgService) {
        this.orgService = orgService;
    }

    public Location getSupplierLocation() {
        return supplierLocation;
    }

    public void setSupplierLocation(Location supplierLocation) {
        this.supplierLocation = supplierLocation;
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

    public List<String> getCountriesFromMSA() {
        return countriesFromMSA;
    }

    public void setCountriesFromMSA(List<String> countriesFromMSA) {
        this.countriesFromMSA = countriesFromMSA;
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

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    public boolean isNewAddressCreation() {
        return newAddressCreation;
    }

    public void setNewAddressCreation(boolean newAddressCreation) {
        this.newAddressCreation = newAddressCreation;
    }

    public boolean isEditAddress() {
        return editAddress;
    }

    public void setEditAddress(boolean editAddress) {
        this.editAddress = editAddress;
    }
}
