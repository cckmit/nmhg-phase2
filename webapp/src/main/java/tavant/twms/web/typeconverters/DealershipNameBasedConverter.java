/**
 * 
 */
package tavant.twms.web.typeconverters;

import org.springframework.util.StringUtils;

import tavant.twms.domain.orgmodel.DealershipRepository;
import tavant.twms.domain.orgmodel.ServiceProvider;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class DealershipNameBasedConverter extends
        ValidatableDomainObjectConverter<DealershipRepository, ServiceProvider> {
    public DealershipNameBasedConverter() {
        super("dealershipRepository");
    }

    @Override
    public ServiceProvider fetchByName(String name) throws Exception {
        if (StringUtils.hasText(name)) {
        	ServiceProvider dealer = getService().findByDealerName(name);
            if (dealer != null){
                return dealer;
            } else if (org.apache.commons.lang.math.NumberUtils.isNumber(name)){
                return getService().findByDealerId(new Long(name));
            }        	
        } return null;
    }

    @Override
    public String getName(ServiceProvider dealer) throws Exception {
        return dealer.getName();
    }

}
