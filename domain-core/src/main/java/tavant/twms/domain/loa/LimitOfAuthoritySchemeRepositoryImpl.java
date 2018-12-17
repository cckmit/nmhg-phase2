package tavant.twms.domain.loa;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

public class LimitOfAuthoritySchemeRepositoryImpl extends GenericRepositoryImpl<LimitOfAuthorityScheme, Long> implements
LimitOfAuthoritySchemeRepository {

	public LimitOfAuthorityScheme findByName(final String name) {

		return (LimitOfAuthorityScheme) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from LimitOfAuthorityScheme l where  l.name=:nameParam")
								.setString("nameParam", name).uniqueResult();
					}
				});
	}
	
	public PageResult<LimitOfAuthorityScheme> findAllLOASchemes(ListCriteria listCriteria){
		
		String query = " from LimitOfAuthorityScheme loaScheme ";
		return findPage(query, listCriteria);
	}
	public List<LimitOfAuthorityScheme> findLOASchemesForUser(final String loaUser) {

		return getHibernateTemplate().find(
						"select scheme from LimitOfAuthorityScheme scheme join scheme.loaLevels as level where "
								+ "level.loaUser.name = ?",
								new Object[] { loaUser });
				
	}

    @SuppressWarnings("unchecked")
    public List<LimitOfAuthorityScheme> findLOASchemesByType(final String type) {
        return getHibernateTemplate().find("from LimitOfAuthorityScheme scheme where scheme.type = ?", new Object[]{type});
    }

    public void deleteLOAScheme(final LimitOfAuthorityScheme limitOfAuthorityScheme) {
		getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session.createSQLQuery(
						"UPDATE limit_of_authority_scheme set d_active = 0 where id = '"
								+ limitOfAuthorityScheme.getId()+"'").executeUpdate();
			}

		});
	}

}
