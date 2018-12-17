package tavant.twms.infra;

import org.hibernate.proxy.HibernateProxy;

public class InstanceOfUtil {
	public static boolean isInstanceOfClass(Class cls, Object obj){
		//this first check is to avoid unnecessary initialization of proxy even when it is a subclass of cls
		if (cls.isInstance(obj)){
			return true;
		}

        return (obj instanceof HibernateProxy) &&
                cls.isInstance(((HibernateProxy) obj).getHibernateLazyInitializer().getImplementation());
    }
}
