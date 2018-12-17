package tavant.twms.integration.layer.transformer.global.dealerinterfaces.unitservicehistory;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCalendar;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import tavant.twms.dateutil.TWMSDateFormatUtil;
import tavant.twms.domain.campaign.Campaign;
import tavant.twms.domain.campaign.CampaignNotification;
import tavant.twms.domain.campaign.CampaignService;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.common.I18nDomainTextReader;
import tavant.twms.domain.common.ManufacturingSiteInventory;
import tavant.twms.domain.common.Oem;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemComposition;
import tavant.twms.domain.inventory.InventoryItemCondition;
import tavant.twms.domain.inventory.InventoryItemUtil;
import tavant.twms.domain.inventory.InventoryService;
import tavant.twms.domain.inventory.InventoryTransaction;
import tavant.twms.domain.orgmodel.Address;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.policy.Customer;
import tavant.twms.domain.policy.PolicyException;
import tavant.twms.domain.policy.PolicyService;
import tavant.twms.domain.policy.RegisteredPolicy;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.time.TimePoint;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryrequest.UnitServiceHistoryRequestDocumentDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.AddressInformationDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.ClaimDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.FieldModificationDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.InventoryItemDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.InventoryItemDTO.ClaimHistory;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.InventoryItemDTO.FieldModification;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.InventoryItemDTO.MajorComponents;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.InventoryItemDTO.TransactionHistory;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.InventoryItemDTO.WarrantyCoverages;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.InventoryTransactionDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.MajorComponentDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.OwnerInformationDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.ServiceProviderDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.UnitServiceHistoryResponseDocumentDTO;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.UnitServiceHistoryResponseDocumentDTO.UnitServiceHistoryResponse;
import com.tavant.dealerinterfaces.unitservicehistory.unitservicehistoryresponse.WarrantyCoverageDTO;

/**
 * This class is used to convert response and request objects for
 * GetUnitServiceHistory web service.
 * 
 * @author TWMSUSER
 */
public class UnitServiceHistoryTransformer {
	private CampaignService campaignService;

	private PolicyService policyService;

	private InventoryItemUtil inventoryItemUtil;

	private I18nDomainTextReader i18nDomainTextReader;

	private ConfigParamService configParamService;

	private InventoryService inventoryService;

	private final Logger logger = Logger.getLogger("dealerAPILogger");

	public UnitServiceHistoryRequestDocumentDTO convertXMLToRequestDTO(String src) throws XmlException {
		UnitServiceHistoryRequestDocumentDTO dto = null;
		if (!StringUtils.isBlank(src)) {

			try {
				dto = UnitServiceHistoryRequestDocumentDTO.Factory.parse(src);

				// Create an XmlOptions instance and set the error listener.
				XmlOptions validateOptions = new XmlOptions();
				ArrayList errorList = new ArrayList();
				validateOptions.setErrorListener(errorList);

				if (!dto.validate(validateOptions)) {

					XmlError error = (XmlError) errorList.get(0);
					throw new XmlException(error.getMessage());
				}
			} catch (XmlException xe) {
				logger.error("Error in XML parsing or validation", xe);
				throw xe;
			}
		}
		return dto;
	}

	public boolean canViewStockInventoryItem(InventoryItem inventoryItem) {
		boolean toReturn = false;
		boolean customerTypeAllowed = inventoryService.customersBelongsToConfigParam(inventoryItem);
		if (inventoryItemUtil.isInternalUser() || inventoryItemUtil.checkLoggedInDealerOwner(inventoryItem)
				|| customerTypeAllowed) {
			toReturn = true;
		}
		return toReturn;
	}

	/**
	 * This method will sets the InventoryItem data into
	 * UnitServiceHistoryResponseDocumentDTO object
	 * 
	 * @param inventoryItem
	 * @return UnitServiceHistoryResponseDocumentDTO
	 */
	public UnitServiceHistoryResponseDocumentDTO convertBeanToResponseDTO(InventoryItem inventoryItem) {
		UnitServiceHistoryResponseDocumentDTO unitServiceHistoryRespDocDTO = UnitServiceHistoryResponseDocumentDTO.Factory
				.newInstance();
		UnitServiceHistoryResponse unitServiceHistoryRespDTO = UnitServiceHistoryResponse.Factory.newInstance();
		CalendarDate dateObj = null;

		InventoryItemDTO inventoryItemDTO = InventoryItemDTO.Factory.newInstance();
		Collections.sort(inventoryItem.getTransactionHistory());
		setRetailingDealerInfo(inventoryItemDTO, inventoryItem);
		inventoryItemDTO.setSerialNumber(inventoryItem.getSerialNumber());
		inventoryItemDTO.setBusinessUnitName(inventoryItem.getBusinessUnitInfo().getName());
		/**
		 * Manufacturing site to be populated only if the BU config param allows
		 * it
		 */
		SelectedBusinessUnitsHolder.setSelectedBusinessUnit(inventoryItem.getBusinessUnitInfo().getName());
		Boolean canExternalUserViewManufSite = this.configParamService
				.getBooleanValue(ConfigName.MANUFACTURING_SITE_VISIBLE.getName());
		if (canExternalUserViewManufSite) {
			ManufacturingSiteInventory manufacturingSite = inventoryItem.getManufacturingSiteInventory();
			if (manufacturingSite == null) {
				inventoryItemDTO.setManufacturingSite("");
			} else {
				inventoryItemDTO.setManufacturingSite(manufacturingSite.getBuAppendedName());
			}
		}
		if (inventoryItem.getOfType().getProduct() != null) {
			String productName = inventoryItem.getOfType().getProduct().getName();
			if (productName == null) {
				productName = "";
			}
			inventoryItemDTO.setProduct(productName);
			
			if (inventoryItem.getOfType().getProduct().getIsPartOf() != null) {
				
				String productType = inventoryItem.getOfType().getProduct().getIsPartOf().getName();
				if (productType == null) {
					productType = "";
				}
				inventoryItemDTO.setProductType(productType);
			} else {
				inventoryItemDTO.setProductType("");
			}
		} else {
			inventoryItemDTO.setProduct("");
			inventoryItemDTO.setProductType("");
		}
		String modelNumber = inventoryItem.getOfType().getModel().getName();
		if (modelNumber == null) {
			modelNumber = "";
		}
		inventoryItemDTO.setModelNumber(modelNumber);
		String itemNumber = inventoryItem.getOfType().getNumber();
		if (itemNumber == null) {
			itemNumber = "";
		}
		inventoryItemDTO.setItemNumber(itemNumber);

		if (inventoryItem.getSerializedPart()) {
			InventoryItem installedInventoryItem = inventoryService.findInventoryItemForMajorComponent(inventoryItem
					.getId());
			if (installedInventoryItem != null) {
				inventoryItemDTO.setInstalledOnUnit(installedInventoryItem.getSerialNumber());
			} else {
				inventoryItemDTO.setInstalledOnUnit("");
			}
		} else {
			inventoryItemDTO.setInstalledOnUnit("");
		}
		String itemDesc = inventoryItem.getOfType().getDescription();
		if (itemDesc == null) {
			itemDesc = "";
		}
		inventoryItemDTO.setItemDescription(itemDesc);
		String itemCondition = inventoryItem.getConditionType().getItemCondition(); 
		if (InventoryItemCondition.NEW.getItemCondition().equals(itemCondition)) {
			inventoryItemDTO
					.setItemCondition(i18nDomainTextReader.getProperty("label.warrantyAdmin.itemCondition.new"));
		} else if (InventoryItemCondition.SCRAP.getItemCondition().equals(itemCondition)) {
			inventoryItemDTO.setItemCondition(i18nDomainTextReader
					.getProperty("label.warrantyAdmin.itemCondition.scrap"));
		} else {
			inventoryItemDTO.setItemCondition(i18nDomainTextReader
					.getProperty("label.warrantyAdmin.itemCondition.refurbished"));
		} 
		dateObj = inventoryItem.getShipmentDate();
		if (dateObj != null) {
			inventoryItemDTO.setShipmentDate(CalendarUtil.convertToXMLCalendar(dateObj));
		} else {
			inventoryItemDTO.setShipmentDate(new XmlCalendar());
		}
		if (inventoryItem.getHoursOnMachine() == null) {
			inventoryItemDTO.setHoursInService(null);
		} else {
			inventoryItemDTO.setHoursInService(BigInteger.valueOf(inventoryItem.getHoursOnMachine().longValue()));
		}

		String ownerShip = inventoryItem.getOwnershipState().getName();
		if (ownerShip == null) {
			ownerShip = "";
		}
		inventoryItemDTO.setOwnership(ownerShip);
		if (inventoryItemUtil.isDRDoneByLoggedInUser(inventoryItem)
				|| inventoryItemUtil.isInventoryFullView(inventoryItem)) {

			String salesOrderNumber = inventoryItem.getSalesOrderNumber();
			if (salesOrderNumber != null) {
				inventoryItemDTO.setSalesOrderNumber(salesOrderNumber);
			}
			String invoiceNumber = inventoryItem.getInvoiceNumber();
			if (invoiceNumber != null) {
				inventoryItemDTO.setInvoiceNumber(invoiceNumber);
			}			
		}
		if (inventoryItem.isRetailed()) {
			dateObj = inventoryItem.getDeliveryDate();
			if (dateObj != null) {
				inventoryItemDTO.setDateofDelivery(CalendarUtil.convertToXMLCalendar(dateObj));
			} else {
				inventoryItemDTO.setDateofDelivery(new XmlCalendar());
			}			
			dateObj = inventoryItem.getWntyStartDate();
			if (dateObj != null) {
				inventoryItemDTO.setWarrantyStartDate(CalendarUtil.convertToXMLCalendar(dateObj));
			} else {
				inventoryItemDTO.setWarrantyStartDate(new XmlCalendar());
			}
			dateObj = inventoryItem.getWntyEndDate();
			if (dateObj != null) {
				inventoryItemDTO.setWarrantyEndDate(CalendarUtil.convertToXMLCalendar(dateObj));
			} else {
				inventoryItemDTO.setWarrantyEndDate(new XmlCalendar());
			}
		}
		dateObj = inventoryItem.getInstallationDate();
		if (dateObj != null) {
			inventoryItemDTO.setDateOfInstallation(CalendarUtil.convertToXMLCalendar(dateObj));
		} else {
			inventoryItemDTO.setDateOfInstallation(new XmlCalendar());
		}
				
		String truckNumber = inventoryItem.getFleetNumber();
		if (truckNumber == null) {
			truckNumber = "";
		}
		inventoryItemDTO.setTruckNumber(truckNumber);
		
		String equipmentVINID = inventoryItem.getVinNumber();
		if(equipmentVINID == null) {
			equipmentVINID = "";
		}
		inventoryItemDTO.setEquipmentVINID(equipmentVINID);
		
		Customer operator = inventoryItem.getOperator();
		if (operator != null) {
			inventoryItemDTO.setOperator(operator.getCompanyName());
		} else {
			inventoryItemDTO.setOperator("");
		}
		
		Oem oem = inventoryItem.getOem();
		if (oem != null) {
			inventoryItemDTO.setOEM(oem.getDescription());
		} else {
			inventoryItemDTO.setOEM("");
		}
		
		Party installingDealer = inventoryItem.getInstallingDealer();
		if (installingDealer != null) {
			inventoryItemDTO.setInstallingDealer(installingDealer.getName());
		} else {
			inventoryItemDTO.setInstallingDealer("");
		}
		
		setOwnerInfo(inventoryItemDTO, inventoryItem);
		setTransactionHistory(inventoryItemDTO, inventoryItem);
		setClaimHistory(inventoryItemDTO, inventoryItem);
		setFieldModification(inventoryItemDTO, inventoryItem);
		setWarrantyCoverage(inventoryItemDTO, inventoryItem);
		setMajorComponent(inventoryItemDTO, inventoryItem);
		unitServiceHistoryRespDTO.setInventoryItem(inventoryItemDTO);
		unitServiceHistoryRespDocDTO.setUnitServiceHistoryResponse(unitServiceHistoryRespDTO);
		return unitServiceHistoryRespDocDTO;
	}

	/**
	 * The method populates the major component history data into
	 * InventoryItemDTO
	 * 
	 * @param inventoryItemDTO
	 * @param inventoryItem
	 */
	private void setMajorComponent(InventoryItemDTO inventoryItemDTO, InventoryItem inventoryItem) {
		MajorComponents majorComponentsDTO = MajorComponents.Factory.newInstance();
		List<InventoryItemComposition> invItemCompositionList = inventoryItem.getComposedOf();
		CalendarDate dateObj = null;
		if (invItemCompositionList != null && !invItemCompositionList.isEmpty()) {
			int totalSize = invItemCompositionList.size();
			MajorComponentDTO[] majorComponentDTO = new MajorComponentDTO[totalSize];
			for (int index = 0; index < totalSize; index++) {
				majorComponentDTO[index] = MajorComponentDTO.Factory.newInstance();
				InventoryItemComposition itemComposition = invItemCompositionList.get(index);
				InventoryItem item = itemComposition.getPart();
				majorComponentDTO[index].setSerialNumber(item.getSerialNumber());
				majorComponentDTO[index].setDescription(item.getOfType().getDescription());
				majorComponentDTO[index].setPartNumber(item.getOfType().getNumber());
				dateObj = item.getInstallationDate();
				if (dateObj != null) {
					majorComponentDTO[index].setInstallDate(CalendarUtil.convertToXMLCalendar(dateObj));
				} else {
					majorComponentDTO[index].setInstallDate(new XmlCalendar());
				}
			
				dateObj = item.getWntyStartDate();
				if (dateObj != null) {
					majorComponentDTO[index].setWarrantyStartDate(CalendarUtil.convertToXMLCalendar(dateObj));
				} else {
					majorComponentDTO[index].setWarrantyStartDate(new XmlCalendar());
				}
				dateObj = item.getWntyEndDate();
				if (dateObj != null) {
					majorComponentDTO[index].setWarrantyEndDate(CalendarUtil.convertToXMLCalendar(dateObj));
				} else {
					majorComponentDTO[index].setWarrantyEndDate(new XmlCalendar());
				}					
			}
			majorComponentsDTO.setMajorComponentDetailsArray(majorComponentDTO);
		}
		inventoryItemDTO.setMajorComponents(majorComponentsDTO);
	}

	/**
	 * The method populates the claim history data into InventoryItemDTO
	 * 
	 * @param inventoryItemDTO
	 * @param inventoryItem
	 */
	private void setClaimHistory(InventoryItemDTO inventoryItemDTO, InventoryItem inventoryItem) {
		Collection<Claim> previousClaims = inventoryItemUtil.getClaimsToBeViewed(inventoryItem);
		ClaimHistory cliamHistoryDTO = ClaimHistory.Factory.newInstance();
		if (previousClaims != null) {
			ClaimDTO[] claims = null;
			Iterator<Claim> iterator = previousClaims.iterator();
			List<ClaimDTO> claimList = new ArrayList<ClaimDTO>();
			while (iterator.hasNext()) {
				Claim claim = iterator.next();
				CalendarDate dateObj = null;
				ClaimDTO claimDTO = ClaimDTO.Factory.newInstance();
				claimDTO.setClaimNumber(claim.getClaimNumber());
				dateObj = claim.getFiledOnDate();
				if (dateObj != null) {
					claimDTO.setDateOfClaim(CalendarUtil.convertToXMLCalendar(dateObj));
				} else {
					claimDTO.setDateOfClaim(new XmlCalendar());
				}
				dateObj = claim.getFailureDate();
				if (dateObj != null) {
					claimDTO.setFailureDate(CalendarUtil.convertToXMLCalendar(dateObj));
				} else {
					claimDTO.setFailureDate(new XmlCalendar());
				}

				claimDTO.setStatus(claim.getState().getState());
				List<OEMPartReplaced> partReplacedList = claim.getServiceInformation().getServiceDetail()
						.getOemPartsReplaced();
				if (partReplacedList != null && !partReplacedList.isEmpty()) {
					int numberOfPartsReplaced = 0;
					StringBuffer dataBuff = new StringBuffer();
					for (OEMPartReplaced partReplaced : partReplacedList) {
						Item item = null;
						if (partReplaced.getItemReference().getReferredItem() != null) {
							item = partReplaced.getItemReference().getReferredItem();
						} else if (partReplaced.getItemReference().getReferredInventoryItem() != null) {
							item = partReplaced.getItemReference().getReferredInventoryItem().getOfType();
						}
						if (item != null) {
							String itemDescription = item.getDescription();
							String alteranteNumber = item.getAlternateNumber();
							if (numberOfPartsReplaced > 0) {
								dataBuff.append(" ");
							}
							dataBuff.append(alteranteNumber + "-" + itemDescription);
							numberOfPartsReplaced++;
						}
					}
					claimDTO.setIRPartsReplaced(dataBuff.toString());
				}

				String faultLocation = claim.getServiceInformation().getFaultCode();
				if (faultLocation == null) {
					faultLocation = "";
				}
				claimDTO.setFaultLocation(faultLocation);
				claimDTO.setHoursInService(claim.getHoursInService());
				if (inventoryItemUtil.isRetailedDealer()) {
					Money totalAmountPaidAfterTax = claim.getPayment().getTotalAmountPaidAfterTax();
					if (totalAmountPaidAfterTax != null) {
						claimDTO.setTotalAmountCredited(totalAmountPaidAfterTax.breachEncapsulationOfAmount());
					}
				}
				claimList.add(claimDTO);
			}
			claims = new ClaimDTO[claimList.size()];
			for (int index = 0; index < claimList.size(); index++)
				claims[index] = claimList.get(index);
			cliamHistoryDTO.setClaimDetailsArray(claims);
		}
		inventoryItemDTO.setClaimHistory(cliamHistoryDTO);
	}


	/**
	 * The method populates the retailing dealer information into
	 * inventoryItemDTO
	 * 
	 * @param inventoryItemDTO
	 * @param inventoryItem
	 */
	private void setRetailingDealerInfo(InventoryItemDTO inventoryItemDTO, InventoryItem inventoryItem) {
		ServiceProvider serviceProvider = (ServiceProvider) inventoryItemUtil.getRetailedDealer(inventoryItem);
		ServiceProviderDTO retailingDealerDTO = ServiceProviderDTO.Factory.newInstance();
		if (inventoryItemUtil.isRetailedDealer() || inventoryItemUtil.isInventoryFullView(inventoryItem)) {
			AddressInformationDTO addressDTO = AddressInformationDTO.Factory.newInstance();
			setAddress(addressDTO, serviceProvider);
			retailingDealerDTO.setServiceProviderNumber(serviceProvider.getServiceProviderNumber());
			retailingDealerDTO.setName(serviceProvider.getName());
			String partyType = serviceProvider.getType();
			if (partyType == null) {
				partyType = "";
			}
			retailingDealerDTO.setType(partyType);
			retailingDealerDTO.setAddress(addressDTO);
		}
		inventoryItemDTO.setRetailingDealer(retailingDealerDTO);
	}

	/**
	 * The method populates the AddressInformationDTO
	 * 
	 * @param addressDTO
	 * @param organization
	 */
	private void setAddress(AddressInformationDTO addressDTO, Party organization) {
		Address address = organization.getAddress();		
		String addressLine1 = address.getAddressLine1();
		if (StringUtils.isEmpty(addressLine1)) {
			addressLine1="";
		}
		addressDTO.setAddress(addressLine1);		
		String city = address.getCity();
		if (StringUtils.isEmpty(city)) {
			city="";
		}
		addressDTO.setCity(city);
		String state = address.getState();
		if (StringUtils.isEmpty(state)) {
			state="";
		}
		addressDTO.setState(state);
		String country = address.getCountry();
		if (StringUtils.isEmpty(country)) {
			country="";
		}
		addressDTO.setCountry(country);
		String zipCode = address.getZipCode();
		if (StringUtils.isEmpty(zipCode)) {
			zipCode="";
		}
		addressDTO.setZipcode(zipCode);
	}

	/**
	 * The method populates the owner information into InventoryItemDTO.
	 * 
	 * @param inventoryItemDTO
	 * @param inventoryItem
	 */
	private void setOwnerInfo(InventoryItemDTO inventoryItemDTO, InventoryItem inventoryItem) {
		OwnerInformationDTO ownerInfoDTO = OwnerInformationDTO.Factory.newInstance();
				
		if (inventoryItem.isRetailed()
				&& (inventoryItemUtil.isInventoryFullView(inventoryItem) || (inventoryItemUtil
						.isDifferentDealerAndOwner(inventoryItem) && inventoryItemUtil
						.isCanViewOwnerInfo(inventoryItem)))) {
			AddressInformationDTO addressDTO = AddressInformationDTO.Factory.newInstance();			
			
			Party party = inventoryItemUtil.getInventoryItemOwner(inventoryItem);
			String ownerName = party.getName();
			if (StringUtils.isEmpty(ownerName)) {
				String companyName = party.getCompanyName();
				if (companyName == null) {
					companyName = "";
				}
				ownerInfoDTO.setName(companyName);
			} else {
				ownerInfoDTO.setName(ownerName);
			}
			String partyType = party.getType();
			if (partyType == null) {
				partyType = "";
			}
			ownerInfoDTO.setType(partyType);
			setAddress(addressDTO, party);
			ownerInfoDTO.setAddress(addressDTO);
		}
		inventoryItemDTO.setOwnerInformation(ownerInfoDTO);
	}

	/**
	 * The method populates the claim history data into InventoryItemDTO.
	 * 
	 * @param inventoryItemDTO
	 * @param inventoryItem
	 */
	private void setTransactionHistory(InventoryItemDTO inventoryItemDTO, InventoryItem inventoryItem) {
		List<InventoryTransaction> inventoryTransactionList = inventoryItem.getTransactionHistory();
		int inventoryTrnxListSize = inventoryTransactionList.size();
		InventoryTransactionDTO[] invTransactionDTO = new InventoryTransactionDTO[inventoryTrnxListSize];
		TransactionHistory transactionHistoryDTO = TransactionHistory.Factory.newInstance();
		for (int index = 0; index < inventoryTrnxListSize; index++) {
			invTransactionDTO[index] = InventoryTransactionDTO.Factory.newInstance();
			InventoryTransaction invTrans = inventoryTransactionList.get(index);
			if (inventoryItemUtil.isLoggedInUserOwnerOfTrnx(invTrans)
					|| inventoryItemUtil.isInventoryFullView(inventoryItem)) {
				invTransactionDTO[index].setTransactionDate(CalendarUtil.convertToXMLCalendar(invTrans
						.getTransactionDate()));
				String sellerName = invTrans.getSeller().getDisplayName();
				String buyerName = invTrans.getBuyer().getDisplayName();
				if (StringUtils.isEmpty(sellerName)) {
					invTransactionDTO[index].setFromCompany(invTrans.getSeller().getCompanyName());
				} else {
					invTransactionDTO[index].setFromCompany(sellerName);
				}
				if (StringUtils.isEmpty(buyerName)) {
					invTransactionDTO[index].setToCompany(invTrans.getBuyer().getCompanyName());
				} else {
					invTransactionDTO[index].setToCompany(buyerName);
				}
				invTransactionDTO[index].setCustomerType(invTrans.getBuyer().getType());
				String invoiceNumber = invTrans.getInvoiceNumber();
				if (invoiceNumber == null) {
					invoiceNumber = "";
				}
				invTransactionDTO[index].setInvoiceNumber(invoiceNumber);
			} else {
				invTransactionDTO[index].setTransactionDate(new XmlCalendar());
				invTransactionDTO[index].setFromCompany("");
				invTransactionDTO[index].setToCompany("");
				invTransactionDTO[index].setCustomerType("");
				invTransactionDTO[index].setInvoiceNumber("");
			}
			invTransactionDTO[index].setTransactionType(invTrans.getInvTransactionType().getTrnxTypeValue());
		}
		transactionHistoryDTO.setTransactionDetailsArray(invTransactionDTO);
		inventoryItemDTO.setTransactionHistory(transactionHistoryDTO);

	}

	/**
	 * The method populates FieldModification history into InventoryItemDTO.
	 * 
	 * @param inventoryItemDTO
	 * @param inventoryItem
	 */
	private void setFieldModification(InventoryItemDTO inventoryItemDTO, InventoryItem inventoryItem) {
		List<CampaignNotification> fieldModNotificationList = campaignService.findNotificationsForItem(inventoryItem);
		int fieldModificationListListSize = fieldModNotificationList.size();
		FieldModification fieldModificationDTO = FieldModification.Factory.newInstance();
		CalendarDate dateObj = null;
		if (fieldModificationListListSize > 0) {
			FieldModificationDTO[] fieldModificationDetailsDTO = null;
			fieldModificationDetailsDTO = new FieldModificationDTO[fieldModificationListListSize];
			for (int index = 0; index < fieldModificationListListSize; index++) {
				fieldModificationDetailsDTO[index] = FieldModificationDTO.Factory.newInstance();
				CampaignNotification fieldModNotification = fieldModNotificationList.get(index);
				Campaign fieldMod = fieldModNotification.getCampaign();
				fieldModificationDetailsDTO[index].setFieldModificationCode(fieldMod.getCode());
				
				String desc = fieldMod.getDescription();
				if (desc == null) {
					desc = "";
				}
				fieldModificationDetailsDTO[index].setDescription(desc);
				dateObj = fieldMod.getBuildTillDate();
				if (dateObj != null) {
					fieldModificationDetailsDTO[index].setEndDate(CalendarUtil.convertToXMLCalendar(dateObj));
				} else {
					fieldModificationDetailsDTO[index].setEndDate(new XmlCalendar());
				}
				fieldModificationDetailsDTO[index].setFieldModificationClass(fieldMod.getCampaignClass()
						.getDescription());
				fieldModificationDetailsDTO[index].setStatus(fieldModNotification.getNotificationStatus());

			}
			fieldModificationDTO.setFieldModificationDetailsArray(fieldModificationDetailsDTO);
		}
		inventoryItemDTO.setFieldModification(fieldModificationDTO);
	}

	/**
	 * The method populates the WarrantyCoverages in InventoryItemDTO.
	 * 
	 * @param inventoryItemDTO
	 * @param inventoryItem
	 */
	private void setWarrantyCoverage(InventoryItemDTO inventoryItemDTO, InventoryItem inventoryItem) {
		try {
			WarrantyCoverages warrantyCoveragesDTO = WarrantyCoverages.Factory.newInstance();

			if (inventoryItem.isRetailed()) {

				List<RegisteredPolicy> registeredPolicies = this.policyService.findRegisteredPolicies(inventoryItem);

				List<Map<String, Object>> policyList = inventoryItemUtil.getPolicyDetails(registeredPolicies,
						inventoryItem, false);

				int totalWarranty = policyList.size();

				WarrantyCoverageDTO[] warrantyDTO = null;

				if (totalWarranty > 0) {
					warrantyDTO = new WarrantyCoverageDTO[totalWarranty];
					boolean isDRDoneByLoggedInUser = inventoryItemUtil.isDRDoneByLoggedInUser(inventoryItem);
					for (int index = 0; index < totalWarranty; index++) {
						Map<String, Object> policyMap = policyList.get(index);
						String monthsCovered = (String) policyMap.get(InventoryItemUtil.MONTHS_COVERED);
						Integer hoursCovered = (Integer) policyMap.get(InventoryItemUtil.HOURS_COVERED);
						warrantyDTO[index] = WarrantyCoverageDTO.Factory.newInstance();
						warrantyDTO[index].setPolicyName((String) policyMap.get(InventoryItemUtil.POLICY_NAME));
						warrantyDTO[index].setPolicyCode((String) policyMap.get(InventoryItemUtil.POLICY_CODE));

						String termsAndConditions = (String) policyMap.get(InventoryItemUtil.POLICY_TERMS_CONDITIONS);
						if (termsAndConditions == null) {
							termsAndConditions = "";
						}
						warrantyDTO[index].setTermsAndConditions(termsAndConditions);
						warrantyDTO[index].setType((String) policyMap.get(InventoryItemUtil.TYPE));
						if (!StringUtils.isEmpty(monthsCovered)) {
							warrantyDTO[index].setMonthsCoveredFromDateOfDelivery(new BigInteger(monthsCovered));
						}
						if (hoursCovered != null && !StringUtils.isEmpty(hoursCovered.toString())) {
							warrantyDTO[index].setHoursCovered(new BigInteger(hoursCovered.toString()));
						}
						String startDate = (String) policyMap.get(InventoryItemUtil.START_DATE);
						if (StringUtils.isEmpty(startDate)) {
							warrantyDTO[index].setStartDate(new XmlCalendar());
						} else {
							warrantyDTO[index].setStartDate(CalendarUtil
									.convertToXMLCalendar(convertToCalendarDate(startDate)));
						}
						String tillDate = (String) policyMap.get(InventoryItemUtil.END_DATE);
						if (StringUtils.isEmpty(tillDate)) {
							warrantyDTO[index].setEndDate(new XmlCalendar());
						} else {
							warrantyDTO[index].setEndDate(CalendarUtil
									.convertToXMLCalendar(convertToCalendarDate(tillDate)));
						}
						if (isDRDoneByLoggedInUser || inventoryItemUtil.isInventoryFullView(inventoryItem)) {
							String purchaseDate = (String) policyMap.get(InventoryItemUtil.PURCHASE_DATE);
							if (StringUtils.isEmpty(purchaseDate)) {
								warrantyDTO[index].setPurchaseDate(new XmlCalendar());
							} else {
								warrantyDTO[index].setPurchaseDate(CalendarUtil
										.convertToXMLCalendar(convertToCalendarDate(purchaseDate)));
							}

							warrantyDTO[index].setPurchaseOrderNumber((String) policyMap
									.get(InventoryItemUtil.PURCHASE_ORD_NUMBER));

							String comments = (String) policyMap.get(InventoryItemUtil.COMMENTS);
							if (StringUtils.isEmpty(comments)) {
								comments = "";
							}
							warrantyDTO[index].setComments(comments);
						} else {
							warrantyDTO[index].setPurchaseDate(new XmlCalendar());
							warrantyDTO[index].setPurchaseOrderNumber("");
							warrantyDTO[index].setComments("");
						}
						warrantyDTO[index].setStatus((String) policyMap
								.get(InventoryItemUtil.POLICY_STATUS_FOR_DISPLAY));

					}
					warrantyCoveragesDTO.setWarrantyCoverageDetailsArray(warrantyDTO);
				}
			}
			inventoryItemDTO.setWarrantyCoverages(warrantyCoveragesDTO);
		} catch (PolicyException e) {
			throw new RuntimeException("PolicyException Exception encountered", e);
		}
	}

	public void setCampaignService(CampaignService campaignService) {
		this.campaignService = campaignService;
	}

	public void setPolicyService(PolicyService policyService) {
		this.policyService = policyService;
	}

	public void setInventoryItemUtil(InventoryItemUtil inventoryItemUtil) {
		this.inventoryItemUtil = inventoryItemUtil;
	}

	/**
	 * The method returns a CalendarDate object converted as per the logged in
	 * user date format.
	 * 
	 * @param dataString
	 * @return CalendarDate
	 */
	private CalendarDate convertToCalendarDate(String dataString) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(TWMSDateFormatUtil.getDateFormatForLoggedInUser());
		try {
			Date date = sdf.parse(dataString);
			calendar.setTime(date);
		} catch (ParseException e) {
			logger.error("Failed to transform String date to java calendar date.", e);
		}
		return (calendar == null) ? null : TimePoint.from(calendar).calendarDate(TimeZone.getDefault());
	}

	public void setI18nDomainTextReader(I18nDomainTextReader i18nDomainTextReader) {
		this.i18nDomainTextReader = i18nDomainTextReader;
	}

	public ConfigParamService getConfigParam() {
		return configParamService;
	}

	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}

	public InventoryService getInventoryService() {
		return inventoryService;
	}

	public void setInventoryService(InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

}
