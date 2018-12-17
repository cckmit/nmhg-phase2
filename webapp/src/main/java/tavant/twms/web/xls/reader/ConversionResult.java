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
import java.util.List;

/**
 * @author vineeth.varghese
 * @date May 30, 2007
 */

// TODO : Need a better name for this class.
public class ConversionResult {

    private Object result;
    private List<String> errors = new ArrayList<String>();

    public ConversionResult(Object result, List<String> errors) {
        super();
        this.result = result;
        this.errors = errors;
    }

    public ConversionResult(Object resultingObject) {
        super();
        this.result = resultingObject;
    }

    public List<String> getErrors() {
        return errors;
    }

    public Object getResult() {
        return result;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean hasConversionErrors() {
        return this.errors.size() > 0;
    }

    public void addErrors(List<String> errors) {
        if (this.errors == null) {
            errors = new ArrayList<String>();
        }
        this.errors.addAll(errors);
    }

    public void addError(String error) {
        if (this.errors == null) {
            errors = new ArrayList<String>();
        }
        this.errors.add(error);
    }
}
