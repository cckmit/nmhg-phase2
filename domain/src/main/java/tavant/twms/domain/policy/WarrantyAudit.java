package tavant.twms.domain.policy;

import tavant.twms.security.AuditableColumns;
import tavant.twms.domain.common.AuditableColEntity;

import javax.persistence.*;
import java.sql.Types;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Cascade;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Aug 29, 2008
 * Time: 11:28:04 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class WarrantyAudit implements  AuditableColumns {

     @Id
    @GeneratedValue(generator = "WarrantyAudit")
	@GenericGenerator(name = "WarrantyAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "WARRANTY_AUDIT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;

    @Type(type = "org.hibernate.type.EnumType", parameters = {
            @Parameter(name = "enumClass", value = "tavant.twms.domain.policy.WarrantyStatus"),
            @Parameter(name = "type", value = "" + Types.VARCHAR) })
    private WarrantyStatus status;

    private String internalComments;

    private String externalComments;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "for_warranty", insertable = false, updatable = false)
    private Warranty forWarranty;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @Transient
    List<RegisteredPolicy> selectedPolicies = new ArrayList<RegisteredPolicy>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WarrantyStatus getStatus() {
        return status;
    }

    public void setStatus(WarrantyStatus status) {
        this.status = status;
    }

    public String getInternalComments() {
        return internalComments;
    }

    public void setInternalComments(String internalComments) {
        this.internalComments = internalComments;
    }

    public String getExternalComments() {
        return externalComments;
    }

    public void setExternalComments(String externalComments) {
        this.externalComments = externalComments;
    }

    public Warranty getForWarranty() {
        return forWarranty;
    }

    public void setForWarranty(Warranty forWarranty) {
        this.forWarranty = forWarranty;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public List<RegisteredPolicy> getSelectedPolicies() {
        return selectedPolicies;
    }

    public void setSelectedPolicies(List<RegisteredPolicy> selectedPolicies) {
        this.selectedPolicies = selectedPolicies;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
