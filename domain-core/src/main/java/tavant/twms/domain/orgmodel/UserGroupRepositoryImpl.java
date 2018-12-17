/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.orgmodel;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * @author vineeth.varghese
 *
 */
public class UserGroupRepositoryImpl extends HibernateDaoSupport implements UserGroupRepository {

    public void createUserGroup(UserGroup group) {
        getHibernateTemplate().save(group);
    }

    public void updateUserGroup(UserGroup group) {
        getHibernateTemplate().update(group);
    }

    /* (non-Javadoc)
     * @see tavant.twms.domain.orgmodel.UserGroupRepositry#findAllUserGroups()
     */
    //TODO - Need to watch this. The whole list is loaded into the memory. Assuming that
    //TODO - the number of groups is not large.
    @SuppressWarnings("unchecked")
    public Set<UserGroup> findAllUserGroups() {
        List<UserGroup> listOfGroups = 
            (List<UserGroup>)getHibernateTemplate().find("select ug from UserGroup ug");
        Set<UserGroup> setOfGroups = new HashSet<UserGroup>();
        setOfGroups.addAll(listOfGroups);
        return setOfGroups;
    }

    /* (non-Javadoc)
     * @see tavant.twms.domain.orgmodel.UserGroupRepositry#findById(java.lang.Long)
     */
    public UserGroup findById(Long groupId) {
        return (UserGroup)getHibernateTemplate().get(UserGroup.class, groupId);
    }

    /* (non-Javadoc)
     * @see tavant.twms.domain.orgmodel.UserGroupRepositry#findByName(java.lang.String)
     */
    public UserGroup findByName(final String groupName) {
        return (UserGroup) getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session session) throws HibernateException, SQLException {
                return session.createQuery("from UserGroup ug where ug.name = :groupName")
                    .setString("groupName", groupName).uniqueResult();
            }
        });
    }

}
