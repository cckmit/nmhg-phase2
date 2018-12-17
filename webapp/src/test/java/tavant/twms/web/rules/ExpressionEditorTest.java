package tavant.twms.web.rules;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import tavant.twms.domain.common.ListOfValues;
import tavant.twms.domain.common.RejectionReason;
import tavant.twms.domain.query.SavedQuery;
import tavant.twms.domain.rules.Constant;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.Equals;
import tavant.twms.domain.rules.PredicateAdministrationException;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.domain.rules.Type;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;
import tavant.twms.infra.WebappRepositoryTestCase;
import tavant.twms.infra.ListCriteria;

public class ExpressionEditorTest extends WebappRepositoryTestCase {

    public void testNewRule() throws IOException {
        ExpressionEditor fixture = new ExpressionEditor();
        fixture.setPredicateAdministrationService(new EmptyService() {

            @Override
            public List<DomainPredicate> findPredicatesByName(String name, String forRuleEditorContext) {
                assertEquals("", name);
                assertEquals("PolicyRules", forRuleEditorContext);
                List<DomainPredicate> predicates = new ArrayList<DomainPredicate>();
                DomainPredicate domainPredicate = new DomainPredicate();
                domainPredicate.setName("name1");

                Equals equals = new Equals(new Constant("A", Type.STRING), new Constant("B", Type.STRING));

                domainPredicate.setId(new Long(1));
                predicates.add(domainPredicate);
                domainPredicate.setPredicate(equals);

                domainPredicate = new DomainPredicate();
                domainPredicate.setName("name2");
                domainPredicate.setId(new Long(2));
                predicates.add(domainPredicate);
                domainPredicate.setPredicate(equals);
                return predicates;
            }

        });
        fixture.setContext("PolicyRules");

        fixture.newExpression();

        ClassPathResource classPathResource = new ClassPathResource("ExpressionEditorTest-dataElementsJSON.js",
                ExpressionEditorTest.class);
        InputStream inputStream = classPathResource.getInputStream();
        String expected = IOUtils.toString(inputStream);

        expected = expected.replaceAll("\\s", "");
        String actual = fixture.getAvailableVariablesJSON();
        actual = actual.replaceAll("\\s", "");
        // assertEquals(expected,actual);

        classPathResource = new ClassPathResource("ExpressionEditorTest-rulesJSON.js", ExpressionEditorTest.class);
        inputStream = classPathResource.getInputStream();
        expected = IOUtils.toString(inputStream);
        expected = expected.replaceAll("\\s", "");
        // assertEquals(expected,fixture.getAvailableRulesJSON().replaceAll("\\s",""));
    }

    static class EmptyService implements PredicateAdministrationService {

        public PageResult<DomainPredicate> findAllNonSearchPredicates(ListCriteria listCriteria) {
            return null;
        }

        public List<DomainPredicate> findPredicatesByName(String name) {
            return null;
        }

        public List<DomainPredicate> findPredicatesByName(String name, String forRuleEditorContext) {
            return null;
        }

        public void delete(DomainPredicate t) throws PredicateAdministrationException {
        }

        public List<SavedQuery> findSavedQueriesByContextAndUser(String context, Long userId) {
            // TODO Auto-generated method stub
            return null;
        }

        public void saveSavedQuery(SavedQuery scr) {
            // TODO Auto-generated method stub

        }

        public List<DomainPredicate> findAll() {
            return null;
        }

        public PageResult<DomainPredicate> findAll(PageSpecification pageSpecification) {
            return null;
        }

        public DomainPredicate findById(Long id) {
            return null;
        }

        public List<DomainPredicate> findByIds(Collection<Long> collectionOfIds) {
            return null;
        }

        public void save(DomainPredicate t) throws PredicateAdministrationException {
        }

        public void update(DomainPredicate t) throws PredicateAdministrationException {
        }

        public void updateSavedQuery(SavedQuery scr) {
            // TODO Auto-generated method stub

        }

        public PageResult<DomainPredicate> findAllRulesInContext(String context, PageSpecification pageSpecification) {
            return null;
        }

        public List<DomainPredicate> findClashingPredicates(DomainPredicate predicate) {
            return null;
        }

        public List<DomainPredicate> findClashingPredicates(String context, DomainPredicate predicate) {
            return null;
        }

        public List<DomainRule> findRulesUsingPredicate(DomainPredicate domainPredicate) {
            return null;
        }

        public List<DomainPredicate> findPredicatesReferringToPredicate(DomainPredicate domainPredicate) {
            return null;
        }

        public List<DomainPredicate> findByNameLike(String name) {
            return null;
        }

        public List<DomainPredicate> findAllPredicatesByContext(String context) {
            // TODO Auto-generated method stub
            return null;
        }

        public SavedQuery findSavedQueryById(Long id) {
            return null;
        }

        public void deleteSavedQuery(SavedQuery scr) {
        }

        public PageResult<DomainPredicate> findAllNonSearchPredicates(PageSpecification pageSpecification) {
            return null;
        }

        public List<DomainPredicate> findNonSearchPredicatesByName(String name) {
            // TODO Auto-generated method stub
            return null;
        }

        public Integer findMaxRuleNumberForContext() {
            // TODO Auto-generated method stub
            return null;
        }

		

		public PageResult<?> findAllDcapPredicates(
				PageSpecification pageSpecification) {
			// TODO Auto-generated method stub
			return null;
		}

		public List<DomainPredicate> findDcapPredicatesByName(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		public List<SavedQuery> findSavedQueriesByUserUsingContext(String context, Long userId) {
			// TODO Auto-generated method stub
			return null;
		}

		public void deleteAll(List<DomainPredicate> entitiesToDelete) {
			// TODO Auto-generated method stub
			
		}

		public List<DomainPredicate> findNonSearchPredicatesByName(String name,
				boolean includeSystemConditions) {
			// TODO Auto-generated method stub
			return null;
		}

		public List<ListOfValues> findAllDescription(String classname) {
			// TODO Auto-generated method stub
			return null;
		}

		
    }
}
