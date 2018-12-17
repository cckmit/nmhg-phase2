/**
 *
 */
package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ui.AbstractUITag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author aniruddha.chaturvedi, janmejay.singh
 *
 */
@SuppressWarnings("serial")
public class FoldTag extends AbstractUITag {

	private String shownInitially;
	private String foldableClass;
	private String tagType;

	@Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new Fold(stack, request, response);
    }

    @Override
    protected void populateParams() {
        super.populateParams();
        Fold link = (Fold) component;
        link.setFoldableClass(foldableClass);
        link.setShownInitially(shownInitially);
        link.setTagType(tagType);
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
