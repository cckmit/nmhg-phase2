package tavant.twms.domain.orgmodel;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.annotations.common.DisableDeActivation;
import tavant.twms.annotations.common.DisableSpecificBuSelection;
import tavant.twms.infra.GenericRepositoryImpl;

public class BrandRepositoryImpl extends GenericRepositoryImpl<Brand, Long>
		implements BrandRepository {

	public Brand findByBrandCode(final String code) {
		return (Brand) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createQuery(
								"from Brand b where b.brandCode=:nameParam")
						.setString("nameParam", code).uniqueResult();
			}
		});
	}

}
