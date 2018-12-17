package tavant.twms.domain.policy;

import java.util.ArrayList;
import java.util.List;

import com.domainlanguage.money.Money;
import com.domainlanguage.time.CalendarDate;

public class DebitMemo {

	private String purchaseOrderNumber;
	
	private String debitMemoNumber;
	
	private String  serialNumber;
	
	private CalendarDate invoiceDate;
	
	private Money totalAmount;
	
	private Money taxAmount;
	
	private String dealerNumber;
	
	private List<ExtWarrantyPlan> plans = new ArrayList<ExtWarrantyPlan>();

	public String getPurchaseOrderNumber() {
		return purchaseOrderNumber;
	}

	public void setPurchaseOrderNumber(String purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
	}

	public String getDebitMemoNumber() {
		return debitMemoNumber;
	}

	public void setDebitMemoNumber(String debitMemoNumber) {
		this.debitMemoNumber = debitMemoNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public CalendarDate getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(CalendarDate invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Money getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Money totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Money getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(Money taxAmount) {
		this.taxAmount = taxAmount;
	}

	public void setPlans(List<ExtWarrantyPlan> plans) {
		this.plans = plans;
	}

	public List<ExtWarrantyPlan> getPlans() {
		return plans;
	}

	public String getDealerNumber() {
		return dealerNumber;
	}

	public void setDealerNumber(String dealerNumber) {
		this.dealerNumber = dealerNumber;
	}
	
	
	
	
	
}
