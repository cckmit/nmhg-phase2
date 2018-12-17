package tavant.twms.web.typeconverters;

import tavant.twms.domain.orgmodel.ServiceProvider;
import tavant.twms.domain.orgmodel.ServiceProviderRepository;

/**
 * @author Priyank.Gupta
 *
 */
public class ServiceProviderNameBasedConverter extends
        ValidatableDomainObjectConverter<ServiceProviderRepository, ServiceProvider> {
    public ServiceProviderNameBasedConverter() {
        super("serviceProviderRepository");
    }

    /* (non-Javadoc)
     * @see tavant.twms.web.typeconverters.NamedDomainObjectConverter#fetchByName(java.lang.String)
     */
    @Override
    public ServiceProvider fetchByName(String name) throws Exception 
    {
    	ServiceProvider serviceProviderName = getService().findServiceProviderByName(name);  
    	if (serviceProviderName != null) {
			return serviceProviderName;
		} else {
			return getService().findServiceProviderByNumber(name);
		}
    		
    }

    /* (non-Javadoc)
     * @see tavant.twms.web.typeconverters.NamedDomainObjectConverter#getName(java.lang.Object)
     */
    @Override
    public String getName(ServiceProvider serviceProvider) throws Exception 
    {
        return serviceProvider.getName();
    }
}
