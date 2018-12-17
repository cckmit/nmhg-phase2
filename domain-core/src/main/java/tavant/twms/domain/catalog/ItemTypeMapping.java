package tavant.twms.domain.catalog;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class ItemTypeMapping {

	@Id
	@GeneratedValue(generator = "ItemTypeMapping")
	@GenericGenerator(name = "ItemTypeMapping", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ITEM_TYPE_MAPPING_SEQ "),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	private String externalItemType;

	private String itemType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExternalItemType() {
		return externalItemType;
	}

	public void setExternalItemType(String externalItemType) {
		this.externalItemType = externalItemType;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

}
