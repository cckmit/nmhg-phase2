package tavant.twms.web.admin.warranty;

import tavant.twms.domain.inventory.InventoryItem;
import tavant.twms.domain.inventory.InventoryTransactionType;
import tavant.twms.domain.orgmodel.Party;

import com.domainlanguage.time.CalendarDate;

public class InventoryTransactionDisplayObject {

	private Long id;

	private CalendarDate transactionDate;

	private Party seller;

	private Party buyer;

	private Party ownerShip;

	private String salesOrderNumber;

	private String invoiceNumber;

	private CalendarDate invoiceDate;

	private InventoryItem transactedItem;

	private InventoryTransactionType invTransactionType;

	private boolean isModifyAllowed;

	private boolean isLatestTransactionForAType;


	private boolean isWarrantyPresent = false;

	public CalendarDate getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(CalendarDate transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Party getSeller() {
		return seller;
	}

	public void setSeller(Party seller) {
		this.seller = seller;
	}

	public Party getBuyer() {
		return buyer;
	}

	public void setBuyer(Party buyer) {
		this.buyer = buyer;
	}

	public Party getOwnerShip() {
		return ownerShip;
	}

	public void setOwnerShip(Party ownerShip) {
		this.ownerShip = ownerShip;
	}

	public String getSalesOrderNumber() {
		return salesOrderNumber;
	}

	public void setSalesOrderNumber(String salesOrderNumber) {
		this.salesOrderNumber = salesOrderNumber;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public CalendarDate getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(CalendarDate invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public InventoryItem getTransactedItem() {
		return transactedItem;
	}

	public void setTransactedItem(InventoryItem transactedItem) {
		this.transactedItem = transactedItem;
	}

	public InventoryTransactionType getInvTransactionType() {
		return invTransactionType;
	}

	public void setInvTransactionType(
			InventoryTransactionType invTransactionType) {
		this.invTransactionType = invTransactionType;
	}

	public boolean isModifyAllowed() {
		return isModifyAllowed;
	}

	public void setModifyAllowed(boolean isModifyAllowed) {
		this.isModifyAllowed = isModifyAllowed;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isLatestTransactionForAType() {
		return isLatestTransactionForAType;
	}

	public void setLatestTransactionForAType(boolean isLatestTransactionForAType) {
		this.isLatestTransactionForAType = isLatestTransactionForAType;
	}

	public boolean isWarrantyPresent() {
		return isWarrantyPresent;
	}

	public void setWarrantyPresent(boolean isWarrantyPresent) {
		this.isWarrantyPresent = isWarrantyPresent;
	}

}
