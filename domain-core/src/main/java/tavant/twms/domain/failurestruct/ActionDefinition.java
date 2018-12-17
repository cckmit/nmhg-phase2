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
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.I18NActionDefinition;
import tavant.twms.domain.common.I18NAssemblyDefinition;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.authz.infra.SecurityHelper;

/**
 * @author kamal.govindraj
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class ActionDefinition implements AuditableColumns , Comparable<ActionDefinition>{
    @Id
    @GeneratedValue(generator = "ActionDefinition")
	@GenericGenerator(name = "ActionDefinition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ACTION_DEFINITION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    Long id;

    @Version
    private int version;

    String code;

    @Transient
    String name;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
    
    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	@JoinColumn(name = "ACTION_DEFINITION", nullable = false)
	private List<I18NActionDefinition>  i18nActionDefinition = new ArrayList<I18NActionDefinition>();

    public ActionDefinition() {
    }

    public ActionDefinition(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
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
    	String name_locale="";
		for (I18NActionDefinition i18nActionDefinitions: this.i18nActionDefinition) {
			if (i18nActionDefinitions!=null && i18nActionDefinitions.getLocale()!=null && 
					i18nActionDefinitions.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && i18nActionDefinitions.getName() != null) {
				name_locale = i18nActionDefinitions.getName();
				break;
			}
			else if(i18nActionDefinitions !=null && i18nActionDefinitions.getLocale()!=null && 
					i18nActionDefinitions.getLocale().equalsIgnoreCase("en_US")) {
				name_locale = i18nActionDefinitions.getName();
			}
		}
		
		return name_locale;
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

	public List<I18NActionDefinition> getI18nActionDefinition() {
		return i18nActionDefinition;
	}

	public void setI18nActionDefinition(List<I18NActionDefinition> actionDefinition) {
		i18nActionDefinition = actionDefinition;
	}

	public int compareTo(ActionDefinition otherDefinition) {
		
        if (this.getName() != null) {
            return this.getName().compareTo(otherDefinition.getName());
        } else {
            return -1; // anything other than 0 is fine
        }
	}

}
