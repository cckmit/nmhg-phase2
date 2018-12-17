/*Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */

package tavant.twms.domain.inventory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
/**
 * Responsible for converting inventory item object to xml and vice-versa.
 * 
 * @author roopali.agrawal
 * 
 */
public class InventoryItemXMLConverter implements Converter{
	InventoryService inventoryService;
	private static Logger logger = LogManager.getLogger(InventoryItemXMLConverter.class);
	
	
	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		if(source!=null)
		{
			InventoryItem inventoryItem=(InventoryItem)source;
			String value=inventoryItem.getId().toString();
			if(value!=null)
			{
				writer.startNode("InventoryId");
				writer.setValue(value);
				writer.endNode();
			}
		}
		
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		reader.moveDown();
		String value=reader.getValue();
		reader.moveUp();
		try {
			InventoryItem item = inventoryService.findSerializedItem(value);
			return item;
		} catch (ItemNotFoundException e) {
			//todo-is it ok to return null...
			logger.error(e);
			return null;
		}
	}

	public boolean canConvert(Class type) {
		return InventoryItem.class.equals(type);
	}
}
