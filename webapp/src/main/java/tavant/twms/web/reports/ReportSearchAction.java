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
package tavant.twms.web.reports;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.util.StringUtils;

import tavant.twms.domain.orgmodel.DealerGroup;
import tavant.twms.domain.orgmodel.DealerGroupService;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.orgmodel.UserRepository;
import tavant.twms.domain.reports.ReportSearchCriteria;
import tavant.twms.domain.reports.ReportService;
import tavant.twms.domain.reports.ReportVO;
import tavant.twms.web.i18n.I18nActionSupport;

/**
 * @author bibin.jacob
 * 
 */
public class ReportSearchAction extends I18nActionSupport {
    private ReportSearchCriteria reportSearchCriteria;

    private String action;

    private String reportType;

    private List<ReportVO> listOfClaim;

    private String documentTitle;

    private boolean showReport;

    private ReportService reportService;

    private DealershipRepository dealershipRepository;

    private UserRepository userRepository;

    private DealerGroupService dealerGroupService;

    private String searchResultJSON;

    private String existingDealers;

    private String existingSuppliers;

    private String existingDealerGroups;

    private String dealerName;

    private String supplierName;

    private String dealerGroupName;

    private String optionSelected;

    private String taskName;

    private Map<String, String> reportTypes = new HashMap<String, String>();

    private Map<String, String> claimReports = new HashMap<String, String>();

    private Map<String, String> partReturnReports = new HashMap<String, String>();

    private Map<String, String> supplierRecoveryReports = new HashMap<String, String>();

    private Map<String, String> claimsByFaultGroupTypes = new HashMap<String, String>();

    private Map<String, String> warrantyPayOutGroupTypes = new HashMap<String, String>();

    // TODO:Replaced hardcode values in jsp to support internationalization
    // TODO:Move methods specific to claim,partreturns,supplier recovery to
    // seperate action classes.
    public ReportSearchAction() {
        super();
        reportTypes.put("PDF", getText("pdf"));
        reportTypes.put("XLS", getText("xls"));
        reportTypes.put("HTML", getText("html"));
        claimReports.put("claimTypeByDealer", getText("claimTypeByDealer"));
        claimReports.put("claimStatusByDealer", getText("claimStatusByDealer"));
        claimReports.put("processingEngineEfficiency", getText("processingEngineEfficiency"));
        claimReports.put("claimsByFault", getText("claimsByFault"));
        claimReports.put("warrantyPayout", getText("warrantyPayout"));
        claimReports.put("claimsByProduct", getText("claimsByProduct"));
        partReturnReports.put("duePartReturnsByDealer", getText("duePartReturnsByDealer"));
        partReturnReports.put("partReturnEfficiencyByDealer", getText("partReturnEfficiencyByDealer"));
        supplierRecoveryReports.put("supplierRecovery", getText("supplierRecovery"));
        claimsByFaultGroupTypes.put("modelFault", getText("modelFault"));
        claimsByFaultGroupTypes.put("faultModel", getText("faultModel"));
        warrantyPayOutGroupTypes.put("month", getText("month"));
        warrantyPayOutGroupTypes.put("quarter", getText("quarter"));
    }

    public String dealers() {
        Collection<ServiceProvider> dealers = dealershipRepository.findAllDealers(dealerName);
        Map<String, String> dealerNames;
        JSONArray jsonArray = new JSONArray();
        for (ServiceProvider dealer : dealers) {
            dealerNames = new HashMap<String, String>();
            dealerNames.put("id", dealer.getId().toString());
            dealerNames.put("name", dealer.getName());
            jsonArray.put(dealerNames);
        }
        searchResultJSON = jsonArray.toString();
        return SUCCESS;
    }

    public String dealerGroups() {
        Collection<DealerGroup> dealerGroups = dealerGroupService.findGroupsForOrganisationHierarchy(dealerGroupName);
        Map<String, String> dealerGroupNames;
        JSONArray jsonArray = new JSONArray();
        for (DealerGroup dealerGroup : dealerGroups) {
            dealerGroupNames = new HashMap<String, String>();
            dealerGroupNames.put("id", dealerGroup.getId().toString());
            dealerGroupNames.put("name", dealerGroup.getName());
            jsonArray.put(dealerGroupNames);
        }
        searchResultJSON = jsonArray.toString();
        return SUCCESS;
    }

    public String suppliers() {
        Collection<Supplier> suppliers = userRepository.findAllSuppliers(supplierName);
        Map<String, String> suppliersNames;
        JSONArray jsonArray = new JSONArray();
        for (Supplier supplier : suppliers) {
            suppliersNames = new HashMap<String, String>();
            suppliersNames.put("id", supplier.getId().toString());
            suppliersNames.put("name", supplier.getName());
            jsonArray.put(suppliersNames);
        }
        searchResultJSON = jsonArray.toString();
        return SUCCESS;
    }

    public String claimReports() {
        return SUCCESS;
    }

    public String partReturnsReports() {
        return SUCCESS;
    }

    public String supplierRecoveryReports() {
        return SUCCESS;
    }

    // TODO:to be removed
    public String setup() {
        showReport = true;
        return SUCCESS;
    }

    @SuppressWarnings("unchecked")
    public String generateClaimReport() throws Exception {
        if (reportSearchCriteria.getDealers().length() > 0) {
            populateDealers(reportSearchCriteria);
            listOfClaim = reportService.findAllClaimsForDealersForCriteria(reportSearchCriteria);
        } else {
            populateDealerGroups(reportSearchCriteria);
            listOfClaim = reportService.findAllClaimsForDealerGroupsForCriteria(reportSearchCriteria);
        }
        if (null != listOfClaim && null != listOfClaim.get(0).getClaims() && listOfClaim.get(0).getClaims().size() > 0) {
            clearErrorsAndMessages();
            return SUCCESS;
        }
        addActionMessage("error.report.search");
        return INPUT;
    }

    @SuppressWarnings("unchecked")
    public String generateProcessingEfficiencyReport() throws Exception {
        listOfClaim = reportService.findAllClaimsForProcessingEfficiency();
        if (null != listOfClaim) {
            clearErrorsAndMessages();
            return SUCCESS;
        }
        addActionMessage("error.report.search");
        return INPUT;
    }

    @SuppressWarnings("unchecked")
    public String generatePartReturnEfficiencyReport() throws Exception {
        if (reportSearchCriteria.getDealers().length() > 0) {
            populateDealers(reportSearchCriteria);
            listOfClaim = reportService.findPartReturnEfficiencyForDealers(reportSearchCriteria);
        } else {
            populateDealerGroups(reportSearchCriteria);
            listOfClaim = reportService.findPartReturnEfficiencyForDealerGroups(reportSearchCriteria);
        }
        if (null != listOfClaim && null != listOfClaim.get(0).getSubReports()
                && listOfClaim.get(0).getSubReports().size() > 0) {
            clearErrorsAndMessages();
            return SUCCESS;
        }
        addActionMessage("error.report.search");
        return INPUT;
    }

    @SuppressWarnings("unchecked")
    public String generateDuePartReturnsReport() throws Exception {
        if (reportSearchCriteria.getDealers().length() > 0) {
            populateDealers(reportSearchCriteria);
            listOfClaim = reportService.findDuePartReturnsForDealers(reportSearchCriteria);
        } else {
            populateDealerGroups(reportSearchCriteria);
            listOfClaim = reportService.findDuePartReturnsForDealerGroups(reportSearchCriteria);
        }
        if (null != listOfClaim && null != listOfClaim.get(0).getSubReports()
                && listOfClaim.get(0).getSubReports().size() > 0) {
            clearErrorsAndMessages();
            return SUCCESS;
        }
        addActionMessage("error.report.search");
        return INPUT;
    }

    
     @SuppressWarnings("unchecked")
     public String generateSupplierRecoveryReport() throws Exception {
        populateSuppliers(reportSearchCriteria); 
        listOfClaim = reportService.findSupplierRecoveryForSuppliers(reportSearchCriteria); 
        if(null != listOfClaim && null!=listOfClaim.get(0).getSupplierRecovery() & listOfClaim.get(0).getSupplierRecovery().size()>0) {
             clearErrorsAndMessages(); 
             return SUCCESS; 
          }
        addActionMessage("error.report.search");
        return INPUT; 
     }
     

    @SuppressWarnings("unchecked")
    public String generateClaimsByFaultReport() throws Exception {
        listOfClaim = reportService.findClaimsByFault(reportSearchCriteria);
        if (null != listOfClaim && null != listOfClaim.get(0).getClaims() && listOfClaim.get(0).getClaims().size() > 0) {
            clearErrorsAndMessages();
            return SUCCESS;
        }
        addActionMessage("error.report.search");
        return INPUT;
    }

    @SuppressWarnings("unchecked")
    public String generateClaimsByProductReport() throws Exception {
        listOfClaim = reportService.findClaimsByProduct(reportSearchCriteria);
        if (null != listOfClaim && null != listOfClaim.get(0).getClaims() && listOfClaim.get(0).getClaims().size() > 0) {
            clearErrorsAndMessages();
            return SUCCESS;
        }
        addActionMessage("error.report.search");
        return INPUT;
    }

    @SuppressWarnings("unchecked")
    public String generateWarrantyPayoutReport() throws Exception {
        listOfClaim = reportService.findWarrantyPayout(reportSearchCriteria);
        if (null != listOfClaim && null != listOfClaim.get(0).getSubReports()
                && listOfClaim.get(0).getSubReports().size() > 0) {
            clearErrorsAndMessages();
            return SUCCESS;
        }
        addActionMessage("error.report.search");
        return INPUT;
    }

    @SuppressWarnings("unchecked")
    private void populateDealers(ReportSearchCriteria reportSearchcriteria) {
        Collection<String> dealers = StringUtils.commaDelimitedListToSet(reportSearchCriteria.getDealers());
        for (String dealer : dealers) {
            reportSearchCriteria.addDealer(Long.parseLong(dealer));
        }
    }

    @SuppressWarnings("unchecked")
    private void populateSuppliers(ReportSearchCriteria reportSearchcriteria) {
        Collection<String> suppliers = StringUtils.commaDelimitedListToSet(reportSearchCriteria.getSuppliers());
        for (String supplier : suppliers) {
            reportSearchCriteria.addSupplier(Long.parseLong(supplier));
        }
    }

    @SuppressWarnings("unchecked")
    private void populateDealerGroups(ReportSearchCriteria reportSearchcriteria) {
        Collection<String> dealerGroups = StringUtils.commaDelimitedListToSet(reportSearchCriteria.getDealerGroups());
        for (String dealerGroup : dealerGroups) {
            reportSearchCriteria.addDealerGroup(Long.parseLong(dealerGroup));
        }
    }

    public String getExistingDealerGroups() {
        return existingDealerGroups;
    }

    public void setExistingDealerGroups(String existingDealerGroups) {
        this.existingDealerGroups = existingDealerGroups;
    }

    public String getExistingDealers() {
        return existingDealers;
    }

    public List<ReportVO> getListOfClaim() {
        return listOfClaim;
    }

    public void setExistingDealers(String existingDealers) {
        this.existingDealers = existingDealers;
    }

    public String getOptionSelected() {
        return optionSelected;
    }

    public void setOptionSelected(String optionSelected) {
        this.optionSelected = optionSelected;
    }

    public String getDealerGroupName() {
        return dealerGroupName;
    }

    public String getDealerName() {
        return dealerName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public String getReportType() {
        return reportType;
    }

    public boolean isShowReport() {
        return showReport;
    }

    public void setDealerGroupName(String dealerGroupName) {
        this.dealerGroupName = dealerGroupName;
    }

    public void setDealerGroupService(DealerGroupService dealerGroupService) {
        this.dealerGroupService = dealerGroupService;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public void setDealershipRepository(DealershipRepository dealershipRepository) {
        this.dealershipRepository = dealershipRepository;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getSearchResultJSON() {
        return searchResultJSON;
    }

    public void setReportSearchCriteria(ReportSearchCriteria reportSearchCriteria) {
        this.reportSearchCriteria = reportSearchCriteria;
    }

    public ReportSearchCriteria getReportSearchCriteria() {
        return reportSearchCriteria;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getExistingSuppliers() {
        return existingSuppliers;
    }

    public void setExistingSuppliers(String existingSuppliers) {
        this.existingSuppliers = existingSuppliers;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Map<String, String> getReportTypes() {
        return reportTypes;
    }

    public Map<String, String> getClaimReports() {
        return claimReports;
    }

    public Map<String, String> getClaimsByFaultGroupTypes() {
        return claimsByFaultGroupTypes;
    }

    public Map<String, String> getPartReturnReports() {
        return partReturnReports;
    }

    public Map<String, String> getSupplierRecoveryReports() {
        return supplierRecoveryReports;
    }

    public Map<String, String> getWarrantyPayOutGroupTypes() {
        return warrantyPayOutGroupTypes;
    }
}
