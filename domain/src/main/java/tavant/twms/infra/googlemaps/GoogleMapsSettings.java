/**
 * 
 */
package tavant.twms.infra.googlemaps;

/**
 * @author mritunjay.kumar
 *
 */
public class GoogleMapsSettings {
	private String apiKey;
	public String clientId;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
    
    
}
