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
package tavant.twms.web.admin.lov;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.LovRepository;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.web.inbox.SummaryTableColumnOptions;

import com.opensymphony.xwork2.Preparable;

/**
 * @author aniruddha.chaturvedi
 *
 */
@SuppressWarnings("serial")
public class ListLovTypes extends SummaryTableAction implements Preparable {
	protected static Logger logger = LogManager.getLogger(ListLovTypes.class);
    private List<LovType> theList;
    private LovRepository lovRepository;
    private List<ListOfValues> lovs;
    
    public void setLovRepository(LovRepository lovRepository) {
		this.lovRepository = lovRepository;
	}

    @Override
    public PageResult<?> getBody() {
    	PageSpecification pageSpecification = new PageSpecification();
        pageSpecification.setPageNumber(getPage()-1);
        pageSpecification.setPageSize(pageSize);
     // Bug #100 - Number of records on LoV page is displayed as 0.
	//	Prior to this fix, total no of records was not being set to PageSpecification.
        pageSpecification.setTotalRecords(theList.size());
        List<LovType> fetchedList = getPageFromTheList(pageSpecification);
        int pages = getNumberOfPagesFromTheList(pageSpecification);
        pages = pages < 0 ? 1: pages;
        return new PageResult(fetchedList, pageSpecification, pages);
    }

	public String showLovType() {
		lovs = lovRepository.findAll(StringUtils.capitalize(getId()));
    	return SUCCESS;
    }
    
	public void prepare() throws Exception {
		initialiseLovTypes();
	}

	@Override
	public List<SummaryTableColumn> getHeader() {
        List<SummaryTableColumn> tableHeadData = new ArrayList<SummaryTableColumn>();
        tableHeadData.add(new SummaryTableColumn("columnTitle.listOfValues.lovName",
        		"name", 100, "string", false, true, true, false));        
        tableHeadData.add(new SummaryTableColumn("columnTitle.listOfValues.lovCategory",
        		"displayName", 100, "string", true, false, false, false, SummaryTableColumnOptions.NO_SORT_NO_FILTER_COL));
        return tableHeadData;
    }
    
    private List<LovType> initialiseLovTypes() {
		theList = new ArrayList<LovType>();
		theList.add(new LovType("smrReason", getText("label.viewClaim.smrReason")));
		theList.add(new LovType("failureReason",getText( "label.partReturn.failureReason")));
		theList.add(new LovType("rejectionReason", getText("label.supplier.rejectionReason")));
		theList.add(new LovType("acceptanceReason", getText("label.supplier.acceptanceReason")));
		theList.add(new LovType("acceptanceReasonForCP", getText("label.supplier.acceptanceReasonForCP")));
		theList.add(new LovType("accountabilityCode", getText("label.claim.accountabilityCode")));
		theList.add(new LovType("partAcceptanceReason", getText("label.partReturnClaim.partAcceptanceReason")));
		theList.add(new LovType("recoveryClaimAcceptanceReason", getText("label.claim.recoveryClaimAcceptanceReason")));
		theList.add(new LovType("recoveryClaimRejectionReason", getText("label.claim.recoveryClaimRejectionReason")));
		theList.add(new LovType("manufacturingSiteInventory", getText("label.inventory.manufacturingSite")));
		theList.add(new LovType("campaignClass",getText("label.campaign.classCode")));
        theList.add(new LovType("sellingEntity",getText("label.lov.sellingEntity")));
        theList.add(new LovType("reportType",getText("label.lov.reportType")));
        theList.add(new LovType("oem",getText("label.lov.oems")));
        theList.add(new LovType("claimCompetitorModel",getText("label.lov.claimCompetitorModel")));
        theList.add(new LovType("fieldModificationInventoryStatus",getText("label.inventory.fieldModificationInventoryStatus")));
        theList.add(new LovType("documentType",getText("label.lov.attachmentType")));
        theList.add(new LovType("suppliers",getText("label.lov.supplier")));
        theList.add(new LovType("recoveryClaimCannotRecoverReason", getText("label.supplier.recoveryClaimCannotRecoverReason")));
        theList.add(new LovType("unitDocumentType", getText("label.lov.unitAttachmentType")));
        theList.add(new LovType("laborRateType", getText("label.lov.laborRateType")));
        theList.add(new LovType("additionalComponentType", getText("label.additionalComponentInstalled.type")));
        theList.add(new LovType("additionalComponentSubType", getText("label.additionalComponentInstalled.subType")));
        theList.add(new LovType("putOnHoldReason", getText("label.supplier.putOnHoldReason")));
        theList.add(new LovType("requestInfoFromUser", getText("label.supplier.reqInfoFromDealer")));
        theList.add(new LovType("recoveryClaimDocumentType", getText("label.lov.recoveryClaimAttachmentType")));
        theList.add(new LovType("supplierPartAcceptanceReason", getText("label.partReturn.supplierPartAcceptanceReason")));
        theList.add(new LovType("supplierPartRejectionReason", getText("label.partReturn.supplierPartRejectionReason")));
        theList.add(new LovType("discountType", getText("label.lov.discountType")));
        Collections.sort(theList);
        return theList;
	}
	
	private int getNumberOfPagesFromTheList(PageSpecification pageSpecification) {
		int pageSize2 = pageSpecification.getPageSize();
		int quotient =  theList.size()/pageSize2;
		if(theList.size() == pageSize2*quotient) {
			return quotient;
		} else {
			return quotient + 1;
		}
	}
	
	private List<LovType> getPageFromTheList(PageSpecification pageSpecification) {
		int startIndex = pageSpecification.getPageNumber() * pageSpecification.getPageSize();
		return theList.subList(startIndex, getEndIndex(theList, startIndex + pageSpecification.getPageSize()));
	}

	private int getEndIndex(List aList,int toIndex) {
		if(aList.size() <= toIndex) {
			return aList.size();
		} else {
			return toIndex;
		}		
	}
	
	public String detail() throws Exception {
        return SUCCESS;
    }
         
    public String preview() {
        return SUCCESS;
    }
    
    public List<LovType> getTheList() {
		return theList;
	}

	public void setTheList(List<LovType> theList) {
		this.theList = theList;
	}
	
	public List<ListOfValues> getLovs() {
		return lovs;
	}

	public void setLovs(List<ListOfValues> lovs) {
		this.lovs = lovs;
	}
}
