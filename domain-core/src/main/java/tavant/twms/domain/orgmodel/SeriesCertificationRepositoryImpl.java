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
package tavant.twms.domain.orgmodel;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.orgmodel.SeriesRefCertification;
import tavant.twms.infra.GenericRepositoryImpl;

/**
 * @author aniruddha.chaturvedi
 * 
 */
public class SeriesCertificationRepositoryImpl extends
		GenericRepositoryImpl<SeriesRefCertification, Long> implements SeriesCertificationRepository {

	public List<SeriesRefCertification> findSeriesCertificationsByCertificateNameAndBrand(final String categoryName,
			final String certificateName, final String certificateBrand) {
		return (List<SeriesRefCertification>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										"from SeriesRefCertification seriesRefCertification where "
												+ " seriesRefCertification.id in (select seriesCertification.seriesRefCertfication from SeriesCertification seriesCertification"
												+ " where seriesCertification.certificateName=:certificateName and  seriesCertification.brand=:brand and seriesCertification.categoryLevel=:categoryName)")
								.setParameter("certificateName",
										certificateName)
								.setParameter("brand", certificateBrand)
								.setParameter("categoryName", categoryName).list();
					}
				});
	}

}
