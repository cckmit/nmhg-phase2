package tavant.twms.integration.layer.authentication;

import org.codehaus.xfire.MessageContext;
import org.codehaus.xfire.handler.AbstractHandler;
import org.jdom.Element;
import org.jdom.Namespace;

public class ClientAuthHandler extends AbstractHandler {

	private String username = null;
	private String password = null;
	
	public ClientAuthHandler() {
	}
	
	public ClientAuthHandler(String username,String password) {
		this.username = username;
		this.password = password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void invoke(MessageContext context) throws Exception {
		Element el = context.getOutMessage().getHeader();
		final Namespace ns = Namespace.getNamespace("http://parts.layer.integration.twms.tavant");  
        el.addNamespaceDeclaration(ns);
        
        Element auth = new Element("AuthenticationToken", ns);
        Element username_el = new Element("Username",ns);
        username_el.addContent(username);
        Element password_el = new Element("Password",ns);
        password_el.addContent(password);
        auth.addContent(username_el);
        auth.addContent(password_el);
        el.addContent(auth);
	}
}
