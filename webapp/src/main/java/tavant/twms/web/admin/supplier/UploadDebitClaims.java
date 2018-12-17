package tavant.twms.web.admin.supplier;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.RecoveryClaim;
import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.claim.payment.CreditMemo;
import tavant.twms.external.PaymentAsyncService;
import tavant.twms.integration.layer.util.CalendarUtil;
import tavant.twms.web.i18n.I18nActionSupport;

import com.domainlanguage.money.Money;

@SuppressWarnings("serial")
public class UploadDebitClaims extends I18nActionSupport{
	
	private static final Logger logger = Logger.getLogger(UploadDebitClaims.class);
	private File file;
	private RecoveryClaimService recoveryClaimService;
	private PaymentAsyncService paymentAsyncService;
	private List<RecClaimDebitNotificationDTO> successNotifications = new ArrayList<RecClaimDebitNotificationDTO>();
	private List<RecClaimDebitNotificationDTO> failureNotifications = new ArrayList<RecClaimDebitNotificationDTO>();

	
	public void validate() {
		try {
			if (this.file == null) {
				throw new IOException();
			}
			InputStream myxls = new FileInputStream(this.file);
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheet("DEBIT_NOTIFICATIONS");
			if (sheet == null) {
				this.file = null;
				throw new IOException();
			}
		} catch (IOException e) {
			logger.error("Invalid input. Please download the template and proceed.",e);
			addActionError("error.campaign.invalidInput");
		}
	}
	
	public String processDebitNotifications(){
		List<CreditMemo> memosToBeDebitted = parseContent();
		if(memosToBeDebitted == null){
			return INPUT;
		}
		for (CreditMemo memo : memosToBeDebitted) {
			syncPaymentMadeForSRClaims(memo);
		}
		
		if(failureNotifications.size() > 0){
			for(RecClaimDebitNotificationDTO failureNotification : failureNotifications){
				   addActionError("error.uploadDebitClaims.notNotified",failureNotification.getClaimNumber());
				}
		}else{
			addActionMessage("All the claims have been successfully notified");
		}
		return SUCCESS;
	}
	
	private void syncPaymentMadeForSRClaims(CreditMemo creditMemo) {
        this.paymentAsyncService.syncCreditMemo(creditMemo);
    }
	
	private List<CreditMemo> parseContent() {
		List<CreditMemo> debitClaims = new ArrayList<CreditMemo>();
		if (this.file == null) {
			addActionError("No file to parse");
			return null;
		}
		try {
			InputStream myxls = new FileInputStream(this.file);
			HSSFWorkbook wb = new HSSFWorkbook(myxls);
			HSSFSheet sheet = wb.getSheet("DEBIT_NOTIFICATIONS");
			int startRow = sheet.getFirstRowNum();
			int numRowsInSheet = sheet.getLastRowNum() + 1;
			for (int i = startRow + 1; i < numRowsInSheet; i++) {
				HSSFRow row = sheet.getRow(i);
				if (row != null) {
					HSSFCell claimCell = row.getCell(row.getFirstCellNum());
					if (claimCell != null) {
						String claimNumber = convertingCellValueToString(claimCell);
						
						if (StringUtils.hasText(claimNumber)) {
							parseRow(debitClaims, row, claimNumber);
						} else {
							if (logger.isDebugEnabled()) {
								logger.debug("finished parsing data...");
							}
							break;
						}
					}
				}
			}
			this.file = null;
		} catch (Exception e) {
			logger.error("Exception in parsing excel", e);
			addActionError("There was an error parsing files.");
			return null;
		}
		return debitClaims;
	}
	
	private String convertingCellValueToString(HSSFCell cell) {
		String value = "";
		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			value = cell.getStringCellValue();
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			value = String.valueOf((long)cell
					.getNumericCellValue());
		}
		return value;
	}
	
	private void parseRow(List<CreditMemo> debitClaims, HSSFRow row,String claimNumber ) {
		//String claimNumber = row.getCell(row.getFirstCellNum()).getStringCellValue();
		short cell1 = 1;
		short cell2 = 2;
		String debitMemoNumber = convertingCellValueToString(row.getCell(cell1));
		HSSFCell dateCell = row.getCell(cell2);
		Date debitMemoDate = null;
		if(dateCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			try {
				debitMemoDate = new SimpleDateFormat("MM/dd/yy").parse(dateCell.getStringCellValue());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if(dateCell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC){
			debitMemoDate = dateCell.getDateCellValue();
		}
		
		RecClaimDebitNotificationDTO newDto = new RecClaimDebitNotificationDTO(claimNumber, debitMemoNumber, debitMemoDate);
		CreditMemo creditMemo = validateClaimNumber(newDto);
		if(creditMemo != null){
			successNotifications.add(newDto);
			debitClaims.add(creditMemo);
		}else{
			failureNotifications.add(newDto);
		}
	}

	private CreditMemo validateClaimNumber(RecClaimDebitNotificationDTO debitNotificationDTO) {
		RecoveryClaim recoveryClaim = recoveryClaimService
				.findActiveRecoveryClaimForClaimForOfflineDebit(debitNotificationDTO.getClaimNumber());
		if(recoveryClaim == null){
			return null;
		}
		if (Boolean.TRUE.equals(recoveryClaim.getContract()
				.getOfflineDebitEnabled())) {
				return populateCreditMemo(recoveryClaim,debitNotificationDTO);
		} else {
			return null;
		}
	}


	private CreditMemo populateCreditMemo(RecoveryClaim recoveryClaim, RecClaimDebitNotificationDTO debitNotificationDTO) {
        CreditMemo creditMemo = new CreditMemo();
        creditMemo.setRecoveryClaim(recoveryClaim);
        creditMemo.setCreditMemoDate(CalendarUtil.convertToCalendarDate(debitNotificationDTO.getDebitMemoDate()));
        creditMemo.setClaimNumber(debitNotificationDTO.getClaimNumber());
        creditMemo.setCreditMemoNumber(debitNotificationDTO.getDebitMemoNumber());
        Money totalRecoveredCost = recoveryClaim.getTotalRecoveredCost();
		creditMemo.setPaidAmount(totalRecoveredCost);
		if(totalRecoveredCost.isNegative()){
        	creditMemo.setCrDrFlag("CR");
        }else{
        	creditMemo.setCrDrFlag("DR");
        }		
		creditMemo.setTaxAmount(Money.valueOf(0, totalRecoveredCost.breachEncapsulationOfCurrency()));
		return creditMemo;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	public void setRecoveryClaimService(RecoveryClaimService recoveryClaimService) {
		this.recoveryClaimService = recoveryClaimService;
	}

	public List<RecClaimDebitNotificationDTO> getFailureNotifications() {
		return failureNotifications;
	}

	public void setFailureNotifications(
			List<RecClaimDebitNotificationDTO> failureNotifications) {
		this.failureNotifications = failureNotifications;
	}

	public List<RecClaimDebitNotificationDTO> getSuccessNotifications() {
		return successNotifications;
	}

	public void setSuccessNotifications(
			List<RecClaimDebitNotificationDTO> successNotifications) {
		this.successNotifications = successNotifications;
	}

	public void setPaymentAsyncService(PaymentAsyncService paymentAsyncService) {
		this.paymentAsyncService = paymentAsyncService;
	}

}
