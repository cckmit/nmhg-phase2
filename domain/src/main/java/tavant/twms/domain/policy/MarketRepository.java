package tavant.twms.domain.policy;

import tavant.twms.infra.GenericRepository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: May 27, 2009
 * Time: 3:35:45 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MarketRepository extends GenericRepository<Market,Long>{

    public List<Market> listAllMarkets();

    public List<Market> listAllMarketTypesForMarket(Long marketId);

    public List<Market> listAllApplicationsForMarketType(Long marketId);
    
    public List<Market> listMarketTypes();
    
    public Market findMarketTypeByTitle(String title);
    
    public Market findMarketApplicationByTitle(Long marketId, final String title);
    
}
