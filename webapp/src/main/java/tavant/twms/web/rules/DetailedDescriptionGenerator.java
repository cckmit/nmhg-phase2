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
package tavant.twms.web.rules;

import tavant.twms.domain.rules.*;
import tavant.twms.web.i18n.I18nActionSupport;

import java.text.MessageFormat;
import java.util.Iterator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author radhakrishnan.j
 *
 */
public class DetailedDescriptionGenerator extends EmptyVisitor {
	private static Logger logger = LogManager.getLogger(DetailedDescriptionGenerator.class);
    private StringBuffer buf = new StringBuffer();
    private DomainPredicate thePredicate;
    
    private I18nActionSupport i18nAction = new I18nActionSupport();
    
    public DetailedDescriptionGenerator(DomainPredicate thePredicate) {
        super();
        this.thePredicate = thePredicate;
    }

    @Override
    public void visit(And visitable) {
        boolean notFirstPredicate = buf.length()!=0;
        if (notFirstPredicate) {
            buf.append(" ( ");
        }        
        visitable.getLhs().accept(this);
        buf.append(" and ");
        visitable.getRhs().accept(this);
        if (notFirstPredicate) {
            buf.append(" )");
        }        
    }

    @Override
    public void visit(DomainPredicate visitable) {
        Predicate delegate = visitable.getPredicate();
        if(delegate!= null){
        	delegate.accept(this);
        }
    }

    @Override
    public void visit(Or visitable) {
        boolean notFirstPredicate = buf.length()!=0;
        if (notFirstPredicate) {
            buf.append(" ( ");
        }        
        visitable.getLhs().accept(this);
        buf.append(" or ");
        visitable.getRhs().accept(this);
        if (notFirstPredicate) {
            buf.append(" )");
        }        
    }
    

    @Override
    public void visit(All visitable) {
        buf.append(" where all of these are true { ");
        for( Iterator<? extends Visitable> iter = visitable.getPredicates().iterator(); iter.hasNext();) {
            iter.next().accept(this);
            if( iter.hasNext() ) {
                buf.append(", ");
            }
        }
        buf.append(" }");
    }

    @Override
    public void visit(Any visitable) {
        buf.append(" where any of these is true { ");
        for( Iterator<? extends Visitable> iter = visitable.getPredicates().iterator(); iter.hasNext();) {
            iter.next().accept(this);
            if( iter.hasNext() ) {
                buf.append(", ");
            }
        }
        buf.append(" }");
    }

    @Override
    public void visit(Constant constant) {
        buf.append("\"");
        buf.append(constant.getLiteral());
        buf.append("\"");
    }

    @Override
    public void visit(Constants constants) {
        buf.append(" {");
        for( Iterator<String> iter = constants.getLiterals().iterator();iter.hasNext(); ) {
            buf.append("\"");
            buf.append(iter.next());
            buf.append("\"");
            if( iter.hasNext() ) {
                buf.append(", ");
            }
        }
        buf.append("}");
    }

    @Override
    public void visit(DomainSpecificVariable visitable) {
        buf.append(' ');
        buf.append( visitable.getDomainName());
    }

    @Override
    public void visit(Equals visitable) {
        buf.append(' ');
        visitable.getLhs().accept(this);
        buf.append(" is ");
        visitable.getRhs().accept(this);
    }

    @Override
    public void visit(ForAnyNOf visitable) {
        buf.append(' ');
        visitable.getConditionToBeSatisfied().accept(this);
        buf.append(MessageFormat.format(" for atleast ''{0}'' ",visitable.get_n()));
        visitable.getCollectionValuedVariable().accept(this);
    }

    @Override
    public void visit(ForAnyOf visitable) {
        buf.append(' ');
        visitable.getConditionToBeSatisfied().accept(this);
        buf.append(" for atleast one ");
        visitable.getCollectionValuedVariable().accept(this);
    }

    @Override
    public void visit(ForEachOf visitable) {
        buf.append(' ');
        visitable.getConditionToBeSatisfied().accept(this);
        buf.append(" for each ");
        visitable.getCollectionValuedVariable().accept(this);
    }

    @Override
    public void visit(GreaterThan visitable) {
        buf.append(' ');
        visitable.getLhs().accept(this);
        buf.append(" is greater than ");
        visitable.getRhs().accept(this);
    }

    @Override
    public void visit(GreaterThanOrEquals visitable) {
        buf.append(' ');
        visitable.getLhs().accept(this);
        buf.append(" is greater than or equal to ");
        visitable.getRhs().accept(this);
    }

    @Override
    public void visit(IsNoneOf visitable) {
        buf.append(' ');
        visitable.getLhs().accept(this);
        buf.append(" is not one of ");
        visitable.getRhs().accept(this);
    }

    @Override
    public void visit(IsOneOf visitable) {
        buf.append(' ');
        visitable.getLhs().accept(this);
        buf.append(" is one of ");
        visitable.getRhs().accept(this);
    }


    @Override
    public void visit(LessThan visitable) {
        buf.append(' ');
        visitable.getLhs().accept(this);
        buf.append(" is less than  ");
        visitable.getRhs().accept(this);
    }

    @Override
    public void visit(LessThanOrEquals visitable) {
        buf.append(' ');
        visitable.getLhs().accept(this);
        buf.append(" is less than or equal to ");
        visitable.getRhs().accept(this);
    }

    
    
    @Override
    public void visit(MethodInvocation visitable) {
        // TODO Auto-generated method stub
        super.visit(visitable);
    }

    @Override
    public void visit(Not visitable) {
        buf.append(" the following is not true ");
        visitable.getOperand().accept(this);
    }

    @Override
    public void visit(IsNotSet visitable) {
        buf.append(' ');
        visitable.getOperand().accept(this);
        buf.append(" is not set ");
    }

    @Override
    public void visit(IsSet isSet) {
        buf.append(' ');
        isSet.getOperand().accept(this);
        buf.append(" is set ");
    }
    
    
    @Override
    public void visit(NotEquals visitable) {
        buf.append(' ');
        visitable.getLhs().accept(this);
        buf.append(" is not  ");
        visitable.getRhs().accept(this);
    }

    public String getDetailedDescription() {
        if( buf.toString().length()==0 ) {
            return thePredicate.getName();
        } else {
            return buf.toString();            
        }
    }
    
    private String getMessagesForKeyLabels()
    {
    	String[] tokens = buf.toString().split(" ");
    	if(tokens != null && tokens.length > 0)
    	{
    		buf = new StringBuffer("");
    		for(String token : tokens)
    		{
    			if(token.contains("."))
    			{	    			
    				String lookUpKey = token;
    				lookUpKey = lookUpKey.replaceAll("'s", "");
    				lookUpKey = lookUpKey.replaceAll(",", "");
    				String lookUpValue = i18nAction.getText(lookUpKey);
    				token = token.replaceAll(lookUpKey, lookUpValue);
	    			buf.append( token + " ");	    			
    			}
    			else
    			{
    				buf.append(token + " ");
    			}
    		}
    	}
    	return buf.toString();
    }
    
}
