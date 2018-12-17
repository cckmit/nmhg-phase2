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

import java.util.*;

import javax.persistence.Column;
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
import javax.persistence.Version;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.BatchSize;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class Assembly implements Comparable<Assembly>, AuditableColumns {
    public static final String FAULT_CODE_SEPARATOR = "-";

    @Id
    @GeneratedValue(generator = "Assembly")
	@GenericGenerator(name = "Assembly", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ASSEMBLY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "ASSEMBLY_DEFINITION")
    private AssemblyDefinition definition;

    @ManyToOne(fetch = FetchType.LAZY)
    private Assembly isPartOfAssembly;

    private Boolean treadAble = Boolean.FALSE;

    @OneToMany(mappedBy = "isPartOfAssembly", cascade = { ALL }, fetch = LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @BatchSize(size = 100)
    private Set<Assembly> composedOfAssemblies = new HashSet<Assembly>();

    @OneToMany(mappedBy = "definedFor", cascade = { ALL }, fetch = LAZY)
    @Cascade( { org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    @BatchSize(size = 100)
    private Set<ActionNode> actions = new HashSet<ActionNode>();

    @OneToOne(cascade = { ALL }, fetch = LAZY)
    private FaultCode faultCode;
    
    @Column(nullable=false)
    private Boolean active = new Boolean(true);
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    /**
     * Is this fault code present in the list of fault codes associated with the causal part or causal part's part class? 
     */
    @Transient
    private boolean intersectingFaultCode = false;

    @Transient
    private boolean intersectingAssembly = false;

    public Assembly() {

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

    public Assembly(AssemblyDefinition definition) {
        this.definition = definition;
    }

    public Set<Assembly> getComposedOfAssemblies() {
        return this.composedOfAssemblies;
    }

    public void setComposedOfAssemblies(Set<Assembly> composedOf) {
        this.composedOfAssemblies = composedOf;
    }

    public Assembly getIsPartOfAssembly() {
        return this.isPartOfAssembly;
    }

    public void setIsPartOfAssembly(Assembly isPartOf) {
        this.isPartOfAssembly = isPartOf;
    }

    public AssemblyDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(AssemblyDefinition assemblyDefinition) {
        this.definition = assemblyDefinition;
    }

    public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean isFaultCode() {
        return this.actions.size() > 0;
    }

    public String getFullCode() {
        List<String> parts = new ArrayList<String>();
        getFullCode(parts);
        Collections.reverse(parts);
        StringBuffer fullCode = new StringBuffer();
        for (Iterator iter = parts.iterator(); iter.hasNext();) {
            if (fullCode.length() > 0) {
                fullCode.append(FAULT_CODE_SEPARATOR);
            }
            fullCode.append((String) iter.next());
        }
        return fullCode.toString();
    }


    public String getFullDescription() {
        List<String> parts = new ArrayList<String>();
        getFullDescription(parts);
        Collections.reverse(parts);
        StringBuffer fullCode = new StringBuffer();
        for (Iterator iter = parts.iterator(); iter.hasNext();) {
            if (fullCode.length() > 0) {
                fullCode.append(FAULT_CODE_SEPARATOR);
            }
            fullCode.append((String) iter.next());
        }
        return fullCode.toString();
    }
    
    public Map getCodeAndName() {
        Map parts = new HashMap();
        getFullCode(parts);
        return parts;
    }

    public Boolean getTreadAble() {
        return this.treadAble;
    }

    public void setTreadAble(Boolean treadAble) {
        this.treadAble = treadAble;
    }

    public FaultCode getFaultCode() {
        return this.faultCode;
    }

    public void setFaultCode(FaultCode faultCode) {
        this.faultCode = faultCode;
    }

    private void getFullCode(List<String> parts) {
        parts.add(getDefinition().getCode());
        if (getIsPartOfAssembly() != null) {
            getIsPartOfAssembly().getFullCode(parts);
        }
    }
    
    private void getFullDescription(List<String> parts) {
        parts.add(getDefinition().getName());
        if (getIsPartOfAssembly() != null) {
            getIsPartOfAssembly().getFullCode(parts);
        }
    }

    private void getFullCode(Map parts) {
        parts.put(getDefinition().getCode(), getDefinition().getName());
        if (getIsPartOfAssembly() != null) {
            getIsPartOfAssembly().getFullCode(parts);
        }
    }

    public List<Assembly> getAssembliesForFaultCodeStartingWith(String faultCode) {
        return getAssembliesForFaultCodeStartingWith(faultCode.split(FAULT_CODE_SEPARATOR));
    }

    public List<Assembly> getAssembliesForFaultCodeStartingWith(String[] faultCodeParts) {
        return getAssembliesForFaultCodeStartingWith(faultCodeParts, 0);
    }

    private List<Assembly> getAssembliesForFaultCodeStartingWith(String[] faultCodeParts, int level) {
        List<Assembly> selectedNodes = new ArrayList<Assembly>();
        if (matches(this.definition, faultCodeParts[level])) {
            if (level == (faultCodeParts.length - 1)) {
                selectedNodes.addAll(getComposedOfAssemblies());
            } else {
                for (Iterator iter = getComposedOfAssemblies().iterator(); iter.hasNext();) {
                    Assembly childNode = (Assembly) iter.next();
                    if (matches(childNode.getDefinition(), faultCodeParts[level + 1])) {
                        selectedNodes.addAll(childNode
                                .getAssembliesForFaultCodeStartingWith(faultCodeParts, level + 1));
                    }
                }
            }
        }
        return selectedNodes;
    }

    private boolean matches(AssemblyDefinition definition, String faultCodePart) {
        return (definition.getCode().contains(faultCodePart) || definition.getName()
                .contains(faultCodePart));
    }

    public Assembly getNodeWithFullcode(String fullCode) {
        return getNodeWithFullcode(fullCode.split(FAULT_CODE_SEPARATOR), 0);

    }

    private Assembly getNodeWithFullcode(String[] faultCodeParts, int level) {
        if (this.getDefinition().getCode().equalsIgnoreCase(faultCodeParts[level])) {
            if (level == (faultCodeParts.length - 1)) {
                return this;
            } else {
                for (Iterator iter = getComposedOfAssemblies().iterator(); iter.hasNext();) {
                    Assembly childNode = (Assembly) iter.next();
                    Assembly matchingNode = childNode
                            .getNodeWithFullcode(faultCodeParts, level + 1);
                    if (matchingNode != null) {
                        return matchingNode;
                    }
                }
            }
        }
        return null;
    }

    public void addChildAssembly(Assembly child) {
        child.setIsPartOfAssembly(this);
        getComposedOfAssemblies().add(child);
    }

    public void removeChildAssembly(Assembly child) {
        child.setIsPartOfAssembly(null);
        getComposedOfAssemblies().remove(child);
    }

    public Set<ActionNode> getActions() {
        return this.actions;
    }

    public void setActions(Set<ActionNode> actions) {
        this.actions = actions;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).toString();
    }

    public int compareTo(Assembly otherItemGroup) {
        if ((this.definition != null) && (this.definition.getName() != null)) {
            return this.getDefinition().getName().compareTo(otherItemGroup.getDefinition().getName());
        } else {
            return -1; // anything other than 0 is fine
        }
    }

    public ActionNode getAction(String actionCode) {
        for (Iterator iter = this.actions.iterator(); iter.hasNext();) {
            ActionNode action = (ActionNode) iter.next();
            if (action.getDefinition().getCode().equalsIgnoreCase(actionCode)) {
                return action;
            }
        }
        return null;
    }

    public Set<ServiceProcedureDefinition> getServiceProcedureDefinitions() {
        Set<ServiceProcedureDefinition> serviceProcedureDefinitions = new HashSet<ServiceProcedureDefinition>();

        for (ActionNode actionNode : this.actions) {
        	if(actionNode.getActive()){
            serviceProcedureDefinitions.add(actionNode.getServiceProcedure().getDefinition());
        	}
        }

        for (Assembly assembly : this.composedOfAssemblies) {
        	if(assembly.getActive()){
            serviceProcedureDefinitions.addAll(assembly.getServiceProcedureDefinitions());
        	}
        }
        return serviceProcedureDefinitions;
    }

    public Assembly findOrCreateAssembly(AssemblyDefinition definition2) {    	
        for (Assembly assembly : this.composedOfAssemblies) {
            if (assembly.getDefinition().equals(definition2)) {            		
                return assembly;
            }
        }
        Assembly assembly = new Assembly(definition2);
        	if(definition2.getAssemblyLevel().getLevel()>1) {
        		this.addChildAssembly(assembly);
        	}       
        return assembly;
    }

    public void createAction(ActionDefinition actionDefinition,
            ServiceProcedureDefinition serviceProcedureDefinition) {
        ActionNode action = new ActionNode(this, actionDefinition, serviceProcedureDefinition);
        this.actions.add(action);
        if (this.faultCode == null) {
            List<AssemblyDefinition> components = new ArrayList<AssemblyDefinition>();
            Collections.sort(components);
            components.add(this.getDefinition());
            Assembly parent = this.getIsPartOfAssembly();
            while (parent != null) {
                components.add(parent.getDefinition());
                parent = parent.getIsPartOfAssembly();
            }
            Collections.reverse(components);
            this.faultCode = new FaultCode(new FaultCodeDefinition(this.getFullCode(), components));
        }

    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public Assembly getPartOfAssembly() {
        return isPartOfAssembly;
    }

    public void setPartOfAssembly(Assembly partOfAssembly) {
        isPartOfAssembly = partOfAssembly;
    }

    public boolean isIntersectingFaultCode() {
        return intersectingFaultCode;
    }

    public void setIntersectingFaultCode(boolean intersectingFaultCode) {
        this.intersectingFaultCode = intersectingFaultCode;
    }

    public boolean isIntersectingAssembly() {
        return intersectingAssembly;
    }

    public void setIntersectingAssembly(boolean intersectingAssembly) {
        this.intersectingAssembly = intersectingAssembly;
    }
}
