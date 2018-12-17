package tavant.twms.domain.uom;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.infra.i18n.I18NBaseText;


@SuppressWarnings("serial")
@Entity
public class I18nUomMappingValues extends I18NBaseText {

	@Id
	@GeneratedValue(generator = "I18nUomMappingValues")
	@GenericGenerator(name = "I18nUomMappingValues", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "I18N_UOM_MAPPING_VALUES_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	private String mappedUom;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMappedUom() {
		return mappedUom;
	}

	public void setMappedUom(String mappedValue) {
		this.mappedUom = mappedValue;
	}
	
	
	
}
