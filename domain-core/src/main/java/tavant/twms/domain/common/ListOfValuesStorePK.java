package tavant.twms.domain.common;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Locale;

import javax.persistence.Embeddable;

@Embeddable
public class ListOfValuesStorePK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3762520741295515842L;

	private String code;
	private Locale locale;

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
