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
package tavant.twms.domain.claim.payment;

import java.util.Currency;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import tavant.twms.domain.catalog.ItemUOMTypes;
import tavant.twms.domain.catalog.MiscItemRate;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.ServiceDetail;
import tavant.twms.domain.claim.ServiceInformation;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.uom.UomMappings;
import tavant.twms.domain.uom.UomMappingsService;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;

public class MiscPartsPaymentComputer extends AbstractPaymentComponentComputer {
	
	private UomMappingsService uomMappingsService;

	private static Logger logger = LogManager.getLogger(MiscPartsPaymentComputer.class);
	
	 public Money computeBaseAmount(PaymentContext ctx) {
	    Claim claim=ctx.getClaim();
        ServiceInformation serviceInformation = claim.getServiceInformation();
        ServiceDetail serviceDetail = serviceInformation.getServiceDetail();
        final List<NonOEMPartReplaced> partsReplaced = serviceDetail.getMiscPartsReplaced();
        Currency baseCurrency = GlobalConfiguration.getInstance().getBaseCurrency();
        Money baseAmt = Money.valueOf(0.0D,baseCurrency);
        if (partsReplaced != null && partsReplaced.size() > 0) {
            for (NonOEMPartReplaced miscPartReplaced : partsReplaced) {
                try {                	
                	MiscItemRate miscItemRate = miscPartReplaced.getMiscItemConfig().getMiscItemRateForCurrency
                						(claim.getCurrencyForCalculation());
                	Money costPrice = miscItemRate.getRate();                	
                	String baseUomString = miscPartReplaced.getMiscItemConfig().getUom().getName();
                    if(!ItemUOMTypes.EACH.getType().equalsIgnoreCase(baseUomString)){
                    	String businessUnitInfo = SelectedBusinessUnitsHolder.getSelectedBusinessUnit();
                    	SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
                    	UomMappings uomMapping = uomMappingsService.findUomMappingForBaseUom(baseUomString);
                        if(uomMapping != null ){
                        	costPrice = costPrice.dividedBy(uomMapping.getMappingFraction());                        	
                        }
                        if(businessUnitInfo == null)
                        	SelectedBusinessUnitsHolder.clearChosenBusinessUnitFilter();
                    }
                    miscPartReplaced.setPricePerUnit(costPrice);
                    Money miscItemCost = costPrice.times(miscPartReplaced.getNumberOfUnits());
                    baseAmt = baseAmt.plus(miscItemCost);
                } catch (Exception e) {
                	logger.error("Error from MiscPartsPaymentComputer " + e);
                }
            }
        }
        return baseAmt;
    }

	public void setUomMappingsService(UomMappingsService uomMappingsService) {
		this.uomMappingsService = uomMappingsService;
	}
}
