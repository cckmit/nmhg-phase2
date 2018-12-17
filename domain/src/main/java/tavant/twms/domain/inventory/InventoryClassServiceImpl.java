package tavant.twms.domain.inventory;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

/**
 * Service interface implementation for {@link InventoryClass}
 * 
 * @author ravi.sinha
 */
public class InventoryClassServiceImpl extends GenericServiceImpl<InventoryClass, Long, Exception> 
	implements InventoryClassService {
	
	private InventoryClassRepository inventoryClassRepository;

	@Override
	public InventoryClass findInventoryClassByName(String name) {
		InventoryClass returnedInvClass = inventoryClassRepository.findInventoryClassByName(name);
		return returnedInvClass;
	}

	@Override
	public GenericRepository<InventoryClass, Long> getRepository() {
		return inventoryClassRepository;
	}
	
	public InventoryClassRepository getInventoryClassRepository() {
		return inventoryClassRepository;
	}
	
	public void setInventoryClassRepository(InventoryClassRepository inventoryClassRepository) {
		this.inventoryClassRepository = inventoryClassRepository;
	}

}
