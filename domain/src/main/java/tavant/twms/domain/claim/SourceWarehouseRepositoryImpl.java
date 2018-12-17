package tavant.twms.domain.claim;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.domain.common.SourceWarehouse;

/**
 * Created by IntelliJ IDEA.
 * User: pradyot.rout
 * Date: Jan 29, 2009
 * Time: 2:02:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class SourceWarehouseRepositoryImpl  extends GenericRepositoryImpl<SourceWarehouse,Long> implements SourceWarehouseRepository{
	
	@SuppressWarnings("unchecked")
		public  SourceWarehouse findSourceWarehouseByCode(final String code) {
		return (SourceWarehouse) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createQuery(
						"select sourceWarehouse from SourceWarehouse sourceWarehouse where sourceWarehouse.code = :code")
						.setParameter("code",code)
						.uniqueResult();
			}
		});
	}
}
