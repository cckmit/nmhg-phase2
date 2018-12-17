package tavant.twms.web.typeconverters;

import com.opensymphony.xwork2.conversion.TypeConversionException;
import org.springframework.util.StringUtils;

/**
 * This converter converts domain objects to strings and vice versa. An empty string gets converted to a NULL
 * object and not to a transient instance. This is useful for handling domain objects which are essentially
 * reference data - the application is not expected to create new transient instances of the domain object,
 * but to use references to existing persisted instances. Hence the name, ReferenceDomainObjectConverter.
 *
 * @author binil.thomas
 */
public class ReferenceDomainObjectConverter extends AbstractDomainObjectConverter {

    @Override
    protected String convertIdToString(Object id) {
        return (id == null) ? "" : id.toString();
    }

    @Override
    protected Object fetchDomainObject(String id, Class toClass) {
        if (StringUtils.hasText(id)) {
            try {
                return getDomainRepository().load(toClass, new Long(id));
            } catch (Exception origEx) {
                TypeConversionException thrownEx = null;
                if (origEx instanceof TypeConversionException) {
                    thrownEx = (TypeConversionException) origEx;
                } else {
                    thrownEx = new TypeConversionException("Error converting " + id + " to " + toClass, origEx);
                }
                throw thrownEx;
            }
        } else {
            return null;
        }
    }
}
