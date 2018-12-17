package tavant.twms.domain.partreturn;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import tavant.twms.infra.GenericRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: deepak.patel
 * Date: 2/12/12
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class WpraRepositoryImpl extends GenericRepositoryImpl<Wpra, Long> implements WpraRepository{

     public void reloadWpras(final List<Wpra> wpras) {
		getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session) throws HibernateException, SQLException {
						session.flush();
						for (Wpra wpra : wpras) {
							session.refresh(wpra);
						}
						return null;
					};
				});
    }
     
	@SuppressWarnings("unchecked")
	public Wpra findLastFiledWpra() {
		// TODO Auto-generated method stub
		return (Wpra) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createQuery("from Wpra wpra where wpra.id=(select MAX(w.id) from Wpra w )");
			}
		});
	}

}
