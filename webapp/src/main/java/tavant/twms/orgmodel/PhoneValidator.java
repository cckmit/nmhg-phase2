/**
 * 
 */
package tavant.twms.orgmodel;

import com.opensymphony.xwork2.validator.FieldValidator;
import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.ExpressionValidator;

/**
 * @author mritunjay.kumar
 * 
 */
public class PhoneValidator extends ExpressionValidator implements
		FieldValidator {
	private String fieldName;

	@Override
	public void validate(Object object) throws ValidationException {
		boolean phoneNumberFound = false;
		boolean validated = true;
		String fieldName = getFieldName();
		Object value = this.getFieldValue(fieldName, object);
		String number = (String) value;
		if (number != null && !"".equals(number.trim())) {
			char[] chr = number.toCharArray();
			for (int i = 0; i < number.length(); i++) {
				if ((chr[i] >= 'a' && chr[i] <= 'z')
						|| (chr[i] >= 'A' && chr[i] <= 'Z')) {
					validated = false;
					break;
				}
				if (chr[i] >= '0' && chr[i] <= '9') {
					phoneNumberFound = true;
				}
			}
			if (validated
					&& (number.indexOf("+") >= 0 || number.indexOf("-") >= 0
							|| number.indexOf("(") >= 0
							|| number.indexOf(")") >= 0 || number.indexOf(".") >= 0)) {
				validated = true;
			}
			if (validated
					&& (number.indexOf("!") != -1 || number.indexOf("@") != -1
							|| number.indexOf("#") != -1
							|| number.indexOf("$") != -1
							|| number.indexOf("%") != -1
							|| number.indexOf("^") != -1
							|| number.indexOf("&") != -1
							|| number.indexOf("*") != -1
							|| number.indexOf("<") != -1
							|| number.indexOf(">") != -1
							|| number.indexOf("?") != -1 || number.indexOf(")") == 0)) {
				validated = false;
			}
			if (validated
					&& (number.indexOf("+)") >= 0 || number.indexOf("-)") >= 0)) {
				validated = false;
			}
			if (validated
					&& (number.indexOf("(") >= 0 && number.indexOf(")") == -1)) {
				validated = false;
			}
			if (validated
					&& (number.indexOf(")") >= 0 && number.indexOf("(") == -1)) {
				validated = false;
			}
			if (!phoneNumberFound && validated) {
				validated = false;
			}
		}
		if (!validated) {
			getValidatorContext().addActionError(
					"error.manageCustomer.invalidPhoneFax");
		}
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
