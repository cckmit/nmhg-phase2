package tavant.twms.domain.catalog;

import tavant.twms.infra.GenericRepository;

public interface SupersessionItemRepository extends GenericRepository<SupersessionItem, Long>{
	public String findSuppreSupersessionItem(Item item);
}
