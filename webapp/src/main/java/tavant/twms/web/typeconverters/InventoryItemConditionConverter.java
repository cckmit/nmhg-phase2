package tavant.twms.web.typeconverters;

import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryItemCondition;

import java.util.Collection;
import java.util.ArrayList;


/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: May 8, 2009
 * Time: 1:41:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class InventoryItemConditionConverter extends 
			NamedDomainObjectConverter<InventoryService, InventoryItemCondition>{

    public InventoryItemConditionConverter(){
		super("inventoryService");
	}

	@Override
	public InventoryItemCondition fetchByName(String name) throws Exception {
		return getService().findInventoryItemConditionByType(name);
	}

	@Override
	public String getName(InventoryItemCondition entity) throws Exception {
		return entity.getItemCondition();
	}

    @Override
	public Collection<InventoryItemCondition> fetchByNames(String[] type) {
        if (type != null && type.length > 0) {
            Collection<InventoryItemCondition> inventoryItemConditions = new ArrayList<InventoryItemCondition>();
            for (int i = 0; i < type.length; i++) {
                if (type[i] != null && !type[i].equals("")) {
                	inventoryItemConditions.add( getService().findInventoryItemConditionByType(type[i]));
                }
            }
            return inventoryItemConditions;
        }
        return null;
    }

}
