/*
 *   Copyright (c) 2007 Tavant Technologies
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

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author mritunjay.kumar
 */
@Configurable
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@XStreamAlias("isAWatchedDealership")
public class IsAWatchedDealership implements MethodInvocation, Predicate,
		Actionable {
	private DomainSpecificVariable domainVariable;

	private MethodInvocationTarget dealerGroupService;

	private String methodName = "isDealerInWatchList";

	public IsAWatchedDealership() {
		super();
	}

	public IsAWatchedDealership(DomainSpecificVariable domainVariable) {
		super();
		Assert.notNull(domainVariable);
		this.domainVariable = domainVariable;
		dealerGroupService = new MethodInvocationTarget("dealerGroupService");
	}

	public String getType() {
		return returnType();
	}

	public boolean isCollection() {
		return false;
	}

	public String getDomainTerm() {
		return "label.operators.isInDealerWatchList";
	}

	public Predicate getInverse() {
		throw new UnsupportedOperationException(
				"Method getInverse() is not supported for "
						+ this.getClass().getName());
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Visitable[] arguments() {
		return new Visitable[] { domainVariable };
	}

	public Visitable invokeOn() {
		return dealerGroupService;
	}

	public String methodName() {
		return methodName;
	}

	public String returnType() {
		return Type.BOOLEAN;
	}

	public DomainSpecificVariable getDomainVariable() {
		return domainVariable;
	}

	public void setDomainVariable(DomainSpecificVariable domainVariable) {
		this.domainVariable = domainVariable;
	}

	public MethodInvocationTarget getDealerGroupService() {
		return dealerGroupService;
	}

	public void setDealerGroupService(MethodInvocationTarget dealerGroupService) {
		this.dealerGroupService = dealerGroupService;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public PredicateEvaluationAction getAction() {
        return PredicateEvaluationActionFactory.getInstance().getActionForIsAWatchedDealership(getDomainVariable());
    }

}
