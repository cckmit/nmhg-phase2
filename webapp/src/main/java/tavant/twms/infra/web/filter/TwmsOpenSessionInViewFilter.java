package tavant.twms.infra.web.filter;

import org.springframework.orm.hibernate3.support.OpenSessionInViewFilter;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.util.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 18 Dec, 2008
 * Time: 6:31:44 PM
 */
public class TwmsOpenSessionInViewFilter extends OpenSessionInViewFilter {

    private String[] activeHibernateFilters = null;

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();

        String activeHibernateFiltersVal = getFilterConfig().getInitParameter("activeHibernateFilters");

        if(StringUtils.hasText(activeHibernateFiltersVal)) {
            this.activeHibernateFilters = StringUtils.delimitedListToStringArray(activeHibernateFiltersVal, ",");
        }
    }

    @Override
    protected Session getSession(SessionFactory sessionFactory) throws DataAccessResourceFailureException {
        Session session = super.getSession(sessionFactory);

        for (String activeHibernateFilter : activeHibernateFilters) {
            session.enableFilter(activeHibernateFilter);            
        }

        return session;
    }
}
