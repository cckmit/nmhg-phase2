/*
 *   Copyright (c) 2007 Tavant Technologies
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

/**
 * @author pradipta.a
 */
public enum RecoveryClaimState {

    NEW("New"),
    ON_HOLD("On Hold"),
    REVIEW_ON_HOLD("Review On Hold"),
    ON_HOLD_FOR_PART_RETURN("On Hold For Part Return"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    IN_RECOVERY("In Recovery"),
    AWAITING_SUPPLIER_RESPONSE("Awaiting Supplier Response"),
    CLOSED_RECOVERED("Closed Recovered"),
    SHIPMENT_FROM_WAREHOUSE("Shipment From WareHouse"),
    NOT_FOR_RECOVERY("Not For Recovery"),
    NOT_FOR_RECOVERY_DISPUTED("Disputed"),
    NOT_FOR_RECOVERY_REQUESTED("Requested"),
    READY_FOR_DEBIT("Ready for Debit"),
    NO_RESPONSE_AND_AUTO_DEBITTED("No Response Auto Debited"),
    DISPUTED_AND_AUTO_DEBITTED("Disputed and Auto Debited"),
    WAIT_FOR_DEBIT("Wait for Debit"),
    WAIT_FOR_NO_RESPONSE_AUTO_DEBIT("Wait Auto Debit For No Response"),
    WAIT_FOR_DISPUTED_AUTO_DEBIT("Wait Auto Debit for Disputed"),
    CLOSED_UNRECOVERED("Closed Unrecovered"),
    AUTO_CLOSED("Auto Closed"),
    NO_AMOUNT_TO_RECOVER_CLOSED("No Amount To Recover Closed"),
    NO_RESPONSE_AND_AUTO_DEBITTED_CLOSED("No Response Auto Debited and Closed"),
    DISPUTED_AND_AUTO_DEBITTED_CLOSED("Disputed Auto Debited and Closed"),
    DEBITTED_AND_CLOSED("Debited and Closed"),
    REOPENED("Reopened"),
    REOPENED_ON_HOLD("Reopen On Hold"),
    WNTY_CLAIM_REOPENED("Wnty Claim Reopened"),
    TRANSFERRED("Transferred"),
    AUTO_DISPUTED_INITIAL_RESPONSE("Auto Disputed Initial Response Period"),
    AUTO_DISPUTED_FINAL_RESPONSE("Auto Disputed Final Response Period"),
    CLOSED_FOR_SECOND_RECOVERY("Closed and flagged for another recovery");
    

    private String state;

    private RecoveryClaimState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }
    
    
    
}
