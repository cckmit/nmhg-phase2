package tavant.twms.domain.common;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

@SuppressWarnings("rawtypes")
public class PdiRepositoryImpl extends GenericRepositoryImpl implements PdiRepository{

	@SuppressWarnings("unchecked")
	public List<DeliveryCheckList> populateDeliveryCheckList() {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				List checkList = session.createCriteria(DeliveryCheckList.class).addOrder(Order.asc("id")).list();
				return checkList;
			}
		});
	}
}
