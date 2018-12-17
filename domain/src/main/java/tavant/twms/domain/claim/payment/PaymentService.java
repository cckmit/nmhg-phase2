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

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.domainlanguage.money.Money;

import tavant.twms.domain.claim.Claim;

/**
 * @author radhakrishnan.j
 */
@Transactional(readOnly = true)
public interface PaymentService {

    /**
     * Calculates the amount to be re-imbursed for a claim.
     * 
     * @param theClaim The claim for which the payments need to be calculated.
     * @return
     */
    public Payment calculatePaymentForClaim(Claim theClaim,Money deductible) throws PaymentCalculationException;

    /**
     * Fetches a CostCategory having the specified code.
     * 
     * @param categoryCode
     * @return null the CostCategory having the specified code; or null if no
     *         such CostCategory exists
     */
    public CostCategory findCostCategoryByCode(String categoryCode);

    /**
     * Returns the list of all CostCategory entities.
     * 
     * @return the list of all CostCategory entities; or an empty list if none
     *         exist.
     */
    public List<CostCategory> findAllCostCategories();

    public LineItemGroup computeSummationSectionForDisplay(Claim claim);
   
    public Payment calculatePaymentForDeniedClaim(Claim claim) throws PaymentCalculationException;
    
    public Payment calculatePaymentForWarrantyOrderClaim(Claim claim) throws PaymentCalculationException;

    @Transactional(readOnly = false)
    public void saveCostCategories(List<CostCategory> costCategories);
}
