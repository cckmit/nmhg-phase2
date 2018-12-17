/*
 *   Copyright (c) 2008 Tavant Technologies
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
package tavant.twms.domain.additionalAttributes;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.I18NAdditionalAttributeName;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.authz.infra.SecurityHelper;

/**
 * @author pradipta.a
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@Inheritance(strategy=InheritanceType.JOINED)
public class AdditionalAttributes implements AuditableColumns,BusinessUnitAware{
	
	public AdditionalAttributes()
	{
		
	}
	
	public AdditionalAttributes(AdditionalAttributes additionalAttributes)
	{
		this.id=additionalAttributes.id;
		this.name=additionalAttributes.name;
		this.mandatory=additionalAttributes.mandatory;
		this.d=additionalAttributes.d;
		this.version=additionalAttributes.version;
		this.i18NAdditionalAttributeNames=additionalAttributes.i18NAdditionalAttributeNames;
		this.attributeAssociations=additionalAttributes.attributeAssociations;
		this.attributePurpose=additionalAttributes.attributePurpose;
		this.attributeType=additionalAttributes.attributeType;
		this.businessUnitInfo=additionalAttributes.businessUnitInfo;
		this.singleSelectValues=additionalAttributes.singleSelectValues;
	}

    @Id
    @GeneratedValue(generator = "AdditionalAttributes")
	@GenericGenerator(name = "AdditionalAttributes", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ADDITIONAL_ATTRIBUTES_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    private String name;

    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL})
	@JoinColumn(name = "ADDITIONAL_ATTRIBUTES_NAME", nullable = false)
	private List<I18NAdditionalAttributeName> i18NAdditionalAttributeNames = new ArrayList<I18NAdditionalAttributeName>();

    private String claimTypes;

    private Boolean mandatory=Boolean.FALSE;

    private String attributeType;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.additionalAttributes.AttributePurpose"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private AttributePurpose attributePurpose;


    @OneToMany(cascade = { CascadeType.ALL })
    @JoinTable(name="add_attr_attr_assoc")
    private List<AttributeAssociation> attributeAssociations = new ArrayList<AttributeAssociation>();

    @Version
    @JsonIgnore
    private int version;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    private String singleSelectValues;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
    	String name_locale="";
		for (I18NAdditionalAttributeName i18NAdditionalAttributeName : this.i18NAdditionalAttributeNames) {
			if (i18NAdditionalAttributeName.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && i18NAdditionalAttributeName.getName() != null) {
				name_locale = i18NAdditionalAttributeName.getName();
				break;
			}
			else if(i18NAdditionalAttributeName.getLocale().equalsIgnoreCase("en_US")) {
				name_locale = i18NAdditionalAttributeName.getName();
			}
		}

		return name_locale;
	}
    
    public void setName(String name) {
    	this.name = name;
    }

    public String getAttributeName() {
    	return name;
    }
    
    public String getClaimTypes() {
        return claimTypes;
    }

    public void setClaimTypes(String claimTypes) {
        this.claimTypes = claimTypes;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    public AttributePurpose getAttributePurpose() {
        return attributePurpose;
    }

    public void setAttributePurpose(AttributePurpose attributePurpose) {
        this.attributePurpose = attributePurpose;
    }

    public List<AttributeAssociation> getAttributeAssociations() {
        return attributeAssociations;
    }

    public void setAttributeAssociations(List<AttributeAssociation> attributeAssociations) {
        this.attributeAssociations = attributeAssociations;
        for (AttributeAssociation attrAssociation : attributeAssociations) {
            attrAssociation.setForAttribute(this);
        }
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	@JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public List<I18NAdditionalAttributeName> getI18NAdditionalAttributeNames() {
		return i18NAdditionalAttributeNames;
	}

	public void setI18NAdditionalAttributeNames(
			List<I18NAdditionalAttributeName> additionalAttributeName) {
		i18NAdditionalAttributeNames = additionalAttributeName;
	}

    public String getSingleSelectValues() {
        return singleSelectValues;
    }

    public void setSingleSelectValues(String singleSelectValues) {
        this.singleSelectValues = singleSelectValues;
    }

}
