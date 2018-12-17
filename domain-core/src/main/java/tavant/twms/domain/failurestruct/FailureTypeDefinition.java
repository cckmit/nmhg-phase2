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
package tavant.twms.domain.failurestruct;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.I18NAssemblyDefinition;
import tavant.twms.domain.common.I18NFailureTypeDefinition;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.authz.infra.SecurityHelper;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class FailureTypeDefinition implements AuditableColumns{
	
	@Id
	@GeneratedValue(generator = "FailureTypeDefinition")
	@GenericGenerator(name = "FailureTypeDefinition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "FAILURE_TYPE_DEFN_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    @JsonIgnore
    private int version;

    private String code;
    
    private String name;

    private String description;
    
    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL})
	@JoinColumn(name = "Failure_Type_definition", nullable = false)
	private List<I18NFailureTypeDefinition>  i18nFailureTypeDefinition = new ArrayList<I18NFailureTypeDefinition>();
    
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
    
    public String getNameInEnglish(){
    	String name_locale= this.name;
    	for (I18NFailureTypeDefinition i18nFailureTypeDefinitionName: this.i18nFailureTypeDefinition) {
			if (i18nFailureTypeDefinitionName != null && i18nFailureTypeDefinitionName.getLocale().equalsIgnoreCase("en_US")) 
			{
				name_locale = i18nFailureTypeDefinitionName.getName();
				break;
			}
			else 
			{
				name_locale = getName();	
			}		
    	}
    	return name_locale;
    }

    public String getName() {
    	String name_locale= this.name;
		for (I18NFailureTypeDefinition i18nFailureTypeDefinitionName: this.i18nFailureTypeDefinition) {
			if (i18nFailureTypeDefinitionName!=null && i18nFailureTypeDefinitionName.getLocale()!=null && 
					i18nFailureTypeDefinitionName.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && i18nFailureTypeDefinitionName.getName() != null) {
				name_locale = i18nFailureTypeDefinitionName.getName();
				break;
			}
			else if(i18nFailureTypeDefinitionName !=null && i18nFailureTypeDefinitionName.getLocale()!=null && 
					i18nFailureTypeDefinitionName.getLocale().equalsIgnoreCase("en_US")) {
				name_locale = i18nFailureTypeDefinitionName.getName();
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

	public List<I18NFailureTypeDefinition> getI18nFailureTypeDefinition() {
		return i18nFailureTypeDefinition;
	}

	public void setI18nFailureTypeDefinition(
			List<I18NFailureTypeDefinition> failureTypeDefinition) {
		i18nFailureTypeDefinition = failureTypeDefinition;
	}

	

	

}
