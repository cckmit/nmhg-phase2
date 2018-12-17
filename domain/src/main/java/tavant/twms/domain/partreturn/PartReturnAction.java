package tavant.twms.domain.partreturn;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.Filter;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Cascade;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.security.AuditableColumns;



@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class PartReturnAction implements AuditableColumns{

	@Id
	@GeneratedValue
	private Long id;
	
	private String actionTaken;
	
	private Long value;

    private String shipmentId;

    private String trackingNumber;

    private String wpraNumber;

    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();
	
    public PartReturnAction() {
    }

    public PartReturnAction (String actionTaken,int value){
		this.actionTaken=actionTaken;
		this.value =new Long(value);
	}

	public String getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(String actionTaken) {
		this.actionTaken = actionTaken;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getWpraNumber() {
        return wpraNumber;
    }

    public void setWpraNumber(String wpraNumber) {
        this.wpraNumber = wpraNumber;
    }
}
