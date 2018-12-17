package tavant.twms.worklist;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import tavant.twms.domain.orgmodel.Role;
import tavant.twms.domain.orgmodel.User;

public class BaseWorkListDaoImpl extends HibernateDaoSupport implements BaseWorkListDao {

    private static final String FIND_TASKS_QUERY = "findAllTasksForSwimlane";

    private static final String COUNT_TASKNAMES_QUERY = "countTasksByTaskNames";

    /**
     * Returns the tasks and the count for that particular task for a given actor.
     */
    public Map<Task, Long> getAllTasks(final WorkListCriteria criteria) {

        Map<Task, Long> taskTypesWithCount = new LinkedHashMap<Task, Long>();
        List<Task> taskTypes = getTasks(criteria);
        if (taskTypes.size() == 0) {
            return taskTypesWithCount;
        }
        getCount(criteria, taskTypes, taskTypesWithCount);

        return taskTypesWithCount;
    }

    public void updateTaskInstance(TaskInstance taskInstance) {
        getHibernateTemplate().update(taskInstance);
    }

    @SuppressWarnings("unchecked")
    protected List<Task> getTasks(final WorkListCriteria criteria) {
        return (List<Task>) getHibernateTemplate().execute(
                new HibernateCallback() {
                    public Object doInHibernate(Session session)
                            throws HibernateException, SQLException {
                        Query query = session.getNamedQuery(FIND_TASKS_QUERY);
                        query.setParameter("process", criteria.getProcess());
                        query.setParameterList("swimlanes",
                                getSwimlanes(criteria.getUser()));
                        return query.list();
                    }
                });
    }

    protected List<String> getSwimlanes(User user) {
        Set<Role> roles = user.getRoles();
        List<String> params = new ArrayList<String>();
        for (Role role : roles) {
            params.add(role.getName());
        }
        return params;
    }

    protected List<String> getTaskNames(List<Task> tasks) {
        List<String> taskNames = new ArrayList<String>();
        for (Task task : tasks) {
            taskNames.add(task.getName());
        }
        return taskNames;
    }

    protected void addSortCriteria(WorkListCriteria criteria, StringBuffer query) {
        if (criteria.isSortCriteriaSpecified()) {
            query.append(" order by ");
            query.append(criteria.getSortCriteriaString());
        }
    }

    protected void addFilterCriteria(WorkListCriteria criteria, StringBuffer query) {
        if (criteria.isFilterCriteriaSpecified()) {
            query.append(" and ");
            query.append(criteria.getParamterizedFilterCriteria());
        }
    }

    @SuppressWarnings("unchecked")
    protected void getCount(final WorkListCriteria criteria, final List<Task> tasks, final Map<Task, Long> taskTypesWithCount) {
        getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(final Session session)
                    throws HibernateException, SQLException {
                Query query = session.getNamedQuery(COUNT_TASKNAMES_QUERY);
                query.setParameter("processDefinition", criteria.getProcess());
                query.setString("actorId", criteria.getUser().getName());
                query.setParameterList("taskNames", getTaskNames(tasks));

                final List<Object[]> result = (List<Object[]>) query.list();
                for (Task task : tasks) {
                    boolean countPresent = false;
                    for (Object[] objects : result) {
                        if (objects[1].equals(task.getName())) {
                            taskTypesWithCount.put(task, (Long) objects[0]);
                            countPresent = true;
                            break;
                        }
                    }
                    if (!countPresent) {
                        taskTypesWithCount.put(task, 0L);
                    }
                }
                return null;
            }
        });
    }
}
