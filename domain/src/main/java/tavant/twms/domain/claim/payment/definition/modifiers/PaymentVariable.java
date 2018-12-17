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
package tavant.twms.domain.claim.payment.definition.modifiers;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.claim.payment.definition.Section;

import tavant.twms.domain.common.I18NAdditionalAttributeName;
import tavant.twms.domain.common.I18NModifierName;
import tavant.twms.security.authz.infra.SecurityHelper;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;


@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
public class PaymentVariable implements BusinessUnitAware,AuditableColumns
{
    @Id
    @GeneratedValue(generator = "PaymentVariable")
	@GenericGenerator(name = "PaymentVariable", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "PAYMENT_VARIABLE_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String name;


    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "MODIFIER_NAME", nullable = false)
	private List<I18NModifierName> i18NModiferNames = new ArrayList<I18NModifierName>();



    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();


    public PaymentVariable() {
    }

    @ManyToOne(fetch = FetchType.LAZY)
    private Section section;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmountName() {
        return getName() + " " + "Modifier";
    }

    public Section getSection() {
        return this.section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    @Override
    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("Variable name = [").append(getName()).append("] and id = [").append(this.id)
                .append("].");
        return buff.toString();
    }

    @Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}

	public List<I18NModifierName> getI18NModiferNames() {
		return i18NModiferNames;
	}

	public void setI18NModiferNames(List<I18NModifierName> modiferNames) {
		i18NModiferNames = modiferNames;
	}

    public String getDisplayName() {
    	String name_locale="";
		for (I18NModifierName i18NModiferName : this.i18NModiferNames) {
			if (i18NModiferName.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && i18NModiferName.getName() != null) {
				name_locale = i18NModiferName.getName();
				break;
			}
			else if(i18NModiferName.getLocale().equalsIgnoreCase("en_US")) {
				name_locale = i18NModiferName.getName();
			}
		}

		return name_locale;
    }


	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}


}
