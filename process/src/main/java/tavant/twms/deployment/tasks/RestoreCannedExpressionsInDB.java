/**
 *
 */
package tavant.twms.deployment.tasks;

import static tavant.twms.deployment.tasks.ExportCannedExpressionsFromDB.PREDICATES_EXPORT_SKIPPED_PROPERTY;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.FileCopyUtils;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.rules.BusinessObjectModel;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.domain.rules.RuleSerializer;
import tavant.twms.infra.PageSpecification;

/**
 * @author radhakrishnan.j
 */
public class RestoreCannedExpressionsInDB extends DefaultTask {
    private PredicateAdministrationService predicateAdministrationService;
    private RuleSerializer xmlSerializer;
    private static final Logger logger =
            Logger.getLogger(RestoreCannedExpressionsInDB.class);

    @Required
    public void setPredicateAdministrationService(
            PredicateAdministrationService predicateAdministrationService) {
        this.predicateAdministrationService = predicateAdministrationService;
    }


    @Required
    public void setXmlSerializer(RuleSerializer xmlRuleSerializer) {
        this.xmlSerializer = xmlRuleSerializer;
    }


    @SuppressWarnings("unchecked")
    public void perform() {

        String predicatesExportWasSkipped =
                System.getProperty(PREDICATES_EXPORT_SKIPPED_PROPERTY);
        System.setProperty(PREDICATES_EXPORT_SKIPPED_PROPERTY, ""); // Clean Up.

        if (Boolean.valueOf(predicatesExportWasSkipped)) {
            if (logger.isInfoEnabled()) {
                logger.info("Predicates were not exported earlier and hence " +
                        "skipping load also.");
            }

            return;
        }

        File file = null;

        try {
            BusinessObjectModelFactory bom = BusinessObjectModelFactory.getInstance();
			SortedSet<String> ruleContexts = new TreeSet<String>();
			ruleContexts.addAll(bom.listAllRuleContexts());
            PageSpecification pageSpecification = new PageSpecification();
            pageSpecification.setPageSize(50);
            for (String ruleContext : ruleContexts) {
                file = new File(ruleContext + "-Predicates.xml");
                if (!file.exists()) {
                    throw new RuntimeException(" File not found [" + file.getAbsolutePath() + "]");
                }
                Reader reader = new FileReader(file);
                StringWriter stringWriter = new StringWriter();
                FileCopyUtils.copy(reader, stringWriter);

                List<DomainPredicate> predicates = (List<DomainPredicate>) xmlSerializer.fromXML(stringWriter.getBuffer().toString());
                for (DomainPredicate domainPredicate : predicates) {
                    predicateAdministrationService.save(domainPredicate);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

}
