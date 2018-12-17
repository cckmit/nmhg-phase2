package tavant.twms.web.util;

import org.hibernate.SessionFactory;

import com.opensymphony.xwork2.ActionSupport;
import org.hibernate.cache.UpdateTimestampsCache;

public class ClearHibernateCacheAction extends ActionSupport {

    private SessionFactory sessionFactory;

    public void clearCache() throws Exception {
		sessionFactory.getCache().evictEntityRegions();
		sessionFactory.getCache().evictCollectionRegions();
		sessionFactory.getCache().evictDefaultQueryRegion();
        sessionFactory.getCache().evictQueryRegion(UpdateTimestampsCache.REGION_NAME);
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
}
