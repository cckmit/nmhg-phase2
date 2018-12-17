/**
 * 
 */
package tavant.twms.domain.inventory;


/**
 * @author fatima.marneni
 *
 */
public class InventoryItemAttributeValueServiceImpl implements
		InventoryItemAttributeValueService {
	private InventoryItemAttributeValueRepository inventoryItemAttributeValueRepository;
	
	public void createInventoryItemAttributeValue(
			InventoryItemAttributeValue inventoryItemAttributeValue) {
		this.inventoryItemAttributeValueRepository.save(inventoryItemAttributeValue);
	}

	public void updateInventoryItemAttributeValue(
			InventoryItemAttributeValue inventoryItemAttributeValue) {
        this.inventoryItemAttributeValueRepository.update(inventoryItemAttributeValue);
	}

	public void setInventoryItemAttributeValueRepository(
			InventoryItemAttributeValueRepository inventoryItemAttributeValueRepository) {
		this.inventoryItemAttributeValueRepository = inventoryItemAttributeValueRepository;
	}

}
