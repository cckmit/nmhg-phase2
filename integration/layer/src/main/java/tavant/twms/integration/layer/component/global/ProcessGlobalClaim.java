package tavant.twms.integration.layer.component.global;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlOptions;
import org.springframework.util.StringUtils;

import tavant.twms.domain.bu.ConfigValueService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.CurrencyConversionAdvice;
import tavant.twms.domain.claim.NonOEMPartReplaced;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.PartPaymentInfo;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.common.Constants;
import tavant.twms.domain.common.Label;
import tavant.twms.domain.inventory.InventoryType;
import tavant.twms.domain.orgmodel.Dealership;
import tavant.twms.domain.orgmodel.OrgService;
import tavant.twms.domain.policy.PolicyDefinition;
import tavant.twms.domain.policy.PolicyDefinitionRepository;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.infra.HibernateCast;
import tavant.twms.integration.layer.IntegrationPropertiesBean;
import tavant.twms.integration.layer.constants.IntegrationConstants;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.integration.layer.util.IntegrationLayerUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.ApplicationArea;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.ApplicationArea.Sender;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.CurrencyCode;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.DebitManufacturingPlant;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.LineAttribute6;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.LineAttribute6.SerialNumbers;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.LineAttribute6.SerialNumbers.UnitDetail;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.LineItems;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.LineItems.LineItem;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.LineItems.LineItem.LineAttribute3;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.LineItems.LineItem.SubItem;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.LineItems.LineItem.SubItem.LineAttribute4;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.NCRFlag;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.NCRWith30Days;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.SMRFlag;
import com.nmhg.warrantyclaimcreditsubmission.CreditSubmission.DataArea.Invoice.SerializedClaim;
import com.nmhg.warrantyclaimcreditsubmission.MTCreditSubmissionDocument;

public class ProcessGlobalClaim extends IntegrationConstants {

    private static Logger logger = Logger.getLogger(ProcessGlobalClaim.class
            .getName());

    IntegrationPropertiesBean integrationPropertiesBean;
    
    CurrencyConversionAdvice claimCurrencyConversionAdvice;
    
    ConfigValueService configValueService;
    
    private PolicyDefinitionRepository policyDefinitionRepository;
    
    

	private OrgService orgService;

    public String syncClaim(Claim claim,boolean isManufactereClaim) {
        if (logger.isInfoEnabled()) {
            logger.info("ProcessGlobalClaim :: syncClaim , The Claim :    "
                    + claim.getClaimNumber());
        }
        return createBodFromObject(claim,isManufactereClaim);
    }

    /**
     * @param claim
     * @return String XML for Warranty claim submission
     */
    private String createBodFromObject(Claim claim,boolean isManufactereClaim) {

        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: createBodFromObject , The Claim :    "
                            + claim.getClaimNumber());
        }
        MTCreditSubmissionDocument creditSubmissionDocumentDTO = MTCreditSubmissionDocument.Factory.newInstance();
        CreditSubmission creditSubmission = CreditSubmission.Factory
                .newInstance();

        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: Create application area , The Claim :    "
                            + claim.getClaimNumber());
        }
        ApplicationArea applicationAreaTypeDTO = createApplicationArea(claim);
        creditSubmission.setApplicationArea(applicationAreaTypeDTO);

        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: Create data area , The Claim :    "
                            + claim.getClaimNumber());
        }
       DataArea doc = DataArea.Factory
                .newInstance();

        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: populateIRInvoiceData , The Claim :    "
                            + claim.getClaimNumber());
        }

        Invoice invoiceTypeDTO = populateInvoiceData(claim,isManufactereClaim);

        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: populatelineItems , The Claim :    "
                            + claim.getClaimNumber());
        }
        LineItems lineItemsTypeDTO = LineItems.Factory.newInstance();
        if(claim.getPayment().getTotalAcceptStateMdtChkbox().equals(true))
		{
        	populatelineItemsForStateMandate(claim,invoiceTypeDTO,lineItemsTypeDTO);	
		}else{
         populatelineItems(claim, invoiceTypeDTO, lineItemsTypeDTO);
		}
        invoiceTypeDTO.setLineItems(lineItemsTypeDTO);
        setCurrencyFeilds(invoiceTypeDTO,claim);
        if(isManufactereClaim){
			setTheAmountForManufacterer(invoiceTypeDTO);
		}
        doc.setInvoice(invoiceTypeDTO);
        creditSubmission.setDataArea(doc);
        creditSubmissionDocumentDTO.setMTCreditSubmission(creditSubmission);
        String xml = creditSubmissionDocumentDTO.xmlText(createXMLOptions());
        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: exiting createBodFromObject , The XML formed is :    ");
            logger.info(xml);
        }
        return xml;

    }


	private void setLineAttribut4(SubItem subItemTypeDTO,String category) {
		if (Section.MEALS.equalsIgnoreCase(category)) {
            subItemTypeDTO.setLineAttribute4(LineAttribute4.MEALS);
        } else if (Section.PARKING.equalsIgnoreCase(category)) {
            subItemTypeDTO.setLineAttribute4(LineAttribute4.PARKING);
        } else if (Section.PER_DIEM.equalsIgnoreCase(category)) {
            subItemTypeDTO.setLineAttribute4(LineAttribute4.PERDIEM);
        } else if (Section.RENTAL_CHARGES.equalsIgnoreCase(category)) {
            subItemTypeDTO.setLineAttribute4(LineAttribute4.RENTALCHARGES);
        } else if (Section.TRAVEL.equalsIgnoreCase(category)) {
            subItemTypeDTO.setLineAttribute4(LineAttribute4.TRAVEL);
        } else if (Section.LOCAL_PURCHASE.equalsIgnoreCase(category)) {
            subItemTypeDTO.setLineAttribute4(LineAttribute4.LOCALPURCHASE);
        } else if (Section.TOLLS.equalsIgnoreCase(category)) {
            subItemTypeDTO.setLineAttribute4(LineAttribute4.TOLLS);
        } else if (Section.OTHER_FREIGHT_DUTY.equalsIgnoreCase(category)) {
            subItemTypeDTO.setLineAttribute4(LineAttribute4.OTHERFREIGHTDUTY);
        } else if ((Section.OTHERS.equalsIgnoreCase(category))) {
            subItemTypeDTO.setLineAttribute4(LineAttribute4.OTHERS);
        }
	}

	private boolean isValidCostCategory(String category) {
		if (Section.MEALS.equalsIgnoreCase(category)
				|| Section.PARKING.equalsIgnoreCase(category)
				|| Section.PER_DIEM.equalsIgnoreCase(category)
				|| Section.RENTAL_CHARGES.equalsIgnoreCase(category)
				|| Section.LOCAL_PURCHASE.equalsIgnoreCase(category)
				|| Section.TOLLS.equalsIgnoreCase(category)
				|| isTravelCategory(category)
				||isOthersCategory(category)) {
			return true;
		}
		return false;
	}

	private void addLaborItemForStateMandate(Claim claim,
			SortedMap<Integer, LineItem> lineItemDtoList,
			LineItemGroup lineItemGroup, LineItem lineItemTypeDTO,
			String category) {
		if (logger.isInfoEnabled()) {
			logger.info("ProcessGlobalClaim :: start addLaborItem:" + category);
		}
		lineItemTypeDTO.setLineAttribute3(LineAttribute3.LABOR);
		lineItemTypeDTO.setTotalAmount(lineItemGroup
				.getTotalCreditAmount()
				.breachEncapsulationOfAmount().negate());

		lineItemTypeDTO.setAmount(lineItemGroup
				.getGroupTotalStateMandateAmount()
				.breachEncapsulationOfAmount().negate());
		if (logger.isInfoEnabled()) {
			logger.info("ProcessGlobalClaim :: exiting addLaborItem:"
					+ category);
		}
		lineItemDtoList.put(new Integer(LineAttribute3.INT_LABOR),
				lineItemTypeDTO);
	}

	/**
     * @param claim
     * @return Populates the application area of the claim credit submission
     *         message
     */
    private ApplicationArea createApplicationArea(Claim claim) {
    	ApplicationArea applicationAreaTypeDTO = ApplicationArea.Factory
                .newInstance();
        Sender senderTypeDto = Sender.Factory.newInstance();
        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: createApplicationArea , The Claim :    "
                            + claim.getClaimNumber());
        }
        int auditOrder = claim.getClaimAudits().size() + 1;
        int asciiA = 65;
        int asciiValueToAppend = asciiA + (auditOrder - 1);
        senderTypeDto.setReferenceId(claim.getClaimNumber() + "_"
                + (char) asciiValueToAppend);
        {
            logger
                    .info("ProcessGlobalClaim :: exiting createApplicationArea , The Claim :    "
                            + claim.getClaimNumber());
        }
        populateTaskLogicalId(senderTypeDto, claim);
        applicationAreaTypeDTO.setSender(senderTypeDto);
        //Date date = getNextCreditSubmissionDate(claim);
        Date date=new Date();
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
		applicationAreaTypeDTO.setCreationDateTime(CalendarUtil
				.convertToDateTimeToString(calender.getTime()));
        applicationAreaTypeDTO.setInterfaceNumber(integrationPropertiesBean.getInterfaceNumberForCreditSubmission());
        applicationAreaTypeDTO.setBODId(integrationPropertiesBean.getBodIdForCreditSubmission());
        return applicationAreaTypeDTO;
    }

    /**
     * @param claim
     * @return Populates the Inventory and warranty generic information
     */
    private Invoice populateInvoiceData(Claim claim,boolean isManufactereClaim) {
    	Invoice invoiceTypeDTO = Invoice.Factory
                .newInstance();

        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: start populateInvoiceData() for the Claim :  "
                            + claim.getClaimNumber());
        }
        // Line context
        invoiceTypeDTO.setLineContext(WARRANTY);

        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: populateInvoiceData() audit size , The Claim :    "
                            + claim.getClaimAudits().size());
        }
        // Claim number along with audit
        int auditOrder = claim.getClaimAudits().size() + 1;
        int asciiA = 65;
        int asciiValueToAppend = asciiA + (auditOrder - 1);
        invoiceTypeDTO.setLineAttribute1(claim.getClaimNumber() + "_"
                + (char) asciiValueToAppend);

        // Batch Source Name
        invoiceTypeDTO.setBatchSourceName(BATCH_SOURCE_NAME_WARRANTY);

        // Ship Site id
        if (StringUtils.hasText(claim.getServicingLocation()
                .getSiteNumber())) {
            invoiceTypeDTO.setShipSiteId(IntegrationLayerUtil.getDealerSiteNumber(claim
					.getServicingLocation().getSiteNumber()));
        }
        // Sales Order line - always passed as "1"
        invoiceTypeDTO.setSalesOrderLine(SALES_ORDER_LINE_DEAFAULT_VALUE);

        // Part item number - only for Parts claim
        // need to verify logic with ramalakshmi/pradyout
		if (claim.isOfType(ClaimType.PARTS)) {
			if (claim.getPartItemReference() != null
					&& claim.getPartItemReference().getUnserializedItem() != null) {
				invoiceTypeDTO.setPartItemNumber(claim.getPartItemReference()
						.getUnserializedItem()
						.getBrandItemNumber(claim.getBrand()));
			} else if (claim.getServiceInformation() != null
					&& claim.getServiceInformation().getCausalPart() != null
					&& claim.getBrand() != null) {

				invoiceTypeDTO.setPartItemNumber(claim.getServiceInformation()
						.getCausalPart().getBrandItemNumber(claim.getBrand()));
			} else if (claim.getPartItemReference() != null
					&& claim.getPartItemReference().getReferredInventoryItem() != null
					&& claim.getPartItemReference().getReferredInventoryItem()
							.getOfType() != null && claim.getBrand() != null) {
				invoiceTypeDTO.setPartItemNumber(claim.getPartItemReference()
						.getReferredInventoryItem().getOfType()
						.getBrandItemNumber(claim.getBrand()));
			}
		}
        // Claim type
        // This logic need to be here till iri implements campaign to Field Modification
        if (claim.getType().toString().equalsIgnoreCase(CLAIM_CAMPAIGN)) {
            invoiceTypeDTO.setClaimType(Invoice.ClaimType.Enum.forString(CLAIM_FIELD_MODIFICATION));
        } else {
            invoiceTypeDTO.setClaimType(Invoice.ClaimType.Enum.forString(claim.getType().toString().toUpperCase()));
        }

        // Project Code
		if (claim.getAcceptanceReason() != null
				&& claim.getAcceptanceReason().getCode() != null
				&& StringUtils.hasText(claim.getAcceptanceReason().getCode())) {
			String projectCode = null;
			if (claim.getAcceptanceReason().getCode().length() > 19) {
				projectCode = IntegrationLayerUtil.getSubString(claim
						.getAcceptanceReason().getCode(), 19);
			} else {
				projectCode = claim.getAcceptanceReason().getCode();
			}
			invoiceTypeDTO.setProjectCode(projectCode);
		} else {
			invoiceTypeDTO
					.setProjectCode(DEAFULT_PROJECT_CODE_FOR_CLAIM_SUBMISSION);
		}

        // SMR Flag
        if (claim.isServiceManagerRequest()) {
            invoiceTypeDTO.setSMRFlag(SMRFlag.Enum.forString(TRUE));
        } else {
            invoiceTypeDTO.setSMRFlag(SMRFlag.Enum.forString(FALSE));
        }

        // SMR Reason
        if (claim.isServiceManagerRequest() &&
                claim.getReasonForServiceManagerRequest() != null) {
            invoiceTypeDTO.setSMRReason(claim.getReasonForServiceManagerRequest().getCode());
        } else {
            invoiceTypeDTO.setSMRReason(EMPTY_STRING);
        }

        // Selling entity
        if (claim.getSellingEntity() != null
                && StringUtils.hasText(claim.getSellingEntity().getCode())) {
            invoiceTypeDTO.setSellingEntity(claim.getSellingEntity()
                    .getCode());
        }
        // Warranty Type
        setWarrantyAndPolicyDeatilsForClaim(invoiceTypeDTO,claim);
        // Work Order number
         if(claim.getWorkOrderNumber()!=null&&StringUtils.hasText(claim.getWorkOrderNumber())){
        invoiceTypeDTO.setWorkOrderNumber(IntegrationLayerUtil.getSubString(claim.getWorkOrderNumber(),20));
         }else{
        	 invoiceTypeDTO.setWorkOrderNumber(IntegrationLayerUtil.getSubString(claim.getClaimNumber(),20));
         }
        
        // BU Name
		invoiceTypeDTO.setBUName(IntegrationLayerUtil.getBuName(claim.getBusinessUnitInfo().getName()));

        // Invoice Number
        if (StringUtils.hasText(claim.getInvoiceNumber())) {
            invoiceTypeDTO.setInvoiceNo(claim.getInvoiceNumber());
        }

        // Return for Reason
        /**
         * @Todo need to find place holder
         */
        invoiceTypeDTO.setReturnForReason(EMPTY_STRING);


        // Multiple Serial number information
        List<ClaimedItem> claimedItems = claim.getClaimedItems();
        LineAttribute6 lineAttribute6TypeDTO = LineAttribute6.Factory
                .newInstance();
        SerialNumbers serialNumbersTypeDTO = SerialNumbers.Factory
                .newInstance();
        int i = 0;
        boolean isPartsWithOutHost = true;
        UnitDetail[] unitDetailTypeDTOArray = new UnitDetail[claimedItems.size()];
        if (claimedItems == null || claimedItems.size() == 0) {
            UnitDetail unitDetailTypeDTO = UnitDetail.Factory
                    .newInstance();
            if (claim.getSourceWarehouse() != null
                    && StringUtils
                    .hasText(claim.getSourceWarehouse().getName())) {
                unitDetailTypeDTO.setSourceWarehouse(claim.getSourceWarehouse()
                        .getCode());
            }

        }
        for (ClaimedItem claimedItem : claimedItems) {
            UnitDetail unitDetailTypeDTO = UnitDetail.Factory
                    .newInstance();
            if (null != claimedItem.getItemReference()
                    && claimedItem.getItemReference().isSerialized()&&null!=claimedItem
                            .getItemReference().getReferredInventoryItem()) {
                unitDetailTypeDTO.setSerialNumber(claimedItem
                        .getItemReference().getReferredInventoryItem()
                        .getSerialNumber());
                unitDetailTypeDTO.setItemNumber(populateItemNumber(claim, claimedItem));
                unitDetailTypeDTO.setModelNumber(claimedItem.getItemReference()
                        .getUnserializedItem().getModel().getName());
                if (null != claimedItem.getItemReference().getReferredInventoryItem()
                        .getSourceWarehouse()) {
                    unitDetailTypeDTO.setSourceWarehouse(claimedItem.getItemReference().getReferredInventoryItem()
                            .getSourceWarehouse().getCode());
                }

            } else {
                if (claimedItem.getItemReference()!=null&&StringUtils.hasText(claimedItem.getItemReference().getUnszdSlNo())) {
                    unitDetailTypeDTO.setSerialNumber(claimedItem.getItemReference().getUnszdSlNo());
                }
                if (claimedItem.getItemReference()!=null&&claimedItem.getItemReference().getUnserializedItem() != null) {
                    unitDetailTypeDTO.setItemNumber(populateItemNumber(claim, claimedItem));
                }
                if (claimedItem.getItemReference()!=null&&claimedItem.getItemReference().getModel() != null) {
                    unitDetailTypeDTO.setModelNumber(claimedItem.getItemReference()
                            .getModel().getName());
                }

            }
            //Source Warehouse needs to be populated for non serialized claims from claim table
            if (unitDetailTypeDTO.getSourceWarehouse() == null
                    && claim.getSourceWarehouse() != null
                    && StringUtils.hasText(claim.getSourceWarehouse()
                    .getName())) {
                unitDetailTypeDTO.setSourceWarehouse(claim
                        .getSourceWarehouse().getCode());
            }

            unitDetailTypeDTOArray[i++] = unitDetailTypeDTO;
            if (null != unitDetailTypeDTO.getItemNumber() && StringUtils.hasText(unitDetailTypeDTO.getItemNumber())) {
                isPartsWithOutHost = false;
            }
        }
        serialNumbersTypeDTO.setUnitDetailArray(unitDetailTypeDTOArray);
        lineAttribute6TypeDTO.setSerialNumbers(serialNumbersTypeDTO);
        invoiceTypeDTO.setLineAttribute6(lineAttribute6TypeDTO);
        if (!isPartsWithOutHost) {
            // SerializedClaim
            if (claim.getClaimedItems() != null&&claim.getClaimedItems().get(0).getItemReference()!=null
                    && claim.getClaimedItems().get(0).getItemReference()
                    .isSerialized()) {
                invoiceTypeDTO.setSerializedClaim(SerializedClaim.Enum.forString(YES));
            } else {
                invoiceTypeDTO.setSerializedClaim(SerializedClaim.Enum.forString(NO));
            }
        }
        else
        {
        	invoiceTypeDTO.setSerializedClaim(SerializedClaim.Enum.forString(NO));
        }

        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: return from populateInvoiceData() , The Claim :    "
                            + claim.getClaimNumber());
        }
        //added new elements for credit submission
        setNMHGfeilds(invoiceTypeDTO,claim, isManufactereClaim);
        return invoiceTypeDTO;
    }

	private void setNMHGfeilds(Invoice invoiceTypeDTO, Claim claim,
			boolean isManufactereClaim) {
		logger.error("claim.getClaimedItems().get(0).getItemReference()"
				+ claim.getClaimedItems().get(0).getItemReference());
		logger.error("claim.getClaimedItems().get(0).getItemReference()"
				+ claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem());
		setNCRInformation(invoiceTypeDTO, claim, isManufactereClaim);
		setDateFeilds(invoiceTypeDTO, claim);
		if (claim.getForDealer().getAddress() != null
				&& claim.getForDealer().getAddress()
						.getEmailForSapNotifications() != null) {
			invoiceTypeDTO.setEmailAddress(claim.getForDealer().getAddress()
					.getEmailForSapNotifications());
		}
		if (claim.getClaimedItems().get(0).getItemReference() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem().getOperator() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem().getOperator().getAddress()
						.getCountry() != null) {
			invoiceTypeDTO.setShipCustomerCountryCode(claim.getClaimedItems()
					.get(0).getItemReference().getReferredInventoryItem()
					.getOperator().getAddress().getCountry());
		}
		if (StringUtils.hasText(claim.getForDealerShip().getAddress()
				.getCountry())) {
			invoiceTypeDTO.setShipCustomerCountryCode(claim.getForDealerShip()
					.getAddress().getCountry());
		}
		invoiceTypeDTO.setCompanyCode(IntegrationLayerUtil
				.getDealerNumber(claim.getForDealer().getDealerNumber()));
		Dealership dealer=new HibernateCast<Dealership>()
		.cast(claim.getForDealer());
		setMarketingInformation(invoiceTypeDTO, claim, dealer);
		if (dealer.getBusinessArea() != null
				&& StringUtils.hasText(dealer.getBusinessArea()))
			invoiceTypeDTO.setBusinessArea(dealer.getBusinessArea());
		if (dealer.getSellingLocation() != null
				&& StringUtils.hasText(dealer.getSellingLocation()))
			invoiceTypeDTO.setSellingLocation(dealer.getSellingLocation());
	}

	private void setCurrencyFeilds(Invoice invoiceTypeDTO, Claim claim) {
		if (claim.getActiveClaimAudit() != null
				&& claim.getActiveClaimAudit().getExchangeRate() != null) {
			invoiceTypeDTO.setExchangeRate(claim.getActiveClaimAudit()
					.getExchangeRate().toString());
		}
		if (claim.getCurrencyForCalculation() != null
				&& claim.getCurrencyForCalculation().getCurrencyCode() != null) {
			invoiceTypeDTO.setCurrencyCode(CurrencyCode.Enum.forString(claim
					.getCurrencyForCalculation().getCurrencyCode()));
		}
		if (claim.getPayment().getEffectiveAmountToBePaid()
				.breachEncapsulationOfAmount() != null) {
			if (claim.getPayment().getEffectiveAmountToBePaid().isNegative()) {
				invoiceTypeDTO.setTotalClaimAmount(claim.getPayment()
						.getEffectiveAmountToBePaid()
						.breachEncapsulationOfAmount().abs());
			} else {
				invoiceTypeDTO.setTotalClaimAmount(claim.getPayment()
						.getEffectiveAmountToBePaid()
						.breachEncapsulationOfAmount().negate());
			}
		}
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())) {
			if (invoiceTypeDTO.getTotalClaimAmount() != null
					&& invoiceTypeDTO.getTotalClaimAmount().signum() == -1
					&& invoiceTypeDTO.getTotalWarrantyCostAmount() != null) {
				invoiceTypeDTO.setTotalWarrantyCostAmount(invoiceTypeDTO
						.getTotalWarrantyCostAmount().abs());
			} else if (invoiceTypeDTO.getTotalWarrantyCostAmount() != null) {
                if(invoiceTypeDTO
						.getTotalWarrantyCostAmount().negate().signum()!=-1){
                	invoiceTypeDTO.setTotalWarrantyCostAmount(invoiceTypeDTO
    						.getTotalWarrantyCostAmount().negate().negate());
                }else{
				invoiceTypeDTO.setTotalWarrantyCostAmount(invoiceTypeDTO
						.getTotalWarrantyCostAmount().negate());
                }
			}
		}
	}

	private void setDateFeilds(Invoice invoiceTypeDTO, Claim claim) {
		invoiceTypeDTO.setTrxDate(CalendarUtil
				.convertToDateToString(new Date()));
		invoiceTypeDTO.setClaimSubmissionDate(CalendarUtil
				.convertToDateToString((CalendarUtil
						.convertToJavaCalendar(claim.getFiledOnDate()))
						.getTime()));
		if (claim.getClaimedItems().get(0).getItemReference() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem().getShipmentDate() != null) {
			invoiceTypeDTO.setUnitShippedDate(CalendarUtil
					.convertToDateToString(CalendarUtil.convertToJavaCalendar(
							claim.getClaimedItems().get(0).getItemReference()
									.getReferredInventoryItem()
									.getShipmentDate()).getTime()));
		}
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())) {
			if (claim.getClaimedItems().get(0).getItemReference() != null
					&& claim.getClaimedItems().get(0).getItemReference()
							.getReferredInventoryItem() != null
					&& claim.getClaimedItems().get(0).getItemReference()
							.getReferredInventoryItem().getWntyStartDate() != null) {
				invoiceTypeDTO.setInstallationDate(CalendarUtil
						.convertToDateToString(CalendarUtil.convertToJavaCalendar(
								claim.getClaimedItems().get(0).getItemReference()
										.getReferredInventoryItem()
										.getWntyStartDate()).getTime()));
			}
		}
	}

	private void setNCRInformation(Invoice invoiceTypeDTO, Claim claim,
			boolean isManufactereClaim) {
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())) {
			invoiceTypeDTO.setNCRFlag(NCRFlag.Enum.forString(NO));
			invoiceTypeDTO.setNCRWith30Days(NCRWith30Days.Enum.forString(NO));
		} else {
			if (claim.isNcr() != null && claim.isNcr() == true) {
				invoiceTypeDTO.setNCRFlag(NCRFlag.Enum.forString(YES));
			} else {
				invoiceTypeDTO.setNCRFlag(NCRFlag.Enum.forString(NO));
			}
			if (claim.isNcrWith30Days() != null
					&& claim.isNcrWith30Days() == true) {
				invoiceTypeDTO.setNCRWith30Days(NCRWith30Days.Enum
						.forString(YES));
			} else {
				invoiceTypeDTO.setNCRWith30Days(NCRWith30Days.Enum
						.forString(NO));
			}
		}
		if (claim.getClaimedItems().get(0).getItemReference() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem()
						.getManufacturingSiteInventory() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem()
						.getManufacturingSiteInventory().getCode() != null
				&& (invoiceTypeDTO.getNCRFlag().equals(
						NCRFlag.Enum.forString(YES)) || invoiceTypeDTO
						.getNCRWith30Days().equals(
								NCRWith30Days.Enum.forString(YES)))) {
			invoiceTypeDTO.setManufacturingLocation(claim.getClaimedItems()
					.get(0).getItemReference().getReferredInventoryItem()
					.getManufacturingSiteInventory().getCode());
		}
		if (isManufactereClaim == true) {
			invoiceTypeDTO
					.setDebitManufacturingPlant(DebitManufacturingPlant.Enum
							.forString(YES));
		} else {
			invoiceTypeDTO
					.setDebitManufacturingPlant(DebitManufacturingPlant.Enum
							.forString(NO));
		}
		if (YES.equalsIgnoreCase(invoiceTypeDTO.getDebitManufacturingPlant()
				.toString())) {

			invoiceTypeDTO.setShipCustomerId(invoiceTypeDTO
					.getManufacturingLocation());
		} else {
			invoiceTypeDTO
					.setShipCustomerId(IntegrationLayerUtil
							.getDealerNumber(claim.getForDealerShip()
									.getDealerNumber()));
		}
	}

	private void setMarketingInformation(Invoice invoiceTypeDTO, Claim claim,
			Dealership dealer) {
		if (claim.getClaimedItems().get(0).getItemReference() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem().getMarketingGroupCode() != null) {
			invoiceTypeDTO.setMarketingGroupCode(claim.getClaimedItems().get(0)
					.getItemReference().getReferredInventoryItem()
					.getMarketingGroupCode());
		}
		if (dealer != null && dealer.getMarketingGroup() != null) {
			if (invoiceTypeDTO.getMarketingGroupCode() == null) {
				invoiceTypeDTO
						.setMarketingGroupCode(dealer.getMarketingGroup());
			}
			if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())) {
				invoiceTypeDTO.setDealerMkgtGroup(dealer.getMarketingGroup());
			}
		}
	}

	private void setWarrantyAndPolicyDeatilsForClaim(Invoice invoiceTypeDTO,
			Claim claim) {
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim
				.getBusinessUnitInfo().getName());
		PolicyDefinition policyDefinition = null;
		String policyCode = null;
		if (claim.isOfType(ClaimType.CAMPAIGN)) {
			invoiceTypeDTO.setWarrantyType(WARRANTY_FPI);
		} else if (!claim.isOfType(ClaimType.CAMPAIGN)) {
			policyCode = getApplicablPolicyCode(claim);
			if (policyCode != null && StringUtils.hasText(policyCode)) {
				if (WarrantyType.POLICY.toString().equalsIgnoreCase(
						policyCode)) {
					invoiceTypeDTO.setWarrantyType(WarrantyType.POLICY
							.toString());
				} else {
					policyDefinition = policyDefinitionRepository
							.findPolicyDefinitionByCode(policyCode.trim());
					if (policyDefinition != null
							&& policyDefinition.getWarrantyType() != null) {
						invoiceTypeDTO.setWarrantyType(policyDefinition
								.getWarrantyType().getType());
					}
				}
			}
		} else if (invoiceTypeDTO.getWarrantyType() == null) {
			invoiceTypeDTO.setWarrantyType(WarrantyType.STANDARD.toString());
		}
		if (claim.isNcr() == true || claim.isNcrWith30Days()) {
			invoiceTypeDTO.setWarrantyType(WarrantyType.STANDARD.toString());
		}
		setCodeAndDescriptionValuesForClaim(policyCode, policyDefinition,
				invoiceTypeDTO, claim);
	}
	
	public String getApplicablPolicyCode(Claim claim){
		String policyCode = null;
		if (claim.getPolicyCode() != null
				&& StringUtils.hasText(claim.getPolicyCode())) {
			policyCode = claim.getPolicyCode();
		} else if (Constants.INVALID_ITEM_NO_WARRANTY.equals(claim.getClaimProcessedAs())) {
			return policyCode =WarrantyType.POLICY
					.toString();
		} else if (Constants.VALID_ITEM_NO_WARRANTY.equals(claim.getClaimProcessedAs())) {
				return policyCode=WarrantyType.POLICY
						.toString();
		} else if (Constants.VALID_ITEM_OUT_OF_WARRANTY.equals(claim.getClaimProcessedAs())) {
				return policyCode= WarrantyType.POLICY
						.toString();
		}
		else if (claim.getClaimedItems() != null
				&& claim.getClaimedItems().get(0) != null
				&& claim.getClaimedItems().get(0).getApplicablePolicy() != null
				&& claim.getClaimedItems().get(0).getApplicablePolicy()
						.getCode() != null
				&& StringUtils.hasText(claim.getClaimedItems().get(0)
						.getApplicablePolicy().getCode())) {
			policyCode=claim.getClaimedItems().get(0)
			.getApplicablePolicy().getCode();
		}
		return policyCode;
	}

	private void setCodeAndDescriptionValuesForClaim(String poilcyCode,
			PolicyDefinition policyDefinition, Invoice invoiceTypeDTO,
			Claim claim) {
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())) {
			if (claim.isOfType(ClaimType.CAMPAIGN)) {
				setFPIdetails(invoiceTypeDTO, claim);
			} else {
				setPolicydetails(poilcyCode,policyDefinition, invoiceTypeDTO, claim);
			}
		}
	}

	private void setPolicydetails(String poilcyCode,PolicyDefinition policyDefinition,
			Invoice invoiceTypeDTO, Claim claim) {
		if (poilcyCode != null&&StringUtils.hasText(poilcyCode)) {
			invoiceTypeDTO.setCode(poilcyCode);
		} else if (claim.getClaimedItems().get(0).getItemReference() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem().getType() != null
				&& claim.getClaimedItems().get(0).getItemReference()
						.getReferredInventoryItem().getType()
						.equals(InventoryType.STOCK)) {
			invoiceTypeDTO.setCode(STOCK_INVENTORY_DEFAULT_POLICY_CODE);
		}
		if (policyDefinition != null
				&& policyDefinition.getNomsTierDescription() != null) {
			invoiceTypeDTO.setDescription(policyDefinition
					.getNomsTierDescription());
		} else if (invoiceTypeDTO.getCode() != null
				&& invoiceTypeDTO.getCode().equalsIgnoreCase(
						DEFAULT_POLICY_CODE)) {
			invoiceTypeDTO.setDescription(POLICY_CLAIMS_NOMS_DESCRIPTION);
		} else if (invoiceTypeDTO.getCode() != null
				&& invoiceTypeDTO.getCode().equalsIgnoreCase(
						STOCK_INVENTORY_DEFAULT_POLICY_CODE)) {
			invoiceTypeDTO.setDescription(STOCK_CLAIMS_NOMS_DESCRIPTION);
			invoiceTypeDTO.setWarrantyType(WarrantyType.STANDARD.toString());
		}
	}

	private void setFPIdetails(Invoice invoiceTypeDTO, Claim claim) {
		if (claim.getCampaign() != null) {
			if (claim.getCampaign().getCode() != null
					&& StringUtils.hasText(claim.getCampaign().getCode())) {
				invoiceTypeDTO.setCode(claim.getCampaign().getCode());
			}
			if (claim.getCampaign().getLabels() != null
					&& !claim.getCampaign().getLabels().isEmpty()) {
				List<Label> labels = new ArrayList<Label>();
				labels.addAll(claim.getCampaign().getLabels());
				invoiceTypeDTO.setDescription(labels.get(0).getName());
			}
		}
	}

	private void setTheAmountForManufacterer(Invoice invoiceTypeDTO) {
		Currency currency = Currency.getInstance(invoiceTypeDTO
				.getCurrencyCode().toString());
		int i = 0;
		if (invoiceTypeDTO.getLineItems() != null) {
			for (LineItem LineItem : invoiceTypeDTO.getLineItems()
					.getLineItemArray()) {
				int j = 0;
				for (SubItem subItem : LineItem.getSubItemArray()) {
					Money subItemAmount = Money.valueOf(subItem.getAmount(),
							currency);
					if (subItemAmount.isNegative()) {
						subItem.setAmount(subItem.getAmount().abs());
					} else {
						subItem.setAmount(subItem.getAmount().negate());
					}
					j++;
				}
				Money lineItemAmount = Money.valueOf(LineItem.getAmount(),
						currency);
				Money lineItemTotalAmount = Money.valueOf(
						LineItem.getTotalAmount(), currency);
				if (lineItemAmount.isNegative()) {
					LineItem.setAmount(LineItem.getAmount().abs());
				} else {
					LineItem.setAmount(LineItem.getAmount().negate());
				}
				if (lineItemTotalAmount.isNegative()) {
					LineItem.setTotalAmount((LineItem.getTotalAmount().abs()));
				} else {
					LineItem.setTotalAmount((LineItem.getTotalAmount().negate()));
				}
				invoiceTypeDTO.getLineItems().setLineItemArray(i, LineItem);
				i++;
			}
		}
		Money totalClaimAmount = Money.valueOf(
				invoiceTypeDTO.getTotalClaimAmount(), currency);
		if (totalClaimAmount.isNegative()) {
			invoiceTypeDTO.setTotalClaimAmount(invoiceTypeDTO
					.getTotalClaimAmount().abs());
		} else {
			invoiceTypeDTO.setTotalClaimAmount(invoiceTypeDTO
					.getTotalClaimAmount().negate());
		}
	}

    /**
     * @param claim
     * @return Populated the claim payment specific information
     */
	/*private LineItems populatelineItems(Claim claim) {
    	String erpCurrency = claimCurrencyConversionAdvice.getCurrencyForERPInteractions(claim);
        LineItems lineItemsTypeDTO = LineItems.Factory.newInstance();
        List<LineItemGroup> lineItemGroups = claim.getPayment().getLineItemGroups();

        boolean isMiscellaneousSet = false;
        boolean isTravelSet = false;
        boolean isNonOEMSet = false;
        BigDecimal totalTravel = new BigDecimal(0);
        *//**
         * @Todo Need to include Misc Parts for a TK CR198
         *//*
        SortedMap<Integer, LineItem> lineItemDtoList = new TreeMap<Integer, LineItem>();
        for (LineItemGroup lineItemGroup : lineItemGroups) {
            if (lineItemGroup.getName().equalsIgnoreCase("Claim Amount")) {
                continue;
            }
            LineItem lineItemTypeDTO = LineItem.Factory.newInstance();
            lineItemTypeDTO.setAmount(lineItemGroup.getAcceptedTotal().breachEncapsulationOfAmount().negate());
            lineItemTypeDTO.setTotalAmount(lineItemGroup.getTotalCreditAmount().breachEncapsulationOfAmount().negate());
            String category = lineItemGroup.getName();
            if (logger.isInfoEnabled()) {
                logger.info("ProcessGlobalClaim :: populatelineItems()  :  "
                        + category);
            }
            if (Section.LABOR.equalsIgnoreCase(category)) {
                addLaborItem(claim, lineItemDtoList, lineItemGroup, lineItemTypeDTO, category);
            } else if (Section.OEM_PARTS.equalsIgnoreCase(category)) {
                addOEMParts(claim, lineItemDtoList, lineItemGroup, lineItemTypeDTO, category);
            } else if (Section.NON_OEM_PARTS.equalsIgnoreCase(category)
                    || Section.MISCELLANEOUS_PARTS.equalsIgnoreCase(category)) {
                if (!isNonOEMSet) {
                    lineItemTypeDTO.setLineAttribute3(LineAttribute3.NONOEMPARTS);
                    lineItemDtoList.put(new Integer(LineAttribute3.INT_NONOEMPARTS), lineItemTypeDTO);
                    setSubItemForNonOEM(claim,
                            claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced(),
                            claim.getServiceInformation().getServiceDetail().getMiscPartsReplaced(),
                            lineItemTypeDTO);
                    isNonOEMSet = true;
                }
                lineItemTypeDTO = getNonOEMLineItem(lineItemDtoList);
                BigDecimal nomOEMAmount = getNonOEMAmount(claim);
                lineItemTypeDTO.setAmount(nomOEMAmount);
                BigDecimal nonOEMTotalAmount = getNonOEMTotalAmount(claim);
                lineItemTypeDTO.setTotalAmount(nonOEMTotalAmount);


            } else if (Section.ITEM_FREIGHT_DUTY.equalsIgnoreCase(category)
                    || Section.MEALS.equalsIgnoreCase(category)
                    || Section.PARKING.equalsIgnoreCase(category)
                    || Section.PER_DIEM.equalsIgnoreCase(category)
                    || Section.RENTAL_CHARGES.equalsIgnoreCase(category)
                    || Section.ADDITIONAL_TRAVEL_HOURS.equalsIgnoreCase(category)
                    || Section.LOCAL_PURCHASE.equalsIgnoreCase(category)
                    || Section.TOLLS.equalsIgnoreCase(category)
                    || Section.OTHER_FREIGHT_DUTY.equalsIgnoreCase(category)
                    || Section.OTHERS.equalsIgnoreCase(category)
                    || isTravelCategory(category)) {
                if (!isMiscellaneousSet) {
                    lineItemTypeDTO
                            .setLineAttribute3(LineAttribute3.MISCELLANEOUS);
                    lineItemDtoList.put(new Integer(LineAttribute3.INT_MISCELLANEOUS), lineItemTypeDTO);
                    isMiscellaneousSet = true;
                }

                lineItemTypeDTO = getMiscellaneousLineItem(lineItemDtoList);
                SubItem subItemTypeDTO = null;

                if (isTravelCategory(category)) {
                    if (logger.isInfoEnabled()) {
                        logger.info("ProcessGlobalClaim :: populatelineItems():" + category);
                    }
                    if (!isTravelSet) {
                        subItemTypeDTO = lineItemTypeDTO.addNewSubItem();
                        subItemTypeDTO.setLineAttribute4(LineAttribute4.TRAVEL);
                        isTravelSet = true;
                    }
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("ProcessGlobalClaim :: populatelineItems():" + category);
                    }
                    subItemTypeDTO = lineItemTypeDTO.addNewSubItem();
                }

                if (isTravelCategory(category)) {
                    if (logger.isInfoEnabled()) {
                        logger.info("ProcessGlobalClaim :: populatelineItems() , 13:");
                    }
                    subItemTypeDTO = getTravelSubLineItem(lineItemTypeDTO);

                    totalTravel = totalTravel.add(lineItemGroup
                            .getTotalCreditAmount()
                            .breachEncapsulationOfAmount());
                    subItemTypeDTO.setAmount(totalTravel.negate());
                } else {
                    subItemTypeDTO.setAmount(lineItemGroup
                            .getTotalCreditAmount().breachEncapsulationOfAmount().negate());
                }

                BigDecimal miscTotalWithOutProrated = getMiscTotalWithoutProrated(claim);
                lineItemTypeDTO.setAmount(miscTotalWithOutProrated);
                BigDecimal miscTotal = getMiscTotal(claim);
                lineItemTypeDTO.setTotalAmount(miscTotal);
                if (Section.ITEM_FREIGHT_DUTY.equalsIgnoreCase(category)) {
                    subItemTypeDTO.setLineAttribute4(LineAttribute4.FREIGHT);
                } else if (Section.MEALS.equalsIgnoreCase(category)) {
                    subItemTypeDTO.setLineAttribute4(LineAttribute4.MEALS);
                } else if (Section.PARKING.equalsIgnoreCase(category)) {
                    subItemTypeDTO.setLineAttribute4(LineAttribute4.PARKING);
                } else if (Section.PER_DIEM.equalsIgnoreCase(category)) {
                    subItemTypeDTO.setLineAttribute4(LineAttribute4.PERDIEM);
                } else if (Section.RENTAL_CHARGES.equalsIgnoreCase(category)) {
                    subItemTypeDTO.setLineAttribute4(LineAttribute4.RENTALCHARGES);
                } else if (Section.ADDITIONAL_TRAVEL_HOURS.equalsIgnoreCase(category)) {
                    subItemTypeDTO.setLineAttribute4(LineAttribute4.ADDITIONALTRAVELHOURS);
                } else if (Section.LOCAL_PURCHASE.equalsIgnoreCase(category)) {
                    subItemTypeDTO.setLineAttribute4(LineAttribute4.LOCALPURCHASE);
                } else if (Section.TOLLS.equalsIgnoreCase(category)) {
                    subItemTypeDTO.setLineAttribute4(LineAttribute4.TOLLS);
                } else if (Section.OTHER_FREIGHT_DUTY.equalsIgnoreCase(category)) {
                    subItemTypeDTO.setLineAttribute4(LineAttribute4.OTHERFREIGHTDUTY);
                } else if (Section.OTHERS.equalsIgnoreCase(category)) {
                    subItemTypeDTO.setLineAttribute4(LineAttribute4.OTHERS);
                }
            }
        }
        lineItemsTypeDTO.setLineItemArray(getLineItemDtoArray(lineItemDtoList,claim));
        if (logger.isInfoEnabled()) {
            logger.info("ProcessGlobalClaim :: exit populatelineItems() , 5:");
        }
        return lineItemsTypeDTO;
    }*/
    private void  populatelineItems(Claim claim,Invoice invoiceTypeDTO,LineItems lineItemsTypeDTO) {
        List<LineItemGroup> lineItemGroups = claim.getPayment().getLineItemGroups();

        boolean isMiscellaneousSet = false;
        boolean isTravelSet = false;
        boolean isOthersSet = false;
        boolean isNonOEMSet = false;
        BigDecimal totalTravel = new BigDecimal(0);
        BigDecimal totalOthers=new BigDecimal(0);
        /**
         * @Todo Need to include Misc Parts for a TK CR198
         */
        SortedMap<Integer, LineItem> lineItemDtoList = new TreeMap<Integer, LineItem>();
        for (LineItemGroup lineItemGroup : lineItemGroups) {
            if (lineItemGroup.getName().equalsIgnoreCase("Claim Amount")) {
            	setTotalWarrantyCostAmount(invoiceTypeDTO,lineItemGroup,claim);
                continue;
            }
            LineItem lineItemTypeDTO = LineItem.Factory.newInstance();
            lineItemTypeDTO.setAmount(lineItemGroup.getAcceptedTotal().breachEncapsulationOfAmount().negate());
            lineItemTypeDTO.setTotalAmount(lineItemGroup.getTotalCreditAmount().breachEncapsulationOfAmount().negate());
            String category = lineItemGroup.getName();
            if (logger.isInfoEnabled()) {
                logger.info("ProcessGlobalClaim :: populatelineItems()  :  "
                        + category);
            }
            if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())&&Section.LATE_FEE.equalsIgnoreCase(category)){
            	invoiceTypeDTO.setLateFeeAmount(lineItemGroup
                        .getTotalCreditAmount().breachEncapsulationOfAmount().negate());
            }
            if (Section.LABOR.equalsIgnoreCase(category)) {
                addLaborItem(claim, lineItemDtoList, lineItemGroup, lineItemTypeDTO, category);
            } else if (Section.OEM_PARTS.equalsIgnoreCase(category)) {
                addOEMParts(claim, lineItemDtoList, lineItemGroup, lineItemTypeDTO, category);
            } else if (Section.NON_OEM_PARTS.equalsIgnoreCase(category)
                    || Section.MISCELLANEOUS_PARTS.equalsIgnoreCase(category)) {
                if (!isNonOEMSet) {
                    lineItemTypeDTO.setLineAttribute3(LineAttribute3.NONOEMPARTS);
                    lineItemDtoList.put(new Integer(LineAttribute3.INT_NONOEMPARTS), lineItemTypeDTO);
/*                    setSubItemForNonOEM(claim,
                            claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced(),
                            claim.getServiceInformation().getServiceDetail().getMiscPartsReplaced(),
                            lineItemTypeDTO);
*/                    isNonOEMSet = true;
                }
                lineItemTypeDTO = getNonOEMLineItem(lineItemDtoList);
                BigDecimal nomOEMAmount = getNonOEMAmount(claim);
                lineItemTypeDTO.setAmount(nomOEMAmount);
                BigDecimal nonOEMTotalAmount = getNonOEMTotalAmount(claim);
                lineItemTypeDTO.setTotalAmount(nonOEMTotalAmount);


            } else if (isValidCostCategory(category)) {
                if (!isMiscellaneousSet) {
                    lineItemTypeDTO
                            .setLineAttribute3(LineAttribute3.MISCELLANEOUS);
                    lineItemDtoList.put(new Integer(LineAttribute3.INT_MISCELLANEOUS), lineItemTypeDTO);
                    isMiscellaneousSet = true;
                }

                lineItemTypeDTO = getMiscellaneousLineItem(lineItemDtoList);
                SubItem subItemTypeDTO = null;

                if (isTravelCategory(category)) {
                    if (logger.isInfoEnabled()) {
                        logger.info("ProcessGlobalClaim :: populatelineItems():" + category);
                    }
                    if (!isTravelSet) {
                        subItemTypeDTO = lineItemTypeDTO.addNewSubItem();
                        subItemTypeDTO.setLineAttribute4(LineAttribute4.TRAVEL);
                        isTravelSet = true;
                    }
                } 
                else if (isOthersCategory(category)) {
                    if (logger.isInfoEnabled()) {
                        logger.info("ProcessGlobalClaim :: populatelineItems():" + category);
                    }
                    if (!isOthersSet) {
                        subItemTypeDTO = lineItemTypeDTO.addNewSubItem();
                        subItemTypeDTO.setLineAttribute4(LineAttribute4.OTHERS);
                        isOthersSet = true;
                    }
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("ProcessGlobalClaim :: populatelineItems():" + category);
                    }
                    subItemTypeDTO = lineItemTypeDTO.addNewSubItem();
                }
                if (isTravelCategory(category)) {
                    if (logger.isInfoEnabled()) {
                        logger.info("ProcessGlobalClaim :: populatelineItems() , 13:");
                    }
                    subItemTypeDTO = getTravelSubLineItem(lineItemTypeDTO);

                    totalTravel = totalTravel.add(lineItemGroup
                            .getTotalCreditAmount()
                            .breachEncapsulationOfAmount());
                    subItemTypeDTO.setAmount(totalTravel.negate());
                } else if(isOthersCategory(category)){
                	subItemTypeDTO = getOthersSubLineItem(lineItemTypeDTO);
					if (!Section.DEDUCTIBLE.equalsIgnoreCase(category)) {
						totalOthers = totalOthers.add(lineItemGroup
								.getTotalCreditAmount()
								.breachEncapsulationOfAmount());
					} else {
						MathContext mc = new MathContext(5,
								RoundingMode.HALF_EVEN);
						totalOthers = totalOthers.subtract(lineItemGroup
								.getTotalCreditAmount()
								.breachEncapsulationOfAmount(), mc);
					}
                    subItemTypeDTO.setAmount(totalOthers.negate());
                
                }else{
                    subItemTypeDTO.setAmount(lineItemGroup
                            .getTotalCreditAmount().breachEncapsulationOfAmount().negate());
                }
                BigDecimal miscTotalWithOutProrated = getMiscTotalWithoutProrated(claim);
                lineItemTypeDTO.setAmount(miscTotalWithOutProrated);
                BigDecimal miscTotal = getMiscTotal(claim);
                lineItemTypeDTO.setTotalAmount(miscTotal);
                setLineAttribut4(subItemTypeDTO, category);
            }
        }
        lineItemsTypeDTO.setLineItemArray(getLineItemDtoArray(lineItemDtoList,claim));
        if (logger.isInfoEnabled()) {
            logger.info("ProcessGlobalClaim :: exit populatelineItems() , 5:");
        }
    }
    private void  populatelineItemsForStateMandate(Claim claim,Invoice invoiceTypeDTO,LineItems lineItemsTypeDTO) {
        List<LineItemGroup> lineItemGroups = claim.getPayment().getLineItemGroups();

        boolean isMiscellaneousSet = false;
        boolean isTravelSet = false;
        boolean isOthersSet = false;
        boolean isNonOEMSet = false;
        BigDecimal totalTravel = new BigDecimal(0);
        BigDecimal totalOthers=new BigDecimal(0);
        /**
         * @Todo Need to include Misc Parts for a TK CR198
         */
        SortedMap<Integer, LineItem> lineItemDtoList = new TreeMap<Integer, LineItem>();
        for (LineItemGroup lineItemGroup : lineItemGroups) {
            if (lineItemGroup.getName().equalsIgnoreCase("Claim Amount")) {
            	setTotalWarrantyCostAmount(invoiceTypeDTO,lineItemGroup,claim);
                continue;
            }
            LineItem lineItemTypeDTO = LineItem.Factory.newInstance();
            lineItemTypeDTO.setAmount(lineItemGroup.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount().negate());
            lineItemTypeDTO.setTotalAmount(lineItemGroup.getTotalCreditAmount().breachEncapsulationOfAmount().negate());
            String category = lineItemGroup.getName();
            if (logger.isInfoEnabled()) {
                logger.info("ProcessGlobalClaim :: populatelineItems()  :  "
                        + category);
            }
            if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())&&Section.LATE_FEE.equalsIgnoreCase(category)){
            	invoiceTypeDTO.setLateFeeAmount(lineItemGroup
                        .getTotalCreditAmount().breachEncapsulationOfAmount().negate());
            }
            if (Section.LABOR.equalsIgnoreCase(category)) {
                addLaborItemForStateMandate(claim, lineItemDtoList, lineItemGroup, lineItemTypeDTO, category);
            } else if (Section.OEM_PARTS.equalsIgnoreCase(category)) {
                addOEMPartsForStateMandate(claim, lineItemDtoList, lineItemGroup, lineItemTypeDTO, category);
            } else if (Section.NON_OEM_PARTS.equalsIgnoreCase(category)
                    || Section.MISCELLANEOUS_PARTS.equalsIgnoreCase(category)) {
                if (!isNonOEMSet) {
                    lineItemTypeDTO.setLineAttribute3(LineAttribute3.NONOEMPARTS);
                    lineItemDtoList.put(new Integer(LineAttribute3.INT_NONOEMPARTS), lineItemTypeDTO);
/*                    setSubItemForNonOEM(claim,
                            claim.getServiceInformation().getServiceDetail().getNonOEMPartsReplaced(),
                            claim.getServiceInformation().getServiceDetail().getMiscPartsReplaced(),
                            lineItemTypeDTO);
*/                    isNonOEMSet = true;
                }
                lineItemTypeDTO = getNonOEMLineItem(lineItemDtoList);
                BigDecimal nomOEMAmount = getNonOEMAmountForStateMandate(claim);
                lineItemTypeDTO.setAmount(nomOEMAmount);
                BigDecimal nonOEMTotalAmount = getNonOEMTotalAmountForStateMandate(claim);
                lineItemTypeDTO.setTotalAmount(nonOEMTotalAmount);


            } else if (isValidCostCategory(category)) {
                if (!isMiscellaneousSet) {
                    lineItemTypeDTO
                            .setLineAttribute3(LineAttribute3.MISCELLANEOUS);
                    lineItemDtoList.put(new Integer(LineAttribute3.INT_MISCELLANEOUS), lineItemTypeDTO);
                    isMiscellaneousSet = true;
                }

                lineItemTypeDTO = getMiscellaneousLineItem(lineItemDtoList);
                SubItem subItemTypeDTO = null;

                if (isTravelCategory(category)) {
                    if (logger.isInfoEnabled()) {
                        logger.info("ProcessGlobalClaim :: populatelineItems():" + category);
                    }
                    if (!isTravelSet) {
                        subItemTypeDTO = lineItemTypeDTO.addNewSubItem();
                        subItemTypeDTO.setLineAttribute4(LineAttribute4.TRAVEL);
                        isTravelSet = true;
                    }
                } 
                else if (isOthersCategory(category)) {
                    if (logger.isInfoEnabled()) {
                        logger.info("ProcessGlobalClaim :: populatelineItems():" + category);
                    }
                    if (!isOthersSet) {
                        subItemTypeDTO = lineItemTypeDTO.addNewSubItem();
                        subItemTypeDTO.setLineAttribute4(LineAttribute4.OTHERS);
                        isOthersSet = true;
                    }
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("ProcessGlobalClaim :: populatelineItems():" + category);
                    }
                    subItemTypeDTO = lineItemTypeDTO.addNewSubItem();
                }
                if (isTravelCategory(category)) {
                    if (logger.isInfoEnabled()) {
                        logger.info("ProcessGlobalClaim :: populatelineItems() , 13:");
                    }
                    subItemTypeDTO = getTravelSubLineItem(lineItemTypeDTO);

                    totalTravel = totalTravel.add(lineItemGroup
                            .getTotalCreditAmount()
                            .breachEncapsulationOfAmount());
                    subItemTypeDTO.setAmount(totalTravel.negate());
                } else if(isOthersCategory(category)){
                	subItemTypeDTO = getOthersSubLineItem(lineItemTypeDTO);
                    if(!Section.DEDUCTIBLE.equalsIgnoreCase(category)){
                    totalOthers = totalOthers.add(lineItemGroup
                            .getTotalCreditAmount()
                            .breachEncapsulationOfAmount());
                    }else{
                    	MathContext mc= new MathContext(5,RoundingMode.HALF_EVEN);
                    	totalOthers=totalOthers.subtract(lineItemGroup
                            .getTotalCreditAmount()
                            .breachEncapsulationOfAmount(), mc);
                    }
                    subItemTypeDTO.setAmount(totalOthers.negate());
                }else{
                    subItemTypeDTO.setAmount(lineItemGroup
                            .getTotalCreditAmount().breachEncapsulationOfAmount().negate());
                }
                BigDecimal miscTotalWithOutProrated = getMiscTotalWithoutProratedForStateMandate(claim);
                lineItemTypeDTO.setAmount(miscTotalWithOutProrated);
                BigDecimal miscTotal = getMiscTotal(claim);
                lineItemTypeDTO.setTotalAmount(miscTotal);
                setLineAttribut4(subItemTypeDTO, category);
            }
        }
        lineItemsTypeDTO.setLineItemArray(getLineItemDtoArray(lineItemDtoList,claim));
        if (logger.isInfoEnabled()) {
            logger.info("ProcessGlobalClaim :: exit populatelineItems() , 5:");
        }
    }


	private SubItem getOthersSubLineItem(LineItem miscLineItemDTO) {
		 SubItem[] subItemArray = miscLineItemDTO.getSubItemArray();
	        for (SubItem subItemTypeDTO : subItemArray) {
	            if (subItemTypeDTO.getLineAttribute4().equals(LineAttribute4.OTHERS))
	                return subItemTypeDTO;
	        }
	        return null;
	}

	private boolean isOthersCategory(String category) {
		if ((Section.TRANSPORTATION_COST.equalsIgnoreCase(category)
				|| Section.ITEM_FREIGHT_DUTY.equalsIgnoreCase(category) || Section.HANDLING_FEE
					.equalsIgnoreCase(category))
				|| Section.DEDUCTIBLE.equalsIgnoreCase(category)) {
			return true;
		}
		return false;
	}

	private void setTotalWarrantyCostAmount(Invoice invoiceTypeDTO,
			LineItemGroup lineItemGroup, Claim claim) {
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())) {
			invoiceTypeDTO.setTotalWarrantyCostAmount(lineItemGroup
					.getScTotalCreditAmount().breachEncapsulationOfAmount());
		}
	}

	private LineItem getNonOEMLineItem(
            Map<Integer, LineItem> lineItemDtoList) {
        LineItem returnLineItemTypeDTO = null;
        returnLineItemTypeDTO = lineItemDtoList.get(new Integer(LineAttribute3.INT_NONOEMPARTS));
        return returnLineItemTypeDTO;
    }

    private BigDecimal getNonOEMTotalAmount(Claim claim) {
        BigDecimal total = new BigDecimal(0);
        LineItemGroup nomOEM = claim.getPayment().getLineItemGroup(Section.NON_OEM_PARTS);
        LineItemGroup miscPart = claim.getPayment().getLineItemGroup(Section.MISCELLANEOUS_PARTS);

        if (nomOEM != null) {
            total = total.add(nomOEM.getTotalCreditAmount().breachEncapsulationOfAmount());
        }

        if (miscPart != null) {
            total = total.add(miscPart.getTotalCreditAmount().breachEncapsulationOfAmount());
        }
        return total.negate();

    }
    private BigDecimal getNonOEMTotalAmountForStateMandate(Claim claim) {
        BigDecimal total = new BigDecimal(0);
        LineItemGroup nomOEM = claim.getPayment().getLineItemGroup(Section.NON_OEM_PARTS);
        LineItemGroup miscPart = claim.getPayment().getLineItemGroup(Section.MISCELLANEOUS_PARTS);

        if (nomOEM != null) {
            total = total.add(nomOEM.getTotalCreditAmount().breachEncapsulationOfAmount());
        }

        if (miscPart != null) {
            total = total.add(miscPart.getTotalCreditAmount().breachEncapsulationOfAmount());
        }
        return total.negate();

    }

    private BigDecimal getNonOEMAmount(Claim claim) {
        BigDecimal total = new BigDecimal(0);
        LineItemGroup nonOEM = claim.getPayment().getLineItemGroup(Section.NON_OEM_PARTS);
        LineItemGroup miscPart = claim.getPayment().getLineItemGroup(Section.MISCELLANEOUS_PARTS);

        if (nonOEM != null) {
            total = total.add(nonOEM.getAcceptedTotal().breachEncapsulationOfAmount());
        }
        if (miscPart != null) {
            total = total.add(miscPart.getAcceptedTotal().breachEncapsulationOfAmount());
        }

        return total.negate();
    }
    
    private BigDecimal getNonOEMAmountForStateMandate(Claim claim) {
        BigDecimal total = new BigDecimal(0);
        LineItemGroup nonOEM = claim.getPayment().getLineItemGroup(Section.NON_OEM_PARTS);
        LineItemGroup miscPart = claim.getPayment().getLineItemGroup(Section.MISCELLANEOUS_PARTS);

        if (nonOEM != null) {
            total = total.add(nonOEM.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }
        if (miscPart != null) {
            total = total.add(miscPart.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }

        return total.negate();
    }

    /**
     * Adds OEM parts to claim payment
     *
     * @param claim
     * @param lineItemDtoList
     * @param lineItemGroup
     * @param lineItemTypeDTO
     * @param category
     */
	private void addOEMPartsForStateMandate(Claim claim,
			Map<Integer, LineItem> lineItemDtoList,
			LineItemGroup lineItemGroup, LineItem lineItemTypeDTO,
			String category) {
		if (logger.isInfoEnabled()) {
			logger.info("ProcessGlobalClaim :: addOEMParts():" + category);
		}
		lineItemTypeDTO.setLineAttribute3(LineAttribute3.OEMPARTS);
		lineItemTypeDTO.setAmount(lineItemGroup.getGroupTotalStateMandateAmount()
				.breachEncapsulationOfAmount().negate());
		lineItemTypeDTO.setTotalAmount(lineItemGroup.getTotalCreditAmount()
				.breachEncapsulationOfAmount().negate());
		setPartsAmountFeildsForStateMandate(lineItemTypeDTO, lineItemGroup, claim);
		lineItemDtoList.put(new Integer(LineAttribute3.INT_OEMPARTS),
				lineItemTypeDTO);
	}

	private void setPartsAmountFeildsForStateMandate(LineItem lineItemTypeDTO,
			LineItemGroup lineItemGroup, Claim claim) {
		Money dnetPerVal = lineItemGroup.getModifierTotalCreditAmount();
		Money dnetAmount = null;
		Money stdCost = null;
		dnetAmount = lineItemGroup.getNetPriceTotalCreditAmount();
		stdCost = lineItemGroup.getScTotalCreditAmount();
		if (dnetAmount != null) {
			lineItemTypeDTO.setDealerNet(dnetAmount
					.breachEncapsulationOfAmount().negate());
		}
		if (dnetPerVal != null) {
			lineItemTypeDTO.setDNETPerVal(dnetPerVal
					.breachEncapsulationOfAmount().negate());
		}
		if (stdCost != null) {
			if (lineItemTypeDTO.getDealerNet().signum() == -1) {
				lineItemTypeDTO.setSTDCost(stdCost
						.breachEncapsulationOfAmount().abs());
			} else {
				if (stdCost.breachEncapsulationOfAmount().negate().signum() != -1) {
					lineItemTypeDTO.setSTDCost(stdCost
							.breachEncapsulationOfAmount().negate().negate());
				} else {
					lineItemTypeDTO.setSTDCost(stdCost
							.breachEncapsulationOfAmount().negate());
				}
			}
		}
	}

	private void addOEMParts(Claim claim,
                             Map<Integer, LineItem> lineItemDtoList, LineItemGroup lineItemGroup,
                             LineItem lineItemTypeDTO, String category) {
        if (logger.isInfoEnabled()) {
            logger.info("ProcessGlobalClaim :: addOEMParts():" + category);
        }
        lineItemTypeDTO.setLineAttribute3(LineAttribute3.OEMPARTS);
//        setSubItemForOEM(claim, lineItemTypeDTO);
        lineItemTypeDTO.setAmount(lineItemGroup.getAcceptedTotal().breachEncapsulationOfAmount().negate());
        lineItemTypeDTO.setTotalAmount(lineItemGroup.getTotalCreditAmount().breachEncapsulationOfAmount().negate());
        setAmerOemPartsAmountFeilds(lineItemTypeDTO,lineItemGroup,claim);
        lineItemDtoList.put(new Integer(LineAttribute3.INT_OEMPARTS), lineItemTypeDTO);
    }

	private void setAmerOemPartsAmountFeilds(LineItem lineItemTypeDTO,
			LineItemGroup lineItemGroup, Claim claim) {
		if (IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())) {
			Money dnetPerVal =lineItemGroup.getModifierTotalCreditAmount();
			Money dnetAmount = null;
			Money stdCost = null;
			dnetAmount = lineItemGroup.getNetPriceTotalCreditAmount();
			stdCost = lineItemGroup.getScTotalCreditAmount();
			if (dnetAmount != null) {
				lineItemTypeDTO.setDealerNet(dnetAmount
						.breachEncapsulationOfAmount().negate());
			}
			if (dnetPerVal != null) {
				lineItemTypeDTO.setDNETPerVal(dnetPerVal
						.breachEncapsulationOfAmount().negate());
			}
			if (stdCost != null) {
			if(lineItemTypeDTO.getDealerNet().signum() == -1){
					lineItemTypeDTO.setSTDCost(stdCost
							.breachEncapsulationOfAmount().abs());
			}else{
				if(stdCost
						.breachEncapsulationOfAmount().negate().signum()!=-1){
					lineItemTypeDTO.setSTDCost(stdCost
							.breachEncapsulationOfAmount().negate().negate());
				}else{
				lineItemTypeDTO.setSTDCost(stdCost
						.breachEncapsulationOfAmount().negate());
				}
			}
			}
		}
	}

	private Money getModifierAmountsForStateMandate(
			List<tavant.twms.domain.claim.payment.LineItem> modifiers,
			Claim claim) {
		Money modifierAmount = Money.valueOf(0.00,
				claim.getCurrencyForCalculation());
		;
		for (tavant.twms.domain.claim.payment.LineItem modifer : modifiers) {
			if (modifer.getStateMandateAmount() != null
					&& !modifer.getStateMandateAmount().isZero()) {
				modifierAmount = modifierAmount.plus(modifer
						.getStateMandateAmount());
			}
		}
		return modifierAmount;
	}
	private Money getModifierAmounts(
			List<tavant.twms.domain.claim.payment.LineItem> modifiers,
			Claim claim) {
		Money modifierAmount = Money.valueOf(0.00,
				claim.getCurrencyForCalculation());
		;
		for (tavant.twms.domain.claim.payment.LineItem modifer : modifiers) {
			if (modifer.getAcceptedCost() != null
					&& !modifer.getAcceptedCost().isZero()) {
				modifierAmount = modifierAmount.plus(modifer.getAcceptedCost());
			}
		}
		return modifierAmount;
	}

	/**
     * Adds Labor Item item to claim payment
     *
     * @param claim
     * @param lineItemDtoList
     * @param lineItemGroup
     * @param lineItemTypeDTO
     * @param category
     */
    private void addLaborItem(Claim claim,
                              Map<Integer, LineItem> lineItemDtoList, LineItemGroup lineItemGroup,
                              LineItem lineItemTypeDTO, String category) {
        if (logger.isInfoEnabled()) {
            logger.info("ProcessGlobalClaim :: start addLaborItem:" + category);
        }
        lineItemTypeDTO.setLineAttribute3(LineAttribute3.LABOR);
        lineItemTypeDTO.setTotalAmount(lineItemGroup
                .getTotalCreditAmount().breachEncapsulationOfAmount().negate());

        lineItemTypeDTO.setAmount(lineItemGroup
                .getAcceptedTotal().breachEncapsulationOfAmount().negate());
        if (logger.isInfoEnabled()) {
            logger.info("ProcessGlobalClaim :: exiting addLaborItem:" + category);
        }
        lineItemDtoList.put(new Integer(LineAttribute3.INT_LABOR), lineItemTypeDTO);
    }

    /**
     * Adds all the OEM Parts to the payment
     *
     * @param claim
     * @param lineItemTypeDTO
     * @return
     */
    private void setSubItemForOEM(Claim claim,
                                  LineItem lineItemTypeDTO) {
        if (logger.isInfoEnabled()) {
            logger.info("ProcessGlobalClaim :: start setSubItemForOEM :   "
                    + lineItemTypeDTO.getLineAttribute3().toString());
        }
        int i = 0;
        LineItemGroup oemGroup = claim.getPayment().getLineItemGroup(Section.OEM_PARTS);
        Boolean flag= (claim.getState()!=null && claim.getState().equals(ClaimState.DENIED))? true : false;
        Payment oldPayment = claim.getPaymentForClaimState(ClaimState.REOPENED.getState());
        List<PartPaymentInfo> itemsToReimburse = null;
        if (oldPayment != null) {
            LineItemGroup oldOemGroup = oldPayment.getLineItemGroup(Section.OEM_PARTS);
            itemsToReimburse = populatePartList(oemGroup.getCurrentPartPaymentInfo(),
                    oldOemGroup.getCurrentPartPaymentInfo(),flag);
        } else {
            itemsToReimburse = oemGroup.getCurrentPartPaymentInfo();
        }

        SubItem[] subItemArray = new SubItem[itemsToReimburse
                .size()];
        for (PartPaymentInfo partPaymentInfo : itemsToReimburse) {
            SubItem subItemTypeDTO = SubItem.Factory.newInstance();
            subItemTypeDTO.setLineAttribute4(LineAttribute4.OEMPARTS);
            subItemTypeDTO.setPartNumber(partPaymentInfo.getPartNumber());
            BigDecimal amount = partPaymentInfo.getUnitPrice().breachEncapsulationOfAmount();
            subItemTypeDTO.setAmount(amount);
            subItemTypeDTO.setQuantity(partPaymentInfo.getQuantity().intValue());
            subItemArray[i++] = subItemTypeDTO;
        }
        lineItemTypeDTO.setSubItemArray(subItemArray);
    }

    private void setSubItemForNonOEM(Claim claim,
                                     List<NonOEMPartReplaced> nonOemPartsReplaced,
                                     List<NonOEMPartReplaced> miscPartsReplaced,
                                     LineItem lineItemTypeDTO) {

        SubItem[] subItemArray = new SubItem[nonOemPartsReplaced
                .size() + miscPartsReplaced.size()];
        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: Start setSubItemForOEM() :  Size of Non OEM Parts Replaced :  "
                            + nonOemPartsReplaced.size());
        }
        int i = 0;
        for (NonOEMPartReplaced partReplaced : nonOemPartsReplaced) {
            SubItem subItemTypeDTO = SubItem.Factory.newInstance();
            subItemTypeDTO.setLineAttribute4(LineAttribute4.NONOEMPARTS);
            subItemTypeDTO.setPartNumber(partReplaced.getDescription());
            subItemTypeDTO.setQuantity(partReplaced.getNumberOfUnits().intValue());
            subItemTypeDTO.setAmount(partReplaced.getPricePerUnit().breachEncapsulationOfAmount());
            subItemArray[i++] = subItemTypeDTO;
        }

        int j = i;
        for (NonOEMPartReplaced partReplaced : miscPartsReplaced) {
            SubItem subItemTypeDTO = SubItem.Factory.newInstance();
            subItemTypeDTO.setLineAttribute4(LineAttribute4.MISCPARTS);
            subItemTypeDTO.setPartNumber(partReplaced.getMiscItemConfig().getMiscellaneousItem().getPartNumber());
            subItemTypeDTO.setQuantity(partReplaced.getNumberOfUnits().intValue());
            subItemTypeDTO.setAmount(partReplaced.getMiscItemConfig().getMiscItemRateForCurrency(
                            claim.getCurrencyForCalculation()).getRate().breachEncapsulationOfAmount());
            subItemArray[j++] = subItemTypeDTO;
        }
        lineItemTypeDTO.setSubItemArray(subItemArray);
        if (logger.isInfoEnabled()) {
            logger
                    .info("ProcessGlobalClaim :: exiting setSubItemForMiscParts()   ");
        }
    }

    private boolean isTravelCategory(String category) {
        return (Section.TRAVEL_BY_DISTANCE.equalsIgnoreCase(category)
                || Section.TRAVEL_BY_TRIP.equalsIgnoreCase(category) || Section.TRAVEL_BY_HOURS
                .equalsIgnoreCase(category)||Section.ADDITIONAL_TRAVEL_HOURS.equalsIgnoreCase(category));
    }

    private LineItem getMiscellaneousLineItem(
            Map<Integer, LineItem> lineItemDtoList) {
        LineItem returnLineItemTypeDTO = null;
        returnLineItemTypeDTO = lineItemDtoList.get(new Integer(LineAttribute3.INT_MISCELLANEOUS));
        return returnLineItemTypeDTO;
    }

    private SubItem getTravelSubLineItem(LineItem miscLineItemDTO) {
        SubItem[] subItemArray = miscLineItemDTO.getSubItemArray();
        for (SubItem subItemTypeDTO : subItemArray) {
            if (subItemTypeDTO.getLineAttribute4().equals(LineAttribute4.TRAVEL))
                return subItemTypeDTO;
        }
        return null;
    }

    private BigDecimal getMiscTotal(Claim claim) {
        BigDecimal total = new BigDecimal(0);
        LineItemGroup travel1 = claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_DISTANCE);
        LineItemGroup travel2 = claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_TRIP);
        LineItemGroup travel3 = claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_HOURS);
        LineItemGroup travel4 = claim.getPayment().getLineItemGroup(Section.ADDITIONAL_TRAVEL_HOURS);
        LineItemGroup meals = claim.getPayment().getLineItemGroup(Section.MEALS);
        LineItemGroup parking = claim.getPayment().getLineItemGroup(Section.PARKING);
        LineItemGroup perDiem = claim.getPayment().getLineItemGroup(Section.PER_DIEM);
        LineItemGroup rentalCharges = claim.getPayment().getLineItemGroup(Section.RENTAL_CHARGES);
        LineItemGroup localPurchase = claim.getPayment().getLineItemGroup(Section.LOCAL_PURCHASE);
        LineItemGroup tolls = claim.getPayment().getLineItemGroup(Section.TOLLS);
        LineItemGroup otherFreightDuty = claim.getPayment().getLineItemGroup(Section.OTHER_FREIGHT_DUTY);
        LineItemGroup handlingFee = claim.getPayment().getLineItemGroup(Section.HANDLING_FEE);
        LineItemGroup transporatation = claim.getPayment().getLineItemGroup(Section.TRANSPORTATION_COST);
        LineItemGroup deductable = claim.getPayment().getLineItemGroup(Section.DEDUCTIBLE);
        LineItemGroup itemFreight = claim.getPayment().getLineItemGroup(Section.ITEM_FREIGHT_DUTY);
        if(!IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())){
        	 LineItemGroup others = claim.getPayment().getLineItemGroup(Section.OTHERS);
        	 if(others!=null){
        		 total = total.add(others.getAcceptedTotal().breachEncapsulationOfAmount()); 
        	 }
        }

        if (travel1 != null) {
            total = total.add(travel1.getTotalCreditAmount().breachEncapsulationOfAmount());
        }

        if (travel2 != null) {
            total = total.add(travel2.getTotalCreditAmount().breachEncapsulationOfAmount());
        }

        if (travel3 != null) {
            total = total.add(travel3.getTotalCreditAmount().breachEncapsulationOfAmount());
        }
        if (travel4 != null) {
            total = total.add(travel4.getTotalCreditAmount().breachEncapsulationOfAmount());

        }
        if (meals != null) {
            total = total.add(meals.getTotalCreditAmount().breachEncapsulationOfAmount());
        }

        if (parking != null) {
            total = total.add(parking.getTotalCreditAmount().breachEncapsulationOfAmount());
        }

        if (perDiem != null) {
            total = total.add(perDiem.getTotalCreditAmount().breachEncapsulationOfAmount());

        }

        if (rentalCharges != null) {
            total = total.add(rentalCharges.getTotalCreditAmount().breachEncapsulationOfAmount());
        }
       
        if (localPurchase != null) {
            total = total.add(localPurchase.getTotalCreditAmount().breachEncapsulationOfAmount());
        }
        if (tolls != null) {
            total = total.add(tolls.getTotalCreditAmount().breachEncapsulationOfAmount());
        }
        if (otherFreightDuty != null) {
            total = total.add(otherFreightDuty.getTotalCreditAmount().breachEncapsulationOfAmount());
        } if (handlingFee != null) {
            total = total.add(handlingFee.getTotalCreditAmount().breachEncapsulationOfAmount());
        }
        if (transporatation != null) {
            total = total.add(transporatation.getTotalCreditAmount().breachEncapsulationOfAmount());
        }
        if (itemFreight != null) {
            total = total.add(itemFreight.getTotalCreditAmount().breachEncapsulationOfAmount());
        }
        if (deductable != null) {
        	total=subtractDdeductableAmountFromTotalAmount(claim,total,deductable.getTotalCreditAmount().breachEncapsulationOfAmount());
        }
        return total.negate();
    }
    

    private BigDecimal getMiscTotalWithoutProrated(Claim claim) {
        BigDecimal total = new BigDecimal(0.00);
        LineItemGroup travel1 = claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_DISTANCE);
        LineItemGroup travel2 = claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_TRIP);
        LineItemGroup travel3 = claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_HOURS);
        LineItemGroup travel4 = claim.getPayment().getLineItemGroup(Section.ADDITIONAL_TRAVEL_HOURS);
        LineItemGroup meals = claim.getPayment().getLineItemGroup(Section.MEALS);
        LineItemGroup parking = claim.getPayment().getLineItemGroup(Section.PARKING);
        LineItemGroup perDiem = claim.getPayment().getLineItemGroup(Section.PER_DIEM);
        LineItemGroup rentalCharges = claim.getPayment().getLineItemGroup(Section.RENTAL_CHARGES);
        LineItemGroup localPurchase = claim.getPayment().getLineItemGroup(Section.LOCAL_PURCHASE);
        LineItemGroup tolls = claim.getPayment().getLineItemGroup(Section.TOLLS);
        LineItemGroup otherFreightDuty = claim.getPayment().getLineItemGroup(Section.OTHER_FREIGHT_DUTY);
        LineItemGroup handlingFee = claim.getPayment().getLineItemGroup(Section.HANDLING_FEE);
        LineItemGroup transporatation = claim.getPayment().getLineItemGroup(Section.TRANSPORTATION_COST);
        LineItemGroup deductable = claim.getPayment().getLineItemGroup(Section.DEDUCTIBLE);
        LineItemGroup itemFreight = claim.getPayment().getLineItemGroup(Section.ITEM_FREIGHT_DUTY);
        if(!IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())){
        	 LineItemGroup others = claim.getPayment().getLineItemGroup(Section.OTHERS);
        	 if(others!=null){
        		 total = total.add(others.getAcceptedTotal().breachEncapsulationOfAmount()); 
        	 }
        }
        if (travel1 != null) {
            total = total.add(travel1.getAcceptedTotal().breachEncapsulationOfAmount());
        }

        if (travel2 != null) {
            total = total.add(travel2.getAcceptedTotal().breachEncapsulationOfAmount());
        }

        if (travel3 != null) {
            total = total.add(travel3.getAcceptedTotal().breachEncapsulationOfAmount());
        }
        if (travel4 != null) {
            total = total.add(travel4.getAcceptedTotal().breachEncapsulationOfAmount());
        }

        if (meals != null) {
            total = total.add(meals.getAcceptedTotal().breachEncapsulationOfAmount());
        }

        if (parking != null) {
            total = total.add(parking.getAcceptedTotal().breachEncapsulationOfAmount());
        }

        if (perDiem != null) {
            total = total.add(perDiem.getAcceptedTotal().breachEncapsulationOfAmount());
        }

        if (rentalCharges != null) {
            total = total.add(rentalCharges.getAcceptedTotal().breachEncapsulationOfAmount());
        }

        if (localPurchase != null) {
            total = total.add(localPurchase.getAcceptedTotal().breachEncapsulationOfAmount());
        }
        if (tolls != null) {
            total = total.add(tolls.getAcceptedTotal().breachEncapsulationOfAmount());
        }
        if (otherFreightDuty != null) {
            total = total.add(otherFreightDuty.getAcceptedTotal().breachEncapsulationOfAmount());
        }
        if (handlingFee != null) {
            total = total.add(handlingFee.getAcceptedTotal().breachEncapsulationOfAmount());
        }
        if (transporatation != null) {
            total = total.add(transporatation.getAcceptedTotal().breachEncapsulationOfAmount());
        }
        if(itemFreight!=null){
       	 total = total.add(itemFreight.getAcceptedTotal().breachEncapsulationOfAmount());
       }
        if (deductable != null) {
        	total=subtractDdeductableAmountFromTotalAmount(claim,total,deductable.getAcceptedTotal().breachEncapsulationOfAmount());
        }
        return total.negate();
    }
    private BigDecimal getMiscTotalWithoutProratedForStateMandate(Claim claim) {
        BigDecimal total = new BigDecimal(0.00);
        LineItemGroup travel1 = claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_DISTANCE);
        LineItemGroup travel2 = claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_TRIP);
        LineItemGroup travel3 = claim.getPayment().getLineItemGroup(Section.TRAVEL_BY_HOURS);
        LineItemGroup travel4 = claim.getPayment().getLineItemGroup(Section.ADDITIONAL_TRAVEL_HOURS);
        LineItemGroup meals = claim.getPayment().getLineItemGroup(Section.MEALS);
        LineItemGroup parking = claim.getPayment().getLineItemGroup(Section.PARKING);
        LineItemGroup perDiem = claim.getPayment().getLineItemGroup(Section.PER_DIEM);
        LineItemGroup rentalCharges = claim.getPayment().getLineItemGroup(Section.RENTAL_CHARGES);
        LineItemGroup localPurchase = claim.getPayment().getLineItemGroup(Section.LOCAL_PURCHASE);
        LineItemGroup tolls = claim.getPayment().getLineItemGroup(Section.TOLLS);
        LineItemGroup otherFreightDuty = claim.getPayment().getLineItemGroup(Section.OTHER_FREIGHT_DUTY);
        LineItemGroup transportation = claim.getPayment().getLineItemGroup(Section.TRANSPORTATION_COST);
        LineItemGroup handlingFee = claim.getPayment().getLineItemGroup(Section.HANDLING_FEE);
        LineItemGroup deductable = claim.getPayment().getLineItemGroup(Section.DEDUCTIBLE);
        LineItemGroup itemFreight = claim.getPayment().getLineItemGroup(Section.ITEM_FREIGHT_DUTY);
        
        if(!IntegrationLayerUtil.isAMERBusinessUnit(claim.getBusinessUnitInfo().getName())){
       	 LineItemGroup others = claim.getPayment().getLineItemGroup(Section.OTHERS);
       	 if(others!=null){
       		 total = total.add(travel1.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount()); 
       	 }
       }
        if (travel1 != null) {
            total = total.add(travel1.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }

        if (travel2 != null) {
            total = total.add(travel2.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }

        if (travel3 != null) {
            total = total.add(travel3.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }
        if (travel4 != null) {
            total = total.add(travel4.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }
        if (meals != null) {
            total = total.add(meals.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }

        if (parking != null) {
            total = total.add(parking.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }

        if (perDiem != null) {
            total = total.add(perDiem.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }

        if (rentalCharges != null) {
            total = total.add(rentalCharges.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }
       
        if (localPurchase != null) {
            total = total.add(localPurchase.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }
        if (tolls != null) {
            total = total.add(tolls.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }
        if (otherFreightDuty != null) {
            total = total.add(otherFreightDuty.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }
        if (transportation != null) {
            total = total.add(transportation.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        } 
        if (handlingFee != null) {
            total = total.add(handlingFee.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }
        if (itemFreight != null) {
            total = total.add(itemFreight.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }
        if (deductable != null) {
        	total=subtractDdeductableAmountFromTotalAmount(claim,total,deductable.getGroupTotalStateMandateAmount().breachEncapsulationOfAmount());
        }
        return total.negate();
    }

	private BigDecimal subtractDdeductableAmountFromTotalAmount(Claim claim,
			BigDecimal total, BigDecimal currentValue) {
		BigDecimal totalChangedAmount = new BigDecimal(0.00);
		MathContext mc= new MathContext(0);
		totalChangedAmount=total.subtract(currentValue, mc);
		logger.error(totalChangedAmount);
		return totalChangedAmount;
	}

	private LineItem[] getLineItemDtoArray(Map<Integer, LineItem> lineItemDtoList, Claim claim) {
		boolean currencyConversion = claimCurrencyConversionAdvice.getCurrencyForERPInteractions(claim)
				.equalsIgnoreCase(claim.getCurrencyForCalculation().getCurrencyCode()) ? false : true;
		if (logger.isInfoEnabled()) {
			logger
					.info("ProcessGlobalClaim :: getLineItemDtoArray , lineItemDtoList.size :  "
							+ lineItemDtoList.size());
		}
		LineItem[] lineItemTypeDTOs = new LineItem[lineItemDtoList.size()];
		int i = 0;
		Currency claimCurrency= claim.getCurrencyForCalculation();
		Currency erpCurrency = Currency.getInstance(claimCurrencyConversionAdvice.getCurrencyForERPInteractions(claim));
		CalendarDate repairDate = claim.getRepairDate();
		for (Integer category : lineItemDtoList.keySet()) {
			if (currencyConversion) {
				SubItem[] sub = lineItemDtoList.get(category).getSubItemArray();
				if (sub != null && sub.length > 0) {
					for (SubItem subItem : sub) {
						subItem.setAmount(amountInERPCurrency(subItem.getAmount(),repairDate,claimCurrency,erpCurrency));
					}
				}
				lineItemDtoList.get(category).setAmount(
						amountInERPCurrency(lineItemDtoList.get(category).getAmount(), repairDate,claimCurrency,erpCurrency));
				lineItemDtoList.get(category).setTotalAmount(
						amountInERPCurrency(lineItemDtoList.get(category).getTotalAmount(), repairDate,claimCurrency,erpCurrency));

			}
			lineItemTypeDTOs[i++] = lineItemDtoList.get(category);
		}
		if (logger.isInfoEnabled()) {
			logger.info("ProcessGlobalClaim :: getLineItemDtoArray , 30 :    ");
		}
		return lineItemTypeDTOs;
	}
    
	private BigDecimal amountInERPCurrency(BigDecimal currentValue, CalendarDate repairDate,Currency claimCurrency,Currency erpCurrency) {
		Money moneyToConvert = new Money(currentValue,claimCurrency);
		return claimCurrencyConversionAdvice.convertMoneyUsingAppropriateConFactor(moneyToConvert, repairDate,
				erpCurrency).breachEncapsulationOfAmount();
	}

    private XmlOptions createXMLOptions() {
        // Generate the XML document
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSavePrettyPrint();
        xmlOptions.setSavePrettyPrintIndent(4);
        xmlOptions.setSaveAggressiveNamespaces();
        xmlOptions.setUseDefaultNamespace();
        return xmlOptions;
    }

    /**
     * This is the most lousy logic to populate the part replacement list on
     * reopening. Badly need a revisit...leaving it for now.
     * <p/>
     * Requirement : Dealer submits a claim for the first time. Processor
     * approves it, credit submission includes all parts Processor reopens,
     * changes the replaced club car parts. In case of reopen, only delta parts
     * should go. If claim was approved initially with part A, quantity 2, and
     * processor on reopen modifies it to quantity 3, the credit submission
     * should go with part A, quantity 1.
     * <p/>
     * All the parts that are added afresh or deleted should be included as
     * well.
     *
     * @param currentPartInfo
     * @param previousPartInfo
     * @return
     */
    private List<PartPaymentInfo> populatePartList(
			List<PartPaymentInfo> currentPartInfo,
			List<PartPaymentInfo> previousPartInfo, boolean flag) {

		// NOTE: Equals is over-ridden and checks only for the part number.

		// This would be the final list that would have all the delta parts.
		List<PartPaymentInfo> finalList = new ArrayList<PartPaymentInfo>();

		// If no parts is found in current list, ie. all parts are removed, need
		// to send all.
		if (currentPartInfo.isEmpty()) {
			finalList.addAll(previousPartInfo);
		}

		// Case when parts are added for the first time, add all.
		if (previousPartInfo.isEmpty()) {
			finalList.addAll(currentPartInfo);
		}

		// Else,
		if (currentPartInfo.size() > 0 && previousPartInfo.size() > 0) {

			// Contains all of current not present in previous
			List<PartPaymentInfo> onlyCurrentList = new ArrayList<PartPaymentInfo>();

			// Contains intersection of current and previous
			List<PartPaymentInfo> intersectionList = new ArrayList<PartPaymentInfo>();

			// Contains all of previous not present in current
			List<PartPaymentInfo> onlyPreviousList = new ArrayList<PartPaymentInfo>();
            onlyCurrentList.addAll(currentPartInfo);
            intersectionList.addAll(currentPartInfo);
            onlyPreviousList.addAll(previousPartInfo);

            // Populate the intersection.
            intersectionList.retainAll(onlyPreviousList);

            // Remove the intersection from the other lists.
            onlyCurrentList.removeAll(intersectionList);
            onlyPreviousList.removeAll(intersectionList);
            

			for (PartPaymentInfo deletedPartInfo : onlyPreviousList) {
				deletedPartInfo.setQuantity(-deletedPartInfo.getQuantity());
				finalList.add(deletedPartInfo);
			}

			// If the claim state is Denied then modifying the PartPaymentInfo
			// with Negative Quantity.
			if (flag) {
				for (PartPaymentInfo deniedPartInfo : currentPartInfo) {
					deniedPartInfo.setQuantity(-deniedPartInfo.getQuantity());
					finalList.add(deniedPartInfo);
				}
			} else {

				// All non intersecting parts in current and previous should be
				// added.
				finalList.addAll(onlyCurrentList);

				// Iterate through the intersection list and get the delta of
				// the
				// quantity.
				for (int i = 0; i < intersectionList.size(); i++) {
					PartPaymentInfo discrepantPart = intersectionList.get(i);
					for (int j = 0; j < previousPartInfo.size(); j++) {
						PartPaymentInfo previousPart = previousPartInfo.get(j);
						if (discrepantPart.getPartNumber().equals(
								previousPart.getPartNumber())) {
							PartPaymentInfo deltaPart = discrepantPart
									.diff(previousPart);
							if (deltaPart != null) {
								finalList.add(deltaPart);
							}
							continue;
						}

					}

				}

			}
		}

		return finalList;
	}

    public void setIntegrationPropertiesBean(
            IntegrationPropertiesBean integrationPropertiesBean) {
        this.integrationPropertiesBean = integrationPropertiesBean;
    }

    private void populateTaskLogicalId(Sender senderTypeDTO, Claim claim) {
        senderTypeDTO.setTask(integrationPropertiesBean.getTaskIdForCreditSubmission());
        senderTypeDTO.setLogicalId(integrationPropertiesBean.getLogicalIdForCreditSubmission());
    }

    private String populateItemNumber(Claim claim, ClaimedItem claimedItem) {
        return claimedItem.getItemReference().getUnserializedItem().getNumber();
	}

    public void setConfigValueService(ConfigValueService configValueService) {
        this.configValueService = configValueService;
    }
	
	public CurrencyConversionAdvice getClaimCurrencyConversionAdvice() {
		return claimCurrencyConversionAdvice;
	}

	public void setClaimCurrencyConversionAdvice(CurrencyConversionAdvice claimCurrencyConversionAdvice) {
		this.claimCurrencyConversionAdvice = claimCurrencyConversionAdvice;
	}
	
	public OrgService getOrgService() {
		return orgService;
	}

	public void setOrgService(OrgService orgService) {
		this.orgService = orgService;
	}
	public void setPolicyDefinitionRepository(
			PolicyDefinitionRepository policyDefinitionRepository) {
		this.policyDefinitionRepository = policyDefinitionRepository;
	}
}
