package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <ul>
 * <li>This abstract class is to be inherited by any component that has auto auggest feature.</li> 
 * </ul>
 *
 * @author janmejay.singh
 */
public abstract class AutoSuggestable extends InputComponent {
    
    private List<String> associatedFieldIds;
    private String autoComplete;
    private String url;
    private String searchDelay;
    private String maxListLength;
    private boolean autoSuggest;//the field that tells weather auto suggest is enabled or not.
    
    public AutoSuggestable(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }
    
    public void evaluateExtraParams() {
        super.evaluateExtraParams();
        if(autoSuggest) {
            addParameter("autoSuggest", autoSuggest);
            addParameter("autoSuggestWasUsedBefore", TaglibUtil.isUsedBefore(request, AutoSuggestable.class));
            url = addParamToURL(findString(url), name, "%{searchString}");
            if(autoComplete != null) {
                addParameter("autoComplete", autoComplete);
            } else {
                addParameter("autoComplete", Boolean.toString(false));//default is false
            }
            if(url != null) {
                addParameter("url", url);//mandatory attribute
            } else {
                throw new IllegalArgumentException("'url' is a mandatory attribute when autoSuggest(attribute) is set to true.");
            }
            if(searchDelay != null) {
                addParameter("searchDelay", searchDelay);
            } else {
                addParameter("searchDelay", String.valueOf(650));//default is 650 mills
            }
            if(maxListLength != null) {
                addParameter("maxListLength", maxListLength);
            } else {
                addParameter("maxListLength", String.valueOf(10));//default is 10 rows
            }
            if(associatedFieldIds != null) {
                addParameter("associatedFieldIds", associatedFieldIds);
            }
        }
    }

    private String addParamToURL(String url, String name, String value) {
        if(url.indexOf("?") > -1) {
            url = url + "&" + name + "=" + value;
        } else {
            url = url + "?" + name + "=" + value;
        }
        return url;
    }

    public String getMaxListLength() {
        return maxListLength;
    }

    public void setMaxListLength(String maxListLength) {
        this.maxListLength = maxListLength;
    }

    public String getAutoComplete() {
        return autoComplete;
    }

    public void setAutoComplete(String autoComplete) {
        this.autoComplete = autoComplete;
    }

    public List<String> getAssociatedFieldIds() {
        return associatedFieldIds;
    }

    public void setAssociatedFieldIds(List<String> associatedFieldIds) {
        this.associatedFieldIds = associatedFieldIds;
    }
    
    public void setAssociatedFieldIds(String associatedFieldIds) {
        this.associatedFieldIds = TaglibUtil.splitBasedOnComma(associatedFieldIds);
    }

    public String getSearchDelay() {
        return searchDelay;
    }

    public void setSearchDelay(String searchDelay) {
        this.searchDelay = searchDelay;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAutoSuggest() {
        return Boolean.toString(autoSuggest);
    }

    public void setAutoSuggest(String autoSuggest) {
        this.autoSuggest = Boolean.parseBoolean(autoSuggest);
    }
    
    protected boolean isAutoSuggestEnabled() {
        return autoSuggest;
    }
}
