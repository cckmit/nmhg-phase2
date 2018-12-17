package tavant.twms.domain.common;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.infra.i18n.I18NBaseText;

@SuppressWarnings("serial")
@Entity
public class I18NCampaignText extends I18NBaseText {
	@Id
	@GeneratedValue(generator = "I18NCampaignText")
	@GenericGenerator(name = "I18NCampaignText", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "I18N_Campaign_Text_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	 @Column(length = 4000)
	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description != null) {
			String updatedDesc = description
					.replaceAll(
							"\\u0022|\u0025|\u0026|\u0027|\u002C|\u003A|\u003B|\u003C|\u003E|"
									+ "\u00A6|\u00A7|\u00A9|\u00AE|\u00B0|\u00B1|\u00B4|\u00B6|\u00B7|"
									+ "\u00B8|\u00F7|\u02DC|\u2013|\u2014|\u2018|\u2019|\u201C|\u201D|"
									+ "\u201E|\u2022|\u2044|\u2122|\u2190|\u2191|\u2192|\u2193|\u2194",
							"");
			if (updatedDesc.length() >= 3500) {
				this.description = updatedDesc.substring(0, 3500);
			} else {
				this.description = updatedDesc;
			}
		}
	}
}
