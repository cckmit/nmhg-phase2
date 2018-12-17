package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Writer;

/**
 * <ul>
 *
 * <li>If browser is InternetExplorer, body of tag is displayed.</li>
 *
 * </ul>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 *  &lt;t:isIE&gt;
 *          &lt;div&gt;Will Be Executed If BROWSER Is InternetExplorer, Else Will Not Be Executed.&lt;/div&gt;
 *  &lt;/t:isIE&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author janmejay.singh
 * @t.tag name="isIE" tld-body-content="JSP" description="IsIE tag" tld-tag-class="tavant.twms.taglib.IsIETag"
 */
public class IsIE extends Component {
	public static final String ANSWER = "twms.isIE.answer";

    Boolean answer;
    HttpServletRequest request;

    public IsIE(ValueStack stack, HttpServletRequest req) {
        super(stack);
        this.request = req;
    }

    @SuppressWarnings("unchecked")
    public boolean start(Writer writer) {
    	answer = TaglibUtil.isIE(request);
        stack.getContext().put(ANSWER, answer);
        return answer;
    }

    @SuppressWarnings("unchecked")
    public boolean end(Writer writer, String body) {
        stack.getContext().put(ANSWER, answer);
        return super.end(writer, body);
    }
}