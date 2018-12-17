/**
 * 
 */
package tavant.twms.domain.upload.controller;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * @author surajdeo.prasad
 * 
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "upload_mgt_meta_data")
public class UploadManagementMetaData {
	@Id
	@GeneratedValue(generator = "UploadManagementMetaData")
	@GenericGenerator(name = "UploadManagementMetaData", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "UPLOAD_MGT_META_DATA_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	private String columnName;
	private String columnType; // values = Text or Number or Date
	private Short columnOrder;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Short getColumnOrder() {
		return columnOrder;
	}

	public void setColumnOrder(Short columnOrder) {
		this.columnOrder = columnOrder;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
}
