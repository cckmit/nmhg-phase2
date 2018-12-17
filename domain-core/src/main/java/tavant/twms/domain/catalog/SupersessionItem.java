package tavant.twms.domain.catalog;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class SupersessionItem {
	@Id
	@GeneratedValue(generator = "SupersessionItem")
	@GenericGenerator(name = "SupersessionItem", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "SUPERSESSION_ITEM_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20")})
	private Long id;
	
	@OneToOne
	@JoinColumn(name="old_item_id")
	private Item oldItem;

	@OneToOne
	@JoinColumn(name="new_item_id")
	private Item newItem;

	private Date startDate;
	private Date endDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Item getOldItem() {
		return oldItem;
	}

	public void setOldItem(Item oldItem) {
		this.oldItem = oldItem;
	}

	public Item getNewItem() {
		return newItem;
	}

	public void setNewItem(Item newItem) {
		this.newItem = newItem;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
