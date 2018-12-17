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
package tavant.twms.web.admin.payment;

import com.domainlanguage.time.CalendarDate;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import tavant.twms.domain.catalog.*;
import tavant.twms.domain.claim.Criteria;
import tavant.twms.domain.claim.payment.rates.AdministeredItemPrice;
import tavant.twms.domain.claim.payment.rates.ItemPriceAdminService;
import tavant.twms.domain.claim.payment.rates.ItemPriceModifier;
import tavant.twms.domain.common.AdminConstants;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.DurationOverlapException;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;
import tavant.twms.web.TWMSWebConstants;
import tavant.twms.web.i18n.I18nActionSupport;

import java.util.*;

@SuppressWarnings("serial")
public class ManageItemPriceModifier extends I18nActionSupport implements
		Preparable, Validateable {

	private Logger logger = Logger.getLogger(ManageItemPriceModifier.class);

	private static final String MESSAGE_KEY_FAILURE = "error.manageRates.error";
	private static final String MESSAGE_KEY_OVERLAP = "error.manageRates.durationOverlapForValidityDatePriceEntry";
	private static final String MESSAGE_KEY_DUPLICATE = "error.manageRates.duplicatePriceConfig";

	private static final String MESSAGE_KEY_UPDATE = "message.manageRates.updateItemPriceSuccess";
	private static final String MESSAGE_KEY_CREATE = "message.manageRates.createItemPriceSuccess";
	private static final String MESSAGE_KEY_DELETE = "message.manageRates.deleteItemPriceSuccess";

	private String id;
	private String partCriterion;
	private AdministeredItemPrice itemPrice;
	private List<ItemPriceModifier> priceEntries = new ArrayList<ItemPriceModifier>();

	private CatalogService catalogService;
	private ItemPriceAdminService itemPriceAdminService;
	private ItemGroupService itemGroupService;
	private boolean itemGroupSelected;
	private String itemGroupName;
	private WarrantyService warrantyService;
	private List<WarrantyType> warrantyTypes = new ArrayList<WarrantyType>();


	public void prepare() throws Exception {
		if (StringUtils.isNotBlank(this.id)) {
			Long pk = Long.parseLong(this.id);
			this.itemPrice = this.itemPriceAdminService.findById(pk);
		} else {
			this.itemPrice = new AdministeredItemPrice();
		}
		setWarrantyTypes(this.warrantyService.listWarrantyTypes());
	}

	@Override
	public void validate() {
		for (Iterator<ItemPriceModifier> iter = this.priceEntries.iterator(); iter
				.hasNext();) {
			ItemPriceModifier ipmEntry = iter.next();
			if (ipmEntry == null
					|| (ipmEntry.getDuration().getFromDate() == null
							&& ipmEntry.getDuration().getTillDate() == null && ipmEntry
							.getScalingFactor() == null)) {
				iter.remove();
			}
		}
		validateNegativeModifiers();
		super.validate();
		validatePartCriterion();
		validateItemPriceModifierDate();
	}

	public String showPrice() {
		this.priceEntries = new ArrayList<ItemPriceModifier>();
		this.priceEntries.addAll(this.itemPrice.getEntries());
		return SUCCESS;
	}

	public String createPrice() {
		String action = preparePrice();
		if (SUCCESS.equals(action)) {
			try {
				this.itemPriceAdminService.save(this.itemPrice);
			} catch (RuntimeException e) {
				this.logger.error("Creation Failed", e);
				addActionMessage(MESSAGE_KEY_FAILURE);
				return INPUT;
			}
			addActionMessage(MESSAGE_KEY_CREATE);
		}
		return action;
	}

	public String updatePrice() {
		String action = preparePrice();
		if (SUCCESS.equals(action)) {
			try {
				this.itemPriceAdminService.update(this.itemPrice);
			} catch (RuntimeException e) {
				this.logger.error("Update Failed", e);
				addActionMessage(MESSAGE_KEY_FAILURE);
				return INPUT;
			}
			addActionMessage(MESSAGE_KEY_UPDATE);
		}
		return action;
	}

	public String deletePrice() {
		try {
			this.itemPriceAdminService.delete(this.itemPrice);
			addActionMessage(MESSAGE_KEY_DELETE);
		} catch (RuntimeException e) {
			this.logger.error("Exception in deleting part price", e);
			addActionMessage(MESSAGE_KEY_FAILURE);
		}
		return SUCCESS;
	}

	public String getWarrantyTypeString() {
		Criteria forCriteria = this.itemPrice.getForCriteria();
		if (forCriteria == null
				|| StringUtils.isBlank(forCriteria.getWarrantyType())) {
			return getText("all.warranty.types");
		}
		return forCriteria.getWarrantyType();
	}

	// ********************* Accessors & Mutators ****************//

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AdministeredItemPrice getItemPrice() {
		return this.itemPrice;
	}

	public void setItemPrice(AdministeredItemPrice itemPrice) {
		this.itemPrice = itemPrice;
	}

	public List<ItemPriceModifier> getPriceEntries() {
		return this.priceEntries;
	}

	public void setPriceEntries(List<ItemPriceModifier> priceEntries) {
		this.priceEntries = priceEntries;
	}

	public void setPartCriterion(String partCriterion) {
		this.partCriterion = partCriterion;
	}

	public String getPartCriterion() {
		return this.partCriterion;
	}

	// Dependency
	public void setCatalogService(CatalogService catalogService) {
		this.catalogService = catalogService;
	}

	public void setItemPriceAdminService(
			ItemPriceAdminService itemPriceAdminService) {
		this.itemPriceAdminService = itemPriceAdminService;
	}

	// **************** Private Methods ******************//
	private String preparePrice() {
		if (!this.itemPriceAdminService.isUnique(this.itemPrice)) {
			addActionError(MESSAGE_KEY_DUPLICATE);
			return INPUT;
		}
		Set<Long> idsFromUI = new HashSet<Long>();
		for (ItemPriceModifier rate : this.priceEntries) {
			if (rate.getId() != null) {
				idsFromUI.add(rate.getId());
			}
		}

		for (Iterator<ItemPriceModifier> it = this.itemPrice.getEntries()
				.iterator(); it.hasNext();) {
			ItemPriceModifier rate = it.next();
			if (rate.getId() != null && !idsFromUI.contains(rate.getId())) {
				it.remove();
			}
		}

		try {
			for (ItemPriceModifier item : this.priceEntries) {
				this.itemPrice.set(item.getValue(), item.getDuration());
			}
		} catch (DurationOverlapException e) {
			this.logger.error("Duration Overlap exception", e);
			addActionError(MESSAGE_KEY_OVERLAP);
			return INPUT;
		}
		return SUCCESS;
	}

	private void validatePartCriterion() {
		if (!this.itemGroupSelected
				&& StringUtils.isNotBlank(this.partCriterion)) {
			try {
				Item item = this.catalogService.findItemOwnedByManuf(this.partCriterion);
				this.itemPrice.setItemCriterion(new ItemCriterion(item));

			} catch (CatalogException e) {
				this.logger.error(e);
				addFieldError("partCriterion",
						"error.manageRates.noPartExists",
						new String[] { this.partCriterion });
			}
		} else if (this.itemGroupSelected
				&& StringUtils.isNotBlank(this.itemGroupName)) {
			ItemGroup itemGroup = this.itemGroupService.findByNameAndPurpose(
					this.itemGroupName, AdminConstants.ITEM_PRICE_PURPOSE);
			if (itemGroup == null) {
				addFieldError("itemGroupName",
						"error.manageRates.noItemGroupExists",
						new String[] { this.itemGroupName });
			} else {
				ItemCriterion criterion = new ItemCriterion();
				criterion.setItemGroup(itemGroup);
				this.itemPrice.setItemCriterion(criterion);
			}
		} else {
			addFieldError("partCriterion", "error.manageRates.invalidValue");
		}
	}

	private void validateItemPriceModifierDate() {
		int numRates = this.priceEntries.size();

		if (numRates < 2) {
			return;
		}

		for (int i = 1; i < numRates; i++) {
			CalendarDuration thisIPMDuration = this.priceEntries.get(i)
					.getDuration();
			CalendarDuration prevIPMDuration = this.priceEntries.get(i - 1)
					.getDuration();

			CalendarDate prevEndDate = prevIPMDuration.getTillDate();
			CalendarDate currentStartDate = thisIPMDuration.getFromDate();

			if ((prevEndDate != null && currentStartDate != null)
					&& !(prevEndDate.nextDay().equals(currentStartDate))) {

				addActionError(
						"error.manageRates.noGapsInConsecutiveDateRange",
						new String[] { prevEndDate.toString(),
								currentStartDate.toString() });
			}
		}
	}

	private void validateNegativeModifiers() {
		for (ItemPriceModifier priceModifier : this.priceEntries) {
			if ((priceModifier.getScalingFactor() != null)
					&& (priceModifier.getScalingFactor().doubleValue() < 0)
					&& (priceModifier.getScalingFactor().doubleValue() <= -100)) {
				addActionError("error.manageRates.highestNegativeModifierSpecified");
				break;
			}
		}
	}

	public String getItemGroupName() {
		return this.itemGroupName;
	}

	public void setItemGroupName(String itemGroupName) {
		this.itemGroupName = itemGroupName;
	}

	public boolean isItemGroupSelected() {
		return this.itemGroupSelected;
	}

	public void setItemGroupSelected(boolean itemGroupSelected) {
		this.itemGroupSelected = itemGroupSelected;
	}

	public void setItemGroupService(ItemGroupService itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	@Autowired
	public void setWarrantyService(WarrantyService warrantyService) {
		this.warrantyService = warrantyService;
	}

	public List<WarrantyType> getWarrantyTypes() {
		return warrantyTypes;
	}

	public void setWarrantyTypes(List<WarrantyType> warrantyTypes) {
		this.warrantyTypes = warrantyTypes;
	}
	

}
