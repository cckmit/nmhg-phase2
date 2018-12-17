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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import tavant.twms.domain.bu.BusinessUnitAware;
import tavant.twms.domain.bu.BusinessUnitInfo;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.authz.infra.SecurityHelper;

/**
 * @author aniruddha.chaturvedi
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("none")
@FilterDef(name = "bu_name", parameters = {@ParamDef(name = "name", type = "string")})
@Filter(name = "bu_name", condition = "business_unit_info in (:name)")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public abstract class ListOfValues implements Serializable, BusinessUnitAware, AuditableColumns, Comparable<ListOfValues> {

    @Id
    @GeneratedValue(generator = "ListOfValues")
    @GenericGenerator(name = "ListOfValues", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
    @Parameter(name = "sequence_name", value = "List_Of_Values_SEQ"),
    @Parameter(name = "initial_value", value = "1000"),
    @Parameter(name = "increment_size", value = "20")})
    private Long id;

    private String code;

    @Version
    @JsonIgnore
    private int version;

    private String state = ListOfValues.ACTIVE_STATE;

    public static final String ACTIVE_STATE = "active";

    public static final String EXPIRED_STATE = "expired";

    @SuppressWarnings("unused")    
    private String description;

    @OneToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL, CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "List_Of_I18N_Values", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<I18NLovText> i18nLovTexts = new ArrayList<I18NLovText>();

    public String getDescription() {
        String description_locale = "";
        for (I18NLovText i18nLovText : this.getI18nLovTexts()) {
            if (i18nLovText.getLocale().equalsIgnoreCase(
                    new SecurityHelper().getLoggedInUser().getLocale()
                            .toString()) && i18nLovText.getDescription() != null) {
                description_locale = i18nLovText.getDescription();
                break;
            } else if (i18nLovText.getLocale().equalsIgnoreCase("en_US")) {
                description_locale = i18nLovText.getDescription();
            }
        }

        return description_locale;
    }

    public String getRuleContextDescription() {
        String description_locale = "";
        for (I18NLovText i18nLovText : this.i18nLovTexts) {
            if (i18nLovText.getLocale().equalsIgnoreCase("en_US")) {
                description_locale = i18nLovText.getDescription();
            }
        }
        return description_locale;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
    @JsonIgnore
    private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

    public BusinessUnitInfo getBusinessUnitInfo() {
        return businessUnitInfo;
    }

    public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
        this.businessUnitInfo = buAudit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public List<I18NLovText> getI18nLovTexts() {
        return i18nLovTexts;
    }

    public void setI18nLovTexts(List<I18NLovText> lovTexts) {
        i18nLovTexts = lovTexts;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public abstract ListOfValuesType getType();

    public boolean isOfType(ListOfValuesType type) {
        return getType().equals(type);
    }

    public int compareTo(ListOfValues lov) {
        if (this.getDescription() != null) {
            return this.getDescription().compareTo(lov.getDescription());
        } else {
            return -1;
        }
    }

    public String getBuAppendedName(){
        StringBuffer buAppendedName = new StringBuffer(getBusinessUnitInfo().getName()+"-");
        buAppendedName.append(getDescription());
        return buAppendedName.toString();
    }
    
    @JsonIgnore    
    public String getDescriptionForPrint()
    {
    	return this.description;
    }

}
