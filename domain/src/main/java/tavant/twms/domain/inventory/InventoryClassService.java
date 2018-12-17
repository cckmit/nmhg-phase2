package tavant.twms.domain.inventory;

import tavant.twms.infra.GenericService;

/**
 * Service interface for {@link InventoryClass}
 * 
 * @author ravi.sinha
 */
public interface InventoryClassService extends GenericService<InventoryClass, Long, Exception> {
	
	InventoryClass findInventoryClassByName(String name);
	
}
