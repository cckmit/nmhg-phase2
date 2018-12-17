package tavant.twms.integration.layer.transformer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;

import tavant.extwarranty.ExtendedWarrantyPriceCheckResponseDocumentDTO;
import tavant.extwarranty.PriceCheckResponseLineItemTypeDTO;
import tavant.twms.domain.policy.ExtWarrantyPlan;
import tavant.twms.external.ExtWarrantyPriceCheckResponse;
import tavant.twms.external.ExtWarrantyRequest;
import tavant.twms.integration.layer.util.CurrencyConvertor;

import com.domainlanguage.money.Money;

public class ExtWarrantyPriceCheckResponseTransformer {

	private CurrencyConvertor currencyConvertor;

	public ExtWarrantyPriceCheckResponse transform(String xml) {
		ExtendedWarrantyPriceCheckResponseDocumentDTO doc = null;
		try {
			doc = ExtendedWarrantyPriceCheckResponseDocumentDTO.Factory
					.parse(xml);
		} catch (XmlException e) {
			throw new RuntimeException(e);
		}
		PriceCheckResponseLineItemTypeDTO[] lineItemsType = doc
				.getExtendedWarrantyPriceCheckResponse().getPlans()
				.getLineItemArray();
		ExtWarrantyPriceCheckResponse extWarrantyPriceCheckResponse = new ExtWarrantyPriceCheckResponse();
		List<ExtWarrantyPlan> planList = new ArrayList<ExtWarrantyPlan>();
		for (PriceCheckResponseLineItemTypeDTO priceCheckResponseLineItemTypeDTO : lineItemsType) {
			ExtWarrantyPlan extWarrantyPlan = new ExtWarrantyPlan();
			extWarrantyPlan.setPlanCode(priceCheckResponseLineItemTypeDTO
					.getPlanCode());
			extWarrantyPlan.setPlanItemNumber(priceCheckResponseLineItemTypeDTO
					.getPlanItemNumber());

			String currency = priceCheckResponseLineItemTypeDTO.getAmount()
					.xmlText();
			BigDecimal amt = new BigDecimal(0.0);
			try {
				if (!priceCheckResponseLineItemTypeDTO.getAmount().isNil()) {
					amt = priceCheckResponseLineItemTypeDTO.getAmount()
							.getBigDecimalValue();
				}
			} catch (XmlValueOutOfRangeException xmle) {
				amt = new BigDecimal(0.0);
			}

			double amtDoubleVal = amt.floatValue();
			String currencyCode = getCurrency(currency);
			Currency fromCurrency = Currency.getInstance(currencyCode);
			Money amount = Money.valueOf(amtDoubleVal, fromCurrency);
			Money amountInUSD = currencyConvertor.convertToBaseCurrency(
					fromCurrency, amount);
			extWarrantyPlan.setAmount(amountInUSD);
			planList.add(extWarrantyPlan);
		}
		extWarrantyPriceCheckResponse.setDealerNo(doc
				.getExtendedWarrantyPriceCheckResponse().getDealerNo());
		extWarrantyPriceCheckResponse.setPlans(planList);
		return extWarrantyPriceCheckResponse;
	}
	
	
	public ExtWarrantyPriceCheckResponse transformMock(ExtWarrantyRequest extWarrantyRequest) {
		ExtWarrantyPriceCheckResponse extWarrantyPriceCheckResponse = new ExtWarrantyPriceCheckResponse();
		List<ExtWarrantyPlan> planList = new ArrayList<ExtWarrantyPlan>();
		List<ExtWarrantyPlan> reqPlans = extWarrantyRequest.getPlans();
		for (ExtWarrantyPlan plan : reqPlans) {
			ExtWarrantyPlan extWarrantyPlan = new ExtWarrantyPlan();
			extWarrantyPlan.setPlanCode(plan.getPlanCode());
			extWarrantyPlan.setPlanItemNumber(plan.getPlanItemNumber());
			extWarrantyPlan.setAmount(Money.dollars(55));
			planList.add(extWarrantyPlan);
		}
		extWarrantyPriceCheckResponse.setDealerNo(extWarrantyRequest.getDealerNo());
		extWarrantyPriceCheckResponse.setPlans(planList);
		return extWarrantyPriceCheckResponse;
	}

	private String getCurrency(String currency) {
		String currencyCode = null;

		if (currency != null && StringUtils.isNotEmpty(currency)) {
			currencyCode = StringUtils.substringBetween(currency, "=\"", "\"");
		}
		if (currencyCode == null || StringUtils.isEmpty(currencyCode)) {
			currencyCode = "USD";
		}
		return currencyCode;
	}

	public void setCurrencyConvertor(CurrencyConvertor currencyConvertor) {
		this.currencyConvertor = currencyConvertor;
	}
}
