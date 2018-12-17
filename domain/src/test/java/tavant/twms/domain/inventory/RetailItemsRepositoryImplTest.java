package tavant.twms.domain.inventory;

import java.util.List;

import tavant.twms.domain.common.Label;
import tavant.twms.domain.common.LabelService;
import tavant.twms.infra.DomainRepositoryTestCase;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageSpecification;

public class RetailItemsRepositoryImplTest extends DomainRepositoryTestCase {
    RetailItemsRepository retailItemsRepository;

    LabelService labelService;

    public void setLabelService(LabelService labelService) {
        this.labelService = labelService;
    }

    public void setRetailItemsRepository(RetailItemsRepository retailItemsRepository) {
        this.retailItemsRepository = retailItemsRepository;
    }

    public void testFindAll() {
        assertEquals(true, this.retailItemsRepository.findAll().size()>0);
    }

    public void testFindPage() {
        ListCriteria listCriteria = new ListCriteria();
        PageSpecification pageSpecification = new PageSpecification(0, 10);
        listCriteria.setPageSpecification(pageSpecification);
        assertEquals(true, this.retailItemsRepository.findPage(listCriteria).getResult().size()>0);
        pageSpecification.setPageNumber(1);
        assertEquals(0, this.retailItemsRepository.findPage(listCriteria).getResult().size());
    }

    public void testFindRetailItemsForLabel() {
        Label label = this.labelService.findById("Label1");
        List<InventoryItem> invItems = this.retailItemsRepository.findRetailItemsForLabel(label);
        assertEquals(1, invItems.size());
        assertEquals(new Long(1), invItems.get(0).getId());
    }
}
