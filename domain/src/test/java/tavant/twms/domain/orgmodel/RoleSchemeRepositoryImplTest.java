package tavant.twms.domain.orgmodel;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import tavant.twms.domain.common.Purpose;
import tavant.twms.domain.common.PurposeRepository;
import tavant.twms.infra.DomainRepositoryTestCase;

public class RoleSchemeRepositoryImplTest extends DomainRepositoryTestCase {
	RoleSchemeRepository roleSchemeRepository;

	RoleGroupRepository roleGroupRepository;

	PurposeRepository purposeRepository;

	public void testCreate() {
		RoleScheme roleScheme = new RoleScheme("DSM Assignment");
		roleSchemeRepository.save(roleScheme);
		assertNotNull(roleScheme.getId());
	}

	public void testFindByPurpose() {
		Purpose purpose = new Purpose();
		purpose.setName("name");
		purposeRepository.save(purpose);
		Purpose aPurpose = purposeRepository.findById(purpose.getId());
		RoleScheme roleScheme = new RoleScheme("DSM Assignment");
		Set<Purpose> purposes = new HashSet<Purpose>();
		purposes.add(purpose);
		roleScheme.setPurposes(purposes);
		roleSchemeRepository.save(roleScheme);
		RoleScheme scheme = roleSchemeRepository.findById(roleScheme.getId());
		RoleScheme resultFound = roleSchemeRepository
				.findSchemeForPurpose(aPurpose);
		assertNotNull(resultFound);
		assertEquals(scheme.getId(), resultFound.getId());
	}

	public void testSave_BU() {
		RoleScheme roleScheme = new RoleScheme("DSM Assignment");
		roleSchemeRepository.save(roleScheme);
		assertNotNull(roleScheme.getId());
		assertEquals("IR", roleScheme.getBusinessUnitInfo().getName());
	}

	public RoleGroupRepository getRoleGroupRepository() {
		return roleGroupRepository;
	}

	@Required
	public void setRoleGroupRepository(RoleGroupRepository roleGroupRepository) {
		this.roleGroupRepository = roleGroupRepository;
	}

	public RoleSchemeRepository getRoleSchemeRepository() {
		return roleSchemeRepository;
	}

	@Required
	public void setRoleSchemeRepository(
			RoleSchemeRepository roleSchemeRepository) {
		this.roleSchemeRepository = roleSchemeRepository;
	}

	public PurposeRepository getPurposeRepository() {
		return purposeRepository;
	}

	@Required
	public void setPurposeRepository(PurposeRepository purposeRepository) {
		this.purposeRepository = purposeRepository;
	}

}
