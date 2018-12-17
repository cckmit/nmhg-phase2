/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.claim;

import com.domainlanguage.time.CalendarDate;
import tavant.twms.domain.inventory.InventoryItem;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Entity
@DiscriminatorValue("ATTACHMENT")
public class AttachmentClaim extends Claim {

    public AttachmentClaim() {
        super();
    }

    public AttachmentClaim(InventoryItem forItem, CalendarDate failureDate, CalendarDate repairDate, CalendarDate repairStartDate) {
        super(forItem, failureDate, repairDate, repairStartDate);
    }

    @Override
    public ClaimType getType() {
        return ClaimType.ATTACHMENT;
    }

    public boolean canPolicyBeComputedForClaimedItem(ClaimedItem claimedItem) {
        return claimedItem.getHoursInService() != null;
    }    
}
