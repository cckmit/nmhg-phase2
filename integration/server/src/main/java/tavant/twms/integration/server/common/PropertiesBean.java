/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tavant.twms.integration.server.common;

/**
 *
 * @author prasad.r
 */
public class PropertiesBean {
    
    private String integrationURL;

	private String userName;
    
    private String password;

    public String getIntegrationURL() {
        return integrationURL;
    }

    public void setIntegrationURL(String integrationURL) {
        this.integrationURL = integrationURL;
    }
    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    
    
}
