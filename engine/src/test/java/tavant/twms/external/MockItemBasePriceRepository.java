package tavant.twms.external;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;

import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.catalog.ItemBasePrice;
import tavant.twms.domain.catalog.ItemBasePriceRepository;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.claim.payment.rates.PriceFetchData;
import tavant.twms.infra.CriteriaHelper;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

public class MockItemBasePriceRepository implements ItemBasePriceRepository {

	public ItemBasePrice findByItem(Item arg0) {
		throw new UnsupportedOperationException();

	}

	public void findByItem(Claim arg0, List<PriceFetchData> arg1) {
		throw new UnsupportedOperationException();

	}

	public void delete(ItemBasePrice arg0) {
		throw new UnsupportedOperationException();

	}

	public List<ItemBasePrice> findAll() {
		throw new UnsupportedOperationException();

	}

	public PageResult<ItemBasePrice> findAll(PageSpecification arg0) {
		throw new UnsupportedOperationException();

	}

	public ItemBasePrice findById(Long arg0) {
		throw new UnsupportedOperationException();

	}

	public List<ItemBasePrice> findByIds(Collection<Long> arg0) {
		throw new UnsupportedOperationException();

	}

	public List<ItemBasePrice> findByIds(String arg0, Collection<Long> arg1) {
		throw new UnsupportedOperationException();

	}

	public List<ItemBasePrice> findEntitiesThatMatchPropertyValue(String arg0,
			ItemBasePrice arg1) {
		throw new UnsupportedOperationException();

	}

	public List<ItemBasePrice> findEntitiesThatMatchPropertyValues(
			Set<String> arg0, ItemBasePrice arg1) {
		throw new UnsupportedOperationException();

	}

	public PageResult<ItemBasePrice> findPage(String arg0, ListCriteria arg1) {
		throw new UnsupportedOperationException();

	}

	public void save(ItemBasePrice arg0) {
		throw new UnsupportedOperationException();

	}

	public void update(ItemBasePrice arg0) {
		throw new UnsupportedOperationException();

	}

	public void deleteAll(List<ItemBasePrice> entitiesToDelete) {
		// TODO Auto-generated method stub
		
	}

	public PageResult<ItemBasePrice> fetchPage(Criteria queryCriteria,
			ListCriteria listCriteria, List<String> alreadyAddedAliases) {
		// TODO Auto-generated method stub
		return null;
	}

	public CriteriaHelper getCriteriaHelper() {
		// TODO Auto-generated method stub
		return null;
	}

	public void saveAll(List<ItemBasePrice> entitiesToSave) {
		// TODO Auto-generated method stub
		
	}

	public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
		// TODO Auto-generated method stub
		
	}

	public void updateAll(List<ItemBasePrice> entitiesToUpdate) {
		// TODO Auto-generated method stub
		
	}

}
