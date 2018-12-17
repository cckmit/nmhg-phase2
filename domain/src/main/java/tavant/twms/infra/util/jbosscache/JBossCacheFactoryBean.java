package tavant.twms.infra.util.jbosscache;

import java.util.ArrayList;
import java.util.List;

/*import org.jboss.cache.Cache;
import org.jboss.cache.CacheFactory;
import org.jboss.cache.DefaultCacheFactory;
import org.jboss.cache.config.Configuration;
import org.jboss.cache.config.Configuration.CacheMode;
import org.jboss.cache.factories.XmlConfigurationParser;*/
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.Lifecycle;

/*public class JBossCacheFactoryBean extends AbstractFactoryBean implements Lifecycle{

	private String configFile;
	private String clusterName;
	private CacheMode cacheMode;
	private boolean fetchInMemoryState;
	
	private List<Cache<String, Object>> cacheList = new ArrayList<Cache<String,Object>>();
	
	@Override
	protected Object createInstance() throws Exception {
		XmlConfigurationParser xmlParser = new XmlConfigurationParser();
		Configuration config = xmlParser.parseFile(configFile);
		config.setClusterName(clusterName);
		config.setCacheMode(cacheMode);
		config.setFetchInMemoryState(fetchInMemoryState);
		
		CacheFactory<String, Object> cacheFactory = new DefaultCacheFactory<String, Object>();
		Cache<String, Object> cache = cacheFactory.createCache();
		cacheList.add(cache);
		return cache;
	}

	public boolean isRunning() {
		return true;
	}

	public void start() {
		
	}

	public void stop() {
		for (Cache cache:cacheList) {
			cache.stop();
		}
	}

	public Class getObjectType() {
		return Cache.class;
	}

}*/
