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
package tavant.twms.domain.reports;

import java.util.List;

/**
 * @author bibin.jacob
 * 
 */
public interface ReportService {
    /**
     * Find all claim given dealer, startDate,endDate
     * 
     * @param ReportSearchCriteria
     * @return List ReportVO
     */
    public List<ReportVO> findAllClaimsForDealersForCriteria(
            ReportSearchCriteria reportSearchCriteria);

    /**
     * Find all claim given dealerGroups, startDate,endDate
     * 
     * @param ReportSearchCriteria
     * @return List ReportVO
     */

    public List<ReportVO> findAllClaimsForDealerGroupsForCriteria(
            ReportSearchCriteria reportSearchCriteria);

    /**
     * Find all claims given dealer
     * 
     * @return List ReportVO
     */

    public List<ReportVO> findAllClaimsForProcessingEfficiency();

    /**
     * Find all partReturns given dealer,startDate,endDate
     * 
     * @return List ReportVO
     */

    public List<ReportVO> findPartReturnEfficiencyForDealers(
            ReportSearchCriteria reportSearchCriteria);

    /**
     * Find all partReturns given dealerGroups,startDate,endDate
     * 
     * @return List ReportVO
     */
    public List<ReportVO> findPartReturnEfficiencyForDealerGroups(
            ReportSearchCriteria reportSearchCriteria);

    /**
     * Find all due partReturns given dealers,startDate,endDate
     * 
     * @return List ReportVO
     */

    public List<ReportVO> findDuePartReturnsForDealers(ReportSearchCriteria reportSearchCriteria);

    /**
     * Find all due partReturns given dealerGroups,startDate,endDate
     * 
     * @return List ReportVO
     */

    public List<ReportVO> findDuePartReturnsForDealerGroups(
            ReportSearchCriteria reportSearchCriteria);

    /**
     * Find all supplier Recovery given suppliers,startDate,endDate
     * 
     * @return List ReportVO
     * 
     * TBD commented for the time being
     */

    // public List<ReportVO>
    // findSupplierRecoveryForSuppliers(ReportSearchCriteria
    // reportSearchCriteria);
    /**
     * Find all products given startDate,endDate
     * 
     * @return List ReportVO
     */

    public List<ReportVO> findClaimsByProduct(ReportSearchCriteria reportSearchCriteria);

    /**
     * Find all models given startDate,endDate
     * 
     * @return List ReportVO
     */
    public List<ReportVO> findClaimsByFault(ReportSearchCriteria reportSearchCriteria);

    /**
     * Find all models given current year or last year
     * 
     * @return List ReportVO
     */
    public List<ReportVO> findWarrantyPayout(ReportSearchCriteria reportSearchCriteria);
    
    public List<ReportVO> findSupplierRecoveryForSuppliers(ReportSearchCriteria reportSearchCriteria);

}
