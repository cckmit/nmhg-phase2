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
package tavant.twms.domain.policy;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.inventory.I18NTransactionTypeText;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.SecurityHelper;

/**
 * @author fatima.marneni
 *
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
@FilterDef(name = "bu_name", parameters = { @ParamDef(name = "name", type = "string") })
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CompetitorMake implements AuditableColumns{
	
	@Id
	@GeneratedValue(generator = "CompetitorMake")
	@GenericGenerator(name = "CompetitorMake", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "COMPETITOR_MAKE_SEQ"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "increment_size", value = "1") })
    private Long id;
    
	private String make;
	
	@Version
	private int version;
	
	@OneToMany(fetch = FetchType.EAGER)
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "I18N_COMPETITOR_MAKE", nullable = false)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<I18NCompetitorMakeText> competitorMakeTexts = new ArrayList<I18NCompetitorMakeText>();

	
	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

	@Type(type="tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}
	
    public String getMake() {
        return make;
    }

    public void setMake(String make) {
		this.make = make;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return this.make;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<I18NCompetitorMakeText> getCompetitorMakeTexts() {
		return competitorMakeTexts;
	}

	public void setCompetitorMakeTexts(
			List<I18NCompetitorMakeText> competitorMakeTexts) {
		this.competitorMakeTexts = competitorMakeTexts;
	}

    public String getDisplayMake() {
            String i18nMake = "";
            for (I18NCompetitorMakeText competitorMakeText : this.competitorMakeTexts) {
                if (competitorMakeText.getLocale().equalsIgnoreCase(
                        new SecurityHelper().getLoggedInUser().getLocale()
                                .toString()) && competitorMakeText.getMake() != null) {
                    i18nMake = competitorMakeText.getMake();
                    break;
                }
                else if(competitorMakeText.getLocale().equalsIgnoreCase("en_US")) {
                    i18nMake = competitorMakeText.getMake();
                }

            }
            return i18nMake;
        }

}


