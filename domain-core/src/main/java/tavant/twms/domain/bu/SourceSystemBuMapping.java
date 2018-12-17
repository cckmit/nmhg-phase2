package tavant.twms.domain.bu;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "source_system_bu_mapping")
@SuppressWarnings("serial")
public class SourceSystemBuMapping implements Serializable {

	@Id
	@Column(name = "source_system")
	private String sourceSystem;
	
	@Column(name = "bu_name")
	private String buName;

	public String getSourceSystem() {
		return sourceSystem;
	}

	public void setSourceSystem(String sourceSystem) {
		this.sourceSystem = sourceSystem;
	}

	public String getBuName() {
		return buName;
	}

	public void setBuName(String buName) {
		this.buName = buName;
	}

}
