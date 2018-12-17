/**
 * Copyright (c)2006 Tavant Technologies
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
package tavant.twms.web.admin.upload.convertor;

import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.web.xls.reader.ConversionErrors;
import tavant.twms.web.xls.reader.Convertor;

/**
 * @author kaustubhshobhan.b
 *
 */
public class ItemConvertor implements Convertor {

    private CatalogService catalogService;

    /*
     * (non-Javadoc)
     *
     * @see tavant.twms.web.xls.reader.Convertor#convert(java.lang.Object)
     */
    public Object convert(Object object) {
        String itemNumber = (String)object;
        Item item = null;
        try {
            item = catalogService.findItemOwnedByManuf(itemNumber);
          
        } catch (CatalogException e) {
                //check for brand item number
              	item=catalogService.findBrandItemByName(itemNumber)!=null?catalogService.findBrandItemByName(itemNumber).getItem():null;	
      		if(item==null)
      		{
            item = new Item();
            item.setNumber(itemNumber);
            ConversionErrors.getInstance().addError(
                    "Item Number \"" + itemNumber + "\" Not Found");
      		}
        }
        return item;
    }

    /*
     * (non-Javadoc)
     *
     * @see tavant.twms.web.xls.reader.Convertor#convertWithDependency(java.lang.Object,
     *      java.lang.Object)
     */
    public Object convertWithDependency(Object object, Object dependency) {
        return new UnsupportedOperationException();
    }

    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

}
