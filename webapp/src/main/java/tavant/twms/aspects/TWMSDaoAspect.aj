import net.sf.infrared.aspects.aj.AbstractApiAspect;
                        
public aspect TWMSDaoAspect extends AbstractApiAspect {
    public pointcut apiExecution():
		execution( public *  org.springframework.orm.hibernate3.support.HibernateDaoSupport*+.*(..) );
		
    public String getLayer() {
        return "TWMS-Dao"; 
    }
}