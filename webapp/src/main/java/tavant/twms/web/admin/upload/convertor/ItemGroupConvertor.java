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

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.web.xls.reader.ConversionErrors;
import tavant.twms.web.xls.reader.Convertor;

/**
 * @author kaustubhshobhan.b
 *
 */
public class ItemGroupConvertor implements Convertor {

     private ItemGroupService itemGroupService;

        public Object convert(Object object) {
            String itemGroupCode = (String)object;
            ItemGroup parentItem = null;
            if(object!=null){
                parentItem=itemGroupService.findItemGroupByCode(itemGroupCode);
            }
            if((object != null) && (parentItem == null)){
                parentItem = new ItemGroup();
                parentItem.setGroupCode(itemGroupCode);
                ConversionErrors.getInstance().addError("Parent ItemGroup " +
                        itemGroupCode + " does not exist");
            }

            return parentItem;
        }


        public Object convertWithDependency(Object object, Object dependency) {
            return new UnsupportedOperationException();
        }


        public void setItemGroupService(ItemGroupService itemGroupService) {
            this.itemGroupService = itemGroupService;
        }

}
