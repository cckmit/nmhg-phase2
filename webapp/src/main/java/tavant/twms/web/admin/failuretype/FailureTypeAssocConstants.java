/**
 * 
 */
package tavant.twms.web.admin.failuretype;

/**
 * @author aniruddha.chaturvedi
 *
 */
public interface FailureTypeAssocConstants {
	public static final String NODE_TYPE = "nodeType",
	    INSTANCE_OF = "instanceOf",
	    CODE = "code",
	    ID = "id",
	    DEFINITION = "definition",
	    LABEL = "label", 
	    FAILURE_TYPE_CHILDREN = "failureTypeChildren",
	    FAILURE_CAUSE_CHILDREN = "failureCauseChildren", 
	    FAILURE_ROOT_CAUSE_CHILDREN = "failureRootCauseChildren",
	    FAILURE_CONTEXT_CAUSE = "failureCause", 
	    FAILURE_CONTEXT_ROOT_CAUSE = "failureRootCause";
	
	public static final String NODE_TYPE_ROOT = "root",
	    NODE_TYPE_LEAF = "leaf";
}
