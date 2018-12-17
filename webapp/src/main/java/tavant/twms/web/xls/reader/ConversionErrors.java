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
package tavant.twms.web.xls.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author vineeth.varghese
 * @date Jun 6, 2007
 */
public class ConversionErrors {

    static ConversionErrors instance = new ConversionErrors();

    ThreadLocal<List<String>> errors = new ThreadLocal<List<String>>() {
        @Override
        protected List<String> initialValue() {
            return new ArrayList<String>();
        }
    };

    private ThreadLocal<Map<String, Object>> state = new ThreadLocal<Map<String, Object>>() {
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<String, Object>();
        }
    };

    public static final String CLAIM = "claim";

    public void put(String key, Object objet) {
        state.get().put(key, objet);
    }

    public Object get(String key) {
        return state.get().get(key);
    }

    public static ConversionErrors getInstance() {
        return instance;
    }

    void reset() {
        errors.get().clear();
    }

    public void addError(String string) {
        errors.get().add(string);
    }

    public boolean hasErrors() {
        return !errors.get().isEmpty();
    }

    List<String> getErrors() {
        return errors.get();
    }
}
