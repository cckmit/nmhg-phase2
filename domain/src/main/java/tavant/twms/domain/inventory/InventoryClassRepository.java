package tavant.twms.domain.inventory;

import tavant.twms.infra.GenericRepository;

/**
 * Repository interface for {@link InventoryClass}
 * 
 * @author ravi.sinha
 */
public interface InventoryClassRepository extends GenericRepository<InventoryClass, Long> {
	
	InventoryClass findInventoryClassByName(String name);

}
