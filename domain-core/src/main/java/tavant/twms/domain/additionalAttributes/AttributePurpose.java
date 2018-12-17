/*
 *   Copyright (c) 2008 Tavant Technologies
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
package tavant.twms.domain.additionalAttributes;

/**
 * @author pradipta.a
 */
public enum AttributePurpose {

    PART_SOURCING_PURPOSE("Part Source Purpose"),
    JOB_CODE_PURPOSE("Job Code Purpose"),
    CLAIMED_INVENTORY_PURPOSE("Claimed Inventory Purpose"),
    CLAIM_PURPOSE("Claim Purpose"),
    EQUIPMENT_HISTORY("Equipment_History"),
    CLAIM("Claim"),
    SERVICE_REQUEST("Service Request"),
    QUOTE("Quote"),
    INVENTORY_ADDITIONAL_INFO("Inventory Additional Info");
    

    private AttributePurpose(String purpose) {
        this.purpose = purpose;
    }

    private String purpose;

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

}
