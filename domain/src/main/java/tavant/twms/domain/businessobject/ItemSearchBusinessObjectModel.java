/**
 * 
 */
package tavant.twms.domain.businessobject;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.rules.*;

/**
 * @author mritunjay.kumar
 * 
 */
public class ItemSearchBusinessObjectModel extends AbstractBusinessObjectModel {

	private static Logger logger = LogManager
			.getLogger(ItemSearchBusinessObjectModel.class);

	DomainType domainType;
Boolean brand = Boolean.FALSE;
	private final DomainTypeSystem domainTypeSystem;

	public ItemSearchBusinessObjectModel() {
		domainTypeSystem = new DomainTypeSystem();
		domainType = item();
		discoverPathsToFields(domainType, "item");
	}
	public ItemSearchBusinessObjectModel(Boolean brand) {
		this.brand=brand;
		domainTypeSystem = new DomainTypeSystem();
		domainType = brandItem();
		discoverPathsToFields(domainType, "brandItem");
	}

	protected DomainType item() {
	
		String typeName = Item.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType item = new DomainType("Item", typeName);
			item.simpleField("columnTitle.common.itemNumber", "number", Type.STRING);
			item.queryTemplate("label.common.itemDescription",
					"{alias}.locale= 'USER_LOCALE'  and lower({alias}.description)",
					Type.STRING, true, FunctionField.Types.ONE_TO_MANY.getBaseType(), 
					"item.i18nItemTexts",
					"{alias}");
			
			item.simpleField("label.warrantyAdmin.itemType", "itemType", Type.STRING);
			item.simpleField("columnTitle.itemSearch.status", "status", Type.STRING);
			item.simpleField("label.common.businessUnit", "businessUnitInfo",Type.STRING);
            item.oneToOne("label.common.model", "model", model());
            item.oneToOne("columnTitle.inventoryAction.product_type", "product", product());
            item.oneToMany("label.common.brandItems", "brandItems", brandItem());
            domainTypeSystem.registerDomainType(item);
		}
		return domainTypeSystem.getDomainType(typeName);
		
	}
	

	protected DomainType brandItem() {
		String typeName = BrandItem.class.getSimpleName();
		if (!domainTypeSystem.isKnown(typeName)) {
			DomainType brandItem = new DomainType("BrandItem", typeName);
			brandItem.simpleField("label.common.itemNumber", "itemNumber", Type.STRING);
			brandItem.simpleField("label.common.brandName", "brand", Type.STRING);
			domainTypeSystem.registerDomainType(brandItem);
		}
		return domainTypeSystem.getDomainType(typeName);
	}
	
	@Override
	public Set<DomainType> getDomainTypes() {
		Set<DomainType> set = new HashSet<DomainType>();
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
		if (typeName.equals("Item")) {
			expression = "item";
		}
		else if(typeName.equals("BrandItem")) {
			expression = "brandItem";
		}
		return expression;

	}

	public String getTopLevelTypeName() {
		if(brand)
		{
			return "BrandItem";
		}
		else
		{
		return "Item";
		}
	}

	public String getTopLevelAlias() {
		if(brand)
		{
			return "brandItem";
		}
		else
		{
		return "item";
		}
	}

    private DomainType model() {
        String typeName = ItemGroup.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType itemGroup = new DomainType(/* Domain term */"Model", /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);
            itemGroup.simpleField("label.common.name", "name", Type.STRING);
            itemGroup.simpleField("label.common.groupCode", "groupCode", Type.STRING);
            itemGroup.simpleField("label.common.description", "description", Type.STRING);
            domainTypeSystem.registerDomainType(itemGroup);
        }
        return domainTypeSystem.getDomainType(typeName);
    }

    private DomainType product() {
        String typeName = ItemGroup.class.getSimpleName();
        if (!domainTypeSystem.isKnown(typeName)) {
            DomainType itemGroup = new DomainType(/* Domain term */"Product", /*
                                                                 * Unique Type
                                                                 * Name
                                                                 */typeName);
            itemGroup.simpleField("label.common.name", "name", Type.STRING);
            itemGroup.simpleField("label.common.groupCode", "groupCode", Type.STRING);
            itemGroup.simpleField("label.common.description", "description", Type.STRING);
            domainTypeSystem.registerDomainType(itemGroup);
        }
        return domainTypeSystem.getDomainType(typeName);
    }
}
