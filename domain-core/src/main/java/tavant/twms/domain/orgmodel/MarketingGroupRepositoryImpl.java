package tavant.twms.domain.orgmodel;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.annotations.common.DisableDeActivation;
import tavant.twms.annotations.common.DisableSpecificBuSelection;
import tavant.twms.infra.GenericRepositoryImpl;

public class MarketingGroupRepositoryImpl extends GenericRepositoryImpl<MarketingGroup, Long> implements MarketingGroupRepository{

		 @DisableDeActivation
		 @DisableSpecificBuSelection
		    public MarketingGroup findByMarketingCode(final String code) {
		        return (MarketingGroup) getHibernateTemplate().execute(new HibernateCallback() {
		            public Object doInHibernate(Session session) throws HibernateException, SQLException {
		                 return session.createQuery("from MarketingGroup  m where lower(m.mktGrpCode)=:nameParam").setString(
		                         "nameParam", code).uniqueResult();
		            }
		        });
	    }

}
