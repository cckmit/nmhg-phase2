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

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author vineeth.varghese
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class AssemblyLevel implements AuditableColumns,Comparable<AssemblyLevel>{

	@Id
	@GeneratedValue(generator = "AssemblyLevel")
	@GenericGenerator(name = "AssemblyLevel", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ASSEMBLY_LEVEL_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String name;

    private String description;

    @Column(name = "level_value")
    private int level;

    private String nextCodeValue;

    private CodeGeneratorType generatorType;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public AssemblyLevel() {

    }

    public AssemblyLevel(String name, String description, int level, String nextCodeValue,
            CodeGeneratorType generatorType) {
        this.name = name;
        this.description = description;
        this.level = level;
        this.nextCodeValue = nextCodeValue;
        this.generatorType = generatorType;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * @param level the level to set
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRootLevel() {
        return this.level == 0;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("level", this.level)
                .append("name", this.name).append("description", this.description).toString();
    }

    public AssemblyDefinition createNewDefinition(String name) {
        CodeGeneratorFactory factory = new CodeGeneratorFactory();
        CodeGenerator codeGenerator = factory.getGenerator(this.generatorType);
        this.nextCodeValue = codeGenerator.nextCode(this.nextCodeValue);
        return new AssemblyDefinition(this.nextCodeValue, name, this);
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

	public int compareTo(AssemblyLevel o) {
		return new Integer(this.level).compareTo(new Integer(o.getLevel()));
	}
}
