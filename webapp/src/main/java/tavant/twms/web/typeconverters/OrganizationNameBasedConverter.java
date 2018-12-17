package tavant.twms.web.typeconverters;

import tavant.twms.domain.orgmodel.Organization;
import tavant.twms.domain.orgmodel.OrganizationRepository;

/**
 * @author priyank.gupta
 *
 */
public class OrganizationNameBasedConverter extends
        ValidatableDomainObjectConverter<OrganizationRepository, Organization> {
    public OrganizationNameBasedConverter() {
        super("organizationRepository");
    }

    @Override
    public Organization fetchByName(String name) throws Exception {
        return getService().findByOrganizationName(name);
    }

    @Override
    public String getName(Organization organization) throws Exception {
        return organization.getName();
    }

}
