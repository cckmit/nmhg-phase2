package tavant.twms.integration.layer.component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.XmlException;

import com.domainlanguage.money.Money;

import tavant.extwarranty.InvoiceTypeDTO;
import tavant.extwarranty.PlanTypeDTO;
import tavant.twms.domain.policy.DebitMemo;
import tavant.twms.domain.policy.ExtWarrantyPlan;
import tavant.twms.integration.layer.IntegrationRepositoryTestCase;
import tavant.twms.integration.layer.transformer.ExtWarrantyDebitNotificationTransformer;
import tavant.twms.integration.layer.util.CalendarUtil;

public class ExtWarrantyDebitNotificationTest extends IntegrationRepositoryTestCase {

	private ExtWarrantyDebitNotificationTransformer extWarrantyDebitNotificationTransformer;

	public void testExtWarrantyPriceCheck() {

		InputStream stream = ExtWarrantyDebitNotificationTest.class
				.getResourceAsStream("/ExtWarrantyDebitNotification/Ext-Warranty-Debit-Notification.xml");
		tavant.extwarranty.InvoiceTypeDTO invoice  = null;
		try {
			String str = IOUtils.toString(stream);
			invoice = extWarrantyDebitNotificationTransformer.transform(str);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlException e) {
			e.printStackTrace();
		}
		transform(invoice);
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
    
	public void setExtWarrantyDebitNotificationTransformer(
			ExtWarrantyDebitNotificationTransformer extWarrantyDebitNotificationTransformer) {
		this.extWarrantyDebitNotificationTransformer = extWarrantyDebitNotificationTransformer;
	}



}

