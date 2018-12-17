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
package tavant.twms.web.warranty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import tavant.twms.domain.catalog.CatalogRepository;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyRatesCriteria;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.policy.WarrantyRegistrationType;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.infra.BaseDomain;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.i18n.I18nActionSupport;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;
import com.opensymphony.xwork2.Preparable;

/**
 * @author kiran.sg
 */
@SuppressWarnings("serial")
public class CustomerWarrantyRegistration extends I18nActionSupport implements Preparable {

    private static final int FIRST_PAGE = 0;

    private static final int N_SLNOS_AUTOSUGGEST = 20;

    private static Logger logger = LogManager.getLogger(CustomerWarrantyRegistration.class);

    private Map session;

    private String jsonString;

    private String startsWith;

    private List<String> serialNumberList = new ArrayList<String>();

    private List<Warranty> warrantyList = new ArrayList<Warranty>();

    private List<RegisteredPolicy> availablePolicies = new ArrayList<RegisteredPolicy>();

    private List<Long> selectedPolicyDefinitionIds = new ArrayList<Long>();

    private List<Item> product = new ArrayList<Item>();

    private List<CalendarDate> deliveryDateList = new ArrayList<CalendarDate>();

    private List<Integer> hoursOnMachineList = new ArrayList<Integer>();

    private CatalogRepository catalogRepository;

    private InventoryService inventoryService;

    private PolicyService policyService;

    private WarrantyService warrantyService;

    private Long itemId;

    private Integer index;


    public void prepare() throws Exception {
        this.warrantyList.clear();

        for (int i = 0; i < this.serialNumberList.size(); i++) {
            if (this.serialNumberList.get(i) != null) {
                Warranty warranty = new Warranty();
                try {
                    if (this.serialNumberList.get(i).length() != 0) {
                        InventoryItem inventoryItem = this.inventoryService
                                .findSerializedItem(this.serialNumberList.get(i));
                        inventoryItem.setDeliveryDate(this.deliveryDateList.get(i));
                        inventoryItem.setHoursOnMachine((this.hoursOnMachineList.get(i)).longValue());
                        warranty.setInventoryItem(inventoryItem);
                    }
                } catch (ItemNotFoundException e) {
                    addFieldError("serialNumberList[" + i + "]", "error.notFoundSerialNumber",
                            this.serialNumberList.get(i));
                    logger.error("Inventory item with serialNumber[" + this.serialNumberList.get(i)
                            + "] not found.");
                }
                // Set the customer
                // Customer customer = (Customer) getLoggedInUser();
                // warranty.setCustomer(customer);
                // warranty.setCustomerAddress(customer.getPrimaryAddress());
                this.warrantyList.add(warranty);
            }
        }
    }

    @Override
    public void validate() {
        for (int i = 0; i < this.serialNumberList.size(); i++) {
            // User has not entered the Product and Model
            if (!this.product.isEmpty() && this.product.get(i) == null) {
                addFieldError("product[" + i + "]",
                        "message.customerWarrantyRegistration.chooseProductAndModel");
            }

            if (this.serialNumberList.get(i) != null) {

                // Serial Number Validation
                if (this.serialNumberList.get(i).trim().length() == 0) {
                    addFieldError("serialNumberList[" + i + "]", "message.chooseSerialNumber");
                } else if (this.warrantyList.get(i).getForItem() != null) {
                    if (this.warrantyService.findWarranty(this.warrantyList.get(i).getForItem()) != null) {
                        addFieldError("serialNumberList[" + i + "]", "error.warrantyAlreadyExists",
                                this.warrantyList.get(i).getForItem().getSerialNumber());

                    } else if (!this.product.isEmpty()
                            && this.product.get(i) != null
                            && !this.warrantyList.get(i).getForItem().getOfType().equals(
                                    this.product.get(i))) {
                        addFieldError("serialNumberList[" + i + "]",
                                getText("invalid.serialNumber.forModel"));
                    }
                } else {

                }

                // Delivery Date Validation
                if (this.deliveryDateList.get(i) == null) {
                    addFieldError("deliveryDateList[" + i + "]", "message.chooseDeliveryDate");
                } else if (this.warrantyList.get(i).getForItem() != null
                        && this.deliveryDateList.get(i).isBefore(
                                this.warrantyList.get(i).getForItem().getShipmentDate())) {
                    addFieldError("deliveryDateList[" + i + "]",
                            "error.deliveryDateBeforeShipment", this.warrantyList.get(i)
                                    .getForItem().getShipmentDate());
                } else if (this.deliveryDateList.get(i).isAfter(Clock.today())) {
                    addFieldError("deliveryDateList[" + i + "]",
                            "message.customerWarrantyRegistration.deilveryDateBeforeToday");
                }

                // Hours On Machine Validation
                if (this.hoursOnMachineList.get(i) == null) {
                    addFieldError("hoursOnMachineList[" + i + "]", "error.hoursOnMachineNotFound");
                }
            }
        }
    }

    public String show() {
        // Adding a dummy Serial Number, so that at least one row appears by
        // default
        this.serialNumberList.add("");
        return SUCCESS;
    }

    public String register() {
        for (int i = 0; i < this.warrantyList.size(); i++) {
            InventoryItem inventoryItem = this.warrantyList.get(i).getForItem();
            try {
                if (inventoryItem != null && inventoryItem.getDeliveryDate() != null) {
                    this.availablePolicies = fetchAvailablePolicies(inventoryItem);
                }
                registerWarranty(this.warrantyList.get(i), inventoryItem);
            } catch (Exception ex) {
                logger.error("Error saving warranty for " + inventoryItem, ex);
                addFieldError("serialNumberList[" + i + "]", "error.registeringWarranty",
                        inventoryItem.getSerialNumber());
                return INPUT;
            }
        }

        // TODO Bulk update all the InventoryItem
        for (Warranty warranty : this.warrantyList) {
            InventoryItem inventoryItem = warranty.getForItem();
            this.inventoryService.updateInventoryItem(inventoryItem);
            addActionMessage("message.warrantyRegisteredForItem", inventoryItem.getSerialNumber());
        }

        return SUCCESS;
    }

    public String listProductsAndModels() {
        JSONArray listOfEntries = new JSONArray();
        List<Item> items = fetchMatchingItems();
        for (Item item : items) {
            JSONArray entry = new JSONArray();
            String product = item.getProduct().getName();
            String model = item.getModel().getName();
            entry.put(product + " - " + model).put(item.getId());
            listOfEntries.put(entry);
        }
        this.jsonString = listOfEntries.toString();
        return SUCCESS;
    }

    public String getInventoryItem() {
        String serialNumber = this.serialNumberList.get(0).trim();
        if (serialNumber.length() != 0) {
            try {
                InventoryItem inventoryItem = this.inventoryService
                        .findSerializedItem(serialNumber);
                this.warrantyList.clear();
                Warranty warranty = new Warranty();
                warranty.setInventoryItem(inventoryItem);
                this.warrantyList.add(warranty);
                if (!inventoryItem.getOfType().getId().equals(this.itemId)) {
                    addActionError(getText("invalid.serialNumber.forModel"));
                    return INPUT;
                }
                return SUCCESS;
            } catch (ItemNotFoundException e) {
                addActionError(getText("error.notFoundSerialNumber"), serialNumber);
                return INPUT;
            }
        }
        return INPUT;
    }

    private List<RegisteredPolicy> fetchAvailablePolicies(InventoryItem inventoryItem)
            throws PolicyException {
        List<PolicyDefinition> policyDefinitions = new ArrayList<PolicyDefinition>();
        try {
            policyDefinitions = this.policyService
                    .findPoliciesAvailableForRegistration(inventoryItem,null,null);
            // Only the policies with price zero are selected
            // TODO replace with a service call.
            for (Iterator<PolicyDefinition> iter = policyDefinitions.iterator(); iter.hasNext();) {
                PolicyDefinition policyDefinition = iter.next();
                if (!policyDefinition.isAvailableByDefault()) {
                    iter.remove();
                }
            }
        } catch (PolicyException pex) {
            logger.error("Error fetching policies available for InventoryItem[slNo:"
                    + inventoryItem.getSerialNumber() + "]", pex);
        }

        return createPolicies(policyDefinitions, inventoryItem);
    }

    private List<RegisteredPolicy> createPolicies(List<PolicyDefinition> withDefinitions,
            InventoryItem forItem) throws PolicyException {
        List<RegisteredPolicy> policies = new ArrayList<RegisteredPolicy>();
        for (PolicyDefinition definition : withDefinitions) {
            String policyName = definition.getCode();
            if ((policyName == null) || (policyName.trim().equals(""))) {
                continue; // ignore this policy definition
            }
            RegisteredPolicy policy = new RegisteredPolicy();
            policy.setPolicyDefinition(definition);
            policy.setWarrantyPeriod(definition.warrantyPeriodFor(forItem));

            PolicyRatesCriteria criteria = new PolicyRatesCriteria();
            criteria.setProductType(forItem.getOfType().getProduct());
            // criteria.setCustomerState(((Customer)
            // getLoggedInUser()).getPrimaryAddress().getState());
            criteria.setWarrantyRegistrationType(WarrantyRegistrationType.REGISTRATION);
            Money price = this.policyService.getPolicyFeeForPolicyDefinition(definition, criteria,
                    forItem.getDeliveryDate());
            policy.setPrice(price);
            policies.add(policy);
        }
        Collections.sort(policies, new Comparator<RegisteredPolicy>() {
            public int compare(RegisteredPolicy p1, RegisteredPolicy p2) {
                if (p1.getPolicyDefinition().isAvailableByDefault()) {
                    if (p2.getPolicyDefinition().isAvailableByDefault()) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    if (p2.getPolicyDefinition().isAvailableByDefault()) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        });
        return policies;
    }

    /**
     * Fetches all items which has item name starting with startsWith
     */
    private List<Item> fetchMatchingItems() {

        PageSpecification pageSpecification = new PageSpecification(FIRST_PAGE, N_SLNOS_AUTOSUGGEST);
        int productNamePrefixLength = this.startsWith.indexOf(" - ");
        productNamePrefixLength = productNamePrefixLength == -1 ? this.startsWith.length()
                : productNamePrefixLength;
        String productNamePrefix = this.startsWith.substring(0, productNamePrefixLength);
        return this.catalogRepository.findProdutsWithNameStartingWith(productNamePrefix,
                pageSpecification);
    }

    private void registerWarranty(Warranty warranty, InventoryItem inventoryItem) {
        for (RegisteredPolicy registeredPolicy : this.availablePolicies) {
            this.warrantyService.register(warranty,registeredPolicy.getPolicyDefinition(), registeredPolicy
                    .getWarrantyPeriod(), registeredPolicy.getPolicyDefinition().getAvailability()
                    .getPrice(), null, null, null);

        }
        warranty.setInventoryItem(inventoryItem);

        inventoryItem.setType(new InventoryType("RETAIL"));
        inventoryItem.setRegistrationDate(Clock.today());
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setStatus(BaseDomain.ACTIVE);
        transaction.setSeller(inventoryItem.getOwnedBy());
        transaction.setOwnerShip(getLoggedInUsersDealership());
        transaction.setBuyer(warranty.getCustomer());
        transaction.setTransactionDate(Clock.today());
        transaction.setTransactedItem(inventoryItem);
        inventoryItem.getTransactionHistory().add(transaction);
    }

    public String getJsonString() {
        return this.jsonString;
    }

    public void setStartsWith(String startsWith) {
        this.startsWith = startsWith;
    }

    public void setInventoryService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void setPolicyService(PolicyService policyService) {
        this.policyService = policyService;
    }

    public void setWarrantyService(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    public List<RegisteredPolicy> getAvailablePolicies() {
        return this.availablePolicies;
    }

    public boolean areNonDefaultPoliciesAvailable() {
        // the available policies are sorted on "by-defaultness"
        // if the last policy on the list is available by default, we know
        // that there are no non-default policies available
        return !this.availablePolicies.get(this.availablePolicies.size() - 1).getPolicyDefinition()
                .isAvailableByDefault();
    }

    public void setSelectedPolicyDefinitionIds(List<Long> chosenPolicyIds) {
        this.selectedPolicyDefinitionIds = chosenPolicyIds;
    }

    public List<Long> getSelectedPolicyDefinitionIds() {
        return this.selectedPolicyDefinitionIds;
    }

    public CatalogRepository getCatalogRepository() {
        return this.catalogRepository;
    }

    public void setCatalogRepository(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public Long getItemId() {
        return this.itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Integer getIndex() {
        return this.index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public List<CalendarDate> getDeliveryDateList() {
        return this.deliveryDateList;
    }

    public void setDeliveryDateList(List<CalendarDate> deliveryDateList) {
        this.deliveryDateList = deliveryDateList;
    }

    public List<Integer> getHoursOnMachineList() {
        return this.hoursOnMachineList;
    }

    public void setHoursOnMachineList(List<Integer> hoursOnMachineList) {
        this.hoursOnMachineList = hoursOnMachineList;
    }

    public List<Warranty> getWarrantyList() {
        return this.warrantyList;
    }

    public void setWarrantyList(List<Warranty> warrantyList) {
        this.warrantyList = warrantyList;
    }

    public List<String> getSerialNumberList() {
        return this.serialNumberList;
    }

    public void setSerialNumberList(List<String> serialNumberList) {
        this.serialNumberList = serialNumberList;
    }

    public List<Item> getProduct() {
        return this.product;
    }

    public void setProduct(List<Item> product) {
        this.product = product;
    }


}