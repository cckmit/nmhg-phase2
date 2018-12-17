package tavant.twms.domain.bu;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

public class BusinessUnitRepositoryImpl extends
		GenericRepositoryImpl<BusinessUnit, String> implements
		BusinessUnitRepository {

	public List<BusinessUnit> findAllBusinessUnits() {
		return findAll();
	}

	public BusinessUnit findBusinessUnit(final String name){
		return (BusinessUnit) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery("select bu from BusinessUnit bu where bu.name = :name")
								.setString("name", name)
								.uniqueResult();
					}

				});
	}
	public DivisionBusinessUnitMapping findBusinessUnitForDivisionCode(final String divisionCode){
		return (DivisionBusinessUnitMapping) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery("select dbum from DivisionBusinessUnitMapping dbum where dbum.divisionCode = :divisionCode")
								.setString("divisionCode", divisionCode)
								.uniqueResult();
					}

				});
	}

    public List<BusinessUnit> findBusinessUnitsForNames(final Set<String> buNames){
		return (List<BusinessUnit>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery("select bu from BusinessUnit bu where bu.name in (:buNames)")
                                .setParameterList("buNames",buNames)
								.list();
					}

				});
	}

}