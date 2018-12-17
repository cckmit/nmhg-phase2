package tavant.twms.web.rules;

import java.util.Arrays;

import junit.framework.TestCase;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.claim.Claim;
import tavant.twms.domain.rules.And;
import tavant.twms.domain.rules.Constant;
import tavant.twms.domain.rules.Constants;
import tavant.twms.domain.rules.DomainPredicate;
import tavant.twms.domain.rules.DomainSpecificVariable;
import tavant.twms.domain.rules.Equals;
import tavant.twms.domain.rules.IsNoneOf;
import tavant.twms.domain.rules.Or;
import tavant.twms.domain.rules.Predicate;
import tavant.twms.domain.rules.Type;

public class DetailedDescriptionGeneratorTest extends TestCase {
	private String bom=BusinessObjectModelFactory.CLAIM_RULES;
    public void testDescriptionGeneration() throws Exception {
        String domainVariableExpression = "claim.forItem.type.type";
        DomainSpecificVariable claimInventoryItemType = new DomainSpecificVariable(Claim.class,domainVariableExpression,bom);
        Constant RETAIL = new Constant("RETAIL", Type.STRING);
        Equals checkIfInventoryItemIsRetailed = new Equals(claimInventoryItemType, RETAIL);
        String domainPredicateName = "Inventory Item is Retailed";

        DomainPredicate ifInventoryItemIsRetailed = new DomainPredicate(domainPredicateName,
                checkIfInventoryItemIsRetailed);

        domainVariableExpression = "claim.forItem.conditionType.type";

        DomainSpecificVariable claimInventoryItemCondition = new DomainSpecificVariable(Claim.class,domainVariableExpression,bom);

        String[] itemConditions = new String[] { "NEW", "REFURBISHED" };
        Predicate or = new IsNoneOf(claimInventoryItemCondition, new Constants(Arrays
                .asList(itemConditions), Type.STRING));

        domainPredicateName = "Inventory Item Condition is NEW or REFURBISHED";
        DomainPredicate notNewOrRefurbished = new DomainPredicate(domainPredicateName, or);

        And and = new And(ifInventoryItemIsRetailed, notNewOrRefurbished);

        DomainPredicate newOrRefurbishedAndRetailed = new DomainPredicate(
                "Retailed Item is not New or Refurbished Item", and);

        DetailedDescriptionGenerator fixture = new DetailedDescriptionGenerator(newOrRefurbishedAndRetailed);
        newOrRefurbishedAndRetailed.accept(fixture);
        String expected = "(   Inventory Item Type  is \"RETAIL\"  and   Inventory Item condition  is not one of {\"NEW\", \"REFURBISHED\"}  )";
        assertEquals(expected.replaceAll("\\s",""), fixture.getDetailedDescription().replaceAll("\\s", ""));
        
        fixture = new DetailedDescriptionGenerator(ifInventoryItemIsRetailed);
        ifInventoryItemIsRetailed.accept(fixture);
        assertEquals("  Inventory Item Type  is \"RETAIL\" ".replaceAll("\\s",""),fixture.getDetailedDescription().replaceAll("\\s",""));
        
        
        fixture = new DetailedDescriptionGenerator(notNewOrRefurbished);
        notNewOrRefurbished.accept(fixture);
        assertEquals("  Inventory Item condition  is not one of {\"NEW\", \"REFURBISHED\"} ".replaceAll("\\s",""),fixture.getDetailedDescription().replaceAll("\\s",""));
        
        DomainPredicate newDomainPredicate = new DomainPredicate("Junk",new Or(ifInventoryItemIsRetailed,notNewOrRefurbished));
        fixture = new DetailedDescriptionGenerator(newDomainPredicate);
        newDomainPredicate.accept(fixture);
        assertEquals("(   Inventory Item Type  is \"RETAIL\"  or   Inventory Item condition  is not one of {\"NEW\", \"REFURBISHED\"}  )".replaceAll("\\s",""),fixture.getDetailedDescription().replaceAll("\\s",""));
    }
}
