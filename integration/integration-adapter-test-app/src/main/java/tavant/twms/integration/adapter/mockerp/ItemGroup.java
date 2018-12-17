/*
 *   Copyright (c)2007 Tavant Technologies
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

package tavant.twms.integration.adapter.mockerp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ItemGroup {

    @Id
    @GeneratedValue
    private Long id;

    private String itemGroupName;

    private String itemGroupCode;

    private String description;

    private String itemGroupType;

    private String isPartOf;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemGroupName() {
        return itemGroupName;
    }

    public void setItemGroupName(String itemGroupName) {
        this.itemGroupName = itemGroupName;
    }

    public String getItemGroupCode() {
        return itemGroupCode;
    }

    public void setItemGroupCode(String itemGroupCode) {
        this.itemGroupCode = itemGroupCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getItemGroupType() {
        return itemGroupType;
    }

    public void setItemGroupType(String itemGroupType) {
        this.itemGroupType = itemGroupType;
    }

    public String getIsPartOf() {
        return isPartOf;
    }

    public void setIsPartOf(String isPartOf) {
        this.isPartOf = isPartOf;
    }
}
