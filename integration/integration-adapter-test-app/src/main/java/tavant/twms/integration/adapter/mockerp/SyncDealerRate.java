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

package tavant.twms.integration.adapter.mockerp;

import org.openapplications.oagis.x9.ApplicationAreaType;
import tavant.oagis.CriteriaDTO;
import tavant.oagis.DealerRateDTO;
import tavant.oagis.DurationDTO;
import tavant.oagis.LaborAndTravelPriceDTO;
import tavant.oagis.SyncDealerRateDTO;
import tavant.oagis.SyncDealerRateDataAreaDTO;
import tavant.oagis.SyncDealerRateDocumentDTO;
import tavant.twms.integration.adapter.SyncTracker;

import java.util.*;

public class SyncDealerRate {

    private GenericDao genericDao;

    public String sync(List<SyncTracker> syncTrackers) {
        List<DealerRate> dealerRates = new ArrayList<DealerRate>();
        for (SyncTracker syncTracker : syncTrackers) {
            dealerRates.add((DealerRate) genericDao.findById(DealerRate.class, Long.valueOf(syncTracker.getBusinessId())));
        }
        return transform(dealerRates);
    }

    private String transform(List<DealerRate> dealerRates) {
        SyncDealerRateDocumentDTO getDealerRateDocumentDTO = SyncDealerRateDocumentDTO.Factory.newInstance();
        createSyncDealerRate(getDealerRateDocumentDTO, dealerRates);
        return getDealerRateDocumentDTO.toString();
    }

    private void createSyncDealerRate(SyncDealerRateDocumentDTO getDealerRateDocumentDTO, List<DealerRate> dealerRates) {
        SyncDealerRateDTO getDealerRateDTO = getDealerRateDocumentDTO.addNewSyncDealerRate();
        createApplicationArea(getDealerRateDTO);
        createDataArea(getDealerRateDTO, dealerRates);
    }

    private void createApplicationArea(SyncDealerRateDTO getDealerRateDTO) {
        ApplicationAreaType applicationArea = getDealerRateDTO.addNewApplicationArea();
        applicationArea.setCreationDateTime(Calendar.getInstance());
    }

    private void createDataArea(SyncDealerRateDTO getDealerRateDTO, List<DealerRate> dealerRates) {
        SyncDealerRateDataAreaDTO dataArea = getDealerRateDTO.addNewDataArea();
        createSync(dataArea);
        createItems(dealerRates, dataArea);
    }

    private void createSync(SyncDealerRateDataAreaDTO dataArea) {
        dataArea.addNewSync();
    }

    private void createItems(List<DealerRate> dealerRates, SyncDealerRateDataAreaDTO dataArea) {
        for (DealerRate dealerRate : dealerRates) {
            DealerRateDTO dealerRateDTO = dataArea.addNewDealerRate();
            dealerRateDTO.setBusinessId(""+dealerRate.getId());

            CriteriaDTO criteriaDTO = dealerRateDTO.addNewForCriteria();
            criteriaDTO.setClaimType(dealerRate.getClaimType());
            criteriaDTO.setDealerNumber(dealerRate.getDealerNumber());
            criteriaDTO.setProductType(dealerRate.getProductType());
            criteriaDTO.setWarrantyType(dealerRate.getWarrantyType());

            DurationDTO durationDTO = dealerRateDTO.addNewForDuration();
            durationDTO.setFrom(cal(dealerRate.getFromDate()));
            durationDTO.setTo(cal(dealerRate.getToDate()));

            LaborAndTravelPriceDTO laborAndTravelPriceDTO = dealerRateDTO.addNewLaborAndTravelPrice();
            laborAndTravelPriceDTO.setLaborAmount(dealerRate.getLaborAmount());
            laborAndTravelPriceDTO.setPerHourAmount(dealerRate.getPerHourAmount());
            laborAndTravelPriceDTO.setPerKmAmount(dealerRate.getPerKmAmount());
            laborAndTravelPriceDTO.setPerMileAmount(dealerRate.getPerMileAmount());
            laborAndTravelPriceDTO.setPerTripAmount(dealerRate.getPerTripAmount());
            laborAndTravelPriceDTO.setCurrency(dealerRate.getCurrency());
        }
    }

    private Calendar cal(Date date) {
        Calendar calendar = null;
        if (date != null) {
            calendar = new GregorianCalendar(TimeZone.getDefault());
            calendar.setTime(date);
        }
        return calendar;
    }

    public void setGenericDao(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

}
