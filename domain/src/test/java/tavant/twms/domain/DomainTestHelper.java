package tavant.twms.domain;

import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.Claim;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 20 Sep, 2007
 * Time: 6:54:46 PM
 */
public class DomainTestHelper {

    public static ClaimedItem getOrCreateFirstClaimedItemFromClaim(Claim claim) {
        List<ClaimedItem> claimedItems = claim.getClaimedItems();

        if(claimedItems.isEmpty()) {
            claim.addClaimedItem(new ClaimedItem());
        }

        return claimedItems.get(0);
    }
}
