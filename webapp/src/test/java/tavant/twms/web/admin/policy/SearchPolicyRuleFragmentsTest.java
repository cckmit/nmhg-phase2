package tavant.twms.web.admin.policy;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.PredicateAdministrationServiceImpl;
import tavant.twms.infra.WebappRepositoryTestCase;

public class SearchPolicyRuleFragmentsTest extends WebappRepositoryTestCase {
    
    public void testSearch() throws JSONException {
        SearchPolicyRuleFragments fixture = new SearchPolicyRuleFragments();
        
        final List<DomainPredicate> predicates = new ArrayList<DomainPredicate>();
        DomainPredicate domainPredicate = new DomainPredicate();
        domainPredicate.setId(new Long(1));
        domainPredicate.setName("Claim's Inventory Item Condition is NEW or REFURBISHED");
        predicates.add( domainPredicate );
        
        domainPredicate = new DomainPredicate();
        domainPredicate.setId(new Long(2));
        domainPredicate.setName("Claim's Inventory Item is retailed");
        predicates.add(domainPredicate);
        
        fixture.setPredicateAdministrationService(new PredicateAdministrationServiceImpl() {
            @Override
            public List<DomainPredicate> findPredicatesByName(String arg0, String arg1) {
                return predicates;
            }
            
        });
        
        fixture.setSearchKey("Inventory");
        fixture.search();
        
        
    }

}
