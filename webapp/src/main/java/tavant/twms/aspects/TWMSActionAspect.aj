import net.sf.infrared.aspects.aj.AbstractApiAspect;
                        
public aspect TWMSActionAspect extends AbstractApiAspect {
    public pointcut apiExecution():
		execution( public *  com.opensymphony.xwork2.Action*+.*(..) );
		
    public String getLayer() {
        return "TWMS-Action"; 
    }
}