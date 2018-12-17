/*
 *   Copyright (c) 2007 Tavant Technologies
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
package tavant.twms.domain.rules;

/**
 * @author mritunjay.kumar
 */
import static tavant.twms.domain.rules.PredicateEvaluator.IS_NOT_PREDICATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.catalog.ItemGroupService;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.InstalledParts;
import tavant.twms.domain.claim.OEMPartReplaced;
import tavant.twms.domain.claim.PartReplaced;

public class PartWatchAction implements PredicateEvaluationAction {
	private DomainSpecificVariable domainVariable;
	private String itemWatchListType;

	public String performAction(Map<String, Object> evaluationContext) {
		String resultMessage = null;

		if (domainVariable == null) {
			throw new IllegalStateException(
					"domainVariable cannot be null in PartWatchAction");
		}

		List<Item> itemList = new ArrayList<Item>();
		ItemGroupService service = (ItemGroupService) evaluationContext
				.get("itemGroupService");
		Claim claim = (Claim) evaluationContext.get("claim");
		boolean isNotPredicate = (Boolean) evaluationContext
				.get(IS_NOT_PREDICATE);

		boolean isForOEMParts = domainVariable.getAccessedFromType().equals(
				"OEMPartReplaced")
				&& domainVariable.getFieldName().equals(
						"itemReference.referredItem");
		boolean isForCausalPart = domainVariable.getAccessedFromType().equals(
				"Claim")
				&& domainVariable.getFieldName().equals(
						"claim.serviceInformation.causalPart");

		if (isForOEMParts) {
			List<OEMPartReplaced> oemPartsReplaced = claim
					.getServiceInformation().getServiceDetail()
					.getReplacedParts();
			for (OEMPartReplaced oemPartReplaced : oemPartsReplaced) {
				if (oemPartReplaced.getItemReference() != null) {
					Item item = oemPartReplaced.getItemReference()
							.getReferredItem();
					itemList.add(item);
				}
			}

			List<PartReplaced> oemPartsInstalled = claim
					.getServiceInformation().getServiceDetail()
					.getPriceFetchedParts();
			for ( PartReplaced oemPartInstalled : oemPartsInstalled) {
				if(!claim.getServiceInformation().getServiceDetail().isOEMPartReplaced(oemPartInstalled)) {
					itemList.add(((InstalledParts)oemPartInstalled).getItem());
				}
			}
		} else if (isForCausalPart) {
			Item item = claim.getServiceInformation().getCausalPart();
			itemList.add(item);
		}

		Map<Item, ItemGroup> itemGroupItemMap = service
				.findItemGroupMapForWatchedItems(itemList, itemWatchListType);
		StringBuffer watchMessage = null;
		List<Item> watchedItems = new ArrayList<Item>();
		if (!isNotPredicate) {
			if (itemGroupItemMap != null && itemGroupItemMap.size() > 0) {
				if (isForOEMParts) {
					watchMessage = new StringBuffer(
							"Following oem parts were found in the watch list: ");
				} else if (isForCausalPart) {
					watchMessage = new StringBuffer(
							"Following causal part were found in the watch list: ");
				}
				for (Map.Entry<Item, ItemGroup> entry : itemGroupItemMap
						.entrySet()) {
					watchedItems.add(entry.getKey());
					watchMessage.append((entry.getKey()).getNumber());
					watchMessage.append("(Group: ");
					watchMessage.append((entry.getValue()).getName()).append(
							")");
					watchMessage.append(",");
				}
			}
		} else if (isNotPredicate && isForOEMParts) {
			if (itemList != null && itemList.size() > 0) {
				watchMessage = new StringBuffer(
						"Following oem parts were not found in the watch list: ");
				itemList.removeAll(watchedItems);
				for (Item item : itemList) {
					watchMessage.append(item.getNumber());
					watchMessage.append(",");
				}
			} else {
				watchMessage = new StringBuffer(
						"None of the oem parts replaced ");
			}
		}
		if (watchMessage != null) {
			resultMessage = watchMessage
					.substring(0, watchMessage.length() - 1);
		}
		return resultMessage;
	}

	public void setDomainVariable(DomainSpecificVariable domainVariable) {
		this.domainVariable = domainVariable;
	}

	public void setItemWatchListType(String itemWatchListType) {
		this.itemWatchListType = itemWatchListType;
	}

}
