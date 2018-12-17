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

/**
 * @author radhakrishnan.j
 *
 */
public class ValidationMessage {
    private String message;
    private Type type;

    public ValidationMessage(String message, Type type) {
        super();
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public Type getType() {
        return type;
    }
    
    public static enum Type {
        Error,Warning
    }
    
    public boolean isError() {
        return Type.Error.equals(type);
    }
    
    public static ValidationMessage error(String message) {
        return new ValidationMessage(message,Type.Error);
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(type);buf.append(" : ");
        buf.append(message);
        return buf.toString();
    }
}
