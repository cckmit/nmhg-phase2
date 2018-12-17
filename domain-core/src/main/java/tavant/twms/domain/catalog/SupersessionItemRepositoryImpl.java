package tavant.twms.domain.catalog;

import org.hibernate.Query;

import tavant.twms.infra.GenericRepositoryImpl;

public class SupersessionItemRepositoryImpl extends GenericRepositoryImpl<SupersessionItem, Long> implements SupersessionItemRepository{

	public String findSuppreSupersessionItem(Item item) {
		 	Long oldItemId =item.getId();
		   Query query =  getSession().createSQLQuery("select item_number from item where id in (select new_item_id from supersession_item where old_item_id=:oldItemId)"); 
		   query.setParameter("oldItemId", oldItemId);
		   return (query.list().size() == 0 ? null : query.list().get(0).toString());
	}
}

