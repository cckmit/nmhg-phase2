/**
 * 
 */
package tavant.twms.deployment.tasks;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.FileCopyUtils;
import tavant.twms.domain.rules.*;
import static tavant.twms.deployment.tasks.ExportRulesFromDB.*;

import java.io.*;
import java.util.List;

/**
 * @author radhakrishnan.j
 *
 */
public class RestoreRulesInDB extends DefaultTask {
	private RuleSerializer xmlSerializer;
	private RuleAdministrationService ruleAdministrationService;
	private PredicateAdministrationService predicateAdministrationService;
    private Logger logger = Logger.getLogger(RestoreRulesInDB.class);

    @Required
	public void setRuleAdministrationService(
			RuleAdministrationService ruleAdministrationService) {
		this.ruleAdministrationService = ruleAdministrationService;
	}

	
	@Required
	public void setXmlSerializer(RuleSerializer xmlRuleSerializer) {
		this.xmlSerializer = xmlRuleSerializer;
	}

	@Required
	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}


	@SuppressWarnings("unchecked")
	public void perform() {
		try {

        String rulesExportWasSkipped =
                System.getProperty(RULES_EXPORT_SKIPPED_PROPERTY);
        System.setProperty(RULES_EXPORT_SKIPPED_PROPERTY, ""); // Clean Up.

        if (Boolean.valueOf(rulesExportWasSkipped)) {
            if (logger.isInfoEnabled()) {
                logger.info("Rules were not exported earlier and hence " +
                        "skipping load also.");
            }

            return;
        }

            File file = new File("Rules.xml");
			if( file.exists() ) {
				Reader reader = new FileReader(file);
				Writer stringWriter = new StringWriter();
				FileCopyUtils.copy(reader,stringWriter);
				List<DomainRule> rules = (List<DomainRule>)xmlSerializer.fromXML(stringWriter.toString());		
				
				for( DomainRule rule : rules ) {
					final DomainPredicate persistentEntity = predicateAdministrationService.findById(rule.getPredicate().getId() );
					//Plugin the persistent predicates using the id.
					rule.setPredicate( persistentEntity );
					ruleAdministrationService.save(rule);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
