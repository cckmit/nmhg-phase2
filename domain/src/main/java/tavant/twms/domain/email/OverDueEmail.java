package tavant.twms.domain.email;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.partreturn.PartReturn;

/**
 * Created by deepak.patel on 20/6/14.
 */
public interface OverDueEmail {

    public void createEmailEventForOverdue(Claim claim, PartReturn partReturn);
}
