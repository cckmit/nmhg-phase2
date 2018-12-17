package tavant.twms.domain.claim.payment.rates;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.infra.GenericRepository;

public interface PartPricesRepository extends GenericRepository<PartPrices, Long> {

	public PartPrices findPartPricesByPartNumber(BrandItem partNumber);

	public PartPrices findPartPricesByItemNumber(String nmhgPartNumber);
}
