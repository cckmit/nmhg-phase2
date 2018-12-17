package tavant.twms.domain.inventory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.domainlanguage.time.CalendarDate;

/**
 * @author partha.raghunathan
 * 
 */

@Entity
public class InventoryComment {
	@Id
	@GeneratedValue(generator = "InventoryComment")
	@GenericGenerator(name = "InventoryComment", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "INVENTORY_COMMENT_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_item", insertable = false, updatable = false)
	public InventoryItem inventoryItem;
	
	public int sequenceNumber;
	
	public String userId;
	
	@Type(type = "tavant.twms.infra.CalendarDateUserType")
	public CalendarDate dateOfComment;
	
	@Column(name = "NEMIS_COMMENT", length = 4000)
	public String comment;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public CalendarDate getDateOfComment() {
		return dateOfComment;
	}

	public void setDateOfComment(CalendarDate dateOfComment) {
		this.dateOfComment = dateOfComment;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
