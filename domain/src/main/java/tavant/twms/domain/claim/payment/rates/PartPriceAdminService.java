package tavant.twms.domain.claim.payment.rates;

import org.springframework.transaction.annotation.Transactional;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.infra.GenericService;

public interface PartPriceAdminService extends GenericService<PartPrices, Long, Exception>{
    
    @Transactional(readOnly=false)
    public void save(PartPrices entity);
    
    @Transactional(readOnly=false)
    public void update(PartPrices entity);
    
    @Transactional(readOnly=false)
    public void delete(PartPrices partPrices);
    
	public PartPrices findPartPrices(Long id);
	
	boolean isUnique(PartPrices partPrices);
	
	public PartPrices findPartPricesByPartNumber(BrandItem partNumber);

	public PartPrices findPartPricesByItemNumber(String nmhgPartNumber);
}
