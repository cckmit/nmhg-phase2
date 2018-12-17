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

package tavant.twms.integration.adapter.umo;

import org.mule.impl.RequestContext;
import org.openapplications.oagis.x9.ApplicationAreaType;
import tavant.oagis.BODDTO;
import tavant.oagis.ConfirmBODDTO;
import tavant.oagis.ConfirmBODDataAreaDTO;
import tavant.oagis.ConfirmBODDocumentDTO;
import tavant.twms.integration.adapter.SyncTracker;

import java.util.Calendar;
import java.util.List;

public class SyncWebServiceImpl implements SyncWebService {
    public String sync(List<SyncTracker> syncTrackers) {
        ConfirmBODDocumentDTO confirmBODDocumentDTO = ConfirmBODDocumentDTO.Factory.newInstance();
        createConfirmBOD(confirmBODDocumentDTO, syncTrackers);
        return confirmBODDocumentDTO.toString();
    }

    private void createConfirmBOD(ConfirmBODDocumentDTO confirmBODDocumentDTO, List<SyncTracker> syncTrackers) {
        ConfirmBODDTO confirmBODDTO = confirmBODDocumentDTO.addNewConfirmBOD();
        createApplicationArea(confirmBODDTO);
        createDataArea(confirmBODDTO, syncTrackers);
    }

    private void createApplicationArea(ConfirmBODDTO confirmBODDTO) {
        ApplicationAreaType applicationArea = confirmBODDTO.addNewApplicationArea();
        applicationArea.setCreationDateTime(Calendar.getInstance());
    }

    private void createDataArea(ConfirmBODDTO confirmBODDTO, List<SyncTracker> syncTrackers) {
        ConfirmBODDataAreaDTO dataArea = confirmBODDTO.addNewDataArea();
        createConfirm(dataArea);
        createBODs(syncTrackers, dataArea);
    }

    private void createConfirm(ConfirmBODDataAreaDTO dataArea) {
        dataArea
                .addNewConfirm()
                .addNewOriginalApplicationArea()
                .addNewSender()
                .addNewTaskID()
                .setStringValue(getBodType());
    }

    private void createBODs(List<SyncTracker> syncTrackers, ConfirmBODDataAreaDTO dataArea) {
        int i = 0;
        for (SyncTracker syncTracker : syncTrackers) {
            BODDTO bod = dataArea.addNewBOD();
            bod.setBusinessId("" + syncTracker.getBusinessId());
            if (i++ % 2 == 0) {
                bod.setSuccessful(true);
                bod.setException(null);
           } else {
                bod.setSuccessful(false);
                bod.setException("Fatal Error");
            }
        }
    }

    private String getBodType() {
        return (String) RequestContext
                .getEventContext()
                .getComponentDescriptor()
                .getProperties()
                .get("BodType");
    }
}
