package tavant.twms.domain.orgmodel;

import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import tavant.twms.domain.catalog.ItemGroup;
import tavant.twms.infra.GenericRepositoryImpl;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.QueryParameters;

public class SeriesRefCertificationRepositoryImpl extends
		GenericRepositoryImpl<SeriesRefCertification, Long> implements
		SeriesRefCertificationRepository {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SeriesRefCertification findBySeries(final ItemGroup series) {
		// TODO Auto-generated method stub
		return (SeriesRefCertification) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String hqlQuery = "from SeriesRefCertification src where src.series = :series";
						Query query = session.createQuery(hqlQuery);
						query.setParameter("series", series);
						return query.uniqueResult();

					}
				});
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public List<SeriesCertification> findByCertificateNameForPR(final String certificateName, final String brand) {
		return (List<SeriesCertification>) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						String hqlQuery = "from SeriesCertification crs where upper(crs.certificateName) = upper(:certificateName) and crs.categoryLevel='PR' and upper(crs.brand)=upper(:brand)";
						Query query = session.createQuery(hqlQuery);
						query.setParameter("certificateName", certificateName);
						query.setParameter("brand", brand.toUpperCase());
						return query.list();

					}
				});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<SeriesRefCertification> findAllCertificatesStartingWith(
			final String partialCertificateName, final int pageNumber, final int pageSize) {
		return (List<SeriesRefCertification>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" select seriesRefCertification from SeriesRefCertification seriesRefCertification "
												+ "where upper(seriesRefCertification.certificateName) like :partialCertificateName")
								.setParameter("partialCertificateName",
										partialCertificateName + "%")
								.setFirstResult(pageNumber).setMaxResults(
										pageSize).list();
					}
				});
	}

	public PageResult<SeriesRefCertification> findAllSeriesWithCertificates(
			String fromClause, String orderByClause, String selectClause,
			PageSpecification pageSpecification,
			QueryParameters queryParameters, String distinctClause) {
		// TODO Auto-generated method stub
		return super.findPageUsingQueryForDistinctItems(fromClause, orderByClause, selectClause, pageSpecification, queryParameters, distinctClause);
	}
}
