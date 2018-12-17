package tavant.twms.domain.orgmodel;
import java.util.List;

import tavant.twms.infra.GenericRepositoryImpl;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import java.sql.SQLException;

/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */


/**
 * @author aniruddha.chaturvedi
 *
 */
public class CoreCertificationRepositoryImpl 
extends GenericRepositoryImpl<CoreCertification, Long> implements
		CoreCertificationRepository {

	public List<CoreCertification> findCOCertificateByNameAndBrand(
			final String certificateName, final String categoryLevel,
			final String certificateBrand) {
		return (List<CoreCertification>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from CoreCertification coCertificate where upper(coCertificate.certificateName) = upper(:certificateName) and upper(coCertificate.brand)=upper(:brand) and upper(coCertificate.categoryLevel)=upper(:categoryLevel)")
								.setParameter("certificateName",
										certificateName.trim())
								.setParameter("brand", certificateBrand.trim())
								.setParameter("categoryLevel", categoryLevel)
								.list();
					}
				});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CoreCertification findByCertificateName(final String certificateName) {
		// TODO Auto-generated method stub
		return (CoreCertification) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String hqlQuery = "from CoreCertification where upper(certificateName) = upper(:cerificateName)";
						Query query = session.createQuery(hqlQuery);
						query.setParameter("certificateName", certificateName.trim());
						return query.uniqueResult();
					}
				});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public CoreCertification findByCertificateNameForCO(final String certificateName,final String brand) {
		return (CoreCertification) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String hqlQuery = "from CoreCertification core where upper(core.certificateName) = upper(:certificateName) and upper(core.brand)= upper(:brand)";
						Query query = session.createQuery(hqlQuery);
						query.setParameter("certificateName", certificateName.trim());
						query.setParameter("brand", brand.trim());
						return query.uniqueResult();

					}
				});
	}
	
}
