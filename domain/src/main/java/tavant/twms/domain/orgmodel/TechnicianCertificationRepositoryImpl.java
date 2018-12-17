package tavant.twms.domain.orgmodel;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.ListCriteria;
import tavant.twms.infra.PageResult;

@SuppressWarnings("serial")
public class TechnicianCertificationRepositoryImpl extends
		GenericRepositoryImpl<TechnicianCertification, Long> implements
		TechnicianCertificationRepository, Serializable {
	public List<TechnicianCertification> getCertificationForTechnician(
			final AttributeValue attr) {
		return (List<TechnicianCertification>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from TechnicianCertification where technician=:attr")
								.setParameter("attr", attr).list();
					};
				});
	}

	public PageResult<TechnicianCertification> findAllTechnicianCertificates(
			final ListCriteria listCriteria) {
		return findPage("from TechnicianCertification techCertification",listCriteria);
	}	
}