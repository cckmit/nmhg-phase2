package tavant.twms.domain.common;

import tavant.twms.infra.DomainRepositoryTestCase;

public class PurposeRepositoryTest extends DomainRepositoryTestCase {
    
    PurposeRepository purposeRepository;

    public void testFindByName() {
        assertNotNull(purposeRepository.findPurposeByName("Item Pricing"));
    }

    public void setPurposeRepository(PurposeRepository purposeRepository) {
        this.purposeRepository = purposeRepository;
    }
}
