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

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderBy;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class FailureStructure implements AuditableColumns{

    @Id
    @GeneratedValue(generator = "FailureStructure")
	@GenericGenerator(name = "FailureStructure", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "FAILURE_STRUCT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String name;

    @ManyToMany(cascade = { ALL }, fetch = LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @OrderBy("id DESC")
    private Set<Assembly> assemblies = new HashSet<Assembly>();

    @ManyToOne(fetch = FetchType.LAZY)
    private ItemGroup forItemGroup;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public ItemGroup getForItemGroup() {
        return this.forItemGroup;
    }

    public void setForItemGroup(ItemGroup associatedWith) {
        this.forItemGroup = associatedWith;
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
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Assembly> getAssemblies() {
        return this.assemblies;
    }

    public void setAssemblies(Set<Assembly> assembly) {
        this.assemblies = assembly;
    }

    public void addAssembly(Assembly assembly) {
        this.assemblies.add(assembly);
    }

    /**
     * Returns the Assembly node that has the matching full code
     * @param code - full code
     * @return Assembly node or null if not found
     */
    public Assembly getAssembly(String code) {
        Assembly result = null;
        for (Iterator iter = this.assemblies.iterator(); iter.hasNext() && result == null;) {
            Assembly assembly = (Assembly) iter.next();
            result = assembly.getNodeWithFullcode(code);  //kept the orderby annotation for assemblies property to pickup the latest one
        }
        return result;
    }

    public void removeAssembly(Assembly assembly) {
        this.assemblies.remove(assembly);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("name", this.name).toString();
    }

    @Override
    public boolean equals(Object o) {// auto generated
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FailureStructure that = (FailureStructure) o;

        if (this.assemblies != null ? !this.assemblies.equals(that.assemblies)
                : that.assemblies != null)
            return false;
        if (this.id != null ? !this.id.equals(that.id) : that.id != null)
            return false;
        if (this.name != null ? !this.name.equals(that.name) : that.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {// auto generated
        int result;
        result = (this.id != null ? this.id.hashCode() : 0);
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        result = 31 * result + (this.assemblies != null ? this.assemblies.hashCode() : 0);
        return result;
    }

    public ServiceProcedure findSeriveProcedure(String code) {
        int index = code.lastIndexOf(Assembly.FAULT_CODE_SEPARATOR);
        if (index != -1) {
            String faultCode = code.substring(0, index);
            String actionCode = code.substring(index + 1);
            Assembly assembly = getAssembly(faultCode);
            if (assembly != null) {
                ActionNode action = assembly.getAction(actionCode);
                if (action != null) {
                    return action.getServiceProcedure();
                }
            }
        }
        return null;
    }

    public Set<ServiceProcedureDefinition> getAllServiceProcedureDefinitions() {
        Set<ServiceProcedureDefinition> serviceProcedureDefinitions = new HashSet<ServiceProcedureDefinition>();
        for (Assembly assembly : this.assemblies) {
        	if(assembly.getActive()){
            serviceProcedureDefinitions.addAll(assembly.getServiceProcedureDefinitions());
        	}
        }
        return serviceProcedureDefinitions;
    }

    public void addServiceProcedure(ServiceProcedureDefinition serviceProcedureDefinition) {
    	List<AssemblyDefinition> components = serviceProcedureDefinition.getComponents();
    	Collections.sort(components);
    		Assembly assembly = findOrCreate(components.get(0));
    		for (int i = 1; i < components.size(); i++) {
    			assembly = assembly.findOrCreateAssembly(components.get(i));
    		}
    		assembly.createAction(serviceProcedureDefinition.getActionDefinition(),
    				serviceProcedureDefinition);
    }

    private Assembly findOrCreate(AssemblyDefinition definition) {
        for (Assembly assembly : this.assemblies) {
            if (assembly.getDefinition().equals(definition)) {
                return assembly;
            }
        }
        Assembly assembly = new Assembly(definition);
        this.assemblies.add(assembly);
        return assembly;
    }

    public ServiceProcedure findServiceProceduresForDefn(ServiceProcedureDefinition definition) {
        return findSeriveProcedure(definition.getCode());
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}
