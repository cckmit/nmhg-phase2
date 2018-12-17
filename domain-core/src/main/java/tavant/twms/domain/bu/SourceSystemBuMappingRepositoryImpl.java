package tavant.twms.domain.bu;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;


public class SourceSystemBuMappingRepositoryImpl extends
GenericRepositoryImpl<SourceSystemBuMapping, String> implements SourceSystemBuMappingRepository{

	public SourceSystemBuMapping findBySourceSystem(final String name) {
		return (SourceSystemBuMapping) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery("select mapping from SourceSystemBuMapping mapping where mapping.sourceSystem = :name")
								.setString("name",name).uniqueResult();
					}

				});
	}

}
