package tavant.twms.integration.layer.authentication.dealerinterfaces;

import javax.xml.namespace.QName;

import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.jdom.Element;
import org.jdom.Namespace;

import tavant.twms.integration.layer.constants.DealerInterfaceErrorConstants;

/**
 * The class is for authenticating the user for dealer interface web services.
 * @author TWMSUSER
 */
public class DealerInterfaceAuthenticationHandler extends AbstractHandler {

	private static final Logger logger = Logger.getLogger("dealerAPILogger");

    private final String TOKEN_NS = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    
    private AuthenticationManager authenticationManager;
    
    private final static String SECURITY = "Security";
    
    private final static String USER_NAME_TOKEN = "UsernameToken";
    
    private final static String USER_NAME = "Username";
    
    private final static String PASSWORD = "Password";
    
    private String errorCode = null;
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * This method checks validates the soap header and authenticates the user login.
     * @see org.codehaus.xfire.handler.Handler#invoke(org.codehaus.xfire.MessageContext)
     */
    public void invoke(MessageContext context)  {
        SecurityContext securityContext = new SecurityContextImpl();
        try {
            Element header = context.getInMessage().getHeader();
            if (header == null) {
                setErrorCode(DealerInterfaceErrorConstants.DAPI05);
            } else {
                Namespace ns = Namespace.getNamespace(TOKEN_NS);
                Element securityElmt = header.getChild(SECURITY, ns);
                if(securityElmt == null) {
                    setErrorCode(DealerInterfaceErrorConstants.DAPI05);
                } else {
                    Element userNameTokenElmt = securityElmt.getChild(USER_NAME_TOKEN, ns);
                    if(userNameTokenElmt == null ) {
                        setErrorCode(DealerInterfaceErrorConstants.DAPI05);
                    } else {
                        Element userName = userNameTokenElmt.getChild(USER_NAME, ns);
                        Element userPassword = userNameTokenElmt.getChild(PASSWORD, ns);
                        if(userName == null || StringUtils.isEmpty(userName.getText())) {
                            setErrorCode(DealerInterfaceErrorConstants.DAPI07);
                        } else if(userPassword == null || StringUtils.isEmpty(userPassword.getText())) {
                            setErrorCode(DealerInterfaceErrorConstants.DAPI08);
                        } else {
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                    userName.getText(), userPassword.getText());
                            Authentication authentication = authenticationManager.authenticate(auth);
                            securityContext.setAuthentication(authentication);
                            SecurityContextHolder.setContext(securityContext);
                            setErrorCode(null);
                        }
                    }
                }
             }
        } catch (AuthenticationException  e) {
            logger.error("AuthenticationException from DealerAPI",e);
            setErrorCode(DealerInterfaceErrorConstants.DAPI06);
        } catch (Exception e) {
        	logger.error("Exception from DealerAPI AuthenticationHandler",e);
            setErrorCode(DealerInterfaceErrorConstants.DAPI02);
        }

    }
    
    public QName[] getUnderstoodHeaders() {
        return new QName[] {
                new QName(TOKEN_NS, SECURITY)
        };
    } 
}
