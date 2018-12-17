package tavant.twms.web.claim.upload;

import net.sf.dozer.util.mapping.MapperIF;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.claim.ClaimService;
import tavant.twms.infra.WebappRepositoryTestCase;
import tavant.twms.security.SecurityHelper;

public class UploadClaimTest extends WebappRepositoryTestCase {

    private ClaimService claimService;

    private MapperIF myMapper;

    private SecurityHelper securityHelper;

    public ClaimService getClaimService() {
        return claimService;
    }

    @Required
    public void setClaimService(ClaimService claimService) {
        this.claimService = claimService;
    }

    public MapperIF getMyMapper() {
        return myMapper;
    }

    public void setMyMapper(MapperIF myMapper) {
        this.myMapper = myMapper;
    }

    public SecurityHelper getSecurityHelper() {
        return securityHelper;
    }

    public void setSecurityHelper(SecurityHelper securityHelper) {
        this.securityHelper = securityHelper;
    }

}
