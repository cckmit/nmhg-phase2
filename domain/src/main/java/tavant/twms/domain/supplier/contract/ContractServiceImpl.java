/*
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
package tavant.twms.domain.supplier.contract;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimCurrencyConversionAdvice;
import tavant.twms.domain.claim.ClaimNumberPatternService;
import tavant.twms.domain.claim.ClaimState;
import tavant.twms.domain.claim.ClaimType;
import tavant.twms.domain.claim.HussmanPartsReplacedInstalled;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplaced;
import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.payment.LineItemGroup;
import tavant.twms.domain.claim.payment.Payment;
import tavant.twms.domain.claim.payment.definition.PaymentSectionRepository;
import tavant.twms.domain.claim.payment.definition.Section;
import tavant.twms.domain.claim.payment.rates.ItemPriceAdminService;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.configuration.ConfigName;
import tavant.twms.domain.configuration.ConfigParamService;
import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.domain.partreturn.Carrier;
import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.PartReturn;
import tavant.twms.domain.rules.RuleExecutionTemplate;
import tavant.twms.domain.supplier.CostLineItem;
import tavant.twms.domain.supplier.ItemMapping;
import tavant.twms.domain.supplier.ItemMappingRepository;
import tavant.twms.domain.supplier.SupplierPartReturn;
import tavant.twms.domain.supplier.recovery.RecoverablePart;
import tavant.twms.domain.supplier.recovery.RecoveryClaimInfo;
import tavant.twms.domain.supplier.recovery.RecoveryInfo;
import tavant.twms.infra.HibernateCast;
import tavant.twms.security.SelectedBusinessUnitsHolder;

import com.domainlanguage.money.Money;

/**
 * @author kannan.ekanath
 */
public class ContractServiceImpl implements ContractService {

    private static final Logger logger = Logger.getLogger(ContractServiceImpl.class);

    private ContractRepository contractRepository;

    private ItemMappingRepository itemMappingRepository;

    private PaymentSectionRepository paymentSectionRepository;

    private RuleExecutionTemplate ruleExecutionTemplate;
    
    private ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice;
    
    private ConfigParamService configParamService;

    private ClaimNumberPatternService claimNumberPatternService;
    
    private ItemPriceAdminService itemPriceAdminService;
    

	public void createRecoveryClaims(RecoveryInfo recoveryInfo) {
		for (RecoveryClaimInfo replaceRecClaimInfo : recoveryInfo.getReplacedPartsRecovery()) {
			if (replaceRecClaimInfo.getContract() != null) {
				if (replaceRecClaimInfo.getRecoveryClaim() == null) {
					RecoveryClaim recClaim = new RecoveryClaim();
					recClaim.setRecoveryClaimNumber(claimNumberPatternService.generateNextRecoveryClaimNumber(recoveryInfo));
					recClaim.setContract(replaceRecClaimInfo.getContract());
					recClaim.setClaim(recoveryInfo.getWarrantyClaim());
					replaceRecClaimInfo.setRecoveryClaim(recClaim);
					recClaim.setRecoveryClaimInfo(replaceRecClaimInfo);
					if (!recoveryInfo.getComments().isEmpty())
						recClaim.setComments(recoveryInfo.getComments().first().getComment());
					recoveryInfo.getWarrantyClaim().addRecoveryClaim(recClaim);//TODO : Discuss this with Devendra and then Remove
                    //Putting the contract at oem part replaced label
                    for(RecoverablePart revPart: replaceRecClaimInfo.getRecoverableParts())
                    {
                        if(null != revPart.getOemPart()){
                            revPart.getOemPart().setAppliedContract(replaceRecClaimInfo.getContract());
                        }
                    }
					populateRecClaim(replaceRecClaimInfo, recClaim);
				} else {
					replaceRecClaimInfo.getRecoveryClaim().getCostLineItems().clear();
					populateRecClaim(replaceRecClaimInfo, replaceRecClaimInfo.getRecoveryClaim());
				}
			}
		}
	}
	
	private void populateRecClaim(RecoveryClaimInfo recClaimInfo, RecoveryClaim recClaim) {
		for (RecoverablePart recoverablePart : recClaimInfo.getRecoverableParts()) {
			if (recClaim.getContract().getPhysicalShipmentRequired() != null) {
				recoverablePart.setSupplierReturnNeeded(recClaim.getContract().getPhysicalShipmentRequired());
			}
			updateSupplierItemOnPart(recoverablePart, recClaim.getContract());
		}
        if (configParamService.getBooleanValue(ConfigName.IS_SUPPLIER_NEEDED_FOR_COST_FETCH.getName())) {
            if (recClaimInfo.getContract().getRecoveryBasedOnCausalPart()
                    && recClaimInfo.getContract().getCollateralDamageToBePaid()) {
                Item causalPart = recClaim.getClaim().getServiceInformation().getCausalPart();
                if (isCompensationTermBasedOnCostPrice(recClaimInfo.getContract().getCompensationTermForSection(Section.OEM_PARTS))) {
                    for (RecoverablePart recoverablePart : recClaimInfo.getRecoverableParts()) {
                        if(recoverablePart.getOemPart().getItemReference().getUnserializedItem().equals(causalPart)){ // fetch the price
                            itemPriceAdminService.populateCostPriceForRecoverablePart(recClaimInfo, recoverablePart);
                        }else{
                            recoverablePart.setMaterialCost(Money.valueOf(BigDecimal.ZERO, recClaim.getClaim().getCurrencyForCalculation()));
                            recoverablePart.setCostPricePerUnit(Money.valueOf(BigDecimal.ZERO, recClaim.getClaim().getCurrencyForCalculation()));
                        }
                    }
                }
            } else {
                itemPriceAdminService.populateCostPriceForRecoverableParts(recClaimInfo);
            }
        }
        //Even if Claim Amount is zero , user ll be shown price fetch amount in recovery initiation.
		/*if (recClaim.getClaim().getPayment().getTotalAmount().isPositive()) {*/
			addSupplierCostLineItems(recClaim, recClaimInfo.getRecoverableParts());
		/*}*/
	}

    private boolean isCompensationTermBasedOnCostPrice(CompensationTerm oemPartsCompensationTerm) {
		if (oemPartsCompensationTerm == null) {
			return false;
		} else {
			return CompensationTerm.COST_PRICE.equals(oemPartsCompensationTerm
					.getPriceType());
		}
    }

    public void addSupplierCostLineItems(RecoveryClaim recClaim, List<RecoverablePart> recoverableParts) {
        Claim claim = recClaim.getClaim();
        recClaim.getCostLineItems().clear();
        Payment payment = claim.getPayment();
        Money zeroMoney = Money.dollars(0);
        List<Section> sections = getAllSections(payment.getLineItemGroups());
        for (Section section : sections) {
            if (Section.OEM_PARTS.equals(section.getName())
                    || "OEM Parts".equalsIgnoreCase(section.getName())) {
                CostLineItem cli = createOEMPartsCostLineItem(recClaim, recoverableParts, section);
                recClaim.addCostLineItem(cli);
            } else if (Section.LABOR.equals(section.getName())) {
                Money totalMoney;
                if (recClaim.getContract().doesCoverSection(section.getName())
                        && payment.getLineItemGroup(section.getName()) != null) {
                    CompensationTerm compensationTerm = recClaim.getContract()
                            .getCompensationTermForSection(section.getName());
                    if (CompensationTerm.SPL_SUPPLIER_LABOR_RATE.equals(compensationTerm
                            .getPriceType())) {
                        totalMoney = compensationTerm.getRecoveryFormula().getSupplierRate().times(
                                compensationTerm.getRecoveryFormula().getNoOfHours());
                    } else if (CompensationTerm.SPL_DEALER_LABOR_RATE.equals(compensationTerm
                            .getPriceType())) {
                        totalMoney = getSpecialDealerLaborCost(recClaim, zeroMoney,
                                compensationTerm);
                    } else if (CompensationTerm.STD_SUPPLIER_LABOR_RATE.equals(compensationTerm
                            .getPriceType())) {
                        totalMoney = getStandardSupplierLaborCost(recClaim, zeroMoney,
                                compensationTerm);
                    } else {
                    	Money forConversion = payment.getAcceptedTotalAfterGlobalModifiersProrated(section
                                .getName());
                    	Money copy = new Money(forConversion.breachEncapsulationOfAmount(),forConversion.breachEncapsulationOfCurrency());
                    	totalMoney = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(copy, claim);
                    }
                } else {
                    totalMoney = zeroMoney;
                }
                totalMoney = checkFlatRate(totalMoney,recClaim,section);
                CostLineItem cli = getCostLineItem(recClaim,totalMoney,section);
                recClaim.addCostLineItem(cli);
            } else if (Section.TOTAL_CLAIM.equals(section.getName())||Section.TRAVEL.equals(section.getName())||Section.OTHERS.equals(section.getName())) {
                // no addition required
            } else {
                Money totalMoney;
                if (recClaim.getContract().doesCoverSection(section.getName())) {
                    if (payment.getLineItemGroup(section.getName()) != null) {
                        Money forConversion = payment.getAcceptedTotalAfterGlobalModifiersProrated(section
                                .getName());
                        Money copy = new Money(forConversion.breachEncapsulationOfAmount(), forConversion.breachEncapsulationOfCurrency());
                        totalMoney = claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(copy, claim);
                    } else {
                        totalMoney = zeroMoney;
                    }
                } else {
                    totalMoney = zeroMoney;
                }
                totalMoney = checkFlatRate(totalMoney,recClaim,section);
                CostLineItem cli =getCostLineItem(recClaim,totalMoney,section);
                recClaim.addCostLineItem(cli);
            }
        }
        applyContractAfterProrate(recClaim);
    }
    

	private Money checkFlatRate(Money totalMoney, RecoveryClaim recClaim,
			Section section) {
		if(totalMoney.isZero() && recClaim.getContract().doesCoverSection(section.getName())){
           	CompensationTerm compTerm = recClaim.getContract()
               .getCompensationTermForSection(section.getName());
           	totalMoney = compTerm.getRecoveryFormula().getAddedConstant();
           }
		return totalMoney;
	}

    private CostLineItem createOEMPartsCostLineItem(RecoveryClaim recClaim, List<RecoverablePart> recoverableParts, Section section) {
        List<Money> totalPartsCost = getOemPartsCost(recClaim, recoverableParts, section);
        Money totalPartsCostAmount = Money.sum(totalPartsCost);
        return getCostLineItem(recClaim,totalPartsCostAmount,section);
    }

    /**
	 * This method and all subsequent methods called by this method get used only in Auto recovery scenarios.Also these methods will always
	 * work on the assumption that recoveryInfo needs to be created at Part Level
	 */
	public RecoveryInfo createRecoveryInfo(Claim claim) {
		if(ClaimType.CAMPAIGN.getType().equalsIgnoreCase(claim.getType().getType())){//reopening of campaign claims is not handled here.I am assuming that campaign claims cant be reopened
			return createRecoveryInfoForCampaignClaim(claim);
		}
		else
		{
			if (!claim.getReopenRecoveryClaim())
				return createRecoveryInfoForClaim(claim);
			else
				return createRecoveryInfoForReopenedClaim(claim);
		}
	}
	
	private RecoveryInfo createRecoveryInfoForCampaignClaim(Claim claim) {
		RecoveryInfo recoveryInfo = new RecoveryInfo();
        recoveryInfo.setWarrantyClaim(claim);
        claim.setRecoveryInfo(recoveryInfo);
		RecoveryClaimInfo recoveryClaimInfo = new RecoveryClaimInfo();
		recoveryClaimInfo.setContract(claim.getCampaign().getContract());
		recoveryInfo.addReplacedPartsRecovery(recoveryClaimInfo);
		for (OEMPartReplaced oemPartReplaced : claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced()) {
			ItemMapping mapping = this.itemMappingRepository.findItemMappingForSupplier(oemPartReplaced.getItemReference()
					.getUnserializedItem(), recoveryClaimInfo.getContract().getSupplier());
			if (mapping!=null && recoveryClaimInfo.getContract().getItemsCovered().contains(mapping.getToItem())) {
				recoveryClaimInfo.addRecoverablePart(createRecoverablePart(oemPartReplaced));
			}
		}
		return recoveryInfo;
	}
    
    private RecoveryInfo createRecoveryInfoForClaim(Claim claim){
    	RecoveryInfo recoveryInfo = claim.getRecoveryInfo();
        if (recoveryInfo == null) {
            recoveryInfo = new RecoveryInfo();
            recoveryInfo.setWarrantyClaim(claim);
            claim.setRecoveryInfo(recoveryInfo);
        } else {
        	claim.getRecoveryClaims().clear();
            recoveryInfo.getReplacedPartsRecovery().clear();
        }
        List<Contract> causalPartContracts = findContract(claim, claim.getServiceInformation().getCausalPart(), true);
        causalPartContracts = filterInactiveContractsIfAny(causalPartContracts);
        if (causalPartContracts != null && causalPartContracts.size() == 1) {
            RecoveryClaimInfo recClaimInfo = new RecoveryClaimInfo();
            recClaimInfo.setContract(causalPartContracts.get(0));
            recoveryInfo.setCausalPartRecovery(recClaimInfo);
            for (OEMPartReplaced oemPartReplaced : claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced()) {
                recClaimInfo.addRecoverablePart(createRecoverablePart(oemPartReplaced));
            }
        } else {
            createReplacedContracts(recoveryInfo, claim);
        }
        updateOemPartCostsOnRecoverableParts(recoveryInfo);
        return recoveryInfo;
    }
    
	private RecoveryInfo createRecoveryInfoForReopenedClaim(Claim claim) {
		List<RecoveryClaimInfo> removalList = new ArrayList<RecoveryClaimInfo>();
		for (RecoveryClaimInfo recoveryClaimInfo : claim.getRecoveryInfo().getReplacedPartsRecovery()) {
			if (recoveryClaimInfo.getRecoveryClaim() == null)
				removalList.add(recoveryClaimInfo);
		}
		/*
		 * this is actually wrong.This has been done to remove any contract information which is specified by the user after reopening the
		 * warranty claim The correct approach would be to NOT allow the user to make any contract information updates if the warranty claim
		 * qualifies for auto recovery
		 */
		claim.getRecoveryInfo().getReplacedPartsRecovery().removeAll(removalList);
		removalList = null;

		List<Contract> causalPartContract = findContract(claim, claim.getServiceInformation().getCausalPart(), true);
		causalPartContract = filterInactiveContractsIfAny(causalPartContract);
		if (causalPartContract != null && causalPartContract.size() == 1 && causalPartContract.get(0).getCollateralDamageToBePaid()) {
			prepareCausalPartRecoveryClaimInfoForReopenedClaim(claim, causalPartContract.get(0));
			for (OEMPartReplaced oemPartReplaced : claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced()) {
				claim.getRecoveryInfo().getCausalPartRecovery().addRecoverablePart(createRecoverablePart(oemPartReplaced));
			}
			for (RecoveryClaimInfo recoveryClaimInfo : claim.getRecoveryInfo().getReplacedPartsRecovery()) {
				if (!recoveryClaimInfo.isCausalPartRecovery())
					recoveryClaimInfo.setContract(null);
			}
		} else {
			if (claim.getRecoveryInfo().getCausalPartRecovery() != null)
				claim.getRecoveryInfo().getCausalPartRecovery().setContract(null);

			Map<Contract, List<RecoverablePart>> partContracts = new HashMap<Contract, List<RecoverablePart>>();
			for (OEMPartReplaced oemPartReplaced : claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced()) {
				List<Contract> replacePartContract = findContract(claim, oemPartReplaced.getItemReference().getUnserializedItem(), false);
				if (partContracts.containsKey(replacePartContract.get(0))) {
					RecoverablePart recoverablePart = new RecoverablePart(oemPartReplaced, oemPartReplaced.getNumberOfUnits());
					partContracts.get(replacePartContract.get(0)).add(recoverablePart);
				} else {
					List<RecoverablePart> partsCovered = new ArrayList<RecoverablePart>();
					partsCovered.add(new RecoverablePart(oemPartReplaced, oemPartReplaced.getNumberOfUnits()));
					partContracts.put(replacePartContract.get(0), partsCovered);
				}
			}
			prepareReplacedPartsRecoveryClaimInfoForReopenedClaim(partContracts, claim);
		}
		updateOemPartCostsOnRecoverableParts(claim.getRecoveryInfo());
		return claim.getRecoveryInfo();
	}
    
	private void prepareReplacedPartsRecoveryClaimInfoForReopenedClaim(Map<Contract, List<RecoverablePart>> partContracts, Claim claim) {
		for (RecoveryClaimInfo recoveryClaimInfo : claim.getRecoveryInfo().getReplacedPartsRecovery()) {
			if (recoveryClaimInfo.getContract() != null && !recoveryClaimInfo.isCausalPartRecovery()) {
				if (!partContracts.containsKey(recoveryClaimInfo.getContract()))
					recoveryClaimInfo.setContract(null);
				else {
					recoveryClaimInfo.getRecoverableParts().clear();
					recoveryClaimInfo.getRecoverableParts().addAll(partContracts.get(recoveryClaimInfo.getContract()));
					recoveryClaimInfo.setCausalPartRecovery(false);
					partContracts.remove(recoveryClaimInfo.getContract());
				}
			} else if (partContracts.containsKey(recoveryClaimInfo.getRecoveryClaim().getContract())) {
				recoveryClaimInfo.setContract(recoveryClaimInfo.getRecoveryClaim().getContract());
				recoveryClaimInfo.setCausalPartRecovery(false);
				recoveryClaimInfo.getRecoverableParts().clear();
				recoveryClaimInfo.getRecoverableParts().addAll(partContracts.get(recoveryClaimInfo.getContract()));
				partContracts.remove(recoveryClaimInfo.getContract());
			}
		}
		for (Map.Entry<Contract, List<RecoverablePart>> entry : partContracts.entrySet()) {
			RecoveryClaimInfo recoveryClaimInfo = new RecoveryClaimInfo();
			recoveryClaimInfo.setContract(entry.getKey());
			recoveryClaimInfo.setRecoverableParts(entry.getValue());
			claim.getRecoveryInfo().addReplacedPartsRecovery(recoveryClaimInfo);
		}
	}
    
	private void prepareCausalPartRecoveryClaimInfoForReopenedClaim(Claim claim, Contract causalContract) {
		RecoveryClaimInfo causalPartRecoveryClaimInfo = claim.getRecoveryInfo().getCausalPartRecovery();
		if (causalPartRecoveryClaimInfo == null) {
			boolean foundSomething = false;
			for (RecoveryClaimInfo recoveryClaimInfo : claim.getRecoveryInfo().getReplacedPartsRecovery()) {// here i am looking if there is
																											// some recovery claim which was
																											// created last to last
																											// time.This will come into play
																											// when the claim is reopened
																											// more than once
				if (recoveryClaimInfo.getContract() == null && recoveryClaimInfo.getRecoveryClaim().getContract().equals(causalContract)) {
					recoveryClaimInfo.setContract(causalContract);
					recoveryClaimInfo.setCausalPartRecovery(true);
					recoveryClaimInfo.getRecoverableParts().clear();
					foundSomething = true;
					break;
				}
			}
			if (!foundSomething) {
				causalPartRecoveryClaimInfo = new RecoveryClaimInfo();
				causalPartRecoveryClaimInfo.setContract(causalContract);
				claim.getRecoveryInfo().setCausalPartRecovery(causalPartRecoveryClaimInfo);
			}
		} else if (!causalPartRecoveryClaimInfo.getContract().equals(causalContract)) {
			causalPartRecoveryClaimInfo.setContract(null);// means the claim on this one has to be denied
			causalPartRecoveryClaimInfo = new RecoveryClaimInfo();
			causalPartRecoveryClaimInfo.setContract(causalContract);
			claim.getRecoveryInfo().setCausalPartRecovery(causalPartRecoveryClaimInfo);
		} else {
			causalPartRecoveryClaimInfo.getRecoverableParts().clear();
		}
	}
	
	private void updateOemPartCostsOnRecoverableParts(RecoveryInfo recoveryInfo) {
		if(!configParamService.getBooleanValue(ConfigName.IS_SUPPLIER_NEEDED_FOR_COST_FETCH.getName()))
		{
			for (RecoveryClaimInfo replaceRecClaimInfo : recoveryInfo.getReplacedPartsRecovery()) {
				for (RecoverablePart recoverablePart : replaceRecClaimInfo.getRecoverableParts()) {
					if (recoverablePart.getOemPart() != null) {
						recoverablePart.setMaterialCost(recoverablePart.getOemPart().getMaterialCost());
						recoverablePart.setCostPricePerUnit(recoverablePart.getOemPart().getCostPricePerUnit());
					}
				}
			}
		}
	}

    private void createReplacedContracts(RecoveryInfo recoveryInfo, Claim claim) {
        List<RecoveryClaimInfo> replacedRecClaimInfo = new ArrayList<RecoveryClaimInfo>();
        Map<String, RecoveryClaimInfo> contractMap = new HashMap<String, RecoveryClaimInfo>();
        if (claim.getServiceInformation().getServiceDetail().getOEMPartsReplaced() != null && !claim.getServiceInformation()
                .getServiceDetail().getOEMPartsReplaced().isEmpty()) {
            for (OEMPartReplaced oemPart : claim.getServiceInformation().getServiceDetail().getOEMPartsReplaced()) {
                createSupplierRecInfo(oemPart, claim, contractMap, replacedRecClaimInfo);
            }
        } else {
            for (HussmanPartsReplacedInstalled hussmanPart : claim.getServiceInformation().getServiceDetail()
                    .getHussmanPartsReplacedInstalled()) {
                for (OEMPartReplaced oemPart : hussmanPart.getReplacedParts()) {
                    createSupplierRecInfo(oemPart, claim, contractMap, replacedRecClaimInfo);
                }
            }
        }
        recoveryInfo.getReplacedPartsRecovery().addAll(replacedRecClaimInfo);
    }

    private void createSupplierRecInfo(OEMPartReplaced oemPart, Claim claim, Map<String, RecoveryClaimInfo> contractMap,
                                       List<RecoveryClaimInfo> replacedRecClaimInfo) {
        List<Contract> replacePartContract = findContract(claim, oemPart.getItemReference().getUnserializedItem(), false);
        replacePartContract = filterInactiveContractsIfAny(replacePartContract);
        if (replacePartContract != null && !replacePartContract.isEmpty()) {
            if (!contractMap.containsKey(replacePartContract.get(0).getName())) {
                RecoveryClaimInfo recClaimInfo = new RecoveryClaimInfo();
                recClaimInfo.setContract(replacePartContract.get(0));
                recClaimInfo.addRecoverablePart(createRecoverablePart(oemPart));
                contractMap.put(replacePartContract.get(0).getName(), recClaimInfo);
                replacedRecClaimInfo.add(recClaimInfo);
            } else {
                RecoveryClaimInfo recClaimInfo = contractMap.get(replacePartContract.get(0).getName());
                recClaimInfo.addRecoverablePart(createRecoverablePart(oemPart));
            }
        }
    }

    private RecoverablePart createRecoverablePart(OEMPartReplaced oemPart) {
        RecoverablePart recoverablePart = new RecoverablePart();
        recoverablePart.setOemPart(oemPart);
        recoverablePart.setQuantity(oemPart.getNumberOfUnits());
        
        return recoverablePart;
    }
    
    private Money getWarrantyClaimValue(RecoveryClaim recClaim,Money totalPartsCostAmount, Section section) {
		if (Section.LABOR.equals(section.getName())
				|| ((Section.OEM_PARTS.equals(section.getName()) || "OEM Parts".equalsIgnoreCase(section.getName())) && configParamService
						.getBooleanValue("isPartsReplacedInstalledSectionVisible").booleanValue())) {
			/*
			 * basically either the Section has to be Labor OR [the section has to be OEM PARTS and also the
			 * isPartsReplacedInstalledSectionVisible config param's value should be true]
			 */
			if (recClaim.getClaim().getPayment().getLineItemGroup(section.getName()) != null) {
				Money amountOnClaim = recClaim.getClaim().getPayment().getAcceptedTotalAfterGlobalModifiersProrated(section.getName());
				return new Money(amountOnClaim.breachEncapsulationOfAmount(), amountOnClaim.breachEncapsulationOfCurrency());
			} else {
				return Money.valueOf(BigDecimal.ZERO, recClaim.getClaim().getForDealer().getPreferredCurrency());
			}
		} else if (Section.OEM_PARTS.equals(section.getName()) || "OEM Parts".equalsIgnoreCase(section.getName())) {
			/* if the code comes here it inherently means that the isPartsReplacedInstalledSectionVisible config param's value is FALSE */
			Money warrantyClaimValueWithoutProRating = getWarrantyClaimValueForRecoveredParts(recClaim.getRecoveryClaimInfo()
					.getRecoverableParts(), recClaim.getClaim());
			return recClaim.getClaim().getPayment().getAcceptedTotalAfterGlobalModifiersProrated(warrantyClaimValueWithoutProRating);
		} else {
			return getWarrantyValueForSection(recClaim, section);
		}
	}
    
    private Money getWarrantyValueForSection(RecoveryClaim recClaim, Section section){
    	if (recClaim.getClaim().getPayment().getLineItemGroup(section.getName()) != null) {
			Money amountOnClaim = recClaim.getClaim().getPayment().getAcceptedTotalAfterGlobalModifiersProrated(section.getName());
			return new Money(amountOnClaim.breachEncapsulationOfAmount(), amountOnClaim.breachEncapsulationOfCurrency());
		} else {
			return Money.valueOf(BigDecimal.ZERO, recClaim.getClaim().getForDealer().getPreferredCurrency());
		}
    }
    
    

	private CostLineItem getCostLineItem(RecoveryClaim recClaim, Money sectionAmountforRecovery, Section section) {
		Money totalCostInSupplierCurrency;
		Money warrantyClaimValue = getWarrantyClaimValue(recClaim,sectionAmountforRecovery, section);

		if (recClaim.getContract().getSupplier().getPreferredCurrency() != null
				&& !sectionAmountforRecovery.breachEncapsulationOfCurrency().equals(
						recClaim.getContract().getSupplier().getPreferredCurrency())) {
			totalCostInSupplierCurrency = claimCurrencyConversionAdvice.convertMoneyFromBaseToNaturalCurrency(
					sectionAmountforRecovery, recClaim.getClaim().getRepairDate(), recClaim.getContract().getSupplier()
							.getPreferredCurrency());
		} else {
			totalCostInSupplierCurrency = sectionAmountforRecovery;
		}
		return new CostLineItem(section, warrantyClaimValue, totalCostInSupplierCurrency);
	}


    private void updateSupplierItemOnPart(RecoverablePart recoverablePart, Contract contract) {
		Item item = recoverablePart.getOemPart().getItemReference().getUnserializedItem();
		Supplier supplier = contract.getSupplier();
		List<ItemMapping> mappings = this.itemMappingRepository.findItemMappingsForPart(item, null);
		for (ItemMapping mapping : mappings) {
			if (supplier.getId().equals(mapping.getToItem().getOwnedBy().getId())) {
				recoverablePart.setSupplierItem(mapping.getToItem());
				break;
			}
		}
    }

    public Contract findContract(Long contractId) {
        return this.contractRepository.findById(contractId);
    }

	public void updateSupplierPartReturn(RecoverablePart part, Location location, Carrier carrier, String rgaNumber) {
		if (logger.isDebugEnabled()) {
			logger.debug("Part replaced is [" + part + "]");
		}
		Item item = part.getOemPart().getItemReference().getUnserializedItem();
		Assert.notNull(item);
		Assert.notNull(item.getOwnedBy(), "The item [" + item + "] does not have an owner");
		
			if (part.getSupplierPartReturns().isEmpty() || part.isSupplierPartReturnModificationAllowed()) {
				SupplierPartReturn supplierPartReturn;
				if (part.getSupplierPartReturns().isEmpty()) {
					supplierPartReturn = new SupplierPartReturn(part, 0);
				} else {
					supplierPartReturn = part.getSupplierPartReturns().get(0);
				}
				supplierPartReturn.updateSupplierPartReturn(carrier, location, rgaNumber);
			}
		}
	
	public void updateSupplierPartReturn(RecoverablePart part,String rgaNumber) {
		if (logger.isDebugEnabled()) {
			logger.debug("Part replaced is [" + part + "]");
		}
		Item item = part.getOemPart().getItemReference().getUnserializedItem();
		Assert.notNull(item);
		Assert.notNull(item.getOwnedBy(), "The item [" + item + "] does not have an owner");
			SupplierPartReturn supplierPartReturn;
			if (part.getSupplierPartReturns().isEmpty()) {
				supplierPartReturn = new SupplierPartReturn(part, 0);
			} else {
				supplierPartReturn = part.getSupplierPartReturns().get(0);
			}
			supplierPartReturn.updateSupplierPartReturn(supplierPartReturn.getCarrier(),supplierPartReturn.getReturnLocation(),rgaNumber);
		}
	
    @Deprecated
    public List<Contract> findContracts(Claim claim, PartReplaced part, Item item, boolean isCausalPartOnly) {
        List<Contract> contracts = new ArrayList<Contract>();
        List<ItemMapping> mappings = null;
        if(isCausalPartOnly){
         mappings = this.itemMappingRepository.findItemMappingsForPart(item,
                null);
        }else{
        	if(item != null){
        		mappings = this.itemMappingRepository.findItemMappingsForPart(item,
                    null);
        	}
        	if(part != null){
        		OEMPartReplaced oemPartReplaced = new HibernateCast<OEMPartReplaced>().cast(part);
        		List<ItemMapping> partReplacedMappings = this.itemMappingRepository.findItemMappingsForPart(
        				oemPartReplaced.getItemReference().getUnserializedItem(),null);
        		if(mappings != null && !mappings.isEmpty() && partReplacedMappings != null 
        				&& !partReplacedMappings.isEmpty()){
        			mappings.addAll(partReplacedMappings);
        		}else if(mappings == null && partReplacedMappings != null 
        				&& !partReplacedMappings.isEmpty()){
        			mappings = new ArrayList<ItemMapping>();
        			mappings.addAll(partReplacedMappings);
        		}
        	}
        }
        for (ItemMapping mapping : mappings) {
            if (logger.isDebugEnabled()) {
                logger.debug("Finding contracts for mapping [" + mapping + "]");
            }
            Item supplierItem = mapping.getToItem();
            if (logger.isDebugEnabled()) {
                logger.debug("Supplier Item is [" + supplierItem + "]");
            }
            contracts.addAll(this.contractRepository.findContractsForItem(supplierItem));
        }
        List<Contract> filteredContracts = new ArrayList<Contract>();
        // Filter the contracts
        for (Contract contract : contracts) {
            if (contract.isApplicable(claim, this.ruleExecutionTemplate,doesSerializedPartsHaveShipmentDate())) {
                filteredContracts.add(contract);
            }
        }
        return filteredContracts;
    }

    public List<Contract> findContract(Claim claim, Item item, Boolean isCausalPartRecovery) {
        List<ItemMapping> mappings = this.itemMappingRepository.findItemMappingsForPart(item,
                null);
        List<Contract> contracts = new ArrayList<Contract>();
        for (ItemMapping mapping : mappings) {
            Item supplierItem = mapping.getToItem();
            contracts.addAll(this.contractRepository.findContractsForItem(supplierItem,isCausalPartRecovery));
        }
        List<Contract> filteredContracts = new ArrayList<Contract>();
        // Filter the contracts
        for (Contract contract : contracts) {
            if (contract.isApplicable(claim, this.ruleExecutionTemplate,doesSerializedPartsHaveShipmentDate())) {
                filteredContracts.add(contract);
            }
        }
        return filteredContracts;
    }

    public boolean canAutoInitiateRecovery(Claim claim) {
    	if(ClaimType.CAMPAIGN.getType().equalsIgnoreCase(claim.getType().getType()))
    		return true; 
    	if(claim.getRecoveryClaims().size() > 0){
    		return false;
		}
    	if(claim.isPendingRecovery()){
    		return false;
    	}
    	String whenToInitiateRecovery = null;
    	if(claim.getState().getState().equalsIgnoreCase(ClaimState.ACCEPTED_AND_CLOSED.getState())){
    		whenToInitiateRecovery=AdminConstants.ON_ACCEPT;
    	}else{
    		whenToInitiateRecovery=AdminConstants.ON_SUBMIT;
    	}
    	
    	if(configParamService.getBooleanValue(ConfigName.AUTO_INITIATE_ON_CLAIM_ACCEPT_AND_VENDOR_REVIEW_RESPONSIBILITY.getName()) && !claim.getLastUpdatedBy().hasRole(Role.DEALER)){
    		if(claim.getAccountabilityCode()!=null&&!claim.getAccountabilityCode().getCode().equals("V") && claim.getState().getState().equalsIgnoreCase(ClaimState.ACCEPTED_AND_CLOSED.getState())){
    			return false;
    		}
    	}
    	
        List<Contract> causalPartContracts = findContract(claim, claim.getServiceInformation().getCausalPart(), true);
        causalPartContracts = filterInactiveContractsIfAny(causalPartContracts);
        if (causalPartContracts != null && !causalPartContracts.isEmpty()) {
        	if(causalPartContracts.size() ==1 && !causalPartContracts.get(0).getCollateralDamageToBePaid()){
        		for(OEMPartReplaced oemPart: claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced()) {
        			if(!noContractOnRemovedPart(oemPart, claim)){
        				return false;
        			}
        		}
        	}
            return causalPartContracts.size() == 1 && causalPartContracts.get(0).getWhenToInitiateRecoveryClaim().equalsIgnoreCase(whenToInitiateRecovery);
        } else {
            for(OEMPartReplaced oemPart: claim.getServiceInformation().getServiceDetail().getAllOEMPartsReplaced()) {
                if (!isSingleContract(oemPart, claim, whenToInitiateRecovery)) {
                        return false;
                    }
            }
        }
        return true;
    }
    
    private boolean noContractOnRemovedPart(OEMPartReplaced oemPart, Claim claim){
    	List<Contract> replacePartContracts = findContract(claim, oemPart.getItemReference().getUnserializedItem(), false);
		replacePartContracts = filterInactiveContractsIfAny(replacePartContracts);
		if (replacePartContracts != null && !replacePartContracts.isEmpty() && replacePartContracts.size() > 0) {
			return false;
		}
		return true;
    }
    
    private List<Contract> filterInactiveContractsIfAny(List<Contract> causalPartContracts) {
    	List<Contract> filteredContracts = new ArrayList<Contract>();
    	for (Contract contract : causalPartContracts) {
    		if (contract.getD().isActive()) {
    			filteredContracts.add(contract);
    		}
    	}    	
    	return filteredContracts;
    }
    
    private boolean isSingleContract(OEMPartReplaced oemPart, Claim claim, String whenToInitiateRecovery) {
		List<Contract> replacePartContracts = findContract(claim, oemPart.getItemReference().getUnserializedItem(), false);
		replacePartContracts = filterInactiveContractsIfAny(replacePartContracts);
		if (replacePartContracts != null && !replacePartContracts.isEmpty()) {
			if(replacePartContracts.size() == 1 && replacePartContracts.get(0).getWhenToInitiateRecoveryClaim().equalsIgnoreCase(whenToInitiateRecovery)){
				return true;
			}
			/*for (LineItemGroup lineItemGroup : claim.getPayment().getLineItemGroups()) {
				if (!lineItemGroup.getName().equals(Section.OEM_PARTS) && !lineItemGroup.getName().equals(Section.TOTAL_CLAIM)
						&& lineItemGroup.getLatestAudit().getAcceptedTotal().isPositive())
					return false;
			}		Code commented since not required for NMHG*/
		}
		return false;
    }

    public void updateOEMPartsCostLineItem(RecoveryClaim recClaim) {
    	CostLineItem oemPartsCostLineItem = null;
    	CostLineItem updatedCostLineItem = null;
    	for(CostLineItem costItem : recClaim.getCostLineItems()) {
    		if (Section.OEM_PARTS.equals(costItem.getSection().getName())
                    || "OEM Parts".equalsIgnoreCase(costItem.getSection().getName())) {
    			oemPartsCostLineItem = costItem;
    			updatedCostLineItem = createOEMPartsCostLineItem(recClaim, recClaim.getRecoveryClaimInfo().getRecoverableParts(), costItem.getSection());
    			break;
    		}
    	}
    	if(oemPartsCostLineItem != null && updatedCostLineItem !=null) {
    		recClaim.getCostLineItems().remove(oemPartsCostLineItem);
    		recClaim.addCostLineItem(updatedCostLineItem);
    		recClaim.updateCostLineItemsFromContract(updatedCostLineItem);
    	}
    }
    
    private Money getStandardSupplierLaborCost(RecoveryClaim recClaim, Money zeroMoney,
            CompensationTerm compensationTerm) {
        Money totalMoney;
        List<LaborDetail> laborDetails = recClaim.getClaim().getServiceInformation()
                .getServiceDetail().getLaborPerformed();
        double totalNoOfHours = 0.0;
        for (LaborDetail laborDetail : laborDetails) {
        	//Fix for failed credit Notification TKTSA-1449
            if (laborDetail.getServiceProcedure() != null && laborDetail.getServiceProcedure().getSuggestedLabourHours() != null)
                totalNoOfHours += laborDetail.getServiceProcedure().getSuggestedLabourHours();
            if (laborDetail.getAdditionalLaborHours() != null) {
                totalNoOfHours += laborDetail.getAdditionalLaborHours().doubleValue();
            }
        }
        if (totalNoOfHours != 0) {
            totalMoney = compensationTerm.getRecoveryFormula().getSupplierRate().times(
                    totalNoOfHours);
        } else {
            totalMoney = zeroMoney;
        }
        return totalMoney;
    }

    private Money getSpecialDealerLaborCost(RecoveryClaim recClaim, Money zeroMoney,
            CompensationTerm compensationTerm) {
        Money totalMoney;
        List<LaborDetail> laborDetails = recClaim.getClaim().getServiceInformation()
                .getServiceDetail().getLaborPerformed();
        if (!laborDetails.isEmpty()) {
            totalMoney = laborDetails.get(0).getLaborRate().times(
                    compensationTerm.getRecoveryFormula().getNoOfHours());
        } else {
            totalMoney = zeroMoney;
        }
        return totalMoney;
    }

    private List<Money> getOemPartsCost(RecoveryClaim recClaim,
            List<RecoverablePart> partsToBeProrated, Section section) {
        List<Money> totalPartsCost = new ArrayList<Money>();
        if (recClaim.getContract().doesCoverSection(Section.OEM_PARTS)
                || recClaim.getContract().doesCoverSection("OEM Parts")) {

            Claim claim = recClaim.getClaim();
            Contract contract = recClaim.getContract();
            CompensationTerm compensationTerm =recClaim.getContract().getCompensationTermForSection(section.getName());
            String costPriceType = this.configParamService.getStringValue(ConfigName.SUPPLIER_RECOVERY_CONTRACT_VALUE_CONFIGURATION.getName());

            for (RecoverablePart p : partsToBeProrated) {
            	Money supplierPartCost;
                if (contract.getRecoveryBasedOnCausalPart() && contract.getCollateralDamageToBePaid() && p.getOemPart() != null) {
                    if (p.getOemPart().getItemReference().getUnserializedItem().equals(claim.getServiceInformation().getCausalPart())) {
                        supplierPartCost = p.getSupplierPartCost(contract, claim, compensationTerm, costPriceType,doesSerializedPartsHaveShipmentDate());
                    } else { 
                        supplierPartCost = p.getDNetSupplierCost();
                    }
                } else {
                    supplierPartCost = p.getSupplierPartCost(contract, claim, compensationTerm, costPriceType,doesSerializedPartsHaveShipmentDate());
                }
                if (supplierPartCost != null) {
                    totalPartsCost.add(claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(supplierPartCost, claim));
                }
            }
        }
        return totalPartsCost;
    }
    
	private Money getWarrantyClaimValueForRecoveredParts(List<RecoverablePart> recoverableParts, Claim claim) {
		List<Money> totalPartsCost = new ArrayList<Money>();
		for (RecoverablePart p : recoverableParts) {
			Money warrantyClaimValueForPart = p.getDNetSupplierCost();
			if (warrantyClaimValueForPart != null) {
				totalPartsCost.add(claimCurrencyConversionAdvice.convertMoneyFromNaturalToBaseCurrency(warrantyClaimValueForPart, claim));
			}
		}
		return Money.sum(totalPartsCost);
	}

    protected List<Section> getAllSections() {
        return this.paymentSectionRepository.getSections();
    }
    
    protected List<Section> getAllSections(List<LineItemGroup> lineItemGroups) {
    	String[] sectionNames = new String[lineItemGroups.size()];
    	int count = 0;
    	for(LineItemGroup itemGroup : lineItemGroups) {
    		sectionNames[count++] = itemGroup.getName();
    	}
    	
    	return this.paymentSectionRepository.getSectionWithNameList(sectionNames);
    }

    private void applyContractAfterProrate(RecoveryClaim recClaim) {

        recClaim.updateCostLineItemsFromContract();

    }

    public void createOrUpdateContract(Contract contract) {
//    	  Commented to allow multiple contracts have the same item.
//        List<Item> items = contract.getItemsCovered();
//        for (Item item : items) {
//            removeItemFromPreviousContract(item, contract);
//        }
        this.contractRepository.updateContract(contract);
    }

    private void removeItemFromPreviousContract(Item item, Contract newContract) {
        List<Contract> contracts = new ArrayList<Contract>();
        if (logger.isDebugEnabled()) {
            logger.debug("Supplier Item is [" + item + "]");
        }
        contracts.addAll(this.contractRepository.findContractsForItem(item));
        for (Contract prevContract : contracts) {
            if (!prevContract.getId().equals(newContract.getId())) {
                prevContract.getItemsCovered().remove(item);
                this.contractRepository.update(prevContract);
            }
        }
    }

    // API to set the contract if only a single contract is found
    // API to set the contract if only a single contract is found, else send for
    // manual review.
    public void setSupplierContract(Claim claim) {
        Item causalPart = claim.getServiceInformation().getCausalPart();
        Contract applicableContract;
        if (causalPart != null) {
        	SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
            List<Contract> contracts = findContract(claim,causalPart, true);
            contracts = filterInactiveContractsIfAny(contracts);
            if (contracts != null && contracts.size() == 1) {
                applicableContract = contracts.get(0);
                claim.getServiceInformation().setContract(applicableContract);
                claim.getServiceInformation().setSupplierPartRecoverable(true);

            }
        }else if(ClaimType.CAMPAIGN.getType().equalsIgnoreCase(claim.getType().getType())){
        	SelectedBusinessUnitsHolder.setSelectedBusinessUnit(claim.getBusinessUnitInfo().getName());
        	if(claim.getCampaign() != null && claim.getCampaign().getContract() != null){
        		applicableContract = findContract(claim.getCampaign().getContract().getId());
        		if(applicableContract != null){
        			claim.getServiceInformation().setContract(applicableContract);
                    claim.getServiceInformation().setSupplierPartRecoverable(true);
        		}
        	}
        }
    }

    public List<Contract> findContractsForSuppiler(Supplier supplier) {
        return this.contractRepository.findContractsForSuppiler(supplier);
    }

    public void setContractRepository(ContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public void setItemMappingRepository(ItemMappingRepository itemMappingRepository) {
        this.itemMappingRepository = itemMappingRepository;
    }

    public void setPaymentSectionRepository(PaymentSectionRepository paymentSectionRepository) {
        this.paymentSectionRepository = paymentSectionRepository;
    }

    public void setRuleExecutionTemplate(RuleExecutionTemplate ruleExecutionTemplate) {
        this.ruleExecutionTemplate = ruleExecutionTemplate;
    }

	public void setClaimCurrencyConversionAdvice(
			ClaimCurrencyConversionAdvice claimCurrencyConversionAdvice) {
		this.claimCurrencyConversionAdvice = claimCurrencyConversionAdvice;
	}
	
	public void setConfigParamService(ConfigParamService configParamService) {
		this.configParamService = configParamService;
	}
	
	public List<Contract> findAllContracts(String name, int pageNumber,int pageSize){
		return this.contractRepository.findAllContracts(name, pageNumber, pageSize);
	}

    public boolean isRecoverable(Claim claim) {
    	if(claim.getCommercialPolicy().booleanValue())
    		return false;//FD14 UC08
    	if(ClaimType.CAMPAIGN.getType().equalsIgnoreCase(claim.getType().getType())) {
            if (claim.getCampaign().getContract()!=null) {
                return true;
            } else {
                return false;
            }
        }
    	Item causalPart = claim.getServiceInformation().getCausalPart();
        List<Contract> causalContracts = this.findContract(claim, causalPart, true);
        if (causalContracts.size() > 0) {
            return true;
        }
        if (checkOEMPartReplacedContracts(claim, claim.getServiceInformation().getServiceDetail().getOEMPartsReplaced())) {
            return true;
        }
        List<HussmanPartsReplacedInstalled> hussmanPartsReplaced = claim.getServiceInformation().getServiceDetail()
                .getHussmanPartsReplacedInstalled();
        for (HussmanPartsReplacedInstalled hussmanPartsReplacedInstalled : hussmanPartsReplaced) {
            if (checkOEMPartReplacedContracts(claim, hussmanPartsReplacedInstalled.getReplacedParts())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean doesSerializedPartsHaveShipmentDate(){
    	return  configParamService.getBooleanValue(ConfigName.DO_SERIALIZED_PARTS_HAVE_SHIPMENT_DATE.getName());
    }

    private boolean checkOEMPartReplacedContracts(Claim claim, List<OEMPartReplaced> replacedParts) {
        for (OEMPartReplaced replacePart : replacedParts) {
            List<Contract> contracts = this.findContract(claim, replacePart.getItemReference().getUnserializedItem(), false);
            if (contracts.size() > 0) {
                return true;
            }
        }
        return false;
    }

    public void setClaimNumberPatternService(ClaimNumberPatternService claimNumberPatternService) {
        this.claimNumberPatternService = claimNumberPatternService;
    }
    

	public ItemPriceAdminService getItemPriceAdminService() {
		return itemPriceAdminService;
	}

	public void setItemPriceAdminService(ItemPriceAdminService itemPriceAdminService) {
		this.itemPriceAdminService = itemPriceAdminService;
	}
}