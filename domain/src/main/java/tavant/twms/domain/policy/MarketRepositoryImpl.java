package tavant.twms.domain.policy;

import tavant.twms.domain.common.AdminConstants;
import tavant.twms.infra.GenericRepositoryImpl;

import java.util.List;
import java.sql.SQLException;

import org.springframework.orm.hibernate3.HibernateCallback;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.HibernateException;

/**
 * Created by IntelliJ IDEA.
 * User: irdemo
 * Date: May 27, 2009
 * Time: 3:36:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class MarketRepositoryImpl extends GenericRepositoryImpl<Market,Long> implements MarketRepository{

    public List<Market> listAllMarkets(){
        return (List<Market>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" from Market where parentId is null ").list();
					}

				});
    }

    public List<Market> listAllMarketTypesForMarket(final Long marketId){
        return (List<Market>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" from Market where parentId.id = :marketId and type = 'Market Type' ")
                                .setParameter("marketId",marketId).list();
					}

				});
    }

    public List<Market> listAllApplicationsForMarketType(final Long marketId){
        return (List<Market>) getHibernateTemplate().execute(
				new HibernateCallback() {
					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
								" from Market where parentId.id = :marketId and type = '" +(AdminConstants.MARKET_APPLICATION)+"'")
                                .setParameter("marketId",marketId).list();
					}

				});
    }
    @SuppressWarnings("unchecked")
	public List<Market> listMarketTypes() {

		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(" from Market market where type = '" +(AdminConstants.MARKET_TYPE)+"' order by market.title ").setCacheable(true);
				return query.list();
			}
		});
	}

	public Market findMarketTypeByTitle(final String title) {
		return (Market) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createQuery(
								" from Market market where type = '"
										+ (AdminConstants.MARKET_TYPE)
										+ "' and title=:title ")
						.setString("title", title).uniqueResult();
			}
		});
	}

	public Market findMarketApplicationByTitle(final Long marketId,
			final String title) {
		return (Market) getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				return session
						.createQuery(
								" from Market where parentId.id = :marketId and type = '"
										+ (AdminConstants.MARKET_APPLICATION)
										+ "' and title=:title")
						.setParameter("marketId", marketId)
						.setString("title", title).uniqueResult();
			}
		});
	}

}
