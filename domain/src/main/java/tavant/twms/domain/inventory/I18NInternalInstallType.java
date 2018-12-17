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
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class I18NInternalInstallType extends I18NBaseText {

	@Id
	@GeneratedValue(generator = "I18NInternalInstallType")
	@GenericGenerator(name = "I18NInternalInstallType", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "I18N_INT_INSTALL_TYPE_SEQ"),
			@Parameter(name = "initial_value", value = "1"),
			@Parameter(name = "increment_size", value = "1") })
	private Long id;
	
	private String internalInstallType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getInternalInstallType() {
		return internalInstallType;
	}

	public void setInternalInstallType(String internalInstallType) {
		this.internalInstallType = internalInstallType;
	}
	
}
