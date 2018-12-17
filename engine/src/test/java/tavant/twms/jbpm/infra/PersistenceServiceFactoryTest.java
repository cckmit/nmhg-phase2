package tavant.twms.jbpm.infra;

import org.jbpm.persistence.db.DbPersistenceServiceFactory;

import tavant.twms.infra.EngineRepositoryTestCase;

public class PersistenceServiceFactoryTest extends EngineRepositoryTestCase {
    
    public void testAvailabilityOfSessionFactory() {
        DbPersistenceServiceFactory dbPersistenceServiceFactory = 
            new PersistenceServiceFactory();
        assertNotNull("Valid SessionFactory should be configured", 
                dbPersistenceServiceFactory.getSessionFactory());
    }
}
