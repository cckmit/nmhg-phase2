package tavant.twms.domain.inventory;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.infra.i18n.I18NBaseText;

@SuppressWarnings("serial")
@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class I18NMaintenanceContractText extends I18NBaseText{

	@Id
	@GeneratedValue(generator = "I18NMaintenanceContractText")
	@GenericGenerator(name = "I18NMaintenanceContractText", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "I18NMAINTENANCE_CNTRT_TEXT_SEQ"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "increment_size", value = "1") })
	private Long id;
	
	private String maintenanceContract;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMaintenanceContract() {
		return maintenanceContract;
	}

	public void setMaintenanceContract(String maintenanceContract) {
		this.maintenanceContract = maintenanceContract;
	}

}
