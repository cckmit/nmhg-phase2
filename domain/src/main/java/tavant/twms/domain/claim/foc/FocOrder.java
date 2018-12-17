package tavant.twms.domain.claim.foc;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;

@Entity
@Table(name="FOC_ORDER_DETAILS")
public class FocOrder  implements AuditableColumns{
	
	public static final String ORDER_PERSISTED = "ORDER_PERSISTED";
	
	public static final String CLAIM_INFO_AWAITED = "CLAIM_INFO_AWAITED";
	
	public static final String CLAIM_INFO_RECEIVED = "CLAIM_INFO_RECEIVED";
	
	public static final String CLAIM_CREATED = "CLAIM_CREATED";
	
	
	@Id
    @GeneratedValue(generator = "FocOrder")
	@GenericGenerator(name = "FocOrder", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "FOC_ORDER_DETAILS_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;		
	
	private String orderNo;
	
    @Lob
    @Column(length = 16777210)
    private String orderInfo;
    
    @Lob
    @Column(length = 16777210)
    private String claimInfo;
    
    private String status;

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    private AuditableColEntity d = new AuditableColEntity();
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(String orderInfo) {
		this.orderInfo = orderInfo;
	}

	public String getClaimInfo() {
		return claimInfo;
	}

	public void setClaimInfo(String claimInfo) {
		this.claimInfo = claimInfo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    
}
