package tavant.twms.domain.upload.controller;

import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import tavant.twms.domain.common.I18NUploadErrorText;
import tavant.twms.security.authz.infra.SecurityHelper;


@Entity
@Table(name = "upload_error")
public class UploadError {

	@Id
	@GeneratedValue(generator = "UploadError")
	@GenericGenerator(name = "UploadError", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "UPLOAD_ERROR_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
	private Long id;
	
	private String code;
	
	private String uploadField;
	
	@OneToMany(fetch = FetchType.EAGER)
	@Cascade( { org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	@JoinColumn(name = "upload_error", nullable = false)
	private List<I18NUploadErrorText>  i18nUploadErrorTexts = new ArrayList<I18NUploadErrorText>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getUploadField() {
		return uploadField;
	}

	public void setUploadField(String uploadField) {
		this.uploadField = uploadField;
	}

	public List<I18NUploadErrorText> getI18nUploadErrorTexts() {
		return i18nUploadErrorTexts;
	}

	public void setI18nUploadErrorTexts(List<I18NUploadErrorText> uploadErrorTexts) {
		i18nUploadErrorTexts = uploadErrorTexts;
	}

	public String getDescription() {
		String locale = new SecurityHelper().getLoggedInUser().getLocale().toString(); 
		return getDescription(locale);
	}
	
	public String getDescription(String locale) {
		String name_locale="";
		for (I18NUploadErrorText i18nUploadError: this.i18nUploadErrorTexts) {
			if (i18nUploadError!=null && i18nUploadError.getLocale()!=null && 
					i18nUploadError.getLocale().equalsIgnoreCase(
					locale) && i18nUploadError.getDescription() != null) {
				name_locale = i18nUploadError.getDescription();
				break;
			}
			else if(i18nUploadError !=null && i18nUploadError.getLocale()!=null && 
					i18nUploadError.getLocale().equalsIgnoreCase("en_US")) {
				name_locale = i18nUploadError.getDescription();
			}
		}
		return name_locale;
	}
}
