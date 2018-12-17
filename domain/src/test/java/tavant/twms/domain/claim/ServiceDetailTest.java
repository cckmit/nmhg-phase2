/*
 *   Copyright (c)2007 Tavant Technologies
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

import junit.framework.TestCase;

import com.domainlanguage.money.Money;

/**
 * @author kannan.ekanath
 * 
 */
public class ServiceDetailTest extends TestCase {

    public void testTotalCost() {
        ServiceDetail serviceDetail = new ServiceDetail();
        OEMPartReplaced part1 = new OEMPartReplaced();
        part1.setPricePerUnit(Money.dollars(10));
        part1.setNumberOfUnits(2);
        OEMPartReplaced part2 = new OEMPartReplaced();
        part2.setPricePerUnit(Money.dollars(5));
        part2.setNumberOfUnits(1);
        OEMPartReplaced part3 = new OEMPartReplaced();
        part3.setPricePerUnit(Money.dollars(4));
        part3.setNumberOfUnits(4);
        serviceDetail.addOEMPartReplaced(part1);
        serviceDetail.addOEMPartReplaced(part2);
        serviceDetail.addOEMPartReplaced(part3);

        assertEquals(Money.dollars(41), serviceDetail.getTotalCostOfParts());
    }

    public void testTotalCostEmptyParts() {
        ServiceDetail serviceDetail = new ServiceDetail();
        // for some reason it gives zero dollars :) USD is default currency
        assertEquals(Money.dollars(0), serviceDetail.getTotalCostOfParts());
    }

    public void testTotalCostOtherCurrencies() {
        ServiceDetail serviceDetail = new ServiceDetail();
        OEMPartReplaced part1 = new OEMPartReplaced();
        part1.setPricePerUnit(Money.dollars(10));
        part1.setNumberOfUnits(1);

        OEMPartReplaced part2 = new OEMPartReplaced();
        part2.setPricePerUnit(Money.euros(5));
        part2.setNumberOfUnits(1);

        serviceDetail.addOEMPartReplaced(part1);
        serviceDetail.addOEMPartReplaced(part2);

        try {
            serviceDetail.getTotalCostOfParts();
            fail("Wrong currency dint throw error");
        } catch (IllegalArgumentException e) {
            // fine
        }
    }

    /*
     * This Api is no longer of any use a sthe aapi to be tested is not required
     * for Club car
     */

    /*
     * public void testCostRatios() { Claim claim = new MachineClaim();
     * ServiceInformation serviceInformation = new ServiceInformation();
     * claim.setServiceInformation(serviceInformation); ServiceDetail
     * serviceDetail = new ServiceDetail();
     * serviceInformation.setServiceDetail(serviceDetail);
     * 
     * OEMPartReplaced part1 = new OEMPartReplaced();
     * part1.setSupplierPartReturn(new SupplierPartReturn());
     * part1.setPricePerUnit(Money.dollars(5)); part1.setNumberOfUnits(4);
     * 
     * OEMPartReplaced part2 = new OEMPartReplaced();
     * part2.setSupplierPartReturn(new SupplierPartReturn());
     * part2.setPricePerUnit(Money.dollars(3)); part2.setNumberOfUnits(1);
     * 
     * OEMPartReplaced part3 = new OEMPartReplaced();
     * part3.setPricePerUnit(Money.dollars(2)); part3.setNumberOfUnits(1);
     * 
     * OEMPartReplaced part4 = new OEMPartReplaced();
     * part4.setPricePerUnit(Money.dollars(4)); part4.setNumberOfUnits(1);
     * 
     * OEMPartReplaced part5 = new OEMPartReplaced();
     * part5.setSupplierPartReturn(new SupplierPartReturn());
     * part5.setPricePerUnit(Money.dollars(10)); part5.setNumberOfUnits(2);
     * 
     * serviceDetail.setOEMPartsReplaced(Arrays.asList(new OEMPartReplaced[] {
     * part1, part2, part3, part4, part5 }));
     * 
     * Map<OEMPartReplaced, Ratio> partRatios =
     * serviceDetail.getPartRatios(serviceDetail .getOEMPartsReplaced(), null);
     * assertEquals(3, partRatios.size());
     * 
     * Money total = serviceDetail.getTotalCostOfParts();
     * assertEquals(Money.dollars(49), total);
     * 
     * Ratio ratio1 = part1.cost().dividedBy(total); Ratio ratio2 =
     * part2.cost().dividedBy(total); Ratio ratio5 =
     * part5.cost().dividedBy(total);
     * 
     * assertEquals(ratio1, partRatios.get(part1)); assertEquals(ratio2,
     * partRatios.get(part2)); assertEquals(ratio5, partRatios.get(part5)); }
     */
}
