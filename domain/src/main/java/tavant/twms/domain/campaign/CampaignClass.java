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
package tavant.twms.domain.campaign;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.ListOfValuesType;
import tavant.twms.security.AuditableColumns;


/**
 * @author Kiran.Kollipara
 */
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("CAMPAIGNCLASS")
@Filters({
  @Filter(name="excludeInactive")
})
public class CampaignClass extends ListOfValues implements BusinessUnitAware,AuditableColumns{

    @Transient
    private String name;

    public CampaignClass() {
    	super();
    }

    public String getName() {
        return getDescription();
    }

    @Override
	public ListOfValuesType getType() {
		return ListOfValuesType.CampaignClass;
	}
}