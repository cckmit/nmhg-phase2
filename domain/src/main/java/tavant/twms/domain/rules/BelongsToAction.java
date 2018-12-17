/**
 * 
 */
package tavant.twms.domain.rules;

import tavant.twms.domain.category.CategoryService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.ClaimedItem;
import tavant.twms.domain.claim.LaborDetail;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.failurestruct.ServiceProcedure;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.policy.Policy;
import static tavant.twms.domain.rules.PredicateEvaluator.IS_NOT_PREDICATE;

import java.util.List;
import java.util.Map;

/**
 * @author mritunjay.kumar
 * 
 */
public class BelongsToAction implements PredicateEvaluationAction {
	private DomainSpecificVariable domainVariable;
	private Value watchListName;
	CategoryService service;

	public String performAction(Map<String, Object> evaluationContext) {
		if (domainVariable == null) {
			throw new IllegalStateException(
					"domainVariable cannot be null in PartWatchAction");
		}
		service = (CategoryService) evaluationContext.get("categoryService");
		Claim claim = (Claim) evaluationContext.get("claim");
		boolean isNotPredicate = (Boolean) evaluationContext
				.get(IS_NOT_PREDICATE);
		String categoryName = ((Constant) watchListName).getLiteral();

		if (domainVariable.getAccessedFromType().equals("ClaimedItem")
				&& domainVariable.getFieldName().equals(
						"itemReference.referredInventoryItem")) {
			return getErrorMsgForWatchedInvItem(claim, categoryName,
					isNotPredicate);
		} else if (domainVariable.getAccessedFromType().equals("ClaimedItem")
				&& domainVariable.getFieldName().equals("applicablePolicy")) {
			return getErrorMsgForWatchedPolicy(claim, categoryName,
					isNotPredicate);
		} else if (domainVariable.getAccessedFromType().equals("LaborDetail")
				&& domainVariable.getFieldName().equals("serviceProcedure")) {
			return getErrorMsgForWatchedServiceProcedure(claim, categoryName,
					isNotPredicate);
		} else if (domainVariable.getAccessedFromType().equals("Claim")
				&& domainVariable.getFieldName().equals(
						"claim.serviceInformation.contract.supplier")) {
			return getErrorMsgForWatchedSupplier(claim, categoryName,
					isNotPredicate);
		}else if (domainVariable.getAccessedFromType().equals("OEMPartReplaced")
				&& domainVariable.getFieldName().equals(
				"itemReference.referredItem")) {
			return getErrorMsgForWatchedOemParts(claim, categoryName,
					isNotPredicate);
		}else if (domainVariable.getAccessedFromType().equals("Item")
				&& domainVariable.getFieldName().equals(
				"model")) {
			return getErrorMsgForWatchedModel(claim, categoryName,
					isNotPredicate);
		}
		return null;
	}

	public void setDomainVariable(DomainSpecificVariable domainVariable) {
		this.domainVariable = domainVariable;
	}

	public void setWatchListName(Value watchListName) {
		this.watchListName = watchListName;
	}

	private String getErrorMsgForWatchedInvItem(Claim claim,
			String categoryName, boolean isNotPredicate) {
		StringBuffer watchMessage = new StringBuffer(" Serial No: ");
		for (ClaimedItem claimedItem : claim.getClaimedItems()) {
			if (claimedItem.getItemReference() != null
					&& claimedItem.getItemReference()
							.getReferredInventoryItem() != null) {
				InventoryItem invItem = claimedItem.getItemReference()
						.getReferredInventoryItem();
				boolean isbusinessObjectInNamedCategory = service
						.isBusinessObjectInNamedCategory(invItem, categoryName);
				if (isbusinessObjectInNamedCategory && !isNotPredicate) {
					watchMessage.append(invItem.getSerialNumber()).append(",");
				} else if (!isbusinessObjectInNamedCategory && isNotPredicate) {
					watchMessage.append(invItem.getSerialNumber()).append(",");
				}
			}
		}
		if (watchMessage.toString().endsWith(",")) {
			watchMessage.deleteCharAt(watchMessage.length() - 1);
		}

		if (!isNotPredicate) {
			watchMessage.append(" are in label ");
			watchMessage.append(categoryName);
		} else {
			if (claim.getClaimedItems() != null
					&& claim.getClaimedItems().size() > 0) {
				watchMessage.append(" are not in label ");
				watchMessage.append(categoryName);
			} else {
				watchMessage
						.append(" None of the Inventory Items are associated with claim ");
			}
		}
		watchMessage.append(".");
		return watchMessage.toString();
	}

	private String getErrorMsgForWatchedPolicy(Claim claim,
			String categoryName, boolean isNotPredicate) {
		StringBuffer watchMessage = new StringBuffer(" Policy Codes: ");
		for (ClaimedItem claimedItem : claim.getClaimedItems()) {
			if (claimedItem.getApplicablePolicy() != null) {
				Policy applicablePolicy = claimedItem.getApplicablePolicy();
				boolean isbusinessObjectInNamedCategory = service
						.isBusinessObjectInNamedCategory(applicablePolicy,
								categoryName);
				if (isbusinessObjectInNamedCategory && !isNotPredicate) {
					watchMessage.append(applicablePolicy.getCode()).append(",");
				} else if (!isbusinessObjectInNamedCategory && isNotPredicate) {
					watchMessage.append(applicablePolicy.getCode()).append(",");
				}
			}
		}
		if (watchMessage.toString().endsWith(",")) {
			watchMessage.deleteCharAt(watchMessage.length() - 1);
		}
		if (!isNotPredicate) {
			watchMessage.append(" are in label ");
			watchMessage.append(categoryName);
		} else {
			if (claim.getClaimedItems() != null
					&& claim.getClaimedItems().size() > 0) {
				watchMessage.append(" are not in label ");
				watchMessage.append(categoryName);
			} else {
				watchMessage.append(" None of the Policy are applicable ");
			}
		}
		watchMessage.append(".");
		return watchMessage.toString();

	}

	private String getErrorMsgForWatchedServiceProcedure(Claim claim,
			String categoryName, boolean isNotPredicate) {
		StringBuffer watchMessage = new StringBuffer(" Job Codes: ");
		List<LaborDetail> laborDetails = null;
		if (claim.getServiceInformation().getServiceDetail() != null) {
			laborDetails = claim.getServiceInformation().getServiceDetail()
					.getLaborPerformed();
		}
		for (LaborDetail laborDetail : laborDetails) {
			ServiceProcedure serviceProcedure = laborDetail
					.getServiceProcedure();
			boolean isbusinessObjectInNamedCategory = service
					.isBusinessObjectInNamedCategory(serviceProcedure,
							categoryName);
			if (isbusinessObjectInNamedCategory && !isNotPredicate) {
				watchMessage.append(serviceProcedure.getDefinition().getCode())
						.append(",");
			} else if (!isbusinessObjectInNamedCategory && isNotPredicate) {
				watchMessage.append(serviceProcedure.getDefinition().getCode())
						.append(",");
			}
		}
		if (watchMessage.toString().endsWith(",")) {
			watchMessage.deleteCharAt(watchMessage.length() - 1);
		}
		if (!isNotPredicate) {
			watchMessage.append(" are in label ");
			watchMessage.append(categoryName);
		} else {
			if (laborDetails != null && laborDetails.size() > 0) {
				watchMessage.append(" are not in label ");
				watchMessage.append(categoryName);
			} else {
				watchMessage.append(" ");
			}
		}
		watchMessage.append(".");
		return watchMessage.toString();
	}

	private String getErrorMsgForWatchedSupplier(Claim claim,
			String categoryName, boolean isNotPredicate) {
		StringBuffer watchMessage = new StringBuffer(" Supplier: ");
		if (claim.getServiceInformation().getContract() != null
				&& claim.getServiceInformation().getContract().getSupplier() != null) {
			watchMessage.append(" ").append(
					claim.getServiceInformation().getContract().getSupplier()
							.getName());
			if (!isNotPredicate) {
				watchMessage.append(" is in label ");
				watchMessage.append(categoryName);
			} else {
				watchMessage.append(" is not in label ");
				watchMessage.append(categoryName);
			}
			watchMessage.append(".");
			return watchMessage.toString();
		}
		return null;
	}
	
	private String getErrorMsgForWatchedOemParts(Claim claim,
			String categoryName, boolean isNotPredicate) {
		StringBuffer watchMessage = new StringBuffer(" Oem Parts: ");
		List<OEMPartReplaced> oemParts = claim.getServiceInformation()
				.getServiceDetail().getReplacedParts();
		for (OEMPartReplaced part : oemParts) {
			boolean isbusinessObjectInNamedCategory = service
					.isBusinessObjectInNamedCategory(part.getItemReference().getReferredItem(),
							categoryName);
			if (isbusinessObjectInNamedCategory && !isNotPredicate) {
				watchMessage.append(part.getItemReference().getReferredItem().getNumber())
						.append(",");
			} else if (!isbusinessObjectInNamedCategory && isNotPredicate) {
				watchMessage.append(part.getItemReference().getReferredItem().getNumber())
						.append(",");
			}
		}
		if (watchMessage.toString().endsWith(",")) {
			watchMessage.deleteCharAt(watchMessage.length() - 1);
		}
		if (!isNotPredicate) {
			watchMessage.append(" are in group ");
			watchMessage.append(categoryName);
		} else {
			if (oemParts != null && oemParts.size() > 0) {
				watchMessage.append(" are not in group ");
				watchMessage.append(categoryName);
			} else {
				watchMessage.append(" ");
			}
		}
		watchMessage.append(".");
		return watchMessage.toString();
	}

    private String getErrorMsgForWatchedModel(Claim claim,
			String categoryName, boolean isNotPredicate) {
		StringBuffer watchMessage = new StringBuffer(" Models: ");
		/*List<ClaimedItem> laborDetails = null;
		if (claim.getClaimedItems() != null) {
			laborDetails = claim.getServiceInformation().getServiceDetail()
					.getLaborPerformed();
		}
		for (LaborDetail laborDetail : laborDetails) {
			ServiceProcedure serviceProcedure = laborDetail
					.getServiceProcedure();
			boolean isbusinessObjectInNamedCategory = seretClvice
					.isBusinessObjectInNamedCategory(serviceProcedure,
							categoryName);
			if (isbusinessObjectInNamedCategory && !isNotPredicate) {
				watchMessage.append(serviceProcedure.getDefinition().getCode())
						.append(",");
			} else if (!isbusinessObjectInNamedCategory && isNotPredicate) {
				watchMessage.append(serviceProcedure.getDefinition().getCode())
						.append(",");
			}
		}
		if (watchMessage.toString().endsWith(",")) {
			watchMessage.deleteCharAt(watchMessage.length() - 1);
		}
		if (!isNotPredicate) {
			watchMessage.append(" are in label ");
			watchMessage.append(categoryName);
		} else {
			if (laborDetails != null && laborDetails.size() > 0) {
				watchMessage.append(" are not in label ");
				watchMessage.append(categoryName);
			} else {
				watchMessage.append(" ");
			}
		}
		watchMessage.append(".");*/
		return watchMessage.toString();
	}
}
