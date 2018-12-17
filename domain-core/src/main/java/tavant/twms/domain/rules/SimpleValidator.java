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


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import tavant.twms.domain.businessobject.BusinessObjectModelFactory;
import tavant.twms.domain.businessobject.IBusinessObjectModel;

/**
 * @author radhakrishnan.j
 * 
 */
public class SimpleValidator extends OGNLExpressionGenerator {
    private ValidationContext validationContext = new ValidationContext();

    private List<String> traversalPath = new ArrayList<String>();

    private String context;

    public ValidationContext getValidationContext() {
        return validationContext;
    }

    public SimpleValidator() {
        super();
    }

    @Override
    public void visit(All visitable) {
        for (Predicate predicate : visitable.getPredicates()) {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(Any visitable) {
        // TODO Auto-generated method stub
        super.visit(visitable);
        for (Predicate predicate : visitable.getPredicates()) {
            predicate.accept(this);
        }
    }

    @Override
    public void visit(DomainPredicate visitable) {
        // TODO Auto-generated method stub
        context = visitable.getContext();
        if (context == null) {
            validationContext.addError(MessageFormat.format(
                    "Domain Predicate id = {0} , name = {1} doesn't have context set at {2} ", visitable
                            .getId(), visitable.getName(), getPathToCurrentNode()));
        }
        super.visit(visitable);
    }

    @Override
    public void visit(ForEachOf visitable) {
        super.visit(visitable);
    }

    @Override
    public void visit(IsSet isSet) {
        if (isSet.getOperand() == null) {
            validationContext.addError(MessageFormat.format("Variable name " +
                    "not specified at {0}",
                    getPathToCurrentNode()));
        }
    }

    @Override
    public void visit(MethodInvocationTarget visitable) {
        super.visit(visitable);
    }

    @Override
    public void visit(And visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(Constant constant) {
        if (constant.getLiteral() == null) {
            validationContext.addError(MessageFormat.format("Value for "
                    + "constant of type ''{0}'' is not specified at {1}", constant.getType(),
                    getPathToCurrentNode()));
        }
    }

    @Override
    public void visit(Constants constants) {
        if (constants.getLiterals().isEmpty()) {
            validationContext.addError(MessageFormat.format(
                    "Values for constants of type ''{0}'' is not specified " + "at {1}", constants.getType(), getPathToCurrentNode()));
        }
    }

    @Override
    public void visit(DomainSpecificVariable domainVariable) {
        //BusinessObjectModel bom = BusinessObjectModel.getInstance();
    	//IBusinessObjectModel bom=domainVariable.getBusinessObjectModel();
    	IBusinessObjectModel busObject=BusinessObjectModelFactory.getInstance().getBusinessObjectModel(domainVariable.getContext());
        String fieldName = domainVariable.getFieldName();
        String typeName = domainVariable.getAccessedFromType();
        if (typeName==null || fieldName == null || busObject.getField(typeName, fieldName) == null) {
            validationContext.addError(MessageFormat.format("Field ({0},{1}) is unknown at {2}",
                    typeName,fieldName,getPathToCurrentNode()));
        }

        // if the type is not configured, it will break.
        domainVariable.getType();
    }

    @Override
    public void visit(Equals visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(GreaterThan visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(GreaterThanOrEquals visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(IsNoneOf visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(IsOneOf visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(LessThan visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(LessThanOrEquals visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(Not visitable) {
        visitUnaryPredicate(visitable);
    }

    @Override
    public void visit(IsNotSet visitable) {
        if (visitable.getOperand() == null) {
            validationContext.addError(MessageFormat.format("Variable name not specified at {0}",
                    getPathToCurrentNode()));
        }
    }

    @Override
    public void visit(NotEquals visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(Or visitable) {
        visitBinaryPredicate(visitable);
    }

    @Override
    public void visit(ForAnyNOf visitable) {
        visitUnaryPredicate(visitable);
    }

    @Override
    public void visit(ForAnyOf visitable) {
        visitUnaryPredicate(visitable);
    }

    @Override
    public void visit(IsTrue isTrue) {
        if (isTrue.getOperand() == null) {
            validationContext.addError(MessageFormat.format("Variable name " +
                    "not specified at {0}",
                    getPathToCurrentNode()));
        }
    }

    @Override
    public void visit(IsFalse isFalse) {
        if (isFalse.getOperand() == null) {
            validationContext.addError(MessageFormat.format("Variable name " +
                    "not specified at {0}",
                    getPathToCurrentNode()));
        }
    }

    @Override
    public void visit(MethodInvocation visitable) {
        Visitable invokeOn = visitable.invokeOn();
        if (invokeOn != null) {
            invokeOn.accept(this);
        } else {
            validationContext.addError(MessageFormat.format("MethodInvocation target missing at {0}",
                    getPathToCurrentNode()));
        }

        if (validationContext.hasErrors()) {
            return;
        }

        Visitable[] arguments = visitable.arguments();
        for (Visitable argument : arguments) {
            if (argument != null) {
                argument.accept(this);
            }
        }
    }

    @Override
    public void visitBinaryPredicate(BinaryPredicate visitable) {
        Visitable lhs = visitable.getLhs();
        Visitable rhs = visitable.getRhs();
        if (lhs == null) {
            validationContext.addError(MessageFormat.format("Left hand side "
                    + "for ''{0}'' is not specified at {1}", visitable.getDomainTerm(),
                    getPathToCurrentNode()));
        } else if (rhs == null) {
            validationContext.addError(MessageFormat.format("Right hand side "
                    + "for ''{0}'' is not specified at {1}", visitable.getDomainTerm(),
                    getPathToCurrentNode()));
        }

        if (validationContext.hasErrors()) {
            return;
        }

        traversalPath.add(MessageFormat.format("lhs({0})", visitable.getLhs().getClass().getSimpleName()));
        lhs.accept(this);
        traversalPath.remove(traversalPath.size() - 1);

        traversalPath.add(MessageFormat.format("rhs({0})", visitable.getRhs().getClass().getSimpleName()));
        rhs.accept(this);
        traversalPath.remove(traversalPath.size() - 1);
    }

    @Override
    public void visitUnaryPredicate(UnaryPredicate visitable) {
        Predicate operand = visitable.getOperand();
        traversalPath.add(visitable.getDomainTerm());
        if (operand != null) {
            operand.accept(this);
        } else {
            validationContext.addError(MessageFormat.format("Method "
                    + "invocation target for ''{0}'' is not specified at {1}", visitable.getDomainTerm(),
                    getPathToCurrentNode()));
        }

        if (validationContext.hasErrors()) {
            return;
        }

        traversalPath.remove(traversalPath.size() - 1);
    }

    protected String getPathToCurrentNode() {
        StringBuffer buf = new StringBuffer();
        buf.append("root");
        for (String pathElement : traversalPath) {
            buf.append("->");
            buf.append(pathElement);
        }
        return buf.toString();
    }

}
