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

import org.hibernate.Query;
import org.hibernate.Session;
import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalizeHQLUtil {

    private static final Logger logger = Logger.getLogger(LocalizeHQLUtil.class);

    private static Pattern pattern = Pattern.compile("#.+?#");

    public static Query createLocalizedQuery(Session session, String hql) {

        Matcher matcher = createMatcher(hql);
        String fieldName = getFieldName(matcher);

        if (UserLocale.isDefaultLocale()) {
            return session.createQuery(getDefaultReplaceString(matcher, fieldName));
        } else {
            return session.createQuery(getI18nReplaceString(matcher, fieldName, hql))
                    .setString("fieldNameParam", fieldName);
        }
    }

    static Matcher createMatcher(String hql) {
        Matcher matcher = pattern.matcher(hql);
        if (!matcher.find()) {
            throw new IllegalArgumentException(
                    "HQL to be localized does not match the pattern "
                            + pattern.toString() + " : " + hql);
        }
        return matcher;
    }

    static String getFieldName(Matcher matcher) {
        String match = matcher.group();
        return match.substring(1, match.length() - 1);
    }

    static String getDefaultReplaceString(Matcher matcher, String fieldName) {
        return matcher.replaceFirst(fieldName + ".defaultText");
    }

    static String getI18nReplaceString(Matcher matcher, String fieldName, String hql) {
        String replaceString = new StringBuilder()
                .append(fieldName)
                .append(".i18nTexts['")
                .append(UserLocale.getUserLocale())
                .append("'].fieldName = :fieldNameParam and ")
                .append(fieldName)
                .append(".i18nTexts['")
                .append(UserLocale.getUserLocale())
                .append("'].text")
                .toString();
        if (logger.isDebugEnabled()) {
            logger.debug("Replacing #" + fieldName + "#in " + hql + " with " + replaceString);
        }
        return matcher.replaceFirst(replaceString);
    }

}
