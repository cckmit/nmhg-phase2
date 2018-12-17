package tavant.twms.web.common;

import org.json.JSONObject;
import tavant.twms.infra.BeanProvider;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * @author : janmejay.singh
 *         Date: Jul 5, 2007
 *         Time: 2:41:32 PM
 */
public abstract class ImgColAwarePropertyResolver extends I18nActionSupport implements BeanProvider {
    public static final String IMG_URL = "url",
                               IMG_TITLE = "title";

    protected JSONObject getImgColValue(String title, String url) {
        try {
            return new JSONObject().put(IMG_TITLE, title).put(IMG_URL, url);
        } catch (Exception e) {
            throw new IllegalStateException(e);//will never get thrown... just wrapping it... in a runtime exception.
        }
    }

    public static String getImageTitle(JSONObject imgColValue) {
        try {
            return (String) imgColValue.get(IMG_TITLE);
        } catch (Exception e) {
            throw new IllegalStateException(e);//will never get thrown... just wrapping it... in a runtime exception.
        }
    }
}
