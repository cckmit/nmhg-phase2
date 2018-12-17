/*
 *   Copyright (c)2007 Tavant Technologies*   All Rights Reserved.
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
 *
 *
 * User: kapil.pandit
 * Date: Jan 10, 2007
 * Time: 5:28:17 PM
 */

package tavant.twms.integration.layer.component;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import tavant.oagis.ItemDTO;
import tavant.oagis.ItemDocumentDTO;
import tavant.oagis.ItemSyncRequestDocumentDTO;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.integration.layer.IntegrationRepositoryTestCase;

public class SyncItemTest extends IntegrationRepositoryTestCase {
    SyncItem syncItem;

    CatalogService catalogService;

    public void testSyncCreate() throws CatalogException, XmlException, IOException {
        ItemDTO dto = getItemDTO("/itemsync/Item.xml");
 /*       this.syncItem.sync(dto);
        Item itemFromDB = this.catalogService.findItem(dto.getItemNumber());
        verifyItemFromDB(itemFromDB, dto);*/
    }

    public void testSyncUpdate() throws XmlException, IOException, CatalogException {
        ItemDTO dto = getItemDTO("/itemsync/updated-item.xml");
        /*        this.syncItem.sync(dto);
        Item itemFromDB = this.catalogService.findItem(dto.getItemNumber());
        verifyItemFromDB(itemFromDB, dto);
*/    }

    private void verifyItemFromDB(Item itemFromDB, ItemDTO dto) {
        assertEquals(itemFromDB.getMake(), dto.getManufName());
        assertEquals(itemFromDB.getModel().getGroupCode(), dto.getModelCode());
        assertEquals(itemFromDB.getNumber(), dto.getItemNumber());
        assertEquals(itemFromDB.getProduct().getGroupCode(), dto.getProductCode());
    }

    private ItemDTO getItemDTO(String pathToXml) throws XmlException, IOException {
    	ItemSyncRequestDocumentDTO itemDocument = ItemSyncRequestDocumentDTO.Factory.parse(SyncItemTest.class
                .getResourceAsStream(pathToXml));
        return itemDocument.getItemSyncRequest().getItemArray(0);
    }

    public SyncItem getSyncItem() {
        return this.syncItem;
    }

    public void setSyncItem(SyncItem syncItem) {
        this.syncItem = syncItem;
    }

    public CatalogService getCatalogService() {
        return this.catalogService;
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

}
