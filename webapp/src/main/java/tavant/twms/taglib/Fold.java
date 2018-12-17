/**
 *
 */
package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;
import static tavant.twms.taglib.TaglibUtil.getBoolean;
import static tavant.twms.taglib.TaglibUtil.isUsedBefore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author aniruddha.chaturvedi, janmejay.singh
 *
 */
public class Fold extends UIBean {

	private String foldableClass;
	private String shownInitially;
	private String tagType;
	private boolean tagWasUsedBefore;

    final public static String TEMPLATE = "fold";

    public Fold(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        tagWasUsedBefore = isUsedBefore(request, Fold.class);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    @Override
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        boolean shownInitially = false;
        addParameter("foldableClass", foldableClass);
        addParameter("tagType", tagType);
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
        if(this.shownInitially != null) shownInitially = getBoolean(findValue(this.shownInitially, Boolean.class));
        addParameter("shownInitially", shownInitially);
    }

	public String getFoldableClass() {
		return foldableClass;
	}

	public void setFoldableClass(String foldableClass) {
		this.foldableClass = foldableClass;
	}

	public String getShownInitially() {
		return shownInitially;
	}

	public void setShownInitially(String shownInitially) {
		this.shownInitially = shownInitially;
	}

	public String getTagType() {
		return tagType;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}
}
