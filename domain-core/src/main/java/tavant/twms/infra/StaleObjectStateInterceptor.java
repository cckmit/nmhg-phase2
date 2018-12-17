package tavant.twms.infra;

import org.hibernate.StaleObjectStateException;
import org.hibernate.type.Type;

import java.io.Serializable;

/**
 * Hibernate interceptor to dirty check entities across web requests. This can be used to prevent multiple users from
 * updating the same entity thereby overriding the first user's changes. This requires the version number to be passed
 * back (either hidden field or part of JSON payload) and set on the entity on user action.
 * <p/>
 * The interceptor checks if the version received from the user request matches the version number in database and if
 * it's older throws a StaleObjectStateException
 *
 * @see tavant.twms.infra.DirtyCheck
 */
public class StaleObjectStateInterceptor extends NestedSetInterceptor {

    @Override
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
        if (entity instanceof DirtyCheck) {
            for (int i = 0; i < propertyNames.length; i++) {
                if ("version".equals(propertyNames[i])) {
                    if (((Integer) currentState[i]) < ((Integer) previousState[i])) {
                        throw new StaleObjectStateException(entity.getClass().getCanonicalName(), id);
                    }
                    break;
                }
            }
        }
        return false;
    }
}
