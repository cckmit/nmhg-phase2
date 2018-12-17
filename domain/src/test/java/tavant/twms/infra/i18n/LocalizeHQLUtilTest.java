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

package tavant.twms.infra.i18n;

import tavant.twms.infra.DomainRepositoryTestCase;

import java.util.regex.Matcher;

public class LocalizeHQLUtilTest extends DomainRepositoryTestCase {

    private Matcher matcher;

    protected void setUpInTxnRollbackOnFailure() throws Exception {
        super.setUpInTxnRollbackOnFailure();
        matcher = LocalizeHQLUtil.createMatcher(
                "from Item item where #item.description# like :descriptionParam");
    }

    public void testCreateMatcher() {
        try {
            LocalizeHQLUtil.createMatcher(
                    "from Item item where item.description like :descriptionParam");
            fail("failed to throw an IllegalArgumentException");
        } catch (IllegalArgumentException ignore) {}
    }

    public void testGetFieldName() {
        assertEquals("item.description", LocalizeHQLUtil.getFieldName(matcher));
    }

    public void testGetDefaultReplaceString() {
        assertEquals(
                "from Item item where item.description.defaultText like :descriptionParam",
                LocalizeHQLUtil.getDefaultReplaceString(matcher, "item.description"));
    }

    public void testgetI18nReplaceString() {
        assertEquals(
                "from Item item " +
                        "where item.description.i18nTexts['en_US'].fieldName = :fieldNameParam and " +
                        "item.description.i18nTexts['en_US'].text like :descriptionParam",
                LocalizeHQLUtil.getI18nReplaceString(matcher, "item.description", ""));
    }
}
