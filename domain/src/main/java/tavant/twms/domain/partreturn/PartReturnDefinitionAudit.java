package tavant.twms.domain.partreturn;

import java.util.Date;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Filters({
	@Filter(name="excludeInactive")
})
public class PartReturnDefinitionAudit implements AuditableColumns {

	@Id
	@GeneratedValue(generator="PartReturnDefinitionAudit")
	@GenericGenerator(name="PartReturnDefinitionAudit", strategy="org.hibernate.id.enhanced.SequenceStyleGenerator", parameters={
			@Parameter(name="sequence_name", value="PART_RETURN_DEFN_AUDIT_SEQ"),
			@Parameter(name="initial_value", value="1000"),
			@Parameter(name="increment_size", value="20")})
	private Long id;
	
	private String status;
	
	private String comments;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "for_definition", insertable = false, updatable = false)
	private PartReturnDefinition forDefinition;
	
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	protected AuditableColEntity d = new AuditableColEntity();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public PartReturnDefinition getForDefinition() {
		return forDefinition;
	}

	public void setForDefinition(PartReturnDefinition forDefinition) {
		this.forDefinition = forDefinition;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

}
