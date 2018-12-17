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
package tavant.twms.domain.claim;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import org.hibernate.annotations.AccessType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.core.style.ToStringCreator;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class Job implements Comparable<Job>, AuditableColumns {

    @Id
    @AccessType("field")
    @GeneratedValue(generator = "Job")
	@GenericGenerator(name = "Job", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "JOB_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @ManyToOne(cascade = { ALL }, fetch = LAZY)
    @JoinColumn(name = "JOB_DEFINITION")
    private JobDefinition definition;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    public Job(JobDefinition definition) {
        this.definition = definition;
    }

    public Job() {
        // Required.
    }

    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    public JobDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(JobDefinition definition) {
        this.definition = definition;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("id", this.id).append("job definition",
                                                                      this.definition).toString();
    }

    public int compareTo(Job otherJob) {

        final JobDefinition thisDefinition = getDefinition();
        final JobDefinition otherDefinition = otherJob.getDefinition();

        if (thisDefinition == null || otherDefinition == null) {
            return -1; // anything other than 0 is fine
        }

        final String thisCode = thisDefinition.getCode();
        final String otherCode = otherDefinition.getCode();

        if (thisCode == null || otherCode == null) {
            return -1; // anything other than 0 is fine
        } else {
            return thisCode.compareTo(otherCode);
        }
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
}