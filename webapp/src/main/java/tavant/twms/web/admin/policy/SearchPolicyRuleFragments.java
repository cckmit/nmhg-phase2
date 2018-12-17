/**
 * 
 */
package tavant.twms.web.admin.policy;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Required;
import static tavant.twms.domain.businessobject.BusinessObjectModelFactory.POLICY_RULES;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.web.actions.TwmsActionSupport;
import tavant.twms.web.rules.SerializerFactory;

import java.util.List;

/**
 * @author radhakrishnan.j
 * 
 */
public class SearchPolicyRuleFragments extends TwmsActionSupport {
    private static Logger logger = LogManager.getLogger(SearchPolicyRuleFragments.class);

    private String searchKey;
    private String context = "PolicyRules" ;

    private List<DomainPredicate> searchResult;

    private String searchResultJSON;

    /*private RuleJSONSerializer ruleJSONSerializer = 
        new RuleJSONSerializer(POLICY_RULES);*/

    private PredicateAdministrationService predicateAdministrationService;
    
    private SerializerFactory serializerFactory;

    @Required
    public void setPredicateAdministrationService(PredicateAdministrationService predicateAdministrationService) {
        this.predicateAdministrationService = predicateAdministrationService;
    }

    public String getSearchResultJSON() {
        return searchResultJSON;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String search() throws JSONException {

    	
			searchResult = this.predicateAdministrationService
				.findNonSearchPredicatesByName(searchKey, false);
		

    	JSONArray _JSONArray = serializerFactory.getRuleJSONSerializer(
				POLICY_RULES).toJSONArray(searchResult);

    	if (logger.isDebugEnabled()) {
			logger.debug(" search key [" + searchKey + "] found ["
					+ _JSONArray.toString(4) + "]");
        }
        
    	searchResultJSON = _JSONArray.toString();
        return SUCCESS;
    }

    public List<DomainPredicate> getSearchResult() {
        return searchResult;
    }


    public String getContext() {
        return context;
    }


    public void setContext(String context) {
        this.context = context;
    }

	public SerializerFactory getSerializerFactory() {
		return serializerFactory;
	}
	@Required
	public void setSerializerFactory(SerializerFactory serializerFactory) {
		this.serializerFactory = serializerFactory;
	}
    
    

    
}
