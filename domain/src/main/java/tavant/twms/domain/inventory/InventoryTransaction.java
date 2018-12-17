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

package tavant.twms.domain.inventory;

import javax.persistence.*;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.util.StringUtils;

import tavant.twms.domain.common.AuditableColEntity;
import tavant.twms.domain.orgmodel.Party;
import tavant.twms.infra.BaseDomain;
import tavant.twms.security.AuditableColumns;

import com.domainlanguage.time.CalendarDate;

@Entity
@Filters({
  @Filter(name="excludeInactive")
})
public class InventoryTransaction extends BaseDomain implements Comparable<InventoryTransaction>, AuditableColumns{

    public static final String DR = "DR", ETR = "TTR", RMT = "RTT", DR_DELETE = "DR_DELETE",
            DR_MODIFY = "DR_MODIFY", ETR_MODFY="ETR_MODIFY",ETR_DELETE="ETR_DELETE" , D2D="D2D",EXTENED_WNTY_PURCHASE = "EXTWARPURCHASE";

    @Id
    @GeneratedValue(generator = "InventoryTransaction")
	@GenericGenerator(name = "InventoryTransaction", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "INVENTORY_TRANSACTION_SEQ"),
			@Parameter(name = "initial_value", value = "1000"),
			@Parameter(name = "increment_size", value = "20") })
    private Long id;
    
    
    private Long hoursOnMachine = new Long(0);

    @Version
    private int version;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate transactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Party seller;

    @ManyToOne(fetch = FetchType.LAZY)
    private Party buyer;

    public Long getHoursOnMachine() {
		return hoursOnMachine;
	}

	public void setHoursOnMachine(Long hoursOnMachine) {
		this.hoursOnMachine = hoursOnMachine;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional=false)
    private Party ownerShip;

    private String salesOrderNumber;

    private String invoiceNumber;

    @Type(type = "tavant.twms.infra.CalendarDateUserType")
    private CalendarDate invoiceDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private InventoryItem transactedItem;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private InventoryTransactionType invTransactionType;
    
    @Embedded
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
   	private AuditableColEntity d = new AuditableColEntity();
    
    private Long transactionOrder;
    
    private String shipToSiteNumber;

    @Column(length = 4000)
    private String invoiceComments;

    public String getInvoiceComments() {
		return invoiceComments;
	}

	public void setInvoiceComments(String invoiceComments) {
		this.invoiceComments = invoiceComments;
	}

	public String getShipToSiteNumber() {
		return shipToSiteNumber;
	}

	public void setShipToSiteNumber(String shipToSiteNumber) {
		this.shipToSiteNumber = shipToSiteNumber;
	}

	public Party getSeller() {
        return this.seller;
    }

    public void setSeller(Party from) {
        this.seller = from;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public CalendarDate getInvoiceDate() {
        return this.invoiceDate;
    }

    public void setInvoiceDate(CalendarDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceNumber() {
        return this.invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getSalesOrderNumber() {
        return this.salesOrderNumber;
    }

    public void setSalesOrderNumber(String salesOrderNumber) {
        this.salesOrderNumber = salesOrderNumber;
    }

    public Party getBuyer() {
        return this.buyer;
    }

    public void setBuyer(Party to) {
        this.buyer = to;
    }

    public CalendarDate getTransactionDate() {
        return this.transactionDate;
    }

    public void setTransactionDate(CalendarDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public int compareTo(InventoryTransaction other) {
        if (other == null) {
            return 1;
        }

        return -1 * this.transactionOrder.compareTo(other.getTransactionOrder()); // descending
        // order
    }

    public InventoryItem getTransactedItem() {
        return this.transactedItem;
    }

    public void setTransactedItem(InventoryItem transactedItem) {
        this.transactedItem = transactedItem;
        setHoursOnMachine(transactedItem.getHoursOnMachine());
    }

    public InventoryTransactionType getInvTransactionType() {
        return this.invTransactionType;
    }

    public void setInvTransactionType(InventoryTransactionType invTransactionType) {
        this.invTransactionType = invTransactionType;
    }

    public Party getOwnerShip() {
        return this.ownerShip;
    }

    public void setOwnerShip(Party ownerShip) {
        this.ownerShip = ownerShip;
    }

    public Long getTransactionOrder() {
        return this.transactionOrder;
    }

    public void setTransactionOrder(Long transactionOrder) {
        this.transactionOrder = transactionOrder;
    }

	public AuditableColEntity getD() {
		return d;
	}

	public void setD(AuditableColEntity d) {
		this.d = d;
	}
	public String getModifiedSiteNumber(String siteNumber){
        if(StringUtils.hasText(siteNumber)){
            return siteNumber.substring(9, 13);
        }
        return siteNumber;
    }

}
