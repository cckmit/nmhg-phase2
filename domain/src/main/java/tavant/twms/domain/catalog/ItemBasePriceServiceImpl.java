/**
 *
 */
package tavant.twms.domain.catalog;

import java.util.Collection;
import java.util.List;

import tavant.twms.infra.GenericRepository;
import tavant.twms.infra.GenericServiceImpl;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author kaustubhshobhan.b
 *
 */
public class ItemBasePriceServiceImpl extends GenericServiceImpl<ItemBasePrice, Long, Exception> implements ItemBasePriceService {

	private ItemBasePriceRepository itemBasePriceRepository;

	public ItemBasePrice findByItem(Item item) {
		return itemBasePriceRepository.findByItem(item);
	}

	public void setItemBasePriceRepository(
			ItemBasePriceRepository itemBasePriceRepository) {
		this.itemBasePriceRepository = itemBasePriceRepository;
	}

	@Override
	public GenericRepository<ItemBasePrice, Long> getRepository() {
		return itemBasePriceRepository;
	}


}
