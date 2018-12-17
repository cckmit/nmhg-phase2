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

package tavant.twms.web.i18n;

import java.util.*;

public class PropertyResourceBundle extends ResourceBundle {

    private final Map<String, String> lookup;

    @SuppressWarnings("unchecked")
    public PropertyResourceBundle(Properties messages) {
        lookup = new HashMap(messages);
    }

    public Object handleGetObject(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return lookup.get(key);
    }

    public Enumeration<String> getKeys() {
        return new Enumeration<String>() {
            Iterator<String> iterator = lookup.keySet().iterator();

            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            public String nextElement() {
                return iterator.next();
            }
        };
    }
}
