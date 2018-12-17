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

import tavant.twms.security.SecurityHelper;

import java.util.Locale;

public class UserLocale {

    private static Locale defaultLocale;

    public UserLocale(String language, String country) {
        UserLocale.defaultLocale = new Locale(language, country);
    }

    public static boolean isDefaultLocale() {
        return UserLocale.defaultLocale.equals(getUserLocale());
    }

    public static Locale getUserLocale() {
        return new SecurityHelper().getLoggedInUser().getLocale();
    }
}
