package tavant.twms.domain.claim.payment.rates;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;

public class PartPriceAdminServiceImpl extends
		GenericServiceImpl<PartPrices, Long, Exception> implements
		PartPriceAdminService {

	private PartPricesRepository partPricesRepository;

	public PartPricesRepository getPartPricesRepository() {
		return partPricesRepository;
	}

	public void setPartPricesRepository(
			PartPricesRepository partPricesRepository) {
		this.partPricesRepository = partPricesRepository;
	}

	@Override
	public void save(PartPrices entity) {
		this.partPricesRepository.save(entity);
	}

	@Override
	public GenericRepository<PartPrices, Long> getRepository() {
		return partPricesRepository;
	}

	@Override
	public void update(PartPrices entity) {
		this.partPricesRepository.update(entity);
	}

	@Override
	public void delete(PartPrices partPrices){
		partPrices.getD().setActive(Boolean.FALSE);
		super.update(partPrices);
	}

	public PartPrices findPartPrices(Long id) {
		return partPricesRepository.findById(id);
	}

	public boolean isUnique(PartPrices partPrices) {
		 boolean isUnique = true;
		 PartPrices example =null;
		 if(partPrices.getId()!=null){
			 example=findById(partPrices.getId());
		 }
	     if (same(example,partPrices)) {
	            isUnique = false;
	        }
	        return isUnique;
	}
	 private boolean same(PartPrices source, PartPrices target) {
		    if(source==null || target==null){
		    	return true;
		    }
	        return source.getId() != null 
	                && target.getId() != null 
	                && source.getId().compareTo(target.getId()) == 0
	                &&(StringUtils.isNotEmpty(source.getComments())&&StringUtils.isNotEmpty(target.getComments())
	                &&source.getComments().equals(target.getComments())
	                && isPartPricesValuesSame(source.getRates(),target.getRates()));
	    }
	 
	private boolean isPartPricesValuesSame(List<PartPrice> sourcePartPriceList ,List<PartPrice> targetPartPriceList ){
		if (sourcePartPriceList!= null && targetPartPriceList!=null && sourcePartPriceList.size() == targetPartPriceList.size()) {
            boolean indicator = false;
            for(PartPrice targetPartPrice:targetPartPriceList){
            	  List<PartPriceValues> targetPartPriceValuesList=targetPartPrice.getPartPriceValues();
            	   for(PartPrice sourcePartPrice:sourcePartPriceList){
            		   List<PartPriceValues> sourcePartPriceValuesList=sourcePartPrice.getPartPriceValues();
            for (PartPriceValues targetPartPriceValues :targetPartPriceValuesList) {
                for (PartPriceValues sourcePartPriceValues :sourcePartPriceValuesList) {
                    if (sourcePartPriceValues.getDealerNetPrice().equals(targetPartPriceValues.getDealerNetPrice())
                            && sourcePartPriceValues.getStandardCostPrice().equals(targetPartPriceValues.getStandardCostPrice())&&
                            		sourcePartPriceValues.getPlantCostPrice().equals(targetPartPriceValues.getPlantCostPrice())) {
                        indicator =  true;
                        continue;

                    }
                    else{
                        indicator = false;
                        break;
                    } 
                }

            }
        if (indicator){
            return true;
          }
		 }
            }
		}
        else{
            return false;
        }
        return false; 
		
	}
	
	public PartPrices findPartPricesByPartNumber(BrandItem partNumber){
		return partPricesRepository.findPartPricesByPartNumber(partNumber);
	}

	public PartPrices findPartPricesByItemNumber(String nmhgPartNumber) {
		return partPricesRepository.findPartPricesByItemNumber(nmhgPartNumber);
	}

}
