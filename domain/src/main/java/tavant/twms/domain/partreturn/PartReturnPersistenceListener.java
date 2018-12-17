package tavant.twms.domain.partreturn;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.event.PostLoadEvent;
import org.hibernate.event.PostLoadEventListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

import tavant.twms.domain.claim.RecoveryClaimService;
import tavant.twms.domain.configuration.ConfigParamService;

@SuppressWarnings("serial")
public class PartReturnPersistenceListener implements PostLoadEventListener,
		BeanFactoryAware {
	
	private static Logger logger = LogManager
			.getLogger(PartReturnPersistenceListener.class);
	private BeanFactory beanFactory;

	public void onPostLoad(PostLoadEvent postLoadEvent) {
		Object entity = postLoadEvent.getEntity();
		if (entity instanceof BasePartReturn) {
			BasePartReturn basePartReturn = (BasePartReturn) postLoadEvent.getEntity();
			basePartReturn.setConfigParamService(getConfigParamService());
			basePartReturn.setRecoveryClaimService(getRecoveryClaimService());
		}

	}

	public ConfigParamService getConfigParamService() {
		return (ConfigParamService) this.beanFactory.getBean("configParamService");
	}

	public RecoveryClaimService getRecoveryClaimService() {
		return (RecoveryClaimService) this.beanFactory.getBean("recoveryClaimService");
	}
	
	public BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;

	}

}
