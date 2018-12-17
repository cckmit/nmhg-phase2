package tavant.twms.integration.layer.component;

import static tavant.twms.integration.layer.util.ExceptionUtil.getStackTrace;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.domainlanguage.money.Money;

import tavant.extwarranty.InvoiceTypeDTO;
import tavant.extwarranty.PlanTypeDTO;
import tavant.twms.domain.policy.DebitMemo;
import tavant.twms.domain.policy.ExtWarrantyPlan;
import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.integration.layer.util.CalendarUtil;

public class ProcessExtWarrantyDebitNotification {

	   private static Logger logger = Logger.getLogger(ProcessExtWarrantyDebitNotification.class
	            .getName());


	    private TransactionTemplate transactionTemplate;
	    
	    private WarrantyService warrantyService;


	    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
	        this.transactionTemplate = transactionTemplate;
	    }

	    public List<SyncResponse> sync(final List<InvoiceTypeDTO> invoiceDtoList) {
	        if (logger.isDebugEnabled()) {
	            logger.debug("Received " + invoiceDtoList.size()
	                    + " Extended Warranty Debit Notification(s).");
	        }

	        List<SyncResponse> responses = new ArrayList<SyncResponse>();

	        for (final InvoiceTypeDTO invoiceTypeDTO : invoiceDtoList) {
	            SyncResponse response = new SyncResponse();
	            if(invoiceTypeDTO.getHeader().getDocumentIds() == null){
	            	throw new RuntimeException(" There is no debit memo associated with this invoice. ");
	            }
	            
	            response.setBusinessId(String.valueOf(invoiceTypeDTO.getHeader().getDocumentIds().getDocumentId().getId()));
	            response.setUniqueIdName("SerialNumber");
	            response.setUniqueIdValue(invoiceTypeDTO.getHeader().getUserArea().getSerialNumber());

	            try {
	                transactionTemplate
	                        .execute(new TransactionCallbackWithoutResult() {
	                            @Override
	                            protected void doInTransactionWithoutResult(
	                                    TransactionStatus ts) {
	                            	DebitMemo debitMemo = transform(invoiceTypeDTO);
	                            	warrantyService.notifyDebitForExtWarranty(debitMemo);
	                            }
	                        });
	                response.setSuccessful(true);
	            } catch (RuntimeException e) {
	                logger.error(" Failed to sync Extended Warranty Debit memo number :"
	                        + invoiceTypeDTO.getHeader().getDocumentIds().getDocumentId().getId(), e);
	                response.setSuccessful(false);
	                response.setException(new StringBuilder().append(
	                        " Error syncing Extended Warranty Debit memo with id ").append(
	                        		invoiceTypeDTO.getHeader().getDocumentIds().getDocumentId().getId()).append("\n").toString());
	                response.setErrorCode(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
	                response.setErrorType(SyncResponse.ERROR_CODE_BUSINESS_PROCESS_ERROR);
	            } finally {
	                responses.add(response);
	            }
	        }
	        return responses;
	    }

	    private DebitMemo transform(final InvoiceTypeDTO dto) {
	    	DebitMemo debitMemo = new DebitMemo();
	        debitMemo.setDebitMemoNumber(dto.getHeader().getUserArea().getDebitMemoNumber());
	        debitMemo.setPurchaseOrderNumber(dto.getHeader().getUserArea().getPurchaseOrderNumber());
	        debitMemo.setSerialNumber(dto.getHeader().getUserArea().getSerialNumber());
	        debitMemo.setDealerNumber(dto.getHeader().getDealerNumber());
	        debitMemo.setInvoiceDate(CalendarUtil.convertToCalendarDate(dto.getDocumentDateTime()));
	        String currencyCode = dto.getLine().getTotalAmount().getCurrency();
	        Currency currency = Currency.getInstance(currencyCode);
	        
	        PlanTypeDTO[] planList = dto.getHeader().getUserArea().getPlansList().getPlanArray();
	        List<ExtWarrantyPlan> plans = new ArrayList<ExtWarrantyPlan>();
	        
	        
	        for (PlanTypeDTO planTypeDTO : planList) {
	        	ExtWarrantyPlan plan = new ExtWarrantyPlan();
	        	
				plan.setPlanCode(planTypeDTO.getPlanCode());
				plan.setAmount(Money.valueOf(planTypeDTO.getAmount(),currency));
				plan.setTaxAmount(Money.valueOf(planTypeDTO.getTaxAmount(), currency));
				plans.add(plan);
			}
	        
	        debitMemo.setPlans(plans);
	        return debitMemo;
	    }

		public void setWarrantyService(WarrantyService warrantyService) {
			this.warrantyService = warrantyService;
		}


}
