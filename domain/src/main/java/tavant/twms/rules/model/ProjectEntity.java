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
package tavant.twms.rules.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.validator.constraints.NotEmpty;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

/**
 * This class complements the rules and processes, typically any project
 * resource in the studio which is not a process or a rule belongs to this
 * entity
 * 
 * @author kannan.ekanath
 * 
 */
@Entity()
@Filters({
  @Filter(name="excludeInactive")
})
@Table(name="model_project_entity")
//@org.hibernate.annotations.Table(appliesTo="model_project_entity",
//   namedUniqueConstraints={
//        @NamedUniqueConstraint(name="unique_project",columnNames="name")
//}
//)
public class ProjectEntity implements ModelObject, AuditableColumns {
	@Id
	@GeneratedValue(generator = "ModelProcessEntity")
	@GenericGenerator(name = "ModelProcessEntity", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "MODEL_PROJENTITY_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
        
        @NotEmpty
	private String name;

        @Column(length = 4000)
	private String description;

        @Column(length = 4000)
        @NotEmpty
	private String path;

        @Lob
        @Column(length = 1048576  )
        @NotEmpty
	private String script;
        
        @Embedded
    	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}
