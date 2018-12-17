package tavant.twms.domain.common;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.After;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

@Aspect
public class DisableDeactivationFilter {

	@Before("execution(* tavant.twms.infra.GenericRepository+.*(..)) && "
			+ "@annotation(tavant.twms.annotations.common.DisableDeActivation)")
	public void applyBefore(JoinPoint joinPoint) {
		HibernateDaoSupport daoSupport = (HibernateDaoSupport) joinPoint
				.getTarget();
		daoSupport.getHibernateTemplate().getSessionFactory().getCurrentSession().disableFilter("excludeInactive");
	}

	@After("execution(* tavant.twms.infra.GenericRepository+.*(..)) && "
			+ "@annotation(tavant.twms.annotations.common.DisableDeActivation)")
	public void applyAfter(JoinPoint joinPoint) {
		HibernateDaoSupport daoSupport = (HibernateDaoSupport) joinPoint
				.getTarget();
		daoSupport.getHibernateTemplate().getSessionFactory().getCurrentSession().enableFilter("excludeInactive");
	}

}
