package tavant.twms.domain.policy;

import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.GenericRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: May 27, 2009
 * Time: 3:38:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarketServiceImpl extends GenericServiceImpl<Market,Long,Exception> implements MarketService  {

    private MarketRepository marketRepository;

    public MarketRepository getMarketRepository() {
        return marketRepository;
    }

    public void setMarketRepository(MarketRepository marketRepository) {
        this.marketRepository = marketRepository;
    }

    @SuppressWarnings("unchecked")
	@Override
	public GenericRepository getRepository() {
		return marketRepository;
	}

    public List<Market> listAllMarkets(){
        return marketRepository.listAllMarkets();
    }

    public List<Market> listAllMarketTypesForMarket(Long marketId){
        return marketRepository.listAllMarketTypesForMarket(marketId);
    }

    public List<Market> listAllApplicationsForMarketType(Long marketId){
        return marketRepository.listAllApplicationsForMarketType(marketId);
    }
    
    public List<Market> listMarketTypes(){
    	return marketRepository.listMarketTypes();
    }

	public Market findMarketTypeByTitle(String title) {
		return marketRepository.findMarketTypeByTitle(title);
	}
	
	public Market findMarketApplicationByTitle(Long marketId, final String title){
		return marketRepository.findMarketApplicationByTitle(marketId, title);
	}
    
}
