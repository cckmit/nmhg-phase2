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

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.util.Assert;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * @author radhakrishnan.j
 * 
 */
@Configurable
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@XStreamAlias("isAWatchedPart")
public class IsAWatchedPart implements MethodInvocation, Predicate, Actionable {
	private DomainSpecificVariable domainVariable;

	private MethodInvocationTarget itemGroupService;

	private String methodName = "isPartInWatchList";

	public IsAWatchedPart() {
		super();
	}

	public IsAWatchedPart(DomainSpecificVariable domainVariable) {
		super();
		Assert.notNull(domainVariable);
		this.domainVariable = domainVariable;
		itemGroupService = new MethodInvocationTarget("itemGroupService");
	}

	public void accept(Visitor visitor) {
		visitor.visit(this);
	}

	public Visitable[] arguments() {
		return new Visitable[] { domainVariable };
	}

	public Visitable invokeOn() {
		return itemGroupService;
	}

	public String methodName() {
		return methodName;
	}

	public String returnType() {
		return Type.BOOLEAN;
	}

	public String getDomainTerm() {
		return "is in parts watch list";
	}

	public DomainSpecificVariable getDomainVariable() {
		return domainVariable;
	}

	public void setDomainVariable(DomainSpecificVariable domainVariable) {
		this.domainVariable = domainVariable;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public MethodInvocationTarget getItemGroupService() {
		return itemGroupService;
	}

	public void setItemGroupService(MethodInvocationTarget itemGroupService) {
		this.itemGroupService = itemGroupService;
	}

	public void setArguments(Visitable[] arguments) {
		domainVariable = (DomainSpecificVariable) arguments[0];
	}

	public void setInvokeOn(Visitable visitable) {
	}

	public String getType() {
		return returnType();
	}

	public boolean isCollection() {
		return false;
	}

	public Predicate getInverse() {
		throw new UnsupportedOperationException(
				"Method getInverse() is not supported for "
						+ this.getClass().getName());
	}

	public PredicateEvaluationAction getAction() {
        return PredicateEvaluationActionFactory.getInstance().getActionForIsAWatchedPart(getDomainVariable());
	}

}
