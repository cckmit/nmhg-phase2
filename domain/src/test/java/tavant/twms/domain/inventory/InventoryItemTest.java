package tavant.twms.domain.inventory;

import junit.framework.TestCase;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemComposition;

public class InventoryItemTest extends TestCase {

	public void testIncludeAndIncludes() {
		Item topLevelType = new Item();
		topLevelType.setSerialized(true);
		Item level1Type = new Item();
		level1Type.setSerialized(true);
		topLevelType.include(1, level1Type);

		Item level2Type = new Item();
		level2Type.setSerialized(true);
		level1Type.include(1, level2Type);
		
		InventoryItem topLevelItem = new InventoryItem();
		topLevelItem.setOfType(topLevelType);
		
		InventoryItem level1Item = new InventoryItem();		
		level1Item.setOfType(level1Type);
		
		InventoryItem level2Item = new InventoryItem();		
		level1Item.setOfType(level2Type);
		
		
		ItemComposition level1Composition = topLevelType.getParts().iterator().next();
		topLevelItem.include(level1Item, level1Composition);
		level1Item.include(level2Item,level1Type.getParts().iterator().next());
		
		assertTrue(topLevelItem.includes(level1Item));
		assertTrue(level1Item.includes(level2Item));
		assertTrue(topLevelItem.includes(level2Item));
	}

	public void testGetStructuralRelationship() {
		Item topLevelType = new Item();
		topLevelType.setSerialized(true);
		Item level1Type = new Item();
		level1Type.setSerialized(true);
		topLevelType.include(1, level1Type);

		Item level2Type = new Item();
		level2Type.setSerialized(true);
		level1Type.include(1, level2Type);
		
		InventoryItem topLevelItem = new InventoryItem();
		topLevelItem.setOfType(topLevelType);
		
		InventoryItem level1Item = new InventoryItem();		
		level1Item.setOfType(level1Type);
		
		InventoryItem level2Item = new InventoryItem();		
		level1Item.setOfType(level2Type);
		
		
		ItemComposition level1Composition = topLevelType.getParts().iterator().next();
		topLevelItem.include(level1Item, level1Composition);
		ItemComposition level2Composition = level1Type.getParts().iterator().next();
		level1Item.include(level2Item,level2Composition);
		
		assertTrue(topLevelItem.includes(level1Item));
		assertTrue(level1Item.includes(level2Item));
		assertTrue(topLevelItem.includes(level2Item));
		
		InventoryItemComposition structuralRelationship = topLevelItem.getStructuralRelationship(level1Item);
		assertNotNull(structuralRelationship);
		assertEquals(topLevelItem,structuralRelationship.getPartOf());
		assertEquals(level1Item,structuralRelationship.getPart());
		assertEquals(level1Composition,structuralRelationship.getBasedOn());
		
		InventoryItemComposition structuralRelationship1 = topLevelItem.getStructuralRelationship(level2Item);
		assertNotNull(structuralRelationship1);
		assertEquals(level1Item,structuralRelationship1.getPartOf());
		assertEquals(level2Item,structuralRelationship1.getPart());
		assertEquals(level2Composition,structuralRelationship1.getBasedOn());		
	}

	public void testReplaceSerializedPart() {
		Item topLevelType = new Item();
		topLevelType.setSerialized(true);
		Item level1Type = new Item();
		level1Type.setSerialized(true);
		topLevelType.include(1, level1Type);

		Item level2Type = new Item();
		level2Type.setSerialized(true);
		level1Type.include(1, level2Type);
		
		InventoryItem topLevelItem = new InventoryItem();
		topLevelItem.setOfType(topLevelType);
		
		InventoryItem level1Item = new InventoryItem();		
		level1Item.setOfType(level1Type);
		
		InventoryItem level2Item = new InventoryItem();		
		level2Item.setOfType(level2Type);
		
		
		ItemComposition level1Composition = topLevelType.getParts().iterator().next();
		topLevelItem.include(level1Item, level1Composition);
		ItemComposition level2Composition = level1Type.getParts().iterator().next();
		level1Item.include(level2Item,level2Composition);
		
		assertTrue(topLevelItem.includes(level1Item));
		assertTrue(level1Item.includes(level2Item));
		assertTrue(topLevelItem.includes(level2Item));
		
		InventoryItemComposition structuralRelationship = topLevelItem.getStructuralRelationship(level1Item);
		assertNotNull(structuralRelationship);
		assertEquals(topLevelItem,structuralRelationship.getPartOf());
		assertEquals(level1Item,structuralRelationship.getPart());
		assertEquals(level1Composition,structuralRelationship.getBasedOn());
		
		InventoryItemComposition structuralRelationship1 = topLevelItem.getStructuralRelationship(level2Item);
		assertNotNull(structuralRelationship1);
		assertEquals(level1Item,structuralRelationship1.getPartOf());
		assertEquals(level2Item,structuralRelationship1.getPart());
		assertEquals(level2Composition,structuralRelationship1.getBasedOn());		
		
		InventoryItem newLevel2Item = new InventoryItem();
		newLevel2Item.setOfType(level2Type);
		
		SerializedItemReplacement partReplacement = topLevelItem.replaceSerializedPart(level2Item, newLevel2Item, new ItemReplacementReason());
		assertNotNull(partReplacement);
		assertEquals(level2Item,partReplacement.getOldPart());
		assertEquals(newLevel2Item,partReplacement.getNewPart());
		assertEquals(level2Composition,partReplacement.getForComposition().getBasedOn());
		
	}

}
