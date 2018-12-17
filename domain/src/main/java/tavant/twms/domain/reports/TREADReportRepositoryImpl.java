package tavant.twms.domain.reports;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

public class TREADReportRepositoryImpl  extends HibernateDaoSupport implements TREADReportRepository {

	public List<Map<String, Object>> getClaimsInfo(final int year, final int quarter) {
		return executeQuery("treadReport.claimsInfo",year, quarter);
	}

	public List<Map<String, Object>> getProductionInfo(final int year, final int quarter) {
		return executeQuery("treadReport.productionInfo",year, quarter);
	}

	public List<Map<String, Object>> getConsumerComplaintsInfo(int year, int quarter) {
		return executeQuery("treadReport.complaints",year, quarter);
	}

	public List<Map<String, Object>> getFieldReportsInfo(int year, int quarter) {
		return executeQuery("treadReport.fieldReports",year, quarter);
	}

	public List<Map<String, Object>> getPropertyDamageInfo(int year, int quarter) {
		return executeQuery("treadReport.propertyDamages",year, quarter);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> executeQuery(final String queryName,final int year, final int quater) {
		return getHibernateTemplate().executeFind(new HibernateCallback(){
			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				return session.getNamedQuery(queryName)
						.setParameter("year",year)
						.setParameter("quater",quater)
						.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
						.list();
			}
		});
	}

}
