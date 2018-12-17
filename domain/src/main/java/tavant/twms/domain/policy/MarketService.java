package tavant.twms.domain.policy;

import tavant.twms.infra.GenericService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: May 27, 2009
 * Time: 3:37:26 PM
 * To change this template use File | Settings | File Templates.
 */
@Transactional(readOnly = true)
public interface MarketService extends GenericService<Market,Long,Exception>{

    public List<Market> listAllMarkets();

    public List<Market> listAllMarketTypesForMarket(Long marketId);

    public List<Market> listAllApplicationsForMarketType(Long marketId);
    
    public List<Market> listMarketTypes();
    
    public Market findMarketTypeByTitle(String title);
    
    public Market findMarketApplicationByTitle(Long marketId, final String title);
}
