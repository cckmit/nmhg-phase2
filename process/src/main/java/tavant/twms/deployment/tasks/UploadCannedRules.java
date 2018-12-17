/**
 * 
 */
package tavant.twms.deployment.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.PredicateAdministrationException;
import tavant.twms.domain.rules.PredicateAdministrationService;
import tavant.twms.domain.rules.RuleSerializer;

/**
 * @author radhakrishnan.j
 *
 */
public class UploadCannedRules extends DefaultTask {
	private PredicateAdministrationService predicateAdministrationService;
	private RuleSerializer xmlSerializer;
	private String ruleFileNames;
	private String[] fileNames;
	
	@Required
	public void setPredicateAdministrationService(
			PredicateAdministrationService predicateAdministrationService) {
		this.predicateAdministrationService = predicateAdministrationService;
	}

	@Required
	public void setXmlSerializer(RuleSerializer xmlSerializer) {
		this.xmlSerializer = xmlSerializer;
	}

	public RuleSerializer getXmlSerializer() {
		return xmlSerializer;
	}

	public void setRuleFileNames(
			String commaDelimitedListOfRuleFileNames) {
		this.ruleFileNames = commaDelimitedListOfRuleFileNames;
	}

	
	
	@Override
	public void throwErrorOnInvalidInputs() {
		if( ruleFileNames==null ) {
			throw new IllegalArgumentException(MessageFormat.format("action input ''{0}'' is not set.","ruleFileNames"));
		}
		fileNames = StringUtils.commaDelimitedListToStringArray(ruleFileNames);		
	}

	@SuppressWarnings("unchecked")
	public void perform() {
		try {
			for( String fileName : fileNames ) {
				ClassPathResource resource = new ClassPathResource(fileName);
				InputStream is = resource.getInputStream();
				Reader reader = new BufferedReader(new InputStreamReader(is)); 
				StringWriter stringWriter = new StringWriter();
				FileCopyUtils.copy(reader,stringWriter);
				Set<DomainPredicate> ruleFragments = (Set<DomainPredicate>)xmlSerializer.fromXML(stringWriter.getBuffer().toString());
				
				for(DomainPredicate ruleFragment : ruleFragments ) {
					predicateAdministrationService.save(ruleFragment);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (PredicateAdministrationException e) {
			throw new RuntimeException(e);
		}
	}

}
