package tavant.twms.web.claim;

import org.json.JSONArray;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 6/6/13
 * Time: 10:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReprocessClaimAction extends I18nActionSupport {

    private String jsonString;

    private String claimId;

    public ClaimService claimService;

    public ClaimService getClaimService() {
        return claimService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public String getClaimId() {
        return claimId;
    }

    public void setClaimId(String claimId) {
        this.claimId = claimId;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getClaimSyncTrackerState(){
        ClaimState state = getClaimService().findClaim(new Long(getClaimId())).getState();
        JSONArray oneEntry = new JSONArray();
        oneEntry.put(String.valueOf(state.equals(ClaimState.PENDING_PAYMENT_RESPONSE)));
        this.jsonString = oneEntry.toString();
        return SUCCESS;
    }

}
