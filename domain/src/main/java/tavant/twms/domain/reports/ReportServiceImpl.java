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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.ServiceProvider;

/**
 * @author bibin.jacob
 * 
 */
public class ReportServiceImpl implements ReportService {

    ClaimReportRepository claimReportRepository;

    DealerGroupService dealerGroupService;

    public List<ReportVO> findAllClaimsForDealersForCriteria(
            ReportSearchCriteria reportSearchCriteria) {
        populateException(reportSearchCriteria);
        List<Claim> claims = claimReportRepository.findAllClaimsForCriteria(reportSearchCriteria);
        List<ReportVO> reports = new ArrayList<ReportVO>();
        ReportVO reportVO = new ReportVO();
        reportVO.setDealerGroupName("");
        reportVO.setClaims(claims);
        reports.add(reportVO);
        return reports;
    }

    public List<ReportVO> findAllClaimsForDealerGroupsForCriteria(
            ReportSearchCriteria reportSearchCriteria) {
        populateException(reportSearchCriteria);
        List<ReportVO> reports = new ArrayList<ReportVO>();
        for (Long dealer : reportSearchCriteria.getSelectedDealerGroups()) {
            DealerGroup group = dealerGroupService.findById(dealer);
            Set<ServiceProvider> dealerGroups = group.getIncludedDealers();
            reportSearchCriteria.removeDealers();
            for (ServiceProvider dealership : dealerGroups) {
                reportSearchCriteria.addDealer(dealership.getId());
            }
            List<Claim> claims = claimReportRepository
                    .findAllClaimsForCriteria(reportSearchCriteria);
            ReportVO reportVO = new ReportVO();
            reportVO.setDealerGroupName(group.getName());
            reportVO.setClaims(claims);
            reports.add(reportVO);
        }
        return reports;
    }

    @SuppressWarnings("unchecked")
    public List<ReportVO> findAllClaimsForProcessingEfficiency() {
        List<ReportVO> claims = claimReportRepository.findClaimsForProcessingEfficiency();
        return claims;
    }

    @SuppressWarnings("unchecked")
    public List<ReportVO> findPartReturnEfficiencyForDealers(
            ReportSearchCriteria reportSearchCriteria) {
        populateException(reportSearchCriteria);
        List<SubReportVO> partReturns = claimReportRepository.findPartReturns(reportSearchCriteria);
        List<ReportVO> reports = new ArrayList<ReportVO>();
        ReportVO reportVO = new ReportVO();
        reportVO.setDealerGroupName("");
        reportVO.setSubReports(partReturns);
        reports.add(reportVO);
        return reports;
    }

    @SuppressWarnings("unchecked")
    public List<ReportVO> findPartReturnEfficiencyForDealerGroups(
            ReportSearchCriteria reportSearchCriteria) {
        populateException(reportSearchCriteria);
        List<ReportVO> reports = new ArrayList<ReportVO>();
        for (Long dealer : reportSearchCriteria.getSelectedDealerGroups()) {
            DealerGroup group = dealerGroupService.findById(dealer);
            Set<ServiceProvider> dealerGroups = group.getIncludedDealers();
            reportSearchCriteria.removeDealers();
            for (ServiceProvider dealership : dealerGroups) {
                reportSearchCriteria.addDealer(dealership.getId());
            }
            List<SubReportVO> partReturns = claimReportRepository
                    .findPartReturns(reportSearchCriteria);
            ReportVO reportVO = new ReportVO();
            reportVO.setDealerGroupName(group.getName());
            reportVO.setSubReports(partReturns);
            reports.add(reportVO);
        }
        return reports;
    }

    @SuppressWarnings("unchecked")
    public List<ReportVO> findDuePartReturnsForDealers(ReportSearchCriteria reportSearchCriteria) {
        List<SubReportVO> duePartReturns = claimReportRepository
                .findAllDuePartReturns(reportSearchCriteria);
        List<ReportVO> reports = new ArrayList<ReportVO>();
        ReportVO reportVO = new ReportVO();
        reportVO.setDealerGroupName("");
        reportVO.setSubReports(duePartReturns);
        reports.add(reportVO);
        return reports;
    }

    @SuppressWarnings("unchecked")
    public List<ReportVO> findDuePartReturnsForDealerGroups(
            ReportSearchCriteria reportSearchCriteria) {
        List<ReportVO> reports = new ArrayList<ReportVO>();
        for (Long dealer : reportSearchCriteria.getSelectedDealerGroups()) {
            DealerGroup group = dealerGroupService.findById(dealer);
            Set<ServiceProvider> dealerGroups = group.getIncludedDealers();
            reportSearchCriteria.removeDealers();
            for (ServiceProvider dealership : dealerGroups) {
                reportSearchCriteria.addDealer(dealership.getId());
            }
            List<SubReportVO> duePartReturns = claimReportRepository
                    .findAllDuePartReturns(reportSearchCriteria);
            ReportVO reportVO = new ReportVO();
            reportVO.setDealerGroupName(group.getName());
            reportVO.setSubReports(duePartReturns);
            reports.add(reportVO);
        }
        return reports;
    }

    
    @SuppressWarnings("unchecked")
    public List<ReportVO> findSupplierRecoveryForSuppliers(ReportSearchCriteria reportSearchCriteria){ 
    	populateException(reportSearchCriteria); 
        List<SubReportVO>
        supplierRecovery=claimReportRepository.findSupplierRecovery(reportSearchCriteria);
        List<ReportVO> reports=new ArrayList<ReportVO>();
        ReportVO reportVO=new
        ReportVO(); reportVO.setDealerGroupName("");
        reportVO.setSupplierRecovery(supplierRecovery);
        reports.add(reportVO);
        return reports; }
     

    @SuppressWarnings("unchecked")
    public List<ReportVO> findClaimsByProduct(ReportSearchCriteria reportSearchCriteria) {
        populateException(reportSearchCriteria);
        List<SubReportVO> claims = claimReportRepository.findClaimsByProduct(reportSearchCriteria);
        List<ReportVO> reports = new ArrayList<ReportVO>();
        ReportVO reportVO = new ReportVO();
        reportVO.setDealerGroupName("");
        reportVO.setClaims(claims);
        reports.add(reportVO);
        return reports;
    }

    @SuppressWarnings("unchecked")
    public List<ReportVO> findClaimsByFault(ReportSearchCriteria reportSearchCriteria) {
        populateException(reportSearchCriteria);
        List<SubReportVO> claims = claimReportRepository.findClaimsByFault(reportSearchCriteria);
        List<ReportVO> reports = new ArrayList<ReportVO>();
        ReportVO reportVO = new ReportVO();
        reportVO.setDealerGroupName("");
        reportVO.setClaims(claims);
        reportVO.setOrderBy(reportSearchCriteria.getOrderBy());
        reports.add(reportVO);
        return reports;
    }

    @SuppressWarnings("unchecked")
    public List<ReportVO> findWarrantyPayout(ReportSearchCriteria reportSearchCriteria) {
        List<SubReportVO> warrantyPayout = claimReportRepository
                .findWarrantyPayout(reportSearchCriteria);
        List<SubReportVO> taxAmount = claimReportRepository.findTaxAmount();
        Iterator taxIterator = taxAmount.listIterator();
        for (SubReportVO warrantyPayoutVO : warrantyPayout) {
            SubReportVO taxVO = (SubReportVO) taxIterator.next();
            warrantyPayoutVO.setTaxCurrentYear(taxVO.getTaxCurrentYear());
            warrantyPayoutVO.setTaxLastYear(taxVO.getTaxLastYear());
            warrantyPayoutVO.setTotalAmountCurrentYear(taxVO.getTotalAmountCurrentYear());
            warrantyPayoutVO.setTotalAmountLastYear(taxVO.getTotalAmountLastYear());
        }
        List<ReportVO> reports = new ArrayList<ReportVO>();
        ReportVO reportVO = new ReportVO();
        reportVO.setDealerGroupName("");
        reportVO.setYear(new GregorianCalendar().get(Calendar.YEAR));
        reportVO.setSubReports(warrantyPayout);
        reports.add(reportVO);
        return reports;
    }

    private void populateException(ReportSearchCriteria reportSearchCriteria) {
        if ((reportSearchCriteria == null) || (reportSearchCriteria.getStartDate() == null)
                || (reportSearchCriteria.getEndDate() == null)
                || (reportSearchCriteria.getStartDate().isAfter(reportSearchCriteria.getEndDate()))) {
            throw new IllegalArgumentException("Invalid ReportSearchCriteria["
                    + reportSearchCriteria + "]");
        }
    }

    public void setClaimReportRepository(ClaimReportRepository claimReportRepository) {
        this.claimReportRepository = claimReportRepository;
    }

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

}
