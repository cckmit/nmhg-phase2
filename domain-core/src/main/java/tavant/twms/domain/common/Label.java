/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.domain.common;

import java.io.Serializable;

import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.*;

import tavant.twms.security.AuditableColumns;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;

/**
 * @author aniruddha.chaturvedi
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@SuppressWarnings("serial")
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class Label implements Serializable, AuditableColumns, BusinessUnitAware {
    public static final String INVENTORY="INVENTORY";
    public static final String POLICY="POLICY";
    public static final String SUPPLIER="SUPPLIER";
    public static final String FAULT_CODE_DEFINITION="FAULTCODEDEFINITION";
    public static final String SERVICE_PROCEDURE_DEFINITION="SERVICEPROCEDUREDEFINITION";
    public static final String MODEL="MODEL";
    public static final String CAMPAIGN="CAMPAIGN";
    public static final String SERVICECODE="SERVICECODE";
    public static final String WAREHOUSE="WAREHOUSE";
    public static final String FLEET_INVENTORY="FLEET_INVENTORY";
    public static final String CONTRACT="CONTRACT";
    public static final String FLEET_CUSTOMER="FLEETCUSTOMER";
    @Id
    private String name;

    @Version
    private int version;

    private String type;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    public Label() {
    }

    public Label(String name,String type) {
        this.name = name;
        this.type = type;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BusinessUnitInfo getBusinessUnitInfo() {
        return businessUnitInfo;
    }

    public void setBusinessUnitInfo(BusinessUnitInfo businessUnitInfo) {
        this.businessUnitInfo = businessUnitInfo;
    }
}
