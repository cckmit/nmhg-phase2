package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.Component;
import org.apache.struts2.views.jsp.ComponentTagSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @see JsConfig
 *
 * @author janmejay.singh
 */
@SuppressWarnings("serial")
public class JsConfigTag extends ComponentTagSupport {

    private String debug;
    private String allowQueryConfig;
    private String baseScriptUri;
    private String baseRelativePath;
    private String libraryScriptUri;
    private String iePreventClobber;
    private String ieClobberMinimal;
    private String preventBackButtonFix;
    private String searchIds;
    private String parseWidgets;

    @Override
    public Component getBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        return new JsConfig(stack, request, response);
    }

    @Override
    public void populateParams() {
        JsConfig config = (JsConfig) component;
        if(debug != null)
            config.setDebug(debug);
        if(allowQueryConfig != null)
            config.setAllowQueryConfig(allowQueryConfig);
        if(baseRelativePath != null)
            config.setBaseRelativePath(baseRelativePath);
        if(baseScriptUri != null)
            config.setBaseScriptUri(baseScriptUri);
        if(libraryScriptUri != null)
            config.setLibraryScriptUri(libraryScriptUri);
        if(iePreventClobber != null)
            config.setIePreventClobber(iePreventClobber);
        if(ieClobberMinimal != null)
            config.setIeClobberMinimal(ieClobberMinimal);
        if(preventBackButtonFix != null)
            config.setPreventBackButtonFix(preventBackButtonFix);
        if(searchIds != null)
            config.setSearchIds(searchIds);
        if(parseWidgets != null)
            config.setParseWidgets(parseWidgets);
    }

    public String getAllowQueryConfig() {
        return allowQueryConfig;
    }

    public void setAllowQueryConfig(String allowQueryConfig) {
        this.allowQueryConfig = allowQueryConfig;
    }

    public String getBaseRelativePath() {
        return baseRelativePath;
    }

    public void setBaseRelativePath(String baseRelativePath) {
        this.baseRelativePath = baseRelativePath;
    }

    public String getBaseScriptUri() {
        return baseScriptUri;
    }

    public void setBaseScriptUri(String baseScriptUri) {
        this.baseScriptUri = baseScriptUri;
    }

    public String getDebug() {
        return debug;
    }

    public void setDebug(String debug) {
        this.debug = debug;
    }

    public String getIeClobberMinimal() {
        return ieClobberMinimal;
    }

    public void setIeClobberMinimal(String ieClobberMinimal) {
        this.ieClobberMinimal = ieClobberMinimal;
    }

    public String getIePreventClobber() {
        return iePreventClobber;
    }

    public void setIePreventClobber(String iePreventClobber) {
        this.iePreventClobber = iePreventClobber;
    }

    public String getLibraryScriptUri() {
        return libraryScriptUri;
    }

    public void setLibraryScriptUri(String libraryScriptUri) {
        this.libraryScriptUri = libraryScriptUri;
    }

    public String getParseWidgets() {
        return parseWidgets;
    }

    public void setParseWidgets(String parseWidgets) {
        this.parseWidgets = parseWidgets;
    }

    public String getPreventBackButtonFix() {
        return preventBackButtonFix;
    }

    public void setPreventBackButtonFix(String preventBackButtonFix) {
        this.preventBackButtonFix = preventBackButtonFix;
    }

    public String getSearchIds() {
        return searchIds;
    }

    public void setSearchIds(String searchIds) {
        this.searchIds = searchIds;
    }
}
