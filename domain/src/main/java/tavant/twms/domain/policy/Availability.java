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
package tavant.twms.domain.policy;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;
import com.domainlanguage.timeutil.Clock;

import org.hibernate.annotations.Cascade;
import javax.persistence.CascadeType;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.domain.common.CalendarDuration;
import tavant.twms.domain.common.GlobalConfiguration;
import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryItemCondition;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author radhakrishnan.j
 *
 */
@Embeddable
public class Availability {

    @Embedded
    @AttributeOverrides( {
            @AttributeOverride(name = "fromDate", column = @Column(name = "from_date")),
            @AttributeOverride(name = "tillDate", column = @Column(name = "till_date")) })
    private CalendarDuration duration;

    @Type(type = "tavant.twms.infra.MoneyUserType")
    @Columns(columns = { @Column(name = "amount", nullable = false),
            @Column(name = "currency", nullable = false) })
    private Money price;
    
    @OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
    @JoinTable(name = "POLICY_PRODUCTS", joinColumns = { @JoinColumn(name = "POLICY_DEFINITION") }, inverseJoinColumns = { @JoinColumn(name = "POLICY_PRODUCT_MAPPING") })
    @Cascade({org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private List<PolicyProductMapping> products =  new ArrayList<PolicyProductMapping>();
	
	public List<PolicyProductMapping> getProducts() {
		return products;
	}

	public void setProducts(List<PolicyProductMapping> products) {
		this.products = products;
	}

	@ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "policy_for_itemconditions", joinColumns = { @JoinColumn(name = "policy_defn") }, inverseJoinColumns = { @JoinColumn(name = "for_itemcondition") })
    private Set<InventoryItemCondition> itemConditions = new HashSet<InventoryItemCondition>();

    @ManyToOne(fetch = FetchType.LAZY)
    private OwnershipState ownershipState;

    public Availability() {
        GlobalConfiguration config = GlobalConfiguration.getInstance();
        this.price = config.zeroInBaseCurrency();
    }

    @Transient
    public boolean isAvailableByDefault() {
        return this.price.isZero();
    }

    public boolean isAvailableFor(InventoryItem inventoryItem) {
        CalendarDate today = Clock.today();
        if (!this.duration.includes(today)) {
            return false;
        }

        InventoryItemCondition inventoryItemCondition = inventoryItem.getConditionType();
        if (this.itemConditions != null && !this.itemConditions.isEmpty()) {
            boolean itemConditionCovered = false;
            for (InventoryItemCondition inItemCondition : this.itemConditions) {
                if (inItemCondition.getItemCondition().equalsIgnoreCase(inventoryItemCondition.getItemCondition())) {
                    itemConditionCovered = true;
                    break;
                }
            }
            if (!itemConditionCovered) {
                return false;
            }
        }

        String ownerState = inventoryItem.getOwnershipState().getName();

        if (OwnershipState.BOTH.getName().equals(this.ownershipState.getName()) ||
                this.ownershipState.getName().equals(ownerState)) {

            if (isPolicyProductPartOfTree(inventoryItem.getOfType().getModel())) {
                return true;
            }

        }

        return false;
    }

    /**
     * 
     *
     * @param itemModel
     * @return
     */
    private boolean isPolicyProductPartOfTree(ItemGroup itemModel) {
    	for(PolicyProductMapping product : this.products){
        	if(product.getProduct().equals(itemModel)){
        		return true;
        	}else if(product.getProduct().getIsPartOf().equals(itemModel)){
        		return true;
        	}
        }
        return false;
    }

    public CalendarDuration getDuration() {
        return this.duration;
    }

    public void setDuration(CalendarDuration duration) {
        this.duration = duration;
    }

    public Money getPrice() {
        return this.price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public OwnershipState getOwnershipState() {
        return this.ownershipState;
    }

    public void setOwnershipState(OwnershipState ownershipState) {
        this.ownershipState = ownershipState;
    }

	public Set<InventoryItemCondition> getItemConditions() {
		return itemConditions;
	}

	public void setItemConditions(Set<InventoryItemCondition> itemConditions) {
		this.itemConditions = itemConditions;
	}
    
    
}
