package tavant.twms.infra;

import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContextImpl;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.userdetails.UserDetailsService;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import tavant.twms.domain.orgmodel.User;
import tavant.twms.security.model.OrgAwareUserDetails;

public abstract class IntegrationTestCase extends AbstractTransactionalDataSourceSpringContextTests {

	private SessionFactory sessionFactory;

    private UserDetailsService userDetailsService;

    @Override
    public String[] getConfigLocations() {
        return new String[] { "classpath:integration-test-context.xml",
                "classpath*:/app-context.xml" };
    }

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    protected User login(String userId) {
        OrgAwareUserDetails user = (OrgAwareUserDetails) userDetailsService.loadUserByUsername(userId);

        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user, user.getPassword());

        SecurityContext securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(authenticationToken);
        SecurityContextHolder.setContext(securityContext);

        return user.getOrgUser();
    }

    protected void flush() {
        Session session = getSession();
        session.flush();
    }

    protected void evict(Object someObject) {
        Session session = getSession();
        session.evict(someObject);
    }

    protected void flushAndClear() {
        Session session = getSession();
        session.flush();
        session.clear();
    }

    protected Session getSession() {
        return SessionFactoryUtils.getSession(sessionFactory, false);
    }
}
