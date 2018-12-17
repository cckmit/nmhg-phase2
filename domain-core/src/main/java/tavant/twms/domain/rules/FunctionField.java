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

/**
 * User: <a href="mailto:vikas.sasidharan@tavant.com>Vikas Sasidharan</a>
 * Date: Apr 24, 2007
 * Time: 10:21:46 PM
 */

package tavant.twms.domain.rules;

public class FunctionField extends SimpleField {

    public static enum Types {
        SIMPLE(SimpleField.class),
        ONE_TO_ONE(OneToOneAssociation.class),
        ONE_TO_MANY(OneToManyAssociation.class);

        private Class baseType;

        private Types(Class baseType) {
            this.baseType = baseType;
        }

        public Class getBaseType() {
            return baseType;
        }
    }

    private Class baseType;

    public FunctionField(String domainName, String expression,
                         String domainType, boolean isHardWired,
                         Class baseType) {
        super(domainName, expression, domainType, isHardWired);
        this.baseType = baseType;
    }

    public Class getBaseType() {
        return baseType;
    }
}
