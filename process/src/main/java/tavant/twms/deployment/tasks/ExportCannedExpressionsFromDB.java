/**
 * 
 */
package tavant.twms.deployment.tasks;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.FileCopyUtils;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.domain.rules.RuleSerializer;
import tavant.twms.infra.PageResult;
import tavant.twms.infra.PageSpecification;

/**
 * @author radhakrishnan.j
 *
 */
public class ExportCannedExpressionsFromDB extends DefaultTask {
	private PredicateAdministrationService predicateAdministrationService;
	private RuleSerializer xmlSerializer;
    private static final String PREDICATE_TABLE_NAME = "domain_predicate";
    private static final Logger logger =
            Logger.getLogger(ExportCannedExpressionsFromDB.class);
    public static final String PREDICATES_EXPORT_SKIPPED_PROPERTY =
            "tavant.twms.process.exportPredicatesTaskWasSkipped";

    @Required
	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	
	@Required
	public void setXmlSerializer(RuleSerializer xmlRuleSerializer) {
		this.xmlSerializer = xmlRuleSerializer;
	}



	public void perform() {
		try {

            if(!doesTableExist(PREDICATE_TABLE_NAME)) {
                // First time that this task is being run. Hence, avoid this
                // step to prevent the export from breaking.
                if(logger.isInfoEnabled()) {
                    logger.info("Predicate table [" + PREDICATE_TABLE_NAME +
                    "] not found. Skipping export of canned predicates " +
                            "from DB.");
                }

                System.setProperty(PREDICATES_EXPORT_SKIPPED_PROPERTY, "true");

                return;
            }

            BusinessObjectModelFactory bom = BusinessObjectModelFactory.getInstance();
			SortedSet<String> ruleContexts = new TreeSet<String>();
			ruleContexts.addAll(bom.listAllRuleContexts());
			PageSpecification pageSpecification = new PageSpecification();
			pageSpecification.setPageSize(50);
			for( String ruleContext : ruleContexts ) {
				PageResult<DomainPredicate> page = predicateAdministrationService.findAllRulesInContext(ruleContext, pageSpecification);
				List<DomainPredicate> predicates = page.getResult();
				
				String xml = xmlSerializer.toXML(predicates);
				StringReader stringReader = new StringReader(xml);
                File file = new File(ruleContext+"-Predicates.xml");
                Writer writer = new FileWriter(file);
                FileCopyUtils.copy(stringReader,writer);
			}
		} catch (Exception e) {
			// ExportExpressions task can fail for valid reasons. For eg., if
            // the domain model has changed (say, a new field was added), this
            // change won't be reflected in the db *until* the create.database
            // task is run. 'coz of this the queries executed as part of this
            // task would fail in such a scenario. We shouldn't short-circuit
            // the entire tasks execution in this case.
            logger.error("Exception while exporting expressions from DB : " +
				e.getMessage());
		}
	}

}
