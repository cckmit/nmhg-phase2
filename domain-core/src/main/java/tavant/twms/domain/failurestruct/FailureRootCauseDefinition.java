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

package tavant.twms.domain.failurestruct;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.I18NFailureRootCauseDefinition;
import tavant.twms.security.authz.infra.SecurityHelper;

/**
 * @author jhulfikar.ali
 *
 */
@Entity
@Table (name="FAILURE_ROOT_CAUSE_DEFINITION")
public class FailureRootCauseDefinition implements Comparable<FailureRootCauseDefinition> {
	@Id
	@GeneratedValue(generator = "FailureRootCauseDefinition")
	@GenericGenerator(name = "FailureRootCauseDefinition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "FAILURE_ROOT_CAUSE_DEFN_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String code;
    
    private String name;

    private String description;
    
    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "FAILURE_ROOT_CAUSE_DEFINITION", nullable = false)
	private List<I18NFailureRootCauseDefinition>  i18nFailureRootCauseDefinition = new ArrayList<I18NFailureRootCauseDefinition>();
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

  
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
    	String name_locale= this.name;
		for (I18NFailureRootCauseDefinition i18nFailureRootCauseDefinitionName: this.i18nFailureRootCauseDefinition) {
			if (i18nFailureRootCauseDefinitionName!=null && i18nFailureRootCauseDefinitionName.getLocale()!=null && 
					i18nFailureRootCauseDefinitionName.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && i18nFailureRootCauseDefinitionName.getName() != null) {
				name_locale = i18nFailureRootCauseDefinitionName.getName();
				break;
			}
			else if(i18nFailureRootCauseDefinitionName !=null && i18nFailureRootCauseDefinitionName.getLocale()!=null && 
					i18nFailureRootCauseDefinitionName.getLocale().equalsIgnoreCase("en_US")) {
				name_locale = i18nFailureRootCauseDefinitionName.getName();
			}
		}
		
		return name_locale;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("code", this.code)
                .append("name", this.name).append("description", this.description).toString();
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public List<I18NFailureRootCauseDefinition> getI18nFailureRootCauseDefinition() {
		return i18nFailureRootCauseDefinition;
	}

	public void setI18nFailureRootCauseDefinition(
			List<I18NFailureRootCauseDefinition> failureRootCauseDefinition) {
		i18nFailureRootCauseDefinition = failureRootCauseDefinition;
	}
	
	public int compareTo(FailureRootCauseDefinition o) {
		return this.getName().compareToIgnoreCase(o.getName());
	}

}
