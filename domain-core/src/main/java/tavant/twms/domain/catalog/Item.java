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

package tavant.twms.domain.catalog;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.FilterJoinTable;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Type;
import org.springframework.core.style.ToStringCreator;
import org.springframework.util.Assert;

import tavant.twms.common.Views;
import tavant.twms.domain.bu.BusinessUnit;
import tavant.twms.domain.bu.BusinessUnitComparator;
import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.domain.orgmodel.Supplier;
import tavant.twms.infra.InstanceOfUtil;
import tavant.twms.security.AuditableColumns;
import tavant.twms.security.SecurityHelper;

import com.domainlanguage.money.Money;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * Represents an Inventory Item (Either an equipment or Part)
 *
 * @author kamal.govindraj
 *
 */
@Entity
@Filters({
	@Filter(name="excludeInactive")
})
@FilterDef(name="bu_name", parameters=@ParamDef( name="name", type="string" ) )
@Filter(name = "bu_name", condition = "id in (select mapping.item from bu_item_mapping mapping where mapping.bu in (:name)) ")
public class Item implements AuditableColumns {
	@Id
	@GeneratedValue(generator = "Item")
	@GenericGenerator(name = "Item", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "ITEM_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
			private Long id;

	@Version
    @JsonIgnore
    private int version;

	@NotNull	
	private String name;

	@NotNull
	private String make;

	@ManyToOne(fetch = FetchType.LAZY)
    @JsonView(Views.ExtendedPublic.class)
	private ItemGroup model;

    @JsonIgnore
    private String itemYear;

	@NotNull
	@Column(name = "item_number")
	private String number;

	private String description;
	
	private String OptionDesigantion;

	@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL })
	@JoinColumn(name = "Item", nullable = false)
	private List<I18NItemText> i18nItemTexts = new ArrayList<I18NItemText>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "items_in_group", joinColumns = { @JoinColumn(name = "item") }, inverseJoinColumns = { @JoinColumn(name = "item_group") })
	private Set<ItemGroup> belongsToItemGroups = new HashSet<ItemGroup>();

	@ManyToOne(fetch = FetchType.LAZY)
	private ItemGroup product;

	// TODO - enable lazy loading, some tests
	// fail if lazy loading is enabled, they need to be fixed first
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Party ownedBy;

	@Type(type = "tavant.twms.infra.MoneyUserType")
	@Columns(columns = { @Column(name = "cost_amt"),
			@Column(name = "cost_curr") })
    @JsonIgnore
    private Money costPrice;

	@OneToMany(mappedBy = "partOf")
	@Cascade( { CascadeType.ALL, CascadeType.DELETE_ORPHAN })
	private Set<ItemComposition> parts = new HashSet<ItemComposition>();

	@OneToMany(mappedBy = "item")
	@Cascade( { CascadeType.ALL })
	@JsonIgnore
	private List<BrandItem> brandItems = new ArrayList<BrandItem>();

	private boolean serialized;

    @JsonIgnore
    private boolean usageMeter;

	@Column(name = "item_type")
	private String itemType;

	private String status;

    @JsonIgnore
    private String dealerActualPrice;

    @JsonIgnore
	private String dealerActualPriceCurrency;

    @JsonIgnore
	private String elitePrice;

    @JsonIgnore
	private String elitePriceCurrency;

    @JsonIgnore
	private String weight;

    @JsonIgnore
	private String weightUom;

	@Column(name = "Dim_Pkg_Length")
    @JsonIgnore
	private String dimensionPackageLength;

	@Column(name = "Dim_Pkg_Width")
    @JsonIgnore
	private String dimensionPackageWidth;

	@Column(name = "Dim_Pkg_Height")
    @JsonIgnore
	private String dimensionPackageHeight;

    @JsonIgnore
	private String dimensionUom;

    @JsonIgnore
    private String itemClassCode;

	@ManyToOne(fetch = FetchType.LAZY)
	private ItemGroup classCode;

    @JsonIgnore
	private String supersessionItems;

    @JsonIgnore
	private String hazardFlag;

    @JsonIgnore
	private String dropshipFlag;

    @JsonIgnore
	private String stdQtyPack;

    @JsonIgnore
	private String warrantyFlag;

    @JsonIgnore
	private String keywords;

    @JsonIgnore
	private String divisionCode;

    @JsonIgnore
	private String marketingGroupCode;

	@Transient
    @JsonIgnore
	private String numberForDisplay;

	@Transient
    @JsonIgnore
	private String oemItemNumber;

	@Type(type = "org.hibernate.type.EnumType", parameters = {
			@Parameter(name = "enumClass", value = "tavant.twms.domain.catalog.ItemUOMTypes"),
			@Parameter(name = "type", value = "" + Types.VARCHAR) })
			private ItemUOMTypes uom;

    @JsonIgnore
    private String longDescription;

    @JsonIgnore
    private String machineCode;

    @JsonIgnore
	private String machineCodeDescription;

	private String subSeriesCode;

    @JsonIgnore
	private String subSeriesCodeDescription;

    @JsonIgnore
	private String internalDivisionCode;

    @JsonIgnore
    private String planCode;

	@Column(name = "alternate_item_number")
    @JsonIgnore
    private String alternateNumber;

    @JsonIgnore
	private Boolean duplicateAlternateNumber;

    @JsonIgnore
	private Boolean servicePart;

    @JsonIgnore
	private String pdiFormName;

    @JsonIgnore
	private String serviceCategory;

	/*@OneToMany(fetch = FetchType.LAZY)
	@Cascade( { CascadeType.ALL })
	@JoinColumn(name = "Item", nullable = false)
	private List<SupplierItemLocation> supplierItemLocations = new ArrayList<SupplierItemLocation>();*/

	@Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private AuditableColEntity d = new AuditableColEntity();

    @JsonIgnore
    private String SpecialOptionStatus;

    @JsonIgnore
	private String SpecialOptionStatusDesc;

    @JsonIgnore
    private String DieselTier;

    @JsonIgnore
    private String ModelPower;

    @JsonIgnore
	private String ModelPowerDesc;
	
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "bu_item_mapping", joinColumns = { @JoinColumn(name = "item") }, inverseJoinColumns = { @JoinColumn(name = "bu") })
	@Sort(type = SortType.COMPARATOR, comparator = BusinessUnitComparator.class)
	@FilterJoinTable(name = "bu_name", condition = "bu in (:name) ")
    @JsonIgnore
    private SortedSet<BusinessUnit> businessUnits = new TreeSet<BusinessUnit>();

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the make
	 */
	public String getMake() {
		return make;
	}

	/**
	 * @param make
	 *            the make to set
	 */
	public void setMake(String make) {
		this.make = make;
	}

	/**
	 * @return the model
	 */
	public ItemGroup getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(ItemGroup model) {
		this.model = model;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	public String getDescription() {
		String i18nDescription = "";
		for (I18NItemText i18NItemText : this.i18nItemTexts) {
			if (i18NItemText.getLocale().equalsIgnoreCase(
					new SecurityHelper().getLoggedInUser().getLocale()
					.toString())
					&& i18NItemText.getDescription() != null) {
				i18nDescription = i18NItemText.getDescription();
				break;
			}
		/*	}else if (this.getBusinessUnitInfo()!= null && this.getBusinessUnitInfo().getName()!=null && this.getBusinessUnitInfo().getName().equals("EMEA") && i18NItemText.getLocale().equalsIgnoreCase("en_GB")) {
				i18nDescription = i18NItemText.getDescription();
			}*/else if (i18NItemText.getLocale().equalsIgnoreCase("en_US")) {
				i18nDescription = i18NItemText.getDescription();
			}
		}
		return i18nDescription;
	}

    @JsonIgnore
    public String getItemDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
		if (this.getI18nItemTexts() != null
				&& this.getI18nItemTexts().size() != 0) {
			String locale = new SecurityHelper().getLoggedInUser().getLocale()
			.toString();
			Boolean found = false;
			for (I18NItemText it : this.getI18nItemTexts()) {
				if (it.getLocale().equalsIgnoreCase(locale)) {
					it.setDescription(description);
					found = true;
				}
			}
			if (!found) {
				I18NItemText i18NItemText = new I18NItemText();
				i18NItemText.setDescription(description);
				i18NItemText.setLocale(new SecurityHelper().getLoggedInUser()
						.getLocale().toString());
				this.getI18nItemTexts().add(i18NItemText);
			}
		} /*else if(this.getBusinessUnitInfo()!= null && this.getBusinessUnitInfo().getName()!=null && this.getBusinessUnitInfo().getName().equals("EMEA")){
			I18NItemText i18NItemText = new I18NItemText();
			i18NItemText.setDescription(description);
			i18NItemText.setLocale("en_GB");
			this.getI18nItemTexts().add(i18NItemText);
		}*/ else {
			I18NItemText i18NItemText = new I18NItemText();
			i18NItemText.setDescription(description);
			i18NItemText.setLocale("en_US");
			this.getI18nItemTexts().add(i18NItemText);
		}
	}

	// Through item sync, will receive description in multiple locale
	// and will always have one description at least.
	public void setDescription(String description, String locale) {

		// update description 
		boolean localeFound = false;
		if (null != this.getI18nItemTexts()
				&& this.getI18nItemTexts().size() != 0) {
			for (I18NItemText i18NItemText : this.getI18nItemTexts()) {
				if (i18NItemText.getLocale().equalsIgnoreCase(locale)) {
					//update description
					i18NItemText.setDescription(description);
					i18NItemText.setLocale(locale);
					localeFound = true;
					i18NItemText.setDescription(description);
					break;
				}
			}
			/*if(localeFound){
				I18NItemText i18NItemText = new I18NItemText();
				i18NItemText.setDescription(description);
				this.description=description;
				i18NItemText.setLocale(new SecurityHelper().getLoggedInUser()
						.getLocale().toString());
				this.getI18nItemTexts().add(i18NItemText);
			}*/
		}	
			// add description
			if(!localeFound){
				I18NItemText newI18NItemText = new I18NItemText();
				newI18NItemText.setDescription(description);
				newI18NItemText.setLocale(locale);
				this.getI18nItemTexts().add(newI18NItemText);
			}
	}

	/**
	 * @return the belongsToItemGroups
	 */
	public Set<ItemGroup> getBelongsToItemGroups() {
		return this.belongsToItemGroups;
	}

	/**
	 * @param belongsToItemGroups
	 *            the belongsToItemGroups to set
	 */
	public void setBelongsToItemGroups(Set<ItemGroup> belongsToItemGroups) {
		this.belongsToItemGroups = belongsToItemGroups;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ItemGroup getProduct() {
		return product;
	}

	public void setProduct(ItemGroup product) {
		this.product = product;
	}

	public Party getOwnedBy() {
		return ownedBy;
	}

	public void setOwnedBy(Party ownedBy) {
		this.ownedBy = ownedBy;
	}

    @JsonIgnore
    public boolean isOwnedByOEM() {
		return !(InstanceOfUtil.isInstanceOfClass(Supplier.class, ownedBy));
	}

	public String getItemYear() {
		return itemYear;
	}

	public void setItemYear(String itemYear) {
		this.itemYear = itemYear;
	}

	public Money getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(Money costPrice) {
		this.costPrice = costPrice;
	}

	/**
	 * @param parts
	 *            the parts to set
	 */
	public void setParts(Set<ItemComposition> parts) {
		this.parts = parts;
	}

	/**
	 * @return the parts
	 */
	public Set<ItemComposition> getParts() {
		return parts;
	}

	/**
	 * @param serialized
	 *            the serialized to set
	 */
	public void setSerialized(boolean serialized) {
		this.serialized = serialized;
	}

	/**
	 * @return the serialized
	 */
	public boolean isSerialized() {
		return serialized;
	}

	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", id).append("name", name)
		.append("make", make).append("model", model).append("number",
				number).append("description", description).toString();
	}

	public Item include(int quantity, Item part) {
		ItemComposition itemComposition = new ItemComposition(part, this,
				quantity);
		getParts().add(itemComposition);
		return this;
	}

	public boolean includes(Item part) {
		Assert.notNull(part, "Part not specified");

		// A BFS traversal based check. A DFS traversal could be sub-optimal.
		Set<ItemComposition> _composedParts = getParts();
		for (ItemComposition _composedPart : _composedParts) {
			if (part.equals(_composedPart.getItem())) {
				return true;
			}
		}

		for (ItemComposition _composedPart : _composedParts) {
			if (_composedPart.getItem().includes(part)) {
				return true;
			}
		}

		return false;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public boolean hasUsageMeter() {
		return usageMeter;
	}

	public void setUsageMeter(boolean usageMeter) {
		this.usageMeter = usageMeter;
	}

	public Item cloneMe() {
		Item clone = new Item();
		clone.setDescription(this.getDescription());
		clone.setName(this.getName());
		clone.setMake(this.getMake());
		clone.setSerialized(this.isSerialized());
		clone.setUsageMeter(this.hasUsageMeter());
		clone.setItemYear(this.getItemYear());
		clone.setProduct(this.getProduct());
		clone.setModel(this.getModel());
		clone.setVersion(0);
		clone.setOwnedBy(this.getOwnedBy());
		clone.setItemType(this.getItemType());
		clone.setPdiFormName(this.getPdiFormName());
		return clone;
	}

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}

/*	@JsonView(value=Views.ExtendedPublic.class)
	@Type(type = "tavant.twms.domain.bu.BusinessUnitInfoType")
	private BusinessUnitInfo businessUnitInfo = new BusinessUnitInfo();

	public BusinessUnitInfo getBusinessUnitInfo() {
		return businessUnitInfo;
	}

	public void setBusinessUnitInfo(BusinessUnitInfo buAudit) {
		this.businessUnitInfo = buAudit;
	}
*/
	public List<I18NItemText> getI18nItemTexts() {
		return i18nItemTexts;
	}

	public void setI18nItemTexts(List<I18NItemText> itemTexts) {
		i18nItemTexts = itemTexts;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

    @JsonIgnore
    public String getRuleContextDescription() {
		String description_locale = "";
		for (I18NItemText i18nLovText : this.i18nItemTexts) {
			if (i18nLovText.getLocale().equalsIgnoreCase("en_US")) {
				description_locale = i18nLovText.getDescription();
			}
		}
		return description_locale;
	}

	public ItemGroup getClassCode() {
		return classCode;
	}

	public void setClassCode(ItemGroup classCode) {
		this.classCode = classCode;
	}

	public String getDealerActualPriceCurrency() {
		return dealerActualPriceCurrency;
	}

	public void setDealerActualPriceCurrency(String dealerActualPriceCurrency) {
		this.dealerActualPriceCurrency = dealerActualPriceCurrency;
	}

	public String getDimensionUom() {
		return dimensionUom;
	}

	public void setDimensionUom(String dimensionUom) {
		this.dimensionUom = dimensionUom;
	}

	public String getDropshipFlag() {
		return dropshipFlag;
	}

	public void setDropshipFlag(String dropshipFlag) {
		this.dropshipFlag = dropshipFlag;
	}

	public String getElitePriceCurrency() {
		return elitePriceCurrency;
	}

	public void setElitePriceCurrency(String elitePriceCurrency) {
		this.elitePriceCurrency = elitePriceCurrency;
	}

	public String getHazardFlag() {
		return hazardFlag;
	}

	public void setHazardFlag(String hazardFlag) {
		this.hazardFlag = hazardFlag;
	}

	public String getItemClassCode() {
		return itemClassCode;
	}

	public void setItemClassCode(String itemClassCode) {
		this.itemClassCode = itemClassCode;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getStdQtyPack() {
		return stdQtyPack;
	}

	public void setStdQtyPack(String stdQtyPack) {
		this.stdQtyPack = stdQtyPack;
	}

	public String getSupersessionItems() {
		return supersessionItems;
	}

	public void setSupersessionItems(String supersessionItems) {
		this.supersessionItems = supersessionItems;
	}

	public String getWarrantyFlag() {
		return warrantyFlag;
	}

	public void setWarrantyFlag(String warrantyFlag) {
		this.warrantyFlag = warrantyFlag;
	}

	public String getWeightUom() {
		return weightUom;
	}

	public void setWeightUom(String weightUom ) {
		this.weightUom = weightUom;
	}

	public boolean isUsageMeter() {
		return usageMeter;
	}

	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	public ItemUOMTypes getUom() {
		return uom;
	}

	public void setUom(ItemUOMTypes uom) {
		this.uom = uom;
	}

	public String getInternalDivisionCode() {
		return internalDivisionCode;
	}

	public void setInternalDivisionCode(String internalDivisionCode) {
		this.internalDivisionCode = internalDivisionCode;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public String getMachineCode() {
		return machineCode;
	}

	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}

	public String getMachineCodeDescription() {
		return machineCodeDescription;
	}

	public void setMachineCodeDescription(String machineCodeDescription) {
		this.machineCodeDescription = machineCodeDescription;
	}

	public String getPlanCode() {
		return planCode;
	}

	public void setPlanCode(String planCode) {
		this.planCode = planCode;
	}

	public String getSubSeriesCode() {
		return subSeriesCode;
	}

	public void setSubSeriesCode(String subSeriesCode) {
		this.subSeriesCode = subSeriesCode;
	}

	public String getSubSeriesCodeDescription() {
		return subSeriesCodeDescription;
	}

	public void setSubSeriesCodeDescription(String subSeriesCodeDescription) {
		this.subSeriesCodeDescription = subSeriesCodeDescription;
	}

	public String getDealerActualPrice() {
		return dealerActualPrice;
	}

	public void setDealerActualPrice(String dealerActualPrice) {
		this.dealerActualPrice = dealerActualPrice;
	}

	public String getDimensionPackageHeight() {
		return dimensionPackageHeight;
	}

	public void setDimensionPackageHeight(String dimensionPackageHeight) {
		this.dimensionPackageHeight = dimensionPackageHeight;
	}

	public String getDimensionPackageLength() {
		return dimensionPackageLength;
	}

	public void setDimensionPackageLength(String dimensionPackageLength) {
		this.dimensionPackageLength = dimensionPackageLength;
	}

	public String getDimensionPackageWidth() {
		return dimensionPackageWidth;
	}

	public void setDimensionPackageWidth(String dimensionPackageWidth) {
		this.dimensionPackageWidth = dimensionPackageWidth;
	}

	public String getElitePrice() {
		return elitePrice;
	}

	public void setElitePrice(String elitePrice) {
		this.elitePrice = elitePrice;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getAlternateNumber() {
		return alternateNumber;
	}

	public void setAlternateNumber(String alternateNumber) {
		this.alternateNumber = alternateNumber;
	}

	public Boolean getDuplicateAlternateNumber() {
		return duplicateAlternateNumber;
	}

	public void setDuplicateAlternateNumber(Boolean duplicateAlternateNumber) {
		this.duplicateAlternateNumber = duplicateAlternateNumber;
	}

	public Boolean getServicePart() {
		return servicePart;
	}

	public void setServicePart(Boolean servicePart) {
		this.servicePart = servicePart;
	}

	/**
	 * This method is required as Jasper Sub Report loses the authentication of main report
	 * Need to revisit while doing i18n of reports
	 * @return
	 */
    @JsonIgnore
    public String getDescriptionForPrint(){
		for (I18NItemText i18NItemText : this.i18nItemTexts) {
			if (i18NItemText.getLocale().equalsIgnoreCase("en_US")) {
				return i18NItemText.getDescription();				
			}
		}
		return "";
	}

	public void setNumberForDisplay(String numberForDisplay) {
		this.numberForDisplay = numberForDisplay;
	}

	public String getNumberForDisplay() {
		this.numberForDisplay = this.number + " " + this.getOwnedBy().getName();
		return this.numberForDisplay;
	}

	public String getOemItemNumber() {
		return oemItemNumber;
	}

	public void setOemItemNumber(String oemItemNumber) {
		this.oemItemNumber = oemItemNumber;
	}


	public String getPdiFormName() {
		return pdiFormName;
	}

	public void setPdiFormName(String pdiFormName) {
		this.pdiFormName = pdiFormName;
	}


	public String getMarketingGroupCode() {
		return marketingGroupCode;
	}

	public void setMarketingGroupCode(String marketingGroupCode) {
		this.marketingGroupCode = marketingGroupCode;
	}


	public String getServiceCategory() {
		return serviceCategory;
	}

	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}

	public String getBrandItemNumber(String brand)
	{
		if(!this.brandItems.isEmpty()&& brand!=null && !brand.isEmpty())
		{
			for(BrandItem item:brandItems)
			{
				if(item.getBrand().equalsIgnoreCase(brand))
				{
					return item.getItemNumber();
				}
			}
		}
		return this.number;
	}
	
	public BrandItem getBrandItem(String brand)
	{
		if(!this.brandItems.isEmpty()&& brand!=null && !brand.isEmpty())
		{
			for(BrandItem item:brandItems)
			{
				if(item.getBrand().equalsIgnoreCase(brand))
				{
					return item;
				}
			}
		}
		return null;
	}

    public String getBrandItemId(String brand)
    {
        if(!this.brandItems.isEmpty()&& brand!=null && !brand.isEmpty())
        {
            for(BrandItem item:brandItems)
            {
                if(item.getBrand().equalsIgnoreCase(brand))
                {
                    return String.valueOf(item.getId());
                }
            }
        }
        return this.number;
    }

    @JsonIgnore
    public String getSpecialOptionStatus() {
		return SpecialOptionStatus;
	}

	public void setSpecialOptionStatus(String specialOptionStatus) {
		SpecialOptionStatus = specialOptionStatus;
	}

    @JsonIgnore
	public String getSpecialOptionStatusDesc() {
		return SpecialOptionStatusDesc;
	}

	public void setSpecialOptionStatusDesc(String specialOptionStatusDesc) {
		SpecialOptionStatusDesc = specialOptionStatusDesc;
	}

    @JsonIgnore
    public String getDieselTier() {
		return DieselTier;
	}

	public void setDieselTier(String dieselTier) {
		DieselTier = dieselTier;
	}

    @JsonIgnore
    public String getModelPower() {
		return ModelPower;
	}

	public void setModelPower(String modelPower) {
		ModelPower = modelPower;
	}

    @JsonIgnore
	public String getModelPowerDesc() {
		return ModelPowerDesc;
	}

	public void setModelPowerDesc(String modelPowerDesc) {
		ModelPowerDesc = modelPowerDesc;
	}
	
	public List<BrandItem> getBrandItems() {
		return brandItems;
	}

	public SortedSet<BusinessUnit> getBusinessUnits() {
		return businessUnits;
	}

	public void setBusinessUnits(SortedSet<BusinessUnit> businessUnits) {
		this.businessUnits = businessUnits;
	}

	public String getOptionDesigantion() {
		return OptionDesigantion;
	}

	public void setOptionDesigantion(String optionDesigantion) {
		OptionDesigantion = optionDesigantion;
	}
	
}

