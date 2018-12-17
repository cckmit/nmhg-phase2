/**
 * 
 */
package tavant.twms.web.typeconverters;

import tavant.twms.domain.partreturn.Location;
import tavant.twms.domain.partreturn.LocationRepository;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class ReturnLocationConverter extends
        ValidatableDomainObjectConverter<LocationRepository, Location> {

    public ReturnLocationConverter() {
        super("locationRepository");

    }

    @Override
    public Location fetchByName(String name) throws Exception {
        return getService().findByLocationCode(name);
    }

    @Override
    public String getName(Location entity) throws Exception {
        return entity.getCode();
    }

}
