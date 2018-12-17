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

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * @author kamal.govindraj
 * 
 */
@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class ActionLevel implements AuditableColumns{

    @Id
    @GeneratedValue(generator = "ActionLevel")
	@GenericGenerator(name = "ActionLevel", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ACTION_LEVEL_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    private String nextCodeValue;

    private CodeGeneratorType generatorType;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public CodeGeneratorType getGeneratorType() {
        return this.generatorType;
    }

    public void setGeneratorType(CodeGeneratorType generatorType) {
        this.generatorType = generatorType;
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

    public String getNextCodeValue() {
        return this.nextCodeValue;
    }

    public void setNextCodeValue(String nextCodeValue) {
        this.nextCodeValue = nextCodeValue;
    }

    public ActionDefinition create(String name) {
        CodeGeneratorFactory codeGeneratorFactory = new CodeGeneratorFactory();
        this.nextCodeValue = codeGeneratorFactory.getGenerator(this.generatorType)
                .nextCode(this.nextCodeValue);
        return new ActionDefinition(name, this.nextCodeValue);
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

}
