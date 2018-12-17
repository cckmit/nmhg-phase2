package tavant.twms.domain.orgmodel;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.annotations.common.DisableDeActivation;
import tavant.twms.infra.CriteriaHelper;

public class TechnicianRepositoryImpl extends HibernateDaoSupport implements
		TechnicianRepository {
	
	private CriteriaHelper criteriaHelper;

	public void save(Technician technician) {
		getHibernateTemplate().save(technician);
	}

	public void update(Technician technician) {
		getHibernateTemplate().update(technician);
	}

	public Technician findById(Long id) {
		return (Technician) getHibernateTemplate().get(Technician.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Technician> findTechnicianForDealer(final Long dealerId) {
		return (List<Technician>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria technicianCriteria = session
								.createCriteria(Technician.class).createCriteria("orgUser");
						List addedAliases = new ArrayList<String>(5);
						technicianCriteria.createAlias("belongsToOrganizations", "belongsToOrgs");
						technicianCriteria.add(Restrictions.eq(
								"belongsToOrgs.id", dealerId));
						TechnicianRepositoryImpl.this.criteriaHelper
								.addAliasIfRequired(technicianCriteria,
										"roles", "roles", addedAliases);
						technicianCriteria.add(Restrictions.eq("roles.name",
								"technician"));
						technicianCriteria
								.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
						return technicianCriteria.list();
					};
				});
	}
	
	@SuppressWarnings("unchecked")
	@DisableDeActivation
	public Technician findByName(final String userName) {
        return (Technician) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from Technician u where upper(u.orgUser.name) =:nameParam").setString(
                        "nameParam", userName.trim().toUpperCase()).uniqueResult();
            }
        });
    }

	@SuppressWarnings("unchecked")
	public List<Technician> findTechnicianForDealers(final Set<Long> dealerIds) {
		return (List<Technician>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						Criteria technicianCriteria = session
								.createCriteria(Technician.class);
						List addedAliases = new ArrayList<String>(5);
						technicianCriteria.createAlias("orgUser.belongsToOrganizations", "belongsToOrgs");
						technicianCriteria.add(Restrictions.in(
								"belongsToOrgs.id", dealerIds));
						TechnicianRepositoryImpl.this.criteriaHelper
								.addAliasIfRequired(technicianCriteria,
										"orgUser.roles", "roles", addedAliases);
						technicianCriteria.add(Restrictions.eq("roles.name",
								"technician"));
						technicianCriteria
								.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
						return technicianCriteria.list();
					};
				});
	}

	public CriteriaHelper getCriteriaHelper() {
		return this.criteriaHelper;
	}

	public void setCriteriaHelper(CriteriaHelper criteriaHelper) {
		this.criteriaHelper = criteriaHelper;
	}
}
