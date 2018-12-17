package tavant.twms.domain.claim.payment.rates;

import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.domain.catalog.BrandItem;
import tavant.twms.infra.GenericRepositoryImpl;

public class PartPricesRepositoryImpl extends GenericRepositoryImpl<PartPrices, Long> implements 
PartPricesRepository{
	
	public PartPrices findPartPricesByPartNumber(final BrandItem partNumber){
        return (PartPrices) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                 return session.createQuery("select pp from PartPrices pp join pp.nmhg_part_number.brandItems bi where bi=:partNumber").setEntity(
                         "partNumber", partNumber).uniqueResult();
            }
        });
    }
	
	
	public PartPrices findPartPricesByItemNumber(final String partNumber){
        return (PartPrices) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                 return session.createQuery("select pp from PartPrices pp join pp.nmhg_part_number i where i.number=:partNumber").setString(
                         "partNumber", partNumber).uniqueResult();
            }
        });
    }

}
