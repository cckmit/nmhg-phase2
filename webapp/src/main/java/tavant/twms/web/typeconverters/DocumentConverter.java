package tavant.twms.web.typeconverters;

import tavant.twms.domain.common.Document;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: vikas.sasidharan
 * Date: 29 Jun, 2009
 * Time: 5:29:02 PM
 */
public class DocumentConverter extends DomainObjectConverter {

    //The UI can populate this, when it explicitly wants a null to be shown
    public static final String NULL = "null";
    private Logger logger = Logger.getLogger(DocumentConverter.class);

    @Override
    public Object convertValue(Map ctx, Object obj, Class toType) {

        Object returnedValue;

        if (toType == String.class) {
            returnedValue = ((Document) obj).getFileName();
        } else if (obj instanceof File[]) {
            File file = null;

            try {
                file = ((File[]) obj)[0];
                returnedValue = new Document(file);
            } catch (Exception e) {
                String errorMessage = "Exception in converting file [" + file + "] to Document : ";
                logger.error(errorMessage, e);
                throw new RuntimeException(errorMessage, e);
            }
        } else { // Use parent(DomainObjectConverter)'s conversion logic.
            returnedValue = convertToNonStringType(obj, toType);
        }

        return returnedValue;
    }
}