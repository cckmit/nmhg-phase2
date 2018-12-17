package tavant.twms.web.typeconverters;

import java.util.ArrayList;
import java.util.Collection;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.inventory.ItemNotFoundException;

public class InventoryTypeByTypeConvertor extends 
			NamedDomainObjectConverter<InventoryService, InventoryType>{
	
	public InventoryTypeByTypeConvertor(){
		super("inventoryService");
	}

	@Override
	public InventoryType fetchByName(String name) throws Exception {
		return getService().findInventoryTypeByType(name);
	}

	@Override
	public String getName(InventoryType entity) throws Exception {
		return entity.getType();
	}
	
	@Override
	public Collection<InventoryType> fetchByNames(String[] type) throws ItemNotFoundException {
        if (type != null && type.length > 0) {
            Collection<InventoryType> inventoryTypes = new ArrayList<InventoryType>();
            for (int i = 0; i < type.length; i++) {
                if (type[i] != null && !type[i].equals("")) {
                	inventoryTypes.add(getService().findInventoryTypeByType(type[i]));
                }
            }
            return inventoryTypes;
        }
        return null;
    }

			
}
