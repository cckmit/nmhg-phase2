package tavant.twms.action;

/**
 * @author janmejay.singh
 *         Date: Aug 13, 2007
 *         Time: 1:40:50 PM
 */
public interface ThemeProvider {
    public static final String DEFAULT_CSS_THEME_KEY = "css.defaultTheme";
    public static final String CSS_THEME_SESSION_KEY = "session.cssTheme";

    public String getCssTheme();
}
