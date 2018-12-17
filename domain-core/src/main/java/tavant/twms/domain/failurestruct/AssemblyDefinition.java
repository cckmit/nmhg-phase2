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
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.common.I18NAssemblyDefinition;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.authz.infra.SecurityHelper;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class AssemblyDefinition implements Comparable<AssemblyDefinition>, AuditableColumns{

	@Id
	@GeneratedValue(generator = "AssemblyDefinition")
	@GenericGenerator(name = "AssemblyDefinition", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ASSEMBLY_DEFINITION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String code;

    @Transient
    private String name;

    @OneToMany(fetch = FetchType.LAZY)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL })
	@JoinColumn(name = "assembly_definition", nullable = false)
	private List<I18NAssemblyDefinition>  i18nAssemblyDefinition = new ArrayList<I18NAssemblyDefinition>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    private AssemblyLevel assemblyLevel;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public AssemblyDefinition(String code, String name, AssemblyLevel level) {
        this.code = code;
        this.name = name;
        this.assemblyLevel = level;
    }

    public AssemblyDefinition(String name, AssemblyLevel level) {
        this(null, name, level);
    }

    public AssemblyDefinition() {

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
		for (I18NAssemblyDefinition i18NassemblyDefinitions: this.i18nAssemblyDefinition) {
			if (i18NassemblyDefinitions!=null && i18NassemblyDefinitions.getLocale()!=null && 
					i18NassemblyDefinitions.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
							.toString()) && i18NassemblyDefinitions.getName() != null) {
				name_locale = i18NassemblyDefinitions.getName();
				break;
			}
			else if(i18NassemblyDefinitions !=null && i18NassemblyDefinitions.getLocale()!=null && 
					i18NassemblyDefinitions.getLocale().equalsIgnoreCase("en_US")) {
				name_locale = i18NassemblyDefinitions.getName();
			}
		}
		
		return name_locale;
	
    }

    public void setName(String name) {
        this.name = name;
    }

   
    public AssemblyLevel getAssemblyLevel() {
        return this.assemblyLevel;
    }

    public void setAssemblyLevel(AssemblyLevel level) {
        this.assemblyLevel = level;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("code", this.code)
                .append("name", this.name).append("level", this.assemblyLevel).toString();
    }
    
    public int compareTo(AssemblyDefinition otherDefinition) {
        if (this.getAssemblyLevel() != null) {
            return this.getAssemblyLevel().compareTo(otherDefinition.getAssemblyLevel());
        } else {
            return -1; // anything other than 0 is fine
        }
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public List<I18NAssemblyDefinition> getI18nAssemblyDefinition() {
		return this.i18nAssemblyDefinition;
	}

	public void setI18nAssemblyDefinition(
			List<I18NAssemblyDefinition> assemblyDefinition) {
		this.i18nAssemblyDefinition = assemblyDefinition;
	}

	

}
