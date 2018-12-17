package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <ul>
 * <li>Creates a HTML textarea.</li>
 * <li>It can publish an event on change(optional).</li>
 * <li>It can execute a function when changed(optional).</li>
 * </ul>
 * 
 * @author janmejay.singh
 * @t.tag name="textarea" tld-body-content="empty" description="textarea tag"
 *        tld-tag-class="tavant.twms.taglib.TextareaTag"
 */
public class Textarea extends InputComponent {

	public static final String TEMPLATE = "twms_textarea";

	private String rows;
	private String cols;
	private String maxLength;
	private String wrap;

	public Textarea(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	public void evaluateExtraParams() {
		super.evaluateExtraParams();
		if (cols != null) {
			addParameter("cols", findString(cols));
		}

		if (rows != null) {
			addParameter("rows", findString(rows));
		}

		if (maxLength != null) {
			addParameter("maxLength", findString(maxLength));
		}
		if (wrap != null) {
			addParameter("wrap", findString(wrap));
		}

		if (cssStyle != null) {
			addParameter("cssStyle", findString(cssStyle));
		}

	}

	@Override
	protected String getDefaultTemplate() {
		return TEMPLATE;
	}

	public String getCols() {
		return cols;
	}

	public void setCols(String cols) {
		this.cols = cols;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(String maxLength) {
		this.maxLength = maxLength;
	}

	public void setWrap(String wrap) {
		this.wrap = wrap;
	}

}
