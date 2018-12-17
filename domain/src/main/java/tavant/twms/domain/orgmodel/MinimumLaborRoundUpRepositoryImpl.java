package tavant.twms.domain.orgmodel;



import java.sql.SQLException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

public class MinimumLaborRoundUpRepositoryImpl extends
           GenericRepositoryImpl<MinimumLaborRoundUp, Long> 
           implements MinimumLaborRoundUpRepository                         {

	
	
	public void updateMinimumLaborRoundUp(MinimumLaborRoundUp minimumLaborRoundUp){
		getHibernateTemplate().update(minimumLaborRoundUp);
	}
 
	public void createMinimumLaborRoundUp(MinimumLaborRoundUp minimumLaborRoundUp){
		
		getHibernateTemplate().save(minimumLaborRoundUp);
	}
	
	@SuppressWarnings("unchecked")
	public MinimumLaborRoundUp findMinimumLaborRoundUp() {
		return (MinimumLaborRoundUp) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session.createQuery(" from MinimumLaborRoundUp mlr" ).uniqueResult();
								
								
					};
				});
	}
	
}


