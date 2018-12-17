package tavant.twms.taglib;

import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.components.UIBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <ul>
 * <li>Generates javascript to set dojo's djConfig assoc array correctly.(takes care of dojo components internationalization)</li> 
 * </ul>
 *
 * @author janmejay.singh
 * @t.tag name="djConfig" tld-body-content="empty" description="DjConfig tag" tld-tag-class="tavant.twms.taglib.DjConfigTag"
 */
public class JsConfig extends UIBean {
    
    public static final String TEMPLATE = "twms_jsConfig";
    
    private boolean tagWasUsedBefore;
    
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

    public JsConfig(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.tagWasUsedBefore = TaglibUtil.isUsedBefore(request, this.getClass());
        setDefaultValues();
    }
    
    @Override
    public void evaluateExtraParams() {
        addParameter("tagWasUsedBefore", tagWasUsedBefore);
        addParameter("locale", TaglibUtil.getDojoLocale(request));
        addParameter("debug", debug);
        addParameter("allowQueryConfig", allowQueryConfig);
        addParameter("baseScriptUri", baseScriptUri);
        addParameter("baseRelativePath", baseRelativePath);
        addParameter("libraryScriptUri", libraryScriptUri);
        addParameter("iePreventClobber", iePreventClobber);
        addParameter("ieClobberMinimal", ieClobberMinimal);
        addParameter("preventBackButtonFix", preventBackButtonFix);
        addParameter("searchIds", searchIds);
        addParameter("parseWidgets", parseWidgets);
    }

    @Override
    protected String getDefaultTemplate() {
        return TEMPLATE;
    }
    
    private void setDefaultValues() {
        debug = Boolean.toString(false);
        allowQueryConfig = Boolean.toString(false);
        baseScriptUri = "";
        baseRelativePath = "";
        libraryScriptUri = "";
        iePreventClobber = Boolean.toString(false);
        ieClobberMinimal = Boolean.toString(true);
        preventBackButtonFix = Boolean.toString(true);
        searchIds = "";
        parseWidgets = Boolean.toString(true);
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
