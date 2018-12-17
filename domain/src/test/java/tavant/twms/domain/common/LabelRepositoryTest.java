package tavant.twms.domain.common;

import tavant.twms.infra.DomainRepositoryTestCase;

public class LabelRepositoryTest extends DomainRepositoryTestCase {

	LabelRepository labelRepository;

	public void setLabelRepository(LabelRepository labelRepository) {
		this.labelRepository = labelRepository;
	}

	public void testCreateAndFindOps() {
		assertEquals(3, labelRepository.findAll().size());
		Label label = new Label("name");
		labelRepository.save(label);
		
		flush();
		assertTrue(labelRepository.findAll().contains(label));
		assertEquals(4, labelRepository.findAll().size());
		assertEquals(label, labelRepository.findById("name"));
		
		assertEquals(3, labelRepository.findLabelsWithNameLike("L", 0, 10).size());
		assertNotNull(labelRepository.findLabelWithName("Label1"));
	}
}
