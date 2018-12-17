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
package tavant.twms.domain.partreturn;

import org.springframework.core.style.ToStringCreator;

/**
 * @author pradipta.a
 */
public enum PartReturnTaskTriggerStatus {

    // before start of Part return process
    TO_BE_TRIGGERED("toBeTriggered"),

    WPRA_GENERATED("wpraGenerated"),
    // after part return process started
    TRIGGERED("triggered"),
    // before shipment generation
    TO_GENERATE_SHIPMENT("toGenerateShipment"),
    // after shipment is generated
    SHIPMENT_GENERATED("shipmentGenerated"),

    CEVA_TRACKING_INFO_UPDATE("CEVATrackingInfoUpdate"),
    //Dealer Requested Parts
    DEALER_REQUEST_TRIGGERED("dealerRequestedTriggered"),

    TO_GENERATE_SHIPMENT_FOR_DEALER("toGenerateShipment"),

    SHIPMENT_GENERATED_FOR_DEALER("shipmentGeneratedForDealer"),

    // after the process has been ended
    TO_BE_ENDED("toBeEnded"),
    // after part return process has ended
    ENDED("ended");

    private String status;

    private PartReturnTaskTriggerStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("status", this.status).toString();
    }
}
