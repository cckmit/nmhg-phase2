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
package tavant.twms.web.admin.dto;

import tavant.twms.domain.catalog.ItemGroup;

/**
 * @author kaustubhshobhan.b
 *
 */
public class ItemDTO {

    private String itemNumber;

    private String description;

    private ItemGroup productType;

    private String make;

    private String model;

    private String isSerialized;

    private String hasUsageMeter;

    private String error;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHasUsageMeter() {
        return hasUsageMeter;
    }

    public void setHasUsageMeter(String hasUsageMeter) {
        this.hasUsageMeter = hasUsageMeter;
    }

    public String getIsSerialized() {
        return isSerialized;
    }

    public void setIsSerialized(String isSerialized) {
        this.isSerialized = isSerialized;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public ItemGroup getProductType() {
        return productType;
    }

    public void setProductType(ItemGroup productType) {
        this.productType = productType;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
