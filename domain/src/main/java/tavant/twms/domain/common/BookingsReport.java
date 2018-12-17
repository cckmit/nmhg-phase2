package tavant.twms.domain.common;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "bookings_report")
public class BookingsReport {

	@Id
    @GeneratedValue(generator = "BookingsReport")
	@GenericGenerator(name = "BookingsReport", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "BOOKING_REPORT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;
    
    private Date invTransLastProcessedTime;
    
    private String failedBookingIds;
    
    private Date warrantyLastProcessedTime;
    
	@Column (name="no_of_dr",nullable = true)
    private Integer noOfDR;
    
	@Column (name="no_of_d2d",nullable = true)
    private Integer noOfD2D;
    
	@Column (nullable = true)
	private Integer noOfSignatureSheet;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getNoOfDR() {
		return noOfDR;
	}

	public void setNoOfDR(Integer noOfDR) {
		this.noOfDR = noOfDR;
	}

	public Integer getNoOfD2D() {
		return noOfD2D;
	}

	public void setNoOfD2D(Integer noOfD2D) {
		this.noOfD2D = noOfD2D;
	}

	
	public Integer getNoOfSignatureSheet() {
		return noOfSignatureSheet;
	}

	public void setNoOfSignatureSheet(Integer noOfSignatureSheet) {
		this.noOfSignatureSheet = noOfSignatureSheet;
	}

	public Date getInvTransLastProcessedTime() {
		return invTransLastProcessedTime;
	}

	public void setInvTransLastProcessedTime(Date invTransLastProcessedTime) {
		this.invTransLastProcessedTime = invTransLastProcessedTime;
	}

	public Date getWarrantyLastProcessedTime() {
		return warrantyLastProcessedTime;
	}

	public void setWarrantyLastProcessedTime(Date warrantyLastProcessedTime) {
		this.warrantyLastProcessedTime = warrantyLastProcessedTime;
	}

	public String getFailedBookingIds() {
		return failedBookingIds;
	}

	public void setFailedBookingIds(String failedBookingIds) {
		this.failedBookingIds = failedBookingIds;
	}

  
}
