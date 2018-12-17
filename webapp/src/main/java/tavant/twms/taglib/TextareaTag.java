package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see Textarea
 * 
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class TextareaTag extends InputComponentTag {

	private String rows = "4";
	private String cols = "80";
	private String maxLength = "4000";
	private String wrap;

	public void setWrap(String wrap) {
		this.wrap = wrap;
	}

	@Override
	public Component getBean(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		return new Textarea(stack, request, response);
	}

	@Override
	public void populateParams() {
		super.populateParams();
		Textarea field = (Textarea) component;
		field.setMaxLength(maxLength);
		field.setCols(cols);
		field.setRows(rows);
		field.setWrap(wrap);

	}

	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}
}
