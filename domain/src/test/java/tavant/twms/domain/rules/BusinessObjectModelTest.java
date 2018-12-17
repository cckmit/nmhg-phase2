/*
 *   Copyright (c)2007 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.rules;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;

import junit.framework.TestCase;
import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.catalog.Item;
import tavant.twms.domain.claim.OEMPartReplaced;

/**
 * @author radhakrishnan.j
 * 
 */
public class BusinessObjectModelTest extends TestCase {

    public void testBuildPaths_SimpleField_RelativeExpression() {
        BusinessObjectModel fixture = new BusinessObjectModel() {

            @Override
            void initialize() {
                // Don't initialize anything.
            }

        };
        assertTrue(fixture.fieldsInBO.isEmpty());
        DomainType claimType = new DomainType("Claim", "Claim");
        Field failureDate = claimType.simpleField("Failure Date", "failureDate", Type.DATE);
        fixture.discoverPathsToFields(claimType, "claim");

        SortedMap<String, FieldTraversal> claimDataElements = fixture
                .getDataElementsForType(claimType);
        assertNotNull(claimDataElements);
        FieldTraversal pathToFailureDateField = claimDataElements.get("claim.failureDate");
        assertNotNull(pathToFailureDateField);
        assertEquals(2, pathToFailureDateField.getFieldsInPath().size());
        assertEquals(claimType, ((OneToOneAssociation) pathToFailureDateField.getFieldsInPath()
                .get(0)).getOfType());
        assertEquals(failureDate, pathToFailureDateField.getFieldsInPath().get(1));
        assertFalse(pathToFailureDateField.endsInACollection());
        assertEquals(Type.DATE, pathToFailureDateField.getType());
        assertEquals("claim.failureDate", pathToFailureDateField.getExpression());
        assertEquals("Claim's Failure Date", pathToFailureDateField.getDomainName());

        DomainSpecificVariable domainSpecificVariable = pathToFailureDateField
                .getDomainSpecificVariable();
        domainSpecificVariable.setContext(BusinessObjectModelFactory.CLAIM_RULES);
        assertNotNull(domainSpecificVariable);
        assertEquals("claim.failureDate", domainSpecificVariable.getToken());
        assertEquals("Claim's Date Of Failure", domainSpecificVariable.getDomainName());
        assertEquals(Type.DATE, domainSpecificVariable.getType());
    }

    public void testBuildPaths_SimpleField_HardwiredExpression() {
        BusinessObjectModel fixture = new BusinessObjectModel() {

            @Override
            void initialize() {
                // Don't initialize anything.
            }

        };
        assertTrue(fixture.fieldsInBO.isEmpty());
        DomainType claimType = new DomainType("Claim", "Claim");
        String domainName = "Sum of Club Car Parts Replaced Quantity";
        String hardwiredExpression = "sumOfIntegers({contextExpression}."
                + "serviceInformation.serviceDetail.oemPartsReplaced." + "{numberOfUnits})";
        Field sumOfOEMQuantity = claimType.functionField(domainName, hardwiredExpression,
                Type.INTEGER, true, FunctionField.Types.ONE_TO_MANY.getBaseType());
        fixture.discoverPathsToFields(claimType, "claim");

        SortedMap<String, FieldTraversal> claimDataElements = fixture
                .getDataElementsForType(claimType);
        FieldTraversal pathToFailureDateField = claimDataElements
                .get("sumOfIntegers(claim.serviceInformation."
                        + "serviceDetail.oemPartsReplaced.{numberOfUnits})");
        assertNotNull(pathToFailureDateField);
        assertEquals(2, pathToFailureDateField.getFieldsInPath().size());
        assertEquals(claimType, ((OneToOneAssociation) pathToFailureDateField.getFieldsInPath()
                .get(0)).getOfType());
        assertEquals(sumOfOEMQuantity, pathToFailureDateField.getFieldsInPath().get(1));
        assertFalse(pathToFailureDateField.endsInACollection());
        assertEquals(Type.INTEGER, pathToFailureDateField.getType());
        assertEquals("sumOfIntegers(claim.serviceInformation."
                + "serviceDetail.oemPartsReplaced.{numberOfUnits})", pathToFailureDateField
                .getExpression());
        assertEquals("Sum of Club Car Parts Replaced Quantity", pathToFailureDateField
                .getDomainName());

        DomainSpecificVariable domainSpecificVariable = pathToFailureDateField
                .getDomainSpecificVariable();
        domainSpecificVariable.setContext(BusinessObjectModelFactory.CLAIM_RULES);
        assertNotNull(domainSpecificVariable);
        assertEquals("sumOfIntegers(claim.serviceInformation."
                + "serviceDetail.oemPartsReplaced.{numberOfUnits})", domainSpecificVariable
                .getToken());
        assertEquals("Sum of Club Car Parts Replaced Quantity", domainSpecificVariable
                .getDomainName());
        assertEquals(Type.INTEGER, domainSpecificVariable.getType());
    }

    public void testBuildPaths_CollectionValuedExpression() {
        BusinessObjectModel fixture = new BusinessObjectModel() {

            @Override
            void initialize() {
                // Don't initialize anything.
            }

        };
        assertTrue(fixture.fieldsInBO.isEmpty());
        DomainType claimType = new DomainType("Claim", "Claim");
        String domainName = "Club Car Part Replaced";
        DomainType oEMPartReplaced = new DomainType(domainName, OEMPartReplaced.class
                .getSimpleName());
        Field numberOfUnits = oEMPartReplaced.simpleField("Number of Units Replaced",
                "numberOfUnits", Type.STRING);
        claimType.oneToMany("Club Car Parts Replaced", "oEMPartsReplaced", oEMPartReplaced);

        fixture.discoverPathsToFields(claimType, "claim");

        SortedMap<String, FieldTraversal> claimDataElements = fixture
                .getDataElementsForType(claimType);
        FieldTraversal pathToOEMPartsReplaced = claimDataElements.get("claim.oEMPartsReplaced");
        assertNotNull(pathToOEMPartsReplaced);
        assertEquals(2, pathToOEMPartsReplaced.getFieldsInPath().size());
        assertEquals(claimType, ((OneToOneAssociation) pathToOEMPartsReplaced.getFieldsInPath()
                .get(0)).getOfType());
        assertEquals(oEMPartReplaced, ((OneToManyAssociation) pathToOEMPartsReplaced
                .getFieldsInPath().get(1)).getOfType());
        assertTrue(pathToOEMPartsReplaced.endsInACollection());
        assertEquals(OEMPartReplaced.class.getSimpleName(), pathToOEMPartsReplaced.getType());
        assertEquals("claim.oEMPartsReplaced", pathToOEMPartsReplaced.getExpression());
        assertEquals("Claim's Club Car Parts Replaced", pathToOEMPartsReplaced.getDomainName());

        SortedMap<String, FieldTraversal> oemPartsDataElements = fixture
                .getDataElementsForType(oEMPartReplaced);
        assertNotNull(oemPartsDataElements);
        FieldTraversal fieldTraversal = oemPartsDataElements.get("numberOfUnits");
        assertEquals(2, fieldTraversal.getFieldsInPath().size());
        assertEquals(oEMPartReplaced, (((OneToOneAssociation) fieldTraversal.getFieldsInPath().get(
                0)).getOfType()));
        assertEquals(numberOfUnits, fieldTraversal.getFieldsInPath().get(1));
        assertFalse(fieldTraversal.endsInACollection());
    }

    public void testBuildPaths_OneToOneExpression() {
        BusinessObjectModel fixture = new BusinessObjectModel() {

            @Override
            void initialize() {
                // Don't initialize anything.
            }

        };
        assertTrue(fixture.fieldsInBO.isEmpty());
        DomainType claimType = new DomainType("Claim", "Claim");
        String domainName = "Club Car Part Replaced";
        DomainType oEMPartReplaced = new DomainType(domainName, OEMPartReplaced.class.getName());

        DomainType item = new DomainType("Item", Item.class.getName());
        Field itemNumber = item.simpleField("Number", "number", Type.STRING);

        Field numberOfUnits = oEMPartReplaced.simpleField("Number of Units Replaced",
                "numberOfUnits", Type.STRING);
        oEMPartReplaced.oneToOne("Part", "itemReference.referredItem", item);
        claimType.oneToMany("Club Car Parts Replaced", "oEMPartsReplaced", oEMPartReplaced);

        fixture.discoverPathsToFields(claimType, "claim");

        SortedMap<String, FieldTraversal> claimDataElements = fixture
                .getDataElementsForType(claimType);
        FieldTraversal pathToOEMPartsReplaced = claimDataElements.get("claim.oEMPartsReplaced");
        assertNotNull(pathToOEMPartsReplaced);
        assertEquals(2, pathToOEMPartsReplaced.getFieldsInPath().size());
        assertEquals(claimType, ((OneToOneAssociation) pathToOEMPartsReplaced.getFieldsInPath()
                .get(0)).getOfType());
        assertEquals(oEMPartReplaced, ((OneToManyAssociation) pathToOEMPartsReplaced
                .getFieldsInPath().get(1)).getOfType());
        assertTrue(pathToOEMPartsReplaced.endsInACollection());
        assertEquals(OEMPartReplaced.class.getName(), pathToOEMPartsReplaced.getType());
        assertEquals("claim.oEMPartsReplaced", pathToOEMPartsReplaced.getExpression());
        assertEquals("Claim's Club Car Parts Replaced", pathToOEMPartsReplaced.getDomainName());

        SortedMap<String, FieldTraversal> oemPartsDataElements = fixture
                .getDataElementsForType(oEMPartReplaced);
        assertNotNull(oemPartsDataElements);
        FieldTraversal oemPartsFieldTraversal = oemPartsDataElements.get("numberOfUnits");
        assertEquals(2, oemPartsFieldTraversal.getFieldsInPath().size());
        assertEquals(oEMPartReplaced, ((OneToOneAssociation) oemPartsFieldTraversal
                .getFieldsInPath().get(0)).getOfType());
        assertEquals(numberOfUnits, oemPartsFieldTraversal.getFieldsInPath().get(1));
        assertFalse(oemPartsFieldTraversal.endsInACollection());

        /*
         * FieldTraversal fieldTraversal =
         * oemPartsDataElements.get("itemReference.referredItem.number");
         * assertEquals(itemNumber,fieldTraversal.getFieldsInPath().get(2));
         * assertFalse(oemPartsDataElements.get("itemReference.referredItem.number").endsInACollection());
         */
    }

    public void testListAllContexts() {
        BusinessObjectModel businessObjectModel = BusinessObjectModel.getInstance();
        Set<String> contexts = new HashSet<String>();
        contexts.add("ClaimRules");
        contexts.add("ContractApplicabilityRules");
        contexts.add("PolicyRules");
        contexts.add("EntryValidationRules");
        contexts.add("ClaimProcessorRouting");
        contexts.add("recClaimProcessorRouting");
        assertEquals(contexts, businessObjectModel.listAllContexts());
    }

    public void testGetFieldForClaimsDealer() {
        BusinessObjectModel businessObjectModel = BusinessObjectModel.getInstance();
        FieldTraversal dealerField = businessObjectModel.getField("Claim", "claim.forDealer");

        assertNotNull(dealerField);
        assertEquals("Dealership", dealerField.getType());
        assertEquals("claim.forDealer", dealerField.getExpression());
    }

    public void testGetFieldForClaimsDealersName() {
        BusinessObjectModel businessObjectModel = BusinessObjectModel.getInstance();
        FieldTraversal dealerNameField = businessObjectModel.getField("Claim",
                "claim.forDealer.name");

        assertNotNull(dealerNameField);
        assertEquals("string", dealerNameField.getType());
        assertEquals("claim.forDealer.name", dealerNameField.getExpression());
    }
}
