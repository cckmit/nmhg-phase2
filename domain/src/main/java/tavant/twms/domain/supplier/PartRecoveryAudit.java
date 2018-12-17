package tavant.twms.domain.supplier;

import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.partreturn.PartReturnAction;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.security.AuditableColumns;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 10/7/13
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Filters({
        @Filter(name="excludeInactive")
})
public class PartRecoveryAudit implements AuditableColumns {

    @Id
    @GeneratedValue(generator = "PartRecoveryAudit")
    @GenericGenerator(name = "PartRecoveryAudit", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
            @Parameter(name = "sequence_name", value = "PART_RECOVERY_AUDIT_SEQ"),
            @Parameter(name = "initial_value", value = "1000"),
            @Parameter(name = "increment_size", value = "20") })
    private Long id;

    @Version
    private int version;


    private String comments;

    private String prStatus;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "for_part_replaced", updatable = false,insertable = false)
    private RecoverablePart forPartReplaced;

    private String shipmentNumber;

    private String trackingNo;

    private String failureCause;

    private String acceptanceCause;

    @Embedded
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    protected AuditableColEntity d = new AuditableColEntity();

    public PartRecoveryAudit() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    public String getPrStatus() {
        return prStatus;
    }

    public void setPrStatus(String prStatus) {
        this.prStatus = prStatus;
    }

    public RecoverablePart getForPartReplaced() {
        return forPartReplaced;
    }

    public void setForPartReplaced(RecoverablePart forPartReplaced) {
        this.forPartReplaced = forPartReplaced;
    }

    public AuditableColEntity getD() {
        return d;
    }

    public void setD(AuditableColEntity d) {
        this.d = d;
    }

    public String getShipmentNumber() {
        return shipmentNumber;
    }

    public void setShipmentNumber(String shipmentNumber) {
        this.shipmentNumber = shipmentNumber;
    }

    public String getTrackingNo() {
        return trackingNo;
    }

    public void setTrackingNo(String trackingNo) {
        this.trackingNo = trackingNo;
    }

    public PartRecoveryAudit clone() {
        PartRecoveryAudit partRecoveryAudit = new PartRecoveryAudit();
        partRecoveryAudit.setComments(comments);
        partRecoveryAudit.setForPartReplaced(forPartReplaced);
        partRecoveryAudit.setPrStatus(prStatus);
        partRecoveryAudit.setShipmentNumber(shipmentNumber);
        partRecoveryAudit.setTrackingNo(trackingNo);
        partRecoveryAudit.setPartReturnAction1(partReturnAction1);
        partRecoveryAudit.setPartReturnAction2(partReturnAction2);
        partRecoveryAudit.setPartReturnAction3(partReturnAction3);
        partRecoveryAudit.setFailureCause(failureCause);
        partRecoveryAudit.setAcceptanceCause(acceptanceCause);
        return partRecoveryAudit;
    }

    @OneToOne(fetch = FetchType.LAZY,optional = true,cascade = { CascadeType.ALL })
    private PartReturnAction partReturnAction1;

    @OneToOne(fetch = FetchType.LAZY,optional = true,cascade = { CascadeType.ALL })
    private PartReturnAction partReturnAction2;

    @OneToOne(fetch = FetchType.LAZY,optional = true,cascade = { CascadeType.ALL })
    private PartReturnAction partReturnAction3;

    public PartReturnAction getPartReturnAction1() {
        return partReturnAction1;
    }

    public void setPartReturnAction1(PartReturnAction partReturnAction1) {
        this.partReturnAction1 = partReturnAction1;
    }

    public PartReturnAction getPartReturnAction2() {
        return partReturnAction2;
    }

    public void setPartReturnAction2(PartReturnAction partReturnAction2) {
        this.partReturnAction2 = partReturnAction2;
    }

    public PartReturnAction getPartReturnAction3() {
        return partReturnAction3;
    }

    public void setPartReturnAction3(PartReturnAction partReturnAction3) {
        this.partReturnAction3 = partReturnAction3;
    }

    public String getFailureCause() {
        return failureCause;
    }

    public void setFailureCause(String failureCause) {
        this.failureCause = failureCause;
    }

    public String getAcceptanceCause() {
        return acceptanceCause;
    }

    public void setAcceptanceCause(String acceptanceCause) {
        this.acceptanceCause = acceptanceCause;
    }
}
