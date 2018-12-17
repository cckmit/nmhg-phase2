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

package tavant.twms.web.partsreturn;

import static tavant.twms.web.documentOperations.DocumentAction.getDocumentListJSON;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;

import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.log4j.Logger;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import tavant.twms.common.TWMSCommonUtil;
import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.additionalAttributes.AdditionalAttributes;
import tavant.twms.domain.catalog.CatalogException;
import tavant.twms.domain.catalog.CatalogService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimAudit;
import tavant.twms.domain.claim.ClaimService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplacedService;
import tavant.twms.domain.claim.PartsClaim;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimAudit;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemRepository;
import tavant.twms.domain.notification.EventService;
import tavant.twms.domain.orgmodel.*;
import tavant.twms.domain.partreturn.*;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingService;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.contract.Contract;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.domain.common.Document;
import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.infra.BeanProvider;
import tavant.twms.infra.DomainRepository;
import tavant.twms.infra.HibernateCast;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.jbpm.WorkflowConstants;
import tavant.twms.process.PartReturnProcessingService;
import tavant.twms.process.PartTaskBean;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.common.fop.FopHandler;
import tavant.twms.web.inbox.SummaryTableAction;
import tavant.twms.web.inbox.SummaryTableColumn;
import tavant.twms.worklist.WorkListCriteria;
import tavant.twms.worklist.WorkListItemService;
import tavant.twms.worklist.partreturn.PartReturnWorkList;
import tavant.twms.worklist.partreturn.PartReturnWorkListItemService;
import tavant.twms.worklist.partreturn.PartReturnWorkListService;
import tavant.twms.worklist.supplier.SupplierRecoveryWorkListDao;
import tavant.twms.security.SelectedBusinessUnitsHolder;
import com.opensymphony.xwork2.Preparable;
import freemarker.core.ReturnInstruction.Return;

import com.opensymphony.xwork2.Preparable;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.TraxSource;

public class PartReturnInboxAction extends SummaryTableAction implements
        Preparable {

    private static final Logger logger = Logger
            .getLogger(PartReturnInboxAction.class);
    public static final String DEFAULT_DATE_PATTERN = "MM-dd-yyyy";
    // The next 3 fields will be populated by xwork.xml using set-params\
    // interceptor
    private String taskName;
    
    // i am creating this filed for the issue NMHGSLMS-851
    private String shippingComments;

    public boolean wpra=false;
    
	public boolean isWPRA() {
		return getConfigParamService().getBooleanValue(ConfigName.SHOW_WPRA_NUMBER_IN_LOCATION_VIEW.getName());
	}


	private String switchButtonActionName;
	
	private String switchButtonLabel;
	
	private String switchButtonTabLabel;

    // This field will be injected from webapp-context, a definition of fields
    private Map partReturnFields;

    private String number;

    private String name;

    private String serialNumberId;

	private String actionUrl;

    private List<ClaimWithPartBeans> claimWithPartBeans = new ArrayList<ClaimWithPartBeans>();

    private PartReturnWorkListService partReturnWorkListService;

    private PartReturnService partReturnService;

    private PartReturnWorkListItemService partReturnWorkListItemService;

    private WorkListItemService workListItemService;

    private List<OEMPartReplacedBean> partReplacedBeans = new ArrayList<OEMPartReplacedBean>() ;

    private String shipmentIdString;

    protected final List<PartsWithDealerBeans> shipmentsForPrintTag = new ArrayList<PartsWithDealerBeans>();

    public static final String MARK_FOR_INSPECTION = "Mark for Inspection";

    public static final String MARK_NOT_RECEIVED = "Mark not received";

    public static final String ACCEPT = "Accept";

    public static final String REJECT = "Reject";
    
    protected Map<ShipmentStatus,String> shipmentStatus;

    protected Map<ReceiptStatus,String> receiptStatus;

    private InventoryItemRepository inventoryItemRepository;

	protected Map<InspectionStatus,String> inspectionStatus;

    private boolean isClaimDenied;

    protected ClaimService claimService;

    private String claimID;

    private EventService eventService;

    List<String> claimsDenied = new ArrayList<String>();

    private ConfigParamService configParamService;

    private List<Document> attachments = new ArrayList<Document>();

    private CatalogService catalogService;

    private PartReplacedService partReplacedService;
    
    private WarehouseService warehouseService;

    private String inboxViewType = null;

    protected String transitionTaken;

    private String denialMessage;
    
    protected String clientDate;
    
	private Shipment shipment;
	
    private DomainRepository domainRepository;
    
    private SupplierRecoveryWorkListDao supplierRecoveryWorkListDao;
    
    public static final String WPRA_VIEW="wpraView_shipmentGenerated";
    
    public static final String SHIPMENT_VIEW="shipmentGenerated";
    
    
   
	public String getClientDate() {
		return clientDate;
	}

	public void setClientDate(String clientDate) {
		this.clientDate = clientDate;
	}

	
	public String getDenialMessage() {
		return denialMessage;
	}

	public void setDenialMessage(String denialMessage) {
		this.denialMessage = denialMessage;
	}

	public String getTransitionTaken() {
		return transitionTaken;
	}

	public void setTransitionTaken(String transitionTaken) {
		this.transitionTaken = transitionTaken;
	}

	public String getInboxViewType() {
		return inboxViewType;
	}

	public void setInboxViewType(String inboxViewType) {
		this.inboxViewType = inboxViewType;
	}


    public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getSerialNumberId() {
		return serialNumberId;
	}

	public void setSerialNumberId(String serialNumberId) {
		this.serialNumberId = serialNumberId;
	}


    public InventoryItemRepository getInventoryItemRepository() {
		return inventoryItemRepository;
	}

	public void setInventoryItemRepository(InventoryItemRepository inventoryItemRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
	}

	@SuppressWarnings("unchecked")
    @Override
    protected List<SummaryTableColumn> getHeader() {
        Assert
                .state(this.partReturnFields != null,
                        "Column definitions using SummaryTableColumn are not being configured");
        Assert.hasText(this.taskName,
                "Task name hasn't been set for getting column definitions");
        Assert.state(this.partReturnFields.containsKey(this.taskName+(inboxViewType==null ? "" :"_"+inboxViewType)),
                "The configured column definitions ["
                        + this.partReturnFields.keySet()
                        + "] doesnt have a key for [" + this.taskName + "]");
        List<SummaryTableColumn> columnList = new ArrayList<SummaryTableColumn>();
		if (showWPRA()) {
			columnList = (List<SummaryTableColumn>) this.partReturnFields
					.get(TWMSWebConstants.WPRA);
		} else {
			columnList = (List<SummaryTableColumn>) this.partReturnFields
					.get(this.taskName
							+ (inboxViewType == null ? "" : "_" + inboxViewType));
		}
        if((columnList!= null && columnList.size() > 0) &&
        		(getLoggedInUser().hasRole(Role.RECEIVER_LIMITED_VIEW) ||
        		getLoggedInUser().hasRole(Role.INSPECTOR_LIMITED_VIEW))){
        	columnList = removeDealerColumnForLimitedView(columnList);
        }
        return columnList;
    }
	
	
	protected boolean isWPRAViewApplicableBasedOnInboxAndUser(){
		if (isLoggedInUserADealer()
				&& (this.taskName.equalsIgnoreCase("Overdue Parts") || this.taskName
						.equalsIgnoreCase("Due Parts")) && configParamService.getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())) {
			return true;
		}
		if (isLoggedInUserAnInternalUser()
				&& (this.taskName
						.equalsIgnoreCase("Due Parts Inspection") || this.taskName
						.equalsIgnoreCase("Due Parts Receipt")) && configParamService.getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())) {
			return true;
		}
		return false;
	}
	
	protected boolean showWPRA() {
		if (isWPRA()) {
			if (inboxViewType != null
					&& !inboxViewType.equalsIgnoreCase("claim")) {
				return true;
			}
			if (inboxViewType == null) {
				return isWPRAViewApplicableBasedOnInboxAndUser();
			}
		}

		return false;
	}
	
	protected boolean showWPRALabel() {
		if (isWPRA()) {
			if ((switchButtonActionName!=null && switchButtonActionName.startsWith("locationBased")) || (inboxViewType != null
					&& inboxViewType.equalsIgnoreCase("claim"))) {
				return isWPRAViewApplicableBasedOnInboxAndUser();
			}
		}

		return false;
	}

	private List<SummaryTableColumn> removeDealerColumnForLimitedView(List<SummaryTableColumn> columnList){
		List<SummaryTableColumn> tmpColumnList = new ArrayList<SummaryTableColumn>();

		for(SummaryTableColumn column : columnList){

			if(!"claim.forDealer.name".equalsIgnoreCase(column.getExpression())){
				tmpColumnList.add(column);
			}
		}
		return tmpColumnList;
	}

    @Override
    @SuppressWarnings("unchecked")
    protected PageResult<?> getBody() {
        logger.debug("Fetching task list for folderName [" + getFolderName()
                + "]");
        PartReturnWorkList partReturnWorkList = getWorkList();
        List partReturnTaskList = partReturnWorkList.getPartReturnTaskItem();
        return new PageResult(partReturnTaskList, new PageSpecification(this.page,
                this.pageSize, partReturnWorkList.getTaskItemCount()), getTotalNumberOfPages(partReturnWorkList
                .getTaskItemCount()));
    }

	@Override
	public BeanProvider getBeanProvider() {
		return new SkipInitialOgnlBeanProvider() {
			@Override
			public Object getProperty(String propertyPath, Object root) {
				if ("claim.itemReference.unserializedItem.model.name".equals(propertyPath)) {
					boolean isSerialized = (Boolean) super.getProperty(
							"claim.itemReference.serialized", root);
					String modelNumber = "";
					if (isSerialized) {
						modelNumber = (String) super
								.getProperty(
										"claim.itemReference.unserializedItem.model.name",
										root);
					} else {
						modelNumber = (String) super.getProperty(
								"claim.itemReference.model.name", root);
					}
					return modelNumber;
				} else {
					return super.getProperty(propertyPath, root);
				}
			}
		};
	}

    public String showPreview() throws Exception {
        generateView();
        if (this.orgService.isInspector(getLoggedInUser())
                || this.orgService.isReceiver(getLoggedInUser())) {

            List<ClaimWithPartBeans> claimsWithPart = this.claimWithPartBeans;
            if (!claimsWithPart.isEmpty()) {
            	SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claimsWithPart.iterator().next().getClaim().getBusinessUnitInfo().getName());
            }
            for (Iterator<ClaimWithPartBeans> iterator = claimsWithPart
                    .iterator(); iterator.hasNext();) {
                ClaimWithPartBeans individualClaim = iterator.next();
                if ((ClaimState.DENIED_AND_CLOSED.getState())
                        .equals(individualClaim.getClaim().getState()
                                .getState())) {
                    this.claimsDenied.add(individualClaim.getClaim()
                            .getClaimNumber());
                }
            }
            if (this.claimsDenied.size() > 0) {
                setClaimDenied(true);
                this.denialMessage = getText("label.parts.deniedClaimsWarning", "", StringUtils.collectionToDelimitedString(this.claimsDenied, ","));
            }
        }
        return SUCCESS;
    }

    @Deprecated
    @SuppressWarnings("unchecked")
    public String fetchShipmentForTagPrint() {
        String[] shipmentIds = StringUtils
                .commaDelimitedListToStringArray(this.shipmentIdString);
        for (int i = 0; i < shipmentIds.length; i++) {
            populateShipmentsForPrintTagForPartShipper(shipmentIds[i]);
        }
        populatePrintShipmentDetails(shipmentsForPrintTag);
        this.shipmentIdString = null;
        return SUCCESS;
    }

    @Deprecated
    private void populateShipmentsForPrintTagForPartShipper(String shipmentId) {
        List<TaskInstance> taskInstances = findAllPartTasksForId(shipmentId);
        List<PartTaskBean> beans = new ArrayList<PartTaskBean>();
        for (TaskInstance instance : taskInstances) {
            beans.add(new PartTaskBean(instance));
        }
        this.shipmentsForPrintTag.add(new PartsWithDealerBeans(beans, shipmentId));
    }

    @SuppressWarnings("unchecked")
    public String fetchNewShipmentForTagPrint() {
        ServletContext context = ServletActionContext.getServletContext();
        PartsWithDealerBeans parts = populateShipmentsForPrintTag(shipmentIdString);
        ShipmentTagVO shipmentTag = getPrintShipmentDetails(parts);
        shipmentTag.setLabelShippedDate(getText("label.partReturnConfiguration.shipmentDate"));
        if(!isBuConfigAMER() && getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName())
                && !(taskName.equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER) || taskName.equalsIgnoreCase(WorkflowConstants.DEALER_REQUESTED_PARTS_SHIPPED))){
           FopHandler.transformXMLString("/partShipmentTag.xsl", getXMLString(shipmentTag), context, response);
        }
        else if(isBuConfigAMER() && !(getConfigParamService().getBooleanValue(ConfigName.ENABLE_PART_RETURN_THROUGH_WPRA.getName()))
                && !(taskName.equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER) || taskName.equalsIgnoreCase(WorkflowConstants.DEALER_REQUESTED_PARTS_SHIPPED)||taskName.equalsIgnoreCase(WorkflowConstants.SUPPLIER_SHIPMENMT_GENERATED)||taskName.equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG)||taskName.equalsIgnoreCase(WorkflowConstants.PARTS_SHIPPED_TO_NMHG))){
            FopHandler.transformXMLString("/partShipmentTag_US.xsl", getXMLString(shipmentTag), context, response);
        } 
        else if(!isBuConfigAMER() && (taskName.equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER) || taskName.equalsIgnoreCase(WorkflowConstants.DEALER_REQUESTED_PARTS_SHIPPED))){
         shipmentTag.setLabelShippedDate(getText("label.partReturnConfiguration.availableDate"));
         FopHandler.transformXMLString("/partShipmentTagForPartshipperToDealer.xsl", getXMLString(shipmentTag), context, response);
        }
        else if(isBuConfigAMER() && taskName.equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER) || taskName.equalsIgnoreCase(WorkflowConstants.DEALER_REQUESTED_PARTS_SHIPPED)||taskName.equalsIgnoreCase(WorkflowConstants.SUPPLIER_SHIPMENMT_GENERATED)){
            shipmentTag.setLabelShippedDate(getText("label.partReturnConfiguration.availableDate"));
            FopHandler.transformXMLString("/partShipmentTagForPartshipperToDealer_US.xsl", getXMLString(shipmentTag), context, response);
           }
        else if(isBuConfigAMER() &&(taskName.equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG)||taskName.equalsIgnoreCase(WorkflowConstants.PARTS_SHIPPED_TO_NMHG))){
            shipmentTag.setLabelShippedDate(getText("label.partReturnConfiguration.availableDate"));
            FopHandler.transformXMLString("/partShipmentTagForSupplierToNMHG_US.xsl", getXMLString(shipmentTag), context, response);
           }
        return null;

    }

    protected PartsWithDealerBeans populateShipmentsForPrintTag(String shipmentId) {
        List<TaskInstance> taskInstances = printAllPartTasksForId(shipmentId);
        List<PartTaskBean> beans = new ArrayList<PartTaskBean>();
        for (TaskInstance instance : taskInstances) {
            beans.add(new PartTaskBean(instance));
        }
        return new PartsWithDealerBeans(beans, shipmentId);
    }
    protected List<TaskInstance> printAllPartTasksForId(String id) {
        WorkListCriteria criteria = createCriteria();
        criteria.setIdentifier(id);
        if(wpra)
        {
        	return this.partReturnWorkListItemService.printAllTasksForWPRA(criteria);	
        }
        else 
        {
        	return this.partReturnWorkListItemService.printAllTasksForShipment(criteria);
        }
              
    }

    protected TraxSource getXMLString(ShipmentTagVO shipmentTagVO){
        XStream stream = new XStream();
        stream.alias("shipmentTag", ShipmentTagVO.class);
        stream.alias("partDetail", ClaimWithPartVO.class);
        stream.alias("loadinfo", LoadInformation.class);
        stream.alias("part", PartReturnVO.class);
        stream.alias("claim", ClaimVO.class);
        stream.alias("returnToAddress", ReturnAddressVO.class);
        stream.alias("shipment", ShipmentVO.class);
        stream.alias("address", AddressVO.class);
        return new TraxSource(shipmentTagVO, stream);
    }
    private ShipmentTagVO getShippmentDetialsForSuppliertoNMHG(){
        ShipmentTagVO shipmentTag = new ShipmentTagVO();
        List<ClaimWithPartVO> partDetails = new ArrayList<ClaimWithPartVO>();
    	List<SupplierPartReturn> list = new ArrayList<SupplierPartReturn>();
  	  List<TaskInstance> taskInstances = this.getSupplierRecoveryWorkListDao().getPreviewPaneForClaimLocation(new Long(getShipmentIdString()),this.taskName);
	      Map<RecoveryClaim, List<SupplierPartReturn>> claimsInShipment = new HashMap<RecoveryClaim, List<SupplierPartReturn>>();
    	for (TaskInstance taskInstance : taskInstances) {
			RecoveryClaim recoveryClaim = (RecoveryClaim) taskInstance.getVariable("recoveryClaim");
			if (claimsInShipment.containsKey(recoveryClaim)) {
				claimsInShipment.get(recoveryClaim).add((SupplierPartReturn) taskInstance.getVariable("supplierPartReturn"));
			} else {
				claimsInShipment.put(recoveryClaim, new ArrayList<SupplierPartReturn>());
				claimsInShipment.get(recoveryClaim).add((SupplierPartReturn) taskInstance.getVariable("supplierPartReturn"));
			}
		}
        ClaimWithPartVO claimWithPart;
        PartReturnVO partReturnVO;
      for (RecoveryClaim recoveryClaim : claimsInShipment.keySet()) {
        Map<String, PartReturnVO> parts = new HashMap<String, PartReturnVO>();
      for (SupplierPartReturn  supplierPartReturn : claimsInShipment.get(recoveryClaim)) {
              ClaimVO claimVo = new ClaimVO();
              populateClaim(recoveryClaim.getClaim(), claimVo);  	
              OEMPartReplaced part = supplierPartReturn.getRecoverablePart().getOemPart();
              list.add(supplierPartReturn);                   
              String partNumber = recoveryClaim.getClaim().getServiceInformation().getCausalBrandPart().getItemNumber();
              shipmentTag.setBusinessUnit(recoveryClaim.getClaim().getBusinessUnitInfo().getName());
              if (parts.get(recoveryClaim.getClaim().getServiceInformation().getCausalBrandPart().getItemNumber()) == null) {
                  claimWithPart = new ClaimWithPartVO();
                  partReturnVO = new PartReturnVO(partNumber);
                  if(StringUtils.hasText(part.getSerialNumber())){
						partReturnVO.setComponentSerialNumber(part.getSerialNumber());
						}
                  partReturnVO.setShippingCommentsInClaim(shippingCommentsInClaim(recoveryClaim.getClaim()));
                  partReturnVO.setShippingCommentsInRecClaim(shippingCommentsInRecClaim(recoveryClaim.getClaim(), part));
                  partReturnVO.setNumberOfParts(part.getShippedPartsQuantity());
                  partReturnVO.setDescription(getOEMPartCrossRefForDisplay(recoveryClaim.getClaim().getServiceInformation().getCausalPart(), recoveryClaim.getClaim().getServiceInformation().getOemDealerCausalPart(), false, recoveryClaim.getClaim().getForDealer()));
                  String dueDate= null;
                  if(!part.getPartReturns().isEmpty()){
                	     CalendarDate  calendarDate =   part.getPartReturns().get(0).getDueDate().plusDays(10);
                         dueDate=calendarDate.toString("dd-MM-yyyy");
                
                  }
                  
                  partReturnVO.setDueDate(dueDate);
                  partReturnVO.setDueDateMessage(MessageFormat.format(getText("label.partShipmentTag.supplierWarrantyPartReturnTagMessages"),dueDate));
                  if(!part.getPartReturns().isEmpty()){
                  if(null != part.getPartReturns().get(0).getWpra()){
                      partReturnVO.setWpraNumber(!part.getPartReturns().isEmpty() ? part.getPartReturns().get(0).getWpra().getWpraNumber():null);
                  }
                  }
                  partReturnVO.setRmaNumber(!part.getPartReturns().isEmpty() ? part.getPartReturns().get(0).getRmaNumber():null);
                  if(null != part.getPartReturnConfiguration() && null != part.getPartReturnConfiguration().getPartReturnDefinition()){
                      partReturnVO.setReceiverInstructions(getStringValue(part.getPartReturnConfiguration().getPartReturnDefinition().getReceiverInstructions()));
                    
                  }
                  partReturnVO.setDueDateMessage(MessageFormat.format(getText("label.partShipmentTag.warrantyPartReturnTagMessages"),dueDate));
                  if(!part.getPartReturns().isEmpty()){
                  if(null != part.getPartReturns().get(0).getWpra()){
                      partReturnVO.setWpraNumber(!part.getPartReturns().isEmpty() ? part.getPartReturns().get(0).getWpra().getWpraNumber():null);
                  }
                  }
                  partReturnVO.setRmaNumber(!part.getPartReturns().isEmpty() ? part.getPartReturns().get(0).getRmaNumber():null);
                  if(null != part.getPartReturnConfiguration() && null != part.getPartReturnConfiguration().getPartReturnDefinition()){
                      partReturnVO.setReceiverInstructions(getStringValue(part.getPartReturnConfiguration().getPartReturnDefinition().getReceiverInstructions()));
                    
                  }
                  partReturnVO.setShipperComments(part.getLatestAuditComments());
                  partReturnVO.setProblemPartNumber(partNumber);
                  //vendor part Number
                  if(null != recoveryClaim.getClaim().getRecoveryInfo())
                  {
                     List<RecoveryClaimInfo> recInfo = recoveryClaim.getClaim().getRecoveryInfo().getReplacedPartsRecovery();
                     for(RecoveryClaimInfo recIn : recInfo)
                     {
                         List<RecoverablePart> recParts = recIn.getRecoverableParts();
                         for(RecoverablePart recPart : recParts)
                         {
                             if(recPart.getOemPart().getItemReference().getReferredItem().getNumber().equals(part.getItemReference().getReferredItem().getNumber()))
                             {
                                 if(recPart.getOemPart().isReturnDirectlyToSupplier())
                                 {
                                     //Set supplier and recovery information
                                     if(StringUtils.hasText(recIn.getRecoveryClaim().getDocumentNumber())){
                                         claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber().concat("-").
                                      		   concat(recIn.getRecoveryClaim().getDocumentNumber()));
                                  	   }
                                  	   else{
                                  		   claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber());   
                                  	   }
                                     claimVo.setShippingInstruction(recPart.getOemPart().getAppliedContract().getShippingInstruction());
                                     claimVo.setSupplierName(recPart.getOemPart().getAppliedContract().getSupplier().getName());
                                     shipmentTag.setFrom(populateAddress(recPart.getOemPart().getAppliedContract().getSupplier().getAddress()));
                                     shipmentTag.setDealerNumber(recPart.getOemPart().getAppliedContract().getSupplier().getSupplierNumber());
                                     shipmentTag.setNonWarrantyAnalysis(recIn.getRecoveryClaim().getLatestRecoveryAudit().getExternalComments());
                                     
                                 } 
                                 if(null != recPart.getSupplierItem())
                                 {
                                     partReturnVO.setVendorPartNumber(recPart.getSupplierItem().getNumber());
                                     if(null != recPart.getSupplierPartReturns() && recPart.getSupplierPartReturns().size() >0)
                                     {
                                         partReturnVO.setVendorRequestedDate((!recPart.getSupplierPartReturns().isEmpty()) ? recPart.getSupplierPartReturns().get(0).getD().getCreatedOn():null);
                                         if(StringUtils.hasText(recIn.getRecoveryClaim().getDocumentNumber())){
                                             claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber().concat("-").
                                          		   concat(recIn.getRecoveryClaim().getDocumentNumber()));
                                      	   }
                                      	  else{
                                      		   claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber());   
                                      	  }
                                         claimVo.setShippingInstruction(recPart.getOemPart().getAppliedContract().getShippingInstruction());
                                         claimVo.setSupplierName(recPart.getOemPart().getAppliedContract().getSupplier().getName());
                                         shipmentTag.setFrom(populateAddress(recPart.getOemPart().getAppliedContract().getSupplier().getAddress()));
                                         shipmentTag.setDealerNumber(recPart.getOemPart().getAppliedContract().getSupplier().getSupplierNumber());
                                         shipmentTag.setNonWarrantyAnalysis(recIn.getRecoveryClaim().getLatestRecoveryAudit().getExternalComments());
                                       
                                     }
                                 }
                                 
                             }
                             
                         }
                    
                     parts.put(partNumber, partReturnVO);
                     claimWithPart.setPart(partReturnVO);
                     claimWithPart.setClaim(claimVo);
                     partDetails.add(claimWithPart);
                  }
              }
            }
      }
      }
    
    shipmentTag.setPartDetails(partDetails);  	  
  OEMPartReplaced part = (!list.isEmpty()) ?list.get(0).getRecoverablePart().getOemPart(): null;
   PartReturn returnedPart =(!part.getPartReturns().isEmpty()) ? part.getPartReturns().get(0) : null;

  ServiceProvider dealer = returnedPart.getReturnedBy();
  Shipment shipment = null;

  if(null != returnedPart.getShipment()) {
      shipment= returnedPart.getShipment();
      shipmentTag.setShipment(getShipmentDetails(shipment, dealer));
  }
  else if(null != part.getPartReturns()){
      shipment=   part.getPartReturn().getShipment();
      shipmentTag.setShipment(getShipmentDetails(shipment, dealer));
  }
/*        shipmentTag.setRmaNumber(part.getPartReturns().isEmpty() ? "" : part.getPartReturns().get(0).getRmaNumber());*/
  Location returnLocation =  returnedPart.getReturnLocation();
  //For canadian dealers
  try{
  if(dealer.getAddress().getCountry().equalsIgnoreCase("CA")){
      returnLocation = this.getWarehouseService().getDefaultReturnLocation(getCentralLogisticLocation());
  }

  }catch (NullPointerException exp){
      //continue
  }
  ReturnAddressVO returnToAddress = getReturnToAddress(returnLocation.getAddress());
  AddressVO addressVO = returnToAddress.getAddress();
  if(StringUtils.hasText(returnLocation.getCode())){
    	 Warehouse warehouse = this.getWarehouseService().findByWarehouseCode(returnLocation.getCode());
    	 if(warehouse!=null && StringUtils.hasText(warehouse.getBusinessName())){
    		addressVO.setBusinessName(warehouse.getBusinessName());
    	 }
    	 if(warehouse!=null && StringUtils.hasText(warehouse.getContactPersonName())){
    		returnToAddress.setContactPersonName(warehouse.getContactPersonName());
    	 }
  }
  returnToAddress.setAddress(addressVO); 
  shipmentTag.setReturnToAddress(returnToAddress);
  shipmentTag.setLanguage(getLoggedInUser().getLocale().getLanguage());
      return shipmentTag;	
  }
    private ShipmentTagVO getShippmentDetialsForNMHGtoSupplier(){
        ShipmentTagVO shipmentTag = new ShipmentTagVO();
        List<ClaimWithPartVO> partDetails = new ArrayList<ClaimWithPartVO>();
       	shipmentTag.setNmhgToDealerShipment("false");
    	List<SupplierPartReturn> list = new ArrayList<SupplierPartReturn>();
    	 this.setShipment((Shipment) this.getDomainRepository().load(Shipment.class, new Long(getShipmentIdString()))); 
    	  List<TaskInstance> taskInstances = this.getSupplierRecoveryWorkListDao().getTasksForSupplierPartReturns(this.getShipment().getSupplierPartReturns(),"Supplier Shipment Generated");
	      Map<RecoveryClaim, List<SupplierPartReturn>> claimsInShipment = new HashMap<RecoveryClaim, List<SupplierPartReturn>>();
	    	for (TaskInstance taskInstance : taskInstances) {
				RecoveryClaim recoveryClaim = (RecoveryClaim) taskInstance.getVariable("recoveryClaim");
				if (claimsInShipment.containsKey(recoveryClaim)) {
					claimsInShipment.get(recoveryClaim).add((SupplierPartReturn) taskInstance.getVariable("supplierPartReturn"));
				} else {
					claimsInShipment.put(recoveryClaim, new ArrayList<SupplierPartReturn>());
					claimsInShipment.get(recoveryClaim).add((SupplierPartReturn) taskInstance.getVariable("supplierPartReturn"));
				}
			}
	        ClaimWithPartVO claimWithPart;
	        PartReturnVO partReturnVO;
	      for (RecoveryClaim recoveryClaim : claimsInShipment.keySet()) {
	        Map<String, PartReturnVO> parts = new HashMap<String, PartReturnVO>();
        for (SupplierPartReturn  supplierPartReturn : claimsInShipment.get(recoveryClaim)) {
                ClaimVO claimVo = new ClaimVO();
                populateClaim(recoveryClaim.getClaim(), claimVo);  	
                OEMPartReplaced part = supplierPartReturn.getRecoverablePart().getOemPart();
                list.add(supplierPartReturn);                   
                String partNumber = recoveryClaim.getClaim().getServiceInformation().getCausalBrandPart().getItemNumber();
                shipmentTag.setBusinessUnit(recoveryClaim.getClaim().getBusinessUnitInfo().getName());
                if (parts.get(recoveryClaim.getClaim().getServiceInformation().getCausalBrandPart().getItemNumber()) == null) {
                    claimWithPart = new ClaimWithPartVO();
                    partReturnVO = new PartReturnVO(partNumber);
                    if(StringUtils.hasText(part.getSerialNumber())){
						partReturnVO.setComponentSerialNumber(part.getSerialNumber());
						}
                    partReturnVO.setShippingCommentsInClaim(shippingCommentsInClaim(recoveryClaim.getClaim()));
                    partReturnVO.setShippingCommentsInRecClaim(shippingCommentsInRecClaim(recoveryClaim.getClaim(), part));
                    partReturnVO.setNumberOfParts(supplierPartReturn.getRecoverablePart().getReceivedFromSupplier());
                    partReturnVO.setDescription(getOEMPartCrossRefForDisplay(recoveryClaim.getClaim().getServiceInformation().getCausalPart(), recoveryClaim.getClaim().getServiceInformation().getOemDealerCausalPart(), false, recoveryClaim.getClaim().getForDealer()));
                    String dueDate= null;
                    if(!part.getPartReturns().isEmpty())
                       dueDate= part.getPartReturns().get(0).getDueDate().toString("dd-MM-yyyy");
                    partReturnVO.setDueDate(dueDate);
                    partReturnVO.setDueDateMessage(MessageFormat.format(getText("label.partShipmentTag.warrantyPartReturnTagMessages"),dueDate));
                    if(!part.getPartReturns().isEmpty()){
                    if(null != part.getPartReturns().get(0).getWpra()){
                        partReturnVO.setWpraNumber(!part.getPartReturns().isEmpty() ? part.getPartReturns().get(0).getWpra().getWpraNumber():null);
                    }
                    }
                    partReturnVO.setRmaNumber(!part.getPartReturns().isEmpty() ? part.getPartReturns().get(0).getRmaNumber():null);
                    if(null != part.getPartReturnConfiguration() && null != part.getPartReturnConfiguration().getPartReturnDefinition()){
                        partReturnVO.setReceiverInstructions(getStringValue(part.getPartReturnConfiguration().getPartReturnDefinition().getReceiverInstructions()));
                      
                    }
                    partReturnVO.setDueDateMessage(MessageFormat.format(getText("label.partShipmentTag.warrantyPartReturnTagMessages"),dueDate));
                    if(!part.getPartReturns().isEmpty()){
                    if(null != part.getPartReturns().get(0).getWpra()){
                        partReturnVO.setWpraNumber(!part.getPartReturns().isEmpty() ? part.getPartReturns().get(0).getWpra().getWpraNumber():null);
                    }
                    }
                    partReturnVO.setRmaNumber(!part.getPartReturns().isEmpty() ? part.getPartReturns().get(0).getRmaNumber():null);
                    if(null != part.getPartReturnConfiguration() && null != part.getPartReturnConfiguration().getPartReturnDefinition()){
                        partReturnVO.setReceiverInstructions(getStringValue(part.getPartReturnConfiguration().getPartReturnDefinition().getReceiverInstructions()));
                      
                    }
                    partReturnVO.setShipperComments(part.getLatestAuditComments());
                    partReturnVO.setProblemPartNumber(partNumber);
                    //vendor part Number
                    if(null != recoveryClaim.getClaim().getRecoveryInfo())
                    {
                       List<RecoveryClaimInfo> recInfo = recoveryClaim.getClaim().getRecoveryInfo().getReplacedPartsRecovery();
                       for(RecoveryClaimInfo recIn : recInfo)
                       {
                           List<RecoverablePart> recParts = recIn.getRecoverableParts();
                           for(RecoverablePart recPart : recParts)
                           {
                               if(recPart.getOemPart().getItemReference().getReferredItem().getNumber().equals(part.getItemReference().getReferredItem().getNumber()))
                               {
                                   if(recPart.getOemPart().isReturnDirectlyToSupplier())
                                   {
                                       //Set supplier and recovery information
										if (StringUtils.hasText(recIn.getRecoveryClaim().getDocumentNumber())) {
											claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber().
													concat("-").concat(recIn.getRecoveryClaim().getDocumentNumber()));
										} else {
											claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber());
										}
                                       claimVo.setShippingInstruction(recPart.getOemPart().getAppliedContract().getShippingInstruction());
                                       claimVo.setSupplierName(recPart.getOemPart().getAppliedContract().getSupplier().getName());
                                       shipmentTag.setFrom(populateAddress(recPart.getOemPart().getAppliedContract().getSupplier().getAddress()));
                                       shipmentTag.setDealerNumber(recPart.getOemPart().getAppliedContract().getSupplier().getSupplierNumber());
                                       
                                   } 
                                   if(null != recPart.getSupplierItem())
                                   {
                                       partReturnVO.setVendorPartNumber(recPart.getSupplierItem().getNumber());
                                       if(null != recPart.getSupplierPartReturns() && recPart.getSupplierPartReturns().size() >0)
                                       {
                                           partReturnVO.setVendorRequestedDate((!recPart.getSupplierPartReturns().isEmpty()) ? recPart.getSupplierPartReturns().get(0).getD().getCreatedOn():null);
                                           if (StringUtils.hasText(recIn.getRecoveryClaim().getDocumentNumber())) {
   											claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber().
   													concat("-").concat(recIn.getRecoveryClaim().getDocumentNumber()));
   										    } 
                                           else {
   											claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber());
   									     	}
                                           claimVo.setShippingInstruction(recPart.getOemPart().getAppliedContract().getShippingInstruction());
                                           claimVo.setSupplierName(recPart.getOemPart().getAppliedContract().getSupplier().getName());
                                           shipmentTag.setFrom(populateAddress(recPart.getOemPart().getAppliedContract().getSupplier().getAddress()));
                                           shipmentTag.setDealerNumber(recPart.getOemPart().getAppliedContract().getSupplier().getSupplierNumber());
                                         
                                       }
                                   }
                                   
                               }
                               
                           }
                       parts.put(partNumber, partReturnVO);
                       claimWithPart.setPart(partReturnVO);
                       claimWithPart.setClaim(claimVo);
                       partDetails.add(claimWithPart);
                    }
                }
              }
        }
	      }
	    
	    shipmentTag.setPartDetails(partDetails);  	  
    OEMPartReplaced part = (!list.isEmpty()) ?list.get(0).getRecoverablePart().getOemPart(): null;
     PartReturn returnedPart =(!part.getPartReturns().isEmpty()) ? part.getPartReturns().get(0) : null;

    ServiceProvider dealer = returnedPart.getReturnedBy();
    Shipment shipment = null;
  
    if(null != returnedPart.getShipment()) {
        shipment= returnedPart.getShipment();
        shipmentTag.setShipment(getShipmentDetails(shipment, dealer));
    }
    else if(null != part.getPartReturns()){
        shipment=   part.getPartReturn().getShipment();
        shipmentTag.setShipment(getShipmentDetails(shipment, dealer));
    }
/*        shipmentTag.setRmaNumber(part.getPartReturns().isEmpty() ? "" : part.getPartReturns().get(0).getRmaNumber());*/
    Location returnLocation =  returnedPart.getReturnLocation();
    //For canadian dealers
    try{
    if(dealer.getAddress().getCountry().equalsIgnoreCase("CA")){
        returnLocation = this.getWarehouseService().getDefaultReturnLocation(getCentralLogisticLocation());
    }

    }catch (NullPointerException exp){
        //continue
    }
    ReturnAddressVO returnToAddress = getReturnToAddress(returnLocation.getAddress());
    AddressVO addressVO = returnToAddress.getAddress();
    if(StringUtils.hasText(returnLocation.getCode())){
      	 Warehouse warehouse = this.getWarehouseService().findByWarehouseCode(returnLocation.getCode());
      	 if(warehouse!=null && StringUtils.hasText(warehouse.getBusinessName())){
      		addressVO.setBusinessName(warehouse.getBusinessName());
      	 }
      	 if(warehouse!=null && StringUtils.hasText(warehouse.getContactPersonName())){
      		returnToAddress.setContactPersonName(warehouse.getContactPersonName());
      	 }
    }
    returnToAddress.setAddress(addressVO); 
    shipmentTag.setReturnToAddress(returnToAddress);
/*     shipmentTag.setFrom(populateAddress(getAddressForShipmentTag(dealer)));
    shipmentTag.setDealerNumber(dealer.getServiceProviderNumber());*/
    shipmentTag.setLanguage(getLoggedInUser().getLocale().getLanguage());
      return shipmentTag;
    }
    private ShipmentTagVO getShippmentDetialsForDealertoNMHG(PartsWithDealerBeans shipmentsGeneratedForPrint){
        ShipmentTagVO shipmentTag = new ShipmentTagVO();
        List<ClaimWithPartVO> partDetails = new ArrayList<ClaimWithPartVO>();
        
        if(isBuConfigAMER() && taskName.equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER) ||taskName.equalsIgnoreCase(WorkflowConstants.DEALER_REQUESTED_PARTS_SHIPPED))
        {
        	shipmentTag.setNmhgToDealerShipment("true");
        }
        Map<Claim, List<PartTaskBean>> distinctClmMap = new HashMap<Claim, List<PartTaskBean>>();
        List<PartTaskBean> claimList = null;

        for (PartTaskBean partTaskBean : shipmentsGeneratedForPrint.getPartTaskBeans()) {
            if (distinctClmMap.get(partTaskBean.getClaim()) == null) {
                claimList = new ArrayList<PartTaskBean>();
                claimList.add(partTaskBean);
                distinctClmMap.put(partTaskBean.getClaim(), claimList);
            } else {
                distinctClmMap.get(partTaskBean.getClaim()).add(partTaskBean);
            }
        }

        ClaimWithPartVO claimWithPart;
        PartReturnVO partReturnVO;
        for (Claim claim : distinctClmMap.keySet()) {
            Map<String, PartReturnVO> parts = new HashMap<String, PartReturnVO>();
            for (PartTaskBean partTaskBean : distinctClmMap.get(claim)) {
                if (partTaskBean.getClaim().getId() == claim.getId()) {
                    ClaimVO claimVo = new ClaimVO();
                    populateClaim(claim, claimVo);
                    OEMPartReplaced part = partTaskBean.getPart();    
                    String partNumber=part.getBrandItem().getItemNumber();
                    if (parts.get(part.getBrandItem().getItemNumber()) == null) {
                        claimWithPart = new ClaimWithPartVO();
                        partReturnVO = new PartReturnVO(partNumber);
                        if(StringUtils.hasText(part.getSerialNumber())){
							partReturnVO.setComponentSerialNumber(part.getSerialNumber());
						}
                        partReturnVO.setShippingCommentsInClaim(shippingCommentsInClaim(claim));
                        partReturnVO.setShippingCommentsInRecClaim(shippingCommentsInRecClaim(claim, part));
                        partReturnVO.setNumberOfParts(1);
                        partReturnVO.setDescription(part.getBrandItem().getItem().getDescription());
                        String dueDate= null;
                        if(isBuConfigAMER()){
                        	if(!part.getPartReturns().isEmpty())
                        	    dueDate= part.getPartReturns().get(0).getDueDate().toString(DEFAULT_DATE_PATTERN);
                        }else{
                          	if(!part.getPartReturns().isEmpty())
                          	   dueDate= part.getPartReturns().get(0).getDueDate().toString("dd-MM-yyyy");
                        }
                              
                        partReturnVO.setDueDate(dueDate);   
                        if(!isBuConfigAMER()){
                        if(!part.getPartReturns().isEmpty()){
                            if(null != part.getPartReturns().get(0).getWpra()){
                                partReturnVO.setWpraNumber(!part.getPartReturns().isEmpty() ? part.getPartReturns().get(0).getWpra().getWpraNumber():null);
                            }
                            }
                        }
                       
                        partReturnVO.setDueDateMessage(MessageFormat.format(getText("label.partShipmentTag.warrantyPartReturnTagMessages"),dueDate));
                        partReturnVO.setRmaNumber(!part.getPartReturns().isEmpty() ? part.getPartReturns().get(0).getRmaNumber():null);
                        if(null != part.getPartReturnConfiguration() && null != part.getPartReturnConfiguration().getPartReturnDefinition()){
                            partReturnVO.setReceiverInstructions(getStringValue(part.getPartReturnConfiguration().getPartReturnDefinition().getReceiverInstructions()));
                          
                        }
                        partReturnVO.setShipperComments(part.getLatestAuditComments());
                        partReturnVO.setProblemPartNumber(partNumber);
                        //vendor part Number
                        if(null != claim.getRecoveryInfo()){
                           List<RecoveryClaimInfo> recInfo = claim.getRecoveryInfo().getReplacedPartsRecovery();
                           for(RecoveryClaimInfo recIn : recInfo){
                               List<RecoverablePart> recParts = recIn.getRecoverableParts();
                               for(RecoverablePart recPart : recParts){
                                   if(recPart.getOemPart().getItemReference().getReferredItem().getNumber().equals(part.getItemReference().getReferredItem().getNumber())){
                                       if(recPart.getOemPart().isReturnDirectlyToSupplier()){
                                           //Set supplier and recovery information 
                                    	   if(StringUtils.hasText(recIn.getRecoveryClaim().getDocumentNumber())){
                                           claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber().concat("-").
                                        		   concat(recIn.getRecoveryClaim().getDocumentNumber()));
                                    	   }
                                    	   else{
                                    		   claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber());   
                                    	   }
                                           claimVo.setShippingInstruction(recPart.getOemPart().getAppliedContract().getShippingInstruction());
                                           claimVo.setSupplierName(recPart.getOemPart().getAppliedContract().getSupplier().getName());
                                       }
                                       if(null != recPart.getSupplierItem()){
                                    	   //set recovery claim number if part return was not return directly to supplier
											if (StringUtils.hasText(recIn.getRecoveryClaim().getDocumentNumber())) {
												claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber().
														concat("-").concat(recIn.getRecoveryClaim().getDocumentNumber()));
											} else {
												claimVo.setRecoveryClaimNumber(recIn.getRecoveryClaim().getRecoveryClaimNumber());
											}
                                           partReturnVO.setVendorPartNumber(recPart.getSupplierItem().getNumber());
                                           if(null != recPart.getSupplierPartReturns() && recPart.getSupplierPartReturns().size() >0){
                                               partReturnVO.setVendorRequestedDate((!recPart.getSupplierPartReturns().isEmpty()) ? recPart.getSupplierPartReturns().get(0).getD().getCreatedOn():null);
                                           }
                                       }
                                   }
                               }
                           }
                        }
                        parts.put(partNumber, partReturnVO);
                        claimWithPart.setPart(partReturnVO);
                        claimWithPart.setClaim(claimVo);
                        partDetails.add(claimWithPart);
                    } else {
                        partReturnVO = null;
                        partReturnVO=parts.get(partNumber);
                        partReturnVO.setNumberOfParts(partReturnVO.getNumberOfParts() + 1);
                        parts.put(partNumber, partReturnVO);
                    }
                }
            }
        }
        shipmentTag.setPartDetails(partDetails);
        OEMPartReplaced part = (!shipmentsGeneratedForPrint.getPartTaskBeans().isEmpty()) ? shipmentsGeneratedForPrint.getPartTaskBeans().get(0).getPart() : null;
         PartReturn returnedPart =(!part.getPartReturns().isEmpty()) ? part.getPartReturns().get(0) : null;
   
        ServiceProvider dealer = returnedPart.getReturnedBy() == null ? shipmentsGeneratedForPrint.getDealer() : returnedPart.getReturnedBy();
        Shipment shipment = null;
      
        if(null != returnedPart.getShipment()) {
            shipment= returnedPart.getShipment();
            shipmentTag.setShipment(getShipmentDetails(shipment, dealer));
        }
        else if(shipmentsGeneratedForPrint.getPartTaskBeans().size() > 0 && null != shipmentsGeneratedForPrint.getPartTaskBeans().get(0).getPartReturn()){
            shipment= (!shipmentsGeneratedForPrint.getPartTaskBeans().isEmpty()) ?  shipmentsGeneratedForPrint.getPartTaskBeans().get(0).getPartReturn().getShipment() : null;
            shipmentTag.setShipment(getShipmentDetails(shipment, dealer));
        }
      /*  shipmentTag.setRmaNumber(part.getPartReturns().isEmpty() ? "" : part.getPartReturns().get(0).getRmaNumber());*/
        Location returnLocation =  returnedPart.getReturnLocation() == null ? shipmentsGeneratedForPrint.getReturnLocation() : returnedPart.getReturnLocation();
        //For canadian dealers
        try{
        if(dealer.getAddress().getCountry().equalsIgnoreCase("CA")){
            returnLocation = this.getWarehouseService().getDefaultReturnLocation(getCentralLogisticLocation());
        }

        }catch (NullPointerException exp){
            //continue
        }
        ReturnAddressVO returnToAddress = getReturnToAddress(returnLocation.getAddress());
        AddressVO addressVO = returnToAddress.getAddress();
        if(StringUtils.hasText(returnLocation.getCode())){
          	 Warehouse warehouse = this.getWarehouseService().findByWarehouseCode(returnLocation.getCode());
          	 if(warehouse!=null && StringUtils.hasText(warehouse.getBusinessName())){
          		addressVO.setBusinessName(warehouse.getBusinessName());
          	 }
          	 if(warehouse!=null && StringUtils.hasText(warehouse.getContactPersonName())){
          		returnToAddress.setContactPersonName(warehouse.getContactPersonName());
          	 }
        }
        returnToAddress.setAddress(addressVO); 
        shipmentTag.setReturnToAddress(returnToAddress);
         shipmentTag.setFrom(populateAddress(getAddressForShipmentTag(dealer)));
        shipmentTag.setDealerNumber(dealer.getServiceProviderNumber());
        shipmentTag.setLanguage(getLoggedInUser().getLocale().getLanguage());
        shipmentTag.setBusinessUnit((!shipmentsGeneratedForPrint.getPartTaskBeans().isEmpty()) ? shipmentsGeneratedForPrint.getPartTaskBeans().get(0).getClaim().getBusinessUnitInfo().getName():null);
        //Wpra number and Load dimenision section in shipment tag needed for EMEA bu not needed for AMER bu 
        if(!isBuConfigAMER()){
	        if(!part.getPartReturns().isEmpty()){
	        if(null != part.getPartReturns().get(0).getWpra()){
	              shipmentTag.setWpraNumber((!part.getPartReturns().isEmpty()) ? part.getPartReturns().get(0).getWpra().getWpraNumber() : null);
	        }
	       }
	        List<LoadInformation> loadinfos = new ArrayList<LoadInformation>();
	        if(shipment != null) {
	                for(ShipmentLoadDimension sipLoadInfo : shipment.getShipmentLoadDimension()){
	                    LoadInformation loadInfo = new LoadInformation();
	                    loadInfo.setBreadth(sipLoadInfo.getBreadth());
	                    loadInfo.setHeight(sipLoadInfo.getHeight());
	                    loadInfo.setLen(sipLoadInfo.getLength());
	                    loadInfo.setLoadType(sipLoadInfo.getLoadType());
	                    loadInfo.setWeight(sipLoadInfo.getWeight());
	                    loadinfos.add(loadInfo);
	                }
	        }
	        shipmentTag.setLoadinfos(loadinfos);
        }
        return shipmentTag;
    }
    protected ShipmentTagVO getPrintShipmentDetails(PartsWithDealerBeans shipmentsGeneratedForPrint) {
        ShipmentTagVO shipmentTag = new ShipmentTagVO();
        //NMHG to Supplier
        if(shipmentsGeneratedForPrint.getPartTaskBeans().isEmpty() && taskName.equalsIgnoreCase(WorkflowConstants.SUPPLIER_SHIPMENMT_GENERATED)){
        	shipmentTag= getShippmentDetialsForNMHGtoSupplier();
        }
        //Supplier to NMHG
        if(shipmentsGeneratedForPrint.getPartTaskBeans().isEmpty() && (taskName.equalsIgnoreCase(WorkflowConstants.SHIPMENT_GENERATED_TO_NMHG)||taskName.equalsIgnoreCase(WorkflowConstants.PARTS_SHIPPED_TO_NMHG))){
        	shipmentTag =  getShippmentDetialsForSuppliertoNMHG();
        }
        //dealer to NMHG and  NMHG to Dealer
        if(!shipmentsGeneratedForPrint.getPartTaskBeans().isEmpty()){
        	shipmentTag = getShippmentDetialsForDealertoNMHG(shipmentsGeneratedForPrint);
        }
        return shipmentTag;
    }

	protected Address getAddressForShipmentTag(ServiceProvider dealer) {
		//return  dealer.getAddress();
		return  dealer.getShipmentAddress();
	}

    /*public boolean isClaimFiledByCanadianDealer(Long id) {
        ServiceProvider dealership = orgService.findDealerById(id);
        return dealership.getAddress().getCountry().equalsIgnoreCase("CA");
    }*/

    protected void populateClaim(Claim claim, ClaimVO claimVo){
        claimVo.setCause(claim.getProbableCause());
        claimVo.setFault(claim.getConditionFound());
        claimVo.setWorkPerformed(claim.getWorkPerformed());
        claimVo.setAdditionalNotes(claim.getOtherComments());
        claimVo.setClaimNumber(claim.getClaimNumber());
       /* //For canadian dealers shipment is always through NMHG
        if(!isClaimFiledByCanadianDealer(claim.getForDealer().getId())){
            List<RecoveryClaim> recoveryClaims= claim.getRecoveryClaims();
            for (RecoveryClaim recoveryClaim : recoveryClaims) {
                 claimVo.setRecoveryClaimNumber(recoveryClaim.getRecoveryClaimNumber());
                 claimVo.setShippingInstruction(recoveryClaim.getContract().getShippingInstruction());
                 claimVo.setSupplierName(recoveryClaim.getContract().getSupplier().getName());
            }
        }
*/
        
        claimVo.setClaimType(claim.getType().getType());
        claimVo.setWorkOrderNumber(claim.getWorkOrderNumber());
        claimVo.setMachineSerialNumber(claim.getSerialNumber());
        claimVo.setModel(claim.getModel());
        claimVo.setFpiNumber(claim.getCampaign()!=null ? claim.getCampaign().getCode() :null);
        String failureDate=null;
        String installationDate=null;
        if(claim.getFailureDate()!=null)
        	if(isBuConfigAMER())
        		failureDate= claim.getFailureDate().toString(DEFAULT_DATE_PATTERN);
        	else
        	  failureDate= claim.getFailureDate().toString("dd-MM-yyyy");
        if(claim.getInstallationDate()!=null)
        	if(isBuConfigAMER())
        		installationDate=claim.getInstallationDate().toString(DEFAULT_DATE_PATTERN);
        	else
        	installationDate=claim.getInstallationDate().toString("dd-MM-yyyy");
    
        if(claim.getActiveClaimAudit()!=null)
        	if(isBuConfigAMER())        		
             claimVo.setPartFittedDate(claim.getActiveClaimAudit().getRepairDate()!=null ? claim.getActiveClaimAudit().getRepairDate().toString(DEFAULT_DATE_PATTERN) : null) ;
        claimVo.setFailureDate(failureDate);
        claimVo.setInstallDate(installationDate);
        claimVo.setHourOnMeter(claim.getHoursInService());
        claimVo.setHourOnPart(claim.getHoursOnPart());
        claimVo.setProblemDesc(claim.getConditionFound());
        claimVo.setDealerName(claim.getForDealer().getDisplayName());
        claimVo.setComponentSerialNumber(claim.getSerialNumber());
		if (StringUtils.hasText(claim.getBrand())) {
			if (claim.getBrand().equals(BrandType.UTILEV)) {
				String dealerBrand = this.orgService.findDealerBrands(claim
						.getForDealer());
				claimVo.setBrand(dealerBrand);
			} else {
				claimVo.setBrand(claim.getBrand());
			}
		} else {
			claimVo.setBrand("");
		}
    }

    protected String getStringValue(String arg){
        if(StringUtils.hasText(arg)){
            return arg;
        }
        return "";
    }

    private ShipmentVO getShipmentDetails(Shipment shipment, ServiceProvider dealer){
        ShipmentVO shipmentVO = new ShipmentVO();
        String shipmentDate=null;
        if(isBuConfigAMER()){
        	  SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
             shipmentDate=formatter.format(shipment.getShipmentDate());   
             shipmentVO.setShipmentDate(shipmentDate);
   		  SimpleDateFormat fromFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.S");
   		  SimpleDateFormat toFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm");
             try {
				shipmentVO.setPrintDate(toFormat.format(fromFormat.parse(clientDate)));
			} catch (ParseException e) 
			{
				e.printStackTrace();
			}
        }
        else{
          SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.S");
          shipmentDate=formatter.format(shipment.getShipmentDate());            
           shipmentVO.setShipmentDate(shipmentDate);
			shipmentVO.setPrintDate(clientDate);
				
        }
    
        shipmentVO.setShipmentNumber(shipment.getId().toString());
        shipmentVO.setTackingNumber(shipment.getTrackingId());
        shipmentVO.setCarrier(shipment.getCarrier()!=null ? shipment.getCarrier().getName():null);
        shipmentVO.setBarcode(shipment.getId().toString());
        if(shippingComments!=null && !shippingComments.equals("")){
        	 shipmentVO.setSenderComments(shippingComments);
        }else{
        	 shipmentVO.setSenderComments(shipment.getComments());
        }
        if(dealer.getAddress().getCountry().equalsIgnoreCase("CA")){
            shipmentVO.setLocation(getCentralLogisticLocation());
        }else{
            shipmentVO.setLocation(shipment.getDestination().getCode());
        }
        return shipmentVO;
    }
    
      protected ReturnAddressVO getReturnToAddress(Address address){
        ReturnAddressVO returnAddress = new ReturnAddressVO();
//    	String addressStr = getAddressString(address);
        AddressVO addressVO = populateAddress(address);
        returnAddress.setAddress(addressVO);
        return returnAddress;
    }

    protected AddressVO populateAddress(Address address){
        AddressVO addressVO = new AddressVO();
		   if(address!=null){
		        if(StringUtils.hasText( address.getContactPersonName()))
		            addressVO.setContactPersonName(address.getContactPersonName());   
		       
		        if(StringUtils.hasText(address.getAddressLine1()))
		            addressVO.setAddressLine1(address.getAddressLine1());
		        if(StringUtils.hasText(address.getAddressLine2()))
		            addressVO.setAddressLine2(address.getAddressLine2());
		        if(StringUtils.hasText(address.getAddressLine3()))
		            addressVO.setAddressLine3(address.getAddressLine3());
		        if(StringUtils.hasText(address.getAddressLine4()))
		            addressVO.setAddressLine4(address.getAddressLine4());
		        addressVO.setCity(address.getCity());
		        addressVO.setState(address.getState());
		        addressVO.setCountry(address.getCountry());
		        addressVO.setZipCode(address.getZipCode());
		        if(address.getBelongsTo() != null)
		            addressVO.setName(address.getBelongsTo().getName());
		   }
        return addressVO;
    }


    /* private void populateShipmentsForPrintTag(String shipmentId) {
        List<TaskInstance> taskInstances = findAllPartTasksForId(shipmentId);
        List<PartTaskBean> beans = new ArrayList<PartTaskBean>();
        for (TaskInstance instance : taskInstances) {
            beans.add(new PartTaskBean(instance));
        }
        this.shipmentsForPrintTag.add(new PartsWithDealerBeans(beans, shipmentId));
    }*/

    protected void generateView() {
        preparePreview(findAllPartTasksForId(getId()));// Converstion Smells
        // !!!
    }

    protected List<TaskInstance> findAllPartTasksForId(String id) {
        WorkListCriteria criteria = createCriteria();
        criteria.setIdentifier(id);
        if(showWPRA()){
        	return this.partReturnWorkListItemService.findAllTasksForWPRA(criteria);	
        }
        else {
        return this.partReturnWorkListItemService.findAllTasksForShipment(criteria);
        }
    }

	protected void preparePreview(final List<TaskInstance> partTasks) {
		this.claimWithPartBeans = preparePreviewAndReturn(partTasks, this.claimWithPartBeans);
		for (ClaimWithPartBeans claimWithPartBeans : this.claimWithPartBeans) {
				for (OEMPartReplacedBean oemPartReplacedBean : claimWithPartBeans.getPartReplacedBeans()) {
					if (oemPartReplacedBean.getOemPartReplaced().isPartReturnInitiatedBySupplier())
						populateApplicableContractsMap(oemPartReplacedBean);
				}
		}
	}

	private void populateApplicableContractsMap(OEMPartReplacedBean partReplacedBean) {
		for (RecoveryClaimInfo recoveryClaimInfo : partReplacedBean.getClaim().getRecoveryInfo().getReplacedPartsRecovery()) {
			for (RecoverablePart recoverablePart : recoveryClaimInfo.getRecoverableParts()) {
				if (recoverablePart.getOemPart().equals(partReplacedBean.getOemPartReplaced())) {
					if (partReplacedBean.getApplicableContracts().containsKey(recoveryClaimInfo.getRecoveryClaim().getContract())) {
						partReplacedBean.getApplicableContracts().put(
								recoveryClaimInfo.getRecoveryClaim().getContract(),
								partReplacedBean.getApplicableContracts().get(recoveryClaimInfo.getRecoveryClaim().getContract()).intValue()
										+ recoverablePart.getQuantity());
					} else {
						partReplacedBean.getApplicableContracts().put(recoveryClaimInfo.getRecoveryClaim().getContract(), recoverablePart.getQuantity());
					}
				}
			}
		}
	}

    protected List<ClaimWithPartBeans> preparePreviewAndReturn(
            final List<TaskInstance> partTasks,
            List<ClaimWithPartBeans> beansList) {
        Map<Claim, List<OEMPartReplacedBean>> claimAndItsParts = new HashMap<Claim, List<OEMPartReplacedBean>>();
        Map<OEMPartReplaced, OEMPartReplacedBean> oemPartAndItsPartReturns = new HashMap<OEMPartReplaced, OEMPartReplacedBean>();
        for (TaskInstance partTask : partTasks) {
            PartTaskBean partTaskBean = new PartTaskBean(partTask);
            if (oemPartAndItsPartReturns.get(partTaskBean.getPart()) == null) {
                oemPartAndItsPartReturns.put(partTaskBean.getPart(),
                        new OEMPartReplacedBean(partTaskBean));
            } else {
                oemPartAndItsPartReturns.get(partTaskBean.getPart())
                        .getPartReturnTasks().add(partTaskBean);
                oemPartAndItsPartReturns.get(partTaskBean.getPart())
                        .getPartTaskIds().add(partTaskBean.getId());
            }

        }
        for (OEMPartReplacedBean partReplacedBean : oemPartAndItsPartReturns
                .values()) {
        	prepareCountForPartReplaced(partReplacedBean);
            if (claimAndItsParts.get(partReplacedBean.getClaim()) == null) {
                claimAndItsParts.put(partReplacedBean.getClaim(),
                        new ArrayList<OEMPartReplacedBean>());
            }
            claimAndItsParts.get(partReplacedBean.getClaim()).add(
                    partReplacedBean);
        }
        Collection<List<OEMPartReplacedBean>> claimBasedGroupOfPartTaskBeans = claimAndItsParts
                .values();
        for (List<OEMPartReplacedBean> aGroupOfPartReplacedBean : claimBasedGroupOfPartTaskBeans) {
            beansList.add(new ClaimWithPartBeans(aGroupOfPartReplacedBean));
        }

        if(!beansList.isEmpty())
        Collections.sort(beansList,new Comparator<ClaimWithPartBeans>() {
					public int compare(ClaimWithPartBeans obj1,
							ClaimWithPartBeans obj2) {
						return obj1.getClaim().getClaimNumber().compareTo(obj2.getClaim().getClaimNumber());
					}
        });
        return beansList;
    }

    public void updatePartStatus(OEMPartReplaced partReplaced) {
        getPartReturnService().updatePartStatus(partReplaced);
    }
    protected void prepareCountForPartReplaced(
            OEMPartReplacedBean partReplacedBean) {
        PartTaskBean partTaskBean = partReplacedBean.getPartReturnTasks()
                .iterator().next();
        for (PartReturn partReturn : partTaskBean.getPart().getPartReturns()) {
            if (PartReturnStatus.PART_TO_BE_SHIPPED.equals(partReturn.getStatus()) ||PartReturnStatus.PART_MOVED_TO_OVERDUE.equals(partReturn.getStatus()) || PartReturnStatus.PART_TO_BE_SHIPPED_TO_DEALER.equals(partReturn.getStatus()) || PartReturnStatus.WPRA_GENERATED.equals(partReturn.getStatus())) {
                partReplacedBean.setToBeShipped(partReplacedBean
                        .getToBeShipped() + 1);
            } else if (PartReturnStatus.SHIPMENT_GENERATED.equals(partReturn.getStatus()) ||PartReturnStatus.NMHG_TO_DEALER_SHIPMENT_GENERATED.equals(partReturn.getStatus())){// || PartReturnStatus.WAITING_FOR_CEVA_TRACKING_INFO.equals(partReturn.getStatus())) {
                partReplacedBean.setShipmentGenerated((partReplacedBean
                        .getShipmentGenerated() + 1));
            } else if (PartReturnStatus.CANNOT_BE_SHIPPED.equals(partReturn.getStatus()) || PartReturnStatus.NMHG_TO_DEALER_CANNOT_BE_SHIPPED.equals(partReturn.getStatus())) {
                partReplacedBean.setCannotBeShipped(partReplacedBean
                        .getCannotBeShipped() + 1);
            }
            else if (PartReturnStatus.WAITING_FOR_CEVA_TRACKING_INFO.equals(partReturn.getStatus())) {
                partReplacedBean
                        .setCevaTracking(partReplacedBean.getCevaTracking() + 1);
            }
            else {
                if ((!PartReturnStatus.REMOVED_BY_PROCESSOR.equals(partReturn.getStatus())) && PartReturnStatus.PART_SHIPPED.ordinal() <= partReturn
                        .getStatus().ordinal()) {
                    partReplacedBean
                            .setShipped((partReplacedBean.getShipped() + 1));
                }
                if ((!PartReturnStatus.REMOVED_BY_PROCESSOR.equals(partReturn.getStatus())) && PartReturnStatus.PART_RECEIVED.ordinal() <= partReturn
                        .getStatus().ordinal()) {
                    partReplacedBean
                            .setReceived(partReplacedBean.getReceived() + 1);
                }
                if ((!PartReturnStatus.REMOVED_BY_PROCESSOR.equals(partReturn.getStatus())) && (PartReturnStatus.PART_ACCEPTED.ordinal() <= partReturn
                        .getStatus().ordinal()
                        || PartReturnStatus.PART_REJECTED.ordinal() <= partReturn
                        .getStatus().ordinal())) {
                    partReplacedBean.setInspected(partReplacedBean
                            .getInspected() + 1);

                }
            }
        }
        populateQtyForShipment(partReplacedBean);
        partReplacedBean.setNotReceived(partReplacedBean.getShipmentGenerated()
                + partReplacedBean.getToBeShipped());
        partReplacedBean.setTotalNoOfParts(partTaskBean.getPart().getActivePartReturns().size());

    }

    private void populateQtyForShipment(OEMPartReplacedBean replacedBean) {
    	replacedBean.setQtyForShipment(replacedBean.getPartReturnTasks().size());
    }

    // This method needs optimizations
    public List<PartTaskBean> getSelectedPartTaskBeans() {
        return getSelectedPartTaskBeans(getPartReplacedBeans());
    }

    public List<PartTaskBean> getSelectedPartTaskBeans(
            List<OEMPartReplacedBean> partReplacedBeans) {
    	return getSelectedPartTaskBeans(partReplacedBeans, true);
    }

    public List<PartTaskBean> getSelectedPartTaskBeans(
                boolean processPartTaskBean) {
    	return getSelectedPartTaskBeans(getPartReplacedBeans(), processPartTaskBean);
    }

    public List<PartTaskBean> getSelectedPartTaskBeans(
            List<OEMPartReplacedBean> partReplacedBeans, boolean processPartTaskBean) {
        List<PartTaskBean> prunedPartTasks = new ArrayList<PartTaskBean>();
        for (OEMPartReplacedBean partReplacedBean : partReplacedBeans) {

            if (partReplacedBean.isSelected()) {
                prepareTaskBeans(partReplacedBean, processPartTaskBean);
                List<PartTaskBean> taskBeans = partReplacedBean
                        .getPartReturnTasks();
                for (PartTaskBean partTaskBean : taskBeans) {
                    if (partTaskBean != null
                            && (partTaskBean.isSelected()
                            || "Due Parts Receipt".equals(getTaskName()) || "Due Parts Inspection"
                            .equals(getTaskName()))
                            && (partTaskBean.getNumber() != null || partTaskBean
                            .getTask().getId() != -1)) {
                    	if(processPartTaskBean)
                    		processPartTaskBean(partTaskBean);
                         prunedPartTasks.add(partTaskBean);
                    }
                 }
            }
        }
        return prunedPartTasks;
    }

    private void prepareTaskBeans(OEMPartReplacedBean partReplacedBean, boolean processPartTaskBean) {
        Iterator<PartTaskBean> partTaskIttr = partReplacedBean
                .getPartReturnTasks().iterator();
        PartTaskBean partTaskBean;
        if (WorkflowConstants.SHIPMENT_GENERATED.equals(this.taskName) || WorkflowConstants.SHIPMENT_GENERATED_FOR_DEALER.equals(this.taskName)) {
        	if(this.transitionTaken != null && "Generate Shipment".equalsIgnoreCase(this.transitionTaken)) {
        		for (int i = 0; i < partReplacedBean.getShip()
		                && partTaskIttr.hasNext(); i++) {
		            partTaskBean = partTaskIttr.next();
		            partTaskBean.setSelected(true);
		            PartReturn partReturn = partTaskBean.getPartReturn();
		            if(processPartTaskBean)
		            	partReturn
		                    .setTriggerStatus(PartReturnTaskTriggerStatus.TO_GENERATE_SHIPMENT);
		        }
        	}else {
        		for (;partTaskIttr.hasNext();) {
		            partTaskBean = partTaskIttr.next();
		            partTaskBean.setSelected(true);
        		}
        	}
        }
        else if (WorkflowConstants.DUE_PARTS_TASK.equals(this.taskName) || WorkflowConstants.OVERDUE_PARTS_TASK.equals(this.taskName) || WorkflowConstants.REJETCTED_PARTS_INBOX.equals(this.taskName)) {
            for (int i = 0; i < partReplacedBean.getShip()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                PartReturn partReturn = partTaskBean.getPartReturn();
                if(processPartTaskBean)  {
                    if(WorkflowConstants.REJETCTED_PARTS_INBOX.equals(this.taskName)){
                          	partReturn.setTriggerStatus(PartReturnTaskTriggerStatus.DEALER_REQUEST_TRIGGERED);
                    }else{
                	    partReturn.setTriggerStatus(PartReturnTaskTriggerStatus.TO_GENERATE_SHIPMENT);
                    }
                }
            }
            for (int i = 0; i < partReplacedBean.getCannotShip()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                PartReturn partReturn = partTaskBean.getPartReturn();
                if(processPartTaskBean) {
                     if(WorkflowConstants.REJETCTED_PARTS_INBOX.equals(this.taskName)){
                          	partReturn.setStatus(PartReturnStatus.RETURN_TO_DEALER_NOT_REQUIRED);
                    }else{
                	    partReturn.setStatus(PartReturnStatus.CANNOT_BE_SHIPPED);
                    }
	                partReturn.setTriggerStatus(PartReturnTaskTriggerStatus.TO_BE_ENDED);
                }
            }
        } else if (WorkflowConstants.DUE_PARTS_RECEIPT.equals(this.taskName)) {
            for (int i = 0; i < partReplacedBean.getReceive()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                partTaskBean.setActionTaken(MARK_FOR_INSPECTION);
                partTaskBean.setWarehouseLocation(partReplacedBean
                        .getWarehouseLocation());
            }
            for (int i = 0; i < partReplacedBean.getDidNotReceive()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                partTaskBean.setActionTaken(MARK_NOT_RECEIVED);
            }
        }
         else if (WorkflowConstants.DEALER_REQUESTED_PART.equals(this.taskName)) {
            for (int i = 0; i < partReplacedBean.getShip()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                PartReturn partReturn = partTaskBean.getPartReturn();
                if(processPartTaskBean)
                	partReturn
                        .setTriggerStatus(PartReturnTaskTriggerStatus.TO_GENERATE_SHIPMENT_FOR_DEALER);
            }
            for (int i = 0; i < partReplacedBean.getCannotShip()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                PartReturn partReturn = partTaskBean.getPartReturn();
                if(processPartTaskBean) {
                    partReturn.setStatus(PartReturnStatus.NMHG_TO_DEALER_CANNOT_BE_SHIPPED);
	                partReturn.setTriggerStatus(PartReturnTaskTriggerStatus.TO_BE_ENDED);
                }
            }
        }
         if (WorkflowConstants.DUE_PARTS_INSPECTION.equals(this.taskName)
                || (partReplacedBean.isToBeInspected())) {
            partTaskIttr = partReplacedBean.getPartReturnTasks().iterator();
            for (int i = 0; i < partReplacedBean.getAccepted()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                partTaskBean.setActionTaken(ACCEPT);
                if (partReplacedBean.isMultipleAcceptanceCauses()) {
                    partTaskBean.setAcceptanceCause(partReplacedBean
                            .getAcceptanceCauses().get(i));
                } else {
                    partTaskBean.setAcceptanceCause(partReplacedBean
                            .getAcceptanceCauses().get(0));
                }
            }
            for (int i = 0; i < partReplacedBean.getRejected()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                partTaskBean.setActionTaken(REJECT);
                if (partReplacedBean.isMultipleFailureCauses()) {
                    partTaskBean.setFailureCause(partReplacedBean
                            .getFailureCauses().get(i));
                } else {
                    partTaskBean.setFailureCause(partReplacedBean
                            .getFailureCauses().get(0));
                }

            }
        }
        else if (WorkflowConstants.WPRA_TO_BE_GENERATED.equals(this.taskName) || WorkflowConstants.PREPARE_DUE_PARTS.equals(this.taskName) ){// || WorkflowConstants.GENERATED_WPRA.equals(this.taskName)) {
            for (int i = 0; i < partReplacedBean.getToBeShipped()
                    && partTaskIttr.hasNext(); i++) {
                partTaskBean = partTaskIttr.next();
                partTaskBean.setSelected(true);
                PartReturn partReturn = partTaskBean.getPartReturn();
                if(processPartTaskBean)  {
                	    partReturn.setTriggerStatus(PartReturnTaskTriggerStatus.WPRA_GENERATED);
                    }
                }
            }
        else if(WorkflowConstants.GENERATED_WPRA.equals(this.taskName)){
        	int selectedParts = partReplacedBean.getToBeShipped() + partReplacedBean.getShipmentGenerated() + partReplacedBean.getCevaTracking();
        	for (int i = 0; i < selectedParts && partTaskIttr.hasNext(); i++) {
        		partTaskBean = partTaskIttr.next();
        		partTaskBean.setSelected(true);
             }
        }
        else if (WorkflowConstants.CEVA_TRACKING.equals(this.taskName)) {
                for (int i = 0; i < partReplacedBean.getCevaTracking()
                        && partTaskIttr.hasNext(); i++) {
                    partTaskBean = partTaskIttr.next();
                    partTaskBean.setSelected(true);
                    PartReturn partReturn = partTaskBean.getPartReturn();
                    if(processPartTaskBean)  {
                        partReturn.setTriggerStatus(PartReturnTaskTriggerStatus.CEVA_TRACKING_INFO_UPDATE);
                    }
                }
            }
    }

    public String getJSONifiedAttachmentList() {
        try {
            Claim claim = this.claimService.findClaim(claimWithPartBeans.get(0).getClaim().getId());
            List<Document> attachments = claim.getAttachments();
            if (attachments == null || attachments.size() <= 0) {
                return "[]";
            }
            return getDocumentListJSON(attachments).toString();
        } catch (Exception e) {
            return "[]";
        }
    }

	@Override
	public void validate() {
        List<PartTaskBean> partTasks = new ArrayList<PartTaskBean>();
		partTasks = getSelectedPartTaskBeans(false);
		if (partTasks.size() == 0) {
			addActionError("error.partReturnConfiguration.noPartSelected");
		}
		for (PartTaskBean bean : partTasks) {
			String itemNumber = bean.getPart().getItemReference()
					.getUnserializedItem().getNumber();
			if (!(bean.getTask().isOpen())) {
				addActionError(
						"error.partReturnConfiguration.partNotAvailable",
						new String[] { itemNumber });
			}
		}
	}







    protected void addErrorMessageForPartReturn(String errorMessage,PartTaskBean bean){
    	addActionError(errorMessage, new String[] { getPartNumberForPartTaskBean(bean)});
    }


    private String getPartNumberForPartTaskBean(PartTaskBean partTaskBean){
    	String itemNumber = partTaskBean.getPart().getItemReference().getUnserializedItem().getNumber();
    	return itemNumber;

    }



    protected String resultingView() {
        generateView();
        if (getClaimWithPartBeans().size() == 0) {
            addActionMessage("message.itemStatus.updated");
            return SUCCESS;
        }
        if (!hasActionErrors()) {
            addActionMessage("message.itemStatus.updated");
            addActionMessage("message.itemStatus.continue_next_step");
        }
        return INPUT;
    }

    public List<TaskInstance> getTasksFromPartTaskBeans(
            List<PartTaskBean> partTaskBeans) {
        List<TaskInstance> tasks = new ArrayList<TaskInstance>();
        for (PartTaskBean partTaskBean : partTaskBeans) {
            tasks.add(partTaskBean.getTask());
        }
        return tasks;
    }

    public List<PartReturn> getPartReturnsFromPartTaskBeans(
            List<PartTaskBean> partTaskBeans) {
        List<PartReturn> parts = new ArrayList<PartReturn>();
        for (PartTaskBean partTaskBean : partTaskBeans) {
            parts.add(partTaskBean.getPartReturn());
        }
        return parts;
    }

    // The default implementation doesn't have any processing. Callback
    // method for subclasses.
    protected void processPartTaskBean(PartTaskBean partTaskBean) {

    }

    // Does any context need to be passed along
	protected PartReturnWorkList getWorkList() {
		if (showWPRA()) {
			if(isLoggedInUserADealer()){
				return this.partReturnWorkListService
						.getPartReturnWorkListForWpraByDealership(createCriteria());
			}
			else {
			return this.partReturnWorkListService
					.getPartReturnWorkListForWpraByActorId(createCriteria());
			}
		} else {
			return this.partReturnWorkListService
					.getPartReturnWorkListByShipment(createCriteria());
		}
	}

    protected WorkListCriteria createCriteria() {
        WorkListCriteria criteria = new WorkListCriteria(getLoggedInUser());
        criteria.setTaskName(this.taskName);
        ServiceProvider loggedInUsersDealership = getLoggedInUsersDealership();
        if (loggedInUsersDealership == null || isLoggedInUserAnEnterpriseDealer()) {
            // Let this pathetic fix be forgiven.
            ServiceProvider s = new ServiceProvider();
            s.setId(-1L);
            s.setVersion(0);
            criteria.setServiceProvider(s);
        } else {
            criteria.setServiceProvider(loggedInUsersDealership);
        }
        criteria.setServiceProviderList(getLoggedInUser().getBelongsToOrganizations());
        if (logger.isInfoEnabled()) {
            logger.info("Folder Name [" + this.taskName + "]");
            logger.info("page size " + this.pageSize + " page to be fetched "
                    + getPage());
        }
        addFilterCriteria(criteria); // TODO : Don't need this for the
        // preview service call.
        addSortCriteria(criteria);
        criteria.setPageSize(this.pageSize);
        criteria.setPageNumber(getPage() - 1);
        return criteria;
    }

    protected void addSortCriteria(WorkListCriteria criteria) {
        for (Iterator<String[]> iter = this.sorts.iterator(); iter.hasNext();) {
            String[] sort = iter.next();
            String sortOnColumn = sort[0];
            boolean ascending = sort[1].equals(SORT_DESCENDING) ? false : true;
            if (logger.isInfoEnabled()) {
                logger.info("Adding sort criteria " + sortOnColumn + " "
                        + (ascending ? "ascending" : "descending"));
            }
            criteria.addSortCriteria(sortOnColumn, ascending);
        }
    }

    protected void addFilterCriteria(WorkListCriteria criteria) {
        for (Iterator<String> iter = this.filters.keySet().iterator(); iter
                .hasNext();) {
            String filterName = iter.next();
            String filterValue = this.filters.get(filterName);
            if (logger.isInfoEnabled()) {
                logger.info("Adding filter criteria " + filterName + " : "
                        + filterValue);
            }
            criteria.addFilterCriteria(filterName, filterValue);
        }
    }

    //When part off is enabled.
	public int getNoOfPartsWithActionTaken(
			OEMPartReplacedBean partReplacedBean, String status,
			boolean onlySelected) {
		int countOfPartsWithStatus = 0;
		Iterator<PartTaskBean> partTaskIttr = partReplacedBean
				.getPartReturnTasks().iterator();
		while (partTaskIttr.hasNext()) {
			PartTaskBean partTaskBean = partTaskIttr.next();
			if (onlySelected) {
				if (partTaskBean.isSelected()
						&& (partTaskBean.getActionTaken()
								.equalsIgnoreCase(status)))
					countOfPartsWithStatus++;
			} else if (partTaskBean.getActionTaken().equalsIgnoreCase(status))
				countOfPartsWithStatus++;

		}
		return countOfPartsWithStatus;

	}

	public int getNoOfPartsWithActionTaken(
		List<PartTaskBean> partTaskBeans, String status,
			boolean onlySelected) {
		int countOfPartsWithStatus = 0;
		Iterator<PartTaskBean> partTaskIttr = partTaskBeans.iterator();
		while (partTaskIttr.hasNext()) {
			PartTaskBean partTaskBean = partTaskIttr.next();
			if (onlySelected) {
				if (partTaskBean.isSelected()
						&& (partTaskBean.getActionTaken()
								.equalsIgnoreCase(status)))
					countOfPartsWithStatus++;
			} else if (partTaskBean.getActionTaken().equalsIgnoreCase(status))
				countOfPartsWithStatus++;

		}
		return countOfPartsWithStatus;

	}

	  /*This takes into consideration those beans that are received and inspected at the same time by the receiver
	 who has inspector role as well .*/
	public int getNoOfPartsWithActionTakenForReceive( List<PartTaskBean> partTaskBeans, String status,
			boolean onlySelected) {
		int countOfPartsWithStatus = 0;
		Iterator<PartTaskBean> partTaskIttr = partTaskBeans.iterator();
		while (partTaskIttr.hasNext()) {
			PartTaskBean partTaskBean = partTaskIttr.next();
			if (onlySelected) {
				if (partTaskBean.isSelected()
						&& ((partTaskBean.getActionTaken()
								.equalsIgnoreCase(status))|| partTaskBean.isToBeInspected()))
					countOfPartsWithStatus++;
			} else if (partTaskBean.getActionTaken().equalsIgnoreCase(status))
				countOfPartsWithStatus++;

		}
		return countOfPartsWithStatus;

	}

	public List<PartTaskBean> getBeansForShipmentGeneration(
			List<PartTaskBean> selectedParts) {
		List<PartTaskBean> partTaskBeans = new ArrayList<PartTaskBean>();
		/* List<Claim> claimCount = new ArrayList<Claim>(); */
		for (PartTaskBean partTaskBean : selectedParts) {

			if (PartReturnTaskTriggerStatus.TO_GENERATE_SHIPMENT
					.equals(partTaskBean.getPartReturn().getTriggerStatus())) {
				partTaskBeans.add(partTaskBean);
				/* claimCount.add(partTaskBean.getClaim()); */
				/*
				 * distinctClaims.add(partTaskBean);
				 * System.out.println(distinctClaims);
				 */
			}
		}
		return partTaskBeans;
	}

    public List<PartTaskBean> getBeansToBeEnded(List<PartTaskBean> selectedParts) {
        List<PartTaskBean> partTaskBeans = new ArrayList<PartTaskBean>();

        for (PartTaskBean partTaskBean : selectedParts) {


            if (PartReturnTaskTriggerStatus.TO_BE_ENDED.equals(partTaskBean
                    .getPartReturn().getTriggerStatus())) {
                partTaskBeans.add(partTaskBean);

            }
        }
        return partTaskBeans;
    }

    public boolean getIsSwitchViewEnabled() {
        return (this.switchButtonActionName != null);
    }

    public List<ClaimWithPartBeans> getClaimWithPartBeans() {
        return this.claimWithPartBeans;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setPartReturnFields(Map partReturnFields) {
        this.partReturnFields = partReturnFields;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public void setPartReturnWorkListItemService(
            PartReturnWorkListItemService partReturnWorkListItemService) {
        this.partReturnWorkListItemService = partReturnWorkListItemService;
    }

    public void setPartReturnWorkListService(
            PartReturnWorkListService partReturnWorkListService) {
        this.partReturnWorkListService = partReturnWorkListService;
    }

    public PartReturnWorkListItemService getPartReturnWorkListItemService() {
        return this.partReturnWorkListItemService;
    }

    public PartReturnWorkListService getPartReturnWorkListService() {
        return this.partReturnWorkListService;
    }

    public void setWorkListItemService(WorkListItemService workListItemService) {
        this.workListItemService = workListItemService;
    }

    public List<OEMPartReplacedBean> getPartReplacedBeans() {
        return this.partReplacedBeans;
    }

    public List<OEMPartReplacedBean> getPartReplacedBeansAfterSetting(){//To do at client side
    	for (OEMPartReplacedBean partReplacedBean : this.partReplacedBeans) {
            partReplacedBean.setSelected(false);
		   for(PartTaskBean partTaskBean : partReplacedBean.getPartReturnTasks()){
			   if(partTaskBean != null && partTaskBean.isSelected())
			   {    partReplacedBean.setSelected(true);

				 }
			   if(partTaskBean != null  && partTaskBean.isToBeInspected()){
				   partReplacedBean.setToBeInspected( true);
			   }
			   }
    }
    	return this.partReplacedBeans;
    }

    public void setPartReplacedBeans(List<OEMPartReplacedBean> partReplacedBeans) {
        this.partReplacedBeans = partReplacedBeans;
    }

    public String getShipmentIdString() {
        return this.shipmentIdString;
    }

    public void setShipmentIdString(String shipmentIdString) {
        this.shipmentIdString = shipmentIdString;
    }

    public List<PartsWithDealerBeans> getShipmentsForPrintTag() {
        return this.shipmentsForPrintTag;
    }

    public WorkListItemService getWorkListItemService() {
        return this.workListItemService;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public String getActionUrl() {
        return this.actionUrl;
    }

    public String getSwitchButtonActionName() {
        return this.switchButtonActionName;
    }

    public void setSwitchButtonActionName(String viewButton) {
        this.switchButtonActionName = viewButton;
    }

    public String getSwitchButtonTabLabel() {
        return this.switchButtonTabLabel;
    }

    public void setSwitchButtonTabLabel(String switchButtonTabLabel) {
        this.switchButtonTabLabel = switchButtonTabLabel;
    }

    public void prepare() throws Exception {
        getPartReplacedBeans();
    }

    public boolean isClaimDenied() {
        return this.isClaimDenied;
    }

    public boolean isNotShipmentView() {
    	return !"shipment".equals(getInboxViewType());
    }

    public void setClaimDenied(boolean isClaimDenied) {
        this.isClaimDenied = isClaimDenied;
    }

    public List<String> getClaimsDenied() {
        return this.claimsDenied;
    }

    public void setClaimsDenied(List<String> claimsDenied) {
        this.claimsDenied = claimsDenied;
    }

    public void processForRejectedPartInboxFlow(PartTaskBean partTaskBean) {
        Claim claim = partTaskBean.getClaim();
        if (claim != null) {
            ClaimState state = claim.getState();
            if (state.ordinal() > ClaimState.PROCESSOR_REVIEW.ordinal()
                    && state.ordinal() < ClaimState.PENDING_PAYMENT_RESPONSE
                    .ordinal()
                    && !(state.equals(ClaimState.REJECTED_PART_RETURN))) {
                TaskInstance taskInstance = this.workListItemService
                        .findTaskForClaimWithTaskName(claim.getId(),
                                "PartsReturnScheduler");
                if(null != taskInstance){
                    claim.setState(ClaimState.REJECTED_PART_RETURN);
                    this.workListItemService.endTaskWithTransition(taskInstance,
                        "goToRejectedPartsInbox");
                }
            }
        }
    }

    public void processForOnHoldForPartReturnInboxFlow(PartTaskBean partTaskBean) {
        Claim claim = partTaskBean.getClaim();
        List<OEMPartReplaced> parts = claim.getServiceInformation()
                .getServiceDetail().getReplacedParts();
        for (OEMPartReplaced part : parts) {
            List<PartReturn> partReturns = part.getPartReturns();
            if (partReturns == null || partReturns.size() == 0) {
                continue;
            }
            for (PartReturn partReturn : partReturns) {
                InspectionResult inspectionResult = partReturn.getInspectionResult();
                if (inspectionResult == null && !(PartReturnStatus.CANNOT_BE_SHIPPED.equals(partReturn.getStatus()) ||
                        PartReturnStatus.REMOVED_BY_PROCESSOR.equals(partReturn.getStatus()))) {
                    return;
                }
            }
        }
        if (claim != null) {
            if (ClaimState.ON_HOLD_FOR_PART_RETURN.equals(claim.getState())) {
                TaskInstance taskInstance = this.workListItemService
                        .findTaskForClaimForPartReturn(claim.getId());
                if (taskInstance != null) {
                    this.workListItemService.endTaskWithTransition(taskInstance,
                            "toProcessorReview");
                }
            }
        }

    }

    public String getOEMPartCrossRefForDisplay(Item item, Item oemDealerItem,
                                               boolean isNumber, Organization organization) {
        Organization loggedInUserOrganization = getLoggedInUser().getBelongsToOrganization();
        return catalogService.findOEMPartCrossRefForDisplay(item,oemDealerItem,
                                    isNumber, organization, loggedInUserOrganization, Dealership.class);
    }
    
	public String getOEMPartCrossRefForDisplay(Item item, Item oemDealerItem,
			boolean isNumber, Organization dealership, String brand) {
		if (brand != null && !brand.isEmpty() && isNumber && isLoggedInUserADealer()) {
			return item.getBrandItemNumber(brand);
		}
		Organization loggedInUserOrganization = getLoggedInUser()
				.getBelongsToOrganization();
		return catalogService.findOEMPartCrossRefForDisplay(item,
				oemDealerItem, isNumber, dealership, loggedInUserOrganization,
				Dealership.class);
	}
    public void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public PartReturnService getPartReturnService() {
        return partReturnService;
    }

    public void setPartReturnService(PartReturnService partReturnService) {
        this.partReturnService = partReturnService;
    }

    public void setPartReplacedService(PartReplacedService partReplacedService) {
        this.partReplacedService = partReplacedService;
    }


	private void populatePrintShipmentDetails(List<PartsWithDealerBeans> shipmentsGeneratedForPrint) {
		for (PartsWithDealerBeans partsWithDealerBeans : shipmentsGeneratedForPrint) {
			Map<Claim, List<PartTaskBean>> distinctClmMap = new HashMap<Claim, List<PartTaskBean>>();
			List<PrintShipmentVO> shipmentList = new ArrayList<PrintShipmentVO>();
			List<PartTaskBean> arrayForClaim = null;
			// For each shipment get all part task beans and group it according
			// to claims.(stored in distinctClmMap)
			for (PartTaskBean partTaskBean : partsWithDealerBeans.getPartTaskBeans()) {
				if (distinctClmMap.get(partTaskBean.getClaim()) == null) {
					arrayForClaim = new ArrayList<PartTaskBean>();
					arrayForClaim.add(partTaskBean);
					distinctClmMap.put(partTaskBean.getClaim(), arrayForClaim);
				} else {
					List<PartTaskBean> arrayForClaims = distinctClmMap.get(partTaskBean.getClaim());
					arrayForClaims.add(partTaskBean);
					distinctClmMap.put(partTaskBean.getClaim(), arrayForClaims);
				}

			}
			// For each distinct claim in shipment create a PrintShipmentVo
			// instance
			for (Claim claim : distinctClmMap.keySet()) {
				PrintShipmentVO shipmentView = new PrintShipmentVO();
				shipmentView.setClaim(claim);
				// Populate the partDetails for each PrintShipmentVo
				Map<String, PartReturnVO> partDetails = new HashMap<String, PartReturnVO>();
				for (PartTaskBean partTaskBean : distinctClmMap.get(claim)) {
					if (partTaskBean.getClaim().getId() == claim.getId()) {
						OEMPartReplaced part = partTaskBean.getPart();
						String partNumber = part.getItemReference().getReferredItem().getNumber();
						if (partDetails.get(part.getItemReference().getReferredItem().getNumber()) == null) {
							PartReturnVO partReturnVO = new PartReturnVO(partNumber);
							if(StringUtils.hasText(part.getSerialNumber())){
							partReturnVO.setComponentSerialNumber(part.getSerialNumber());
							}
							partReturnVO.setNumberOfParts(1);
							partReturnVO.setDescription(part.getItemReference().getReferredItem().getDescription());
							partDetails.put(partNumber, partReturnVO);
						} else {
							PartReturnVO partReturnVO = partDetails.get(partNumber);
							if(StringUtils.hasText(part.getSerialNumber())){
								partReturnVO.setComponentSerialNumber(part.getSerialNumber());
								}
							partReturnVO.setNumberOfParts(partReturnVO.getNumberOfParts() + 1);

							partDetails.put(partNumber, partReturnVO);
						}
					}
				}

				shipmentView.setPartReturnVOList(partDetails.values());
				shipmentList.add(shipmentView);
			}
			Collections.sort(shipmentList);
			partsWithDealerBeans.setClaimsInShipment(shipmentList);
		}
	}




	public Boolean isDisabledBasedOnPartsClaim(Claim claim) {
		Boolean isDisabledBasedOnPartsClaim = false;
		if (claim != null && claim.getType() != null && claim.getBusinessUnitInfo() != null) {
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
			if (ClaimType.PARTS.getType().equals(claim.getType().getType())
					&& (!configParamService.getBooleanValue(ConfigName.BUPART_REPLACEABLEBY_NONBUPART.getName()))) {
				PartsClaim partsClaim = new HibernateCast<PartsClaim>().cast(claim);
				if (!partsClaim.getPartInstalled()) {
					isDisabledBasedOnPartsClaim = true;
				}
			}
		}
		return isDisabledBasedOnPartsClaim;
	}



	protected Boolean checkConfigurationEnabledBasedOnOEMPartBeans(List<OEMPartReplacedBean> oemPartBeans,String configName) {
		boolean enabled = false;
		try {
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(oemPartBeans
					.get(0).getClaim().getBusinessUnitInfo().getName());
			enabled = configParamService.getBooleanValue(configName);
		} catch (Exception e) {
			logger.debug("error.invalid.data.forSelected.part");
		}
		 return enabled;
	}

	protected Boolean checkConfigurationEnabledBasedOnClaimWithPartBeans(List<ClaimWithPartBeans> claimPartBeans,String configName) {
		boolean enabled = false;
		try {
			SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claimPartBeans.get(0).getClaim().getBusinessUnitInfo().getName());
			enabled = configParamService.getBooleanValue(configName);
		} catch (Exception e) {
			logger.debug("error.invalid.data.forSelected.part");
		}
		 return enabled;
	}



    public Boolean isTaskShipmentGenerated(){
    	return this.taskName.equals(WorkflowConstants.SHIPMENT_GENERATED);
    }

    public Boolean isTaskPartsShipped(){
    	return this.taskName.equals(WorkflowConstants.PARTS_SHIPPED);
    }

    public Boolean isTaskDuePartsReceipt(){
    	return this.taskName.equals(WorkflowConstants.DUE_PARTS_RECEIPT);
    }

    public Boolean isTaskDuePartsInspection(){
    	return this.taskName.equals(WorkflowConstants.DUE_PARTS_INSPECTION);
    }

    public PartReplacedService getPartReplacedService() {
        return partReplacedService;
    }

    public List<Document> getAttachments() {
        return attachments;
    }


    public void setAttachments(List<Document> attachments) {
        this.attachments = attachments;
    }

    public ClaimService getClaimService() {
        return claimService;
    }

    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public String getClaimID() {
        return claimID;
    }

    public void setClaimID(String claimID) {
        this.claimID = claimID;
    }

    public ConfigParamService getConfigParamService() {
        return configParamService;
    }

    public void setConfigParamService(ConfigParamService configParamService) {
        this.configParamService = configParamService;
    }

    /**
     * For now creating events for rejected parts at claim level. If this works, can add
     * part level information.
     *
     * @param partTaskBeans
     */
    public void createEventsForRejectedPartsOnClaim(List<PartTaskBean> partTaskBeans) {
        HashMap<String, Integer> claimsWithRejectedParts = new HashMap<String, Integer>();
        Claim currentClaim = null;
        for (PartTaskBean partTaskBean : partTaskBeans) {
            if (REJECT.equals(partTaskBean.getActionTaken())) {
                currentClaim = partTaskBean.getClaim();
                if (currentClaim != null) {
                    String claimNumber = currentClaim.getClaimNumber();
                    if (claimsWithRejectedParts.containsKey(claimNumber)) {
                        // this claim number exists, an event has been created, hence do nothing.
                    } else {
                        // claim number does not exist, create an event, and
                        // add it to the map so that the mails are not duplicated for the same claim.
                        eventService.createEvent("partReturn",
                                EventState.PARTS_REJECTED_ON_CLAIM,
                                currentClaim.getId());
                        claimsWithRejectedParts
                                .put(claimNumber, new Integer(1));
                    }
                }
            }
        }
    }

    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    public EventService getEventService() {
        return eventService;
    }


    public Map<ShipmentStatus,String> getShipmentStatusList(){
      this.shipmentStatus=new TreeMap<ShipmentStatus, String>();
  	  this.shipmentStatus.put(ShipmentStatus.GENERATE_SHIPMENT,ShipmentStatus.GENERATE_SHIPMENT.getStatus());
  	  this.shipmentStatus.put(ShipmentStatus.CANNOT_SHIP,ShipmentStatus.CANNOT_SHIP.getStatus());
  	  return this.shipmentStatus;
    }

    public void setReceiptStatus(Map<ReceiptStatus,String> receiptStatus) {
        this.receiptStatus = receiptStatus;
    }

    public void setShipmentStatus(Map<ShipmentStatus,String> shipmentStatus) {
        this.shipmentStatus = shipmentStatus;
    }

    public void setInspectionStatus(Map<InspectionStatus,String> inspectionStatus) {
        this.inspectionStatus = inspectionStatus;
    }

    public Map<InspectionStatus,String> getInspectionStatusList(){
          this.inspectionStatus=new TreeMap<InspectionStatus, String>();
    	  this.inspectionStatus.put(InspectionStatus.ACCEPT,InspectionStatus.ACCEPT.getStatus());
    	  this.inspectionStatus.put(InspectionStatus.REJECT,InspectionStatus.REJECT.getStatus());
    	  return this.inspectionStatus;
      }


    /*
     * If the function name is given as getReceiptStatus() Struts is not able to recognize the list key
     * as a map(in <s:select list> ) ie If both the 'name' and the 'list' have the same name , the <s:select list does
     * not work!!.If you have an answer as  to why Struts behaves so  , please feel free to explain.:)
     *
     * Thus changed the function name from getReceiptStatus() to getReceiptStatusList()
     */
  public Map<ReceiptStatus,String> getReceiptStatusList(){
	  this.receiptStatus=new TreeMap<ReceiptStatus, String>();
	  this.receiptStatus.put(ReceiptStatus.RECEIVED,ReceiptStatus.RECEIVED.getStatus());
	  this.receiptStatus.put(ReceiptStatus.NOT_RECEIVED,ReceiptStatus.NOT_RECEIVED.getStatus());
	  return this.receiptStatus;
  }

    public String getPartReturnClaimDenialWindowPeriodForBU(String buName)
    {
    	String windowPeriod = null;
    	Map<String, List<Object>> daysBUMapConsideredForDenying = configParamService
			.getValuesForAllBUs(ConfigName.DAYS_FOR_WAITING_FOR_PART_RETURNS_CLAIM_DENIED
					.getName());
    	if (daysBUMapConsideredForDenying != null && daysBUMapConsideredForDenying.get(buName) != null)
    	{
    		windowPeriod = (String)daysBUMapConsideredForDenying.get(buName).get(0);
    	}
    	return windowPeriod;
    }

    public String getShipmentGenClaimDenialWindowPeriodForBU(String buName)
    {
    	String windowPeriod = null;
    	Map<String, List<Object>> daysBUMapConsideredForDenying = configParamService
			.getValuesForAllBUs(ConfigName.DENY_SHIPMENT_GENERATED_CLAIMS_CROSSED_WINDOW_PERIOD_DAYS
					.getName());
    	if (daysBUMapConsideredForDenying != null && daysBUMapConsideredForDenying.get(buName) != null)
    	{
    		windowPeriod = (String)daysBUMapConsideredForDenying.get(buName).get(0);
    	}
    	return windowPeriod;
    }

    public List<ListOfValues> getLovsForClass(String className) {
    	Claim claim = this.claimService.findClaim(claimWithPartBeans.get(0).getClaim().getId());
    	return claimService.getLovsForClass(className, claim);
    }

    protected Collection<List<PartTaskBean>> getTaskInstanceGroupsByWpra(List<PartTaskBean> partTaskBeans) {
          if (partTaskBeans == null) {
              return null;
          }

          Map<String, List<PartTaskBean>> locationToPartTaskBean = new HashMap<String, List<PartTaskBean>>();
          for (PartTaskBean instance : partTaskBeans) {
              if (PartReturnTaskTriggerStatus.WPRA_GENERATED.equals(instance.getPartReturn().getTriggerStatus())) {
                  StringBuilder keyId = new StringBuilder(String.valueOf(instance.getClaim().getForDealer().getId())).append(String.valueOf(instance.getPartReturn().getReturnLocation().getId()));
                  List<PartTaskBean> partBeanForLocation = locationToPartTaskBean.get(keyId.toString());
                  if (partBeanForLocation == null) {
                      partBeanForLocation = new ArrayList<PartTaskBean>();
                      locationToPartTaskBean.put(keyId.toString(), partBeanForLocation);
                  }
                  partBeanForLocation.add(instance);
              }
          }
          return locationToPartTaskBean.values();
      }
    
    public Map<String,Map<String,List<String> >> setEmailDetailsForWPRA(Map<String,Map<String,List<String> >> map,Wpra wpra,OEMPartReplacedBean partReplacedBean){
   	 Map<String, List<String>> mapClaimPartNumbers = new HashMap<String, List<String>>();
   	 List<String> partNumbers = new ArrayList<String>();
   		 if(StringUtils.hasText(wpra.getWpraNumber())){
   	       if(!CollectionUtils.isEmpty((partReplacedBean.getPartReturnTasks()))){
   		    List<PartTaskBean> partReturnTasks = partReplacedBean.getPartReturnTasks();
   			 for(PartTaskBean partTaskBean:partReturnTasks){
   	         	   if(partTaskBean.getClaim()!=null){
   	         		 Claim claim = partTaskBean.getClaim();
   	         		 if(claim.getServiceInformation()!=null && claim.getServiceInformation().getServiceDetail()!=null 
   	     			 && !CollectionUtils.isEmpty(claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced())){ 
   	     			 List<OEMPartReplaced> allOEMPartsReplaced = claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced();
   	         		 if(!CollectionUtils.isEmpty(allOEMPartsReplaced)){
   	         			for(OEMPartReplaced oemPartReplaced : allOEMPartsReplaced){
   	         				if(isWpraForPart(wpra.getParts(),oemPartReplaced)&&
   	         						oemPartReplaced.getItemReference()!=null && oemPartReplaced.getItemReference().getReferredItem()!=null){
   	         			     	Item referredItem = oemPartReplaced.getItemReference().getReferredItem();
   	         			     	String brandPartNumber = referredItem.getBrandItemNumber(claim.getBrand());
   	         					partNumbers.add(brandPartNumber);	
   	         		     } 
   	         		    }	 
   	         		   }
   	         	     }
   	         	    StringBuilder claimDetails = new StringBuilder();
   	         	    claimDetails.append(claim.getClaimNumber().toString()).append("#");
   	         	    if(claim.getFiledBy()!=null && StringUtils.hasText(claim.getFiledBy().getName())){
   	         	     claimDetails.append("filedBy=").append(claim.getFiledBy().getName());	
   	         	    }
   	         	   if(claim.getFiledBy()!=null && StringUtils.hasText(claim.getFiledBy().getEmail())){
   	         	     claimDetails.append("#Email=").append(claim.getFiledBy().getEmail());
   	         	    }
   	         		mapClaimPartNumbers.put(claimDetails.toString(), partNumbers);
   	         	   } 
   	             }
   			    
   	           }
   	    }
   		if(map.containsKey(wpra.getWpraNumber())){
   			Map<String, List<String>> mapExistingClaimPartNumbers = map.get(wpra.getWpraNumber());
   			mapClaimPartNumbers.putAll(mapExistingClaimPartNumbers);
   		    map.put(wpra.getWpraNumber(), mapClaimPartNumbers);
   		} 
   		else{
   		   map.put(wpra.getWpraNumber(), mapClaimPartNumbers);
   		 }
   	    return  map;
    }
    
    private boolean isWpraForPart(List<PartReturn> parts,OEMPartReplaced oemPartReplaced){
   	 for(PartReturn partReturn :  parts){
   		 if(partReturn.getOemPartReplaced().getId().longValue()==oemPartReplaced.getId().longValue()){
   			 return true;
   		 }
   	 }
   	 return false;
   	 
    }
    
	protected boolean isPositiveNumber(String str) {
		boolean isPositiveNumber = false;
		try {
			if (StringUtils.hasText(str)) {
				double loadDimension = Double.parseDouble(str);
				if(loadDimension >= 0){
				  isPositiveNumber = true;
		}
	}
		} catch (NumberFormatException e) {
			isPositiveNumber = false;
		}
		return isPositiveNumber;
	}
    
	public WarehouseService getWarehouseService() {
		return warehouseService;
	}

	public void setWarehouseService(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}
	
	public String getSwitchButtonLabel() {
		if (showWPRALabel()) {
			return switchButtonActionName + "_WPRA";
		}
		return switchButtonActionName;
	}
	
	private String shippingCommentsInClaim(Claim claim){
    	String shippingCommentsInClaim = claim.getPartReturnCommentsToDealer();
    	if(StringUtils.hasText(shippingCommentsInClaim)){
    		return shippingCommentsInClaim;
    	}
    	for(ClaimAudit audit : claim.getClaimAudits()){
    		if(StringUtils.hasText(audit.getPartReturnCommentsToDealer())){
    			shippingCommentsInClaim = audit.getPartReturnCommentsToDealer();
    		}
    	}
    	return shippingCommentsInClaim;
    }
    
    private String shippingCommentsInRecClaim(Claim claim, OEMPartReplaced replacedPart){
    	String shippingCommentsInRecClaim = null;
    	RecoveryClaim matchingRecClaim = getMatchingRecClaim(claim, replacedPart);
    	if(matchingRecClaim != null){
    		shippingCommentsInRecClaim = matchingRecClaim.getPartReturnCommentsToDealer();
    		if(StringUtils.hasText(shippingCommentsInRecClaim)){
        		return shippingCommentsInRecClaim;
        	}
    		for(RecoveryClaimAudit recClaimAudit : matchingRecClaim.getRecoveryClaimAudits()){
    			if(StringUtils.hasText(recClaimAudit.getPartReturnCommentsToDealer())){
    				shippingCommentsInRecClaim = recClaimAudit.getPartReturnCommentsToDealer();
    			}
    		}
    	}
    	return shippingCommentsInRecClaim;
    }
    
    private RecoveryClaim getMatchingRecClaim(Claim claim, OEMPartReplaced replacedPart){
    	if(claim.getRecoveryClaims() == null || claim.getRecoveryClaims().isEmpty()){
    		return null;
    	}
    	for(RecoveryClaim recClaim : claim.getRecoveryClaims()){
    		for(RecoverablePart recoverablePart : recClaim.getRecoveryClaimInfo().getRecoverableParts()){
    			if(recoverablePart.getOemPart().equals(replacedPart)){
    				return recClaim;
    			}
    		}
    	}
    	return null;
    }

	/**
	 * @return the shippingComments
	 */
	public String getShippingComments() {
		return shippingComments;
	}

	/**
	 * @param shippingComments the shippingComments to set
	 */
	public void setShippingComments(String shippingComments) {
		this.shippingComments = shippingComments;
	}

	/**
	 * @return the shipment
	 */
	public Shipment getShipment() {
		return shipment;
	}

	/**
	 * @param shipment the shipment to set
	 */
	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
	}

	/**
	 * @return the domainRepository
	 */
	public DomainRepository getDomainRepository() {
		return domainRepository;
	}

	/**
	 * @param domainRepository the domainRepository to set
	 */
	public void setDomainRepository(DomainRepository domainRepository) {
		this.domainRepository = domainRepository;
	}

	/**
	 * @return the supplierRecoveryWorkListDao
	 */
	public SupplierRecoveryWorkListDao getSupplierRecoveryWorkListDao() {
		return supplierRecoveryWorkListDao;
	}

	/**
	 * @param supplierRecoveryWorkListDao the supplierRecoveryWorkListDao to set
	 */
	public void setSupplierRecoveryWorkListDao(
			SupplierRecoveryWorkListDao supplierRecoveryWorkListDao) {
		this.supplierRecoveryWorkListDao = supplierRecoveryWorkListDao;
	}

	

}