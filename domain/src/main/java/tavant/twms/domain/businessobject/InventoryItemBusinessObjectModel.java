package tavant.twms.domain.businessobject;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.inventory.InventoryTransactionType;
import tavant.twms.domain.inventory.Option;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.User;
import tavant.twms.domain.orgmodel.UserRepositoryImpl;
import tavant.twms.domain.policy.MarketingInformation;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.domain.policy.Warranty;
import tavant.twms.domain.rules.DomainType;
import tavant.twms.domain.rules.DomainTypeSystem;
import tavant.twms.domain.rules.FunctionField;
import tavant.twms.domain.rules.QueryDomainType;
import tavant.twms.domain.rules.Type;
import tavant.twms.security.SecurityHelper;

public class InventoryItemBusinessObjectModel extends AbstractBusinessObjectModel {

	private static Logger logger = LogManager.getLogger(InventoryItemBusinessObjectModel.class);
	
	private OrgService orgService;
	
	private SecurityHelper securityHelper;

	DomainType domainType;

	 

	public InventoryItemBusinessObjectModel() {
		this(false);
	}
	
	public InventoryItemBusinessObjectModel(boolean AMERInventorySearch){
		domainTypeSystem = new DomainTypeSystem();
		domainType = inventoryItem(AMERInventorySearch);
		discoverPathsToFields(domainType, "inventoryItem");
	}

	protected DomainType inventoryItem(boolean isInternalInstallTypeRequired) {
		String typeName = InventoryItem.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType inventoryItem = new DomainType(
			/* Domain term */"Inventory Item",
			/** Unique Type Name */
			typeName);
			inventoryItem.simpleField("label.common.serialNumber", "serialNumber",Type.STRING);
			inventoryItem.simpleField("label.common.factoryOrderNumber", "factoryOrderNumber",Type.STRING);
			inventoryItem.simpleField("label.common.vinNumber", "vinNumber",Type.STRING);
			inventoryItem.simpleField("label.inventory.dateofManufacture", "builtOn",Type.DATE);
			inventoryItem.simpleField("label.common.dateOfBuild","builtOn",Type.DATE );
			inventoryItem.simpleField("label.inventory.dateOfShipment","shipmentDate",Type.DATE );
			inventoryItem.simpleField("label.common.dateOfDelivery", "deliveryDate",Type.DATE);
			inventoryItem.simpleField("label.common.usage", "hoursOnMachine",Type.INTEGER);
			inventoryItem.simpleField("label.common.condition", "conditionType.itemCondition",Type.STRING);
			inventoryItem.simpleField("label.inventory.inventoryType", "type.type",Type.STRING);
			inventoryItem.simpleField("label.common.businessUnit", "businessUnitInfo",Type.STRING);
			inventoryItem.simpleField("label.common.preOrderBooking", "preOrderBooking", Type.BOOLEAN);
			inventoryItem.simpleField("label.common.MarketingGroupCode", "marketingGroupCode", Type.STRING);
			 
			/*inventoryItem.oneToOne("label.inventory.manufacturingSiteInventory", "manufacturingSiteInventory", manufacturingSite());*/
			inventoryItem.queryTemplate("label.inventory.manufacturingSite",
                    "{alias1}.locale= 'USER_LOCALE'  and lower({alias1}.description)",
                    Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
                    "inventoryItem.manufacturingSiteInventory.i18nLovTexts",
                "{alias1}");
			inventoryItem.queryTemplate("label.common.itemDescription", 
            		"{alias}.locale= 'USER_LOCALE'  and lower({alias}.description)",
            		Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(),
            		"inventoryItem.ofType.i18nItemTexts",
            		"{alias}");
			inventoryItem.oneToOne("label.common.item", "ofType", item());
			inventoryItem.simpleField("columnTitle.common.warrantyStartDate", "wntyStartDate", Type.DATE);
			inventoryItem.simpleField("columnTitle.common.warrantyEndDate", "wntyEndDate", Type.DATE);
			inventoryItem.oneToOne("label.equipmentInfo.servicingDealer", "currentOwner", dealership());
			inventoryItem.oneToOne("label.newClaim.warranty", "latestWarranty", warranty(isInternalInstallTypeRequired));
			inventoryItem.oneToMany("label.claimSearch.campaign","campaignNotifications", campaignNotifications());
			 
		//	inventoryItem.oneToMany("Option", "options", option());
			/** Perf Fix - Begin **/
			/*inventoryItem.queryTemplate("label.warrantyAdmin.ownerName",
					"{alias}.transactionDate=(select max(it.transactionDate) from InventoryTransaction it where it.transactedItem=inventoryItem group by it.transactedItem) and lower({alias}.buyer.name)",
					Type.STRING, true, FunctionField.Types.ONE_TO_MANY
							.getBaseType(), "inventoryItem.transactionHistory",
					"{alias}");

			inventoryItem.queryTemplate("label.common.owningDealerName",
					"{alias}.transactionDate=(select min(it.transactionDate) from InventoryTransaction it where it.transactedItem=inventoryItem group by it.transactedItem) and lower({alias}.buyer.name)",
					Type.STRING, true, FunctionField.Types.ONE_TO_MANY
							.getBaseType(), "inventoryItem.transactionHistory",
					"{alias}"); */

			inventoryItem.simpleField("label.warrantyAdmin.ownerName", "latestBuyer.name",Type.STRING);
            inventoryItem.simpleField("label.common.owningDealerName", "currentOwner.name",Type.STRING);
            inventoryItem.simpleField("label.common.owner.customerclassification", "latestBuyer.customerClassification",Type.STRING);
            /** Perf Fix - End **/
            
			inventoryItem.oneToMany("label.inventory.inventoryTransactions",
					"transactionHistory",
					transactionHistory());

			domainTypeSystem.registerDomainType(inventoryItem);
		}
		return domainTypeSystem.getDomainType(typeName);
	}

	private DomainType transactionHistory() {
        String typeName = InventoryTransaction.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType item = new QueryDomainType(/* Domain term */typeName, /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);
            item.simpleField("label.warrantyAdmin.transactionDate", "transactionDate", Type.DATE);
            item.simpleField("label.common.buyerName", "buyer.name", Type.STRING);
            item.simpleField("label.common.sellerName", "seller.name", Type.STRING);
            item.simpleField("label.common.invoiceNumber", "invoiceNumber", Type.STRING);
            item.simpleField("label.inventory.salesOrderNumber","salesOrderNumber", Type.STRING);
            item.simpleField("label.warranty.transactionType", "invTransactionType.trnxTypeKey", Type.STRING);
            domainTypeSystem.registerDomainType(item);
        }
        return domainTypeSystem.getDomainType(typeName);
    }



	protected DomainType dealership() {
		String typeName = ServiceProvider.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType dealership = new QueryDomainType(
			/* Domain term */"Dealer", /*
										 * Unique Type Name
										 */typeName);
			dealership.simpleField("label.common.name", "name", Type.STRING);
			dealership.simpleField("label.common.number", "dealerNumber", Type.STRING);
			dealership.simpleField("label.common.preferredCurrency",
					"preferredCurrency.code", Type.STRING);
			dealership.simpleField("label.common.owner.customerclassification", "customerClassification", Type.STRING);

			domainTypeSystem.registerDomainType(dealership);
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	protected DomainType policy(){
		String typeName = RegisteredPolicy.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType policy = new QueryDomainType(
			/* Domain term */"Policy", /*
										 * Unique Type Name
										 */typeName);
			policy.simpleField("label.common.policyCode", "policyDefinition.code", Type.STRING);
			policy.simpleField("label.common.policyName", "policyDefinition.description", Type.STRING);			
			domainTypeSystem.registerDomainType(policy);
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	protected DomainType warranty(boolean isInternalInstallTypeRequired) {
		String typeName = Warranty.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType warranty = new QueryDomainType(
			/* Domain term */"Warranty", /*
										 * Unique Type Name
										 */typeName);
			warranty.simpleField("label.warranty.transactionType", "transactionType.trnxTypeKey",  Type.STRING);
			warranty.oneToOne("label.marketInfo", "marketingInformation", marketingInformation(isInternalInstallTypeRequired));
			warranty.simpleField("label.common.number", "dealerNumber", Type.STRING);
			warranty.simpleField("label.warrantyAdmin.customerType", "customerType", Type.STRING);
			warranty.simpleField("label.common.status", "status.status", Type.STRING);
			warranty.simpleField("label.common.preferredCurrency",
					"preferredCurrency.code", Type.STRING);
			warranty.simpleField("label.common.owner.customerclassification", "customerClassification", Type.STRING);
			warranty.oneToMany("label.common.registeredPolicy", "policies",policy());
			domainTypeSystem.registerDomainType(warranty);
		}
		return domainTypeSystem.getDomainType(typeName);
	}

	private DomainType item() {
        String typeName = Item.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType item = new QueryDomainType(/* Domain term */typeName, /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);            
            
            item.simpleField("label.common.itemNumber", "number", Type.STRING);
            item.simpleField("label.common.model", "model.description", Type.STRING);
            item.simpleField("label.common.product", "product.groupCode", Type.STRING);
            domainTypeSystem.registerDomainType(item);
        }
        return domainTypeSystem.getDomainType(typeName);
    }
	
	/*private DomainType option() {
        String typeName =Option.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType option = new QueryDomainType( typeName, 
                     typeName);
            option.simpleField("optionDescription", "optionDescription", Type.STRING);
           // option.simpleField("optionType", "optionType", Type.STRING);
            option.simpleField("optionCode", "optionCode", Type.STRING);
            domainTypeSystem.registerDomainType(option);
        }
        return domainTypeSystem.getDomainType(typeName);
    }
	*/
		
	private DomainType marketingInformation(boolean isInternalInstallTypeRequired){
		String typeName = MarketingInformation.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType marketingInformation = new QueryDomainType(/* Domain term */typeName, /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);            
            
            marketingInformation.simpleField("label.salesMan", "dealerRepresentative", Type.STRING);
            marketingInformation.simpleField("Customer Representative", "customerRepresentative", Type.STRING);
            marketingInformation.simpleField("label.defineSearch.contractCode", "contractCode.contractCode", Type.STRING);
            if(isInternalInstallTypeRequired){
            	marketingInformation.simpleField("label.defineSearch.internalInstallType", "internalInstallType.internalInstallType", Type.STRING);
            }
            domainTypeSystem.registerDomainType(marketingInformation);
        }
        return domainTypeSystem.getDomainType(typeName);
		
	}

	
	private DomainType manufacturingSite() {
        String typeName = ListOfValues.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType item = new QueryDomainType(/* Domain term */typeName, /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);
            item.simpleField("label.common.code", "code", Type.STRING);
            item.simpleField("label.common.state", "state", Type.STRING);
            item.simpleField("label.common.description", "description", Type.STRING);


            domainTypeSystem.registerDomainType(item);
        }
        return domainTypeSystem.getDomainType(typeName);
    }
	
	protected DomainType campaignNotifications() {
		String typeName = Campaign.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType campaignNotifications = new QueryDomainType("Campaign", typeName);
			campaignNotifications.simpleField("label.claimSearch.campaignCode", "campaign.code",
					Type.STRING);

			domainTypeSystem.registerDomainType(campaignNotifications);
			return campaignNotifications;
		}
		return domainTypeSystem.getDomainType(typeName);

	}

	private DomainTypeSystem domainTypeSystem;// =new DomainTypeSystem();

	@Override
	public Set<DomainType> getDomainTypes() {
		Set<DomainType> set=new HashSet<DomainType>();
		set.add(domainType);
		return set;
	}

	@Override
	public DomainTypeSystem getDomainTypeSystem() {
		return domainTypeSystem;
	}

	@Override
	public String getExpressionForDomainType(String typeName) {
		String expression = null;
		if (typeName.equals("InventoryItem"))
			expression = "inventoryItem";
		return expression;

	}

	public String getTopLevelTypeName() {
		return "InventoryItem";
	}

	public String getTopLevelAlias() {
		return "inventoryItem";
	}
	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	
	public SecurityHelper getSecurityHelper() {
		return securityHelper;
	}

	public void setSecurityHelper(SecurityHelper securityHelper) {
		this.securityHelper = securityHelper;
	}


}
