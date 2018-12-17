package tavant.twms.domain.inventory;

import junit.framework.TestCase;
import tavant.twms.domain.catalog.ItemComposition;

public class InventoryItemCompositionTest extends TestCase {
	
	public void testReplacePart() {
		InventoryItemComposition fixture = new InventoryItemComposition();
		ItemComposition itemComposition = new ItemComposition();
		fixture.setBasedOn(itemComposition);
		
		InventoryItem oldPart = new InventoryItem();
		fixture.setPart(oldPart);
		
		InventoryItem newPart = new InventoryItem();
		
		ItemReplacementReason itemReplacementReason = new ItemReplacementReason();
		SerializedItemReplacement partReplacement = fixture.replacePart(newPart, itemReplacementReason);
		assertNotNull(partReplacement);
		assertEquals(fixture,partReplacement.getForComposition());
		assertEquals(oldPart,partReplacement.getOldPart());
		assertEquals(newPart,partReplacement.getNewPart());
		assertEquals(itemReplacementReason,partReplacement.getDueTo());
	}

/*	protected void fillParts(InventoryItem equipment) {
		Item equipmentType = equipment.getOfType();
		Set<ItemComposition> composedOfTypes = equipmentType.getParts();
		for(ItemComposition itemComposition : composedOfTypes ) {
			Item itemType = itemComposition.getItem();
			if( itemType.isSerialized() ) {
				//Create a new inventory item
				int quantity = itemComposition.getQuantity();
				for (int i = 0; i < quantity; i++) {
					//Let the first instance of a type be included in the equipment.
					InventoryItem serializedItem = newSerializedItem(itemType);
					
					inventoryItemRepository.save(serializedItem);
					assertNotNull(serializedItem.getId());
					equipment.include(serializedItem, itemComposition);
					assertTrue(equipment.includes(serializedItem));
					inventoryItemRepository.update(equipment);
					if( itemType.getParts().isEmpty() ) {
						//Create a few instances to keep in stock for replacements.
						for( int j=0;j < 10;j++) {
							InventoryItem stockPart = newSerializedItem(itemType);
							inventoryItemRepository.save(stockPart);
						}
					} else {
						fillParts(serializedItem);
					}
					
				}
			}
		}
	}

	protected InventoryItem newSerializedItem(Item itemType) {
		CalendarDate referenceDate = CalendarDate.date(2003,1,1);
		CalendarDate lastWeek = referenceDate.plusDays(-7);
		CalendarDate someDayLastMonth = referenceDate.plusDays(-31);
		InventoryItem inventoryItem = new InventoryItem();
		inventoryItem.setOfType(itemType);
		
		//Built-on.
		inventoryItem.setBuiltOn(someDayLastMonth);
		
		//Shipped to dealer/channel on
		inventoryItem.setShipmentDate(lastWeek);
		
		//Received by Dealer on.
		inventoryItem.setDeliveryDate(referenceDate);
		
		
		inventoryItem.setConditionType(InventoryItemCondition.NEW);
		inventoryItem.setHoursOnMachine(1);
		inventoryItem.setType(InventoryType.STOCK);
		serialNumberGenerator.assignSerialNumber(inventoryItem);
		return inventoryItem;
	}

	@SuppressWarnings("serial")
	static class SerialNumberGenerator {
		Sequence onlyOneSequence = new Sequence();
		public void assignSerialNumber(InventoryItem forItem) {
			forItem.setSerialNumber(onlyOneSequence.next().toString());
		}
	}
	
	static class Sequence {
		int i=1234500;
		public Integer next() {
			return i++;
		}
	}
*/}
