package tavant.twms.domain.inventory;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author fatima.marneni
 *
 */
@Transactional(readOnly = true)
public interface InventoryItemAttributeValueService {
	
	@Transactional(readOnly=false)
	void createInventoryItemAttributeValue(InventoryItemAttributeValue inventoryItemAttributeValue);
    
	@Transactional(readOnly=false)
	void updateInventoryItemAttributeValue(InventoryItemAttributeValue inventoryItemAttributeValue);
}