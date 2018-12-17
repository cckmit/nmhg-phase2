/**
 * 
 */
package tavant.twms.deployment.tasks;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.FileCopyUtils;
import tavant.twms.domain.rules.DomainRule;
import tavant.twms.domain.rules.RuleAdministrationService;
import tavant.twms.domain.rules.RuleSerializer;

import java.io.*;
import java.util.List;

/**
 * @author radhakrishnan.j
 *
 */
public class ExportRulesFromDB extends DefaultTask {
	private RuleSerializer xmlSerializer;
	private RuleAdministrationService ruleAdministrationService;
    private Logger logger = Logger.getLogger(ExportRulesFromDB.class);
    private static final String RULE_TABLE_NAME = "domain_rule";
    public static final String RULES_EXPORT_SKIPPED_PROPERTY =
            "tavant.twms.process.exportRulesTaskWasSkipped";

    @Required
	public void setRuleAdministrationService(
			RuleAdministrationService ruleAdministrationService) {
		this.ruleAdministrationService = ruleAdministrationService;
	}

	
	@Required
	public void setXmlSerializer(RuleSerializer xmlRuleSerializer) {
		this.xmlSerializer = xmlRuleSerializer;
	}



	public void perform() {
		try {

            if(!doesTableExist(RULE_TABLE_NAME)) {
                // First time that this task is being run. Hence, avoid this
                // step to prevent the export from breaking.
                if(logger.isInfoEnabled()) {
                    logger.info("Rule table [" + RULE_TABLE_NAME +
                    "] not found. Skipping export of canned rules from DB.");
                }

                System.setProperty(RULES_EXPORT_SKIPPED_PROPERTY, "true");

                return;
            }

            File file = new File("Rules.xml");
			if( file.exists() ) {
				file.delete();
				file.createNewFile();
			}
			Writer writer = new FileWriter(file);
			
			List<DomainRule> rules = ruleAdministrationService.findAll();
			
			String xml = xmlSerializer.toXML(rules);
			StringReader stringReader = new StringReader(xml);
			FileCopyUtils.copy(stringReader,writer);
		} catch (Exception e) {
            // ExportRules task can fail for valid reasons. For eg., if the 
            // domain model has changed (say, a new field was added), this
            // change won't be reflected in the db *until* the create.database
            // task is run. 'coz of this the queries executed as part of this
            // task would fail in such a scenario. We shouldn't short-circuit
            // the entire tasks execution in this case.
            logger.error("Exception while exporting rules from DB : " +
				e.getMessage());
		}
	}

}
