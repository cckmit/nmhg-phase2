/*
 *   Copyright (c)2006 Tavant Technologies
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
package tavant.twms.infra;

import java.util.Map;

import junit.framework.TestCase;

public class ListCriteriaTest extends TestCase {

    private ListCriteria fixture;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        fixture = new ListCriteria();
    }

    public void testAddSortCriteria() {
        boolean inAscOrder = true;
        fixture.addSortCriteria("someKey", inAscOrder);
        
        assertTrue(fixture.getSortCriteria().containsKey("someKey"));
        assertEquals("asc", fixture.getSortCriteria().get("someKey"));
        

        fixture.addSortCriteria("someKey", !inAscOrder);
        assertTrue(fixture.getSortCriteria().containsKey("someKey"));
        assertEquals("desc", fixture.getSortCriteria().get("someKey"));
    }

    public void testRemoveSortCriteria() {
        boolean inAscOrder = true;
        fixture.addSortCriteria("someKey", inAscOrder);
        fixture.removeSortCriteria();
        assertTrue(fixture.getSortCriteria().isEmpty());
    }

    public void testAddFilterCriteria() {
        fixture.addFilterCriteria("someKey", "someValue");
        
        assertTrue(fixture.getFilterCriteria().containsKey("someKey"));
        assertEquals("someValue",fixture.getFilterCriteria().get("someKey"));
    }

    public void testRemoveFilterCriteria() {
        fixture.addFilterCriteria("someKey", "someValue");
        fixture.removeFilterCriteria();
        assertTrue(fixture.getFilterCriteria().isEmpty());
    }
    
    @SuppressWarnings("deprecation")
    public void testGetFilterCriteriaString_NoNulls() {
        fixture.addFilterCriteria("key1", "value1");
        fixture.addFilterCriteria("key2", "value2");        
        String filterCriteriaString = fixture.getFilterCriteriaString();
        assertEquals("key1 like 'value1%' and key2 like 'value2%'".trim(),filterCriteriaString.trim());
    }
    
    @SuppressWarnings("deprecation")
    public void testGetFilterCriteriaString_NullCondition() {
        fixture.addFilterCriteria("key1",null);
        fixture.addFilterCriteria("key2", "value2");        
        String filterCriteriaString = fixture.getFilterCriteriaString();
        assertEquals("key1 is null  and key2 like 'value2%'".trim(),filterCriteriaString.trim());
    }

    public void testAddFilterCriteria_NonNullConditionHavingOneDefaultEscapableCharacter() {
        fixture.addFilterCriteria("key1","valueWithAn_");
        assertEquals("valueWithAn\\_",
                fixture.getFilterCriteria().get("key1"));
    }

    public void testAddFilterCriteria_NonNullConditionHavingMultipleDefaultEscapableCharacter() {
        fixture.addFilterCriteria("key1","valueWithAn_AndAnother_Also");
        assertEquals("valueWithAn\\_AndAnother\\_Also",
                fixture.getFilterCriteria().get("key1"));
    }

    public void testAddFilterCriteria_NonNullConditionHavingOneNonDefaultEscapableCharacter() {
        fixture.addCharacterToBeEscaped('#');
        fixture.addFilterCriteria("key1","valueWithA#");
        assertEquals("valueWithA\\#",
                fixture.getFilterCriteria().get("key1"));
    }

    public void testAddFilterCriteria_NonNullConditionHavingOneDefaultAndNonDefaultEscapableCharacterEach() {
        fixture.addCharacterToBeEscaped('#');
        fixture.addFilterCriteria("key1","valueWithA#AndAn_Also");
        assertEquals("valueWithA\\#AndAn\\_Also",
                fixture.getFilterCriteria().get("key1"));
    }
    
    public void disable_testGetParametrizedFilterCriteriaString_NoNulls() {
        fixture.addFilterCriteria("key1", "value1");
        fixture.addFilterCriteria("key2", "value2");        
        String filterCriteriaString = fixture.getParamterizedFilterCriteria();
        Map<String, Object> parameterMap = fixture.getParameterMap();
        assertEquals("key1 like :key1 and key2 like :key2".trim(),filterCriteriaString.trim());
        assertTrue( parameterMap.get("key1").equals("value1%") );
        assertTrue( parameterMap.get("key2").equals("value2%") );        
    }    
    
    public void disable_testGetParametrizedFilterCriteriaString_Nulls() {
        fixture.addFilterCriteria("key1",null);
        fixture.addFilterCriteria("key2", "value2");        
        String filterCriteriaString = fixture.getParamterizedFilterCriteria();
        Map<String, Object> parameterMap = fixture.getParameterMap();
        assertEquals("key1 is null  and key2 like :key2",filterCriteriaString);
        assertFalse( parameterMap.containsKey("key1") );
        assertTrue( parameterMap.get("key2").equals("value2%") );        
    }    
    
    public void testGetSortCriteriaString() {
        fixture.addSortCriteria("key1", true);
        fixture.addSortCriteria("key2", false);        
        String sortCriteriaString = fixture.getSortCriteriaString();
        assertEquals("key1 asc, key2 desc",sortCriteriaString);
    }

    public void testStripDOTCharactersFromForStringHavingNoDots() {
        String testString = "abc123@#$";
        assertEquals(testString,
                fixture.stripDOTCharactersFrom(testString));
    }

    public void testStripDOTCharactersFromForStringHavingOneDot() {
        String testString = "abc123.@#$";
        assertEquals("abc123@#$", 
                fixture.stripDOTCharactersFrom(testString));
    }

    public void testStripDOTCharactersFromForStringHavingMultipleDots() {
        String testString = ".abc.123.@#$.";
        assertEquals("abc123@#$",
                fixture.stripDOTCharactersFrom(testString));
    }
}