package tavant.twms.web.typeconverters;

import tavant.twms.domain.policy.WarrantyService;
import tavant.twms.domain.policy.WarrantyType;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: May 8, 2009
 * Time: 3:21:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class WarrantyTypeConverter extends NamedDomainObjectConverter<WarrantyService, WarrantyType> {
    public WarrantyTypeConverter() {
        super("warrantyService");
    }

    @Override
    public WarrantyType fetchByName(String name) throws Exception {
        return getService().findWarrantyTypeByType(name);
    }

    @Override
    public String getName(WarrantyType entity) throws Exception {
        return entity.getType();
    }
}
