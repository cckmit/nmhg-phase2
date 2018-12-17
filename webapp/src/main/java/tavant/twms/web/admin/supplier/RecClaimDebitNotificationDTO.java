package tavant.twms.web.admin.supplier;

import java.util.Date;

public class RecClaimDebitNotificationDTO {

	public RecClaimDebitNotificationDTO(String claimNumber,
			String debitMemoNumber, Date debitMemoDate) {
		this.claimNumber = claimNumber;
		this.debitMemoNumber = debitMemoNumber;
		this.debitMemoDate = debitMemoDate;
	}

	private String claimNumber;

	private String debitMemoNumber;

	private Date debitMemoDate;

	public String getClaimNumber() {
		return claimNumber;
	}

	public void setClaimNumber(String claimNumber) {
		this.claimNumber = claimNumber;
	}

	public String getDebitMemoNumber() {
		return debitMemoNumber;
	}

	public void setDebitMemoNumber(String debitMemoNumber) {
		this.debitMemoNumber = debitMemoNumber;
	}

	public Date getDebitMemoDate() {
		return debitMemoDate;
	}

	public void setDebitMemoDate(Date debitMemoDate) {
		this.debitMemoDate = debitMemoDate;
	}

}
