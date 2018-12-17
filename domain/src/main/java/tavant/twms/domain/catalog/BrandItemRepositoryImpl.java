package tavant.twms.domain.catalog;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.partreturn.PartReturnDefinition;
import tavant.twms.infra.GenericRepositoryImpl;

public class BrandItemRepositoryImpl extends GenericRepositoryImpl<BrandItem, Long> implements BrandItemRepository{

	  @SuppressWarnings("unchecked")
		public BrandItem findBrandByItemIdAndBrand(final Item item,final String brand) {
			return (BrandItem) getHibernateTemplate().execute(new HibernateCallback() {
				public Object doInHibernate(Session session)
						throws HibernateException, SQLException {
						return session.createQuery(
								" select b from BrandItem b where b.item = :id " +
								" and b.brand = :brand"
								)
								.setParameter("id", item).setParameter("brand",brand).uniqueResult();
					}
			});
		}
	  
	    @SuppressWarnings("unchecked")
	    public List<BrandItem> fetchItemBrands(final Item item) {
	        return (List<BrandItem>) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                return session.createQuery(
	                        "select bi from BrandItem bi where bi.item =:item ")
	                        .setParameter("item",item).list();
	            }

	        });
	    }

		@SuppressWarnings("unchecked")
		public BrandItem findBrandItemByName(final String number) {
			return (BrandItem) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                return session.createQuery(
	                        "from BrandItem bi where bi.itemNumber =:number ")
	                        .setParameter("number",number).uniqueResult();
	            }

	        });
		}

		@SuppressWarnings("unchecked")
		public BrandItem findUniqueBrandItemByNMHGItemNumber(final String itemNumber) {
			return (BrandItem) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                return session.createQuery(
	                        "from BrandItem bi where bi.itemNumber =:itemNumber ")
	                        .setParameter("itemNumber",itemNumber).setMaxResults(1).uniqueResult();
	            }

	        });
		}
		
		@SuppressWarnings("unchecked")
		public BrandItem findUniqueBrandItemByNumberAndBrand(final String itemNumber,final String brand) {
			return (BrandItem) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                return session.createQuery(
	                        "from BrandItem bi where bi.itemNumber =:itemNumber and bi.brand=:brand")
	                        .setParameter("itemNumber",itemNumber).setParameter("brand",brand).uniqueResult();
	            }

	        });
		}
		
		@SuppressWarnings("unchecked")
		public List<BrandItem> findBrandItems(final String number,final String name) {
			return (List<BrandItem>) getHibernateTemplate().execute(new HibernateCallback() {
	            public Object doInHibernate(Session session) throws HibernateException, SQLException {
	                return session.createQuery(
	                        "from BrandItem bi where bi.itemNumber =:number and bi.brand=:name")
	                        .setParameter("number",number).setParameter("name",name).list();
	            }

	        });
		}
}
