package tavant.twms.domain.orgmodel;



import java.sql.SQLException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;

import tavant.twms.infra.GenericRepositoryImpl;

public class AdditionalLaborEligibilityRepositoryImpl extends
           GenericRepositoryImpl<AdditionalLaborEligibility, Long> 
           implements AdditionalLaborEligibilityRepository                         {

	
	
	public void updateAdditionalLaborEligibility(AdditionalLaborEligibility additionalLaborEligibility){
		getHibernateTemplate().update(additionalLaborEligibility);
	}
 
	public void createAdditionalLaborEligibility(AdditionalLaborEligibility additionalLaborEligibility){
		
		getHibernateTemplate().save(additionalLaborEligibility);
	}
	
	@SuppressWarnings("unchecked")
	public AdditionalLaborEligibility findAddditionalLabourEligibility() {
		return (AdditionalLaborEligibility) getHibernateTemplate().execute(
				new HibernateCallback() {

					public Object doInHibernate(Session session)
							throws HibernateException, SQLException {
						return session
								.createQuery(
										" from AdditionalLaborEligibility ale" ).uniqueResult();
								
								
					};
				});
	}	
	
}


