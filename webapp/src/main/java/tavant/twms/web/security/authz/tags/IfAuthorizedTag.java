package tavant.twms.web.security.authz.tags;

import javax.servlet.jsp.JspException;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.SecurityHelper;
import tavant.twms.security.authz.AuthorizationManager;

/**
 * @author prashanth.konda
 *
 */
@SuppressWarnings("serial")
public class IfAuthorizedTag extends javax.servlet.jsp.tagext.TagSupport {

	private String resource;

	private AuthorizationManager authorizationManager;

	public int doStartTag() throws JspException {
		
		if (StringUtils.hasText(resource)) {
			authorizationManager = (AuthorizationManager) getApplicationBean(
					"authorizationManager", AuthorizationManager.class);			
			try {
				if (authorizationManager.isPermissionGranted(getCurrentUser(),
						resource)) {
					return processResult(EVAL_BODY_INCLUDE);
				} else {
					return processResult(SKIP_BODY);
				}
			} catch (RuntimeException e) {
				return processResult(SKIP_BODY);
			}
		}
		return super.doStartTag();
	}

	private int processResult(int parentEvalResult) {
		pageContext.setAttribute(
				AbstractConditionAwareAuthorizeTag.EVAL_RESULT_PAGE_ATTRIBUTE,
				(parentEvalResult == EVAL_BODY_INCLUDE));
		return parentEvalResult;
	}

	protected Object getApplicationBean(String beanName, Class clazz) {
		ApplicationContext appCtx = WebApplicationContextUtils
				.getWebApplicationContext(pageContext.getServletContext());
		return appCtx.getBean(beanName, clazz);
	}

	protected User getCurrentUser() {
		SecurityHelper securityHelper = (SecurityHelper) getApplicationBean(
				"securityHelper", SecurityHelper.class);
		return securityHelper.getLoggedInUser();
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}



}