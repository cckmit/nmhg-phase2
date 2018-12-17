package tavant.twms.domain.claim.payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

import org.springframework.util.StringUtils;

import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.TravelDetail;
import tavant.twms.domain.claim.UOM;
import tavant.twms.domain.claim.payment.rates.TravelRate;
import tavant.twms.domain.claim.payment.rates.TravelRateValues;
import tavant.twms.domain.common.GlobalConfiguration;

import com.domainlanguage.money.Money;

public class TravelByDistancePaymentComputer extends TravelPaymentComputer {

    public Money getTravelRateBasedOnSection(TravelRate travelRate, Currency preferredCurrency) {
        return getTravelRateValueForPreferredCurrency(travelRate, preferredCurrency).getDistanceRate();
    }

    public Money computeTravelBasedOnSectionAndRate(TravelDetail travelDetail, TravelRate travelRate,
                                                    Currency preferredCurrency,Claim claim) {
    	Money perKmInBaseCurrency=Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
    	TravelRateValues  travelRateValues=null;
    	if(travelRate!=null)
    	{
        travelRateValues = getTravelRateValueForPreferredCurrency(travelRate, preferredCurrency);
        perKmInBaseCurrency = travelRateValues.getDistanceRate();
    	}
        travelDetail.setDistanceCharge(perKmInBaseCurrency);
        Money travelCost = Money.valueOf(0, GlobalConfiguration.getInstance().getBaseCurrency());
        if (travelRateValues!=null&&travelRateValues.getDistanceRate() == null) {
            return travelCost;
        }
        if (travelDetail.getDistanceCharge() != null) {
            travelCost = Money.valueOf(0.0D, travelDetail.getDistanceCharge().breachEncapsulationOfCurrency());

            if (travelRate!=null&&travelRate.getValueIsDistanceFlatRate() != null && travelRate.getValueIsDistanceFlatRate() && travelDetail.getDistance() != null && travelDetail.getDistance().doubleValue() > 0) {
                travelCost = travelCost.plus(travelDetail.getDistanceCharge());
            } else {
                if (travelDetail.getDistance() != null) {
                    if (StringUtils.hasText(travelDetail.getUom())
                            && UOM.KM.getUom().equals(travelDetail.getUom())) {
                        travelCost = travelCost.plus(travelDetail.getDistanceCharge().
                                times(travelDetail.getDistance().divide(new BigDecimal(1.60934721869), 4, 2)));
                    } else {
                        travelCost = travelCost.plus(travelDetail.getDistanceCharge().
                                times(travelDetail.getDistance()));
                    }
                }
            }
        }
        return travelCost;

    }

    protected Money convertAmountIfUnitOfMeasureInKMs(Claim claim, TravelDetail travelDetail, Money travelRateAmount) {
        if (claim.getServiceInformation().getServiceDetail().getTravelDetails() != null) {
            if (StringUtils.hasText(travelDetail.getUom())
                    && UOM.KM.getUom().equals(travelDetail.getUom())
                    && travelRateAmount != null && travelRateAmount.breachEncapsulationOfAmount().doubleValue() != new BigDecimal(0).doubleValue()) {
                BigDecimal moneyValueAmount = travelRateAmount.breachEncapsulationOfAmount().divide(new BigDecimal(1.60934721869), 4, 2);
                travelRateAmount = Money.valueOf(moneyValueAmount.setScale(2, RoundingMode.CEILING), claim.getCurrencyForCalculation());
            }
        }
        return travelRateAmount;
    }

}
